package com.tacz.guns.resource.manager;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.tacz.guns.GunMod;
import com.tacz.guns.util.ResourceScanner;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Reader;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * 通用数据管理器，采用懒加载策略<br>
 * 从资源包/数据包中读取json文件并解析为数据
 * @param <T> 数据类型
 */
public class LazyJsonDataManager<T> extends SimplePreparableReloadListener<LazyJsonDataManager.PreparedResult<T>> {

    protected final Map<ResourceLocation, T> dataMap = Maps.newHashMap();
    protected final Map<ResourceLocation, Supplier<LoadResult<T>>> lazyLoaderMap = Maps.newHashMap();
    protected final Set<ResourceLocation> failedData = new HashSet<>();
    protected final Set<ResourceLocation> allResourceIds = new HashSet<>();

    private final Gson gson;
    private final Class<T> dataClass;
    private final Marker marker;
    private final FileToIdConverter fileToIdConverter;
    private final Predicate<ResourceLocation> eagerLoadPredicate;

    public LazyJsonDataManager(Class<T> dataClass, Gson pGson, FileToIdConverter fileToIdConverter, String marker) {
        this(dataClass, pGson, fileToIdConverter, marker, id -> true);
    }

    public LazyJsonDataManager(Class<T> dataClass, Gson pGson, String directory, String marker, Predicate<ResourceLocation> eagerLoadPredicate) {
        this(dataClass, pGson, FileToIdConverter.json(directory), marker, eagerLoadPredicate);
    }

    public LazyJsonDataManager(Class<T> dataClass, Gson pGson, FileToIdConverter fileToIdConverter, String marker, Predicate<ResourceLocation> eagerLoadPredicate) {
        this.gson = pGson;
        this.dataClass = dataClass;
        this.marker = MarkerManager.getMarker(marker);
        this.fileToIdConverter = fileToIdConverter;
        this.eagerLoadPredicate = eagerLoadPredicate;
    }

    @NotNull
    @Override
    protected PreparedResult<T> prepare(ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        Map<ResourceLocation, ResourceLocation> scannedResources = ResourceScanner.scanDirectoryResources(pResourceManager, fileToIdConverter);
        Map<ResourceLocation, PreparedEntry<T>> preparedEntries = Maps.newHashMapWithExpectedSize(scannedResources.size());
        for (Map.Entry<ResourceLocation, ResourceLocation> entry : scannedResources.entrySet()) {
            ResourceLocation id = entry.getKey();
            ResourceLocation resourcePath = entry.getValue();
            // 给默认包的东西直接加载好
            if (shouldEagerLoad(id)) {
                JsonElement sourceElement = readResourceElement(pResourceManager, resourcePath);
                if (sourceElement == null) {
                    preparedEntries.put(id, PreparedEntry.failure());
                } else {
                    preparedEntries.put(id, PreparedEntry.eager(sourceElement));
                }
            } else {
                preparedEntries.put(id, PreparedEntry.lazy(() -> loadResource(id, resourcePath, pResourceManager)));
            }
        }
        return new PreparedResult<>(preparedEntries, Set.copyOf(scannedResources.keySet()));
    }

    @Override
    protected void apply(PreparedResult<T> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        dataMap.clear();
        lazyLoaderMap.clear();
        failedData.clear();
        allResourceIds.clear();
        allResourceIds.addAll(pObject.allResourceIds());
        for (Map.Entry<ResourceLocation, PreparedEntry<T>> entry : pObject.entries().entrySet()) {
            ResourceLocation id = entry.getKey();
            PreparedEntry<T> preparedEntry = entry.getValue();
            if (preparedEntry.failed()) {
                failedData.add(id);
            } else if (preparedEntry.loader() != null) {
                lazyLoaderMap.put(id, preparedEntry.loader());
            } else {
                LoadResult<T> loaded = loadResourceFromElement(id, preparedEntry.sourceElement());
                if (loaded.failed()) {
                    failedData.add(id);
                } else if (loaded.data() != null) {
                    dataMap.put(id, loaded.data());
                }
            }
        }
    }

