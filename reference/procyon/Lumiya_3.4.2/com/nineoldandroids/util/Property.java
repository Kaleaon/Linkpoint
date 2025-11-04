// 
// Decompiled by Procyon v0.6.0
// 

package com.nineoldandroids.util;

public abstract class Property<T, V>
{
    private final String mName;
    private final Class<V> mType;
    
    public Property(final Class<V> mType, final String mName) {
        this.mName = mName;
        this.mType = mType;
    }
    
    public static <T, V> Property<T, V> of(final Class<T> clazz, final Class<V> clazz2, final String s) {
        return new ReflectiveProperty<T, V>(clazz, clazz2, s);
    }
    
    public abstract V get(final T p0);
    
    public String getName() {
        return this.mName;
    }
    
    public Class<V> getType() {
        return this.mType;
    }
    
    public boolean isReadOnly() {
        return false;
    }
    
    public void set(final T t, final V v) {
        throw new UnsupportedOperationException("Property " + this.getName() + " is read-only");
    }
}
