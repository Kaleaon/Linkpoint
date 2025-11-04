// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.base;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Iterator;
import java.util.EnumSet;
import java.util.HashMap;
import java.lang.reflect.Field;
import java.util.WeakHashMap;
import com.google.common.annotations.GwtIncompatible;
import java.lang.ref.WeakReference;
import java.util.Map;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.Beta;
import javax.annotation.CheckReturnValue;

@CheckReturnValue
@Beta
@GwtCompatible(emulated = true)
public final class Enums
{
    @GwtIncompatible("java.lang.ref.WeakReference")
    private static final Map<Class<? extends Enum<?>>, Map<String, WeakReference<? extends Enum<?>>>> enumConstantCache;
    
    static {
        enumConstantCache = new WeakHashMap<Class<? extends Enum<?>>, Map<String, WeakReference<? extends Enum<?>>>>();
    }
    
    private Enums() {
    }
    
    @GwtIncompatible("java.lang.ref.WeakReference")
    static <T extends Enum<T>> Map<String, WeakReference<? extends Enum<?>>> getEnumConstants(final Class<T> clazz) {
        synchronized (Enums.enumConstantCache) {
            final Map map = Enums.enumConstantCache.get(clazz);
            Map<String, WeakReference<? extends Enum<?>>> populateCache;
            if (map != null) {
                populateCache = map;
            }
            else {
                populateCache = populateCache((Class<Enum>)clazz);
            }
            return populateCache;
        }
    }
    
    @GwtIncompatible("reflection")
    public static Field getField(final Enum<?> enum1) {
        final Class<?> declaringClass = enum1.getDeclaringClass();
        try {
            return declaringClass.getDeclaredField(enum1.name());
        }
        catch (final NoSuchFieldException detailMessage) {
            throw new AssertionError((Object)detailMessage);
        }
    }
    
    public static <T extends Enum<T>> Optional<T> getIfPresent(final Class<T> clazz, final String s) {
        Preconditions.checkNotNull(clazz);
        Preconditions.checkNotNull(s);
        return Platform.getEnumIfPresent(clazz, s);
    }
    
    @GwtIncompatible("java.lang.ref.WeakReference")
    private static <T extends Enum<T>> Map<String, WeakReference<? extends Enum<?>>> populateCache(final Class<T> elementType) {
        final HashMap hashMap = new HashMap();
        for (final Enum referent : EnumSet.allOf(elementType)) {
            hashMap.put(referent.name(), new WeakReference(referent));
        }
        Enums.enumConstantCache.put(elementType, hashMap);
        return hashMap;
    }
    
    public static <T extends Enum<T>> Converter<String, T> stringConverter(final Class<T> clazz) {
        return (Converter<String, T>)new StringConverter((Class<Enum>)clazz);
    }
    
    private static final class StringConverter<T extends Enum<T>> extends Converter<String, T> implements Serializable
    {
        private static final long serialVersionUID = 0L;
        private final Class<T> enumClass;
        
        StringConverter(final Class<T> clazz) {
            this.enumClass = Preconditions.checkNotNull(clazz);
        }
        
        @Override
        protected String doBackward(final T t) {
            return t.name();
        }
        
        @Override
        protected T doForward(final String name) {
            return Enum.valueOf(this.enumClass, name);
        }
        
        @Override
        public boolean equals(@Nullable final Object o) {
            return o instanceof StringConverter && this.enumClass.equals(((StringConverter)o).enumClass);
        }
        
        @Override
        public int hashCode() {
            return this.enumClass.hashCode();
        }
        
        @Override
        public String toString() {
            return "Enums.stringConverter(" + this.enumClass.getName() + ".class)";
        }
    }
}
