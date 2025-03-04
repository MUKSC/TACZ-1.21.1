package com.tacz.guns.resource.pojo.data.block;

import com.google.gson.*;
import com.tacz.guns.GunMod;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.crafting.CraftingHelper;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

public record TabConfig(ResourceLocation id, String name, ItemStack icon) {
    public static final ResourceLocation TAB_AMMO = new ResourceLocation(GunMod.MOD_ID, "ammo");

    public static final ResourceLocation TAB_PISTOL = new ResourceLocation(GunMod.MOD_ID, "pistol");
    public static final ResourceLocation TAB_SNIPER = new ResourceLocation(GunMod.MOD_ID, "sniper");
    public static final ResourceLocation TAB_RIFLE = new ResourceLocation(GunMod.MOD_ID, "rifle");
    public static final ResourceLocation TAB_SHOTGUN = new ResourceLocation(GunMod.MOD_ID, "shotgun");
    public static final ResourceLocation TAB_SMG = new ResourceLocation(GunMod.MOD_ID, "smg");
    public static final ResourceLocation TAB_RPG = new ResourceLocation(GunMod.MOD_ID, "rpg");
    public static final ResourceLocation TAB_MG = new ResourceLocation(GunMod.MOD_ID, "mg");

    public static final ResourceLocation TAB_SCOPE = new ResourceLocation(GunMod.MOD_ID, "scope");
    public static final ResourceLocation TAB_MUZZLE = new ResourceLocation(GunMod.MOD_ID, "muzzle");
    public static final ResourceLocation TAB_STOCK = new ResourceLocation(GunMod.MOD_ID, "stock");
    public static final ResourceLocation TAB_GRIP = new ResourceLocation(GunMod.MOD_ID, "grip");
    public static final ResourceLocation TAB_EXTENDED_MAG = new ResourceLocation(GunMod.MOD_ID, "extended_mag");
    public static final ResourceLocation TAB_LASER = new ResourceLocation(GunMod.MOD_ID, "laser");

    public static final ResourceLocation TAB_MISC = new ResourceLocation(GunMod.MOD_ID, "misc");
    public static final ResourceLocation TAB_EMPTY = new ResourceLocation(GunMod.MOD_ID, "empty");

    public static class Deserializer implements JsonDeserializer<TabConfig> {
        @Override
        public TabConfig deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (!json.isJsonObject()) {
                throw new JsonParseException("TabConfig must be a JSON object");
            }
            JsonObject object = json.getAsJsonObject();
            if (!object.has("id") || !object.get("id").isJsonPrimitive()) {
                throw new JsonParseException("TabConfig must have an id");
            }
            ResourceLocation id = context.deserialize(object.get("id"), ResourceLocation.class);
            ItemStack icon = CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(object, "icon"), true);
            String name = GsonHelper.getAsString(object, "name", "tacz.type.unknown.name");
            return new TabConfig(id, name, icon);
        }
    }

    @NotNull
    public Component getName() {
        return Component.translatable(name==null ? "tacz.type.unknown.name" : name);
    }
}
