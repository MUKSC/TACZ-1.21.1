package com.tacz.guns.client.resource.manager;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.audio.OggAudioStream;
import com.tacz.guns.GunMod;
import com.tacz.guns.config.client.SoundConfig;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.sound.sampled.AudioFormat;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SoundAssetsManager extends SimplePreparableReloadListener<Map<ResourceLocation, ResourceLocation>> {
    public record SoundData(ByteBuffer byteBuffer, AudioFormat audioFormat) {
    }
    private static final Marker MARKER = MarkerManager.getMarker("SoundsLoader");
    private static final ExecutorService PRELOAD_EXECUTOR = Executors.newSingleThreadExecutor(r -> {
        Thread thread = new Thread(r, "tacz-sound-preload");
        thread.setDaemon(true);
        return thread;
    });

    private final Map<ResourceLocation, ResourceLocation> soundPathMap = Maps.newHashMap();
    private final Map<ResourceLocation, SoundData> loadedDataMap = Maps.newHashMap();
    private final Map<ResourceLocation, CompletableFuture<SoundData>> loadingTasks = new HashMap<>();

    private final FileToIdConverter filetoidconverter = new FileToIdConverter("tacz_sounds", ".ogg");
    private @Nullable ResourceManager resourceManager;
    private long generation;

    @Override
    @NotNull
    protected Map<ResourceLocation, ResourceLocation> prepare(@NotNull ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        Map<ResourceLocation, ResourceLocation> output = Maps.newHashMap();
        for(Map.Entry<ResourceLocation, Resource> entry : filetoidconverter.listMatchingResources(pResourceManager).entrySet()) {
            ResourceLocation resourcelocation = entry.getKey();
            ResourceLocation resourcelocation1 = filetoidconverter.fileToId(resourcelocation);
            output.put(resourcelocation1, resourcelocation);
        }
        return output;
    }

    @Override
    protected synchronized void apply(Map<ResourceLocation, ResourceLocation> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        long startTime = System.nanoTime();
        generation++;
        resourceManager = pResourceManager;
        soundPathMap.clear();
        soundPathMap.putAll(pObject);
        loadedDataMap.clear();
        loadingTasks.clear();
        int eagerLoadedCount = 0;
        if (!SoundConfig.ENABLE_LAZY_SOUND_LOAD.get()) {
            for (Map.Entry<ResourceLocation, ResourceLocation> entry : soundPathMap.entrySet()) {
                SoundData loaded = loadSound(pResourceManager, entry.getKey(), entry.getValue(), "reload-eager");
                if (loaded != null) {
                    loadedDataMap.put(entry.getKey(), loaded);
                    eagerLoadedCount++;
                }
            }
        }
        if (GunMod.LOGGER.isDebugEnabled()) {
            GunMod.LOGGER.debug(MARKER, "Sound registry applied: knownEntries={}, eagerLoaded={}, lazyLoadEnabled={}, costMs={}",
                    soundPathMap.size(), eagerLoadedCount, SoundConfig.ENABLE_LAZY_SOUND_LOAD.get(), nanosToMillis(startTime));
        }
    }

    @Nullable
    public synchronized SoundData getData(ResourceLocation id) {
        SoundData cached = loadedDataMap.get(id);
        if (cached != null) {
            return cached;
        }

        ResourceManager manager = resourceManager;
        ResourceLocation soundPath = soundPathMap.get(id);
        if (manager == null || soundPath == null) {
            return null;
        }

        CompletableFuture<SoundData> loadingTask = loadingTasks.get(id);
        if (loadingTask != null && loadingTask.isDone()) {
            loadingTasks.remove(id);
        }
        if (loadingTask != null && !loadingTask.isDone() && GunMod.LOGGER.isDebugEnabled()) {
            GunMod.LOGGER.debug(MARKER, "Sound {} requested while background preload is still running; fallback to synchronous load on thread={}",
                    id, Thread.currentThread().getName());
        }

        SoundData loaded = loadSound(manager, id, soundPath, "sync-get");
        if (loaded != null) {
            loadedDataMap.put(id, loaded);
        }
        return loaded;
    }

    public synchronized void preload(ResourceLocation id) {
        if (!SoundConfig.ENABLE_LAZY_SOUND_LOAD.get()) {
            return;
        }
        if (loadedDataMap.containsKey(id) || loadingTasks.containsKey(id)) {
            return;
        }

        ResourceManager manager = resourceManager;
        ResourceLocation soundPath = soundPathMap.get(id);
        if (manager == null || soundPath == null) {
            return;
        }

        long currentGeneration = generation;
        if (GunMod.LOGGER.isDebugEnabled()) {
            GunMod.LOGGER.debug(MARKER, "Queue sound preload: id={}, path={}, generation={}", id, soundPath, currentGeneration);
        }
        CompletableFuture<SoundData> future = CompletableFuture.supplyAsync(() -> loadSound(manager, id, soundPath, "async-preload"), PRELOAD_EXECUTOR);
        loadingTasks.put(id, future);
        future.whenComplete((data, throwable) -> {
            synchronized (this) {
                if (loadingTasks.get(id) == future) {
                    loadingTasks.remove(id);
                }
                if (throwable != null) {
                    GunMod.LOGGER.warn(MARKER, "Unexpected sound preload failure: {}", id, throwable);
                    return;
                }
                if (data == null || generation != currentGeneration) {
                    return;
                }
                loadedDataMap.put(id, data);
            }
        });
    }

    @Nullable
    private SoundData loadSound(ResourceManager manager, ResourceLocation id, ResourceLocation soundPath, String trigger) {
        long startTime = System.nanoTime();
        try {
            Resource resource = manager.getResource(soundPath).orElse(null);
            if (resource == null) {
                GunMod.LOGGER.warn(MARKER, "Missing sound resource: {}", soundPath);
                return null;
            }
            try (InputStream stream = resource.open(); OggAudioStream audioStream = new OggAudioStream(stream)) {
                ByteBuffer bytebuffer = audioStream.readAll();
                SoundData soundData = new SoundData(bytebuffer, audioStream.getFormat());
                if (GunMod.LOGGER.isDebugEnabled()) {
                    GunMod.LOGGER.debug(MARKER,
                            "Loaded sound: id={}, path={}, trigger={}, bytes={}, format={}, channels={}, sampleRate={}, thread={}, costMs={}",
                            id, soundPath, trigger, bytebuffer.capacity(), audioStream.getFormat().getEncoding(),
                            audioStream.getFormat().getChannels(), audioStream.getFormat().getSampleRate(),
                            Thread.currentThread().getName(), nanosToMillis(startTime));
                }
                return soundData;
            }
        } catch (IOException exception) {
            GunMod.LOGGER.warn(MARKER, "Failed to read sound file: {}", soundPath, exception);
            return null;
        }
    }

    private static String nanosToMillis(long startTime) {
        return String.format("%.3f", (System.nanoTime() - startTime) / 1_000_000.0D);
    }

    public synchronized int getLoadedCount() {
        return loadedDataMap.size();
    }

    public synchronized int getKnownCount() {
        return soundPathMap.size();
    }
}
