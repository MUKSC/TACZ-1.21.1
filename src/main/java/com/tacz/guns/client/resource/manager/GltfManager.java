package com.tacz.guns.client.resource.manager;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.tacz.guns.GunMod;
import com.tacz.guns.api.client.animation.gltf.AnimationStructure;
import com.tacz.guns.client.resource.ClientAssetsManager;
import com.tacz.guns.client.resource.pojo.animation.gltf.RawAnimationStructure;
import com.tacz.guns.resource.manager.LazyJsonDataManager;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;


public class GltfManager extends LazyJsonDataManager<AnimationStructure> {
    private static final FileToIdConverter FILE_TO_ID_CONVERTER = new FileToIdConverter("animations", ".gltf");

    public GltfManager() {
        super(AnimationStructure.class, ClientAssetsManager.GSON, FILE_TO_ID_CONVERTER, "GltfAnimationLoader",
                id -> GunMod.MOD_ID.equals(id.getNamespace()));
    }

    @Override
    protected @Nullable AnimationStructure parseJson(JsonElement element) throws JsonParseException {
        RawAnimationStructure rawStructure = getGson().fromJson(element, RawAnimationStructure.class);
        return rawStructure == null ? null : new AnimationStructure(rawStructure);
    }

    public AnimationStructure getGltfAnimation(ResourceLocation id) {
        return getData(id);
    }
}
