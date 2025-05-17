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
import com.tacz.guns.compat.kubejs.recipe.TimelessGunSmithTableRecipeSchema;
import com.tacz.guns.compat.kubejs.util.TimelessItemWrapper;
import dev.latvian.mods.kubejs.event.EventGroupRegistry;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchemaRegistry;
import dev.latvian.mods.kubejs.registry.BuilderTypeRegistry;
import dev.latvian.mods.kubejs.script.BindingRegistry;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.HashMap;
import java.util.Map;

public class TimelessKubeJSPlugin implements KubeJSPlugin {
    public static final String KUBEJS_MODID = "kubejs";
    private static final Map<String, DeferredItem<? extends AbstractGunItem>> GUNTYPE_REGISTER_MAP = new HashMap<>();

    @Override
    public void registerBuilderTypes(BuilderTypeRegistry registry) {
        registry.addDefault(Registries.ITEM, CustomGunItemBuilder.class, CustomGunItemBuilder::new);
    }

    @Override
    public void registerEvents(EventGroupRegistry registry) {
        //提早加载防止出现问题
        TimelessCommonEvents.INSTANCE.init();
        TimelessServerEvents.INSTANCE.init();
        TimelessClientEvents.INSTANCE.init();
        registry.register(GunKubeJSEvents.GROUP);
    }

    @Override
    public void registerBindings(BindingRegistry registry) {
        registry.add("TimelessItem", TimelessItemWrapper.class);
        registry.add("GunProperties", GunProperties.class);
    }

    @Override
    public void registerRecipeSchemas(RecipeSchemaRegistry registry) {
        registry.namespace(GunMod.MOD_ID).register("gun_smith_table_crafting", TimelessGunSmithTableRecipeSchema.SCHEMA);
    }

    public static void registerGunType(String typeName, DeferredItem<? extends AbstractGunItem> registryObject) {
        GUNTYPE_REGISTER_MAP.put(typeName, registryObject);
    }

    @SubscribeEvent
    public void onItemRegister(RegisterEvent event) {
        if (ModList.get().isLoaded(KUBEJS_MODID) && event.getRegistryKey().equals(Registries.ITEM)) {
            for (Map.Entry<String, DeferredItem<? extends AbstractGunItem>> entry : GUNTYPE_REGISTER_MAP.entrySet()) {
                GunItemManager.registerGunItem(entry.getKey(), entry.getValue());
            }
        }
    }
}
