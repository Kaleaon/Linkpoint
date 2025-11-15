package com.lumiyaviewer.lumiya.render.picking

import android.opengl.Matrix
import com.lumiyaviewer.lumiya.slproto.types.LLVector4

class IntersectInfo {
    Int faceID
    Boolean faceKnown
    LLVector4 intersectPoint
    Float s
    Float t
    Float u
    Float v

    constructor(intersectInfo: IntersectInfo, fArr: FloatArray, i: Int) {
        this.intersectPoint = intersectInfo.intersectPoint
        this.faceID = intersectInfo.faceID
        this.s = intersectInfo.s
        this.t = intersectInfo.t
        this.faceKnown = intersectInfo.faceKnown
        if (this.faceKnown) {
            FloatArray fArr2 = FloatArray(8)
            fArr2[0] = this.s
            fArr2[1] = this.t
            fArr2[3] = 1.0f
            Matrix.multiplyMV(fArr2, 4, fArr, i, fArr2, 0)
            this.u = fArr2[4]
            this.v = fArr2[5]
            return
        }
        this.u = intersectInfo.u
        this.v = intersectInfo.v
    }

    constructor(lLVector4: LLVector4) {
        this.intersectPoint = lLVector4
        this.faceID = 0
        this.u = 0.0f
        this.v = 0.0f
        this.s = 0.0f
        this.t = 0.0f
        this.faceKnown = false
    }

    constructor(lLVector4: LLVector4, i: Int, f: Float, f2: Float) {
        this.intersectPoint = lLVector4
        this.faceID = i
        this.u = f
        this.v = f2
        this.s = f
        this.t = f2
        this.faceKnown = true
    }
}
