package com.lumiyaviewer.lumiya.res.executors

import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * Executor for primitive computation tasks with pause/resume capability
 */
object PrimComputeExecutor : WeakExecutor("PrimCompute", 1) {
    
    private var isPaused = false
    private val pauseLock = ReentrantLock()
    private val unpaused = pauseLock.newCondition()

    /**
     * Get singleton instance
     */
    fun getInstance(): PrimComputeExecutor = this

    /**
     * Called before executing each task - handles pause state
     */
    override fun beforeExecute(thread: Thread, runnable: Runnable) {
        super.beforeExecute(thread, runnable)
        
        pauseLock.lock()
        try {
            while (isPaused) {
                try {
                    unpaused.await()
                } catch (e: InterruptedException) {
                    thread.interrupt()
                    return
                }
            }
        } finally {
            pauseLock.unlock()
        }
    }

    /**
     * Pause execution of tasks
     */
    fun pause() {
        pauseLock.withLock {
            isPaused = true
        }
    }

    /**
     * Resume execution of tasks
     */
    fun resume() {
        pauseLock.withLock {
            isPaused = false
            unpaused.signalAll()
        }
    }
}
