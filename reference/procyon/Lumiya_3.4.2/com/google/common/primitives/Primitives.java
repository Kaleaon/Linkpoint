// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.primitives;

import com.google.common.base.Preconditions;
import java.util.Set;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.CheckReturnValue;

@CheckReturnValue
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
    }
    
    private static void add(final Map<Class<?>, Class<?>> map, final Map<Class<?>, Class<?>> map2, final Class<?> clazz, final Class<?> clazz2) {
        map.put(clazz, clazz2);
        map2.put(clazz2, clazz);
    }
    
    public static Set<Class<?>> allPrimitiveTypes() {
        return Primitives.PRIMITIVE_TO_WRAPPER_TYPE.keySet();
    }
    
    public static Set<Class<?>> allWrapperTypes() {
        return Primitives.WRAPPER_TO_PRIMITIVE_TYPE.keySet();
    }
    
    public static boolean isWrapperType(final Class<?> clazz) {
        return Primitives.WRAPPER_TO_PRIMITIVE_TYPE.containsKey(Preconditions.checkNotNull(clazz));
    }
    
    public static <T> Class<T> unwrap(Class<T> clazz) {
        Preconditions.checkNotNull(clazz);
        final Class clazz2 = Primitives.WRAPPER_TO_PRIMITIVE_TYPE.get(clazz);
        if (clazz2 != null) {
            clazz = clazz2;
        }
        return clazz;
    }
    
    public static <T> Class<T> wrap(Class<T> clazz) {
        Preconditions.checkNotNull(clazz);
        final Class clazz2 = Primitives.PRIMITIVE_TO_WRAPPER_TYPE.get(clazz);
        if (clazz2 != null) {
            clazz = clazz2;
        }
        return clazz;
    }
}
