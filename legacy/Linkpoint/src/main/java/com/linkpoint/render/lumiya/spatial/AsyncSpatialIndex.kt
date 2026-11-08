package com.linkpoint.render.lumiya.spatial

import android.util.Log
import com.linkpoint.scene.worker.SceneWorkerPool
import com.linkpoint.scene.worker.TaskPriority
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicReference

/**
 * Asynchronous spatial octree wrapper that offloads tree construction, insertions,
 * deletions, and subdivisions off the main UI rendering thread.
 *
 * Keeps a thread-safe snapshot reference so the main display GL thread can read
 * spatial query results on frame boundaries without blocking.
 */
class AsyncSpatialIndex(
    private val workerPool: SceneWorkerPool = SceneWorkerPool.getInstance()
) {
    companion object {
        private const val TAG = "AsyncSpatialIndex"
    }

    private val underlyingIndex = SpatialIndex()
    private val entriesSnapshot = AtomicReference<List<SpatialEntry>>(emptyList())
    private val activeEntries = ConcurrentHashMap<Long, SpatialEntry>()

    /**
     * Asynchronously insert an entry into the spatial octree.
     */
    fun insertAsync(entry: SpatialEntry): CompletableFuture<Void?> {
        activeEntries[entry.id] = entry
        return workerPool.submitCallable(TaskPriority.HIGH, "OctreeInsert-${entry.id}") {
            underlyingIndex.insert(entry)
            updateSnapshot()
            null
        }
    }

    /**
     * Asynchronously update an entry in the spatial octree.
     */
    fun updateAsync(entry: SpatialEntry): CompletableFuture<Void?> {
        activeEntries[entry.id] = entry
        return workerPool.submitCallable(TaskPriority.HIGH, "OctreeUpdate-${entry.id}") {
            underlyingIndex.update(entry)
            updateSnapshot()
            null
        }
    }

    /**
     * Asynchronously remove an entry from the spatial octree.
     */
    fun removeAsync(id: Long): CompletableFuture<Void?> {
        activeEntries.remove(id)
        return workerPool.submitCallable(TaskPriority.HIGH, "OctreeRemove-$id") {
            underlyingIndex.remove(id)
            updateSnapshot()
            null
        }
    }

    /**
     * Asynchronously clear the spatial octree.
     */
    fun clearAsync(): CompletableFuture<Void?> {
        activeEntries.clear()
        return workerPool.submitCallable(TaskPriority.HIGH, "OctreeClear") {
            underlyingIndex.clear()
            updateSnapshot()
            null
        }
    }

    /**
     * Query frustum culling on background thread or read current snapshot.
     */
    fun queryFrustumAsync(
        culler: FrustumCuller,
        maxResults: Int = SpatialIndex.MAX_RESULTS
    ): CompletableFuture<List<SpatialEntry>> {
        return workerPool.submitCallable(TaskPriority.HIGH, "OctreeFrustumQuery") {
            underlyingIndex.queryFrustum(culler, maxResults)
        }
    }

    /**
     * Synchronously query frustum using current snapshot (non-blocking for GL thread).
     */
    fun queryFrustumSnapshot(culler: FrustumCuller, maxResults: Int = SpatialIndex.MAX_RESULTS): List<SpatialEntry> {
        val snapshot = entriesSnapshot.get()
        val result = mutableListOf<SpatialEntry>()
        for (entry in snapshot) {
            if (result.size >= maxResults) break
            if (culler.isAABBVisible(entry.minX, entry.minY, entry.minZ, entry.maxX, entry.maxY, entry.maxZ)) {
                result.add(entry)
            }
        }
        return result
    }

    private fun updateSnapshot() {
        entriesSnapshot.set(ArrayList(activeEntries.values))
    }

    val objectCount: Int get() = activeEntries.size

    val underlying: SpatialIndex get() = underlyingIndex
}
