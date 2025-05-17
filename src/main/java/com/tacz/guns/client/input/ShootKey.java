package com.tacz.guns.client.input;

import com.mojang.blaze3d.platform.InputConstants;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.client.gameplay.IClientPlayerGunOperator;
import com.tacz.guns.api.entity.ShootResult;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.gun.FireMode;
import com.tacz.guns.client.gameplay.LocalPlayerSprint;
import com.tacz.guns.client.sound.SoundPlayManager;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
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
public class ShootKey {
    public static final KeyMapping SHOOT_KEY = new KeyMapping("key.tacz.shoot.desc",
            KeyConflictContext.IN_GAME,
            KeyModifier.NONE,
            InputConstants.Type.MOUSE,
            GLFW.GLFW_MOUSE_BUTTON_LEFT,
            "key.category.tacz");
    private static boolean lastTimeShootSuccess = false;

    @SubscribeEvent
    public static void autoShoot(ClientTickEvent.Post event) {
        if (!isInGame()) {
            return;
        }
        LocalPlayerSprint.stopSprint = false;

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || player.isSpectator()) {
            return;
        }
        ItemStack mainHandItem = player.getMainHandItem();
        if (mainHandItem.getItem() instanceof IGun iGun) {
            FireMode fireMode = iGun.getFireMode(mainHandItem);
            boolean isBurstAuto = fireMode == FireMode.BURST && TimelessAPI.getCommonGunIndex(iGun.getGunId(mainHandItem))
                    .map(index -> index.getGunData().getBurstData().isContinuousShoot())
                    .orElse(false);
            IClientPlayerGunOperator operator = IClientPlayerGunOperator.fromLocalPlayer(player);
            if (SHOOT_KEY.isDown()) {
                // 能开火时禁止冲刺
                LocalPlayerSprint.stopSprint = true;

                if (fireMode != FireMode.AUTO && !isBurstAuto && lastTimeShootSuccess) {
                    // 非全自动情况，禁止连续开火
                    return;
                }
                if (operator.shoot() == ShootResult.SUCCESS) {
                    lastTimeShootSuccess = true;
                }
            } else {
                lastTimeShootSuccess = false;
            }
        }
    }

    public static boolean autoShootController() {
        if (!isInGame()) {
            return false;
        }
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || player.isSpectator()) {
            return false;
        }
        ItemStack mainHandItem = player.getMainHandItem();
        if (mainHandItem.getItem() instanceof IGun iGun) {
            FireMode fireMode = iGun.getFireMode(mainHandItem);
            boolean isBurstAuto = fireMode == FireMode.BURST && TimelessAPI.getCommonGunIndex(iGun.getGunId(mainHandItem))
                    .map(index -> index.getGunData().getBurstData().isContinuousShoot())
                    .orElse(false);
            IClientPlayerGunOperator operator = IClientPlayerGunOperator.fromLocalPlayer(player);
            if (fireMode == FireMode.AUTO || isBurstAuto) {
                return operator.shoot() == ShootResult.SUCCESS;
            }
        }
        return false;
    }

    @SubscribeEvent
    public static void semiShoot(InputEvent.MouseButton.Post event) {
        if (isInGame() && SHOOT_KEY.matchesMouse(event.getButton())) {
            // 松开鼠标，重置 DryFire 状态
            if (event.getAction() == GLFW.GLFW_RELEASE) {
                SoundPlayManager.resetDryFireSound();
                return;
            }
            Minecraft mc = Minecraft.getInstance();
            LocalPlayer player = mc.player;
            if (player == null || player.isSpectator()) {
                return;
            }
            ItemStack mainHandItem = player.getMainHandItem();
            if (mainHandItem.getItem() instanceof IGun iGun) {
                FireMode fireMode = iGun.getFireMode(mainHandItem);
                boolean isBurstSemi = fireMode == FireMode.BURST && TimelessAPI.getCommonGunIndex(iGun.getGunId(mainHandItem))
                        .map(index -> !index.getGunData().getBurstData().isContinuousShoot())
                        .orElse(false);
                if (fireMode == FireMode.UNKNOWN) {
                    player.sendSystemMessage(Component.translatable("message.tacz.fire_select.fail"));
                }
                if (fireMode == FireMode.SEMI || isBurstSemi) {
                    lastTimeShootSuccess = IClientPlayerGunOperator.fromLocalPlayer(player).shoot() == ShootResult.SUCCESS;
                }
            }
        }
    }

    public static boolean semiShootController(boolean isPress) {
        if (!isInGame()) {
            return false;
        }
        // 松开鼠标，重置 DryFire 状态
        if (!isPress) {
            SoundPlayManager.resetDryFireSound();
            return false;
        }
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || player.isSpectator()) {
            return false;
        }
        ItemStack mainHandItem = player.getMainHandItem();
        if (mainHandItem.getItem() instanceof IGun iGun) {
            FireMode fireMode = iGun.getFireMode(mainHandItem);
            boolean isBurstSemi = fireMode == FireMode.BURST && TimelessAPI.getCommonGunIndex(iGun.getGunId(mainHandItem))
                    .map(index -> !index.getGunData().getBurstData().isContinuousShoot())
                    .orElse(false);
            if (fireMode == FireMode.UNKNOWN) {
                player.sendSystemMessage(Component.translatable("message.tacz.fire_select.fail"));
                return false;
            }
            if (fireMode == FireMode.SEMI || isBurstSemi) {
                return IClientPlayerGunOperator.fromLocalPlayer(player).shoot() == ShootResult.SUCCESS;
            }
        }
        return false;
    }
}
