// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.util.concurrent;

import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.ThreadFactory;
import com.google.common.base.Preconditions;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import com.google.common.annotations.Beta;

@Beta
public final class JdkFutureAdapters
{
    private JdkFutureAdapters() {
    }
    
    public static <V> ListenableFuture<V> listenInPoolThread(final Future<V> future) {
        if (!(future instanceof ListenableFuture)) {
            return new ListenableFutureAdapter<V>((Future<Object>)future);
        }
        return (ListenableFuture<V>)future;
    }
    
    public static <V> ListenableFuture<V> listenInPoolThread(final Future<V> future, final Executor executor) {
        Preconditions.checkNotNull(executor);
        if (!(future instanceof ListenableFuture)) {
            return new ListenableFutureAdapter<V>((Future<Object>)future, executor);
        }
        return (ListenableFuture<V>)future;
    }
    
    private static class ListenableFutureAdapter<V> extends ForwardingFuture<V> implements ListenableFuture<V>
    {
        private static final Executor defaultAdapterExecutor;
        private static final ThreadFactory threadFactory;
        private final Executor adapterExecutor;
        private final Future<V> delegate;
        private final ExecutionList executionList;
        private final AtomicBoolean hasListeners;
        
        static {
            threadFactory = new ThreadFactoryBuilder().setDaemon(true).setNameFormat("ListenableFutureAdapter-thread-%d").build();
            defaultAdapterExecutor = Executors.newCachedThreadPool(ListenableFutureAdapter.threadFactory);
        }
        
        ListenableFutureAdapter(final Future<V> future) {
            this(future, ListenableFutureAdapter.defaultAdapterExecutor);
        }
        
        ListenableFutureAdapter(final Future<V> future, final Executor executor) {
            this.executionList = new ExecutionList();
            this.hasListeners = new AtomicBoolean(false);
            this.delegate = Preconditions.checkNotNull(future);
            this.adapterExecutor = Preconditions.checkNotNull(executor);
        }
        
        @Override
        public void addListener(final Runnable runnable, final Executor executor) {
            this.executionList.add(runnable, executor);
            if (this.hasListeners.compareAndSet(false, true)) {
                if (this.delegate.isDone()) {
                    this.executionList.execute();
                    return;
                }
                this.adapterExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                            try {
                                Uninterruptibles.getUninterruptibly((Future<Object>)ListenableFutureAdapter.this.delegate);
                                ListenableFutureAdapter.this.executionList.execute();
                            }
                            catch (final Throwable t) {
                                continue;
                            }
                            break;
                        }
                    }
                });
            }
        }
        
        @Override
        protected Future<V> delegate() {
            return this.delegate;
        }
    }
}
