package com.tacz.guns.api;

import com.google.common.reflect.TypeToken;

public record GunProperty<T>(
        String name,
        Class<T> type
) {
    public static <T> GunProperty<T> of(String name, Class<T> type) {
        return new GunProperty<T>(name, type);
    }

    @SuppressWarnings("unchecked")
    public static <T> GunProperty<T> of(String name, TypeToken<T> type) {
        return new GunProperty<T>(name, (Class<T>) type.getRawType());
    }
}
