package com.tacz.guns.compat.cloth.common;

import com.google.common.collect.Lists;
import com.tacz.guns.config.common.AmmoConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.network.chat.Component;

public class AmmoClothConfig {
    public static void init(ConfigBuilder root, ConfigEntryBuilder entryBuilder) {
        ConfigCategory ammo = root.getOrCreateCategory(Component.translatable("config.tacz.common.ammo"));

        ammo.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.tacz.common.ammo.explosive_ammo_destroys_blocks"), AmmoConfig.EXPLOSIVE_AMMO_DESTROYS_BLOCK.get())
                .setDefaultValue(false).setTooltip(Component.translatable("config.tacz.common.ammo.explosive_ammo_destroys_blocks.desc"))
                .setSaveConsumer(value -> {
                    AmmoConfig.EXPLOSIVE_AMMO_DESTROYS_BLOCK.set(value);
                    AmmoConfig.EXPLOSIVE_AMMO_DESTROYS_BLOCK.save();
                }).build());

        ammo.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.tacz.common.ammo.explosive_ammo_fire"), AmmoConfig.EXPLOSIVE_AMMO_FIRE.get())
                .setDefaultValue(false).setTooltip(Component.translatable("config.tacz.common.ammo.explosive_ammo_fire.desc"))
                .setSaveConsumer(value -> {
                    AmmoConfig.EXPLOSIVE_AMMO_FIRE.set(value);
                    AmmoConfig.EXPLOSIVE_AMMO_FIRE.save();
                }).build());

        ammo.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.tacz.common.ammo.explosive_ammo_knock_back"), AmmoConfig.EXPLOSIVE_AMMO_KNOCK_BACK.get())
                .setDefaultValue(true).setTooltip(Component.translatable("config.tacz.common.ammo.explosive_ammo_knock_back.desc"))
                .setSaveConsumer(value -> {
                    AmmoConfig.EXPLOSIVE_AMMO_KNOCK_BACK.set(value);
                    AmmoConfig.EXPLOSIVE_AMMO_KNOCK_BACK.save();
                }).build());

        ammo.addEntry(entryBuilder.startIntField(Component.translatable("config.tacz.common.ammo.explosive_ammo_visible_distance"), AmmoConfig.EXPLOSIVE_AMMO_VISIBLE_DISTANCE.get())
                .setMin(0).setMax(Integer.MAX_VALUE).setDefaultValue(192).setTooltip(Component.translatable("config.tacz.common.ammo.explosive_ammo_visible_distance.desc"))
                .setSaveConsumer(value -> {
                    AmmoConfig.EXPLOSIVE_AMMO_VISIBLE_DISTANCE.set(value);
                    AmmoConfig.EXPLOSIVE_AMMO_VISIBLE_DISTANCE.save();
                }).build());

        ammo.addEntry(entryBuilder.startStrList(Component.translatable("config.tacz.common.ammo.pass_through_blocks"), AmmoConfig.PASS_THROUGH_BLOCKS.get())
                .setDefaultValue(Lists.newArrayList()).setTooltip(Component.translatable("config.tacz.common.ammo.pass_through_blocks.desc"))
                .setSaveConsumer(value -> {
                    AmmoConfig.PASS_THROUGH_BLOCKS.set(value);
                    AmmoConfig.PASS_THROUGH_BLOCKS.save();
                }).build());

        ammo.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.tacz.common.ammo.destroy_glass"), AmmoConfig.DESTROY_GLASS.get())
                .setDefaultValue(true).setTooltip(Component.translatable("config.tacz.common.ammo.destroy_glass.desc"))
                .setSaveConsumer(value -> {
                    AmmoConfig.DESTROY_GLASS.set(value);
                    AmmoConfig.DESTROY_GLASS.save();
                }).build());

        ammo.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.tacz.common.ammo.ignite_block"), AmmoConfig.IGNITE_BLOCK.get())
                .setDefaultValue(true).setTooltip(Component.translatable("config.tacz.common.ammo.ignite_block.desc"))
                .setSaveConsumer(value -> {
                    AmmoConfig.IGNITE_BLOCK.set(value);
                    AmmoConfig.IGNITE_BLOCK.save();
                }).build());

        ammo.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.tacz.common.ammo.ignite_entity"), AmmoConfig.IGNITE_ENTITY.get())
                .setDefaultValue(true).setTooltip(Component.translatable("config.tacz.common.ammo.ignite_entity.desc"))
                .setSaveConsumer(value -> {
                    AmmoConfig.IGNITE_ENTITY.set(value);
                    AmmoConfig.IGNITE_ENTITY.save();
                }).build());

        ammo.addEntry(entryBuilder.startDoubleField(Component.translatable("config.tacz.common.ammo.global_speed_modifier"), AmmoConfig.GLOBAL_BULLET_SPEED_MODIFIER.get())
                .setDefaultValue(2.0).setTooltip(Component.translatable("config.tacz.common.ammo.global_speed_modifier.desc"))
                .setSaveConsumer(value -> {
                    AmmoConfig.GLOBAL_BULLET_SPEED_MODIFIER.set(value);
                    AmmoConfig.GLOBAL_BULLET_SPEED_MODIFIER.save();
                }).build());
    }
}
