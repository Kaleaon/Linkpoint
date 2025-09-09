package com.google.common.util.concurrent;

import com.google.common.base.Preconditions;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.Executor;
import java.util.logging.Logger;
import javax.annotation.concurrent.GuardedBy;

final class SerializingExecutor implements Executor {
    /* access modifiers changed from: private */
    public static final Logger log = Logger.getLogger(SerializingExecutor.class.getName());
    private final Executor executor;
    /* access modifiers changed from: private */
    public final Object internalLock = new Object();
    /* access modifiers changed from: private */
    @GuardedBy("internalLock")
    public boolean isWorkerRunning = false;
    /* access modifiers changed from: private */
    @GuardedBy("internalLock")
    public final Deque<Runnable> queue = new ArrayDeque();
    /* access modifiers changed from: private */
    @GuardedBy("internalLock")
    public int suspensions = 0;

    private final class QueueWorker implements Runnable {
        private QueueWorker() {
        }

        /* JADX WARNING: Code restructure failed: missing block: B:10:?, code lost:
            r0.run();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:11:0x0018, code lost:
            r2 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:12:0x0019, code lost:
            com.google.common.util.concurrent.SerializingExecutor.access$500().log(java.util.logging.Level.SEVERE, "Exception while executing runnable " + r0, r2);
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private void workOnQueue() {
            /*
                r7 = this;
                r1 = 0
            L_0x0001:
                com.google.common.util.concurrent.SerializingExecutor r0 = com.google.common.util.concurrent.SerializingExecutor.this
                java.lang.Object r2 = r0.internalLock
                monitor-enter(r2)
                com.google.common.util.concurrent.SerializingExecutor r0 = com.google.common.util.concurrent.SerializingExecutor.this     // Catch:{ all -> 0x004c }
                int r0 = r0.suspensions     // Catch:{ all -> 0x004c }
                if (r0 == 0) goto L_0x0037
                r0 = r1
            L_0x0011:
                if (r0 == 0) goto L_0x0044
                monitor-exit(r2)     // Catch:{ all -> 0x004c }
                r0.run()     // Catch:{ RuntimeException -> 0x0018 }
                goto L_0x0001
            L_0x0018:
                r2 = move-exception
                java.util.logging.Logger r3 = com.google.common.util.concurrent.SerializingExecutor.log
                java.util.logging.Level r4 = java.util.logging.Level.SEVERE
                java.lang.StringBuilder r5 = new java.lang.StringBuilder
                r5.<init>()
                java.lang.String r6 = "Exception while executing runnable "
                java.lang.StringBuilder r5 = r5.append(r6)
                java.lang.StringBuilder r0 = r5.append(r0)
                java.lang.String r0 = r0.toString()
                r3.log(r4, r0, r2)
                goto L_0x0001
            L_0x0037:
                com.google.common.util.concurrent.SerializingExecutor r0 = com.google.common.util.concurrent.SerializingExecutor.this     // Catch:{ all -> 0x004c }
                java.util.Deque r0 = r0.queue     // Catch:{ all -> 0x004c }
                java.lang.Object r0 = r0.poll()     // Catch:{ all -> 0x004c }
                java.lang.Runnable r0 = (java.lang.Runnable) r0     // Catch:{ all -> 0x004c }
                goto L_0x0011
            L_0x0044:
                com.google.common.util.concurrent.SerializingExecutor r0 = com.google.common.util.concurrent.SerializingExecutor.this     // Catch:{ all -> 0x004c }
                r1 = 0
                boolean unused = r0.isWorkerRunning = r1     // Catch:{ all -> 0x004c }
                monitor-exit(r2)     // Catch:{ all -> 0x004c }
                return
            L_0x004c:
                r0 = move-exception
                monitor-exit(r2)     // Catch:{ all -> 0x004c }
                throw r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.common.util.concurrent.SerializingExecutor.QueueWorker.workOnQueue():void");
        }

        public void run() {
            try {
                workOnQueue();
            } catch (Error e) {
                synchronized (SerializingExecutor.this.internalLock) {
                    boolean unused = SerializingExecutor.this.isWorkerRunning = false;
                    throw e;
                }
            }
        }
    }

    public SerializingExecutor(Executor executor2) {
        this.executor = (Executor) Preconditions.checkNotNull(executor2);
    }

    private void startQueueWorker() {
        synchronized (this.internalLock) {
            if (this.queue.peek() == null) {
                return;
            }
            if (this.suspensions > 0) {
                return;
            }
            if (!this.isWorkerRunning) {
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
        boolean z = false;
        synchronized (this.internalLock) {
            if (this.suspensions > 0) {
                z = true;
            }
            Preconditions.checkState(z);
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
