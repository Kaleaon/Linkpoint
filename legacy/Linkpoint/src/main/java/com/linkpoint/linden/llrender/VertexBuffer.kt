package com.linkpoint.linden.llrender

import com.linkpoint.linden.llmath.Color4
import com.linkpoint.linden.llmath.Vector2
import com.linkpoint.linden.llmath.Vector3
import java.nio.ByteBuffer
import java.nio.ByteOrder

class VertexBuffer(
    private val typeMask: UInt,
    private val numVerts: Int,
    private val numIndices: Int
) {
    data class UploadSnapshot(
        val typeMask: UInt,
        val vertexBytes: ByteArray?,
        val normalBytes: ByteArray?,
        val texCoordBytes: List<ByteArray?>,
        val colorBytes: ByteArray?,
        val indexBytes: ByteArray?
    )

    companion object {
        val MAP_VERTEX: UInt        = (1u shl 0)
        val MAP_NORMAL: UInt        = (1u shl 1)
        val MAP_TEXCOORD0: UInt     = (1u shl 2)
        val MAP_TEXCOORD1: UInt     = (1u shl 3)
        val MAP_TEXCOORD2: UInt     = (1u shl 4)
        val MAP_TEXCOORD3: UInt     = (1u shl 5)
        val MAP_COLOR: UInt         = (1u shl 6)
        val MAP_EMISSIVE: UInt      = (1u shl 7)
        val MAP_TANGENT: UInt       = (1u shl 8)
        val MAP_WEIGHT: UInt        = (1u shl 9)
        val MAP_WEIGHT4: UInt       = (1u shl 10)
        val MAP_CLOTHWEIGHT: UInt   = (1u shl 11)
        val MAP_JOINT: UInt         = (1u shl 12)
        val MAP_TEXTURE_INDEX: UInt = (1u shl 13)

        private const val VERTEX_STRIDE = 12    // 3 × F32
        private const val NORMAL_STRIDE = 12    // 3 × F32
        private const val TEXCOORD_STRIDE = 8   // 2 × F32
        private const val COLOR_STRIDE = 4      // 4 × U8
        private const val INDEX_STRIDE = 2      // U16
    }

    private val vertexData: ByteBuffer? =
        if (typeMask and MAP_VERTEX != 0u) allocDirect(numVerts * VERTEX_STRIDE) else null

    private val normalData: ByteBuffer? =
        if (typeMask and MAP_NORMAL != 0u) allocDirect(numVerts * NORMAL_STRIDE) else null

    private val texCoordData: Array<ByteBuffer?> = Array(4) { ch ->
        val mask = MAP_TEXCOORD0 shl ch
        if (typeMask and mask != 0u) allocDirect(numVerts * TEXCOORD_STRIDE) else null
    }

    private val colorData: ByteBuffer? =
        if (typeMask and MAP_COLOR != 0u) allocDirect(numVerts * COLOR_STRIDE) else null

    private val indexData: ByteBuffer? =
        if (numIndices > 0) allocDirect(numIndices * INDEX_STRIDE) else null

    private var uploadedSnapshot: UploadSnapshot? = null
    private var isCurrentlyBound: Boolean = false

    private fun allocDirect(bytes: Int): ByteBuffer =
        ByteBuffer.allocateDirect(bytes).order(ByteOrder.nativeOrder())

    fun putVertex(index: Int, v: Vector3) {
        val buf = requireNotNull(vertexData) { "MAP_VERTEX not set" }
        val off = index * VERTEX_STRIDE
        buf.putFloat(off, v.x)
        buf.putFloat(off + 4, v.y)
        buf.putFloat(off + 8, v.z)
    }

    fun getVertex(index: Int): Vector3 {
        val buf = requireNotNull(vertexData) { "MAP_VERTEX not set" }
        val off = index * VERTEX_STRIDE
        return Vector3(buf.getFloat(off), buf.getFloat(off + 4), buf.getFloat(off + 8))
    }

    fun putNormal(index: Int, n: Vector3) {
        val buf = requireNotNull(normalData) { "MAP_NORMAL not set" }
        val off = index * NORMAL_STRIDE
        buf.putFloat(off, n.x)
        buf.putFloat(off + 4, n.y)
        buf.putFloat(off + 8, n.z)
    }

    fun getNormal(index: Int): Vector3 {
        val buf = requireNotNull(normalData) { "MAP_NORMAL not set" }
        val off = index * NORMAL_STRIDE
        return Vector3(buf.getFloat(off), buf.getFloat(off + 4), buf.getFloat(off + 8))
    }

    fun putTexCoord(index: Int, channel: Int, tc: Vector2) {
        require(channel in 0..3)
        val buf = requireNotNull(texCoordData[channel]) { "MAP_TEXCOORD$channel not set" }
        val off = index * TEXCOORD_STRIDE
        buf.putFloat(off, tc.x)
        buf.putFloat(off + 4, tc.y)
    }

    fun getTexCoord(index: Int, channel: Int): Vector2 {
        require(channel in 0..3)
        val buf = requireNotNull(texCoordData[channel]) { "MAP_TEXCOORD$channel not set" }
        val off = index * TEXCOORD_STRIDE
        return Vector2(buf.getFloat(off), buf.getFloat(off + 4))
    }

    fun putColor(index: Int, c: Color4) {
        val buf = requireNotNull(colorData) { "MAP_COLOR not set" }
        val off = index * COLOR_STRIDE
        buf.put(off, (c.r * 255f + 0.5f).toInt().coerceIn(0, 255).toByte())
        buf.put(off + 1, (c.g * 255f + 0.5f).toInt().coerceIn(0, 255).toByte())
        buf.put(off + 2, (c.b * 255f + 0.5f).toInt().coerceIn(0, 255).toByte())
        buf.put(off + 3, (c.a * 255f + 0.5f).toInt().coerceIn(0, 255).toByte())
    }

    fun getColor(index: Int): Color4 {
        val buf = requireNotNull(colorData) { "MAP_COLOR not set" }
        val off = index * COLOR_STRIDE
        return Color4(
            (buf.get(off).toInt() and 0xFF) / 255f,
            (buf.get(off + 1).toInt() and 0xFF) / 255f,
            (buf.get(off + 2).toInt() and 0xFF) / 255f,
            (buf.get(off + 3).toInt() and 0xFF) / 255f
        )
    }

    fun putIndex(i: Int, value: Short) {
        val buf = requireNotNull(indexData) { "No index buffer allocated" }
        buf.putShort(i * INDEX_STRIDE, value)
    }

    fun getIndex(i: Int): Short {
        val buf = requireNotNull(indexData) { "No index buffer allocated" }
        return buf.getShort(i * INDEX_STRIDE)
    }

    fun getNumVerts(): Int = numVerts
    fun getNumIndices(): Int = numIndices
    fun getTypeMask(): UInt = typeMask

    fun flush() {
        uploadedSnapshot = UploadSnapshot(
            typeMask = typeMask,
            vertexBytes = vertexData?.copyBytes(),
            normalBytes = normalData?.copyBytes(),
            texCoordBytes = texCoordData.map { it?.copyBytes() },
            colorBytes = colorData?.copyBytes(),
            indexBytes = indexData?.copyBytes()
        )
    }

    fun bind() {
        if (uploadedSnapshot == null) {
            flush()
        }
        isCurrentlyBound = true
    }

    fun unbind() {
        isCurrentlyBound = false
    }

    fun isBound(): Boolean = isCurrentlyBound

    fun getUploadedSnapshot(): UploadSnapshot? = uploadedSnapshot

    private fun ByteBuffer.copyBytes(): ByteArray {
        val copy = ByteArray(capacity())
        val src = duplicate()
        src.position(0)
        src.limit(capacity())
        src.get(copy)
        return copy
    }
}
