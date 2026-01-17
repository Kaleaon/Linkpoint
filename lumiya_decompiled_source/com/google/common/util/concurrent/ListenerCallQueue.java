package com.google.common.util.concurrent;

import com.google.common.base.Preconditions;
import com.google.common.collect.Queues;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.concurrent.GuardedBy;
/* loaded from: classes.dex */
final class ListenerCallQueue<L> implements Runnable {
    private static final Logger logger = Logger.getLogger(ListenerCallQueue.class.getName());
    private final Executor executor;
    @GuardedBy("this")
    private boolean isThreadScheduled;
    private final L listener;
    @GuardedBy("this")
    private final Queue<Callback<L>> waitQueue = Queues.newArrayDeque();

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static abstract class Callback<L> {
        private final String methodCall;

        /* JADX INFO: Access modifiers changed from: package-private */
        public Callback(String str) {
            this.methodCall = str;
        }

        abstract void call(L l);

        /* JADX INFO: Access modifiers changed from: package-private */
        public void enqueueOn(Iterable<ListenerCallQueue<L>> iterable) {
            for (ListenerCallQueue<L> listenerCallQueue : iterable) {
                listenerCallQueue.add(this);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ListenerCallQueue(L l, Executor executor) {
        this.listener = (L) Preconditions.checkNotNull(l);
        this.executor = (Executor) Preconditions.checkNotNull(executor);
    }

    synchronized void add(Callback<L> callback) {
        this.waitQueue.add(callback);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void execute() {
        boolean z = false;
        synchronized (this) {
            if (!this.isThreadScheduled) {
                this.isThreadScheduled = true;
                z = true;
            }
        }
        if (z) {
            try {
                this.executor.execute(this);
            } catch (RuntimeException e) {
                synchronized (this) {
                    this.isThreadScheduled = false;
                    logger.log(Level.SEVERE, "Exception while running callbacks for " + this.listener + " on " + this.executor, (Throwable) e);
                    throw e;
                }
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:10:0x001a, code lost:
        com.google.common.util.concurrent.ListenerCallQueue.logger.log(java.util.logging.Level.SEVERE, "Exception while executing callback: " + r8.listener + "." + ((com.google.common.util.concurrent.ListenerCallQueue.Callback) r0).methodCall, (java.lang.Throwable) r3);
     */
    /* JADX WARN: Code restructure failed: missing block: B:7:0x0013, code lost:
        r0.call(r8.listener);
     */
    /* JADX WARN: Code restructure failed: missing block: B:9:0x0019, code lost:
        r3 = move-exception;
     */
    @Override // java.lang.Runnable
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void run() {
        /*
            r8 = this;
            r2 = 0
            r1 = 1
        L2:
            monitor-enter(r8)     // Catch: java.lang.Throwable -> L47
            boolean r0 = r8.isThreadScheduled     // Catch: java.lang.Throwable -> L50
            com.google.common.base.Preconditions.checkState(r0)     // Catch: java.lang.Throwable -> L50
            java.util.Queue<com.google.common.util.concurrent.ListenerCallQueue$Callback<L>> r0 = r8.waitQueue     // Catch: java.lang.Throwable -> L50
            java.lang.Object r0 = r0.poll()     // Catch: java.lang.Throwable -> L50
            com.google.common.util.concurrent.ListenerCallQueue$Callback r0 = (com.google.common.util.concurrent.ListenerCallQueue.Callback) r0     // Catch: java.lang.Throwable -> L50
            if (r0 == 0) goto L4b
            monitor-exit(r8)     // Catch: java.lang.Throwable -> L50
            L r3 = r8.listener     // Catch: java.lang.RuntimeException -> L19 java.lang.Throwable -> L47
            r0.call(r3)     // Catch: java.lang.RuntimeException -> L19 java.lang.Throwable -> L47
            goto L2
        L19:
            r3 = move-exception
            java.util.logging.Logger r4 = com.google.common.util.concurrent.ListenerCallQueue.logger     // Catch: java.lang.Throwable -> L47
            java.util.logging.Level r5 = java.util.logging.Level.SEVERE     // Catch: java.lang.Throwable -> L47
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L47
            r6.<init>()     // Catch: java.lang.Throwable -> L47
            java.lang.String r7 = "Exception while executing callback: "
            java.lang.StringBuilder r6 = r6.append(r7)     // Catch: java.lang.Throwable -> L47
            L r7 = r8.listener     // Catch: java.lang.Throwable -> L47
            java.lang.StringBuilder r6 = r6.append(r7)     // Catch: java.lang.Throwable -> L47
            java.lang.String r7 = "."
            java.lang.StringBuilder r6 = r6.append(r7)     // Catch: java.lang.Throwable -> L47
            java.lang.String r0 = com.google.common.util.concurrent.ListenerCallQueue.Callback.access$000(r0)     // Catch: java.lang.Throwable -> L47
            java.lang.StringBuilder r0 = r6.append(r0)     // Catch: java.lang.Throwable -> L47
            java.lang.String r0 = r0.toString()     // Catch: java.lang.Throwable -> L47
            r4.log(r5, r0, r3)     // Catch: java.lang.Throwable -> L47
            goto L2
        L47:
            r0 = move-exception
        L48:
            if (r1 != 0) goto L53
        L4a:
            throw r0
        L4b:
            r0 = 0
            r8.isThreadScheduled = r0     // Catch: java.lang.Throwable -> L50
            monitor-exit(r8)     // Catch: java.lang.Throwable -> L5f
            return
        L50:
            r0 = move-exception
        L51:
            monitor-exit(r8)     // Catch: java.lang.Throwable -> L50
            throw r0     // Catch: java.lang.Throwable -> L47
        L53:
            monitor-enter(r8)
            r1 = 0
            r8.isThreadScheduled = r1     // Catch: java.lang.Throwable -> L59
            monitor-exit(r8)     // Catch: java.lang.Throwable -> L59
            goto L4a
        L59:
            r0 = move-exception
            monitor-exit(r8)     // Catch: java.lang.Throwable -> L59
            throw r0
            r0 = move-exception
            r1 = r2
            goto L48
        L5f:
            r0 = move-exception
            r1 = r2
            goto L51
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.common.util.concurrent.ListenerCallQueue.run():void");
    }
}
