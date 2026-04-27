// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;

@GwtCompatible(serializable = true)
class EmptyImmutableListMultimap extends ImmutableListMultimap<Object, Object>
{
    static final EmptyImmutableListMultimap INSTANCE;
    private static final long serialVersionUID = 0L;
    
    static {
        INSTANCE = new EmptyImmutableListMultimap();
    }
    
    private EmptyImmutableListMultimap() {
        super(ImmutableMap.of(), 0);
    }
    
    private Object readResolve() {
        return EmptyImmutableListMultimap.INSTANCE;
    }
}
