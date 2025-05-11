package com.tacz.guns.compat.kubejs.recipe;

import com.mojang.serialization.Codec;
import com.tacz.guns.crafting.result.GunSmithTableResult;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.rhino.type.TypeInfo;

public class GunSmithTableResultComponent implements RecipeComponent<GunSmithTableResult> {
    public static final GunSmithTableResultComponent COMPONENT = new GunSmithTableResultComponent();

    @Override
    public Codec<GunSmithTableResult> codec() {
        return GunSmithTableResult.CODEC;
    }

    @Override
    public TypeInfo typeInfo() {
        return TypeInfo.of(GunSmithTableResult.class);
    }
}
