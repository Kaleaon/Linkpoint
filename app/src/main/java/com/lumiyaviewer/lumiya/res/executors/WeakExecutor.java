package com.lumiyaviewer.lumiya.res.executors;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.res.collections.WeakQueue;
import com.lumiyaviewer.lumiya.utils.HasPriority;
import java.lang.ref.WeakReference;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;

public class WeakExecutor extends ThreadPoolExecutor {
    private final boolean usePriorities = false;

    private static class ComparableFutureTask<T> extends FutureTask<T> implements Comparable<ComparableFutureTask<T>> {
        private final int priority;

        private static class WeakCallable<T> implements Callable<T> {
            private final WeakReference<Callable<T>> callableRef;

            private WeakCallable(Callable<T> callable) {
                this.callableRef = new WeakReference(callable);
            }

            /* synthetic */ WeakCallable(Callable callable, WeakCallable weakCallable) {
                this(callable);
            }

            public T call() throws Exception {
                Callable callable = (Callable) this.callableRef.get();
                return callable != null ? callable.call() : null;
            }
        }

        private static class WeakRunnable implements Runnable {
            private final WeakReference<Runnable> runnableRef;

            private WeakRunnable(Runnable runnable) {
                this.runnableRef = new WeakReference(runnable);
            }

            /* synthetic */ WeakRunnable(Runnable runnable, WeakRunnable weakRunnable) {
                this(runnable);
            }

            public void run() {
                Runnable runnable = (Runnable) this.runnableRef.get();
                if (runnable != null) {
                    runnable.run();
                }
            }
        }

        ComparableFutureTask(Runnable runnable, T t) {
            super(new WeakRunnable(runnable, null), t);
            if (runnable instanceof HasPriority) {
                this.priority = ((HasPriority) runnable).getPriority();
            } else {
                this.priority = 0;
            }
        }

        ComparableFutureTask(Callable<T> callable) {
            super(new WeakCallable(callable, null));
            if (callable instanceof HasPriority) {
                this.priority = ((HasPriority) callable).getPriority();
            } else {
                this.priority = 0;
            }
        }

        public int compareTo(@Nonnull ComparableFutureTask<T> comparableFutureTask) {
            return comparableFutureTask == this ? 0 : this.priority - comparableFutureTask.priority;
        }
    }

    WeakExecutor(String str, int i) {
        super(i, i, 60, TimeUnit.SECONDS, new WeakQueue(), (runnable) -> createBasicThread(str, runnable));
        Debug.Printf("Executor for %s: maxThreads %d", str, Integer.valueOf(i));
        allowCoreThreadTimeOut(true);
    }

    public WeakExecutor(String str, int i, BlockingQueue<Runnable> blockingQueue) {
        super(i, i, 60, TimeUnit.SECONDS, blockingQueue, (runnable) -> createPriorityThread(str, runnable));
        Debug.Printf("Executor for %s: maxThreads %d", str, Integer.valueOf(i));
        allowCoreThreadTimeOut(true);
    }

    static /* synthetic */ Thread createBasicThread(String str, Runnable runnable) {
        Thread thread = new Thread(runnable, str);
        Debug.Printf("Creating thread %s got %d", str, Long.valueOf(thread.getId()));
        thread.setPriority(4);
        return thread;
    }

    static /* synthetic */ Thread createPriorityThread(String str, Runnable runnable) {
        Thread thread = new Thread(runnable, str);
        Debug.Printf("Creating thread %s got %d", str, Long.valueOf(thread.getId()));
        thread.setPriority(4);
        return thread;
    }

    protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T t) {
        return this.usePriorities ? new ComparableFutureTask(runnable, t) : super.newTaskFor(runnable, t);
    }

    protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        return this.usePriorities ? new ComparableFutureTask(callable) : super.newTaskFor(callable);
    }
}
