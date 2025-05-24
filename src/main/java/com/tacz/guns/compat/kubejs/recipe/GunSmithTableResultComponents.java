package com.tacz.guns.compat.kubejs.recipe;

import com.google.gson.JsonElement;
import com.tacz.guns.compat.kubejs.util.GunSmithTableResultInfo;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.component.ComponentRole;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;

public class GunSmithTableResultComponents {
    public static final RecipeComponent<GunSmithTableResultInfo> RESULT_INFO = new RecipeComponent<>() {
        @Override
        public String componentType() {
            return "gun_smith_table_result_info";
        }

        @Override
        public Class<?> componentClass() {
            return GunSmithTableResultInfo.class;
        }

        @Override
        public ComponentRole role() {
            return ComponentRole.OUTPUT;
        }

        @Override
        public JsonElement write(RecipeJS recipe, GunSmithTableResultInfo value) {
            return value.toJson();
        }

        @Override
        public GunSmithTableResultInfo read(RecipeJS recipe, Object from) {
            GunSmithTableResultInfo info = GunSmithTableResultInfo.of(from);
            if (recipe instanceof TimelessRecipeJS tRecipe) {
                tRecipe.setResultInfo(info);
            }
            return info;
        }
    };
}
