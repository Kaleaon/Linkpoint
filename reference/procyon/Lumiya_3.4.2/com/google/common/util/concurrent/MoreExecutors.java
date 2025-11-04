// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.util.concurrent;

import java.util.concurrent.Delayed;
import java.util.concurrent.ScheduledFuture;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;
import javax.annotation.concurrent.GuardedBy;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.ArrayList;
import java.util.Iterator;
import com.google.common.collect.Queues;
import com.google.common.collect.Lists;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.Future;
import java.util.concurrent.BlockingQueue;
import com.google.common.base.Preconditions;
import java.util.concurrent.Callable;
import java.util.Collection;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.Executor;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.Beta;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible(emulated = true)
public final class MoreExecutors
{
    private MoreExecutors() {
    }
    
    @Beta
    @GwtIncompatible("TODO")
    public static void addDelayedShutdownHook(final ExecutorService executorService, final long n, final TimeUnit timeUnit) {
        new Application().addDelayedShutdownHook(executorService, n, timeUnit);
    }
    
    public static Executor directExecutor() {
        return DirectExecutor.INSTANCE;
    }
    
    @Beta
    @GwtIncompatible("concurrency")
    public static ExecutorService getExitingExecutorService(final ThreadPoolExecutor threadPoolExecutor) {
        return new Application().getExitingExecutorService(threadPoolExecutor);
    }
    
    @Beta
    @GwtIncompatible("TODO")
    public static ExecutorService getExitingExecutorService(final ThreadPoolExecutor threadPoolExecutor, final long n, final TimeUnit timeUnit) {
        return new Application().getExitingExecutorService(threadPoolExecutor, n, timeUnit);
    }
    
    @Beta
    @GwtIncompatible("TODO")
    public static ScheduledExecutorService getExitingScheduledExecutorService(final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor) {
        return new Application().getExitingScheduledExecutorService(scheduledThreadPoolExecutor);
    }
    
    @Beta
    @GwtIncompatible("TODO")
    public static ScheduledExecutorService getExitingScheduledExecutorService(final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor, final long n, final TimeUnit timeUnit) {
        return new Application().getExitingScheduledExecutorService(scheduledThreadPoolExecutor, n, timeUnit);
    }
    
    static <T> T invokeAnyImpl(ListeningExecutorService iterator, Collection<? extends Callable<T>> iterator2, final boolean b, long n) throws InterruptedException, ExecutionException, TimeoutException {
        Preconditions.checkNotNull(iterator);
        int size = ((Collection)iterator2).size();
        while (true) {
            while (true) {
                boolean b2 = false;
                Label_0021: {
                    if (size <= 0) {
                        b2 = false;
                        break Label_0021;
                    }
                    Label_0113: {
                        break Label_0113;
                        Iterator iterator3;
                        final ArrayList<Object> arrayListWithCapacity;
                        final LinkedBlockingQueue<Object> linkedBlockingQueue;
                        int n2;
                        Future future = null;
                        long nanoTime;
                        long nanoTime2 = 0L;
                        Object value;
                        ExecutionException ex2;
                        Label_0110_Outer:Label_0249_Outer:
                        while (true) {
                            while (true) {
                                Label_0344: {
                                    while (true) {
                                        Label_0260: {
                                            try {
                                                iterator3 = ((Collection)iterator2).iterator();
                                                arrayListWithCapacity.add(submitAndAddQueueListener(iterator, (Callable<Object>)iterator3.next(), (BlockingQueue<Future<Object>>)linkedBlockingQueue));
                                                n2 = size - 1;
                                                size = 1;
                                                while (true) {
                                                    future = linkedBlockingQueue.poll();
                                                    if (future == null) {
                                                        if (n2 <= 0) {
                                                            if (size == 0) {
                                                                break Label_0113;
                                                            }
                                                            if (!b) {
                                                                future = linkedBlockingQueue.take();
                                                            }
                                                            else {
                                                                future = linkedBlockingQueue.poll(n, TimeUnit.NANOSECONDS);
                                                                if (future == null) {
                                                                    break;
                                                                }
                                                                nanoTime = System.nanoTime();
                                                                n -= nanoTime - nanoTime2;
                                                                nanoTime2 = nanoTime;
                                                            }
                                                        }
                                                        else {
                                                            arrayListWithCapacity.add(submitAndAddQueueListener(iterator, (Callable<Object>)iterator3.next(), (BlockingQueue<Future<Object>>)linkedBlockingQueue));
                                                            --n2;
                                                            ++size;
                                                        }
                                                    }
                                                    if (future != null) {
                                                        break Label_0260;
                                                    }
                                                }
                                                throw new TimeoutException();
                                                b2 = true;
                                                break Label_0021;
                                                nanoTime2 = System.nanoTime();
                                                continue Label_0110_Outer;
                                            }
                                            finally {
                                                iterator2 = arrayListWithCapacity.iterator();
                                                if (((Iterator)iterator2).hasNext()) {
                                                    break Label_0344;
                                                }
                                            }
                                        }
                                        --size;
                                        try {
                                            value = future.get();
                                            iterator = (ListeningExecutorService)arrayListWithCapacity.iterator();
                                            while (((Iterator)iterator).hasNext()) {
                                                ((Iterator<Future>)iterator).next().cancel(true);
                                            }
                                            return (T)value;
                                        }
                                        catch (final ExecutionException ex) {
                                            continue Label_0249_Outer;
                                        }
                                        catch (final RuntimeException cause) {
                                            ex2 = new ExecutionException(cause);
                                            continue Label_0249_Outer;
                                        }
                                        break;
                                    }
                                    break;
                                }
                                ((Future)((Iterator<? extends Callable<T>>)iterator2).next()).cancel(true);
                                continue;
                            }
                        }
                    }
                    if (iterator2 == null) {
                        iterator2 = new ExecutionException((Throwable)null);
                    }
                    throw iterator2;
                }
                Preconditions.checkArgument(b2);
                final ArrayList<Object> arrayListWithCapacity = Lists.newArrayListWithCapacity(size);
                final LinkedBlockingQueue<Object> linkedBlockingQueue = Queues.newLinkedBlockingQueue();
                if (!b) {
                    final long nanoTime2 = 0L;
                    continue;
                }
                break;
            }
            continue;
        }
    }
    
