package com.tacz.guns.client.init;

import com.tacz.guns.client.gui.GunSmithTableScreen;
import com.tacz.guns.inventory.GunSmithTableMenu;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ModContainerScreen {
    @SubscribeEvent
    public static void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(GunSmithTableMenu.TYPE, new MenuScreens.ScreenConstructor<GunSmithTableMenu, GunSmithTableScreen>() {
            @Override
            public @NotNull GunSmithTableScreen create(@NotNull GunSmithTableMenu menu, @NotNull Inventory inventory, @NotNull Component title) {
                return new GunSmithTableScreen(menu, inventory, title);
            }
        });
    }
}
