package com.tacz.guns.compat.kubejs.recipe;

import com.mojang.serialization.Codec;
import com.tacz.guns.crafting.GunSmithTableIngredient;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.rhino.type.TypeInfo;

public class GunSmithTableIngredientComponent implements RecipeComponent<GunSmithTableIngredient> {
    public static final GunSmithTableIngredientComponent COMPONENT = new GunSmithTableIngredientComponent();

    @Override
    public Codec<GunSmithTableIngredient> codec() {
        return GunSmithTableIngredient.CODEC;
    }

    @Override
    public TypeInfo typeInfo() {
        return TypeInfo.of(GunSmithTableIngredient.class);
    }
}
