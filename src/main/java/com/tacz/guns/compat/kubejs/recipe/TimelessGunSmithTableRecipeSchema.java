package com.tacz.guns.compat.kubejs.recipe;

import com.tacz.guns.crafting.GunSmithTableIngredient;
import com.tacz.guns.crafting.result.GunSmithTableResult;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.ComponentRole;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;

import java.util.List;

public interface TimelessGunSmithTableRecipeSchema {
    RecipeKey<List<GunSmithTableIngredient>> MATERIALS = GunSmithTableIngredientComponent.COMPONENT
        .asList().key("materials", ComponentRole.INPUT);

    RecipeKey<GunSmithTableResult> RESULT = GunSmithTableResultComponent.COMPONENT
        .key("result", ComponentRole.OUTPUT);

    RecipeSchema SCHEMA = new RecipeSchema(RESULT, MATERIALS);
}