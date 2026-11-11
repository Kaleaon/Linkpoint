package com.linkpoint.linden.llmath

data class LLVolumeFaceRef(val faceIndex: Int, val bounds: BBox)

class LLVolumeOctree(bounds: BBox, maxDepth: Int = 6) {
    private val root: LLOctreeNode<LLVolumeFaceRef>

    init {
        val minV = bounds.getMinLocal()
        val maxV = bounds.getMaxLocal()
        val center = (minV + maxV) * 0.5f
        val size = maxOf(
            maxV.x - minV.x,
            maxV.y - minV.y,
            maxV.z - minV.z
        )
        root = LLOctreeNode(center, size, maxDepth = maxDepth)
    }

    fun insert(faceIndex: Int, bounds: BBox) {
        val center = (bounds.getMinLocal() + bounds.getMaxLocal()) * 0.5f
        root.insert(center, LLVolumeFaceRef(faceIndex, bounds))
    }

    fun query(point: Vector3, radius: Float): List<LLVolumeFaceRef> =
        root.query(point, radius)

    fun clear() = root.clear()
}
