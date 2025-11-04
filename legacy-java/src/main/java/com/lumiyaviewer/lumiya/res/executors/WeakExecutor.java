package com.lumiyaviewer.lumiya.res.executors;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.res.collections.WeakQueue;
import com.lumiyaviewer.lumiya.utils.HasPriority;
import java.lang.ref.WeakReference;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;

/* loaded from: classes.dex */
public class WeakExecutor extends ThreadPoolExecutor {
    private final boolean usePriorities;

    /* loaded from: classes.dex */
    private static class ComparableFutureTask<T> extends FutureTask<T> implements Comparable<ComparableFutureTask<T>> {
        private final int priority;

        /* loaded from: classes.dex */
        private static class WeakCallable<T> implements Callable<T> {
            private final WeakReference<Callable<T>> callableRef;

            private WeakCallable(Callable<T> callable) {
                this.callableRef = new WeakReference<>(callable);
            }

            /* synthetic */ WeakCallable(Callable callable, WeakCallable weakCallable) {
                this(callable);
            }

            @Override // java.util.concurrent.Callable
            public T call() throws Exception {
                Callable<T> callable = this.callableRef.get();
                if (callable != null) {
                    return callable.call();
                }
                return null;
            }
        }

        /* loaded from: classes.dex */
        private static class WeakRunnable implements Runnable {
            private final WeakReference<Runnable> runnableRef;

            private WeakRunnable(Runnable runnable) {
                this.runnableRef = new WeakReference<>(runnable);
            }

            /* synthetic */ WeakRunnable(Runnable runnable, WeakRunnable weakRunnable) {
                this(runnable);
            }

            @Override // java.lang.Runnable
            public void run() {
                Runnable runnable = this.runnableRef.get();
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

        @Override // java.lang.Comparable
        public int compareTo(@Nonnull ComparableFutureTask<T> comparableFutureTask) {
            if (comparableFutureTask == this) {
                return 0;
            }
            return this.priority - comparableFutureTask.priority;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public WeakExecutor(final String str, int i) {
        super(i, i, 60L, TimeUnit.SECONDS, new WeakQueue(), new ThreadFactory() { // from class: com.lumiyaviewer.lumiya.res.executors.-$Lambda$paN_qX4OegT79dFg6kmGbliJfA0
            private final /* synthetic */ Thread $m$0(Runnable runnable) {
                return WeakExecutor.m118lambda$com_lumiyaviewer_lumiya_res_executors_WeakExecutor_531((String) str, runnable);
            }

            @Override // java.util.concurrent.ThreadFactory
            public final Thread newThread(Runnable runnable) {
                return $m$0(runnable);
            }
        });
        this.usePriorities = false;
        Debug.Printf("Executor for %s: maxThreads %d", str, Integer.valueOf(i));
        allowCoreThreadTimeOut(true);
    }

    public WeakExecutor(final String str, int i, BlockingQueue<Runnable> blockingQueue) {
        super(i, i, 60L, TimeUnit.SECONDS, blockingQueue, new ThreadFactory() { // from class: com.lumiyaviewer.lumiya.res.executors.-$Lambda$paN_qX4OegT79dFg6kmGbliJfA0.1
            private final /* synthetic */ Thread $m$0(Runnable runnable) {
                return WeakExecutor.m117lambda$com_lumiyaviewer_lumiya_res_executors_WeakExecutor_1106((String) str, runnable);
            }

            @Override // java.util.concurrent.ThreadFactory
            public final Thread newThread(Runnable runnable) {
                return $m$0(runnable);
            }
        });
        this.usePriorities = true;
        Debug.Printf("Executor for %s: maxThreads %d", str, Integer.valueOf(i));
        allowCoreThreadTimeOut(true);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_res_executors_WeakExecutor_1106, reason: not valid java name */
    public static /* synthetic */ Thread m117lambda$com_lumiyaviewer_lumiya_res_executors_WeakExecutor_1106(String str, Runnable runnable) {
        Thread thread = new Thread(runnable, str);
        Debug.Printf("Creating thread %s got %d", str, Long.valueOf(thread.getId()));
        thread.setPriority(4);
        return thread;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_res_executors_WeakExecutor_531, reason: not valid java name */
    public static /* synthetic */ Thread m118lambda$com_lumiyaviewer_lumiya_res_executors_WeakExecutor_531(String str, Runnable runnable) {
        Thread thread = new Thread(runnable, str);
        Debug.Printf("Creating thread %s got %d", str, Long.valueOf(thread.getId()));
        thread.setPriority(4);
        return thread;
    }

    @Override // java.util.concurrent.AbstractExecutorService
    protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T t) {
        return this.usePriorities ? new ComparableFutureTask(runnable, t) : super.newTaskFor(runnable, t);
    }

    @Override // java.util.concurrent.AbstractExecutorService
    protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        return this.usePriorities ? new ComparableFutureTask(callable) : super.newTaskFor(callable);
    }
}
