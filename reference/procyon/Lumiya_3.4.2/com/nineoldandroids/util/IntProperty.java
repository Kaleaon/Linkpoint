// 
// Decompiled by Procyon v0.6.0
// 

package com.nineoldandroids.util;

public abstract class IntProperty<T> extends Property<T, Integer>
{
    public IntProperty(final String s) {
        super(Integer.class, s);
    }
    
    @Override
    public final void set(final T t, final Integer n) {
        this.set(t, (int)n);
    }
    
    public abstract void setValue(final T p0, final int p1);
}