    @GwtIncompatible("TODO")
    private static boolean isAppEngine() {
        boolean b = false;
        if (System.getProperty("com.google.appengine.runtime.environment") == null) {
            return false;
        }
        try {
            if (Class.forName("com.google.apphosting.api.ApiProxy").getMethod("getCurrentEnvironment", (Class<?>[])new Class[0]).invoke(null, new Object[0]) != null) {
                b = true;
            }
            return b;
        }
        catch (final ClassNotFoundException ex) {
            return false;
        }
        catch (final InvocationTargetException ex2) {
            return false;
        }
        catch (final IllegalAccessException ex3) {
            return false;
        }
        catch (final NoSuchMethodException ex4) {
            return false;
        }
    }
    
    @GwtIncompatible("TODO")
    public static ListeningExecutorService listeningDecorator(final ExecutorService executorService) {
        ListeningExecutorService listeningExecutorService;
        if (!(executorService instanceof ListeningExecutorService)) {
            if (!(executorService instanceof ScheduledExecutorService)) {
                listeningExecutorService = new ListeningDecorator(executorService);
            }
            else {
                listeningExecutorService = new ScheduledListeningDecorator((ScheduledExecutorService)executorService);
            }
        }
        else {
            listeningExecutorService = (ListeningExecutorService)executorService;
        }
        return listeningExecutorService;
    }
    
    @GwtIncompatible("TODO")
    public static ListeningScheduledExecutorService listeningDecorator(final ScheduledExecutorService scheduledExecutorService) {
        ListeningScheduledExecutorService listeningScheduledExecutorService;
        if (!(scheduledExecutorService instanceof ListeningScheduledExecutorService)) {
            listeningScheduledExecutorService = new ScheduledListeningDecorator(scheduledExecutorService);
        }
        else {
            listeningScheduledExecutorService = (ListeningScheduledExecutorService)scheduledExecutorService;
        }
        return listeningScheduledExecutorService;
    }
    
    @GwtIncompatible("TODO")
    public static ListeningExecutorService newDirectExecutorService() {
        return new DirectExecutorService();
    }
    
    @GwtIncompatible("concurrency")
    static Thread newThread(final String name, Runnable thread) {
        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(thread);
        thread = platformThreadFactory().newThread(thread);
        try {
            ((Thread)thread).setName(name);
            return (Thread)thread;
        }
        catch (final SecurityException ex) {
            return (Thread)thread;
        }
    }
    
