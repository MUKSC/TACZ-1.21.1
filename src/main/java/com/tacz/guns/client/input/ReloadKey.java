package com.tacz.guns.client.input;

import com.mojang.blaze3d.platform.InputConstants;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.client.gameplay.IClientPlayerGunOperator;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.config.client.KeyConfig;
import com.tacz.guns.resource.pojo.data.gun.Bolt;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyModifier;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.lwjgl.glfw.GLFW;

import static com.tacz.guns.util.InputExtraCheck.isInGame;

@EventBusSubscriber(value = Dist.CLIENT)
public class ReloadKey {
    public static final KeyMapping RELOAD_KEY = new KeyMapping("key.tacz.reload.desc",
            KeyConflictContext.IN_GAME,
            KeyModifier.NONE,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            "key.category.tacz");

    @SubscribeEvent
    public static void onReloadPress(InputEvent.Key event) {
        if (isInGame() && event.getAction() == GLFW.GLFW_PRESS && RELOAD_KEY.matches(event.getKey(), event.getScanCode())) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null || player.isSpectator()) {
                return;
            }
            if (player.getMainHandItem().getItem() instanceof IGun iGun) {
                // 如果使用背包直读，且没有换弹冷却机制，则在输入时就屏蔽换弹
                if (iGun.useInventoryAmmo(player.getMainHandItem())) {
                    return;
                }
                IClientPlayerGunOperator.fromLocalPlayer(player).reload();
            }
        }
    }

    public static boolean onReloadControllerPress(boolean isPress) {
        if (isInGame() && isPress) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null || player.isSpectator()) {
                return false;
            }
            if (IGun.mainHandHoldGun(player)) {
                IClientPlayerGunOperator.fromLocalPlayer(player).reload();
                return true;
            }
        }
        return false;
    }

    @SubscribeEvent
    public static void autoReload(PlayerTickEvent.Pre event) {
        if (!(event.getEntity() instanceof LocalPlayer)) {
            return;
        }

        if (!KeyConfig.AUTO_RELOAD.get()) {
            return;
        }

        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || player.isSpectator() || player.tickCount % 5 != 0) {
            return;
        }
        ItemStack currentGunItem = player.getMainHandItem();
        if (player.getMainHandItem().getItem() instanceof IGun iGun) {
            // 如果使用背包直读，且没有换弹冷却机制，则在输入时就屏蔽换弹
            if (iGun.useInventoryAmmo(player.getMainHandItem())) {
                return;
            }
            boolean flag = TimelessAPI.getCommonGunIndex(iGun.getGunId(currentGunItem))
                    .map(gunIndex -> gunIndex.getGunData().getBolt() != Bolt.OPEN_BOLT)
                    .orElse(false);

            int ammoCount = iGun.getCurrentAmmoCount(currentGunItem) + (iGun.hasBulletInBarrel(currentGunItem) && flag ? 1 : 0);
            if (ammoCount > 0) {
                return;
            }
            IClientPlayerGunOperator.fromLocalPlayer(player).reload();
        }
    }
}
