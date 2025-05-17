package com.tacz.guns.compat.kubejs.events;

import com.tacz.guns.api.client.event.BeforeRenderHandEvent;
import com.tacz.guns.api.client.event.RenderItemInHandBobEvent;
import com.tacz.guns.api.client.event.RenderLevelBobEvent;
import com.tacz.guns.api.client.event.SwapItemWithOffHand;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.ScriptTypePredicate;
import net.neoforged.bus.api.Event;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class TimelessClientEvents implements TimelessKubeJSEventRegister{
    public static final TimelessClientEvents INSTANCE = new TimelessClientEvents();
    public static final Map<Class<? extends Event>, Consumer<Event>> EVENT_HANDLERS = new HashMap<>();
    public static final EventHandler BEFORE_RENDER_HAND = INSTANCE.registerTimelessEvent(
            "beforeRenderHand",
            GunKubeJSEvents.BeforeRenderHandEventJS.class,
            BeforeRenderHandEvent.class,
            GunKubeJSEvents.BeforeRenderHandEventJS::new
    );
    public static final EventHandler RENDER_ITEM_IN_HAND_BOB_HURT = INSTANCE.registerTimelessEvent(
            "renderItemInHandBobHurt",
            GunKubeJSEvents.RenderItemInHandBobHurtEventJS.class,
            RenderItemInHandBobEvent.BobHurt.class,
            GunKubeJSEvents.RenderItemInHandBobHurtEventJS::new,
            true
    );
    public static final EventHandler RENDER_ITEM_IN_HAND_BOB_VIEW = INSTANCE.registerTimelessEvent(
            "renderItemInHandBobView",
            GunKubeJSEvents.RenderItemInHandBobViewEventJS.class,
            RenderItemInHandBobEvent.BobView.class,
            GunKubeJSEvents.RenderItemInHandBobViewEventJS::new,
            true
    );
    public static final EventHandler RENDER_LEVEL_BOB_HURT = INSTANCE.registerTimelessEvent(
            "renderLevelBobHurt",
            GunKubeJSEvents.RenderLevelBobHurtEventJS.class,
            RenderLevelBobEvent.BobHurt.class,
            GunKubeJSEvents.RenderLevelBobHurtEventJS::new,
            true
    );
    public static final EventHandler RENDER_LEVEL_BOB_VIEW = INSTANCE.registerTimelessEvent(
            "renderLevelBobView",
            GunKubeJSEvents.RenderLevelBobViewEventJS.class,
            RenderLevelBobEvent.BobView.class,
            GunKubeJSEvents.RenderLevelBobViewEventJS::new,
            true
    );
    public static final EventHandler SWAP_ITEM_WITH_OFF_HAND = INSTANCE.registerTimelessEvent(
            "swapItemWithOffHand",
            GunKubeJSEvents.SwapItemWithOffHandEventJS.class,
            SwapItemWithOffHand.class,
            GunKubeJSEvents.SwapItemWithOffHandEventJS::new
    );

    private TimelessClientEvents() {
    }

    @Override
    public Map<Class<? extends Event>, Consumer<Event>> getEventHandlers() {
        return EVENT_HANDLERS;
    }

    @Override
    public <E extends Event> void registerEventHandler(Class<E> eventClass, Consumer<Event> eventPoster) {
        EVENT_HANDLERS.put(eventClass, eventPoster);
    }


    @Override
    public ScriptTypePredicate getScriptType() {
        return ScriptType.CLIENT;
    }
}
