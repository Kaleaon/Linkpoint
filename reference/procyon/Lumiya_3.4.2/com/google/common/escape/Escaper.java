// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.escape;

import com.google.common.base.Function;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.Beta;

@Beta
@GwtCompatible
public abstract class Escaper
{
    private final Function<String, String> asFunction;
    
    protected Escaper() {
        this.asFunction = new Function<String, String>() {
            @Override
            public String apply(final String s) {
                return Escaper.this.escape(s);
            }
        };
    }
    
    public final Function<String, String> asFunction() {
        return this.asFunction;
    }
    
    public abstract String escape(final String p0);
}
