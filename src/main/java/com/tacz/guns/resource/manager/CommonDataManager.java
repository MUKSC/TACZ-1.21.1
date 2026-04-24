package com.tacz.guns.resource.manager;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.tacz.guns.resource.network.DataType;
import com.tacz.guns.util.ResourceScanner;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

/**
 * 服务端侧数据管理器<br>
 * 该类型的数据管理器用于服务端数据加载并向客户端同步
 * @param <T> 数据类型
 */
public class CommonDataManager<T> extends JsonDataManager<T> implements INetworkCacheReloadListener {
    private final DataType type;
    protected Map<ResourceLocation, String> networkCache;

    public CommonDataManager(DataType type, Class<T> dataClass, Gson pGson, String directory, String marker) {
        super(dataClass, pGson, directory, marker);
        this.type = type;
    }

    @Override
    @NotNull
    protected PreparedResult<T> prepare(ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        Map<ResourceLocation, JsonElement> scannedResources = ResourceScanner.scanDirectory(pResourceManager, getFileToIdConverter(), getGson());
        Map<ResourceLocation, PreparedEntry<T>> preparedEntries = Maps.newHashMapWithExpectedSize(scannedResources.size());
        for (Map.Entry<ResourceLocation, JsonElement> entry : scannedResources.entrySet()) {
            ResourceLocation id = entry.getKey();
            JsonElement sourceElement = entry.getValue();
            if (sourceElement == null) {
                preparedEntries.put(id, PreparedEntry.failure());
                continue;
            }
            preparedEntries.put(id, PreparedEntry.eager(sourceElement));
        }
        return new PreparedResult<>(preparedEntries, Set.copyOf(scannedResources.keySet()));
    }

    @Override
    protected void apply(PreparedResult<T> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        super.apply(pObject, pResourceManager, pProfiler);

        ImmutableMap.Builder<ResourceLocation, String> builder = ImmutableMap.builder();
        pObject.entries().forEach((id, preparedEntry) -> {
            JsonElement sourceElement = preparedEntry.sourceElement();
            if (sourceElement != null) {
                builder.put(id, getGson().toJson(sourceElement));
            }
        });
        this.networkCache = builder.build();
    }

    public void clear() {
        this.dataMap.clear();
        this.lazyLoaderMap.clear();
    }

    public Map<ResourceLocation, String> getNetworkCache() {
        return this.networkCache;
    }

    public DataType getType() {
        return this.type;
    }
}
