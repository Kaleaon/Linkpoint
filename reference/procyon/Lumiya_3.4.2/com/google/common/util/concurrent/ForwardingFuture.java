// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.util.concurrent;

import com.google.common.base.Preconditions;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import com.google.common.collect.ForwardingObject;

public abstract class ForwardingFuture<V> extends ForwardingObject implements Future<V>
{
    protected ForwardingFuture() {
    }
    
    @Override
    public boolean cancel(final boolean b) {
        return this.delegate().cancel(b);
    }
    
    @Override
    protected abstract Future<V> delegate();
    
    @Override
    public V get() throws InterruptedException, ExecutionException {
        return this.delegate().get();
    }
    
    @Override
    public V get(final long n, final TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
        return this.delegate().get(n, timeUnit);
    }
    
    @Override
    public boolean isCancelled() {
        return this.delegate().isCancelled();
    }
    
    @Override
    public boolean isDone() {
        return this.delegate().isDone();
    }
    
    public abstract static class SimpleForwardingFuture<V> extends ForwardingFuture<V>
    {
        private final Future<V> delegate;
        
        protected SimpleForwardingFuture(final Future<V> future) {
            this.delegate = Preconditions.checkNotNull(future);
        }
        
        @Override
        protected final Future<V> delegate() {
            return this.delegate;
        }
    }
}
