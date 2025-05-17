package com.tacz.guns.api.client.event;


import com.tacz.guns.api.event.common.KubeJSGunEventPoster;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

/**
 * 当第一人称视角触发摇晃时，世界背景的摇晃
 */
public class RenderLevelBobEvent extends Event implements KubeJSGunEventPoster<RenderLevelBobEvent>, ICancellableEvent {
    public static class BobHurt extends RenderLevelBobEvent {
        public BobHurt() {
            postClientEventToKubeJS(this);
        }
    }

    public static class BobView extends RenderLevelBobEvent {
        public BobView() {
            postClientEventToKubeJS(this);
        }
    }
}
