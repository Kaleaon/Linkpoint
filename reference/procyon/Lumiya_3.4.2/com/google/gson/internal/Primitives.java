// 
// Decompiled by Procyon v0.6.0
// 

package com.google.gson.internal;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class Primitives
{
    private static final Map<Class<?>, Class<?>> PRIMITIVE_TO_WRAPPER_TYPE;
    private static final Map<Class<?>, Class<?>> WRAPPER_TO_PRIMITIVE_TYPE;
    
    static {
        final HashMap m = new HashMap(16);
        final HashMap i = new HashMap(16);
        add(m, i, Boolean.TYPE, Boolean.class);
        add(m, i, Byte.TYPE, Byte.class);
        add(m, i, Character.TYPE, Character.class);
        add(m, i, Double.TYPE, Double.class);
        add(m, i, Float.TYPE, Float.class);
        add(m, i, Integer.TYPE, Integer.class);
        add(m, i, Long.TYPE, Long.class);
        add(m, i, Short.TYPE, Short.class);
        add(m, i, Void.TYPE, Void.class);
        PRIMITIVE_TO_WRAPPER_TYPE = Collections.unmodifiableMap((Map<?, ?>)m);
        WRAPPER_TO_PRIMITIVE_TYPE = Collections.unmodifiableMap((Map<?, ?>)i);
    }
    
    private Primitives() {
        throw new UnsupportedOperationException();
    }
    
    private static void add(final Map<Class<?>, Class<?>> map, final Map<Class<?>, Class<?>> map2, final Class<?> clazz, final Class<?> clazz2) {
        map.put(clazz, clazz2);
        map2.put(clazz2, clazz);
    }
    
    public static boolean isPrimitive(final Type type) {
        return Primitives.PRIMITIVE_TO_WRAPPER_TYPE.containsKey(type);
    }
    
    public static boolean isWrapperType(final Type type) {
        return Primitives.WRAPPER_TO_PRIMITIVE_TYPE.containsKey($Gson$Preconditions.checkNotNull(type));
    }
    
    public static <T> Class<T> unwrap(Class<T> clazz) {
        final Class clazz2 = Primitives.WRAPPER_TO_PRIMITIVE_TYPE.get($Gson$Preconditions.checkNotNull(clazz));
        if (clazz2 != null) {
            clazz = clazz2;
        }
        return clazz;
    }
    
    public static <T> Class<T> wrap(Class<T> clazz) {
        final Class clazz2 = Primitives.PRIMITIVE_TO_WRAPPER_TYPE.get($Gson$Preconditions.checkNotNull(clazz));
        if (clazz2 != null) {
            clazz = clazz2;
        }
        return clazz;
    }
}
