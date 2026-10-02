package com.linkpoint.assets.pool

import android.util.Log
import com.linkpoint.assets.TexturePriority
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.PriorityBlockingQueue

/**
 * Priority queue for asset loading and decoding requests.
 *
 * Orders assets by:
 *  1. Asset Priority class (CRITICAL, HIGH, NORMAL, LOW, PREFETCH).
 *  2. Frustum visibility (assets in the immediate camera view frustum load first).
 *  3. Distance to camera (nearer assets prioritized over farther assets).
 *
 * Provides immediate cancellation (`cancelAll`, `cancelStale`) on teleports or region crossings.
 */
class FrustumPriorityLoadingQueue {

    companion object {
        private const val TAG = "FrustumPriorityLoadingQueue"
        private const val DEFAULT_CAPACITY = 256
    }

    data class AssetLoadingRequest(
        val assetId: UUID,
        val type: AssetCategory,
        val priority: TexturePriority = TexturePriority.NORMAL,
        val worldPositionX: Float? = null,
        val worldPositionY: Float? = null,
        val worldPositionZ: Float? = null,
        val createdAtMs: Long = System.currentTimeMillis(),
        val payload: Any? = null
    ) : Comparable<AssetLoadingRequest> {

        var computedDistanceMeters: Float = Float.MAX_VALUE
        var isCurrentlyInFrustum: Boolean = true

        override fun compareTo(other: AssetLoadingRequest): Int {
            // 1. Higher priority enum value first (CRITICAL(0) < HIGH(1) < ... < PREFETCH(4))
            val priorityDiff = priority.value.compareTo(other.priority.value)
            if (priorityDiff != 0) return priorityDiff

            // 2. Frustum visibility: in-frustum assets come before out-of-frustum assets
            if (isCurrentlyInFrustum != other.isCurrentlyInFrustum) {
                return if (isCurrentlyInFrustum) -1 else 1
            }

            // 3. Distance to camera: closer assets first
            val distDiff = computedDistanceMeters.compareTo(other.computedDistanceMeters)
            if (distDiff != 0) return distDiff

            // 4. FIFO for ties
            return createdAtMs.compareTo(other.createdAtMs)
        }
    }

    enum class AssetCategory {
        TEXTURE,
        MESH
    }

    private val queue = PriorityBlockingQueue<AssetLoadingRequest>(DEFAULT_CAPACITY)
    private val pendingAssetMap = ConcurrentHashMap<UUID, AssetLoadingRequest>()

    // Camera state for frustum & distance sorting
    @Volatile var cameraX: Float = 0f; private set
    @Volatile var cameraY: Float = 0f; private set
    @Volatile var cameraZ: Float = 0f; private set
    @Volatile var frustumCullingEnabled: Boolean = true; private set

    /**
     * Update camera position to refresh distance and frustum prioritization.
     */
    fun updateCameraPosition(x: Float, y: Float, z: Float) {
        cameraX = x
        cameraY = y
        cameraZ = z
        recalculateDistances()
    }

    /**
     * Offer a new request to the loading queue with frustum & distance calculation.
     */
    fun enqueue(request: AssetLoadingRequest): Boolean {
        if (pendingAssetMap.containsKey(request.assetId)) {
            return false // Already queued
        }

        updateRequestMetrics(request)
        pendingAssetMap[request.assetId] = request
        val added = queue.offer(request)
        if (!added) {
            pendingAssetMap.remove(request.assetId)
        }
        return added
    }

    /**
     * Poll the next highest-priority request from the queue.
     */
    fun poll(): AssetLoadingRequest? {
        val req = queue.poll()
        if (req != null) {
            pendingAssetMap.remove(req.assetId)
        }
        return req
    }

    /**
     * Remove a specific request by asset UUID.
     */
    fun remove(assetId: UUID): Boolean {
        val req = pendingAssetMap.remove(assetId) ?: return false
        return queue.remove(req)
    }

    /**
     * Cancel all pending requests in the queue (e.g. during region transfer or teleport).
     */
    fun cancelAll(): Int {
        val count = queue.size
        queue.clear()
        pendingAssetMap.clear()
        Log.i(TAG, "Cancelled $count queued asset loading requests")
        return count
    }

    /**
     * Cancel stale requests older than maxAgeMs or outside current view distance.
     */
    fun cancelStale(maxAgeMs: Long = 45000L, maxDistanceMeters: Float = 256f): Int {
        val now = System.currentTimeMillis()
        var cancelled = 0

        val iterator = queue.iterator()
        while (iterator.hasNext()) {
            val req = iterator.next()
            val age = now - req.createdAtMs
            val isFar = req.computedDistanceMeters > maxDistanceMeters

            if (age > maxAgeMs || (isFar && req.priority == TexturePriority.PREFETCH)) {
                iterator.remove()
                pendingAssetMap.remove(req.assetId)
                cancelled++
            }
        }

        if (cancelled > 0) {
            Log.i(TAG, "Purged $cancelled stale/distant requests from queue")
        }
        return cancelled
    }

    /**
     * Recalculate distance and frustum status for all queued items when camera moves.
     */
    private fun recalculateDistances() {
        if (queue.isEmpty()) return

        val items = queue.toList()
        var modified = false
        for (item in items) {
            if (updateRequestMetrics(item)) {
                modified = true
            }
        }

        if (modified) {
            // Re-sort priority queue by re-offering elements
            val snapshot = ArrayList<AssetLoadingRequest>()
            queue.drainTo(snapshot)
            queue.addAll(snapshot)
        }
    }

    private fun updateRequestMetrics(req: AssetLoadingRequest): Boolean {
        if (req.worldPositionX == null || req.worldPositionY == null || req.worldPositionZ == null) {
            req.computedDistanceMeters = 0f
            req.isCurrentlyInFrustum = true
            return false
        }

        val dx = req.worldPositionX - cameraX
        val dy = req.worldPositionY - cameraY
        val dz = req.worldPositionZ - cameraZ
        val dist = kotlin.math.sqrt(dx * dx + dy * dy + dz * dz)

        val oldDist = req.computedDistanceMeters
        req.computedDistanceMeters = dist
        req.isCurrentlyInFrustum = dist < 128f // Simplified frustum distance boundary

        return kotlin.math.abs(oldDist - dist) > 1.0f
    }

    val size: Int get() = queue.size
    fun isEmpty(): Boolean = queue.isEmpty()
}
