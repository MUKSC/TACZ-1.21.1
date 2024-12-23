package com.tacz.guns.compat.kubejs.util;

import com.google.common.collect.Maps;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.attachment.AttachmentType;
import com.tacz.guns.api.item.gun.AbstractGunItem;
import com.tacz.guns.api.item.gun.FireMode;
import com.tacz.guns.item.AttachmentItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.EnumMap;

public class GunNbtFactory extends TimelessItemNbtFactory<AbstractGunItem, GunNbtFactory>{
    //可以在任何时候引用而无需担心加载时机
    //但是无法自动匹配正确的物品类型（由于目前缺少添加新物品的附属模组，故可以认为暂时不需要）
    private int ammoCount = 0;
    private FireMode fireMode = FireMode.UNKNOWN;
    private boolean bulletInBarrel = false;
    private EnumMap<AttachmentType, ResourceLocation> attachments = Maps.newEnumMap(AttachmentType.class);

    public GunNbtFactory(@Nonnull AbstractGunItem item) {
        super(item);
    }

    public GunNbtFactory() {
        super((AbstractGunItem) TimelessItemType.MODERN_KINETIC_GUN.getItem());
    }

    public GunNbtFactory setAmmoCount(int ammoCount) {
        this.ammoCount = ammoCount;
        return this;
    }

    public GunNbtFactory setFireMode(FireMode fireMode) {
        this.fireMode = fireMode;
        return this;
    }

    public GunNbtFactory setBulletInBarrel(boolean bulletInBarrel) {
        this.bulletInBarrel = bulletInBarrel;
        return this;
    }

    public GunNbtFactory putAttachment(AttachmentType type, ResourceLocation attachmentId) {
        this.attachments.put(type, attachmentId);
        return this;
    }

    public GunNbtFactory putAllAttachment(EnumMap<AttachmentType, ResourceLocation> attachments) {
        this.attachments = attachments;
        return this;
    }

    @Override
    public ItemStack build() {
        ItemStack stack = new ItemStack(item, count);
        if (item instanceof IGun iGun) {
            iGun.setGunId(stack, id);
            iGun.setFireMode(stack, fireMode);
            iGun.setCurrentAmmoCount(stack, ammoCount);
            iGun.setBulletInBarrel(stack, bulletInBarrel);
            attachments.forEach((attachmentType, attachmentId) -> {
                ItemStack attachmentStack = new AttachmentNbtFactory((AttachmentItem) TimelessItemType.ATTACHMENT.getItem())
                        .setId(attachmentId)
                        .build();
                iGun.installAttachment(stack, attachmentStack);
            });
        }
        return stack;
    }
}
