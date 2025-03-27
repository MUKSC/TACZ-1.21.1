package com.tacz.guns.config;

import com.tacz.guns.config.client.KeyConfig;
import com.tacz.guns.config.client.RenderConfig;
import com.tacz.guns.config.client.ZoomConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public class ClientConfig {
    public static ModConfigSpec spec;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        KeyConfig.init(builder);
        RenderConfig.init(builder);
        ZoomConfig.init(builder);
        spec = builder.build();
    }
}
