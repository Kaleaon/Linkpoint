// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import javax.annotation.Nullable;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible
public final class SettableFuture<V> extends TrustedFuture<V>
{
    private SettableFuture() {
    }
    
    public static <V> SettableFuture<V> create() {
        return new SettableFuture<V>();
    }
    
    public boolean set(@Nullable final V v) {
        return super.set(v);
    }
    
    public boolean setException(final Throwable exception) {
        return super.setException(exception);
    }
    
    @Beta
    public boolean setFuture(final ListenableFuture<? extends V> future) {
        return super.setFuture(future);
    }
}
