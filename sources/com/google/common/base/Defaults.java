package com.google.common.base;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;

@CheckReturnValue
public final class Defaults {
    private static final Map<Class<?>, Object> DEFAULTS;

    static {
        HashMap hashMap = new HashMap();
        put(hashMap, Boolean.TYPE, false);
        put(hashMap, Character.TYPE, 0);
        put(hashMap, Byte.TYPE, (byte) 0);
        put(hashMap, Short.TYPE, (short) 0);
        put(hashMap, Integer.TYPE, 0);
        put(hashMap, Long.TYPE, 0L);
        put(hashMap, Float.TYPE, Float.valueOf(0.0f));
        put(hashMap, Double.TYPE, Double.valueOf(0.0d));
        DEFAULTS = Collections.unmodifiableMap(hashMap);
    }

    private Defaults() {
    }

    @Nullable
    public static <T> T defaultValue(Class<T> cls) {
        return DEFAULTS.get(Preconditions.checkNotNull(cls));
    }

    private static <T> void put(Map<Class<?>, Object> map, Class<T> cls, T t) {
        map.put(cls, t);
    }
}
