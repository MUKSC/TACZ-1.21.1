package com.tacz.guns.compat.kubejs.util;

import com.tacz.guns.api.DefaultAssets;
import com.tacz.guns.api.item.builder.AmmoItemBuilder;
import com.tacz.guns.api.item.builder.AttachmentItemBuilder;
import com.tacz.guns.api.item.builder.BlockItemBuilder;
import com.tacz.guns.api.item.gun.AbstractGunItem;
import com.tacz.guns.item.AmmoItem;
import com.tacz.guns.item.AttachmentItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

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

    public static ItemStack blockItem(ItemLike itemLike, Consumer<BlockItemBuilder> callback) {
        BlockItemBuilder itemBuilder = BlockItemBuilder.create(itemLike);
        callback.accept(itemBuilder);
        return itemBuilder.build();
    }

    public static ItemStack of(ResourceLocation id) {
        return ItemIndexInfo.createFromResourceLocation(id).getItemStack();
    }

    public static ItemStack of(Item item, ResourceLocation id) {
        return ItemIndexInfo.createFromResourceLocation(id).getItemStack(item);
    }

    public static class ItemIndexInfo {
        private static final String FALL_BACK = "error";
        private final String parent;
        private final ResourceLocation indexId;
        private static final ItemIndexInfo DEFAULT = new ItemIndexInfo(FALL_BACK, DefaultAssets.EMPTY_GUN_ID);

        private ItemIndexInfo(String pNamespace, String pParent, String pPath) {
            this.parent = (isTypeValid(pParent)) ? pParent : FALL_BACK;
            this.indexId = new ResourceLocation(pNamespace, pPath);
        }

        private ItemIndexInfo(String pParent, ResourceLocation pLocation) {
            this.parent = (isTypeValid(pParent)) ? pParent : FALL_BACK;
            this.indexId = (pLocation != null) ? pLocation : DefaultAssets.EMPTY_GUN_ID;
        }

        public static ItemIndexInfo createFromResourceLocation(ResourceLocation id) {
            String namespace = id.getNamespace();
            String path = id.getPath();
            int i = path.indexOf("/");
            if (i > 0) {
                String type = path.substring(0, i);
                String indexPath = path.substring(i + 1);
                return new ItemIndexInfo(namespace, type, indexPath);
            }
            return DEFAULT;
        }

        public static ItemIndexInfo create(String pLocation) {
            ResourceLocation id = new ResourceLocation(pLocation);
            return createFromResourceLocation(id);
        }

        public String getParent() {
            return parent;
        }

        public ResourceLocation getIndexId() {
            return indexId;
        }

        public ItemStack getItemStack() {
            return switch (parent) {
                case "gun" -> gunItem(builder -> builder.setId(indexId));
                case "attachment" -> attachmentItem(builder -> builder.setId(indexId));
                case "ammo" -> ammoItem(builder -> builder.setId(indexId));
                default -> ItemStack.EMPTY;
            };
        }

        public ItemStack getItemStack(Item item) {
            return switch (parent) {
                case "gun" -> gunItem(item, builder -> builder.setId(indexId));
                case "attachment" -> attachmentItem(item, builder -> builder.setId(indexId));
                case "ammo" -> ammoItem(item, builder -> builder.setId(indexId));
                case "block" -> blockItem(item, builder -> builder.setId(indexId));
                default -> ItemStack.EMPTY;
            };
        }

        public boolean isValid() {
            return isTypeValid(this.parent);
        }

        public boolean isValidForRecipe() {
            return isTypeValidForRecipe(this.parent);
        }

        private static boolean isTypeValid(String type) {
            return (type != null) && (type.equals("gun") || type.equals("attachment") || type.equals("ammo") || type.equals("block"));
        }

        private static boolean isTypeValidForRecipe(String type) {
            return (type != null) && (type.equals("gun") || type.equals("attachment") || type.equals("ammo"));
        }
    }
}
