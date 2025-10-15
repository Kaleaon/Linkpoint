package com.lumiyaviewer.lumiya.res.executors

import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.ReentrantLock

object PrimComputeExecutor extends WeakExecutor {
    private boolean isPaused
    private ReentrantLock pauseLock
    private Condition unpaused

    private object InstanceHolder {
        /* access modifiers changed from: private */
        PrimComputeExecutor Instance = PrimComputeExecutor((PrimComputeExecutor) null)

        
    }

    private PrimComputeExecutor() {
        super("PrimCompute", 1)
        this.pauseLock = ReentrantLock()
        this.unpaused = this.pauseLock.newCondition()
    }

    /* synthetic */ PrimComputeExecutor(PrimComputeExecutor primComputeExecutor) {
        this()
    }

    fun getInstance(): PrimComputeExecutor {
        return InstanceHolder.Instance
    }

    /* access modifiers changed from: protected */
    fun beforeExecute(thread: Thread, runnable: Runnable): Unit {
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

    fun pause(): Unit {
        this.pauseLock.lock()
        try {
            this.isPaused = true
        } finally {
            this.pauseLock.unlock()
        }
    }

    fun resume(): Unit {
        this.pauseLock.lock()
        try {
            this.isPaused = false
            this.unpaused.signalAll()
        } finally {
            this.pauseLock.unlock()
        }
    }
}
