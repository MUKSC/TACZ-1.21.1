package com.tacz.guns.mixin.client;

import com.tacz.guns.client.event.PreventsHotbarEvent;
import com.tacz.guns.client.event.RenderCrosshairEvent;
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

    @Inject(method = "renderHotbarAndDecorations", at = @At("HEAD"), cancellable = true)
    private void renderHotbarAndDecorations(GuiGraphics graphics, DeltaTracker delta, CallbackInfo ci) {
        PreventsHotbarEvent.onRenderHotbarEvent(ci);
    }

    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    private void renderCrosshair(GuiGraphics p_282828_, DeltaTracker p_343490_, CallbackInfo ci) {
        RenderCrosshairEvent.onRenderCrosshair(p_282828_, minecraft.getWindow(), p_343490_, ci);
    }
}
