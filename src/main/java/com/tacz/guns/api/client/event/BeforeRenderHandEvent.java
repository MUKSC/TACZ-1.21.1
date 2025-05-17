package com.tacz.guns.api.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tacz.guns.api.event.common.KubeJSGunEventPoster;
import net.neoforged.bus.api.Event;

/**
 * 在调用 ItemInHandRenderer#renderHandsWithItems 方法时触发该事件
 * 用于相机动画相关调用
 */
public class BeforeRenderHandEvent extends Event implements KubeJSGunEventPoster<BeforeRenderHandEvent> {
    private final PoseStack poseStack;

    public BeforeRenderHandEvent(PoseStack poseStack) {
        this.poseStack = poseStack;
        postClientEventToKubeJS(this);
    }

    public PoseStack getPoseStack() {
        return poseStack;
    }
}
