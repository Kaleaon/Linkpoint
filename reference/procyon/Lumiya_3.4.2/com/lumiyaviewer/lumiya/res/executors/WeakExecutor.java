// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.res.executors;

import java.lang.ref.WeakReference;
import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.utils.HasPriority;
import java.util.concurrent.FutureTask;
import java.util.concurrent.Callable;
import java.util.concurrent.RunnableFuture;
import com.lumiyaviewer.lumiya.Debug;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.BlockingQueue;
import com.lumiyaviewer.lumiya.res.collections.WeakQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadPoolExecutor;

public class WeakExecutor extends ThreadPoolExecutor
{
    private final boolean usePriorities;
    
    WeakExecutor(final String s, final int i) {
        super(i, i, 60L, TimeUnit.SECONDS, new WeakQueue<Runnable>(), new _$Lambda$paN_qX4OegT79dFg6kmGbliJfA0(s));
        this.usePriorities = false;
        Debug.Printf("Executor for %s: maxThreads %d", s, i);
        this.allowCoreThreadTimeOut(true);
    }
    
    public WeakExecutor(final String s, final int i, final BlockingQueue<Runnable> workQueue) {
        super(i, i, 60L, TimeUnit.SECONDS, workQueue, new _$Lambda$paN_qX4OegT79dFg6kmGbliJfA0$1(s));
        this.usePriorities = true;
        Debug.Printf("Executor for %s: maxThreads %d", s, i);
        this.allowCoreThreadTimeOut(true);
    }
    
    @Override
    protected <T> RunnableFuture<T> newTaskFor(final Runnable runnable, final T value) {
        if (this.usePriorities) {
            return new ComparableFutureTask<T>(runnable, value);
        }
        return super.newTaskFor(runnable, value);
    }
    
    @Override
    protected <T> RunnableFuture<T> newTaskFor(final Callable<T> callable) {
        if (this.usePriorities) {
            return new ComparableFutureTask<T>(callable);
        }
        return super.newTaskFor(callable);
    }
    
    private static class ComparableFutureTask<T> extends FutureTask<T> implements Comparable<ComparableFutureTask<T>>
    {
        private final int priority;
        
        ComparableFutureTask(final Runnable runnable, final T result) {
            super(new WeakRunnable(runnable, null), result);
            if (runnable instanceof HasPriority) {
                this.priority = ((HasPriority)runnable).getPriority();
            }
            else {
                this.priority = 0;
            }
        }
        
        ComparableFutureTask(final Callable<T> callable) {
            super(new WeakCallable(callable, null));
            if (callable instanceof HasPriority) {
                this.priority = ((HasPriority)callable).getPriority();
            }
            else {
                this.priority = 0;
            }
        }
        
        @Override
        public int compareTo(@Nonnull final ComparableFutureTask<T> comparableFutureTask) {
            if (comparableFutureTask == this) {
                return 0;
            }
            return this.priority - comparableFutureTask.priority;
        }
        
        private static class WeakCallable<T> implements Callable<T>
        {
            private final WeakReference<Callable<T>> callableRef;
            
            private WeakCallable(final Callable<T> referent) {
                this.callableRef = new WeakReference<Callable<T>>(referent);
            }
            
            @Override
            public T call() throws Exception {
                final Callable callable = this.callableRef.get();
                if (callable != null) {
                    return (T)callable.call();
                }
                return null;
            }
        }
        
        private static class WeakRunnable implements Runnable
        {
            private final WeakReference<Runnable> runnableRef;
            
            private WeakRunnable(final Runnable referent) {
                this.runnableRef = new WeakReference<Runnable>(referent);
            }
            
            @Override
            public void run() {
                final Runnable runnable = this.runnableRef.get();
                if (runnable != null) {
                    runnable.run();
                }
            }
        }
    }
}
