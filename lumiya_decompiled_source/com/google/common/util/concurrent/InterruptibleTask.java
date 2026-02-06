package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
@GwtCompatible(emulated = true)
/* loaded from: classes.dex */
abstract class InterruptibleTask implements Runnable {
    private static final AtomicReferenceFieldUpdater<InterruptibleTask, Thread> RUNNER = AtomicReferenceFieldUpdater.newUpdater(InterruptibleTask.class, Thread.class, "runner");
    private volatile boolean doneInterrupting;
    private volatile Thread runner;

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void interruptTask() {
        Thread thread = this.runner;
        if (thread != null) {
            thread.interrupt();
        }
        this.doneInterrupting = true;
    }

    @Override // java.lang.Runnable
    public final void run() {
        if (RUNNER.compareAndSet(this, null, Thread.currentThread())) {
            try {
                runInterruptibly();
            } finally {
                if (wasInterrupted()) {
                    while (!this.doneInterrupting) {
                        Thread.yield();
                    }
                }
            }
        }
    }

    abstract void runInterruptibly();

    abstract boolean wasInterrupted();
}
