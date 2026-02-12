package com.linkpoint.render.lumiya.spatial

import android.util.Log
import java.util.concurrent.ConcurrentHashMap

/**
 * Loose octree spatial index for efficient frustum-culled draw-list generation.
 *
 * Design lineage: Lumiya `SpatialIndex.java` / `SpatialTree.java` /
 * `SpatialObjectIndex.java`, modernised with a proper octree instead of a
 * simple depth-binned linked list.
 *
 * The tree covers the standard SL region (256 × 256 × 4096) and subdivides
 * down to a configurable minimum cell size.
 */
class SpatialIndex {

    companion object {
        private const val TAG = "SpatialIndex"

        /** SL region extent. */
        const val REGION_XY = 256.0f
        const val REGION_Z = 4096.0f

        /** Smallest cell edge length (metres). */
        const val MIN_CELL_SIZE = 8.0f

        /** Maximum objects to return per frustum query. */
        const val MAX_RESULTS = 4096
    }

    // All registered entries by ID
    private val entries = ConcurrentHashMap<Long, SpatialEntry>()

    // Root octree node
    private val root = OctreeNode(
        minX = 0f, minY = 0f, minZ = 0f,
        sizeX = REGION_XY, sizeY = REGION_XY, sizeZ = REGION_Z
    )

    // ── Mutation ─────────────────────────────────────────────────────────

    fun insert(entry: SpatialEntry) {
        entries[entry.id] = entry
        root.insert(entry)
    }

    fun remove(id: Long) {
        entries.remove(id)?.let { root.remove(it) }
    }

    fun update(entry: SpatialEntry) {
        remove(entry.id)
        insert(entry)
    }

    fun clear() {
        entries.clear()
        root.clear()
    }

    // ── Queries ──────────────────────────────────────────────────────────

    /**
     * Gather all entries whose AABB intersects the frustum, up to [maxResults].
     */
    fun queryFrustum(culler: FrustumCuller, maxResults: Int = MAX_RESULTS): List<SpatialEntry> {
        val result = mutableListOf<SpatialEntry>()
        root.queryFrustum(culler, result, maxResults)
        return result
    }

    val objectCount: Int get() = entries.size
}

/**
 * A single spatial entry representing an object's bounding volume.
 */
data class SpatialEntry(
    val id: Long,
    var posX: Float,
    var posY: Float,
    var posZ: Float,
    /** Half-extents for AABB. */
    var halfExtentX: Float = 1.0f,
    var halfExtentY: Float = 1.0f,
    var halfExtentZ: Float = 1.0f,
    /** Distance from camera (updated per frame). */
    var distanceToCamera: Float = 0f,
    /** Whether this entry is an avatar. */
    var isAvatar: Boolean = false
) {
    val minX get() = posX - halfExtentX
    val minY get() = posY - halfExtentY
    val minZ get() = posZ - halfExtentZ
    val maxX get() = posX + halfExtentX
    val maxY get() = posY + halfExtentY
    val maxZ get() = posZ + halfExtentZ
}

/**
 * Octree node for spatial partitioning.
 */
class OctreeNode(
    val minX: Float, val minY: Float, val minZ: Float,
    val sizeX: Float, val sizeY: Float, val sizeZ: Float
) {
    companion object {
        private const val MAX_OBJECTS_PER_LEAF = 16
    }

    private val maxX get() = minX + sizeX
    private val maxY get() = minY + sizeY
    private val maxZ get() = minZ + sizeZ

    private var children: Array<OctreeNode?>? = null
    private val objects = mutableListOf<SpatialEntry>()

    private val isLeaf: Boolean get() = children == null

    fun insert(entry: SpatialEntry) {
        if (!intersects(entry)) return

        if (isLeaf) {
            objects.add(entry)
            if (objects.size > MAX_OBJECTS_PER_LEAF && sizeX > SpatialIndex.MIN_CELL_SIZE) {
                subdivide()
            }
        } else {
            children?.forEach { it?.insert(entry) }
        }
    }

    fun remove(entry: SpatialEntry) {
        if (!intersects(entry)) return
        objects.remove(entry)
        children?.forEach { it?.remove(entry) }
    }

    fun clear() {
        objects.clear()
        children?.forEach { it?.clear() }
        children = null
    }

    fun queryFrustum(culler: FrustumCuller, result: MutableList<SpatialEntry>, maxResults: Int) {
        if (result.size >= maxResults) return
        if (!culler.isAABBVisible(minX, minY, minZ, maxX, maxY, maxZ)) return

        for (obj in objects) {
            if (result.size >= maxResults) return
            if (culler.isAABBVisible(obj.minX, obj.minY, obj.minZ, obj.maxX, obj.maxY, obj.maxZ)) {
                result.add(obj)
            }
        }

        children?.forEach { it?.queryFrustum(culler, result, maxResults) }
    }

    private fun subdivide() {
        val hx = sizeX / 2f; val hy = sizeY / 2f; val hz = sizeZ / 2f
        children = arrayOf(
            OctreeNode(minX,      minY,      minZ,      hx, hy, hz),
            OctreeNode(minX + hx, minY,      minZ,      hx, hy, hz),
            OctreeNode(minX,      minY + hy, minZ,      hx, hy, hz),
            OctreeNode(minX + hx, minY + hy, minZ,      hx, hy, hz),
            OctreeNode(minX,      minY,      minZ + hz, hx, hy, hz),
            OctreeNode(minX + hx, minY,      minZ + hz, hx, hy, hz),
            OctreeNode(minX,      minY + hy, minZ + hz, hx, hy, hz),
            OctreeNode(minX + hx, minY + hy, minZ + hz, hx, hy, hz)
        )
        // Re-distribute existing objects
        val toRedistribute = ArrayList(objects)
        objects.clear()
        for (obj in toRedistribute) {
            children?.forEach { it?.insert(obj) }
        }
    }

    private fun intersects(entry: SpatialEntry): Boolean {
        return entry.maxX >= minX && entry.minX <= maxX &&
               entry.maxY >= minY && entry.minY <= maxY &&
               entry.maxZ >= minZ && entry.minZ <= maxZ
    }
}