    @Beta
    @GwtIncompatible("concurrency")
    public static ThreadFactory platformThreadFactory() {
        Label_0036: {
            if (!isAppEngine()) {
                break Label_0036;
            }
            try {
                return (ThreadFactory)Class.forName("com.google.appengine.api.ThreadManager").getMethod("currentRequestThreadFactory", (Class<?>[])new Class[0]).invoke(null, new Object[0]);
                return Executors.defaultThreadFactory();
            }
            catch (final IllegalAccessException cause) {
                throw new RuntimeException("Couldn't invoke ThreadManager.currentRequestThreadFactory", cause);
            }
            catch (final ClassNotFoundException cause2) {
                throw new RuntimeException("Couldn't invoke ThreadManager.currentRequestThreadFactory", cause2);
            }
            catch (final NoSuchMethodException cause3) {
                throw new RuntimeException("Couldn't invoke ThreadManager.currentRequestThreadFactory", cause3);
            }
            catch (final InvocationTargetException ex) {
                throw Throwables.propagate(ex.getCause());
            }
        }
    }
    
    @GwtIncompatible("concurrency")
    static Executor renamingDecorator(final Executor executor, final Supplier<String> supplier) {
        Preconditions.checkNotNull(executor);
        Preconditions.checkNotNull(supplier);
        if (!isAppEngine()) {
            return new Executor() {
                @Override
                public void execute(final Runnable runnable) {
                    executor.execute(Callables.threadRenaming(runnable, supplier));
                }
            };
        }
        return executor;
    }
    
    @GwtIncompatible("concurrency")
    static ExecutorService renamingDecorator(final ExecutorService executorService, final Supplier<String> supplier) {
        Preconditions.checkNotNull(executorService);
        Preconditions.checkNotNull(supplier);
        if (!isAppEngine()) {
            return new WrappingExecutorService(executorService) {
                @Override
                protected Runnable wrapTask(final Runnable runnable) {
                    return Callables.threadRenaming(runnable, supplier);
                }
                
                @Override
                protected <T> Callable<T> wrapTask(final Callable<T> callable) {
                    return Callables.threadRenaming(callable, supplier);
                }
            };
        }
        return executorService;
    }
    
    @GwtIncompatible("concurrency")
    static ScheduledExecutorService renamingDecorator(final ScheduledExecutorService scheduledExecutorService, final Supplier<String> supplier) {
        Preconditions.checkNotNull(scheduledExecutorService);
        Preconditions.checkNotNull(supplier);
        if (!isAppEngine()) {
            return new WrappingScheduledExecutorService(scheduledExecutorService) {
                @Override
                protected Runnable wrapTask(final Runnable runnable) {
                    return Callables.threadRenaming(runnable, supplier);
                }
                
                @Override
                protected <T> Callable<T> wrapTask(final Callable<T> callable) {
                    return Callables.threadRenaming(callable, supplier);
                }
            };
        }
        return scheduledExecutorService;
    }
    
    @Deprecated
    @GwtIncompatible("TODO")
    public static ListeningExecutorService sameThreadExecutor() {
        return new DirectExecutorService();
    }
    
    @Beta
    @GwtIncompatible("concurrency")
    public static boolean shutdownAndAwaitTermination(final ExecutorService executorService, long sourceDuration, final TimeUnit sourceUnit) {
        Preconditions.checkNotNull(sourceUnit);
        executorService.shutdown();
        while (true) {
            try {
                sourceDuration = TimeUnit.NANOSECONDS.convert(sourceDuration, sourceUnit) / 2L;
                if (!executorService.awaitTermination(sourceDuration, TimeUnit.NANOSECONDS)) {
                    executorService.shutdownNow();
                    executorService.awaitTermination(sourceDuration, TimeUnit.NANOSECONDS);
                }
                return executorService.isTerminated();
            }
            catch (final InterruptedException ex) {
                Thread.currentThread().interrupt();
                executorService.shutdownNow();
                return executorService.isTerminated();
            }
            continue;
        }
    }
    
    @GwtIncompatible("TODO")
    private static <T> ListenableFuture<T> submitAndAddQueueListener(final ListeningExecutorService listeningExecutorService, final Callable<T> callable, final BlockingQueue<Future<T>> blockingQueue) {
        final ListenableFuture<T> submit = listeningExecutorService.submit(callable);
        submit.addListener(new Runnable() {
            @Override
            public void run() {
                blockingQueue.add(submit);
            }
        }, directExecutor());
        return submit;
    }
    
    @GwtIncompatible("TODO")
    private static void useDaemonThreadFactory(final ThreadPoolExecutor threadPoolExecutor) {
        threadPoolExecutor.setThreadFactory(new ThreadFactoryBuilder().setDaemon(true).setThreadFactory(threadPoolExecutor.getThreadFactory()).build());
    }
    
