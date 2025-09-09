package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.util.concurrent.Service;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.concurrent.GuardedBy;

@Beta
public abstract class AbstractScheduledService implements Service {
    /* access modifiers changed from: private */
    public static final Logger logger = Logger.getLogger(AbstractScheduledService.class.getName());
    /* access modifiers changed from: private */
    public final AbstractService delegate = new ServiceDelegate();

    @Beta
    public static abstract class CustomScheduler extends Scheduler {

        private class ReschedulableCallable extends ForwardingFuture<Void> implements Callable<Void> {
            @GuardedBy("lock")
            private Future<Void> currentFuture;
            private final ScheduledExecutorService executor;
            private final ReentrantLock lock = new ReentrantLock();
            private final AbstractService service;
            private final Runnable wrappedRunnable;

            ReschedulableCallable(AbstractService abstractService, ScheduledExecutorService scheduledExecutorService, Runnable runnable) {
                this.wrappedRunnable = runnable;
                this.executor = scheduledExecutorService;
                this.service = abstractService;
            }

            public Void call() throws Exception {
                this.wrappedRunnable.run();
                reschedule();
                return null;
            }

            public boolean cancel(boolean z) {
                this.lock.lock();
                try {
                    return this.currentFuture.cancel(z);
                } finally {
                    this.lock.unlock();
                }
            }

            /* access modifiers changed from: protected */
            public Future<Void> delegate() {
                throw new UnsupportedOperationException("Only cancel and isCancelled is supported by this future");
            }

            public boolean isCancelled() {
                this.lock.lock();
                try {
                    return this.currentFuture.isCancelled();
                } finally {
                    this.lock.unlock();
                }
            }

            /* JADX WARNING: Code restructure failed: missing block: B:15:0x0035, code lost:
                if (r6.currentFuture.isCancelled() != false) goto L_0x0020;
             */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void reschedule() {
                /*
                    r6 = this;
                    r0 = 0
                    com.google.common.util.concurrent.AbstractScheduledService$CustomScheduler r1 = com.google.common.util.concurrent.AbstractScheduledService.CustomScheduler.this     // Catch:{ Throwable -> 0x0028 }
                    com.google.common.util.concurrent.AbstractScheduledService$CustomScheduler$Schedule r1 = r1.getNextSchedule()     // Catch:{ Throwable -> 0x0028 }
                    java.util.concurrent.locks.ReentrantLock r2 = r6.lock
                    r2.lock()
                    java.util.concurrent.Future<java.lang.Void> r2 = r6.currentFuture     // Catch:{ Throwable -> 0x0038, all -> 0x003f }
                    if (r2 != 0) goto L_0x002f
                L_0x0010:
                    java.util.concurrent.ScheduledExecutorService r2 = r6.executor     // Catch:{ Throwable -> 0x0038, all -> 0x003f }
                    long r4 = r1.delay     // Catch:{ Throwable -> 0x0038, all -> 0x003f }
                    java.util.concurrent.TimeUnit r1 = r1.unit     // Catch:{ Throwable -> 0x0038, all -> 0x003f }
                    java.util.concurrent.ScheduledFuture r1 = r2.schedule(r6, r4, r1)     // Catch:{ Throwable -> 0x0038, all -> 0x003f }
                    r6.currentFuture = r1     // Catch:{ Throwable -> 0x0038, all -> 0x003f }
                L_0x0020:
                    java.util.concurrent.locks.ReentrantLock r1 = r6.lock
                    r1.unlock()
                L_0x0025:
                    if (r0 != 0) goto L_0x0046
                L_0x0027:
                    return
                L_0x0028:
                    r0 = move-exception
                    com.google.common.util.concurrent.AbstractService r1 = r6.service
                    r1.notifyFailed(r0)
                    return
                L_0x002f:
                    java.util.concurrent.Future<java.lang.Void> r2 = r6.currentFuture     // Catch:{ Throwable -> 0x0038, all -> 0x003f }
                    boolean r2 = r2.isCancelled()     // Catch:{ Throwable -> 0x0038, all -> 0x003f }
                    if (r2 == 0) goto L_0x0010
                    goto L_0x0020
                L_0x0038:
                    r0 = move-exception
                    java.util.concurrent.locks.ReentrantLock r1 = r6.lock
                    r1.unlock()
                    goto L_0x0025
                L_0x003f:
                    r0 = move-exception
                    java.util.concurrent.locks.ReentrantLock r1 = r6.lock
                    r1.unlock()
                    throw r0
                L_0x0046:
                    com.google.common.util.concurrent.AbstractService r1 = r6.service
                    r1.notifyFailed(r0)
                    goto L_0x0027
                */
                throw new UnsupportedOperationException("Method not decompiled: com.google.common.util.concurrent.AbstractScheduledService.CustomScheduler.ReschedulableCallable.reschedule():void");
            }
        }

