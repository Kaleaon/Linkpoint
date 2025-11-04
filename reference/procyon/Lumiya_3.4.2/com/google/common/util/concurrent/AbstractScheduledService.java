// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.util.concurrent;

import java.util.logging.Level;
import com.google.common.base.Supplier;
import com.google.common.base.Preconditions;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.concurrent.GuardedBy;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executor;
import java.util.logging.Logger;
import com.google.common.annotations.Beta;

@Beta
public abstract class AbstractScheduledService implements Service
{
    private static final Logger logger;
    private final AbstractService delegate;
    
    static {
        logger = Logger.getLogger(AbstractScheduledService.class.getName());
    }
    
    protected AbstractScheduledService() {
        this.delegate = new ServiceDelegate();
    }
    
    @Override
    public final void addListener(final Listener listener, final Executor executor) {
        this.delegate.addListener(listener, executor);
    }
    
    @Override
    public final void awaitRunning() {
        this.delegate.awaitRunning();
    }
    
    @Override
    public final void awaitRunning(final long n, final TimeUnit timeUnit) throws TimeoutException {
        this.delegate.awaitRunning(n, timeUnit);
    }
    
    @Override
    public final void awaitTerminated() {
        this.delegate.awaitTerminated();
    }
    
    @Override
    public final void awaitTerminated(final long n, final TimeUnit timeUnit) throws TimeoutException {
        this.delegate.awaitTerminated(n, timeUnit);
    }
    
