// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.util.concurrent;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible(emulated = true)
abstract class InterruptibleTask implements Runnable
{
    private static final AtomicReferenceFieldUpdater<InterruptibleTask, Thread> RUNNER;
    private volatile boolean doneInterrupting;
    private volatile Thread runner;
    
    static {
        RUNNER = AtomicReferenceFieldUpdater.newUpdater(InterruptibleTask.class, Thread.class, "runner");
    }
    
    final void interruptTask() {
        final Thread runner = this.runner;
        if (runner != null) {
            runner.interrupt();
        }
        this.doneInterrupting = true;
    }
    
    @Override
    public final void run() {
        if (!InterruptibleTask.RUNNER.compareAndSet(this, null, Thread.currentThread())) {
            return;
        }
        try {
            this.runInterruptibly();
            if (this.wasInterrupted()) {
                while (!this.doneInterrupting) {
                    Thread.yield();
                }
            }
        }
        finally {
            while (true) {
                if (!this.wasInterrupted()) {
                    break Label_0048;
                }
                while (!this.doneInterrupting) {
                    Thread.yield();
                }
                break Label_0048;
                continue;
            }
        }
    }
    
    abstract void runInterruptibly();
    
    abstract boolean wasInterrupted();
}
