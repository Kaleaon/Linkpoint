// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;

@GwtCompatible
public enum BoundType
{
    CLOSED {
        @Override
        BoundType flip() {
            return BoundType$2.OPEN;
        }
    }, 
    OPEN {
        @Override
        BoundType flip() {
            return BoundType$1.CLOSED;
        }
    };
    
    static BoundType forBoolean(final boolean b) {
        BoundType boundType;
        if (!b) {
            boundType = BoundType.OPEN;
        }
        else {
            boundType = BoundType.CLOSED;
        }
        return boundType;
    }
    
    abstract BoundType flip();
}
