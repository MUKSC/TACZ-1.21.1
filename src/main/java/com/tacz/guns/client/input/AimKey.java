package com.tacz.guns.client.input;

import com.mojang.blaze3d.platform.InputConstants;
import com.tacz.guns.api.client.gameplay.IClientPlayerGunOperator;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.config.client.KeyConfig;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyModifier;
import org.lwjgl.glfw.GLFW;

import static com.tacz.guns.util.InputExtraCheck.isInGame;

@EventBusSubscriber(value = Dist.CLIENT)
public class AimKey {
    public static final KeyMapping AIM_KEY = new KeyMapping("key.tacz.aim.desc",
            KeyConflictContext.IN_GAME,
            KeyModifier.NONE,
            InputConstants.Type.MOUSE,
            GLFW.GLFW_MOUSE_BUTTON_RIGHT,
            "key.category.tacz");

    @SubscribeEvent
    public static void onAimPress(InputEvent.MouseButton.Post event) {
        if (isInGame() && AIM_KEY.matchesMouse(event.getButton())) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null || player.isSpectator()) {
                return;
            }
            if (!(player instanceof IClientPlayerGunOperator operator)) {
                return;
            }
            if (IGun.mainHandHoldGun(player)) {
                boolean action = true;
                if (!KeyConfig.HOLD_TO_AIM.get()) {
                    action = !operator.isAim();
                }
                if (event.getAction() == GLFW.GLFW_PRESS) {
                    IClientPlayerGunOperator.fromLocalPlayer(player).aim(action);
                }
                if (KeyConfig.HOLD_TO_AIM.get() && event.getAction() == GLFW.GLFW_RELEASE) {
                    IClientPlayerGunOperator.fromLocalPlayer(player).aim(false);
                }
            }
        }
    }

    public static boolean onAimControllerPress(boolean isPress) {
        if (!isInGame()) {
            return false;
        }
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || player.isSpectator()) {
            return false;
        }
        if (!(player instanceof IClientPlayerGunOperator operator)) {
            return false;
        }
        if (!IGun.mainHandHoldGun(player)) {
            return false;
        }
        boolean action = true;
        if (!KeyConfig.HOLD_TO_AIM.get()) {
            action = !operator.isAim();
        }
        if (isPress) {
            IClientPlayerGunOperator.fromLocalPlayer(player).aim(action);
            return true;
        }
        if (KeyConfig.HOLD_TO_AIM.get()) {
            IClientPlayerGunOperator.fromLocalPlayer(player).aim(false);
            return true;
        }
        return false;
    }

    @SubscribeEvent
    public static void cancelAim(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (!(player instanceof IClientPlayerGunOperator operator)) {
            return;
        }
        if (operator.isAim() && (!isInGame() || player.isSpectator())) {
            IClientPlayerGunOperator.fromLocalPlayer(player).aim(false);
        }
    }
}
