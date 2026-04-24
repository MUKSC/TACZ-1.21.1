package com.tacz.guns.client.resource.manager;

import com.google.gson.Gson;
import com.tacz.guns.client.resource.pojo.display.IDisplay;
import com.tacz.guns.resource.manager.JsonDataManager;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Predicate;

/**
 * 通用数据管理器<br>
 * 从资源包/数据包中读取json文件并解析为数据
 * @param <T> 数据类型
 */
public class DisplayManager<T extends IDisplay> extends JsonDataManager<T> {
    public DisplayManager(Class<T> dataClass, Gson pGson, String directory, String marker) {
        super(dataClass, pGson, FileToIdConverter.json(directory), marker);
    }

    public DisplayManager(Class<T> dataClass, Gson pGson, FileToIdConverter fileToIdConverter, String marker) {
        super(dataClass, pGson, fileToIdConverter, marker);
    }

    public DisplayManager(Class<T> dataClass, Gson pGson, String directory, String marker, Predicate<ResourceLocation> eagerLoadPredicate) {
        super(dataClass, pGson, FileToIdConverter.json(directory), marker, eagerLoadPredicate);
    }

    @Override
    protected void postLoad(T data) {
        data.init();
    }
}
