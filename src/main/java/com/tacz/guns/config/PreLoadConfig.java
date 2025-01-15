package com.tacz.guns.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.tacz.guns.GunMod;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.nio.file.Path;

public class PreLoadConfig {
    public static ModConfigSpec spec;
    public static ModConfigSpec.BooleanValue override;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.push("gunpack");
        builder.comment("When enabled, the mod will not try to overwrite the default pack under .minecraft/tacz\n" +
                "Since 1.0.4, the overwriting will only run when you start client or a dedicated server");
        override = builder.define("DefaultPackDebug", false);
        builder.pop();
        spec = builder.build();
    }

    /* FIXME: I'm not sure what these are supposed do and how to implement this on NeoForge
    public static PreLoadModConfig getModConfig() {
        ModContainer container = GunMod.container;
        var c = new PreLoadModConfig(ModConfig.Type.COMMON, spec, container, "tacz-pre.toml");
        // 从 ConfigTracker 中移除，防止从默认文件夹重复加载
        ConfigTracker.INSTANCE.configSets().get(ModConfig.Type.COMMON).remove(c);
        ConfigTracker.INSTANCE.fileMap().remove(c.getFileName(), c);
        return c;
    }

    public static void load(Path configBasePath) {
        if (spec.isLoaded()) return;
        PreLoadModConfig config = getModConfig();
        final CommentedFileConfig configData = config.getHandler().reader(configBasePath).apply(config);
        config.setConfigData(configData);
        config.fireEvent(IConfigEvent.loading(config));
        config.save();
    }*/
}
