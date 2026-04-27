package com.linkpoint.render.picking

import com.linkpoint.slproto.types.LLVector3

class CollisionBox {
    val Array<LLVector3> vertices

    @JvmStatic
private class InstanceHolder {
        private const val CollisionBox Instance = CollisionBox()

        private InstanceHolder() {
        }
    }

    private CollisionBox() {
        this.vertices = LLVector3[36]
        addCollisionFace(0, -0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0)
        addCollisionFace(1, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, 0)
        addCollisionFace(2, -0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 1)
        addCollisionFace(3, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, 1)
        addCollisionFace(4, -0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 2)
        addCollisionFace(5, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, 2)
    }

    /* synthetic */ CollisionBox(CollisionBox collisionBox) {
        this()
    }

     private fun addCollisionFace(i: Int, f: Float, f2: Float, f3: Float, f4: Float, f5: Float, i2: Int) {
        val i3: Int = (i * 2) * 3
        val lLVector3Arr: Array<LLVector3> = Array<LLVector3>{getCollisionVertex(f, f2, f5, i2), getCollisionVertex(f3, f2, f5, i2), getCollisionVertex(f3, f4, f5, i2), getCollisionVertex(f, f4, f5, i2)}
        this.vertices[i3 + 0] = lLVector3Arr[0]
        this.vertices[i3 + 1] = lLVector3Arr[1]
        this.vertices[i3 + 2] = lLVector3Arr[3]
        this.vertices[i3 + 3] = lLVector3Arr[1]
        this.vertices[i3 + 4] = lLVector3Arr[2]
        this.vertices[i3 + 5] = lLVector3Arr[3]
    }

     private fun getCollisionVertex(f: Float, f2: Float, f3: Float, i: Int): LLVector3 {
        switch (i) {
            case 0:
                return LLVector3(f3, f, f2)
            case 1:
                return LLVector3(f, f3, f2)
            case 2:
                return LLVector3(f, f2, f3)
            default:
                return null
        }
    }

    @JvmStatic
     fun getInstance(): CollisionBox {
        return InstanceHolder.Instance
    }
}
