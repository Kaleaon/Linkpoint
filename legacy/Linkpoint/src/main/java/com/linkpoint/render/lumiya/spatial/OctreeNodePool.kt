package com.linkpoint.render.lumiya.spatial

import java.util.ArrayDeque

/**
 * Pre-allocated pool of [OctreeNode] instances to avoid heap allocation stalls
 * on the hot render path during dynamic object reallocation and node splitting.
 */
class OctreeNodePool(
    initialCapacity: Int = DEFAULT_INITIAL_CAPACITY,
    private val maxPoolSize: Int = DEFAULT_MAX_POOL_SIZE
) {
    companion object {
        const val DEFAULT_INITIAL_CAPACITY = 2048
        const val DEFAULT_MAX_POOL_SIZE = 8192
    }

    private val pool = ArrayDeque<OctreeNode>(initialCapacity)

    init {
        synchronized(pool) {
            for (i in 0 until initialCapacity) {
                pool.addLast(OctreeNode())
            }
        }
    }

    /**
     * Acquire an OctreeNode configured with the specified bounds and depth.
     */
    fun acquire(
        minX: Float, minY: Float, minZ: Float,
        sizeX: Float, sizeY: Float, sizeZ: Float,
        depth: Int
    ): OctreeNode {
        val node = synchronized(pool) {
            if (pool.isNotEmpty()) pool.removeLast() else null
        } ?: OctreeNode()

        node.configure(minX, minY, minZ, sizeX, sizeY, sizeZ, depth)
        return node
    }

    /**
     * Recycle a node and all of its child nodes back into the pool.
     */
    fun recycle(node: OctreeNode) {
        val children = node.children
        if (children != null) {
            for (child in children) {
                if (child != null) {
                    recycle(child)
                }
            }
            node.children = null
        }
        node.reset()

        synchronized(pool) {
            if (pool.size < maxPoolSize) {
                pool.addLast(node)
            }
        }
    }

    /**
     * Return the number of currently available idle nodes in the pool.
     */
    fun availableCount(): Int = synchronized(pool) { pool.size }

    /**
     * Clear all pooled instances.
     */
    fun clear() {
        synchronized(pool) {
            pool.clear()
        }
    }
}
