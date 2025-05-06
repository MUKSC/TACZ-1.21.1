package com.tacz.guns.api.client.event;

import com.tacz.guns.api.event.common.KubeJSGunEventPoster;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * 当第一人称视角触发摇晃时，世界背景的摇晃
 */
public class RenderLevelBobEvent extends Event implements KubeJSGunEventPoster<RenderLevelBobEvent> {
    /**
     * 使用注解也可以，但是热重载会导致游戏崩溃
     */
    @Override
    public boolean isCancelable() {
        return true;
    }

    @Cancelable
    public static class BobHurt extends RenderLevelBobEvent {
        public BobHurt() {
            postClientEventToKubeJS(this);
        }
    }

    @Cancelable
    public static class BobView extends RenderLevelBobEvent {
        public BobView() {
            postClientEventToKubeJS(this);
        }
    }
}
