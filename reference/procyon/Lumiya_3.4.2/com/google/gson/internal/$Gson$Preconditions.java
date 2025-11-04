// 
// Decompiled by Procyon v0.6.0
// 

package com.google.gson.internal;

public final class $Gson$Preconditions
{
    private $Gson$Preconditions() {
        throw new UnsupportedOperationException();
    }
    
    public static void checkArgument(final boolean b) {
        if (b) {
            return;
        }
        throw new IllegalArgumentException();
    }
    
    public static <T> T checkNotNull(final T t) {
        if (t != null) {
            return t;
        }
        throw new NullPointerException();
    }
}
