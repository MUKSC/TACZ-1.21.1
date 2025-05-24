package com.tacz.guns.compat.kubejs.recipe;

import com.tacz.guns.compat.kubejs.util.GunSmithTableResultInfo;
import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.ItemComponents;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;

public interface TimelessGunSmithTableRecipeSchema {
    RecipeKey<GunSmithTableResultInfo> RESULT = GunSmithTableResultComponents.RESULT_INFO.key("result");
    RecipeKey<InputItem[]> MATERIALS = ItemComponents.INPUT_ARRAY.key("materials");

    RecipeSchema SCHEMA = new RecipeSchema(TimelessRecipeJS.class, TimelessRecipeJS::new, RESULT, MATERIALS);
}