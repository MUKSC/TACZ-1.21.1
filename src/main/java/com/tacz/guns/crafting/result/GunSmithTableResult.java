package com.tacz.guns.crafting.result;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tacz.guns.resource.pojo.data.recipe.GunResult;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GunSmithTableResult {
    public static final Codec<GunSmithTableResult> CODEC = Codec.of(GunSmithTableResult::encode, GunSmithTableResult::decode);
    public static final StreamCodec<RegistryFriendlyByteBuf, GunSmithTableResult> STREAM_CODEC = StreamCodec.composite(
        ItemStack.OPTIONAL_STREAM_CODEC, GunSmithTableResult::getResult,
        ByteBufCodecs.STRING_UTF8, GunSmithTableResult::getGroup,
        GunSmithTableResult::new
    );

    public static <T> DataResult<T> encode(GunSmithTableResult input, DynamicOps<T> ops, T prefix) {
        throw new UnsupportedOperationException();
    }

    public static <T> DataResult<Pair<GunSmithTableResult, T>> decode(DynamicOps<T> ops, T input) {
        return ops.getMap(input).map(map -> {
            String typeName = Codec.STRING.fieldOf("type").decode(ops, map).getOrThrow();
            int count = Codec.INT.optionalFieldOf("count", 1).decode(ops, map).getOrThrow();
            CompoundTag extraTag = CompoundTag.CODEC.optionalFieldOf("nbt", null).decode(ops, map).getOrThrow();

            GunSmithTableResult result;
            switch (typeName) {
                case GunSmithTableResult.GUN,
                     GunSmithTableResult.AMMO,
                     GunSmithTableResult.ATTACHMENT -> {
                    ResourceLocation id = ResourceLocation.CODEC.fieldOf("id").decode(ops, map).getOrThrow();
                    RawGunTableResult raw = new RawGunTableResult(typeName, id, count);
                    if (extraTag != null) raw.setNbt(extraTag);
                    if (typeName.equals(GunSmithTableResult.GUN)) {
                        GunResult.CODEC.parse(ops, input).result().ifPresent(raw::setExtraData);
                    }
                    result = new GunSmithTableResult(raw);
                }
                case GunSmithTableResult.CUSTOM -> {
                    ItemStack itemStack = ItemStack.OPTIONAL_CODEC.fieldOf("item").decode(ops, map).getOrThrow();
                    String group = Codec.STRING.optionalFieldOf("group", StringUtils.EMPTY).decode(ops, map).getOrThrow();
                    result = new GunSmithTableResult(itemStack, group);
                    if (extraTag != null) {
                        result.getResult().update(DataComponents.CUSTOM_DATA, CustomData.EMPTY, data -> data.update(tag -> {
                            for (String key : extraTag.getAllKeys()) {
                                Tag value = extraTag.get(key);
                                if (value != null) {
                                    tag.put(key, value);
                                }
                            }
                        }));
                    }
                }
                default -> result = new GunSmithTableResult(ItemStack.EMPTY, StringUtils.EMPTY);
            }
            return Pair.of(result, input);
        });
    }

    public static final String GUN = "gun";
    public static final String AMMO = "ammo";
    public static final String ATTACHMENT = "attachment";
    public static final String CUSTOM = "custom";

    private ItemStack result = ItemStack.EMPTY;
    private String group = "";

    @Nullable
    private RawGunTableResult raw = null;

    public GunSmithTableResult(@NotNull RawGunTableResult raw) {
        this.raw = raw;
    }

    public void init(HolderLookup.Provider provider) {
        if (raw != null) {
            GunSmithTableResult result = RawGunTableResult.init(provider, raw);
            this.result = result.getResult();
            this.group = result.getGroup();
            this.raw = null;
        }
    }

    public GunSmithTableResult(ItemStack result, String group) {
        this.result = result;
        this.group = group;
    }

    public ItemStack getResult() {
        return result;
    }

    public String getGroup() {
        return group;
    }
}
