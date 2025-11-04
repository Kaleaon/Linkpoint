// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.util.concurrent;

import com.google.common.base.Throwables;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.Iterator;
import com.google.common.collect.ImmutableList;
import java.util.concurrent.Callable;
import java.util.Collection;
import com.google.common.base.Preconditions;
import java.util.concurrent.ExecutorService;

abstract class WrappingExecutorService implements ExecutorService
{
    private final ExecutorService delegate;
    
    protected WrappingExecutorService(final ExecutorService executorService) {
        this.delegate = Preconditions.checkNotNull(executorService);
    }
    
    private final <T> ImmutableList<Callable<T>> wrapTasks(final Collection<? extends Callable<T>> collection) {
        final ImmutableList.Builder<Callable<T>> builder = ImmutableList.builder();
        final Iterator<? extends Callable<T>> iterator = collection.iterator();
        while (iterator.hasNext()) {
            builder.add(this.wrapTask((Callable<T>)iterator.next()));
        }
        return builder.build();
    }
    
    @Override
    public final boolean awaitTermination(final long n, final TimeUnit timeUnit) throws InterruptedException {
        return this.delegate.awaitTermination(n, timeUnit);
    }
    
    @Override
    public final void execute(final Runnable runnable) {
        this.delegate.execute(this.wrapTask(runnable));
    }
    
    @Override
    public final <T> List<Future<T>> invokeAll(final Collection<? extends Callable<T>> collection) throws InterruptedException {
        return this.delegate.invokeAll((Collection<? extends Callable<T>>)this.wrapTasks((Collection<? extends Callable<Object>>)collection));
    }
    
    @Override
    public final <T> List<Future<T>> invokeAll(final Collection<? extends Callable<T>> collection, final long n, final TimeUnit timeUnit) throws InterruptedException {
        return this.delegate.invokeAll((Collection<? extends Callable<T>>)this.wrapTasks((Collection<? extends Callable<Object>>)collection), n, timeUnit);
    }
    
    @Override
    public final <T> T invokeAny(final Collection<? extends Callable<T>> collection) throws InterruptedException, ExecutionException {
        return this.delegate.invokeAny((Collection<? extends Callable<T>>)this.wrapTasks((Collection<? extends Callable<Object>>)collection));
    }
    
    @Override
    public final <T> T invokeAny(final Collection<? extends Callable<T>> collection, final long n, final TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
        return this.delegate.invokeAny((Collection<? extends Callable<T>>)this.wrapTasks((Collection<? extends Callable<Object>>)collection), n, timeUnit);
    }
    
    @Override
    public final boolean isShutdown() {
        return this.delegate.isShutdown();
    }
    
    @Override
    public final boolean isTerminated() {
        return this.delegate.isTerminated();
    }
    
    @Override
    public final void shutdown() {
        this.delegate.shutdown();
    }
    
    @Override
    public final List<Runnable> shutdownNow() {
        return this.delegate.shutdownNow();
    }
    
    @Override
    public final Future<?> submit(final Runnable runnable) {
        return this.delegate.submit(this.wrapTask(runnable));
    }
    
    @Override
    public final <T> Future<T> submit(final Runnable runnable, final T t) {
        return this.delegate.submit(this.wrapTask(runnable), t);
    }
    
    @Override
    public final <T> Future<T> submit(final Callable<T> callable) {
        return this.delegate.submit((Callable<T>)this.wrapTask((Callable<T>)Preconditions.checkNotNull((Callable<T>)callable)));
    }
    
    protected Runnable wrapTask(final Runnable task) {
        return new Runnable() {
            final /* synthetic */ Callable val$wrapped = WrappingExecutorService.this.wrapTask((Callable<Object>)Executors.callable(task, (T)null));
            
            @Override
            public void run() {
                try {
                    this.val$wrapped.call();
                }
                catch (final Exception ex) {
                    Throwables.propagate(ex);
                }
            }
        };
    }
    
    protected abstract <T> Callable<T> wrapTask(final Callable<T> p0);
}
