package com.lumiyaviewer.lumiya.render.picking

import android.opengl.Matrix
import com.lumiyaviewer.lumiya.render.RenderContext
import com.lumiyaviewer.lumiya.slproto.types.LLVector3
import com.lumiyaviewer.lumiya.slproto.types.LLVector4
import kotlin.math.abs

object GLRayTrace {

    data class RayIntersectInfo(
        val intersectPoint: LLVector4,
        val s: Float,
        val t: Float
    ) {
        override fun toString(): String {
            return "RayIntersectInfo{intersectPoint=$intersectPoint, s=$s, t=$t}"
        }
    }

    fun getIntersectionDepth(renderContext: RenderContext, lLVector4: LLVector4, fArr: FloatArray): Float {
        val r4 = FloatArray(4)
        val fArr2 = FloatArray(8)
        r4[0] = lLVector4.x
        r4[1] = lLVector4.y
        r4[2] = lLVector4.z
        r4[3] = 1.0f
        Matrix.multiplyMV(fArr2, 0, fArr, 0, r4, 0)
        if (renderContext.hasGL20) {
            Matrix.multiplyMV(fArr2, 4, renderContext.modelViewMatrix.getMatrixData(), renderContext.modelViewMatrix.getMatrixDataOffset(), fArr2, 0)
        } else {
            Matrix.multiplyMV(fArr2, 4, renderContext.projectionMatrix, 0, fArr2, 0)
        }
        return fArr2[6]
    }

    fun intersect_RayTriangle(lLVector3: LLVector3, lLVector32: LLVector3, lLVector3Arr: Array<LLVector3>, i: Int): RayIntersectInfo? {
        val sub = LLVector3.sub(lLVector3Arr[i + 1], lLVector3Arr[i + 0])
        val sub2 = LLVector3.sub(lLVector3Arr[i + 2], lLVector3Arr[i + 0])
        val cross = LLVector3.cross(sub, sub2)
        if (cross.isZero()) {
            return null
        }
        val sub3 = LLVector3.sub(lLVector32, lLVector3)
        var f = -cross.dot(LLVector3.sub(lLVector3, lLVector3Arr[i + 0]))
        var dot = cross.dot(sub3)
        if (abs(dot) < 1.0E-7f) {
            return null
        }
        dot = f / dot
        if (dot.toDouble() < 0.0) {
            return null
        }
        val lLVector33 = LLVector3(sub3)
        lLVector33.mul(dot)
        lLVector33.add(lLVector3)
        val dot2 = sub.dot(sub)
        val dot3 = sub.dot(sub2)
        var dot4 = sub2.dot(sub2)
        val sub4 = LLVector3.sub(lLVector33, lLVector3Arr[i + 0])
        var dot5 = sub4.dot(sub)
        val dot6 = sub4.dot(sub2)
        val f2 = (dot3 * dot3) - (dot2 * dot4)
        if (abs(f2) < 1.0E-7f) {
            return null
        }
        dot4 = ((dot3 * dot6) - (dot4 * dot5)) / f2
        if (dot4.toDouble() < 0.0 || dot4.toDouble() > 1.0) {
            return null
        }
        dot5 = ((dot5 * dot3) - (dot6 * dot2)) / f2
        return if (dot5.toDouble() < 0.0 || (dot4 + dot5).toDouble() > 1.0) {
            null
        } else {
            RayIntersectInfo(LLVector4(lLVector33.x, lLVector33.y, lLVector33.z, dot), dot4, dot5)
        }
    }
}