        @Beta
        protected static final class Schedule {
            /* access modifiers changed from: private */
            public final long delay;
            /* access modifiers changed from: private */
            public final TimeUnit unit;

            public Schedule(long j, TimeUnit timeUnit) {
                this.delay = j;
                this.unit = (TimeUnit) Preconditions.checkNotNull(timeUnit);
            }
        }

        public CustomScheduler() {
            super();
        }

        /* access modifiers changed from: protected */
        public abstract Schedule getNextSchedule() throws Exception;

        /* access modifiers changed from: package-private */
        public final Future<?> schedule(AbstractService abstractService, ScheduledExecutorService scheduledExecutorService, Runnable runnable) {
            ReschedulableCallable reschedulableCallable = new ReschedulableCallable(abstractService, scheduledExecutorService, runnable);
            reschedulableCallable.reschedule();
            return reschedulableCallable;
        }
    }

    public static abstract class Scheduler {
        private Scheduler() {
        }

        public static Scheduler newFixedDelaySchedule(long j, long j2, TimeUnit timeUnit) {
            Preconditions.checkNotNull(timeUnit);
            Preconditions.checkArgument(!((j2 > 0 ? 1 : (j2 == 0 ? 0 : -1)) <= 0), "delay must be > 0, found %s", Long.valueOf(j2));
            final long j3 = j;
            final long j4 = j2;
            final TimeUnit timeUnit2 = timeUnit;
            return new Scheduler() {
                public Future<?> schedule(AbstractService abstractService, ScheduledExecutorService scheduledExecutorService, Runnable runnable) {
                    return scheduledExecutorService.scheduleWithFixedDelay(runnable, j3, j4, timeUnit2);
                }
            };
        }

        public static Scheduler newFixedRateSchedule(long j, long j2, TimeUnit timeUnit) {
            Preconditions.checkNotNull(timeUnit);
            Preconditions.checkArgument(!((j2 > 0 ? 1 : (j2 == 0 ? 0 : -1)) <= 0), "period must be > 0, found %s", Long.valueOf(j2));
            final long j3 = j;
            final long j4 = j2;
            final TimeUnit timeUnit2 = timeUnit;
            return new Scheduler() {
                public Future<?> schedule(AbstractService abstractService, ScheduledExecutorService scheduledExecutorService, Runnable runnable) {
                    return scheduledExecutorService.scheduleAtFixedRate(runnable, j3, j4, timeUnit2);
                }
            };
        }

        /* access modifiers changed from: package-private */
        public abstract Future<?> schedule(AbstractService abstractService, ScheduledExecutorService scheduledExecutorService, Runnable runnable);
    }

    private final class ServiceDelegate extends AbstractService {
        /* access modifiers changed from: private */
        public volatile ScheduledExecutorService executorService;
        /* access modifiers changed from: private */
        public final ReentrantLock lock;
        /* access modifiers changed from: private */
        public volatile Future<?> runningTask;
        /* access modifiers changed from: private */
        public final Runnable task;

        class Task implements Runnable {
            Task() {
            }

