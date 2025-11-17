package com.linkpoint.slproto.prims

import com.linkpoint.slproto.types.LLVector3

class PrimMath {
    val F_DEG_TO_RAD: Float = 0.017453292f
    val F_PI: Float = 3.1415927f

    Float lerp(Float f, Float f2, Float f3) {
        return ((f2 - f) * f3) + f
    }

    FloatArray lookAt(LLVector3 lLVector3, LLVector3 lLVector32, LLVector3 lLVector33) {
        FloatArray fArr = FloatArray(16)
        LLVector3 sub = LLVector3.sub(lLVector32, lLVector3)
        sub.normVec()
        LLVector3 lLVector34 = LLVector3(sub)
        lLVector34.setCross(lLVector33)
        fArr[0] = lLVector34.x
        fArr[4] = lLVector34.y
        fArr[8] = lLVector34.z
        fArr[1] = lLVector33.x
        fArr[5] = lLVector33.y
        fArr[9] = lLVector33.z
        fArr[2] = -sub.x
        fArr[6] = -sub.y
        fArr[10] = -sub.z
        fArr[15] = 1.0f
        return fArr
    }
}
