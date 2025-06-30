package com.tacz.guns.config.common;

import com.google.common.collect.Lists;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public class AmmoConfig {
    public static ModConfigSpec.BooleanValue EXPLOSIVE_AMMO_DESTROYS_BLOCK;
    public static ModConfigSpec.BooleanValue EXPLOSIVE_AMMO_FIRE;
    public static ModConfigSpec.BooleanValue EXPLOSIVE_AMMO_KNOCK_BACK;
    public static ModConfigSpec.IntValue EXPLOSIVE_AMMO_VISIBLE_DISTANCE;
    public static ModConfigSpec.ConfigValue<List<String>> PASS_THROUGH_BLOCKS;
    public static ModConfigSpec.BooleanValue DESTROY_GLASS;
    public static ModConfigSpec.BooleanValue IGNITE_BLOCK;
    public static ModConfigSpec.BooleanValue IGNITE_ENTITY;
    public static ModConfigSpec.DoubleValue GLOBAL_BULLET_SPEED_MODIFIER;

    public static void init(ModConfigSpec.Builder builder) {
        builder.push("ammo");

        builder.comment("Warning: Ammo with explosive properties can break blocks");
        EXPLOSIVE_AMMO_DESTROYS_BLOCK = builder.define("ExplosiveAmmoDestroysBlock", true);

        builder.comment("Warning: Ammo with explosive properties can set the surroundings on fire");
        EXPLOSIVE_AMMO_FIRE = builder.define("ExplosiveAmmoFire", false);

        builder.comment("Ammo with explosive properties can add knockback effect");
        EXPLOSIVE_AMMO_KNOCK_BACK = builder.define("ExplosiveAmmoKnockBack", true);

        builder.comment("The distance at which the explosion effect can be seen");
        EXPLOSIVE_AMMO_VISIBLE_DISTANCE = builder.defineInRange("ExplosiveAmmoVisibleDistance", 192, 0, Integer.MAX_VALUE);

        builder.comment("Those blocks that the ammo can pass through");
        PASS_THROUGH_BLOCKS = builder.define("PassThroughBlocks", Lists.newArrayList());

        builder.comment("Whether a ammo can break the glass");
        DESTROY_GLASS = builder.define("DestroyGlass", true);

        builder.comment("Whether a ammo can ignite the block");
        IGNITE_BLOCK = builder.define("IgniteBlock", true);

        builder.comment("Whether a ammo can ignite the entity");
        IGNITE_ENTITY = builder.define("IgniteEntity", true);

        builder.comment("Global bullet speed modifier, the init speed of the bullet will be multiplied by this value, default is 2.0");
        builder.comment("This is to compensate the side effects introduced while fixing the shooter variable input issue");
        GLOBAL_BULLET_SPEED_MODIFIER = builder.defineInRange("GlobalBulletSpeedModifier", 2.0, 0.01, 20);

        builder.pop();
    }
}