            public void run() {
                ServiceDelegate.this.lock.lock();
                try {
                    if (!ServiceDelegate.this.runningTask.isCancelled()) {
                        AbstractScheduledService.this.runOneIteration();
                    } else {
                        ServiceDelegate.this.lock.unlock();
                    }
                } catch (Throwable th) {
                    try {
                        AbstractScheduledService.this.shutDown();
                    } catch (Exception e) {
                        AbstractScheduledService.logger.log(Level.WARNING, "Error while attempting to shut down the service after failure.", e);
                    }
                    ServiceDelegate.this.notifyFailed(th);
                    ServiceDelegate.this.runningTask.cancel(false);
                } finally {
                    ServiceDelegate.this.lock.unlock();
                }
            }
        }

        private ServiceDelegate() {
            this.lock = new ReentrantLock();
            this.task = new Task();
        }

        /* access modifiers changed from: protected */
        public final void doStart() {
            this.executorService = MoreExecutors.renamingDecorator(AbstractScheduledService.this.executor(), (Supplier<String>) new Supplier<String>() {
                public String get() {
                    return AbstractScheduledService.this.serviceName() + " " + ServiceDelegate.this.state();
                }
            });
            this.executorService.execute(new Runnable() {
                public void run() {
                    ServiceDelegate.this.lock.lock();
                    try {
                        AbstractScheduledService.this.startUp();
                        Future unused = ServiceDelegate.this.runningTask = AbstractScheduledService.this.scheduler().schedule(AbstractScheduledService.this.delegate, ServiceDelegate.this.executorService, ServiceDelegate.this.task);
                        ServiceDelegate.this.notifyStarted();
                    } catch (Throwable th) {
                        ServiceDelegate.this.notifyFailed(th);
                        if (ServiceDelegate.this.runningTask != null) {
                            ServiceDelegate.this.runningTask.cancel(false);
                        }
                    } finally {
                        ServiceDelegate.this.lock.unlock();
                    }
                }
            });
        }

        /* access modifiers changed from: protected */
        public final void doStop() {
            this.runningTask.cancel(false);
            this.executorService.execute(new Runnable() {
                /* JADX WARNING: No exception handlers in catch block: Catch:{  } */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public void run() {
                    /*
                        r2 = this;
                        com.google.common.util.concurrent.AbstractScheduledService$ServiceDelegate r0 = com.google.common.util.concurrent.AbstractScheduledService.ServiceDelegate.this     // Catch:{ Throwable -> 0x003e }
                        java.util.concurrent.locks.ReentrantLock r0 = r0.lock     // Catch:{ Throwable -> 0x003e }
                        r0.lock()     // Catch:{ Throwable -> 0x003e }
                        com.google.common.util.concurrent.AbstractScheduledService$ServiceDelegate r0 = com.google.common.util.concurrent.AbstractScheduledService.ServiceDelegate.this     // Catch:{ all -> 0x0033 }
                        com.google.common.util.concurrent.Service$State r0 = r0.state()     // Catch:{ all -> 0x0033 }
                        com.google.common.util.concurrent.Service$State r1 = com.google.common.util.concurrent.Service.State.STOPPING     // Catch:{ all -> 0x0033 }
                        if (r0 != r1) goto L_0x0029
                        com.google.common.util.concurrent.AbstractScheduledService$ServiceDelegate r0 = com.google.common.util.concurrent.AbstractScheduledService.ServiceDelegate.this     // Catch:{ all -> 0x0033 }
                        com.google.common.util.concurrent.AbstractScheduledService r0 = com.google.common.util.concurrent.AbstractScheduledService.this     // Catch:{ all -> 0x0033 }
                        r0.shutDown()     // Catch:{ all -> 0x0033 }
                        com.google.common.util.concurrent.AbstractScheduledService$ServiceDelegate r0 = com.google.common.util.concurrent.AbstractScheduledService.ServiceDelegate.this     // Catch:{ Throwable -> 0x003e }
                        java.util.concurrent.locks.ReentrantLock r0 = r0.lock     // Catch:{ Throwable -> 0x003e }
                        r0.unlock()     // Catch:{ Throwable -> 0x003e }
                        com.google.common.util.concurrent.AbstractScheduledService$ServiceDelegate r0 = com.google.common.util.concurrent.AbstractScheduledService.ServiceDelegate.this     // Catch:{ Throwable -> 0x003e }
                        r0.notifyStopped()     // Catch:{ Throwable -> 0x003e }
                    L_0x0028:
                        return
                    L_0x0029:
                        com.google.common.util.concurrent.AbstractScheduledService$ServiceDelegate r0 = com.google.common.util.concurrent.AbstractScheduledService.ServiceDelegate.this     // Catch:{ Throwable -> 0x003e }
                        java.util.concurrent.locks.ReentrantLock r0 = r0.lock     // Catch:{ Throwable -> 0x003e }
                        r0.unlock()     // Catch:{ Throwable -> 0x003e }
                        return
                    L_0x0033:
                        r0 = move-exception
                        com.google.common.util.concurrent.AbstractScheduledService$ServiceDelegate r1 = com.google.common.util.concurrent.AbstractScheduledService.ServiceDelegate.this     // Catch:{ Throwable -> 0x003e }
                        java.util.concurrent.locks.ReentrantLock r1 = r1.lock     // Catch:{ Throwable -> 0x003e }
                        r1.unlock()     // Catch:{ Throwable -> 0x003e }
                        throw r0     // Catch:{ Throwable -> 0x003e }
                    L_0x003e:
                        r0 = move-exception
                        com.google.common.util.concurrent.AbstractScheduledService$ServiceDelegate r1 = com.google.common.util.concurrent.AbstractScheduledService.ServiceDelegate.this
                        r1.notifyFailed(r0)
                        goto L_0x0028
                    */
                    throw new UnsupportedOperationException("Method not decompiled: com.google.common.util.concurrent.AbstractScheduledService.ServiceDelegate.AnonymousClass3.run():void");
                }
            });
        }

