package com.tacz.guns.crafting.result;

import com.tacz.guns.resource.pojo.data.block.TabConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GunSmithTableResult {
    public static final String GUN = "gun";
    public static final String AMMO = "ammo";
    public static final String ATTACHMENT = "attachment";
    public static final String CUSTOM = "custom";

    private ItemStack result = ItemStack.EMPTY;
    private ResourceLocation group = null;

    @Nullable
    private RawGunTableResult raw = null;

    public GunSmithTableResult(ItemStack result, @Nullable ResourceLocation group) {
        this.result = result;
        this.group = group==null ? TabConfig.TAB_EMPTY : group;
    }


    public GunSmithTableResult(@NotNull RawGunTableResult raw) {
        this.raw = raw;
    }

    public GunSmithTableResult(@NotNull RawGunTableResult raw, @Nullable ResourceLocation group) {
        this.raw = raw;
        this.group = group==null ? TabConfig.TAB_EMPTY : group;
    }

    public void init() {
        if (raw != null) {
            GunSmithTableResult result = RawGunTableResult.init(raw);
            this.result = result.getResult();
            if (group == null || group.equals(TabConfig.TAB_EMPTY)) {
                this.group = result.getGroup();
            }
            this.raw = null;
        }
    }

    public ItemStack getResult() {
        return result;
    }

    public ResourceLocation getGroup() {
        return group;
    }
}
