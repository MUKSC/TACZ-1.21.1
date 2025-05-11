package com.tacz.guns.compat.kubejs.util;

import com.tacz.guns.api.item.IAttachment;
import com.tacz.guns.item.AttachmentItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class AttachmentNbtFactory extends TimelessItemNbtFactory<AttachmentItem, AttachmentNbtFactory>{

    public AttachmentNbtFactory(@Nonnull AttachmentItem item) {
        super(item);
    }

    public AttachmentNbtFactory() {
        super((AttachmentItem) TimelessItemType.ATTACHMENT.getItem());
    }

    @Deprecated
    public void setSkinId(ResourceLocation skinId) {
    }

    @Override
    public ItemStack build() {
        ItemStack stack = new ItemStack(item, count);
        if (item instanceof IAttachment iAttachment) {
            iAttachment.setAttachmentId(stack, id);
        }
        return stack;
    }
}
