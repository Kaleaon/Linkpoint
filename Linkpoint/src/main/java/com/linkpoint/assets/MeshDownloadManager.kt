package com.linkpoint.assets

import android.util.Log
import com.linkpoint.protocol.llsd.LLSDMap
import com.linkpoint.protocol.types.LLVector3
import kotlinx.coroutines.Deferred
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

/**
 * Manages mesh asset download queues with distance gating and LOD request throttling.
 *
 * Integrates with [LLMeshFetcher] to calculate screen pixel coverage and select target LOD levels
 * before issuing HTTP mesh requests. Gates requests for distant avatars (>50m), defers sub-pixel
 * attachment requests, and fetches computed target LOD byte ranges to minimize network payload.
 */
class MeshDownloadManager(
    private val fetcher: LLMeshFetcher,
    private val meshManager: MeshManager
) {

    companion object {
        private const val TAG = "MeshDownloadManager"
        private const val MAX_CONCURRENT_LOD_UPGRADES = 4

        // Estimated average payload sizes per LOD level for bandwidth savings estimation
        private const val ESTIMATED_LOD0_BYTES = 500_000L // 500 KB
        private const val ESTIMATED_LOD1_BYTES = 150_000L // 150 KB
        private const val ESTIMATED_LOD2_BYTES = 40_000L  // 40 KB
        private const val ESTIMATED_LOD3_BYTES = 10_000L  // 10 KB
    }

    private val activeUpgradeTasks = AtomicLong(0)

    fun canStartUpgradeTask(): Boolean {
        return activeUpgradeTasks.get() < MAX_CONCURRENT_LOD_UPGRADES
    }

    fun onUpgradeTaskStarted() {
        activeUpgradeTasks.incrementAndGet()
    }

    fun onUpgradeTaskCompleted() {
        if (activeUpgradeTasks.get() > 0) {
            activeUpgradeTasks.decrementAndGet()
        }
    }

    /**
     * Evaluate whether a mesh object's LOD should be changed based on projected pixel coverage
     * and a 15% distance hysteresis buffer. Returns null if no LOD change is warranted or
     * if the request would duplicate an active/pending LOD.
     */
    fun evaluateLodChange(
        meshId: UUID,
        objectPos: LLVector3,
        boundingRadius: Float,
        currentLod: MeshLOD?,
        pendingLod: MeshLOD?,
        lastEvaluatedDistance: Float,
        isAvatar: Boolean = false,
        isAttachment: Boolean = false,
        header: LLSDMap? = null
    ): LLMeshFetcher.LodSelection? {
        val currentDistance = camera.position.distance(objectPos)

        // Apply 15% distance hysteresis buffer guardrail
        if (currentLod != null && !fetcher.shouldReevaluate(lastEvaluatedDistance, currentDistance, 0.15f)) {
            return null
        }

        val selection = fetcher.selectLod(
            meshId = meshId,
            objectPos = objectPos,
            boundingRadius = boundingRadius,
            camera = camera,
            isAvatar = isAvatar,
            isAttachment = isAttachment,
            header = header
        )

        // Hysteresis guardrails: prevent duplicate or oscillating LOD fetch requests
        if (selection.isDeferred || selection.targetLod == currentLod || selection.targetLod == pendingLod) {
            return null
        }

        return selection
    }

    data class QueuedMeshRequest(
        val meshId: UUID,
        val objectPos: LLVector3,
        val boundingRadius: Float,
        val isAvatar: Boolean = false,
        val isAttachment: Boolean = false,
        val header: LLSDMap? = null,
        var lastEvaluatedDistance: Float = 0f,
        var lastSelectedLod: MeshLOD? = null
    )

    data class MeshDownloadManagerDiagnostics(
        val totalRequests: Long,
        val deferredRequestsCount: Long,
        val activeDeferredQueueSize: Int,
        val lod0Requests: Long,
        val lod1Requests: Long,
        val lod2Requests: Long,
        val lod3Requests: Long,
        val estimatedBytesSaved: Long
    )

    @Volatile
    var camera: LLMeshFetcher.CameraParams = LLMeshFetcher.CameraParams()
        private set

    private val deferredQueue = ConcurrentHashMap<UUID, QueuedMeshRequest>()

    // Metrics counters
    private val totalRequestsCount = AtomicLong(0)
    private val deferredRequestsCount = AtomicLong(0)
    private val lod0Count = AtomicLong(0)
    private val lod1Count = AtomicLong(0)
    private val lod2Count = AtomicLong(0)
    private val lod3Count = AtomicLong(0)
    private val estimatedBytesSaved = AtomicLong(0)

    /**
     * Update active camera parameters and re-evaluate deferred requests.
     * Returns list of mesh IDs that were promoted out of the deferred queue.
     */
    fun updateCamera(newCamera: LLMeshFetcher.CameraParams): List<UUID> {
        camera = newCamera
        return reevaluateDeferredQueue()
    }

    /**
     * Request a mesh asset with distance gating and LOD selection based on screen pixel coverage.
     *
     * @return Deferred<MeshData?> or null if the request was deferred due to sub-pixel coverage.
     */
    suspend fun requestMesh(
        meshId: UUID,
        objectPos: LLVector3,
        boundingRadius: Float,
        isAvatar: Boolean = false,
        isAttachment: Boolean = false,
        header: LLSDMap? = null
    ): MeshData? {
        totalRequestsCount.incrementAndGet()

        val selection = fetcher.selectLod(
            meshId = meshId,
            objectPos = objectPos,
            boundingRadius = boundingRadius,
            camera = camera,
            isAvatar = isAvatar,
            isAttachment = isAttachment,
            header = header
        )

        if (selection.isDeferred) {
            deferredRequestsCount.incrementAndGet()
            deferredQueue[meshId] = QueuedMeshRequest(
                meshId = meshId,
                objectPos = objectPos,
                boundingRadius = boundingRadius,
                isAvatar = isAvatar,
                isAttachment = isAttachment,
                header = header,
                lastEvaluatedDistance = selection.distanceMeters,
                lastSelectedLod = selection.targetLod
            )
            try { Log.d(TAG, "Deferred mesh $meshId: ${selection.reason}") } catch (_: Throwable) {}
            return null
        }

        // Object is not deferred, remove from deferred queue if present
        deferredQueue.remove(meshId)

        // Track LOD distribution and payload savings estimate
        recordLodMetrics(selection.targetLod)

        // Delegate to MeshManager to fetch target LOD byte range
        return meshManager.getMesh(meshId, selection.targetLod)
    }

    /**
     * Re-evaluate deferred queue requests against updated camera parameters.
     * Promotes requests whose projected pixel coverage now exceeds deferral thresholds.
     */
    fun reevaluateDeferredQueue(): List<UUID> {
        if (deferredQueue.isEmpty()) return emptyList()

        val promotedList = mutableListOf<UUID>()
        val currentCamera = camera

        for ((meshId, queued) in deferredQueue) {
            val selection = fetcher.selectLod(
                meshId = meshId,
                objectPos = queued.objectPos,
                boundingRadius = queued.boundingRadius,
                camera = currentCamera,
                isAvatar = queued.isAvatar,
                isAttachment = queued.isAttachment,
                header = queued.header
            )

            if (!selection.isDeferred) {
                promotedList.add(meshId)
                deferredQueue.remove(meshId)
                try { Log.d(TAG, "Promoted deferred mesh $meshId (dist=${selection.distanceMeters}m, px=${selection.projectedPixelCoverage})") } catch (_: Throwable) {}
            } else {
                queued.lastEvaluatedDistance = selection.distanceMeters
                queued.lastSelectedLod = selection.targetLod
            }
        }

        return promotedList
    }

    /**
     * Clear all queued state.
     */
    fun clear() {
        deferredQueue.clear()
    }

    /**
     * Get diagnostic metrics for monitoring network payload reduction.
     */
    fun getDiagnostics(): MeshDownloadManagerDiagnostics {
        return MeshDownloadManagerDiagnostics(
            totalRequests = totalRequestsCount.get(),
            deferredRequestsCount = deferredRequestsCount.get(),
            activeDeferredQueueSize = deferredQueue.size,
            lod0Requests = lod0Count.get(),
            lod1Requests = lod1Count.get(),
            lod2Requests = lod2Count.get(),
            lod3Requests = lod3Count.get(),
            estimatedBytesSaved = estimatedBytesSaved.get()
        )
    }

    private fun recordLodMetrics(lod: MeshLOD) {
        when (lod) {
            MeshLOD.HIGHEST -> lod0Count.incrementAndGet()
            MeshLOD.HIGH -> {
                lod1Count.incrementAndGet()
                estimatedBytesSaved.addAndGet(ESTIMATED_LOD0_BYTES - ESTIMATED_LOD1_BYTES)
            }
            MeshLOD.MEDIUM -> {
                lod2Count.incrementAndGet()
                estimatedBytesSaved.addAndGet(ESTIMATED_LOD0_BYTES - ESTIMATED_LOD2_BYTES)
            }
            MeshLOD.LOW -> {
                lod3Count.incrementAndGet()
                estimatedBytesSaved.addAndGet(ESTIMATED_LOD0_BYTES - ESTIMATED_LOD3_BYTES)
            }
        }
    }
}
