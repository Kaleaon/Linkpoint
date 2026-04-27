// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.util.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

abstract class WrappingScheduledExecutorService extends WrappingExecutorService implements ScheduledExecutorService
{
    final ScheduledExecutorService delegate;
    
    protected WrappingScheduledExecutorService(final ScheduledExecutorService delegate) {
        super(delegate);
        this.delegate = delegate;
    }
    
    @Override
    public final ScheduledFuture<?> schedule(final Runnable runnable, final long n, final TimeUnit timeUnit) {
        return this.delegate.schedule(this.wrapTask(runnable), n, timeUnit);
    }
    
    @Override
    public final <V> ScheduledFuture<V> schedule(final Callable<V> callable, final long n, final TimeUnit timeUnit) {
        return this.delegate.schedule((Callable<V>)this.wrapTask((Callable<V>)callable), n, timeUnit);
    }
    
    @Override
    public final ScheduledFuture<?> scheduleAtFixedRate(final Runnable runnable, final long n, final long n2, final TimeUnit timeUnit) {
        return this.delegate.scheduleAtFixedRate(this.wrapTask(runnable), n, n2, timeUnit);
    }
    
    @Override
    public final ScheduledFuture<?> scheduleWithFixedDelay(final Runnable runnable, final long n, final long n2, final TimeUnit timeUnit) {
        return this.delegate.scheduleWithFixedDelay(this.wrapTask(runnable), n, n2, timeUnit);
    }
}
