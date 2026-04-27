// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.base;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.CheckReturnValue;

@CheckReturnValue
public final class Defaults
{
    private static final Map<Class<?>, Object> DEFAULTS;
    
    static {
        final HashMap m = new HashMap();
        put(m, Boolean.TYPE, false);
        put(m, Character.TYPE, '\0');
        put(m, Byte.TYPE, (Byte)0);
        put(m, Short.TYPE, (Short)0);
        put(m, Integer.TYPE, 0);
        put(m, Long.TYPE, 0L);
        put(m, Float.TYPE, 0.0f);
        put(m, Double.TYPE, 0.0);
        DEFAULTS = Collections.unmodifiableMap((Map<?, ?>)m);
    }
    
    private Defaults() {
    }
    
    @Nullable
    public static <T> T defaultValue(final Class<T> clazz) {
        return (T)Defaults.DEFAULTS.get(Preconditions.checkNotNull(clazz));
    }
    
    private static <T> void put(final Map<Class<?>, Object> map, final Class<T> clazz, final T t) {
        map.put(clazz, t);
    }
}
