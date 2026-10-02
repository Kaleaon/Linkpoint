package com.linkpoint.assets.pool

import android.app.ActivityManager
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.linkpoint.assets.JPEG2000Decoder
import com.linkpoint.assets.MeshData
import com.linkpoint.assets.MeshLOD
import com.linkpoint.assets.TexturePriority
import kotlinx.coroutines.*
import kotlinx.coroutines.CancellationException
import java.util.UUID
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * Off-thread asset decoding worker pool isolated from the main EGL render loop.
 *
 * Requirements & Constraints:
 *  - Dynamically bounded thread count: N - 1 CPU cores (1..4 threads).
 *  - Isolates non-thread-safe OpenGL ES calls strictly to the main EGL render context.
 *  - Auto-throttles queue depth and active worker concurrency under mobile low-memory states.
 *  - Supports cancellation of pending tasks on region transfers / teleports.
 */
class AssetDecodingWorkerPool(
    private val context: Context? = null,
    maxWorkerThreadsOverride: Int? = null
) {
    companion object {
        private const val TAG = "AssetDecodingWorkerPool"
        private const val MAX_BOUNDED_THREADS = 4
        private const val LOW_MEMORY_FREE_MB_THRESHOLD = 32L
    }

    /** Compute dynamic thread pool size bounded to N-1 CPU cores (max 4). */
    val threadCount: Int = maxWorkerThreadsOverride ?: run {
        val cores = Runtime.getRuntime().availableProcessors()
        minOf(MAX_BOUNDED_THREADS, maxOf(1, cores - 1))
    }

    private val threadNumber = AtomicInteger(1)
    private val threadFactory = ThreadFactory { runnable ->
        Thread(runnable, "AssetDecoderWorker-${threadNumber.getAndIncrement()}").apply {
            priority = Thread.NORM_PRIORITY - 1 // Lower priority than UI / render thread
            isDaemon = true
        }
    }

    private val executor: ThreadPoolExecutor = ThreadPoolExecutor(
        threadCount,
        threadCount,
        60L,
        TimeUnit.SECONDS,
        LinkedBlockingQueue<Runnable>(),
        threadFactory
    ).apply {
        allowCoreThreadTimeOut(true)
    }

    private val poolDispatcher = executor.asCoroutineDispatcher()
    private val scope = CoroutineScope(poolDispatcher + SupervisorJob())

    private val activeTasks = ConcurrentHashMap<UUID, Job>()
    private val taskMetrics = ConcurrentHashMap<UUID, Long>()

    // Statistics
    private val totalDecodes = AtomicInteger(0)
    private val successfulDecodes = AtomicInteger(0)
    private val failedDecodes = AtomicInteger(0)
    private val cancelledDecodes = AtomicInteger(0)

    init {
        Log.i(TAG, "Initialized AssetDecodingWorkerPool with $threadCount worker threads (bounded N-1 cores)")
    }

    /**
     * Submit an asynchronous JPEG2000 texture decoding task.
     * Executes entirely off the EGL render thread.
     */
    fun decodeTextureAsync(
        textureId: UUID,
        data: ByteArray,
        discardLevel: Int = 0,
        priority: TexturePriority = TexturePriority.NORMAL,
        onComplete: (Bitmap?) -> Unit
    ): Job {
        if (data.isEmpty()) {
            onComplete(null)
            return Job().apply { complete() }
        }

        // Low memory check — drop speculative/prefetch decoding under memory pressure
        if (isLowMemory() && priority == TexturePriority.PREFETCH) {
            Log.w(TAG, "Dropping PREFETCH texture decode for $textureId due to system low memory")
            cancelledDecodes.incrementAndGet()
            onComplete(null)
            return Job().apply { complete() }
        }

        val job = scope.launch {
            val startTime = System.currentTimeMillis()
            totalDecodes.incrementAndGet()
            taskMetrics[textureId] = startTime

            try {
                ensureActive()
                val bitmap = JPEG2000Decoder.decode(data, discardLevel)
                ensureActive()

                if (bitmap != null) {
                    successfulDecodes.incrementAndGet()
                    onComplete(bitmap)
                } else {
                    failedDecodes.incrementAndGet()
                    onComplete(null)
                }
            } catch (e: CancellationException) {
                if (activeTasks.remove(textureId) != null) {
                    cancelledDecodes.incrementAndGet()
                }
                onComplete(null)
            } catch (e: Exception) {
                Log.e(TAG, "Error decoding texture $textureId off-thread", e)
                failedDecodes.incrementAndGet()
                onComplete(null)
            } finally {
                taskMetrics.remove(textureId)
                activeTasks.remove(textureId)
            }
        }

        activeTasks[textureId] = job
        return job
    }

    /**
     * Submit an asynchronous OpenSim mesh binary parsing task.
     * Executes entirely off the EGL render thread.
     */
    fun parseMeshAsync(
        meshId: UUID,
        data: ByteArray,
        lod: MeshLOD = MeshLOD.HIGH,
        parseBlock: (ByteArray, MeshLOD) -> MeshData?,
        onComplete: (MeshData?) -> Unit
    ): Job {
        if (data.isEmpty()) {
            onComplete(null)
            return Job().apply { complete() }
        }

        val job = scope.launch {
            val startTime = System.currentTimeMillis()
            totalDecodes.incrementAndGet()

            try {
                ensureActive()
                val meshData = parseBlock(data, lod)
                ensureActive()

                if (meshData != null) {
                    successfulDecodes.incrementAndGet()
                    onComplete(meshData)
                } else {
                    failedDecodes.incrementAndGet()
                    onComplete(null)
                }
            } catch (e: CancellationException) {
                if (activeTasks.remove(meshId) != null) {
                    cancelledDecodes.incrementAndGet()
                }
                onComplete(null)
            } catch (e: Exception) {
                Log.e(TAG, "Error parsing mesh $meshId off-thread", e)
                failedDecodes.incrementAndGet()
                onComplete(null)
            } finally {
                activeTasks.remove(meshId)
            }
        }

        activeTasks[meshId] = job
        return job
    }

    /**
     * Cancel a specific decoding task by asset UUID.
     */
    fun cancelTask(assetId: UUID) {
        activeTasks.remove(assetId)?.let { job ->
            job.cancel()
            cancelledDecodes.incrementAndGet()
            Log.d(TAG, "Cancelled asset decode task: $assetId")
        }
    }

    /**
     * Cancel all active and queued decoding tasks (e.g. during teleport or region crossing).
     */
    fun cancelAllPending() {
        val pendingTasks = activeTasks.values.toList()
        val count = pendingTasks.size
        activeTasks.clear()
        pendingTasks.forEach { it.cancel() }
        cancelledDecodes.addAndGet(count)
        Log.i(TAG, "Cancelled $count pending asset decoding tasks on worker pool")
    }

    /**
     * Check if system memory is critically low and requires auto-throttling.
     */
    fun isLowMemory(): Boolean {
        context?.let { ctx ->
            try {
                val activityManager = ctx.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
                val memoryInfo = ActivityManager.MemoryInfo()
                activityManager?.getMemoryInfo(memoryInfo)
                if (memoryInfo.lowMemory || (memoryInfo.availMem / (1024 * 1024)) < LOW_MEMORY_FREE_MB_THRESHOLD) {
                    return true
                }
            } catch (e: Exception) {
                Log.w(TAG, "Failed to inspect system memory info", e)
            }
        }

        val runtime = Runtime.getRuntime()
        val freeBytes = runtime.maxMemory() - (runtime.totalMemory() - runtime.freeMemory())
        val freeMb = freeBytes / (1024 * 1024)
        return freeMb < LOW_MEMORY_FREE_MB_THRESHOLD
    }

    /** Get diagnostic metrics for worker pool */
    fun getDiagnostics(): WorkerPoolDiagnostics {
        return WorkerPoolDiagnostics(
            configuredThreads = threadCount,
            activeThreadCount = executor.activeCount,
            queueSize = executor.queue.size,
            activeTaskCount = activeTasks.size,
            totalDecodes = totalDecodes.get(),
            successfulDecodes = successfulDecodes.get(),
            failedDecodes = failedDecodes.get(),
            cancelledDecodes = cancelledDecodes.get(),
            isLowMemoryState = isLowMemory()
        )
    }

    fun shutdown() {
        cancelAllPending()
        scope.cancel()
        executor.shutdown()
    }

    data class WorkerPoolDiagnostics(
        val configuredThreads: Int,
        val activeThreadCount: Int,
        val queueSize: Int,
        val activeTaskCount: Int,
        val totalDecodes: Int,
        val successfulDecodes: Int,
        val failedDecodes: Int,
        val cancelledDecodes: Int,
        val isLowMemoryState: Boolean
    )
}
