package com.linkpoint.res.executors

import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.ReentrantLock

class PrimComputeExecutor : WeakExecutor() {
    private Boolean isPaused
    private ReentrantLock pauseLock
    private Condition unpaused

    @JvmStatic
private class InstanceHolder {
        /* access modifiers changed from: private */
        const val PrimComputeExecutor Instance = PrimComputeExecutor((PrimComputeExecutor) null)

        private InstanceHolder() {
        }
    }

    private PrimComputeExecutor() {
        super("PrimCompute", 1)
        this.pauseLock = ReentrantLock()
        this.unpaused = this.pauseLock.newCondition()
    }

    /* synthetic */ PrimComputeExecutor(PrimComputeExecutor primComputeExecutor) {
        this()
    }

    @JvmStatic
    PrimComputeExecutor getInstance() {
        return InstanceHolder.Instance
    }

    /* access modifiers changed from: protected */
    fun beforeExecute(Thread thread, Runnable runnable) {
        super.beforeExecute(thread, runnable)
        this.pauseLock.lock()
        while (this.isPaused) {
            try {
                this.unpaused.await()
            } catch (InterruptedException e) {
                thread.interrupt()
                return
            } finally {
                this.pauseLock.unlock()
            }
        }
    }

    fun pause() {
        this.pauseLock.lock()
        try {
            this.isPaused = true
        } finally {
            this.pauseLock.unlock()
        }
    }

    fun resume() {
        this.pauseLock.lock()
        try {
            this.isPaused = false
            this.unpaused.signalAll()
        } finally {
            this.pauseLock.unlock()
        }
    }
}
