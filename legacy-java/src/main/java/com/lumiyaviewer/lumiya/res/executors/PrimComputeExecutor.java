package com.lumiyaviewer.lumiya.res.executors;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/* loaded from: classes.dex */
public class PrimComputeExecutor extends WeakExecutor {
    private boolean isPaused;
    private ReentrantLock pauseLock;
    private Condition unpaused;

    /* loaded from: classes.dex */
    private static class InstanceHolder {
        private static final PrimComputeExecutor Instance = new PrimComputeExecutor(null);

        private InstanceHolder() {
        }
    }

    private PrimComputeExecutor() {
        super("PrimCompute", 1);
        this.pauseLock = new ReentrantLock();
        this.unpaused = this.pauseLock.newCondition();
    }

    /* synthetic */ PrimComputeExecutor(PrimComputeExecutor primComputeExecutor) {
        this();
    }

    public static PrimComputeExecutor getInstance() {
        return InstanceHolder.Instance;
    }

    @Override // java.util.concurrent.ThreadPoolExecutor
    protected void beforeExecute(Thread thread, Runnable runnable) {
        super.beforeExecute(thread, runnable);
        this.pauseLock.lock();
        while (this.isPaused) {
            try {
                this.unpaused.await();
            } catch (InterruptedException e) {
                thread.interrupt();
                return;
            } finally {
                this.pauseLock.unlock();
            }
        }
    }

    public void pause() {
        this.pauseLock.lock();
        try {
            this.isPaused = true;
        } finally {
            this.pauseLock.unlock();
        }
    }

    public void resume() {
        this.pauseLock.lock();
        try {
            this.isPaused = false;
            this.unpaused.signalAll();
        } finally {
            this.pauseLock.unlock();
        }
    }
}
