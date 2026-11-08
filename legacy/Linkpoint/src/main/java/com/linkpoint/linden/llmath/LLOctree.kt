package com.linkpoint.linden.llmath

class LLOctreeNode<T : Any>(
    val center: Vector3,
    val size: Float,
    val depth: Int = 0,
    private val maxDepth: Int = 8,
    private val maxItems: Int = 8
) {
    private val items: MutableList<Pair<Vector3, T>> = mutableListOf()
    private val children: MutableList<LLOctreeNode<T>> = mutableListOf()
    private val halfSize: Float get() = size * 0.5f

    fun contains(point: Vector3): Boolean {
        val h = halfSize
        return point.x >= center.x - h && point.x <= center.x + h &&
               point.y >= center.y - h && point.y <= center.y + h &&
               point.z >= center.z - h && point.z <= center.z + h
    }

    fun insert(point: Vector3, item: T): Boolean {
        if (!contains(point)) return false
        if (children.isNotEmpty()) {
            return children.any { it.insert(point, item) }
        }
        items.add(point to item)
        if (items.size > maxItems && depth < maxDepth) subdivide()
        return true
    }

    fun query(point: Vector3, radius: Float): List<T> {
        val rSq = radius * radius
        val results = mutableListOf<T>()
        if (!intersectsSphere(point, radius)) return results
        for ((pos, item) in items) {
            if (pos.distSquared(point) <= rSq) results.add(item)
        }
        for (child in children) results.addAll(child.query(point, radius))
        return results
    }

    fun remove(item: T): Boolean {
        val iter = items.iterator()
        while (iter.hasNext()) { if (iter.next().second == item) { iter.remove(); return true } }
        return children.any { it.remove(item) }
    }

    fun clear() { items.clear(); children.clear() }

    private fun intersectsSphere(point: Vector3, radius: Float): Boolean {
        val h = halfSize + radius
        return kotlin.math.abs(point.x - center.x) <= h &&
               kotlin.math.abs(point.y - center.y) <= h &&
               kotlin.math.abs(point.z - center.z) <= h
    }

    private fun subdivide() {
        val q = halfSize * 0.5f
        val offsets = listOf(
            Vector3( q,  q,  q), Vector3(-q,  q,  q),
            Vector3( q, -q,  q), Vector3(-q, -q,  q),
            Vector3( q,  q, -q), Vector3(-q,  q, -q),
            Vector3( q, -q, -q), Vector3(-q, -q, -q)
        )
        for (off in offsets) {
            children.add(LLOctreeNode(center + off, halfSize, depth + 1, maxDepth, maxItems))
        }
        for ((pos, item) in items) {
            children.forEach { it.insert(pos, item) }
        }
        items.clear()
    }

    private fun Vector3.distSquared(other: Vector3): Float {
        val dx = x - other.x; val dy = y - other.y; val dz = z - other.z
        return dx * dx + dy * dy + dz * dz
    }
}
