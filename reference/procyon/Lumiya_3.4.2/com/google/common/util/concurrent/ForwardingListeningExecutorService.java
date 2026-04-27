// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.util.concurrent;

import java.util.concurrent.Future;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

public abstract class ForwardingListeningExecutorService extends ForwardingExecutorService implements ListeningExecutorService
{
    protected ForwardingListeningExecutorService() {
    }
    
    @Override
    protected abstract ListeningExecutorService delegate();
    
    @Override
    public ListenableFuture<?> submit(final Runnable runnable) {
        return this.delegate().submit(runnable);
    }
    
    @Override
    public <T> ListenableFuture<T> submit(final Runnable runnable, final T t) {
        return this.delegate().submit(runnable, t);
    }
    
    @Override
    public <T> ListenableFuture<T> submit(final Callable<T> callable) {
        return this.delegate().submit(callable);
    }
}
