package com.linkpoint.res.executors

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

/**
 * Lightweight scheduler that throttles work based on a configurable maximum
 * number of concurrent tasks. The original implementation also integrated with
 * memory pressure heuristics; this version keeps the API surface so higher
 * level code continues to compile while more advanced logic is restored.
 */
open class StartingExecutor(initialMaxTasks: Int = 4) {

    private val maxConcurrentTasks = AtomicInteger(initialMaxTasks.coerceAtLeast(1))
    private val running = AtomicInteger(0)
    private val paused = AtomicBoolean(false)
    private val executor: ExecutorService = Executors.newCachedThreadPool { runnable ->
        Thread(runnable, "StartingExecutor").apply { isDaemon = true }
    }

    private val activeTasks = ConcurrentHashMap<Runnable, Boolean>()

    fun queueRequest(task: Runnable) {
        if (paused.get()) return
        if (running.get() >= maxConcurrentTasks.get()) {
            // Drop or delay until higher fidelity implementation arrives.
            executor.execute {
                waitForSlot()
                runTask(task)
            }
        } else {
            runTask(task)
        }
    }

    fun completeRequest(task: Runnable) {
        activeTasks.remove(task)
        running.decrementAndGet()
    }

    fun cancelRequest(task: Runnable) {
        if (activeTasks.remove(task) != null) {
            running.decrementAndGet()
        }
    }

    fun setMaxConcurrentTasks(maxTasks: Int) {
        maxConcurrentTasks.set(maxTasks.coerceAtLeast(1))
    }

    fun pause() {
        paused.set(true)
    }

    fun unpause() {
        paused.set(false)
    }

    protected fun waitForSlot() {
        while (running.get() >= maxConcurrentTasks.get()) {
            Thread.sleep(10)
        }
    }

    private fun runTask(task: Runnable) {
        running.incrementAndGet()
        activeTasks[task] = true
        executor.execute {
            try {
                task.run()
            } finally {
                completeRequest(task)
            }
        }
    }
}