    @GwtIncompatible("TODO")
    @VisibleForTesting
    static class Application
    {
        final void addDelayedShutdownHook(final ExecutorService obj, final long n, final TimeUnit timeUnit) {
            Preconditions.checkNotNull(obj);
            Preconditions.checkNotNull(timeUnit);
            this.addShutdownHook(MoreExecutors.newThread("DelayedShutdownHook-for-" + obj, new Runnable() {
                @Override
                public void run() {
                    try {
                        obj.shutdown();
                        obj.awaitTermination(n, timeUnit);
                    }
                    catch (final InterruptedException ex) {}
                }
            }));
        }
        
        @VisibleForTesting
        void addShutdownHook(final Thread hook) {
            Runtime.getRuntime().addShutdownHook(hook);
        }
        
        final ExecutorService getExitingExecutorService(final ThreadPoolExecutor threadPoolExecutor) {
            return this.getExitingExecutorService(threadPoolExecutor, 120L, TimeUnit.SECONDS);
        }
        
        final ExecutorService getExitingExecutorService(final ThreadPoolExecutor executor, final long n, final TimeUnit timeUnit) {
            useDaemonThreadFactory(executor);
            final ExecutorService unconfigurableExecutorService = Executors.unconfigurableExecutorService(executor);
            this.addDelayedShutdownHook(unconfigurableExecutorService, n, timeUnit);
            return unconfigurableExecutorService;
        }
        
        final ScheduledExecutorService getExitingScheduledExecutorService(final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor) {
            return this.getExitingScheduledExecutorService(scheduledThreadPoolExecutor, 120L, TimeUnit.SECONDS);
        }
        
        final ScheduledExecutorService getExitingScheduledExecutorService(final ScheduledThreadPoolExecutor executor, final long n, final TimeUnit timeUnit) {
            useDaemonThreadFactory(executor);
            final ScheduledExecutorService unconfigurableScheduledExecutorService = Executors.unconfigurableScheduledExecutorService(executor);
            this.addDelayedShutdownHook(unconfigurableScheduledExecutorService, n, timeUnit);
            return unconfigurableScheduledExecutorService;
        }
    }
    
    private enum DirectExecutor implements Executor
    {
        INSTANCE;
        
        @Override
        public void execute(final Runnable runnable) {
            runnable.run();
        }
        
        @Override
        public String toString() {
            return "MoreExecutors.directExecutor()";
        }
    }
    
