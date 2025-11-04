// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;

@GwtCompatible(serializable = true)
final class UsingToStringOrdering extends Ordering<Object> implements Serializable
{
    static final UsingToStringOrdering INSTANCE;
    private static final long serialVersionUID = 0L;
    
    static {
        INSTANCE = new UsingToStringOrdering();
    }
    
    private UsingToStringOrdering() {
    }
    
    private Object readResolve() {
        return UsingToStringOrdering.INSTANCE;
    }
    
    @Override
    public int compare(final Object o, final Object o2) {
        return o.toString().compareTo(o2.toString());
    }
    
    @Override
    public String toString() {
        return "Ordering.usingToString()";
    }
}
