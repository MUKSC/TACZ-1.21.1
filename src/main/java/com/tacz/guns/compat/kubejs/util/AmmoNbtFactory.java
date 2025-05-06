package com.tacz.guns.compat.kubejs.util;

import com.tacz.guns.api.item.IAmmo;
import com.tacz.guns.item.AmmoItem;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class AmmoNbtFactory extends TimelessItemNbtFactory<AmmoItem, AmmoNbtFactory> {
    public AmmoNbtFactory(@Nonnull AmmoItem item) {
        super(item);
    }

    public AmmoNbtFactory() {
        super((AmmoItem) TimelessItemType.AMMO.getItem());
    }

    @Override
    public ItemStack build() {
        ItemStack stack = new ItemStack(item, count);
        if (item instanceof IAmmo iAmmo) {
            iAmmo.setAmmoId(stack, id);
        }
        return stack;
    }
}
