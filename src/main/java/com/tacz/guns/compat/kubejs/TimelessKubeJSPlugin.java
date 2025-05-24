package com.tacz.guns.compat.kubejs;

import com.tacz.guns.GunMod;
import com.tacz.guns.api.GunProperties;
import com.tacz.guns.api.item.gun.AbstractGunItem;
import com.tacz.guns.api.item.gun.GunItemManager;
import com.tacz.guns.compat.kubejs.custom.CustomGunItemBuilder;
import com.tacz.guns.compat.kubejs.events.GunKubeJSEvents;
import com.tacz.guns.compat.kubejs.events.TimelessClientEvents;
import com.tacz.guns.compat.kubejs.events.TimelessCommonEvents;
import com.tacz.guns.compat.kubejs.events.TimelessServerEvents;
import com.tacz.guns.compat.kubejs.recipe.GunSmithTableResultComponents;
import com.tacz.guns.compat.kubejs.recipe.TimelessGunSmithTableRecipeSchema;
import com.tacz.guns.compat.kubejs.util.GunSmithTableResultInfo;
import com.tacz.guns.compat.kubejs.util.TimelessItemWrapper;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.recipe.schema.RecipeComponentFactoryRegistryEvent;
import dev.latvian.mods.kubejs.recipe.schema.RegisterRecipeSchemasEvent;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.rhino.util.wrap.TypeWrappers;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;

public class TimelessKubeJSPlugin extends KubeJSPlugin {
    public static final String KUBEJS_MODID = "kubejs";
    private static final Map<String, RegistryObject<? extends AbstractGunItem>> GUNTYPE_REGISTER_MAP = new HashMap<>();

    @Override
    public void init() {
        RegistryInfo.ITEM.addType("tacz_gun", CustomGunItemBuilder.class, CustomGunItemBuilder::new);
    }

    @Override
    public void registerEvents() {
        //提早加载防止出现问题
        TimelessCommonEvents.INSTANCE.init();
        TimelessServerEvents.INSTANCE.init();
        TimelessClientEvents.INSTANCE.init();
        GunKubeJSEvents.GROUP.register();
    }

    @Override
    public void registerBindings(BindingsEvent event) {
        event.add("TimelessItem", TimelessItemWrapper.class);
        event.add("GunProperties", GunProperties.class);
        event.add("GunSmithTableResultInfo", GunSmithTableResultInfo.class);
    }

    @Override
    public void registerTypeWrappers(ScriptType type, TypeWrappers typeWrappers) {
        typeWrappers.registerSimple(GunSmithTableResultInfo.class, GunSmithTableResultInfo::of);
    }

    @Override
    public void registerRecipeSchemas(RegisterRecipeSchemasEvent event) {
        event.namespace(GunMod.MOD_ID).register("gun_smith_table_crafting", TimelessGunSmithTableRecipeSchema.SCHEMA);
    }

    @Override
    public void registerRecipeComponents(RecipeComponentFactoryRegistryEvent event) {
        event.register("gunSmithTableResultInfo", GunSmithTableResultComponents.RESULT_INFO);
    }

    public static void registerGunType(String typeName, RegistryObject<? extends AbstractGunItem> registryObject) {
        GUNTYPE_REGISTER_MAP.put(typeName, registryObject);
    }

    @SubscribeEvent
    public void onItemRegister(RegisterEvent event) {
        if (ModList.get().isLoaded(KUBEJS_MODID) && event.getRegistryKey().equals(ForgeRegistries.ITEMS.getRegistryKey())) {
            for (Map.Entry<String, RegistryObject<? extends AbstractGunItem>> entry : GUNTYPE_REGISTER_MAP.entrySet()) {
                GunItemManager.registerGunItem(entry.getKey(), entry.getValue());
            }
        }
    }
}
