// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.base;

import javax.annotation.CheckReturnValue;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.Beta;

@Beta
@GwtCompatible
public abstract class Ticker
{
    private static final Ticker SYSTEM_TICKER;
    
    static {
        SYSTEM_TICKER = new Ticker() {
            @Override
            public long read() {
                return Platform.systemNanoTime();
            }
        };
    }
    
    protected Ticker() {
    }
    
    @CheckReturnValue
    public static Ticker systemTicker() {
        return Ticker.SYSTEM_TICKER;
    }
    
    public abstract long read();
}
