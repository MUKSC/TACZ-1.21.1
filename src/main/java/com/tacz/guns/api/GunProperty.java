package com.tacz.guns.api;

import com.google.common.reflect.TypeToken;
import net.minecraft.Util;

public record GunProperty<T>(
        String name,
        Class<T> type
) {
    public static <T> GunProperty<T> of(String name, Class<T> type) {
        return Util.make(new GunProperty<>(name, type), property -> GunProperties.ALL.put(name, property));
    }

    @SuppressWarnings("unchecked")
    public static <T> GunProperty<T> of(String name, TypeToken<T> type) {
        return Util.make(new GunProperty<>(name, (Class<T>) type.getRawType()), property -> GunProperties.ALL.put(name, property));
    }
}
