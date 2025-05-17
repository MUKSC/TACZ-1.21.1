package com.tacz.guns.init;

import com.tacz.guns.GunMod;
import com.tacz.guns.api.item.gun.GunItemManager;
import com.tacz.guns.item.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegisterEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(GunMod.MOD_ID);

    public static DeferredItem<ModernKineticGunItem> MODERN_KINETIC_GUN = ITEMS.register("modern_kinetic_gun", ModernKineticGunItem::new);

//    public static RegistryObject<ThrowableItem> M67 = ITEMS.register("m67", ThrowableItem::new);

    public static DeferredItem<Item> AMMO = ITEMS.register("ammo", AmmoItem::new);
    public static DeferredItem<AttachmentItem> ATTACHMENT = ITEMS.register("attachment", AttachmentItem::new);

    public static DeferredItem<GunSmithTableItem> GUN_SMITH_TABLE = ITEMS.register("gun_smith_table", () -> new DefaultTableItem(ModBlocks.GUN_SMITH_TABLE.get()));
    public static DeferredItem<GunSmithTableItem> WORKBENCH_111 = ITEMS.register("workbench_a", () -> new GunSmithTableItem(ModBlocks.WORKBENCH_111.get()));
    public static DeferredItem<GunSmithTableItem> WORKBENCH_211 = ITEMS.register("workbench_b", () -> new GunSmithTableItem(ModBlocks.WORKBENCH_211.get()));
    public static DeferredItem<GunSmithTableItem> WORKBENCH_121 = ITEMS.register("workbench_c", () -> new GunSmithTableItem(ModBlocks.WORKBENCH_121.get()));


    public static DeferredItem<Item> TARGET = ITEMS.register("target", () -> new BlockItem(ModBlocks.TARGET.get(), new Item.Properties()));
    public static DeferredItem<Item> STATUE = ITEMS.register("statue", () -> new BlockItem(ModBlocks.STATUE.get(), new Item.Properties()));
    public static DeferredItem<Item> AMMO_BOX = ITEMS.register("ammo_box", AmmoBoxItem::new);
    public static DeferredItem<Item> TARGET_MINECART = ITEMS.register("target_minecart", TargetMinecartItem::new);

    @SubscribeEvent
    public static void onItemRegister(RegisterEvent event) {
        if (event.getRegistryKey().equals(BuiltInRegistries.ITEM.key())) {
            GunItemManager.registerGunItem(ModernKineticGunItem.TYPE_NAME, MODERN_KINETIC_GUN);
        }
    }
}