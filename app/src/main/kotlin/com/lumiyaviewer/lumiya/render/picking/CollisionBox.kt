package com.lumiyaviewer.lumiya.render.picking

import com.lumiyaviewer.lumiya.slproto.types.LLVector3

class CollisionBox private constructor() {
    val vertices: Array<LLVector3> = Array(36) { LLVector3(0f, 0f, 0f) }

    init {
        addCollisionFace(0, -0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0)
        addCollisionFace(1, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, 0)
        addCollisionFace(2, -0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 1)
        addCollisionFace(3, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, 1)
        addCollisionFace(4, -0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 2)
        addCollisionFace(5, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, 2)
    }

    private fun addCollisionFace(
        faceIndex: Int,
        minX: Float,
        minY: Float,
        maxX: Float,
        maxY: Float,
        fixedCoord: Float,
        axis: Int
    ) {
        val baseIndex = (faceIndex * 2) * 3
        val faceVertices = arrayOf(
            getCollisionVertex(minX, minY, fixedCoord, axis),
            getCollisionVertex(maxX, minY, fixedCoord, axis),
            getCollisionVertex(maxX, maxY, fixedCoord, axis),
            getCollisionVertex(minX, maxY, fixedCoord, axis)
        )
        
        vertices[baseIndex + 0] = faceVertices[0]
        vertices[baseIndex + 1] = faceVertices[1]
        vertices[baseIndex + 2] = faceVertices[3]
        vertices[baseIndex + 3] = faceVertices[1]
        vertices[baseIndex + 4] = faceVertices[2]
        vertices[baseIndex + 5] = faceVertices[3]
    }

    private fun getCollisionVertex(a: Float, b: Float, c: Float, axis: Int): LLVector3 {
        return when (axis) {
            0 -> LLVector3(c, a, b)
            1 -> LLVector3(a, c, b)
            2 -> LLVector3(a, b, c)
            else -> LLVector3(0f, 0f, 0f)
        }
    }

    companion object {
        @JvmStatic
        fun getInstance(): CollisionBox = InstanceHolder.Instance
        
        private object InstanceHolder {
            val Instance = CollisionBox()
        }
    }
}