    protected @Nullable T parseJson(JsonElement element) {
        return gson.fromJson(element, getDataClass());
    }

    protected void postLoad(T data) {
    }

    protected final LoadResult<T> loadResource(ResourceLocation id, ResourceLocation resourcePath, ResourceManager manager) {
        JsonElement sourceElement = readResourceElement(manager, resourcePath);
        if (sourceElement == null) {
            return LoadResult.failure();
        }
        return loadResourceFromElement(id, sourceElement);
    }

    protected final LoadResult<T> loadResourceFromElement(ResourceLocation id, JsonElement sourceElement) {
        try {
            T data = parseJson(sourceElement);
            if (data != null) {
                postLoad(data);
            }
            return LoadResult.success(data);
        } catch (JsonParseException | IllegalArgumentException e) {
            GunMod.LOGGER.error(marker, "Failed to load data file {}", id, e);
            return LoadResult.failure();
        }
    }

    @Nullable
    protected final Reader openReader(ResourceManager manager, ResourceLocation resourcePath) throws IOException {
        Resource resource = manager.getResource(resourcePath).orElse(null);
        return resource == null ? null : resource.openAsReader();
    }

    @Nullable
    protected final JsonElement readResourceElement(ResourceManager manager, ResourceLocation resourcePath) {
        try (Reader reader = openReader(manager, resourcePath)) {
            return reader == null ? null : GsonHelper.fromJson(gson, reader, JsonElement.class, true);
        } catch (IOException | JsonParseException | IllegalArgumentException exception) {
            GunMod.LOGGER.error(marker, "Failed to read raw data file {}", resourcePath, exception);
            return null;
        }
    }

    public Class<T> getDataClass() {
        return dataClass;
    }

    public Marker getMarker() {
        return marker;
    }

    public Gson getGson() {
        return gson;
    }

    public T getData(ResourceLocation id) {
        T data = dataMap.get(id);
        if (data != null || failedData.contains(id)) {
            return data;
        }
        Supplier<LoadResult<T>> loader = lazyLoaderMap.get(id);
        if (loader == null) {
            return null;
        }
        synchronized (this) {
            data = dataMap.get(id);
            if (data != null || failedData.contains(id)) {
                return data;
            }
            loader = lazyLoaderMap.remove(id);
            if (loader == null) {
                return null;
            }
            LoadResult<T> loaded = loader.get();
            if (loaded.failed()) {
                failedData.add(id);
                return null;
            }
            data = loaded.data();
            if (data != null) {
                dataMap.put(id, data);
            }
            failedData.remove(id);
            return data;
        }
    }

    public Map<ResourceLocation, T> getAllData() {
        return dataMap;
    }

    public Set<ResourceLocation> getAllIds() {
        return allResourceIds;
    }

    protected final boolean shouldEagerLoad(ResourceLocation id) {
        return eagerLoadPredicate.test(id);
    }

    protected final FileToIdConverter getFileToIdConverter() {
        return fileToIdConverter;
    }

    protected record LoadResult<T>(@Nullable T data, boolean failed) {
        protected static <T> LoadResult<T> success(@Nullable T data) {
            return new LoadResult<>(data, false);
        }

        protected static <T> LoadResult<T> failure() {
            return new LoadResult<>(null, true);
        }
    }

    protected record PreparedEntry<T>(@Nullable JsonElement sourceElement,
                                      @Nullable Supplier<LoadResult<T>> loader, boolean failed) {
        protected static <T> PreparedEntry<T> eager(@Nullable JsonElement sourceElement) {
            return new PreparedEntry<>(sourceElement, null, false);
        }

        protected static <T> PreparedEntry<T> lazy(Supplier<LoadResult<T>> loader) {
            return new PreparedEntry<>(null, loader, false);
        }

        protected static <T> PreparedEntry<T> failure() {
            return new PreparedEntry<>(null, null, true);
        }
    }

    protected record PreparedResult<T>(Map<ResourceLocation, PreparedEntry<T>> entries,
                                       Set<ResourceLocation> allResourceIds) {
    }
}
