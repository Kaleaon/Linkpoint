package com.google.common.util.concurrent;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;

public final class ExecutionList {
    @VisibleForTesting
    static final Logger log = Logger.getLogger(ExecutionList.class.getName());
    @GuardedBy("this")
    private boolean executed;
    @GuardedBy("this")
    private RunnableExecutorPair runnables;

    private static final class RunnableExecutorPair {
        final Executor executor;
        @Nullable
        RunnableExecutorPair next;
        final Runnable runnable;

        RunnableExecutorPair(Runnable runnable2, Executor executor2, RunnableExecutorPair runnableExecutorPair) {
            this.runnable = runnable2;
            this.executor = executor2;
            this.next = runnableExecutorPair;
        }
    }

    private static void executeListener(Runnable runnable, Executor executor) {
        try {
            executor.execute(runnable);
        } catch (RuntimeException e) {
            log.log(Level.SEVERE, "RuntimeException while executing runnable " + runnable + " with executor " + executor, e);
        }
    }

    public void add(Runnable runnable, Executor executor) {
        Preconditions.checkNotNull(runnable, "Runnable was null.");
        Preconditions.checkNotNull(executor, "Executor was null.");
        synchronized (this) {
            if (this.executed) {
                executeListener(runnable, executor);
            } else {
                this.runnables = new RunnableExecutorPair(runnable, executor, this.runnables);
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0016, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x001c, code lost:
        r2 = r1.next;
        r1.next = r0;
        r0 = r1;
        r1 = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0023, code lost:
        executeListener(r0.runnable, r0.executor);
        r0 = r0.next;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:7:0x000f, code lost:
        r3 = r0;
        r0 = null;
        r1 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0012, code lost:
        if (r1 != null) goto L_0x001c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0014, code lost:
        if (r0 != null) goto L_0x0023;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void execute() {
        /*
            r4 = this;
            r1 = 0
            monitor-enter(r4)
            boolean r0 = r4.executed     // Catch:{ all -> 0x0019 }
            if (r0 != 0) goto L_0x0017
            r0 = 1
            r4.executed = r0     // Catch:{ all -> 0x0019 }
            com.google.common.util.concurrent.ExecutionList$RunnableExecutorPair r0 = r4.runnables     // Catch:{ all -> 0x0019 }
            r2 = 0
            r4.runnables = r2     // Catch:{ all -> 0x0019 }
            monitor-exit(r4)     // Catch:{ all -> 0x0019 }
            r3 = r0
            r0 = r1
            r1 = r3
        L_0x0012:
            if (r1 != 0) goto L_0x001c
        L_0x0014:
            if (r0 != 0) goto L_0x0023
            return
        L_0x0017:
            monitor-exit(r4)     // Catch:{ all -> 0x0019 }
            return
        L_0x0019:
            r0 = move-exception
            monitor-exit(r4)     // Catch:{ all -> 0x0019 }
            throw r0
        L_0x001c:
            com.google.common.util.concurrent.ExecutionList$RunnableExecutorPair r2 = r1.next
            r1.next = r0
            r0 = r1
            r1 = r2
            goto L_0x0012
        L_0x0023:
            java.lang.Runnable r1 = r0.runnable
            java.util.concurrent.Executor r2 = r0.executor
            executeListener(r1, r2)
            com.google.common.util.concurrent.ExecutionList$RunnableExecutorPair r0 = r0.next
            goto L_0x0014
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.common.util.concurrent.ExecutionList.execute():void");
    }
}
