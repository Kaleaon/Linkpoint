package com.lumiyaviewer.lumiya.render.picking

import android.opengl.Matrix
import com.lumiyaviewer.lumiya.slproto.types.LLVector4

class IntersectInfo {
    val intersectPoint: LLVector4
    val faceID: Int
    val s: Float
    val t: Float
    val u: Float
    val v: Float
    val faceKnown: Boolean

    constructor(
        other: IntersectInfo,
        transformMatrix: FloatArray,
        matrixOffset: Int
    ) {
        intersectPoint = other.intersectPoint
        faceID = other.faceID
        s = other.s
        t = other.t
        faceKnown = other.faceKnown
        
        if (faceKnown) {
            val coords = FloatArray(8)
            coords[0] = s
            coords[1] = t
            coords[3] = 1.0f
            Matrix.multiplyMV(coords, 4, transformMatrix, matrixOffset, coords, 0)
            u = coords[4]
            v = coords[5]
        } else {
            u = other.u
            v = other.v
        }
    }

    constructor(intersectPoint: LLVector4) {
        this.intersectPoint = intersectPoint
        faceID = 0
        u = 0.0f
        v = 0.0f
        s = 0.0f
        t = 0.0f
        faceKnown = false
    }

    constructor(
        intersectPoint: LLVector4,
        faceID: Int,
        u: Float,
        v: Float
    ) {
        this.intersectPoint = intersectPoint
        this.faceID = faceID
        this.u = u
        this.v = v
        s = u
        t = v
        faceKnown = true
    }
}
