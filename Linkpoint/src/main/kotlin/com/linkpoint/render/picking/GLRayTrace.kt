package com.linkpoint.render.picking

import android.opengl.Matrix
import com.linkpoint.render.RenderContext
import com.linkpoint.slproto.types.LLVector3
import com.linkpoint.slproto.types.LLVector4

class GLRayTrace {

    @JvmStatic
    class RayIntersectInfo {
        val LLVector4 intersectPoint
        val Float s
        val Float t

        RayIntersectInfo(LLVector4 lLVector4, Float f, Float f2) {
            this.intersectPoint = lLVector4
            this.s = f
            this.t = f2
        }

         public override fun toString(): String {
            return "RayIntersectInfo{intersectPoint=" + this.intersectPoint + ", s=" + this.s + ", t=" + this.t + '}'
        }
    }

    @JvmStatic
     fun getIntersectionDepth(renderContext: RenderContext, lLVector4: LLVector4, fArr: FloatArray): Float {
        r4 = Float[4]
        val fArr2: FloatArray = Float[8]
        r4[0] = lLVector4.x
        r4[1] = lLVector4.y
        r4[2] = lLVector4.z
        r4[3] = 1.0f
        Matrix.multiplyMV(fArr2, 0, fArr, 0, r4, 0)
        if (renderContext.hasGL20) {
            Matrix.multiplyMV(fArr2, 4, renderContext.modelViewMatrix.getMatrixData(), renderContext.modelViewMatrix.getMatrixDataOffset(), fArr2, 0)
        } else {
            Matrix.multiplyMV(fArr2, 4, renderContext.projectionMatrix.getMatrixData(), renderContext.projectionMatrix.getMatrixDataOffset(), fArr2, 0)
        }
        return fArr2[6]
    }

    @JvmStatic
     fun intersect_RayTriangle(lLVector3: LLVector3, lLVector32: LLVector3, lLVector3Arr: Array<LLVector3>, i: Int): RayIntersectInfo {
        val sub: LLVector3 = LLVector3.sub(lLVector3Arr[i + 1], lLVector3Arr[i + 0])
        val sub2: LLVector3 = LLVector3.sub(lLVector3Arr[i + 2], lLVector3Arr[i + 0])
        val cross: LLVector3 = LLVector3.cross(sub, sub2)
        if (cross.isZero()) {
            return null
        }
        val sub3: LLVector3 = LLVector3.sub(lLVector32, lLVector3)
        val f: Float = -cross.dot(LLVector3.sub(lLVector3, lLVector3Arr[i + 0]))
        val dot: Float = cross.dot(sub3)
        if (Math.abs(dot) < 1.0E-7f) {
            return null
        }
        dot = f / dot
        if (((Double) dot) < 0.0d) {
            return null
        }
        val lLVector33: LLVector3 = LLVector3(sub3)
        lLVector33.mul(dot)
        lLVector33.add(lLVector3)
        val dot2: Float = sub.dot(sub)
        val dot3: Float = sub.dot(sub2)
        val dot4: Float = sub2.dot(sub2)
        val sub4: LLVector3 = LLVector3.sub(lLVector33, lLVector3Arr[i + 0])
        val dot5: Float = sub4.dot(sub)
        val dot6: Float = sub4.dot(sub2)
        val f2: Float = (dot3 * dot3) - (dot2 * dot4)
        if (Math.abs(f2) < 1.0E-7f) {
            return null
        }
        dot4 = ((dot3 * dot6) - (dot4 * dot5)) / f2
        if (((Double) dot4) < 0.0d || ((Double) dot4) > 1.0d) {
            return null
        }
        dot5 = ((dot5 * dot3) - (dot6 * dot2)) / f2
        return (((Double) dot5) < 0.0d || ((Double) (dot4 + dot5)) > 1.0d) ? null : RayIntersectInfo(LLVector4(lLVector33.x, lLVector33.y, lLVector33.z, dot), dot4, dot5)
    }
}
