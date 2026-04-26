package com.tacz.guns.config.client;

import net.minecraftforge.common.ForgeConfigSpec;

public class ResourceConfig {
    public static ForgeConfigSpec.BooleanValue ENABLE_LAZY_CLIENT_ASSET_LOAD;
    public static ForgeConfigSpec.BooleanValue ENABLE_LAZY_SOUND_LOAD;

    public static void init(ForgeConfigSpec.Builder builder) {
        builder.push("resource");

        builder.comment("Build heavy TACZ client assets such as models and animation state machines on demand.",
                "Inventory items are pre-warmed in the background when possible.",
                "If a render needs an asset before warmup finishes, the render thread will wait for it once.");
        ENABLE_LAZY_CLIENT_ASSET_LOAD = builder.define("EnableLazyClientAssetLoad", true);

        builder.comment("Load TACZ sound buffers on demand instead of decoding all gun-pack sounds during resource reload.");
        ENABLE_LAZY_SOUND_LOAD = builder.define("EnableLazySoundLoad", true);

        builder.pop();
    }
}
