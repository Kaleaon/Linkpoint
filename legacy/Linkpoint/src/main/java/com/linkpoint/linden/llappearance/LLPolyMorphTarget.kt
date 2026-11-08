package com.linkpoint.linden.llappearance

import com.linkpoint.linden.llmath.Vector3

data class MorphDelta(
    val vertexIndex: Int,
    val posDelta: Vector3,
    val normalDelta: Vector3 = Vector3.ZERO,
)

class LLPolyMorphTarget(val name: String) {

    private val deltas: MutableList<MorphDelta> = mutableListOf()
    var currentWeight: Float = 0f
        private set

    fun addDelta(delta: MorphDelta) {
        deltas.add(delta)
    }

    fun addDelta(vertexIndex: Int, dx: Float, dy: Float, dz: Float,
                 nx: Float = 0f, ny: Float = 0f, nz: Float = 0f) {
        deltas.add(MorphDelta(
            vertexIndex,
            Vector3(dx, dy, dz),
            Vector3(nx, ny, nz),
        ))
    }

    fun apply(baseVertices: FloatArray, baseNormals: FloatArray, weight: Float): Boolean {
        if (baseVertices.size % 3 != 0) return false
        val maxIndex = baseVertices.size / 3
        val delta = weight - currentWeight
        for (d in deltas) {
            if (d.vertexIndex >= maxIndex) continue
            val vi = d.vertexIndex * 3
            baseVertices[vi]     += d.posDelta.x * delta
            baseVertices[vi + 1] += d.posDelta.y * delta
            baseVertices[vi + 2] += d.posDelta.z * delta
            if (baseNormals.size == baseVertices.size) {
                baseNormals[vi]     += d.normalDelta.x * delta
                baseNormals[vi + 1] += d.normalDelta.y * delta
                baseNormals[vi + 2] += d.normalDelta.z * delta
            }
        }
        currentWeight = weight
        return true
    }

    fun reset(baseVertices: FloatArray, baseNormals: FloatArray): Boolean =
        apply(baseVertices, baseNormals, 0f)

    fun getDeltaCount(): Int = deltas.size

    fun getDeltas(): List<MorphDelta> = deltas.toList()

    override fun toString(): String = "LLPolyMorphTarget($name, deltas=${deltas.size}, weight=$currentWeight)"
}
