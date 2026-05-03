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
 *
 * The half-extents are always axis-aligned in world space. For prims
 * with a non-identity rotation, callers should expand the local
 * half-extents into a conservative world-space AABB via
 * [SpatialEntry.fromOrientedBox] so a rotated long thin prim doesn't
 * pop in/out of view as it crosses an octree boundary.
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

    companion object {
        /**
         * Build a [SpatialEntry] from an oriented box (local-space half
         * extents + world rotation). The resulting AABB is the
         * smallest axis-aligned box that fully contains the rotated
         * box — an over-estimate, never under-estimate. Inserting
         * rotated prims this way keeps frustum / octree queries
         * correct without per-object OBB tests.
         *
         * Lineage: LL viewer `LLXformMatrix::updateBoundingBoxes` ->
         * `LLDrawable::updateXform`. Singularity uses the same trick
         * and notes "we over-estimate, conservative but works" — the
         * exact phrase mirrored in the work-list this implements.
         *
         * @param posX/Y/Z World-space center of the box.
         * @param halfX/Y/Z Local-space half-extents along the box's
         *                  own axes.
         * @param rotation 4x4 column-major rotation matrix; only the
         *                 upper-left 3x3 is read. Translation columns
         *                 are ignored — pass any matrix shaped like a
         *                 GLES model matrix.
         */
        fun fromOrientedBox(
            id: Long,
            posX: Float, posY: Float, posZ: Float,
            halfX: Float, halfY: Float, halfZ: Float,
            rotation: FloatArray,
            isAvatar: Boolean = false
        ): SpatialEntry {
            val (wx, wy, wz) = conservativeWorldHalfExtents(halfX, halfY, halfZ, rotation)
            return SpatialEntry(
                id = id,
                posX = posX, posY = posY, posZ = posZ,
                halfExtentX = wx, halfExtentY = wy, halfExtentZ = wz,
                isAvatar = isAvatar
            )
        }

        /**
         * Conservative world-space half-extents of a local OBB.
         *
         * Computed as the absolute-value sum of each axis projected
         * through the rotation: |R| * h. This is the standard OBB ->
         * AABB widening: each world axis collects the absolute
         * contribution from every local axis. It exactly bounds the
         * rotated box's eight corners (no slack beyond what rotation
         * introduces).
         *
         * Returns (halfX, halfY, halfZ) in world units.
         */
        fun conservativeWorldHalfExtents(
            halfX: Float, halfY: Float, halfZ: Float,
            rotation: FloatArray
        ): Triple<Float, Float, Float> {
            // Column-major: column k = rotation[k*4 .. k*4+2].
            // World axis i contribution = |R[i,0]|*hx + |R[i,1]|*hy + |R[i,2]|*hz.
            // R[i,k] in row-i, col-k = rotation[k*4 + i].
            val ax0 = Math.abs(rotation[0]); val ax1 = Math.abs(rotation[4]); val ax2 = Math.abs(rotation[8])
            val ay0 = Math.abs(rotation[1]); val ay1 = Math.abs(rotation[5]); val ay2 = Math.abs(rotation[9])
            val az0 = Math.abs(rotation[2]); val az1 = Math.abs(rotation[6]); val az2 = Math.abs(rotation[10])
            val wx = ax0 * halfX + ax1 * halfY + ax2 * halfZ
            val wy = ay0 * halfX + ay1 * halfY + ay2 * halfZ
            val wz = az0 * halfX + az1 * halfY + az2 * halfZ
            return Triple(wx, wy, wz)
        }
    }
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
        when (culler.classifyAABB(minX, minY, minZ, maxX, maxY, maxZ)) {
            FrustumResult.OUTSIDE -> return
            FrustumResult.INSIDE -> collectAll(result, maxResults)
            FrustumResult.INTERSECTS -> {
                for (obj in objects) {
                    if (result.size >= maxResults) return
                    if (culler.isAABBVisible(obj.minX, obj.minY, obj.minZ, obj.maxX, obj.maxY, obj.maxZ)) {
                        result.add(obj)
                    }
                }
                children?.forEach { it?.queryFrustum(culler, result, maxResults) }
            }
        }
    }

    /**
     * Drain every object in this subtree into [result] without further
     * frustum checks. Called once the parent tri-state classifier has
     * proven the whole node sits inside the frustum.
     */
    private fun collectAll(result: MutableList<SpatialEntry>, maxResults: Int) {
        for (obj in objects) {
            if (result.size >= maxResults) return
            result.add(obj)
        }
        children?.forEach { child ->
            if (result.size >= maxResults) return
            child?.collectAll(result, maxResults)
        }
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
