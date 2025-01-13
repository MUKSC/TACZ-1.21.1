package com.tacz.guns.crafting;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;

/**
 * 此类为数据包侧载枪械工作台的实现<br>
 * 枪包的序列化不在此处
 */
public class GunSmithTableSerializer implements RecipeSerializer<GunSmithTableRecipe> {
    @Override
    public MapCodec<GunSmithTableRecipe> codec() {
        return GunSmithTableRecipe.CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, GunSmithTableRecipe> streamCodec() {
        return GunSmithTableRecipe.STREAM_CODEC;
    }
}
