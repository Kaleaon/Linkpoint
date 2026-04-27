// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.util.concurrent;

import java.util.concurrent.TimeoutException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutorService;
import com.google.common.collect.ForwardingObject;

public abstract class ForwardingExecutorService extends ForwardingObject implements ExecutorService
{
    protected ForwardingExecutorService() {
    }
    
    @Override
    public boolean awaitTermination(final long n, final TimeUnit timeUnit) throws InterruptedException {
        return this.delegate().awaitTermination(n, timeUnit);
    }
    
    @Override
    protected abstract ExecutorService delegate();
    
    @Override
    public void execute(final Runnable runnable) {
        this.delegate().execute(runnable);
    }
    
    @Override
    public <T> List<Future<T>> invokeAll(final Collection<? extends Callable<T>> collection) throws InterruptedException {
        return this.delegate().invokeAll(collection);
    }
    
    @Override
    public <T> List<Future<T>> invokeAll(final Collection<? extends Callable<T>> collection, final long n, final TimeUnit timeUnit) throws InterruptedException {
        return this.delegate().invokeAll(collection, n, timeUnit);
    }
    
    @Override
    public <T> T invokeAny(final Collection<? extends Callable<T>> collection) throws InterruptedException, ExecutionException {
        return this.delegate().invokeAny(collection);
    }
    
    @Override
    public <T> T invokeAny(final Collection<? extends Callable<T>> collection, final long n, final TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
        return this.delegate().invokeAny(collection, n, timeUnit);
    }
    
    @Override
    public boolean isShutdown() {
        return this.delegate().isShutdown();
    }
    
    @Override
    public boolean isTerminated() {
        return this.delegate().isTerminated();
    }
    
    @Override
    public void shutdown() {
        this.delegate().shutdown();
    }
    
    @Override
    public List<Runnable> shutdownNow() {
        return this.delegate().shutdownNow();
    }
    
    @Override
    public Future<?> submit(final Runnable runnable) {
        return this.delegate().submit(runnable);
    }
    
    @Override
    public <T> Future<T> submit(final Runnable runnable, final T t) {
        return this.delegate().submit(runnable, t);
    }
    
    @Override
    public <T> Future<T> submit(final Callable<T> callable) {
        return this.delegate().submit(callable);
    }
}
