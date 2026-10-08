package com.linkpoint.linden.llprimitive

import com.linkpoint.linden.llmath.BBox
import com.linkpoint.linden.llrender.VertexBuffer

class LLFace(
    val faceIndex: Int,
    var textureEntry: TextureEntry? = null,
    var vertexBuffer: VertexBuffer? = null
) {
    var dirty: Boolean = true
    var geomCount: Int = 0
    var indicesCount: Int = 0
    var geomIndex: Int = 0
    var textureIndex: Int = 0
    var materialId: LLMaterialID = LLMaterialID.NULL
    var material: LLMaterial? = null

    private var bounds: BBox = BBox()

    fun getBounds(): BBox = bounds
    fun setBounds(b: BBox) { bounds = b }

    fun markDirty() { dirty = true }
    fun clearDirty() { dirty = false }

    fun hasVertexBuffer(): Boolean = vertexBuffer != null
    fun hasMaterial(): Boolean = material != null

    fun isTransparent(): Boolean = textureEntry?.let { te ->
        te.color.a < 1f || te.glow > 0f
    } ?: false
}
