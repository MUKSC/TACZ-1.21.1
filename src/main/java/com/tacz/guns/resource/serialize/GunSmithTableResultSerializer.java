package com.tacz.guns.resource.serialize;

import com.google.gson.*;
import com.mojang.serialization.JsonOps;
import com.tacz.guns.crafting.result.GunSmithTableResult;
import com.tacz.guns.crafting.result.RawGunTableResult;
import com.tacz.guns.resource.CommonAssetsManager;
import com.tacz.guns.resource.pojo.data.recipe.GunResult;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;


public class GunSmithTableResultSerializer implements JsonDeserializer<GunSmithTableResult> {

    @Override
    public GunSmithTableResult deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonObject()) {
            JsonObject jsonObject = json.getAsJsonObject();
            String typeName = GsonHelper.getAsString(jsonObject, "type");
            int count = 1;
            CompoundTag extraTag = null;
            if (jsonObject.has("count")) {
                count = Math.max(GsonHelper.getAsInt(jsonObject, "count"), 1);
            }
            if (jsonObject.has("nbt")) {
                extraTag = CompoundTag.CODEC.parse(JsonOps.INSTANCE, jsonObject.get("nbt")).getOrThrow();
            }

            GunSmithTableResult result;
            switch (typeName) {
                case GunSmithTableResult.GUN,GunSmithTableResult.AMMO,GunSmithTableResult.ATTACHMENT -> {
                    RawGunTableResult raw = new RawGunTableResult(typeName, getId(jsonObject), count);
                    if (extraTag != null) {
                        raw.setNbt(extraTag);
                    }
                    if (typeName.equals(GunSmithTableResult.GUN)) {
                        GunResult gunResult = CommonAssetsManager.GSON.fromJson(jsonObject, GunResult.class);
                        if (gunResult != null) {
                            raw.setExtraData(gunResult);
                        }
                    }
                    result = new GunSmithTableResult(raw);
                }
                case GunSmithTableResult.CUSTOM -> {
                    JsonObject resultObject = GsonHelper.getAsJsonObject(jsonObject, "item");
                    String group = GsonHelper.getAsString(jsonObject, "group", StringUtils.EMPTY);
                    ItemStack itemStack = ItemStack.CODEC.parse(JsonOps.INSTANCE, resultObject).getOrThrow();
                    result = new GunSmithTableResult(itemStack, group);
                    if (extraTag != null) {
                        final CompoundTag finalExtraTag = extraTag;
                        result.getResult().update(DataComponents.CUSTOM_DATA, CustomData.EMPTY, data -> data.update(tag -> {
                            for (String key : finalExtraTag.getAllKeys()) {
                                Tag value = finalExtraTag.get(key);
                                if (value != null) {
                                    tag.put(key, value);
                                }
                            }
                        }));
                    }
                }
                default -> {
                    return new GunSmithTableResult(ItemStack.EMPTY, StringUtils.EMPTY);
                }
            }
            return result;
        }
        return new GunSmithTableResult(ItemStack.EMPTY, StringUtils.EMPTY);
    }

    private ResourceLocation getId(JsonObject jsonObject) {
        return ResourceLocation.parse(GsonHelper.getAsString(jsonObject, "id"));
    }
}
