package com.tacz.guns.client.gui.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tacz.guns.GunMod;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.client.gameplay.IClientPlayerGunOperator;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.client.resource.GunDisplayInstance;
import com.tacz.guns.client.resource.index.ClientGunIndex;
import com.tacz.guns.config.client.RenderConfig;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class HeatBarHudOverlay implements IGuiOverlay {
    private static final ResourceLocation HEAT_BASE = new ResourceLocation(GunMod.MOD_ID, "textures/gui/heat_base.png");
    private static final ResourceLocation HEAT_BAR = new ResourceLocation(GunMod.MOD_ID, "textures/gui/heat_bar.png");
    private static long overHeatAlertTimestamp = -1L;
    private static boolean overHeatAlert = false;
    private static int color = 0xFFFFFF;

    @Override
    public void render(ForgeGui gui, GuiGraphics graphics, float partialTick, int width, int height) {
        if (!RenderConfig.GUN_HUD_ENABLE.get()) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (!(player instanceof IClientPlayerGunOperator)) {
            return;
        }
        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof IGun iGun)) {
            return;
        }
        ResourceLocation gunId = iGun.getGunId(stack);

        GunData gunData = TimelessAPI.getClientGunIndex(gunId).map(ClientGunIndex::getGunData).orElse(null);
        GunDisplayInstance display = TimelessAPI.getGunDisplay(stack).orElse(null);
        if (gunData == null || display == null) {
            return;
        }

        // 如果不是过热类武器则取消
        if (!iGun.isUseHeat(stack, player)) {
            return;
        }

        float anchorPointX = width / 2f;
        float anchorPointY = height / 2f;
        int heatProgress = Math.min(99, iGun.getHeatCount(stack, player) * 100 / iGun.getUpperLimit(stack, player));
        float totalTranslateX = 0;
        float totalTranslateY = 16;
        String heatText = heatProgress + "%";

        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();
        {
            poseStack.translate(anchorPointX, anchorPointY, 0);
            float alpha = 0.7f;

            poseStack.pushPose();
            {
                poseStack.translate(totalTranslateX, totalTranslateY, 0);
                if (iGun.isOverHeat(stack, player)) {
                    handleOverHeatAlert(alpha);
                } else {
                    RenderSystem.setShaderColor(1, 1, 1, alpha);
                }
                graphics.blit(HEAT_BASE, -48, -48, 0, 0, 96, 96, 96, 96);
            }
            poseStack.popPose();

            poseStack.pushPose();
            {
                poseStack.scale(heatProgress / 100f, 1f, 1f);
                poseStack.translate(totalTranslateX, totalTranslateY, 0);
                if (iGun.isOverHeat(stack, player)) {
                    handleOverHeatAlert(alpha);
                } else {
                    if (heatProgress >= 60) {
                        RenderSystem.setShaderColor(1f, 1f, 0.3f, alpha);
                    }
                }
                graphics.blit(HEAT_BAR, -48, -48, 0, 0, 96, 96, 96, 96);
            }
            poseStack.popPose();

            poseStack.pushPose();
            {
                poseStack.translate(totalTranslateX, totalTranslateY, 0);
                RenderSystem.setShaderColor(0.4f, 0.4f, 0.4f, 0.4f * alpha);
                graphics.blit(HEAT_BAR, -48, -48, 0, 0, 96, 96, 96, 96);
            }
            poseStack.popPose();

            Font font = mc.font;
            poseStack.pushPose();
            {
                poseStack.translate(totalTranslateX, totalTranslateY, 0);
                poseStack.translate(-8.5, 14, 0);
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);
                if (iGun.isOverHeat(stack, player)) {
                    poseStack.translate(-15.5, 0, 0);
                    handleOverHeatAlert(alpha);
                    graphics.drawString(font, "OVERHEAT!", 0, 0, color, false);
                } else {
                    if (heatProgress < 10) {
                        poseStack.translate(3.5, 0, 0);
                    }
                    graphics.drawString(font, heatText, 0, 0, 0xFFFFFF, false);
                }
            }
            poseStack.popPose();
        }
        poseStack.popPose();
    }

    private static void handleOverHeatAlert(float alpha) {
        // 0.25 秒刷新一次
        if ((System.currentTimeMillis() - overHeatAlertTimestamp) > 250) {
            overHeatAlertTimestamp = System.currentTimeMillis();
            overHeatAlert = !overHeatAlert;
        }
        if (overHeatAlert) {
            color = 0xFF4C4C;
            RenderSystem.setShaderColor(1, 0.3f, 0.3f, alpha);
        } else {
            color = 0xFFFF4C;
            RenderSystem.setShaderColor(1, 1, 0.3f, alpha);
        }
    }
}
