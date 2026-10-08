package com.linkpoint.linden.llrender

import com.linkpoint.linden.llmath.Vector3
import com.linkpoint.linden.llmath.Vector4

data class LLVertex(
    val x: Float, val y: Float, val z: Float,
    val r: Float = 1f, val g: Float = 1f, val b: Float = 1f, val a: Float = 1f,
    val u: Float = 0f, val v: Float = 0f,
)

class LLRender {

    private var currentMode: BufferType = BufferType.TRIANGLES
    private var building: Boolean = false

    private val pendingVertices: MutableList<LLVertex> = mutableListOf()
    private val flushedBatches: MutableList<Pair<BufferType, List<LLVertex>>> = mutableListOf()

    private var currentColor: Vector4 = Vector4(1f, 1f, 1f, 1f)
    private var currentTexCoord: FloatArray = floatArrayOf(0f, 0f)

    var blendEnabled: Boolean = false
        private set
    var blendSrc: BlendFactor = BlendFactor.SOURCE_ALPHA
        private set
    var blendDst: BlendFactor = BlendFactor.ONE_MINUS_SOURCE_ALPHA
        private set

    var depthTestEnabled: Boolean = true
        private set
    var depthWriteEnabled: Boolean = true
        private set

    fun begin(mode: BufferType) {
        require(!building) { "Already building geometry — call end() first" }
        currentMode = mode
        building = true
        pendingVertices.clear()
    }

    fun end() {
        require(building) { "Not currently building geometry" }
        if (pendingVertices.isNotEmpty()) {
            flushedBatches.add(currentMode to pendingVertices.toList())
        }
        pendingVertices.clear()
        building = false
    }

    fun vertex(x: Float, y: Float, z: Float = 0f) {
        pendingVertices.add(
            LLVertex(x, y, z,
                currentColor.x, currentColor.y, currentColor.z, currentColor.w,
                currentTexCoord[0], currentTexCoord[1])
        )
    }

    fun vertex(v: Vector3) = vertex(v.x, v.y, v.z)

    fun color(r: Float, g: Float, b: Float, a: Float = 1f) {
        currentColor = Vector4(r, g, b, a)
    }

    fun color(c: Vector4) { currentColor = c }

    fun texCoord(u: Float, v: Float) {
        currentTexCoord[0] = u; currentTexCoord[1] = v
    }

    fun flush() {
        pendingVertices.clear()
        flushedBatches.clear()
    }

    fun setBlend(enabled: Boolean) { blendEnabled = enabled }
    fun setBlendFunc(src: BlendFactor, dst: BlendFactor) {
        blendSrc = src; blendDst = dst
    }

    fun setDepthTest(enabled: Boolean) { depthTestEnabled = enabled }
    fun setDepthWrite(enabled: Boolean) { depthWriteEnabled = enabled }

    fun getBatchCount(): Int = flushedBatches.size
    fun isBuilding(): Boolean = building
    fun getPendingVertexCount(): Int = pendingVertices.size

    fun getFlushedBatches(): List<Pair<BufferType, List<LLVertex>>> = flushedBatches.toList()
}
