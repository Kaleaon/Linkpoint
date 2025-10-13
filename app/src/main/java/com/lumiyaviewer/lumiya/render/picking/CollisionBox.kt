package com.lumiyaviewer.lumiya.render.picking

import com.lumiyaviewer.lumiya.slproto.types.LLVector3

class CollisionBox private constructor() {

    companion object {
        @JvmStatic
        fun getInstance(): CollisionBox = INSTANCE

        private val INSTANCE = CollisionBox()
    }

    val vertices = Array<LLVector3>(36) { LLVector3(0f, 0f, 0f) }

    init {
        // Initialize collision box faces
        addCollisionFace(0, -0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0)
        addCollisionFace(1, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, 0)
        addCollisionFace(2, -0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 1)
        addCollisionFace(3, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, 1)
        addCollisionFace(4, -0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 2)
        addCollisionFace(5, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, 2)
    }

    private fun addCollisionFace(
        faceIndex: Int,
        minU: Float,
        minV: Float,
        maxU: Float,
        maxV: Float,
        depth: Float,
        axis: Int
    ) {
        val baseIndex = faceIndex * 2 * 3
        
        val v0 = getCollisionVertex(minU, minV, depth, axis)
        val v1 = getCollisionVertex(maxU, minV, depth, axis)
        val v2 = getCollisionVertex(maxU, maxV, depth, axis)
        val v3 = getCollisionVertex(minU, maxV, depth, axis)
        
        // First triangle
        vertices[baseIndex + 0] = v0
        vertices[baseIndex + 1] = v1
        vertices[baseIndex + 2] = v3
        
        // Second triangle
        vertices[baseIndex + 3] = v1
        vertices[baseIndex + 4] = v2
        vertices[baseIndex + 5] = v3
    }

    private fun getCollisionVertex(u: Float, v: Float, depth: Float, axis: Int): LLVector3 {
        return when (axis) {
            0 -> LLVector3(depth, u, v)  // X-axis face
            1 -> LLVector3(u, depth, v)  // Y-axis face
            2 -> LLVector3(u, v, depth)  // Z-axis face
            else -> LLVector3(0f, 0f, 0f)
        }
    }
}
