package com.google.common.util.concurrent;

import com.google.common.base.Preconditions;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.concurrent.GuardedBy;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class SerializingExecutor implements Executor {
    private static final Logger log = Logger.getLogger(SerializingExecutor.class.getName());
    private final Executor executor;
    @GuardedBy("internalLock")
    private final Deque<Runnable> queue = new ArrayDeque();
    @GuardedBy("internalLock")
    private boolean isWorkerRunning = false;
    @GuardedBy("internalLock")
    private int suspensions = 0;
    private final Object internalLock = new Object();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class QueueWorker implements Runnable {
        private QueueWorker() {
        }

        private void workOnQueue() {
            Runnable runnable;
            while (true) {
                synchronized (SerializingExecutor.this.internalLock) {
                    runnable = SerializingExecutor.this.suspensions != 0 ? null : (Runnable) SerializingExecutor.this.queue.poll();
                    if (runnable == null) {
                        SerializingExecutor.this.isWorkerRunning = false;
                        return;
                    }
                }
                try {
                    runnable.run();
                } catch (RuntimeException e) {
                    SerializingExecutor.log.log(Level.SEVERE, "Exception while executing runnable " + runnable, (Throwable) e);
                }
            }
        }

        @Override // java.lang.Runnable
        public void run() {
            try {
                workOnQueue();
            } catch (Error e) {
                synchronized (SerializingExecutor.this.internalLock) {
                    SerializingExecutor.this.isWorkerRunning = false;
                    throw e;
                }
            }
        }
    }

    public SerializingExecutor(Executor executor) {
        this.executor = (Executor) Preconditions.checkNotNull(executor);
    }

    private void startQueueWorker() {
        synchronized (this.internalLock) {
            if (this.queue.peek() != null) {
                if (this.suspensions <= 0) {
                    if (this.isWorkerRunning) {
                        return;
                    }
                    this.isWorkerRunning = true;
                    try {
                        this.executor.execute(new QueueWorker());
                    } catch (Throwable th) {
                        synchronized (this.internalLock) {
                            this.isWorkerRunning = false;
                            throw th;
                        }
                    }
                }
            }
        }
    }

    @Override // java.util.concurrent.Executor
    public void execute(Runnable runnable) {
        synchronized (this.internalLock) {
            this.queue.add(runnable);
        }
        startQueueWorker();
    }

    public void executeFirst(Runnable runnable) {
        synchronized (this.internalLock) {
            this.queue.addFirst(runnable);
        }
        startQueueWorker();
    }

    public void resume() {
        synchronized (this.internalLock) {
            Preconditions.checkState(this.suspensions > 0);
            this.suspensions--;
        }
        startQueueWorker();
    }

    public void suspend() {
        synchronized (this.internalLock) {
            this.suspensions++;
        }
    }
}
