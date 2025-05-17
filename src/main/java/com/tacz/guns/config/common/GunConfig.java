package com.tacz.guns.config.common;


import net.neoforged.neoforge.common.ModConfigSpec;

public class GunConfig {
    public static ModConfigSpec.IntValue DEFAULT_GUN_FIRE_SOUND_DISTANCE;
    public static ModConfigSpec.IntValue DEFAULT_GUN_SILENCE_SOUND_DISTANCE;
    public static ModConfigSpec.IntValue DEFAULT_GUN_OTHER_SOUND_DISTANCE;
    public static ModConfigSpec.BooleanValue CREATIVE_PLAYER_CONSUME_AMMO;
    public static ModConfigSpec.BooleanValue AUTO_RELOAD_WHEN_RESPAWN;

    public static void init(ModConfigSpec.Builder builder) {
        builder.push("gun");

        builder.comment("The default fire sound range (block)");
        DEFAULT_GUN_FIRE_SOUND_DISTANCE = builder.defineInRange("DefaultGunFireSoundDistance", 64, 0, Integer.MAX_VALUE);

        builder.comment("The silencer default fire sound range (block)");
        DEFAULT_GUN_SILENCE_SOUND_DISTANCE = builder.defineInRange("DefaultGunSilenceSoundDistance", 16, 0, Integer.MAX_VALUE);

        builder.comment("The range (block) of other gun sound, reloading sound etc.");
        DEFAULT_GUN_OTHER_SOUND_DISTANCE = builder.defineInRange("DefaultGunOtherSoundDistance", 16, 0, Integer.MAX_VALUE);

        builder.comment("Whether or not the player will consume ammo in creative mode");
        CREATIVE_PLAYER_CONSUME_AMMO = builder.define("CreativePlayerConsumeAmmo", true);

        builder.comment("Auto reload all the guns in player inventory, useful for pvp servers");
        AUTO_RELOAD_WHEN_RESPAWN = builder.define("AutoReloadWhenRespawn", false);

        builder.pop();
    }
}
