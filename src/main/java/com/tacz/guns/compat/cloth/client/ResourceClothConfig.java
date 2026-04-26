package com.tacz.guns.compat.cloth.client;

import com.tacz.guns.config.client.ResourceConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.network.chat.Component;

public class ResourceClothConfig {
    public static void init(ConfigBuilder root, ConfigEntryBuilder entryBuilder) {
        ConfigCategory resource = root.getOrCreateCategory(Component.translatable("config.tacz.client.resource"));

        resource.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.tacz.client.resource.enable_lazy_client_asset_load"), ResourceConfig.ENABLE_LAZY_CLIENT_ASSET_LOAD.get())
                .setDefaultValue(true).setTooltip(Component.translatable("config.tacz.client.resource.enable_lazy_client_asset_load.desc"))
                .setSaveConsumer(ResourceConfig.ENABLE_LAZY_CLIENT_ASSET_LOAD::set).build());
    }
}
