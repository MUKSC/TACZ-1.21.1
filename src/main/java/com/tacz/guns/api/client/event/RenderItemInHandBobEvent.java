package com.tacz.guns.api.client.event;

import com.tacz.guns.api.event.common.KubeJSGunEventPoster;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

/**
 * 当第一人称视角触发摇晃时，玩家手部的摇晃
 */
public class RenderItemInHandBobEvent extends Event implements KubeJSGunEventPoster<RenderItemInHandBobEvent>, ICancellableEvent {
    public static class BobHurt extends RenderItemInHandBobEvent {
        public BobHurt() {
            postClientEventToKubeJS(this);
        }
    }

    public static class BobView extends RenderItemInHandBobEvent {
        public BobView() {
            postClientEventToKubeJS(this);
        }
    }
}
