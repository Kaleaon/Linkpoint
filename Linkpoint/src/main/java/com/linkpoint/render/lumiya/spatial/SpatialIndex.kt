package com.linkpoint.render.lumiya.spatial

import java.util.concurrent.ConcurrentHashMap

/**
 * Loose octree spatial index for efficient frustum-culled draw-list generation.
 *
 * Design lineage: Lumiya `SpatialIndex.java` / `SpatialTree.java` /
 * `SpatialObjectIndex.java`, modernised with a proper octree, pre-allocated
 * node memory pooling, incremental node updates, depth capping at 8 levels,
 * and a diagnostic safety mechanism toggle.
 */
class SpatialIndex @JvmOverloads constructor(
    private val nodePool: OctreeNodePool = OctreeNodePool(),
    val rootMinX: Float = 0f,
    val rootMinY: Float = 0f,
    val rootMinZ: Float = 0f,
    val rootSizeX: Float = REGION_XY,
    val rootSizeY: Float = REGION_XY,
    val rootSizeZ: Float = REGION_Z
) {

    companion object {
        /** SL region extent. */
        const val REGION_XY = 256.0f
        const val REGION_Z = 4096.0f

        /** Smallest cell edge length (metres). */
        const val MIN_CELL_SIZE = 8.0f

        /** Maximum objects to return per frustum query. */
        const val MAX_RESULTS = 4096

        /** Maximum allowed octree depth to cap memory overhead in dense clusters. */
        const val MAX_DEPTH = 8
    }

    data class EntryBounds(
        val minX: Float, val minY: Float, val minZ: Float,
        val maxX: Float, val maxY: Float, val maxZ: Float
    )

    // All registered entries by ID
    private val entries = ConcurrentHashMap<Long, SpatialEntry>()
    private val entryBounds = ConcurrentHashMap<Long, EntryBounds>()

    /**
     * Diagnostic safety mechanism toggle. When [useOctree] is true (default),
     * frustum culling traverses the 3D bounding volume octree hierarchy.
     * When set to false, culling falls back to legacy linear scan iteration.
     */
    @Volatile
    var useOctree: Boolean = true

    // Root octree node
    private val root = nodePool.acquire(
        minX = rootMinX, minY = rootMinY, minZ = rootMinZ,
        sizeX = rootSizeX, sizeY = rootSizeY, sizeZ = rootSizeZ,
        depth = 1
    )

    // ── Mutation ─────────────────────────────────────────────────────────

    /**
     * Insert a new spatial entry into the octree and entry registry.
     */
    @Synchronized
    fun insert(entry: SpatialEntry) {
        val bounds = EntryBounds(entry.minX, entry.minY, entry.minZ, entry.maxX, entry.maxY, entry.maxZ)
        entries[entry.id] = entry
        entryBounds[entry.id] = bounds
        root.insert(entry, nodePool)
    }

    /**
     * Remove an entry by ID from the octree and entry registry.
     */
    @Synchronized
    fun remove(id: Long) {
        val entry = entries.remove(id)
        val bounds = entryBounds.remove(id)
        if (bounds != null) {
            root.removeByBounds(id, bounds, nodePool)
        } else if (entry != null) {
            root.removeByBounds(
                id,
                EntryBounds(entry.minX, entry.minY, entry.minZ, entry.maxX, entry.maxY, entry.maxZ),
                nodePool
            )
        }
    }

    /**
     * Perform an incremental update for a moved or rotated entry.
     * If the entry's bounding box has changed, it is removed from its previous
     * octree node and re-indexed without performing a full tree rebuild.
     */
    @Synchronized
    fun update(entry: SpatialEntry) {
        updateIncremental(entry)
    }

    /**
     * Incremental update implementation.
     */
    @Synchronized
    fun updateIncremental(entry: SpatialEntry) {
        val newBounds = EntryBounds(entry.minX, entry.minY, entry.minZ, entry.maxX, entry.maxY, entry.maxZ)
        val oldBounds = entryBounds[entry.id]

        if (oldBounds != null && oldBounds == newBounds && entries.containsKey(entry.id)) {
            // Position/rotation bounding box hasn't moved across spatial nodes; update entry in-place
            entries[entry.id] = entry
            return
        }

        if (oldBounds != null) {
            root.removeByBounds(entry.id, oldBounds, nodePool)
        } else if (entries.containsKey(entry.id)) {
            val existing = entries[entry.id]!!
            root.removeByBounds(
                entry.id,
                EntryBounds(existing.minX, existing.minY, existing.minZ, existing.maxX, existing.maxY, existing.maxZ),
                nodePool
            )
        }

        entries[entry.id] = entry
        entryBounds[entry.id] = newBounds
        root.insert(entry, nodePool)
    }

    /**
     * Clear all spatial index entries and reset the octree structure.
     */
    @Synchronized
    fun clear() {
        entries.clear()
        entryBounds.clear()
        root.clearAndRecycle(nodePool)
    }

    // ── Queries ──────────────────────────────────────────────────────────

    /**
     * Gather all entries whose AABB intersects the frustum, up to [maxResults].
     * Uses hierarchical octree traversal when [useOctree] is true, or legacy
     * linear scan when [useOctree] is false.
     */
    @Synchronized
    fun queryFrustum(culler: FrustumCuller, maxResults: Int = MAX_RESULTS): List<SpatialEntry> {
        val result = mutableListOf<SpatialEntry>()
        if (!useOctree) {
            // Diagnostic fallback: legacy linear scan
            for (entry in entries.values) {
                if (result.size >= maxResults) break
                if (culler.isAABBVisible(entry.minX, entry.minY, entry.minZ, entry.maxX, entry.maxY, entry.maxZ)) {
                    result.add(entry)
                }
            }
            return result
        }

        // Hierarchical octree traversal
        val seenSet = HashSet<Long>(maxResults.coerceAtMost(entries.size + 16))
        root.queryFrustum(culler, result, seenSet, maxResults)
        return result
    }

    val objectCount: Int get() = entries.size

    /**
     * Count the total number of active nodes currently in the octree.
     */
    fun getNodeCount(): Int = root.countNodes()

    /**
     * Return the maximum depth reached in the octree.
     */
    fun getMaxDepth(): Int = root.getMaxDepth()

    /**
     * Estimate memory footprint of spatial index nodes and tracking structures in bytes.
     */
    fun getMemoryFootprintBytes(): Long {
        val activeNodes = getNodeCount().toLong()
        val pooledNodes = nodePool.availableCount().toLong()
        val nodeBytes = (activeNodes + pooledNodes) * 128L // ~128 bytes per OctreeNode object
        val mapBytes = entries.size * 96L + entryBounds.size * 64L // Map overheads
        return nodeBytes + mapBytes
    }
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

    companion object {
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

        fun conservativeWorldHalfExtents(
            halfX: Float, halfY: Float, halfZ: Float,
            rotation: FloatArray
        ): Triple<Float, Float, Float> {
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
    var minX: Float = 0f,
    var minY: Float = 0f,
    var minZ: Float = 0f,
    var sizeX: Float = 0f,
    var sizeY: Float = 0f,
    var sizeZ: Float = 0f,
    var depth: Int = 1
) {
    companion object {
        private const val MAX_OBJECTS_PER_LEAF = 16
        const val MAX_DEPTH = 8
    }

    var maxX: Float = minX + sizeX
        private set
    var maxY: Float = minY + sizeY
        private set
    var maxZ: Float = minZ + sizeZ
        private set

    var children: Array<OctreeNode?>? = null
    val objects = mutableListOf<SpatialEntry>()

    val isLeaf: Boolean get() = children == null

    fun configure(
        minX: Float, minY: Float, minZ: Float,
        sizeX: Float, sizeY: Float, sizeZ: Float,
        depth: Int
    ) {
        this.minX = minX
        this.minY = minY
        this.minZ = minZ
        this.sizeX = sizeX
        this.sizeY = sizeY
        this.sizeZ = sizeZ
        this.depth = depth
        this.maxX = minX + sizeX
        this.maxY = minY + sizeY
        this.maxZ = minZ + sizeZ
        this.children = null
        this.objects.clear()
    }

    fun reset() {
        this.children = null
        this.objects.clear()
    }

    fun insert(entry: SpatialEntry, pool: OctreeNodePool? = null) {
        if (!intersectsEntry(entry)) return

        if (isLeaf) {
            objects.add(entry)
            if (objects.size > MAX_OBJECTS_PER_LEAF &&
                sizeX > SpatialIndex.MIN_CELL_SIZE &&
                depth < MAX_DEPTH
            ) {
                subdivide(pool)
            }
        } else {
            children?.forEach { it?.insert(entry, pool) }
        }
    }

    fun removeByBounds(id: Long, bounds: SpatialIndex.EntryBounds, pool: OctreeNodePool? = null): Boolean {
        if (!intersectsBounds(bounds)) return false

        var removed = objects.removeIf { it.id == id }

        val ch = children
        if (ch != null) {
            for (child in ch) {
                if (child != null && child.intersectsBounds(bounds)) {
                    if (child.removeByBounds(id, bounds, pool)) {
                        removed = true
                    }
                }
            }
        }

        return removed
    }

    fun remove(entry: SpatialEntry, pool: OctreeNodePool? = null) {
        val bounds = SpatialIndex.EntryBounds(entry.minX, entry.minY, entry.minZ, entry.maxX, entry.maxY, entry.maxZ)
        removeByBounds(entry.id, bounds, pool)
    }

    fun clearAndRecycle(pool: OctreeNodePool?) {
        objects.clear()
        val ch = children
        if (ch != null) {
            for (child in ch) {
                child?.clearAndRecycle(pool)
                if (child != null && pool != null) {
                    pool.recycle(child)
                }
            }
            children = null
        }
    }

    fun clear() {
        clearAndRecycle(null)
    }

    fun queryFrustum(
        culler: FrustumCuller,
        result: MutableList<SpatialEntry>,
        seenSet: HashSet<Long>,
        maxResults: Int
    ) {
        if (result.size >= maxResults) return
        when (culler.classifyAABB(minX, minY, minZ, maxX, maxY, maxZ)) {
            FrustumResult.OUTSIDE -> return
            FrustumResult.INSIDE -> collectAll(result, seenSet, maxResults)
            FrustumResult.INTERSECTS -> {
                for (obj in objects) {
                    if (result.size >= maxResults) return
                    if (!seenSet.contains(obj.id) &&
                        culler.isAABBVisible(obj.minX, obj.minY, obj.minZ, obj.maxX, obj.maxY, obj.maxZ)
                    ) {
                        seenSet.add(obj.id)
                        result.add(obj)
                    }
                }
                children?.forEach { it?.queryFrustum(culler, result, seenSet, maxResults) }
            }
        }
    }

    private fun collectAll(
        result: MutableList<SpatialEntry>,
        seenSet: HashSet<Long>,
        maxResults: Int
    ) {
        for (obj in objects) {
            if (result.size >= maxResults) return
            if (seenSet.add(obj.id)) {
                result.add(obj)
            }
        }
        children?.forEach { child ->
            if (result.size >= maxResults) return
            child?.collectAll(result, seenSet, maxResults)
        }
    }

    private fun subdivide(pool: OctreeNodePool?) {
        val hx = sizeX / 2f; val hy = sizeY / 2f; val hz = sizeZ / 2f
        val nextDepth = depth + 1

        if (pool != null) {
            children = arrayOf(
                pool.acquire(minX,      minY,      minZ,      hx, hy, hz, nextDepth),
                pool.acquire(minX + hx, minY,      minZ,      hx, hy, hz, nextDepth),
                pool.acquire(minX,      minY + hy, minZ,      hx, hy, hz, nextDepth),
                pool.acquire(minX + hx, minY + hy, minZ,      hx, hy, hz, nextDepth),
                pool.acquire(minX,      minY,      minZ + hz, hx, hy, hz, nextDepth),
                pool.acquire(minX + hx, minY,      minZ + hz, hx, hy, hz, nextDepth),
                pool.acquire(minX,      minY + hy, minZ + hz, hx, hy, hz, nextDepth),
                pool.acquire(minX + hx, minY + hy, minZ + hz, hx, hy, hz, nextDepth)
            )
        } else {
            children = arrayOf(
                OctreeNode(minX,      minY,      minZ,      hx, hy, hz, nextDepth),
                OctreeNode(minX + hx, minY,      minZ,      hx, hy, hz, nextDepth),
                OctreeNode(minX,      minY + hy, minZ,      hx, hy, hz, nextDepth),
                OctreeNode(minX + hx, minY + hy, minZ,      hx, hy, hz, nextDepth),
                OctreeNode(minX,      minY,      minZ + hz, hx, hy, hz, nextDepth),
                OctreeNode(minX + hx, minY,      minZ + hz, hx, hy, hz, nextDepth),
                OctreeNode(minX,      minY + hy, minZ + hz, hx, hy, hz, nextDepth),
                OctreeNode(minX + hx, minY + hy, minZ + hz, hx, hy, hz, nextDepth)
            )
        }

        val toRedistribute = ArrayList(objects)
        objects.clear()
        for (obj in toRedistribute) {
            children?.forEach { it?.insert(obj, pool) }
        }
    }


    fun countNodes(): Int {
        var count = 1
        children?.forEach { child ->
            if (child != null) {
                count += child.countNodes()
            }
        }
        return count
    }

    fun getMaxDepth(): Int {
        var maxD = depth
        children?.forEach { child ->
            if (child != null) {
                maxD = maxOf(maxD, child.getMaxDepth())
            }
        }
        return maxD
    }

    private fun intersectsEntry(entry: SpatialEntry): Boolean {
        return entry.maxX >= minX && entry.minX <= maxX &&
               entry.maxY >= minY && entry.minY <= maxY &&
               entry.maxZ >= minZ && entry.minZ <= maxZ
    }

    private fun intersectsBounds(b: SpatialIndex.EntryBounds): Boolean {
        return b.maxX >= minX && b.minX <= maxX &&
               b.maxY >= minY && b.minY <= maxY &&
               b.maxZ >= minZ && b.minZ <= maxZ
    }
}
