package com.linkpoint.render.picking

import com.linkpoint.slproto.types.LLVector3

object CollisionBox {
    private val vertices: Array<LLVector3> = Array(36) { LLVector3() }

    init {
        addCollisionFace(0, -0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0)
        addCollisionFace(1, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, 0)
        addCollisionFace(2, -0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 1)
        addCollisionFace(3, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, 1)
        addCollisionFace(4, -0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 2)
        addCollisionFace(5, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, 2)
    }

    private fun addCollisionFace(i: Int, f: Float, f2: Float, f3: Float, f4: Float, f5: Float, i2: Int) {
        val i3 = (i * 2) * 3
        val lLVector3Arr = arrayOf(
            getCollisionVertex(f, f2, f5, i2),
            getCollisionVertex(f3, f2, f5, i2),
            getCollisionVertex(f3, f4, f5, i2),
            getCollisionVertex(f, f4, f5, i2)
        )
        vertices[i3 + 0] = lLVector3Arr[0]
        vertices[i3 + 1] = lLVector3Arr[1]
        vertices[i3 + 2] = lLVector3Arr[3]
        vertices[i3 + 3] = lLVector3Arr[1]
        vertices[i3 + 4] = lLVector3Arr[2]
        vertices[i3 + 5] = lLVector3Arr[3]
    }

    private fun getCollisionVertex(f: Float, f2: Float, f3: Float, i: Int): LLVector3 {
        return when (i) {
            0 -> LLVector3(f3, f, f2)
            1 -> LLVector3(f, f3, f2)
            2 -> LLVector3(f, f2, f3)
            else -> LLVector3()
        }
    }

    fun getInstance(): CollisionBox {
        return this
    }
    
    fun getVertices(): Array<LLVector3> {
        return vertices
    }
}
