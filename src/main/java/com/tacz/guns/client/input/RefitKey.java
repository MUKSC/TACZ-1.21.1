package com.tacz.guns.client.input;

import com.mojang.blaze3d.platform.InputConstants;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.client.gui.GunRefitScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyModifier;
import org.lwjgl.glfw.GLFW;

import static com.tacz.guns.util.InputExtraCheck.isInGame;

@EventBusSubscriber(value = Dist.CLIENT)
public class RefitKey {
    public static final KeyMapping REFIT_KEY = new KeyMapping("key.tacz.refit.desc",
            KeyConflictContext.IN_GAME,
            KeyModifier.NONE,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_Z,
            "key.category.tacz");

    @SubscribeEvent
    public static void onRefitPress(InputEvent.Key event) {
        if (event.getAction() == GLFW.GLFW_PRESS && REFIT_KEY.matches(event.getKey(), event.getScanCode())) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null || player.isSpectator()) {
                return;
            }
            if (isInGame()) {
                if (IGun.mainHandHoldGun(player) && Minecraft.getInstance().screen == null) {
                    IGun iGun = IGun.getIGunOrNull(player.getMainHandItem());
                    if (iGun != null && iGun.hasAttachmentLock(player.getMainHandItem())) {
                        return;
                    }
                    Minecraft.getInstance().setScreen(new GunRefitScreen());
                }
            } else if (Minecraft.getInstance().screen instanceof GunRefitScreen refitScreen) {
                refitScreen.onClose();
            }
        }
    }
}
