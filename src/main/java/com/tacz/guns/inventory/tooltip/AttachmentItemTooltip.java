package com.tacz.guns.inventory.tooltip;

import com.tacz.guns.api.item.attachment.AttachmentType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

public class AttachmentItemTooltip implements TooltipComponent {
    private final ItemStack attachmentItem;
    private final ResourceLocation attachmentId;
    private final AttachmentType type;

    public AttachmentItemTooltip(ResourceLocation attachmentId, AttachmentType type, ItemStack attachmentItem) {
        this.attachmentId = attachmentId;
        this.type = type;
        this.attachmentItem = attachmentItem;
    }

    public ResourceLocation getAttachmentId() {
        return attachmentId;
    }

    public AttachmentType getType() {
        return type;
    }

    public ItemStack getAttachmentItem() {
        return attachmentItem;
    }
}
