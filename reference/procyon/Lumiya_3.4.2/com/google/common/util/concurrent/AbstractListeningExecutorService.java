// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.util.concurrent;

import java.util.concurrent.Future;
import javax.annotation.Nullable;
import java.util.concurrent.Callable;
import java.util.concurrent.RunnableFuture;
import com.google.common.annotations.Beta;
import java.util.concurrent.AbstractExecutorService;

@Beta
public abstract class AbstractListeningExecutorService extends AbstractExecutorService implements ListeningExecutorService
{
    @Override
    protected final <T> RunnableFuture<T> newTaskFor(final Runnable runnable, final T t) {
        return TrustedListenableFutureTask.create(runnable, t);
    }
    
    @Override
    protected final <T> RunnableFuture<T> newTaskFor(final Callable<T> callable) {
        return TrustedListenableFutureTask.create(callable);
    }
    
    @Override
    public ListenableFuture<?> submit(final Runnable task) {
        return (ListenableFuture)super.submit(task);
    }
    
    @Override
    public <T> ListenableFuture<T> submit(final Runnable task, @Nullable final T result) {
        return (ListenableFuture)super.submit(task, result);
    }
    
    @Override
    public <T> ListenableFuture<T> submit(final Callable<T> task) {
        return (ListenableFuture)super.submit(task);
    }
}
