package com.tacz.guns.client.event;

import com.tacz.guns.client.gui.GunRefitScreen;
import com.tacz.guns.client.gui.GunSmithTableScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class PreventsHotbarEvent {
    public static void onRenderHotbarEvent(CallbackInfo ci) {
        // todo 需要测试行为
        Screen screen = Minecraft.getInstance().screen;
        // 枪械合成台界面关闭背景
        if (screen instanceof GunSmithTableScreen) {
            ci.cancel();
            return;
        }
        // 枪械改装界面关闭背景
        if (screen instanceof GunRefitScreen) {
            ci.cancel();
        }
    }
}
