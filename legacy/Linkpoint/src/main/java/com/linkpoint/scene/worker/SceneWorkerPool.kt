package com.linkpoint.scene.worker

import android.util.Log
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import kotlin.math.max

/**
 * Task priority levels for scene processing operations.
 */
enum class TaskPriority {
    /** Immediate/high priority: network packets, nearby meshes, avatars, teleport scene updates */
    HIGH,
    /** Low priority: distant LOD mesh decoding, background terrain patches, pre-fetches */
    LOW
}

/**
 * Result of submitting a task to [SceneWorkerPool].
 */
sealed class TaskSubmissionResult {
    object Accepted : TaskSubmissionResult()
    data class Dropped(val reason: String) : TaskSubmissionResult()
    data class Deferred(val reason: String) : TaskSubmissionResult()
}

/**
 * Adaptive background worker thread pool for offloading heavy scene tasks
 * (packet parsing, LLSD deserialization, mesh vertex unpacking, spatial octree updates)
 * away from the main display/GL rendering thread.
 */
class SceneWorkerPool internal constructor(
    val threadCount: Int,
    private val maxQueueCapacity: Int = DEFAULT_MAX_QUEUE_CAPACITY
) {
    companion object {
        private const val TAG = "SceneWorkerPool"
        const val DEFAULT_MAX_QUEUE_CAPACITY = 256

        @Volatile
        private var instance: SceneWorkerPool? = null

        /**
         * Get or create shared singleton instance adapted to available CPU cores on ARM device.
         */
        fun getInstance(
            maxQueueCapacity: Int = DEFAULT_MAX_QUEUE_CAPACITY
        ): SceneWorkerPool {
            return instance ?: synchronized(this) {
                instance ?: run {
                    val cores = Runtime.getRuntime().availableProcessors()
                    // Leave 1 core for UI/GL render thread, allocate remaining (min 2)
                    val poolSize = max(2, cores - 1)
                    SceneWorkerPool(poolSize, maxQueueCapacity).also { instance = it }
                }
            }
        }

        /**
         * Calculate adaptive worker thread count for given core count.
         */
        fun calculateAdaptiveThreadCount(availableCores: Int): Int {
            return max(2, availableCores - 1)
        }
    }

    private class PrioritizedRunnable(
        val priority: TaskPriority,
        val sequenceNumber: Long,
        val runnable: Runnable
    ) : Runnable, Comparable<PrioritizedRunnable> {
        override fun run() {
            runnable.run()
        }

        override fun compareTo(other: PrioritizedRunnable): Int {
            if (this.priority != other.priority) {
                // HIGH priority comes first (ordinal 0 < ordinal 1)
                return this.priority.ordinal.compareTo(other.priority.ordinal)
            }
            // FIFO for same priority
            return this.sequenceNumber.compareTo(other.sequenceNumber)
        }
    }

    private val sequenceCounter = AtomicLong(0L)
    private val activeTaskCount = AtomicInteger(0)
    private val processedTaskCount = AtomicLong(0L)
    private val droppedLowPriorityCount = AtomicLong(0L)
    private val deferredCount = AtomicLong(0L)

    private val workQueue = PriorityBlockingQueue<Runnable>()
    private val executor: ThreadPoolExecutor

    init {
        val threadFactory = object : ThreadFactory {
            private val threadNumber = AtomicInteger(1)
            override fun newThread(r: Runnable): Thread {
                return Thread(r, "SceneWorker-${threadNumber.getAndIncrement()}").apply {
                    // Set background priority so rendering thread gets CPU preference
                    priority = Thread.NORM_PRIORITY - 1
                    isDaemon = true
                }
            }
        }

        executor = ThreadPoolExecutor(
            threadCount,
            threadCount,
            60L,
            TimeUnit.SECONDS,
            workQueue as BlockingQueue<Runnable>,
            threadFactory
        )
        executor.allowCoreThreadTimeOut(true)
        safeLogI("Initialized SceneWorkerPool with $threadCount threads, max queue capacity: $maxQueueCapacity")
    }

    private fun safeLogI(msg: String) {
        try { Log.i(TAG, msg) } catch (_: Throwable) { System.err.println("[$TAG] $msg") }
    }

    private fun safeLogW(msg: String) {
        try { Log.w(TAG, msg) } catch (_: Throwable) { System.err.println("[$TAG] $msg") }
    }

    private fun safeLogE(msg: String, t: Throwable?) {
        try { Log.e(TAG, msg, t) } catch (_: Throwable) { System.err.println("[$TAG] $msg ${t?.message ?: ""}") }
    }

    /**
     * Submit a background task with specified priority.
     * Drops or defers LOW priority tasks when queue capacity limit is reached.
     */
    fun submit(
        priority: TaskPriority = TaskPriority.HIGH,
        taskName: String = "UnnamedTask",
        block: () -> Unit
    ): TaskSubmissionResult {
        val currentQueueSize = workQueue.size
        if (currentQueueSize >= maxQueueCapacity) {
            if (priority == TaskPriority.LOW) {
                droppedLowPriorityCount.incrementAndGet()
                safeLogW("Queue capacity reached ($currentQueueSize/$maxQueueCapacity). Dropping LOW priority task: $taskName")
                return TaskSubmissionResult.Dropped("Queue capacity reached: $currentQueueSize/$maxQueueCapacity")
            } else {
                // High priority task under high load: log warning but accept
                deferredCount.incrementAndGet()
                safeLogW("Queue capacity saturated ($currentQueueSize/$maxQueueCapacity), queuing HIGH priority task: $taskName")
            }
        }

        val seq = sequenceCounter.incrementAndGet()
        val wrappedRunnable = PrioritizedRunnable(priority, seq, Runnable {
            activeTaskCount.incrementAndGet()
            try {
                block()
            } catch (t: Throwable) {
                safeLogE("Error executing scene worker task: $taskName", t)
            } finally {
                activeTaskCount.decrementAndGet()
                processedTaskCount.incrementAndGet()
            }
        })

        executor.execute(wrappedRunnable)
        return TaskSubmissionResult.Accepted
    }

    /**
     * Submit a background callable returning a Future.
     */
    fun <T> submitCallable(
        priority: TaskPriority = TaskPriority.HIGH,
        taskName: String = "CallableTask",
        block: () -> T
    ): CompletableFuture<T> {
        val future = CompletableFuture<T>()
        val result = submit(priority, taskName) {
            try {
                val valResult = block()
                future.complete(valResult)
            } catch (t: Throwable) {
                future.completeExceptionally(t)
            }
        }
        if (result is TaskSubmissionResult.Dropped) {
            future.completeExceptionally(RejectedExecutionException("Task dropped due to queue capacity limits"))
        }
        return future
    }

    /** Get current number of pending queued tasks. */
    fun getQueueSize(): Int = workQueue.size

    /** Get current number of active worker threads executing tasks. */
    fun getActiveCount(): Int = activeTaskCount.get()

    /** Get total count of completed tasks. */
    fun getCompletedTaskCount(): Long = processedTaskCount.get()

    /** Get total count of dropped low-priority tasks. */
    fun getDroppedLowPriorityCount(): Long = droppedLowPriorityCount.get()

    /** Get total count of deferred tasks. */
    fun getDeferredCount(): Long = deferredCount.get()

    /** Maximum queue capacity before low-priority drop. */
    fun getMaxQueueCapacity(): Int = maxQueueCapacity

    /** Shutdown worker pool. */
    fun shutdown() {
        executor.shutdown()
    }
}
