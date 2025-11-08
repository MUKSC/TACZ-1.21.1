package com.tacz.guns.compat.kubejs.events;

import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.script.ScriptTypeHolder;
import dev.latvian.mods.kubejs.script.ScriptTypePredicate;
import net.neoforged.bus.api.Event;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public interface TimelessKubeJSEventRegister {
    default void init() {}

    Map<Class<? extends Event>, Consumer<Event>> getEventHandlers();

    ScriptTypePredicate getScriptType();

    default <E extends Event> EventHandler registerTimelessEvent(
            String id,
            Class<? extends GunKubeJSEvents.GunEventJS<E>> eventJSClass,
            Class<E> eventClass,
            Function<E, ? extends GunKubeJSEvents.GunEventJS<E>> eventJSFactory
    ) {
        return registerTimelessEvent(id, eventJSClass, eventClass, eventJSFactory, false);
    }

    default <E extends Event> EventHandler registerTimelessEvent(
            String id,
            Class<? extends GunKubeJSEvents.GunEventJS<E>> eventJSClass,
            Class<E> eventClass,
            Function<E, ? extends GunKubeJSEvents.GunEventJS<E>> eventJSFactory,
            boolean hasResult
    ) {
        EventHandler handler = registerEventJS(id, eventJSClass, hasResult);
        registerEventHandler(eventClass, (event) -> {
            GunKubeJSEvents.GunEventJS<E> eventJS = eventJSFactory.apply((E) event);
            handler.post(eventJS.getTypeHolder(), eventJS);
        });
        return handler;
    }

    default  <E extends Event> EventHandler registerTimelessCommonEvent(
            String id,
            Class<? extends GunKubeJSEvents.GunEventJS<E>> eventJSClass,
            Class<E> eventClass,
            Function<E, ? extends GunKubeJSEvents.GunEventJS<E>> eventJSFactory
    ) {
        return registerTimelessCommonEvent(id, eventJSClass, eventClass, eventJSFactory, false);
    }

    default <E extends Event> EventHandler registerTimelessCommonEvent(
            String id,
            Class<? extends GunKubeJSEvents.GunEventJS<E>> eventJSClass,
            Class<E> eventClass,
            Function<E, ? extends GunKubeJSEvents.GunEventJS<E>> eventJSFactory,
            boolean hasResult
    ) {
        EventHandler handler = registerEventJS(id, eventJSClass, hasResult);
        registerEventHandler(eventClass, (event) -> {
            GunKubeJSEvents.GunEventJS<E> eventJS = eventJSFactory.apply((E) event);
            handler.post(eventJS.getTypeHolder(), eventJS);
        });
        return handler;
    }

    default <E extends Event> EventHandler registerEventJS(String id, Class<? extends GunKubeJSEvents.GunEventJS<E>> eventJSClass, boolean hasResult) {
        return hasResult ? GunKubeJSEvents.GROUP.add(id, getScriptType(), () -> eventJSClass).hasResult() : GunKubeJSEvents.GROUP.add(id, getScriptType(), () -> eventJSClass);
    }

    <E extends Event> void registerEventHandler(Class<E> eventClass, Consumer<Event> eventPoster);

    default boolean postKubeJSEvent(Event event) {
        Consumer<Event> eventHandler = getEventHandlers().get(event.getClass());
        if (eventHandler != null) {
            eventHandler.accept(event);
            return false;
        } else {
            return true;
        }
    }
}
