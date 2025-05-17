package com.tacz.guns.mixin.client;

import com.tacz.guns.client.event.PreventsHotbarEvent;
import com.tacz.guns.client.event.RenderCrosshairEvent;
import com.tacz.guns.client.gui.overlay.GunHudOverlay;
import com.tacz.guns.client.gui.overlay.HeatBarOverlay;
import com.tacz.guns.client.gui.overlay.InteractKeyTextOverlay;
import com.tacz.guns.client.gui.overlay.KillAmountOverlay;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin {
    @Shadow @Final private Minecraft minecraft;

    @Shadow public abstract int getGuiTicks();

    @Inject(method = "renderHotbarAndDecorations", at = @At("HEAD"), cancellable = true)
    private void renderHotbarAndDecorations(GuiGraphics graphics, DeltaTracker delta, CallbackInfo ci) {
        PreventsHotbarEvent.onRenderHotbarEvent(ci);
    }

    @Inject(method = "renderItemHotbar", at = @At("HEAD"))
    private void renderItemHotbar(GuiGraphics graphics, DeltaTracker delta, CallbackInfo ci) {
        float partialTick = delta.getGameTimeDeltaPartialTick(false);
        GunHudOverlay.render(graphics, partialTick, graphics.guiWidth(), graphics.guiHeight());
        HeatBarOverlay.render(graphics, getGuiTicks(), partialTick, graphics.guiWidth(), graphics.guiHeight());
        KillAmountOverlay.render(graphics, partialTick, graphics.guiWidth(), graphics.guiHeight());
        InteractKeyTextOverlay.render(graphics, partialTick, graphics.guiWidth(), graphics.guiHeight());
    }

    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    private void renderCrosshair(GuiGraphics p_282828_, DeltaTracker p_343490_, CallbackInfo ci) {
        RenderCrosshairEvent.onRenderCrosshair(p_282828_, minecraft.getWindow(), p_343490_, ci);
    }
}
