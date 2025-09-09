package com.google.common.util.concurrent;

import com.google.common.base.Preconditions;
import com.google.common.collect.Queues;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.concurrent.GuardedBy;

final class ListenerCallQueue<L> implements Runnable {
    private static final Logger logger = Logger.getLogger(ListenerCallQueue.class.getName());
    private final Executor executor;
    @GuardedBy("this")
    private boolean isThreadScheduled;
    private final L listener;
    @GuardedBy("this")
    private final Queue<Callback<L>> waitQueue = Queues.newArrayDeque();

    static abstract class Callback<L> {
        /* access modifiers changed from: private */
        public final String methodCall;

        Callback(String str) {
            this.methodCall = str;
        }

        /* access modifiers changed from: package-private */
        public abstract void call(L l);

        /* access modifiers changed from: package-private */
        public void enqueueOn(Iterable<ListenerCallQueue<L>> iterable) {
            for (ListenerCallQueue<L> add : iterable) {
                add.add(this);
            }
        }
    }

    ListenerCallQueue(L l, Executor executor2) {
        this.listener = Preconditions.checkNotNull(l);
        this.executor = (Executor) Preconditions.checkNotNull(executor2);
    }

    /* access modifiers changed from: package-private */
    public synchronized void add(Callback<L> callback) {
        this.waitQueue.add(callback);
    }

    /* access modifiers changed from: package-private */
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
                    logger.log(Level.SEVERE, "Exception while running callbacks for " + this.listener + " on " + this.executor, e);
                    throw e;
                }
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:8:?, code lost:
        r0.call(r8.listener);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void run() {
        /*
            r8 = this;
            r2 = 0
            r1 = 1
        L_0x0002:
            monitor-enter(r8)     // Catch:{ all -> 0x0047 }
            boolean r0 = r8.isThreadScheduled     // Catch:{ all -> 0x0050 }
            com.google.common.base.Preconditions.checkState(r0)     // Catch:{ all -> 0x0050 }
            java.util.Queue<com.google.common.util.concurrent.ListenerCallQueue$Callback<L>> r0 = r8.waitQueue     // Catch:{ all -> 0x0050 }
            java.lang.Object r0 = r0.poll()     // Catch:{ all -> 0x0050 }
            com.google.common.util.concurrent.ListenerCallQueue$Callback r0 = (com.google.common.util.concurrent.ListenerCallQueue.Callback) r0     // Catch:{ all -> 0x0050 }
            if (r0 == 0) goto L_0x004b
            monitor-exit(r8)     // Catch:{ all -> 0x0050 }
            L r3 = r8.listener     // Catch:{ RuntimeException -> 0x0019 }
            r0.call(r3)     // Catch:{ RuntimeException -> 0x0019 }
            goto L_0x0002
        L_0x0019:
            r3 = move-exception
            java.util.logging.Logger r4 = logger     // Catch:{ all -> 0x0047 }
            java.util.logging.Level r5 = java.util.logging.Level.SEVERE     // Catch:{ all -> 0x0047 }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x0047 }
            r6.<init>()     // Catch:{ all -> 0x0047 }
            java.lang.String r7 = "Exception while executing callback: "
            java.lang.StringBuilder r6 = r6.append(r7)     // Catch:{ all -> 0x0047 }
            L r7 = r8.listener     // Catch:{ all -> 0x0047 }
            java.lang.StringBuilder r6 = r6.append(r7)     // Catch:{ all -> 0x0047 }
            java.lang.String r7 = "."
            java.lang.StringBuilder r6 = r6.append(r7)     // Catch:{ all -> 0x0047 }
            java.lang.String r0 = r0.methodCall     // Catch:{ all -> 0x0047 }
            java.lang.StringBuilder r0 = r6.append(r0)     // Catch:{ all -> 0x0047 }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x0047 }
            r4.log(r5, r0, r3)     // Catch:{ all -> 0x0047 }
            goto L_0x0002
        L_0x0047:
            r0 = move-exception
        L_0x0048:
            if (r1 != 0) goto L_0x0053
        L_0x004a:
            throw r0
        L_0x004b:
            r0 = 0
            r8.isThreadScheduled = r0     // Catch:{ all -> 0x0050 }
            monitor-exit(r8)     // Catch:{ all -> 0x005f }
            return
        L_0x0050:
            r0 = move-exception
        L_0x0051:
            monitor-exit(r8)     // Catch:{ all -> 0x0050 }
            throw r0     // Catch:{ all -> 0x0047 }
        L_0x0053:
            monitor-enter(r8)
            r1 = 0
            r8.isThreadScheduled = r1     // Catch:{ all -> 0x0059 }
            monitor-exit(r8)     // Catch:{ all -> 0x0059 }
            goto L_0x004a
        L_0x0059:
            r0 = move-exception
            monitor-exit(r8)     // Catch:{ all -> 0x0059 }
            throw r0
            r0 = move-exception
            r1 = r2
            goto L_0x0048
        L_0x005f:
            r0 = move-exception
            r1 = r2
            goto L_0x0051
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.common.util.concurrent.ListenerCallQueue.run():void");
    }
}
