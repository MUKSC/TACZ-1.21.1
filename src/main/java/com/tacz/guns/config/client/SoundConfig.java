package com.tacz.guns.config.client;

import net.minecraftforge.common.ForgeConfigSpec;

public class SoundConfig {
    public static ForgeConfigSpec.BooleanValue ENABLE_LAZY_SOUND_LOAD;

    public static void init(ForgeConfigSpec.Builder builder) {
        builder.push("sound");

        builder.comment("Load TACZ sound buffers on demand instead of decoding all gun-pack sounds during resource reload.");
        ENABLE_LAZY_SOUND_LOAD = builder.define("EnableLazySoundLoad", true);

        builder.pop();
    }
}