        public String toString() {
            return AbstractScheduledService.this.toString();
        }
    }

    protected AbstractScheduledService() {
    }

    public final void addListener(Service.Listener listener, Executor executor) {
        this.delegate.addListener(listener, executor);
    }

    public final void awaitRunning() {
        this.delegate.awaitRunning();
    }

    public final void awaitRunning(long j, TimeUnit timeUnit) throws TimeoutException {
        this.delegate.awaitRunning(j, timeUnit);
    }

    public final void awaitTerminated() {
        this.delegate.awaitTerminated();
    }

    public final void awaitTerminated(long j, TimeUnit timeUnit) throws TimeoutException {
        this.delegate.awaitTerminated(j, timeUnit);
    }

    /* access modifiers changed from: protected */
    public ScheduledExecutorService executor() {
        final ScheduledExecutorService newSingleThreadScheduledExecutor = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
            public Thread newThread(Runnable runnable) {
                return MoreExecutors.newThread(AbstractScheduledService.this.serviceName(), runnable);
            }
        });
        addListener(new Service.Listener() {
            public void failed(Service.State state, Throwable th) {
                newSingleThreadScheduledExecutor.shutdown();
            }

            public void terminated(Service.State state) {
                newSingleThreadScheduledExecutor.shutdown();
            }
        }, MoreExecutors.directExecutor());
        return newSingleThreadScheduledExecutor;
    }

    public final Throwable failureCause() {
        return this.delegate.failureCause();
    }

    public final boolean isRunning() {
        return this.delegate.isRunning();
    }

    /* access modifiers changed from: protected */
    public abstract void runOneIteration() throws Exception;

    /* access modifiers changed from: protected */
    public abstract Scheduler scheduler();

    /* access modifiers changed from: protected */
    public String serviceName() {
        return getClass().getSimpleName();
    }

    /* access modifiers changed from: protected */
    public void shutDown() throws Exception {
    }

    public final Service startAsync() {
        this.delegate.startAsync();
        return this;
    }

    /* access modifiers changed from: protected */
    public void startUp() throws Exception {
    }

    public final Service.State state() {
        return this.delegate.state();
    }

    public final Service stopAsync() {
        this.delegate.stopAsync();
        return this;
    }

    public String toString() {
        return serviceName() + " [" + state() + "]";
    }
}
