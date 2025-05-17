package com.tacz.guns.api.event.common;

import com.tacz.guns.compat.kubejs.events.TimelessClientEvents;
import com.tacz.guns.compat.kubejs.events.TimelessCommonEvents;
import com.tacz.guns.compat.kubejs.events.TimelessServerEvents;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.ModList;

public interface KubeJSGunEventPoster<E extends Event> {
    default void postEventToKubeJS(E event) {
        if (ModList.get().isLoaded("kubejs")) {
            TimelessCommonEvents.INSTANCE.postKubeJSEvent(event);
        }
    }

    //客户端事件应调用此方法
    default void postClientEventToKubeJS(E event) {
        if (ModList.get().isLoaded("kubejs")) {
            TimelessClientEvents.INSTANCE.postKubeJSEvent(event);
        }
    }

    //服务端事件应调用此方法
    default void postServerEventToKubeJS(E event) {
        if (ModList.get().isLoaded("kubejs")) {
            TimelessServerEvents.INSTANCE.postKubeJSEvent(event);
        }
    }
}
