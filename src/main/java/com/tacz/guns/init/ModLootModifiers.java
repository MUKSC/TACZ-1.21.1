package com.tacz.guns.init;

import com.mojang.serialization.Codec;
import com.tacz.guns.GunMod;
import com.tacz.guns.loot.LootTableInjectorModifier;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModLootModifiers {
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIER_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, GunMod.MOD_ID);

    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> LOOT_TABLE_INJECTOR =
            LOOT_MODIFIER_SERIALIZERS.register("loot_table_injector", () -> LootTableInjectorModifier.CODEC);
}
