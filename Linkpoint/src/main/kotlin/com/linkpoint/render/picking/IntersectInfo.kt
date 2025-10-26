package com.linkpoint.render.picking

import android.opengl.Matrix
import com.linkpoint.slproto.types.LLVector4

class IntersectInfo {
    val Int faceID
    val Boolean faceKnown
    val LLVector4 intersectPoint
    val Float s
    val Float t
    val Float u
    val Float v

    public IntersectInfo(IntersectInfo intersectInfo, FloatArray fArr, Int i) {
        this.intersectPoint = intersectInfo.intersectPoint
        this.faceID = intersectInfo.faceID
        this.s = intersectInfo.s
        this.t = intersectInfo.t
        this.faceKnown = intersectInfo.faceKnown
        if (this.faceKnown) {
            val fArr2: FloatArray = Float[8]
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

    public IntersectInfo(LLVector4 lLVector4) {
        this.intersectPoint = lLVector4
        this.faceID = 0
        this.u = 0.0f
        this.v = 0.0f
        this.s = 0.0f
        this.t = 0.0f
        this.faceKnown = false
    }

    public IntersectInfo(LLVector4 lLVector4, Int i, Float f, Float f2) {
        this.intersectPoint = lLVector4
        this.faceID = i
        this.u = f
        this.v = f2
        this.s = f
        this.t = f2
        this.faceKnown = true
    }
}
