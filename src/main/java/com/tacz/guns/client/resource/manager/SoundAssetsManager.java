package com.tacz.guns.client.resource.manager;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.audio.OggAudioStream;
import com.mojang.blaze3d.audio.SoundBuffer;
import com.tacz.guns.GunMod;
import com.tacz.guns.config.client.ResourceConfig;
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
import org.lwjgl.BufferUtils;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SoundAssetsManager extends SimplePreparableReloadListener<Map<ResourceLocation, ResourceLocation>> {
    private record SoundKey(ResourceLocation id, boolean mono) {
    }

    private static final Marker MARKER = MarkerManager.getMarker("SoundsLoader");
    private static final ExecutorService PRELOAD_EXECUTOR = Executors.newSingleThreadExecutor(r -> {
        Thread thread = new Thread(r, "tacz-sound-preload");
        thread.setDaemon(true);
        return thread;
    });

    private final Map<ResourceLocation, ResourceLocation> soundPathMap = Maps.newHashMap();
    private final Map<SoundKey, SoundBuffer> loadedBuffers = Maps.newHashMap();
    private final Map<SoundKey, CompletableFuture<SoundBuffer>> loadingTasks = new HashMap<>();

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
        clearLoadedBuffers();
        loadingTasks.clear();
        int eagerLoadedCount = 0;
        if (!ResourceConfig.ENABLE_LAZY_SOUND_LOAD.get()) {
            for (Map.Entry<ResourceLocation, ResourceLocation> entry : soundPathMap.entrySet()) {
                SoundKey key = new SoundKey(entry.getKey(), false);
                SoundBuffer loaded = loadSound(pResourceManager, key, entry.getValue(), "reload-eager");
                if (loaded != null) {
                    loadedBuffers.put(key, loaded);
                    eagerLoadedCount++;
                }
            }
        }
        if (GunMod.LOGGER.isDebugEnabled()) {
            GunMod.LOGGER.debug(MARKER, "Sound registry applied: knownEntries={}, eagerLoaded={}, lazyLoadEnabled={}, costMs={}",
                    soundPathMap.size(), eagerLoadedCount, ResourceConfig.ENABLE_LAZY_SOUND_LOAD.get(), nanosToMillis(startTime));
        }
    }

    @Nullable
    public synchronized SoundBuffer getBuffer(@Nullable ResourceLocation id, boolean mono) {
        if (id == null) {
            return null;
        }

        SoundKey key = new SoundKey(id, mono);
        SoundBuffer cached = loadedBuffers.get(key);
        if (cached != null) {
            return cached;
        }

        ResourceManager manager = resourceManager;
        ResourceLocation soundPath = soundPathMap.get(id);
        if (manager == null || soundPath == null) {
            return null;
        }

        CompletableFuture<SoundBuffer> loadingTask = loadingTasks.get(key);
        if (loadingTask != null && loadingTask.isDone()) {
            loadingTasks.remove(key);
        }
        if (loadingTask != null && !loadingTask.isDone() && GunMod.LOGGER.isDebugEnabled()) {
            GunMod.LOGGER.debug(MARKER, "Sound {} requested while background preload is still running; fallback to synchronous load on thread={}",
                    id, Thread.currentThread().getName());
        }

        SoundBuffer loaded = loadSound(manager, key, soundPath, "sync-get");
        if (loaded != null) {
            loadedBuffers.put(key, loaded);
            loadingTasks.remove(key);
        }
        return loaded;
    }

    public synchronized void preload(ResourceLocation id) {
        if (!ResourceConfig.ENABLE_LAZY_SOUND_LOAD.get()) {
            return;
        }
        SoundKey key = new SoundKey(id, false);
        if (loadedBuffers.containsKey(key) || loadingTasks.containsKey(key)) {
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
        CompletableFuture<SoundBuffer> future = CompletableFuture.supplyAsync(() -> loadSound(manager, key, soundPath, "async-preload"), PRELOAD_EXECUTOR);
        loadingTasks.put(key, future);
        future.whenComplete((data, throwable) -> {
            synchronized (this) {
                if (loadingTasks.get(key) != future) {
                    discard(data);
                    return;
                }
                loadingTasks.remove(key);
                if (throwable != null) {
                    GunMod.LOGGER.warn(MARKER, "Unexpected sound preload failure: {}", id, throwable);
                    return;
                }
                if (data == null || generation != currentGeneration) {
                    discard(data);
                    return;
                }
                SoundBuffer existing = loadedBuffers.putIfAbsent(key, data);
                if (existing != null) {
                    discard(data);
                }
            }
        });
    }

    public synchronized void invalidateForSoundEngineReload() {
        generation++;
        loadedBuffers.clear();
        loadingTasks.clear();
    }

    @Nullable
    private SoundBuffer loadSound(ResourceManager manager, SoundKey key, ResourceLocation soundPath, String trigger) {
        long startTime = System.nanoTime();
        try {
            Resource resource = manager.getResource(soundPath).orElse(null);
            if (resource == null) {
                GunMod.LOGGER.warn(MARKER, "Missing sound resource: {}", soundPath);
                return null;
            }
            try (InputStream stream = resource.open(); OggAudioStream audioStream = new OggAudioStream(stream)) {
                ByteBuffer bytebuffer = audioStream.readAll();
                AudioFormat audioFormat = audioStream.getFormat();
                if (key.mono() && audioFormat.getChannels() > 1) {
                    ByteBuffer monoBuffer = downmixToMono(bytebuffer, audioFormat);
                    if (monoBuffer != null) {
                        bytebuffer = monoBuffer;
                        audioFormat = monoFormat(audioFormat);
                    }
                }
                SoundBuffer soundBuffer = new SoundBuffer(bytebuffer, audioFormat);
                if (GunMod.LOGGER.isDebugEnabled()) {
                    GunMod.LOGGER.debug(MARKER,
                            "Loaded sound: id={}, path={}, mono={}, trigger={}, bytes={}, format={}, channels={}, sampleRate={}, thread={}, costMs={}",
                            key.id(), soundPath, key.mono(), trigger, bytebuffer.capacity(), audioFormat.getEncoding(),
                            audioFormat.getChannels(), audioFormat.getSampleRate(),
                            Thread.currentThread().getName(), nanosToMillis(startTime));
                }
                return soundBuffer;
            }
        } catch (IOException exception) {
            GunMod.LOGGER.warn(MARKER, "Failed to read sound file: {}", soundPath, exception);
            return null;
        }
    }

    private static AudioFormat monoFormat(AudioFormat rawFormat) {
        int sampleBytes = rawFormat.getSampleSizeInBits() / Byte.SIZE;
        return new AudioFormat(rawFormat.getEncoding(), rawFormat.getSampleRate(), rawFormat.getSampleSizeInBits(),
                1, sampleBytes, rawFormat.getSampleRate(), rawFormat.isBigEndian(), rawFormat.properties());
    }

    @Nullable
    private static ByteBuffer downmixToMono(ByteBuffer source, AudioFormat format) {
        if (!Encoding.PCM_SIGNED.equals(format.getEncoding()) || format.getSampleSizeInBits() != 16) {
            GunMod.LOGGER.warn(MARKER, "Cannot downmix unsupported sound format to mono: encoding={}, sampleSize={}",
                    format.getEncoding(), format.getSampleSizeInBits());
            return null;
        }

        int channels = format.getChannels();
        int frameSize = format.getFrameSize();
        if (channels <= 0 || frameSize <= 0) {
            GunMod.LOGGER.warn(MARKER, "Cannot downmix sound with invalid channel/frame layout: channels={}, frameSize={}",
                    channels, frameSize);
            return null;
        }

        int frameCount = source.remaining() / frameSize;
        ByteBuffer input = source.duplicate();
        ByteBuffer output = BufferUtils.createByteBuffer(frameCount * Short.BYTES);
        int start = input.position();
        for (int frame = 0; frame < frameCount; frame++) {
            int frameOffset = start + frame * frameSize;
            int mixed = 0;
            for (int channel = 0; channel < channels; channel++) {
                mixed += input.getShort(frameOffset + channel * Short.BYTES);
            }
            output.putShort((short) (mixed / channels));
        }
        output.flip();
        return output;
    }

    private void clearLoadedBuffers() {
        loadedBuffers.values().forEach(SoundAssetsManager::discard);
        loadedBuffers.clear();
    }

    private static void discard(@Nullable SoundBuffer soundBuffer) {
        if (soundBuffer != null) {
            soundBuffer.discardAlBuffer();
        }
    }

    private static String nanosToMillis(long startTime) {
        return String.format("%.3f", (System.nanoTime() - startTime) / 1_000_000.0D);
    }

    public synchronized int getLoadedCount() {
        return loadedBuffers.size();
    }

    public synchronized int getKnownCount() {
        return soundPathMap.size();
    }
}
