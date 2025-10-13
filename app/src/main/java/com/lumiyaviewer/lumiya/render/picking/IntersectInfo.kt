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

    constructor(point: LLVector4) {
        intersectPoint = point
        faceID = 0
        u = 0f
        v = 0f
        s = 0f
        t = 0f
        faceKnown = false
    }

    constructor(point: LLVector4, faceID: Int, u: Float, v: Float) {
        intersectPoint = point
        this.faceID = faceID
        this.u = u
        this.v = v
        s = u
        t = v
        faceKnown = true
    }

    constructor(source: IntersectInfo, matrix: FloatArray, offset: Int) {
        intersectPoint = source.intersectPoint
        faceID = source.faceID
        s = source.s
        t = source.t
        faceKnown = source.faceKnown

        if (faceKnown) {
            val tempArray = FloatArray(8)
            tempArray[0] = s
            tempArray[1] = t
            tempArray[3] = 1.0f
            Matrix.multiplyMV(tempArray, 4, matrix, offset, tempArray, 0)
            u = tempArray[4]
            v = tempArray[5]
        } else {
            u = source.u
            v = source.v
        }
    }
}
