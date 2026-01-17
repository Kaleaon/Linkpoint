package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.util.concurrent.ForwardingListenableFuture;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.annotation.concurrent.GuardedBy;
@GwtCompatible(emulated = true)
/* loaded from: classes.dex */
public final class MoreExecutors {

    @VisibleForTesting
    @GwtIncompatible("TODO")
    /* loaded from: classes.dex */
    static class Application {
        Application() {
        }

        final void addDelayedShutdownHook(final ExecutorService executorService, final long j, final TimeUnit timeUnit) {
            Preconditions.checkNotNull(executorService);
            Preconditions.checkNotNull(timeUnit);
            addShutdownHook(MoreExecutors.newThread("DelayedShutdownHook-for-" + executorService, new Runnable() { // from class: com.google.common.util.concurrent.MoreExecutors.Application.1
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        executorService.shutdown();
                        executorService.awaitTermination(j, timeUnit);
                    } catch (InterruptedException e) {
                    }
                }
            }));
        }

        @VisibleForTesting
        void addShutdownHook(Thread thread) {
            Runtime.getRuntime().addShutdownHook(thread);
        }

        final ExecutorService getExitingExecutorService(ThreadPoolExecutor threadPoolExecutor) {
            return getExitingExecutorService(threadPoolExecutor, 120L, TimeUnit.SECONDS);
        }

        final ExecutorService getExitingExecutorService(ThreadPoolExecutor threadPoolExecutor, long j, TimeUnit timeUnit) {
            MoreExecutors.useDaemonThreadFactory(threadPoolExecutor);
            ExecutorService unconfigurableExecutorService = Executors.unconfigurableExecutorService(threadPoolExecutor);
            addDelayedShutdownHook(unconfigurableExecutorService, j, timeUnit);
            return unconfigurableExecutorService;
        }

        final ScheduledExecutorService getExitingScheduledExecutorService(ScheduledThreadPoolExecutor scheduledThreadPoolExecutor) {
            return getExitingScheduledExecutorService(scheduledThreadPoolExecutor, 120L, TimeUnit.SECONDS);
        }

        final ScheduledExecutorService getExitingScheduledExecutorService(ScheduledThreadPoolExecutor scheduledThreadPoolExecutor, long j, TimeUnit timeUnit) {
            MoreExecutors.useDaemonThreadFactory(scheduledThreadPoolExecutor);
            ScheduledExecutorService unconfigurableScheduledExecutorService = Executors.unconfigurableScheduledExecutorService(scheduledThreadPoolExecutor);
            addDelayedShutdownHook(unconfigurableScheduledExecutorService, j, timeUnit);
            return unconfigurableScheduledExecutorService;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public enum DirectExecutor implements Executor {
        INSTANCE;

        @Override // java.util.concurrent.Executor
        public void execute(Runnable runnable) {
            runnable.run();
        }

        @Override // java.lang.Enum
        public String toString() {
            return "MoreExecutors.directExecutor()";
        }
    }

    @GwtIncompatible("TODO")
    /* loaded from: classes.dex */
    private static final class DirectExecutorService extends AbstractListeningExecutorService {
        private final Object lock;
        @GuardedBy("lock")
        private int runningTasks;
        @GuardedBy("lock")
        private boolean shutdown;

        private DirectExecutorService() {
            this.lock = new Object();
            this.runningTasks = 0;
            this.shutdown = false;
        }

        private void endTask() {
            synchronized (this.lock) {
                int i = this.runningTasks - 1;
                this.runningTasks = i;
                if (i == 0) {
                    this.lock.notifyAll();
                }
            }
        }

        private void startTask() {
            synchronized (this.lock) {
                if (this.shutdown) {
                    throw new RejectedExecutionException("Executor already shutdown");
                }
                this.runningTasks++;
            }
        }

        @Override // java.util.concurrent.ExecutorService
        public boolean awaitTermination(long j, TimeUnit timeUnit) throws InterruptedException {
            long nanos = timeUnit.toNanos(j);
            synchronized (this.lock) {
                while (true) {
                    if (this.shutdown && this.runningTasks == 0) {
                        return true;
                    }
                    if (!(nanos > 0)) {
                        return false;
                    }
                    long nanoTime = System.nanoTime();
                    TimeUnit.NANOSECONDS.timedWait(this.lock, nanos);
                    nanos -= System.nanoTime() - nanoTime;
                }
            }
        }

        @Override // java.util.concurrent.Executor
        public void execute(Runnable runnable) {
            startTask();
            try {
                runnable.run();
            } finally {
                endTask();
            }
        }

        @Override // java.util.concurrent.ExecutorService
        public boolean isShutdown() {
            boolean z;
            synchronized (this.lock) {
                z = this.shutdown;
            }
            return z;
        }

        @Override // java.util.concurrent.ExecutorService
        public boolean isTerminated() {
            boolean z = false;
            synchronized (this.lock) {
                if (this.shutdown && this.runningTasks == 0) {
                    z = true;
                }
            }
            return z;
        }

        @Override // java.util.concurrent.ExecutorService
        public void shutdown() {
            synchronized (this.lock) {
                this.shutdown = true;
                if (this.runningTasks == 0) {
                    this.lock.notifyAll();
                }
            }
        }

        @Override // java.util.concurrent.ExecutorService
        public List<Runnable> shutdownNow() {
            shutdown();
            return Collections.emptyList();
        }
    }

    @GwtIncompatible("TODO")
    /* loaded from: classes.dex */
    private static class ListeningDecorator extends AbstractListeningExecutorService {
        private final ExecutorService delegate;

        ListeningDecorator(ExecutorService executorService) {
            this.delegate = (ExecutorService) Preconditions.checkNotNull(executorService);
        }

        @Override // java.util.concurrent.ExecutorService
        public final boolean awaitTermination(long j, TimeUnit timeUnit) throws InterruptedException {
            return this.delegate.awaitTermination(j, timeUnit);
        }

        @Override // java.util.concurrent.Executor
        public final void execute(Runnable runnable) {
            this.delegate.execute(runnable);
        }

        @Override // java.util.concurrent.ExecutorService
        public final boolean isShutdown() {
            return this.delegate.isShutdown();
        }

        @Override // java.util.concurrent.ExecutorService
        public final boolean isTerminated() {
            return this.delegate.isTerminated();
        }

        @Override // java.util.concurrent.ExecutorService
        public final void shutdown() {
            this.delegate.shutdown();
        }

        @Override // java.util.concurrent.ExecutorService
        public final List<Runnable> shutdownNow() {
            return this.delegate.shutdownNow();
        }
    }

    @GwtIncompatible("TODO")
    /* loaded from: classes.dex */
    private static final class ScheduledListeningDecorator extends ListeningDecorator implements ListeningScheduledExecutorService {
        final ScheduledExecutorService delegate;

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public static final class ListenableScheduledTask<V> extends ForwardingListenableFuture.SimpleForwardingListenableFuture<V> implements ListenableScheduledFuture<V> {
            private final ScheduledFuture<?> scheduledDelegate;

            public ListenableScheduledTask(ListenableFuture<V> listenableFuture, ScheduledFuture<?> scheduledFuture) {
                super(listenableFuture);
                this.scheduledDelegate = scheduledFuture;
            }

            @Override // com.google.common.util.concurrent.ForwardingFuture, java.util.concurrent.Future
            public boolean cancel(boolean z) {
                boolean cancel = super.cancel(z);
                if (cancel) {
                    this.scheduledDelegate.cancel(z);
                }
                return cancel;
            }

            @Override // java.lang.Comparable
            public int compareTo(Delayed delayed) {
                return this.scheduledDelegate.compareTo(delayed);
            }

            @Override // java.util.concurrent.Delayed
            public long getDelay(TimeUnit timeUnit) {
                return this.scheduledDelegate.getDelay(timeUnit);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        @GwtIncompatible("TODO")
        /* loaded from: classes.dex */
        public static final class NeverSuccessfulListenableFutureTask extends AbstractFuture<Void> implements Runnable {
            private final Runnable delegate;

            public NeverSuccessfulListenableFutureTask(Runnable runnable) {
                this.delegate = (Runnable) Preconditions.checkNotNull(runnable);
            }

            @Override // java.lang.Runnable
            public void run() {
                try {
                    this.delegate.run();
                } catch (Throwable th) {
                    setException(th);
                    throw Throwables.propagate(th);
                }
            }
        }

        ScheduledListeningDecorator(ScheduledExecutorService scheduledExecutorService) {
            super(scheduledExecutorService);
            this.delegate = (ScheduledExecutorService) Preconditions.checkNotNull(scheduledExecutorService);
        }

        @Override // java.util.concurrent.ScheduledExecutorService
        public ListenableScheduledFuture<?> schedule(Runnable runnable, long j, TimeUnit timeUnit) {
            TrustedListenableFutureTask create = TrustedListenableFutureTask.create(runnable, null);
            return new ListenableScheduledTask(create, this.delegate.schedule(create, j, timeUnit));
        }

        @Override // java.util.concurrent.ScheduledExecutorService
        public <V> ListenableScheduledFuture<V> schedule(Callable<V> callable, long j, TimeUnit timeUnit) {
            TrustedListenableFutureTask create = TrustedListenableFutureTask.create(callable);
            return new ListenableScheduledTask(create, this.delegate.schedule(create, j, timeUnit));
        }

        @Override // java.util.concurrent.ScheduledExecutorService
        public ListenableScheduledFuture<?> scheduleAtFixedRate(Runnable runnable, long j, long j2, TimeUnit timeUnit) {
            NeverSuccessfulListenableFutureTask neverSuccessfulListenableFutureTask = new NeverSuccessfulListenableFutureTask(runnable);
            return new ListenableScheduledTask(neverSuccessfulListenableFutureTask, this.delegate.scheduleAtFixedRate(neverSuccessfulListenableFutureTask, j, j2, timeUnit));
        }

        @Override // java.util.concurrent.ScheduledExecutorService
        public ListenableScheduledFuture<?> scheduleWithFixedDelay(Runnable runnable, long j, long j2, TimeUnit timeUnit) {
            NeverSuccessfulListenableFutureTask neverSuccessfulListenableFutureTask = new NeverSuccessfulListenableFutureTask(runnable);
            return new ListenableScheduledTask(neverSuccessfulListenableFutureTask, this.delegate.scheduleWithFixedDelay(neverSuccessfulListenableFutureTask, j, j2, timeUnit));
        }
    }

    private MoreExecutors() {
    }

    @Beta
    @GwtIncompatible("TODO")
    public static void addDelayedShutdownHook(ExecutorService executorService, long j, TimeUnit timeUnit) {
        new Application().addDelayedShutdownHook(executorService, j, timeUnit);
    }

    public static Executor directExecutor() {
        return DirectExecutor.INSTANCE;
    }

    @Beta
    @GwtIncompatible("concurrency")
    public static ExecutorService getExitingExecutorService(ThreadPoolExecutor threadPoolExecutor) {
        return new Application().getExitingExecutorService(threadPoolExecutor);
    }

    @Beta
    @GwtIncompatible("TODO")
    public static ExecutorService getExitingExecutorService(ThreadPoolExecutor threadPoolExecutor, long j, TimeUnit timeUnit) {
        return new Application().getExitingExecutorService(threadPoolExecutor, j, timeUnit);
    }

    @Beta
    @GwtIncompatible("TODO")
    public static ScheduledExecutorService getExitingScheduledExecutorService(ScheduledThreadPoolExecutor scheduledThreadPoolExecutor) {
        return new Application().getExitingScheduledExecutorService(scheduledThreadPoolExecutor);
    }

    @Beta
    @GwtIncompatible("TODO")
    public static ScheduledExecutorService getExitingScheduledExecutorService(ScheduledThreadPoolExecutor scheduledThreadPoolExecutor, long j, TimeUnit timeUnit) {
        return new Application().getExitingScheduledExecutorService(scheduledThreadPoolExecutor, j, timeUnit);
    }

    static <T> T invokeAnyImpl(ListeningExecutorService listeningExecutorService, Collection<? extends Callable<T>> collection, boolean z, long j) throws InterruptedException, ExecutionException, TimeoutException {
        long nanoTime;
        int i;
        long j2;
        int i2;
        Future future;
        Preconditions.checkNotNull(listeningExecutorService);
        int size = collection.size();
        Preconditions.checkArgument(size > 0);
        ArrayList<Future> newArrayListWithCapacity = Lists.newArrayListWithCapacity(size);
        LinkedBlockingQueue newLinkedBlockingQueue = Queues.newLinkedBlockingQueue();
        if (z) {
            try {
                nanoTime = System.nanoTime();
            } finally {
                for (Future future2 : newArrayListWithCapacity) {
                    future2.cancel(true);
                }
            }
        } else {
            nanoTime = 0;
        }
        Iterator<? extends Callable<T>> it = collection.iterator();
        newArrayListWithCapacity.add(submitAndAddQueueListener(listeningExecutorService, it.next(), newLinkedBlockingQueue));
        int i3 = size - 1;
        long j3 = j;
        long j4 = nanoTime;
        ExecutionException executionException = null;
        int i4 = 1;
        long j5 = j4;
        while (true) {
            Future future3 = (Future) newLinkedBlockingQueue.poll();
            if (future3 != null) {
                j2 = j3;
                int i5 = i4;
                i = i3;
                future = future3;
                i2 = i5;
            } else if (i3 > 0) {
                int i6 = i3 - 1;
                newArrayListWithCapacity.add(submitAndAddQueueListener(listeningExecutorService, it.next(), newLinkedBlockingQueue));
                int i7 = i4 + 1;
                i = i6;
                j2 = j3;
                i2 = i7;
                future = future3;
            } else if (i4 == 0) {
                if (executionException != null) {
                    throw executionException;
                }
                throw new ExecutionException((Throwable) null);
            } else if (z) {
                Future future4 = (Future) newLinkedBlockingQueue.poll(j3, TimeUnit.NANOSECONDS);
                if (future4 == null) {
                    throw new TimeoutException();
                }
                long nanoTime2 = System.nanoTime();
                i2 = i4;
                i = i3;
                future = future4;
                j2 = j3 - (nanoTime2 - j5);
                j5 = nanoTime2;
            } else {
                j2 = j3;
                int i8 = i4;
                i = i3;
                future = (Future) newLinkedBlockingQueue.take();
                i2 = i8;
            }
            if (future == null) {
                e = executionException;
            } else {
                i2--;
                try {
                    return (T) future.get();
                } catch (RuntimeException e) {
                    e = new ExecutionException(e);
                } catch (ExecutionException e2) {
                    e = e2;
                }
            }
            executionException = e;
            j3 = j2;
            i3 = i;
            i4 = i2;
        }
    }

    @GwtIncompatible("TODO")
    private static boolean isAppEngine() {
        if (System.getProperty("com.google.appengine.runtime.environment") != null) {
            try {
                return Class.forName("com.google.apphosting.api.ApiProxy").getMethod("getCurrentEnvironment", new Class[0]).invoke(null, new Object[0]) != null;
            } catch (ClassNotFoundException e) {
                return false;
            } catch (IllegalAccessException e2) {
                return false;
            } catch (NoSuchMethodException e3) {
                return false;
            } catch (InvocationTargetException e4) {
                return false;
            }
        }
        return false;
    }

    @GwtIncompatible("TODO")
    public static ListeningExecutorService listeningDecorator(ExecutorService executorService) {
        if (executorService instanceof ListeningExecutorService) {
            return (ListeningExecutorService) executorService;
        }
        return !(executorService instanceof ScheduledExecutorService) ? new ListeningDecorator(executorService) : new ScheduledListeningDecorator((ScheduledExecutorService) executorService);
    }

    @GwtIncompatible("TODO")
    public static ListeningScheduledExecutorService listeningDecorator(ScheduledExecutorService scheduledExecutorService) {
        return !(scheduledExecutorService instanceof ListeningScheduledExecutorService) ? new ScheduledListeningDecorator(scheduledExecutorService) : (ListeningScheduledExecutorService) scheduledExecutorService;
    }

    @GwtIncompatible("TODO")
    public static ListeningExecutorService newDirectExecutorService() {
        return new DirectExecutorService();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @GwtIncompatible("concurrency")
    public static Thread newThread(String str, Runnable runnable) {
        Preconditions.checkNotNull(str);
        Preconditions.checkNotNull(runnable);
        Thread newThread = platformThreadFactory().newThread(runnable);
        try {
            newThread.setName(str);
        } catch (SecurityException e) {
        }
        return newThread;
    }

    @Beta
    @GwtIncompatible("concurrency")
    public static ThreadFactory platformThreadFactory() {
        if (isAppEngine()) {
            try {
                return (ThreadFactory) Class.forName("com.google.appengine.api.ThreadManager").getMethod("currentRequestThreadFactory", new Class[0]).invoke(null, new Object[0]);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Couldn't invoke ThreadManager.currentRequestThreadFactory", e);
            } catch (IllegalAccessException e2) {
                throw new RuntimeException("Couldn't invoke ThreadManager.currentRequestThreadFactory", e2);
            } catch (NoSuchMethodException e3) {
                throw new RuntimeException("Couldn't invoke ThreadManager.currentRequestThreadFactory", e3);
            } catch (InvocationTargetException e4) {
                throw Throwables.propagate(e4.getCause());
            }
        }
        return Executors.defaultThreadFactory();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @GwtIncompatible("concurrency")
    public static Executor renamingDecorator(final Executor executor, final Supplier<String> supplier) {
        Preconditions.checkNotNull(executor);
        Preconditions.checkNotNull(supplier);
        return !isAppEngine() ? new Executor() { // from class: com.google.common.util.concurrent.MoreExecutors.2
            @Override // java.util.concurrent.Executor
            public void execute(Runnable runnable) {
                executor.execute(Callables.threadRenaming(runnable, supplier));
            }
        } : executor;
    }

    @GwtIncompatible("concurrency")
    static ExecutorService renamingDecorator(ExecutorService executorService, final Supplier<String> supplier) {
        Preconditions.checkNotNull(executorService);
        Preconditions.checkNotNull(supplier);
        return !isAppEngine() ? new WrappingExecutorService(executorService) { // from class: com.google.common.util.concurrent.MoreExecutors.3
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.common.util.concurrent.WrappingExecutorService
            public Runnable wrapTask(Runnable runnable) {
                return Callables.threadRenaming(runnable, supplier);
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.common.util.concurrent.WrappingExecutorService
            public <T> Callable<T> wrapTask(Callable<T> callable) {
                return Callables.threadRenaming(callable, supplier);
            }
        } : executorService;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @GwtIncompatible("concurrency")
    public static ScheduledExecutorService renamingDecorator(ScheduledExecutorService scheduledExecutorService, final Supplier<String> supplier) {
        Preconditions.checkNotNull(scheduledExecutorService);
        Preconditions.checkNotNull(supplier);
        return !isAppEngine() ? new WrappingScheduledExecutorService(scheduledExecutorService) { // from class: com.google.common.util.concurrent.MoreExecutors.4
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.common.util.concurrent.WrappingExecutorService
            public Runnable wrapTask(Runnable runnable) {
                return Callables.threadRenaming(runnable, supplier);
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.common.util.concurrent.WrappingExecutorService
            public <T> Callable<T> wrapTask(Callable<T> callable) {
                return Callables.threadRenaming(callable, supplier);
            }
        } : scheduledExecutorService;
    }

    @GwtIncompatible("TODO")
    @Deprecated
    public static ListeningExecutorService sameThreadExecutor() {
        return new DirectExecutorService();
    }

    @Beta
    @GwtIncompatible("concurrency")
    public static boolean shutdownAndAwaitTermination(ExecutorService executorService, long j, TimeUnit timeUnit) {
        Preconditions.checkNotNull(timeUnit);
        executorService.shutdown();
        try {
            long convert = TimeUnit.NANOSECONDS.convert(j, timeUnit) / 2;
            if (!executorService.awaitTermination(convert, TimeUnit.NANOSECONDS)) {
                executorService.shutdownNow();
                executorService.awaitTermination(convert, TimeUnit.NANOSECONDS);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            executorService.shutdownNow();
        }
        return executorService.isTerminated();
    }

    @GwtIncompatible("TODO")
    private static <T> ListenableFuture<T> submitAndAddQueueListener(ListeningExecutorService listeningExecutorService, Callable<T> callable, final BlockingQueue<Future<T>> blockingQueue) {
        final ListenableFuture<T> submit = listeningExecutorService.submit((Callable) callable);
        submit.addListener(new Runnable() { // from class: com.google.common.util.concurrent.MoreExecutors.1
            @Override // java.lang.Runnable
            public void run() {
                blockingQueue.add(submit);
            }
        }, directExecutor());
        return submit;
    }

    /* JADX INFO: Access modifiers changed from: private */
    @GwtIncompatible("TODO")
    public static void useDaemonThreadFactory(ThreadPoolExecutor threadPoolExecutor) {
        threadPoolExecutor.setThreadFactory(new ThreadFactoryBuilder().setDaemon(true).setThreadFactory(threadPoolExecutor.getThreadFactory()).build());
    }
}
