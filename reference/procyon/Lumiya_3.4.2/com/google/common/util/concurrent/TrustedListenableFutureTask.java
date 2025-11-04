// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.util.concurrent;

import com.google.common.base.Preconditions;
import com.google.common.annotations.GwtIncompatible;
import java.util.concurrent.Executors;
import javax.annotation.Nullable;
import java.util.concurrent.Callable;
import com.google.common.annotations.GwtCompatible;
import java.util.concurrent.RunnableFuture;

@GwtCompatible
class TrustedListenableFutureTask<V> extends TrustedFuture<V> implements RunnableFuture<V>
{
    private TrustedFutureInterruptibleTask task;
    
    TrustedListenableFutureTask(final Callable<V> callable) {
        this.task = new TrustedFutureInterruptibleTask(callable);
    }
    
    static <V> TrustedListenableFutureTask<V> create(final Runnable task, @Nullable final V result) {
        return new TrustedListenableFutureTask<V>(Executors.callable(task, result));
    }
    
    static <V> TrustedListenableFutureTask<V> create(final Callable<V> callable) {
        return new TrustedListenableFutureTask<V>(callable);
    }
    
    @Override
    final void done() {
        super.done();
        this.task = null;
    }
    
    @GwtIncompatible("Interruption not supported")
    @Override
    protected final void interruptTask() {
        final TrustedFutureInterruptibleTask task = this.task;
        if (task != null) {
            task.interruptTask();
        }
    }
    
    @Override
    public void run() {
        final TrustedFutureInterruptibleTask task = this.task;
        if (task != null) {
            task.run();
        }
    }
    
    private final class TrustedFutureInterruptibleTask extends InterruptibleTask
    {
        private final Callable<V> callable;
        
        TrustedFutureInterruptibleTask(final Callable<V> callable) {
            this.callable = Preconditions.checkNotNull(callable);
        }
        
        @Override
        void runInterruptibly() {
            if (!TrustedListenableFutureTask.this.isDone()) {
                try {
                    TrustedListenableFutureTask.this.set(this.callable.call());
                }
                catch (final Throwable exception) {
                    TrustedListenableFutureTask.this.setException(exception);
                }
            }
        }
        
        @Override
        boolean wasInterrupted() {
            return TrustedListenableFutureTask.this.wasInterrupted();
        }
    }
}
