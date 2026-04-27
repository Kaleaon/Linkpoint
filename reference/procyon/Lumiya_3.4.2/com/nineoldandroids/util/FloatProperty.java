// 
// Decompiled by Procyon v0.6.0
// 

package com.nineoldandroids.util;

public abstract class FloatProperty<T> extends Property<T, Float>
{
    public FloatProperty(final String s) {
        super(Float.class, s);
    }
    
    @Override
    public final void set(final T t, final Float n) {
        this.setValue(t, n);
    }
    
    public abstract void setValue(final T p0, final float p1);
}