    protected ScheduledExecutorService executor() {
        final ScheduledExecutorService singleThreadScheduledExecutor = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryImpl());
        this.addListener(new Listener() {
            @Override
            public void failed(final State state, final Throwable t) {
                singleThreadScheduledExecutor.shutdown();
            }
            
            @Override
            public void terminated(final State state) {
                singleThreadScheduledExecutor.shutdown();
            }
        }, MoreExecutors.directExecutor());
        return singleThreadScheduledExecutor;
    }
    
    @Override
    public final Throwable failureCause() {
        return this.delegate.failureCause();
    }
    
    @Override
    public final boolean isRunning() {
        return this.delegate.isRunning();
    }
    
    protected abstract void runOneIteration() throws Exception;
    
    protected abstract Scheduler scheduler();
    
    protected String serviceName() {
        return this.getClass().getSimpleName();
    }
    
    protected void shutDown() throws Exception {
    }
    
    @Override
    public final Service startAsync() {
        this.delegate.startAsync();
        return this;
    }
    
    protected void startUp() throws Exception {
    }
    
    @Override
    public final State state() {
        return this.delegate.state();
    }
    
    @Override
    public final Service stopAsync() {
        this.delegate.stopAsync();
        return this;
    }
    
    @Override
    public String toString() {
        return this.serviceName() + " [" + this.state() + "]";
    }
    
    class ThreadFactoryImpl implements ThreadFactory
    {
        @Override
        public Thread newThread(final Runnable runnable) {
            return MoreExecutors.newThread(AbstractScheduledService.this.serviceName(), runnable);
        }
    }
    
    @Beta
    public abstract static class CustomScheduler extends Scheduler
    {
        protected abstract Schedule getNextSchedule() throws Exception;
        
        @Override
        final Future<?> schedule(final AbstractService abstractService, final ScheduledExecutorService scheduledExecutorService, final Runnable runnable) {
            final ReschedulableCallable reschedulableCallable = new ReschedulableCallable(abstractService, scheduledExecutorService, runnable);
            reschedulableCallable.reschedule();
            return reschedulableCallable;
        }
        
        private class ReschedulableCallable extends ForwardingFuture<Void> implements Callable<Void>
        {
            @GuardedBy("lock")
            private Future<Void> currentFuture;
            private final ScheduledExecutorService executor;
            private final ReentrantLock lock;
            private final AbstractService service;
            private final Runnable wrappedRunnable;
            
            ReschedulableCallable(final AbstractService service, final ScheduledExecutorService executor, final Runnable wrappedRunnable) {
                this.lock = new ReentrantLock();
                this.wrappedRunnable = wrappedRunnable;
                this.executor = executor;
                this.service = service;
            }
            
            @Override
            public Void call() throws Exception {
                this.wrappedRunnable.run();
                this.reschedule();
                return null;
            }
            
            @Override
            public boolean cancel(final boolean b) {
                this.lock.lock();
                try {
                    return this.currentFuture.cancel(b);
                }
                finally {
                    this.lock.unlock();
                }
            }
            
            @Override
            protected Future<Void> delegate() {
                throw new UnsupportedOperationException("Only cancel and isCancelled is supported by this future");
            }
            
            @Override
            public boolean isCancelled() {
                this.lock.lock();
                try {
                    return this.currentFuture.isCancelled();
                }
                finally {
                    this.lock.unlock();
                }
            }
            
            public void reschedule() {
                // 
                // This method could not be decompiled.
                // 
                // Original Bytecode:
                // 
                //     1: astore_1       
                //     2: aload_0        
                //     3: getfield        com/google/common/util/concurrent/AbstractScheduledService$CustomScheduler$ReschedulableCallable.this$0:Lcom/google/common/util/concurrent/AbstractScheduledService$CustomScheduler;
                //     6: invokevirtual   com/google/common/util/concurrent/AbstractScheduledService$CustomScheduler.getNextSchedule:()Lcom/google/common/util/concurrent/AbstractScheduledService$CustomScheduler$Schedule;
                //     9: astore_2       
                //    10: aload_0        
                //    11: getfield        com/google/common/util/concurrent/AbstractScheduledService$CustomScheduler$ReschedulableCallable.lock:Ljava/util/concurrent/locks/ReentrantLock;
                //    14: invokevirtual   java/util/concurrent/locks/ReentrantLock.lock:()V
                //    17: aload_0        
                //    18: getfield        com/google/common/util/concurrent/AbstractScheduledService$CustomScheduler$ReschedulableCallable.currentFuture:Ljava/util/concurrent/Future;
                //    21: ifnonnull       68
                //    24: aload_0        
                //    25: aload_0        
                //    26: getfield        com/google/common/util/concurrent/AbstractScheduledService$CustomScheduler$ReschedulableCallable.executor:Ljava/util/concurrent/ScheduledExecutorService;
                //    29: aload_0        
                //    30: aload_2        
                //    31: invokestatic    com/google/common/util/concurrent/AbstractScheduledService$CustomScheduler$Schedule.access$800:(Lcom/google/common/util/concurrent/AbstractScheduledService$CustomScheduler$Schedule;)J
                //    34: aload_2        
                //    35: invokestatic    com/google/common/util/concurrent/AbstractScheduledService$CustomScheduler$Schedule.access$900:(Lcom/google/common/util/concurrent/AbstractScheduledService$CustomScheduler$Schedule;)Ljava/util/concurrent/TimeUnit;
                //    38: invokeinterface java/util/concurrent/ScheduledExecutorService.schedule:(Ljava/util/concurrent/Callable;JLjava/util/concurrent/TimeUnit;)Ljava/util/concurrent/ScheduledFuture;
                //    43: putfield        com/google/common/util/concurrent/AbstractScheduledService$CustomScheduler$ReschedulableCallable.currentFuture:Ljava/util/concurrent/Future;
                //    46: aload_0        
                //    47: getfield        com/google/common/util/concurrent/AbstractScheduledService$CustomScheduler$ReschedulableCallable.lock:Ljava/util/concurrent/locks/ReentrantLock;
                //    50: invokevirtual   java/util/concurrent/locks/ReentrantLock.unlock:()V
                //    53: aload_1        
                //    54: ifnonnull       106
                //    57: return         
                //    58: astore_1       
                //    59: aload_0        
                //    60: getfield        com/google/common/util/concurrent/AbstractScheduledService$CustomScheduler$ReschedulableCallable.service:Lcom/google/common/util/concurrent/AbstractService;
                //    63: aload_1        
                //    64: invokevirtual   com/google/common/util/concurrent/AbstractService.notifyFailed:(Ljava/lang/Throwable;)V
                //    67: return         
                //    68: aload_0        
                //    69: getfield        com/google/common/util/concurrent/AbstractScheduledService$CustomScheduler$ReschedulableCallable.currentFuture:Ljava/util/concurrent/Future;
                //    72: invokeinterface java/util/concurrent/Future.isCancelled:()Z
                //    77: istore_3       
                //    78: iload_3        
                //    79: ifeq            24
                //    82: goto            46
                //    85: astore_1       
                //    86: aload_0        
                //    87: getfield        com/google/common/util/concurrent/AbstractScheduledService$CustomScheduler$ReschedulableCallable.lock:Ljava/util/concurrent/locks/ReentrantLock;
                //    90: invokevirtual   java/util/concurrent/locks/ReentrantLock.unlock:()V
                //    93: goto            53
                //    96: astore_1       
                //    97: aload_0        
                //    98: getfield        com/google/common/util/concurrent/AbstractScheduledService$CustomScheduler$ReschedulableCallable.lock:Ljava/util/concurrent/locks/ReentrantLock;
                //   101: invokevirtual   java/util/concurrent/locks/ReentrantLock.unlock:()V
                //   104: aload_1        
                //   105: athrow         
                //   106: aload_0        
                //   107: getfield        com/google/common/util/concurrent/AbstractScheduledService$CustomScheduler$ReschedulableCallable.service:Lcom/google/common/util/concurrent/AbstractService;
                //   110: aload_1        
                //   111: invokevirtual   com/google/common/util/concurrent/AbstractService.notifyFailed:(Ljava/lang/Throwable;)V
                //   114: goto            57
                //    Exceptions:
                //  Try           Handler
                //  Start  End    Start  End    Type                 
                //  -----  -----  -----  -----  ---------------------
                //  2      10     58     68     Ljava/lang/Throwable;
                //  17     24     85     96     Ljava/lang/Throwable;
                //  17     24     96     106    Any
                //  24     46     85     96     Ljava/lang/Throwable;
                //  24     46     96     106    Any
                //  68     78     85     96     Ljava/lang/Throwable;
                //  68     78     96     106    Any
                // 
                // The error that occurred was:
                // 
                // java.lang.IllegalStateException: Expression is linked from several locations: Label_0024:
                //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
                //     at com.strobel.decompiler.ast.AstOptimizer.mergeDisparateObjectInitializations(AstOptimizer.java:2604)
                //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:235)
                //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:662)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:662)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
                //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
                //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
                //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
                //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
                //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
                // 
                throw new IllegalStateException("An error occurred while decompiling this method.");
            }
        }
        
        @Beta
        protected static final class Schedule
        {
            private final long delay;
            private final TimeUnit unit;
            
            public Schedule(final long delay, final TimeUnit timeUnit) {
                this.delay = delay;
                this.unit = Preconditions.checkNotNull(timeUnit);
            }
        }
    }
    
    public abstract static class Scheduler
    {
        private Scheduler() {
        }
        
        public static Scheduler newFixedDelaySchedule(final long n, final long l, final TimeUnit timeUnit) {
            Preconditions.checkNotNull(timeUnit);
            boolean b;
            if (l <= 0L) {
                b = true;
            }
            else {
                b = false;
            }
            Preconditions.checkArgument(!b, "delay must be > 0, found %s", l);
            return new Scheduler() {
                public Future<?> schedule(final AbstractService abstractService, final ScheduledExecutorService scheduledExecutorService, final Runnable runnable) {
                    return scheduledExecutorService.scheduleWithFixedDelay(runnable, n, l, timeUnit);
                }
            };
        }
        
        public static Scheduler newFixedRateSchedule(final long n, final long l, final TimeUnit timeUnit) {
            Preconditions.checkNotNull(timeUnit);
            boolean b;
            if (l <= 0L) {
                b = true;
            }
            else {
                b = false;
            }
            Preconditions.checkArgument(!b, "period must be > 0, found %s", l);
            return new Scheduler() {
                public Future<?> schedule(final AbstractService abstractService, final ScheduledExecutorService scheduledExecutorService, final Runnable runnable) {
                    return scheduledExecutorService.scheduleAtFixedRate(runnable, n, l, timeUnit);
                }
            };
        }
        
        abstract Future<?> schedule(final AbstractService p0, final ScheduledExecutorService p1, final Runnable p2);
    }
    
    private final class ServiceDelegate extends AbstractService
    {
        private volatile ScheduledExecutorService executorService;
        private final ReentrantLock lock;
        private volatile Future<?> runningTask;
        private final Runnable task;
        
        private ServiceDelegate() {
            this.lock = new ReentrantLock();
            this.task = new Task();
        }
        
        @Override
        protected final void doStart() {
            (this.executorService = MoreExecutors.renamingDecorator(AbstractScheduledService.this.executor(), new Supplier<String>() {
                @Override
                public String get() {
                    return AbstractScheduledService.this.serviceName() + " " + ServiceDelegate.this.state();
                }
            })).execute(new Runnable() {
                @Override
                public void run() {
                    ServiceDelegate.this.lock.lock();
                    try {
                        AbstractScheduledService.this.startUp();
                        ServiceDelegate.this.runningTask = AbstractScheduledService.this.scheduler().schedule(AbstractScheduledService.this.delegate, ServiceDelegate.this.executorService, ServiceDelegate.this.task);
                        ServiceDelegate.this.notifyStarted();
                    }
                    catch (final Throwable t) {
                        ServiceDelegate.this.notifyFailed(t);
                        if (ServiceDelegate.this.runningTask != null) {
                            ServiceDelegate.this.runningTask.cancel(false);
                        }
                        ServiceDelegate.this.lock.unlock();
                    }
                    finally {
                        ServiceDelegate.this.lock.unlock();
                    }
                }
            });
        }
        
        @Override
        protected final void doStop() {
            this.runningTask.cancel(false);
            this.executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        ServiceDelegate.this.lock.lock();
                        try {
                            if (ServiceDelegate.this.state() == State.STOPPING) {
                                AbstractScheduledService.this.shutDown();
                                ServiceDelegate.this.lock.unlock();
                                ServiceDelegate.this.notifyStopped();
                            }
                        }
                        finally {
                            ServiceDelegate.this.lock.unlock();
                        }
                    }
                    catch (final Throwable t) {
                        ServiceDelegate.this.notifyFailed(t);
                    }
                }
            });
        }
        
        @Override
        public String toString() {
            return AbstractScheduledService.this.toString();
        }
        
        class Task implements Runnable
        {
            @Override
            public void run() {
                ServiceDelegate.this.lock.lock();
                try {
                    if (!ServiceDelegate.this.runningTask.isCancelled()) {
                        AbstractScheduledService.this.runOneIteration();
                    }
                }
                catch (final Throwable t) {
                    try {
                        AbstractScheduledService.this.shutDown();
                        ServiceDelegate.this.notifyFailed(t);
                        ServiceDelegate.this.runningTask.cancel(false);
                        ServiceDelegate.this.lock.unlock();
                    }
                    catch (final Exception thrown) {
                        AbstractScheduledService.logger.log(Level.WARNING, "Error while attempting to shut down the service after failure.", thrown);
                    }
                }
                finally {
                    ServiceDelegate.this.lock.unlock();
                }
            }
        }
    }
}
