package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

@GwtCompatible(emulated = true)
abstract class InterruptibleTask implements Runnable {
    private static final AtomicReferenceFieldUpdater<InterruptibleTask, Thread> RUNNER = AtomicReferenceFieldUpdater.newUpdater(InterruptibleTask.class, Thread.class, "runner");
    private volatile boolean doneInterrupting;
    private volatile Thread runner;

    InterruptibleTask() {
    }

    /* access modifiers changed from: package-private */
    public final void interruptTask() {
        Thread thread = this.runner;
        if (thread != null) {
            thread.interrupt();
        }
        this.doneInterrupting = true;
    }

    public final void run() {
        boolean wasInterrupted;
        if (RUNNER.compareAndSet(this, (Object) null, Thread.currentThread())) {
            try {
                runInterruptibly();
                if (wasInterrupted) {
                    while (!this.doneInterrupting) {
                        Thread.yield();
                    }
                }
            } finally {
                if (wasInterrupted()) {
                    while (!this.doneInterrupting) {
                        Thread.yield();
                    }
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public abstract void runInterruptibly();

    /* access modifiers changed from: package-private */
    public abstract boolean wasInterrupted();
}
