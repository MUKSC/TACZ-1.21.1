package com.tacz.guns.init;

import com.tacz.guns.GunMod;
import com.tacz.guns.inventory.GunSmithTableMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModContainer {
    public static final DeferredRegister<MenuType<?>> CONTAINER_TYPE = DeferredRegister.create(BuiltInRegistries.MENU, GunMod.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<GunSmithTableMenu>> GUN_SMITH_TABLE_MENU = CONTAINER_TYPE.register("gun_smith_table_menu", () -> GunSmithTableMenu.TYPE);
}