    @GwtIncompatible("TODO")
    private static final class DirectExecutorService extends AbstractListeningExecutorService
    {
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
                final int runningTasks = this.runningTasks - 1;
                this.runningTasks = runningTasks;
                if (runningTasks == 0) {
                    this.lock.notifyAll();
                }
            }
        }
        
        private void startTask() {
            synchronized (this.lock) {
                if (!this.shutdown) {
                    ++this.runningTasks;
                    return;
                }
                throw new RejectedExecutionException("Executor already shutdown");
            }
        }
        
        @Override
        public boolean awaitTermination(long nanos, final TimeUnit timeUnit) throws InterruptedException {
            nanos = timeUnit.toNanos(nanos);
            synchronized (this.lock) {
                while (!this.shutdown || this.runningTasks != 0) {
                    int n;
                    if (nanos > 0L) {
                        n = 1;
                    }
                    else {
                        n = 0;
                    }
                    if (n == 0) {
                        return false;
                    }
                    final long nanoTime = System.nanoTime();
                    TimeUnit.NANOSECONDS.timedWait(this.lock, nanos);
                    nanos -= System.nanoTime() - nanoTime;
                }
                return true;
            }
        }
        
        @Override
        public void execute(final Runnable runnable) {
            this.startTask();
            try {
                runnable.run();
            }
            finally {
                this.endTask();
            }
        }
        
        @Override
        public boolean isShutdown() {
            synchronized (this.lock) {
                return this.shutdown;
            }
        }
        
        @Override
        public boolean isTerminated() {
            boolean b = false;
            synchronized (this.lock) {
                if (this.shutdown && this.runningTasks == 0) {
                    b = true;
                }
                return b;
            }
        }
        
        @Override
        public void shutdown() {
            synchronized (this.lock) {
                this.shutdown = true;
                if (this.runningTasks == 0) {
                    this.lock.notifyAll();
                }
            }
        }
        
        @Override
        public List<Runnable> shutdownNow() {
            this.shutdown();
            return Collections.emptyList();
        }
    }
    
    @GwtIncompatible("TODO")
    private static class ListeningDecorator extends AbstractListeningExecutorService
    {
        private final ExecutorService delegate;
        
        ListeningDecorator(final ExecutorService executorService) {
            this.delegate = Preconditions.checkNotNull(executorService);
        }
        
        @Override
        public final boolean awaitTermination(final long n, final TimeUnit timeUnit) throws InterruptedException {
            return this.delegate.awaitTermination(n, timeUnit);
        }
        
        @Override
        public final void execute(final Runnable runnable) {
            this.delegate.execute(runnable);
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
    }
    
    @GwtIncompatible("TODO")
    private static final class ScheduledListeningDecorator extends ListeningDecorator implements ListeningScheduledExecutorService
    {
        final ScheduledExecutorService delegate;
        
        ScheduledListeningDecorator(final ScheduledExecutorService scheduledExecutorService) {
            super(scheduledExecutorService);
            this.delegate = Preconditions.checkNotNull(scheduledExecutorService);
        }
        
        @Override
        public ListenableScheduledFuture<?> schedule(final Runnable runnable, final long n, final TimeUnit timeUnit) {
            final TrustedListenableFutureTask<Object> create = TrustedListenableFutureTask.create(runnable, (Object)null);
            return new ListenableScheduledTask<Object>(create, this.delegate.schedule(create, n, timeUnit));
        }
        
        @Override
        public <V> ListenableScheduledFuture<V> schedule(final Callable<V> callable, final long n, final TimeUnit timeUnit) {
            final TrustedListenableFutureTask<V> create = TrustedListenableFutureTask.create(callable);
            return new ListenableScheduledTask<V>((ListenableFuture<Object>)create, this.delegate.schedule(create, n, timeUnit));
        }
        
        @Override
        public ListenableScheduledFuture<?> scheduleAtFixedRate(final Runnable runnable, final long n, final long n2, final TimeUnit timeUnit) {
            final NeverSuccessfulListenableFutureTask neverSuccessfulListenableFutureTask = new NeverSuccessfulListenableFutureTask(runnable);
            return new ListenableScheduledTask<Object>((ListenableFuture<Object>)neverSuccessfulListenableFutureTask, this.delegate.scheduleAtFixedRate(neverSuccessfulListenableFutureTask, n, n2, timeUnit));
        }
        
        @Override
        public ListenableScheduledFuture<?> scheduleWithFixedDelay(final Runnable runnable, final long n, final long n2, final TimeUnit timeUnit) {
            final NeverSuccessfulListenableFutureTask neverSuccessfulListenableFutureTask = new NeverSuccessfulListenableFutureTask(runnable);
            return new ListenableScheduledTask<Object>((ListenableFuture<Object>)neverSuccessfulListenableFutureTask, this.delegate.scheduleWithFixedDelay(neverSuccessfulListenableFutureTask, n, n2, timeUnit));
        }
        
        private static final class ListenableScheduledTask<V> extends SimpleForwardingListenableFuture<V> implements ListenableScheduledFuture<V>
        {
            private final ScheduledFuture<?> scheduledDelegate;
            
            public ListenableScheduledTask(final ListenableFuture<V> listenableFuture, final ScheduledFuture<?> scheduledDelegate) {
                super(listenableFuture);
                this.scheduledDelegate = scheduledDelegate;
            }
            
            @Override
            public boolean cancel(final boolean b) {
                final boolean cancel = super.cancel(b);
                if (cancel) {
                    this.scheduledDelegate.cancel(b);
                }
                return cancel;
            }
            
            @Override
            public int compareTo(final Delayed delayed) {
                return this.scheduledDelegate.compareTo(delayed);
            }
            
            @Override
            public long getDelay(final TimeUnit timeUnit) {
                return this.scheduledDelegate.getDelay(timeUnit);
            }
        }
        
        @GwtIncompatible("TODO")
        private static final class NeverSuccessfulListenableFutureTask extends AbstractFuture<Void> implements Runnable
        {
            private final Runnable delegate;
            
            public NeverSuccessfulListenableFutureTask(final Runnable runnable) {
                this.delegate = Preconditions.checkNotNull(runnable);
            }
            
            @Override
            public void run() {
                try {
                    this.delegate.run();
                }
                catch (final Throwable exception) {
                    this.setException(exception);
                    throw Throwables.propagate(exception);
                }
            }
        }
    }
}
