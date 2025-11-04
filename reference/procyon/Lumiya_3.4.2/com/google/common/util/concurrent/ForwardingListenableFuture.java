// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.util.concurrent;

import com.google.common.base.Preconditions;
import java.util.concurrent.Future;
import java.util.concurrent.Executor;

public abstract class ForwardingListenableFuture<V> extends ForwardingFuture<V> implements ListenableFuture<V>
{
    protected ForwardingListenableFuture() {
    }
    
    @Override
    public void addListener(final Runnable runnable, final Executor executor) {
        this.delegate().addListener(runnable, executor);
    }
    
    @Override
    protected abstract ListenableFuture<V> delegate();
    
    public abstract static class SimpleForwardingListenableFuture<V> extends ForwardingListenableFuture<V>
    {
        private final ListenableFuture<V> delegate;
        
        protected SimpleForwardingListenableFuture(final ListenableFuture<V> listenableFuture) {
            this.delegate = Preconditions.checkNotNull(listenableFuture);
        }
        
        @Override
        protected final ListenableFuture<V> delegate() {
            return this.delegate;
        }
    }
}
