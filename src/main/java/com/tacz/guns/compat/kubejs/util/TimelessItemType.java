package com.tacz.guns.compat.kubejs.util;

import com.tacz.guns.init.ModItems;
import net.minecraft.world.item.Item;

public enum TimelessItemType {
    MODERN_KINETIC_GUN(ModItems.MODERN_KINETIC_GUN.get()),
    AMMO(ModItems.AMMO.get()),
    ATTACHMENT(ModItems.ATTACHMENT.get());

    private final Item item;

    TimelessItemType(Item item) {
        this.item = item;
    }

    public Item getItem() {
        return item;
    }
}
