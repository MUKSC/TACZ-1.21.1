package com.tacz.guns.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.nio.file.Path;

/* FIXME: I'm not sure what these are supposed do and how to implement this on NeoForge
public class PreLoadModConfig extends ModConfig {
    private CommentedConfig configData;
    private final ModContainer container;

    public PreLoadModConfig(Type type, IConfigSpec<?> spec, ModContainer container, String fileName) {
        super(type, spec, container, fileName);
        this.container = container;
    }

    public CommentedConfig getConfigData() {
        return this.configData;
    }

    public void setConfigData(final CommentedConfig configData) {
        this.configData = configData;
        this.getSpec().acceptConfig(this.configData);
    }

    public void fireEvent(final IConfigEvent configEvent) {
        this.container.dispatchConfigEvent(configEvent);
    }

    public void save() {
        ((CommentedFileConfig)this.configData).save();
    }

    public Path getFullPath() {
        return ((CommentedFileConfig)this.configData).getNioPath();
    }
}*/
