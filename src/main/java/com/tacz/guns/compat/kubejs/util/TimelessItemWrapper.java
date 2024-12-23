package com.tacz.guns.compat.kubejs.util;

import com.tacz.guns.api.item.builder.AmmoItemBuilder;
import com.tacz.guns.api.item.builder.AttachmentItemBuilder;
import com.tacz.guns.api.item.gun.AbstractGunItem;
import com.tacz.guns.item.AmmoItem;
import com.tacz.guns.item.AttachmentItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

public class TimelessItemWrapper {
    //ItemWrapper，方便KubeJS引用
    //后续可能更改，重新设计
    public static ItemStack gunItem(Consumer<GunNbtFactory> callback) {
        GunNbtFactory itemBuilder = new GunNbtFactory();
        callback.accept(itemBuilder);
        return itemBuilder.build();
    }

    public static ItemStack gunItem(Item item, Consumer<GunNbtFactory> callback) {
        GunNbtFactory itemBuilder = item instanceof AbstractGunItem gunItem ? new GunNbtFactory(gunItem) : new GunNbtFactory();
        callback.accept(itemBuilder);
        return itemBuilder.build();
    }

    public static ItemStack attachmentItem(Consumer<AttachmentItemBuilder> callback) {
        AttachmentItemBuilder itemBuilder = AttachmentItemBuilder.create();
        callback.accept(itemBuilder);
        return itemBuilder.build();
    }

    public static ItemStack attachmentItem(Item item, Consumer<AttachmentNbtFactory> callback) {
        AttachmentNbtFactory itemBuilder = item instanceof AttachmentItem attachmentItem ? new AttachmentNbtFactory(attachmentItem) : new AttachmentNbtFactory();
        callback.accept(itemBuilder);
        return itemBuilder.build();
    }

    public static ItemStack ammoItem(Consumer<AmmoItemBuilder> callback) {
        AmmoItemBuilder itemBuilder = AmmoItemBuilder.create();
        callback.accept(itemBuilder);
        return itemBuilder.build();
    }

    public static ItemStack ammoItem(Item item, Consumer<AmmoNbtFactory> callback) {
        AmmoNbtFactory itemBuilder = item instanceof AmmoItem ammoItem ? new AmmoNbtFactory(ammoItem) : new AmmoNbtFactory();
        callback.accept(itemBuilder);
        return itemBuilder.build();
    }
}
