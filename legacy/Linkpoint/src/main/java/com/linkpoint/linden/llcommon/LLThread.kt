package com.linkpoint.linden.llcommon

import java.util.concurrent.atomic.AtomicReference

enum class LLThreadStatus { STOPPED, RUNNING, PAUSED, QUITTING }

abstract class LLThread(private val name: String) {
    private val statusRef = AtomicReference(LLThreadStatus.STOPPED)
    private var thread: Thread? = null

    val status: LLThreadStatus get() = statusRef.get()
    val isStopped: Boolean get() = status == LLThreadStatus.STOPPED
    val isRunning: Boolean get() = status == LLThreadStatus.RUNNING

    protected abstract fun run()

    fun start() {
        if (statusRef.compareAndSet(LLThreadStatus.STOPPED, LLThreadStatus.RUNNING)) {
            thread = Thread({
                try {
                    run()
                } finally {
                    statusRef.set(LLThreadStatus.STOPPED)
                }
            }, name).also { it.isDaemon = true; it.start() }
        }
    }

    fun stop() {
        statusRef.set(LLThreadStatus.QUITTING)
        thread?.interrupt()
    }

    fun pause() {
        statusRef.compareAndSet(LLThreadStatus.RUNNING, LLThreadStatus.PAUSED)
    }

    fun unpause() {
        statusRef.compareAndSet(LLThreadStatus.PAUSED, LLThreadStatus.RUNNING)
    }

    fun waitForStop(timeoutMs: Long = 5000L) {
        thread?.join(timeoutMs)
    }

    protected fun shouldRun(): Boolean =
        statusRef.get() != LLThreadStatus.QUITTING
}
