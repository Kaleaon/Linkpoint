package com.linkpoint.slproto.prims

import com.linkpoint.slproto.types.LLQuaternion
import com.linkpoint.slproto.types.LLVector2
import com.linkpoint.slproto.types.LLVector3
import java.util.ArrayList

class PrimPath {
    private const val MIN_DETAIL_FACES: Int = 6
    @JvmStatic
private FloatArray tableScale = {1.0f, 1.0f, 1.0f, 0.5f, 0.707107f, 0.53f, 0.525f, 0.5f}
    val Dirty: Boolean = true
    val Open: Boolean = false
    val Path: ArrayList<PathPoint> = ArrayList<>()
    val Step: Float = 1.0f
    val Total: Int = 0

    @JvmStatic
    class PathPoint {
        val TexT: Float = 0.0f
        val pos: LLVector3 = LLVector3()
        val rot: LLQuaternion = LLQuaternion()
        val scale: LLVector2 = LLVector2()
    }

     private fun genNGon(primPathParams: PrimPathParams, i: Int, f: Float, f2: Float, f3: Float) {
        Float f4
        Float f5
        Float f6
        Float f7
        Float f8
        val f9: Float = primPathParams.Revolutions
        val f10: Float = primPathParams.Skew
        val abs: Float = Math.abs(f10)
        val f11: Float = primPathParams.ScaleX * (1.0f - abs)
        val f12: Float = primPathParams.ScaleY
        val f13: Float = 1.0f - primPathParams.TaperX
        val f14: Float = 1.0f - primPathParams.TaperY
        if (f13 > 1.0f) {
            f4 = 1.0f
            f5 = 2.0f - f13
        } else {
            f4 = f13
            f5 = 1.0f
        }
        if (f14 > 1.0f) {
            f6 = 1.0f
            f7 = 2.0f - f14
        } else {
            f6 = f14
            f7 = 1.0f
        }
        val f15: Float = 0.5f
        if (i < 8) {
            f15 = tableScale[i]
        }
        val f16: Float = f15 * (1.0f - f12)
        val f17: Float = primPathParams.RadiusOffset
        if (f17 < 0.0f) {
            f8 = (f17 + 1.0f) * f16
        } else {
            val f18: Float = (1.0f - f17) * f16
            f8 = f16
            f16 = f18
        }
        this.Open = ((primPathParams.End * f2) - primPathParams.Begin < 1.0f || abs > 0.001f || Math.abs(f4 - f5) > 0.001f || Math.abs(f6 - f7) > 0.001f) ? true : Math.abs(f16 - f8) > 0.001f
        val lLQuaternion: LLQuaternion = LLQuaternion()
        val lLQuaternion2: LLQuaternion = LLQuaternion()
        val lLVector3: LLVector3 = LLVector3(1.0f, 0.0f, 0.0f)
        val f19: Float = primPathParams.TwistBegin * f3
        val f20: Float = primPathParams.TwistEnd * f3
        val f21: Float = 1.0f / ((Float) i)
        val f22: Float = primPathParams.Begin
        val pathPoint: PathPoint = PathPoint()
        val f23: Float = 6.2831855f * f9 * f22
        val sin: Float = (Float) (Math.sin((Double) f23) * ((Double) PrimMath.lerp(f8, f16, f22)))
        pathPoint.pos.set(PrimMath.lerp(0.0f, primPathParams.ShearX, sin) + 0.0f + (PrimMath.lerp(-f10, f10, f22) * 0.5f), ((Float) (Math.cos((Double) f23) * ((Double) PrimMath.lerp(f8, f16, f22)))) + PrimMath.lerp(0.0f, primPathParams.ShearY, sin), sin)
        pathPoint.scale.x = PrimMath.lerp(f5, f4, f22) * f11
        pathPoint.scale.y = PrimMath.lerp(f7, f6, f22) * f12
        pathPoint.TexT = f22
        lLQuaternion.setQuat(((PrimMath.lerp(f19, f20, f22) * 2.0f) * 3.1415927f) - 3.1415927f, 0.0f, 0.0f, 1.0f)
        lLQuaternion2.setQuat(f23, lLVector3)
        pathPoint.rot.setMul(lLQuaternion, lLQuaternion2)
        this.Path.add(pathPoint)
        for (Float f24 = ((Float) ((Int) ((f22 + f21) * ((Float) i)))) / ((Float) i); f24 < primPathParams.End; f24 += f21) {
            val pathPoint2: PathPoint = PathPoint()
            val f25: Float = 6.2831855f * f9 * f24
            val cos: Float = (Float) (Math.cos((Double) f25) * ((Double) PrimMath.lerp(f8, f16, f24)))
            val sin2: Float = (Float) (Math.sin((Double) f25) * ((Double) PrimMath.lerp(f8, f16, f24)))
            pathPoint2.pos.set(PrimMath.lerp(0.0f, primPathParams.ShearX, sin2) + 0.0f + (PrimMath.lerp(-f10, f10, f24) * 0.5f), cos + PrimMath.lerp(0.0f, primPathParams.ShearY, sin2), sin2)
            pathPoint2.scale.x = PrimMath.lerp(f5, f4, f24) * f11
            pathPoint2.scale.y = PrimMath.lerp(f7, f6, f24) * f12
            pathPoint2.TexT = f24
            lLQuaternion.setQuat(((PrimMath.lerp(f19, f20, f24) * 2.0f) * 3.1415927f) - 3.1415927f, 0.0f, 0.0f, 1.0f)
            lLQuaternion2.setQuat(f25, lLVector3)
            pathPoint2.rot.setMul(lLQuaternion, lLQuaternion2)
            this.Path.add(pathPoint2)
        }
        val f26: Float = primPathParams.End
        val pathPoint3: PathPoint = PathPoint()
        val f27: Float = f9 * 6.2831855f * f26
        val cos2: Float = (Float) (Math.cos((Double) f27) * ((Double) PrimMath.lerp(f8, f16, f26)))
        val lerp: Float = (Float) (((Double) PrimMath.lerp(f8, f16, f26)) * Math.sin((Double) f27))
        pathPoint3.pos.set((PrimMath.lerp(-f10, f10, f26) * 0.5f) + PrimMath.lerp(0.0f, primPathParams.ShearX, lerp) + 0.0f, cos2 + PrimMath.lerp(0.0f, primPathParams.ShearY, lerp), lerp)
        pathPoint3.scale.x = PrimMath.lerp(f5, f4, f26) * f11
        pathPoint3.scale.y = PrimMath.lerp(f7, f6, f26) * f12
        pathPoint3.TexT = f26
        lLQuaternion.setQuat(((PrimMath.lerp(f19, f20, f26) * 2.0f) * 3.1415927f) - 3.1415927f, 0.0f, 0.0f, 1.0f)
        lLQuaternion2.setQuat(f27, lLVector3)
        pathPoint3.rot.setMul(lLQuaternion, lLQuaternion2)
        this.Path.add(pathPoint3)
        this.Total = this.Path.size()
    }

     public fun generate(primPathParams: PrimPathParams, f: Float, i: Int, z: Boolean, i2: Int): Boolean {
        if (!this.Dirty && (!z)) {
            return false
        }
        if (f < 0.0f) {
            f = 0.0f
        }
        this.Dirty = false
        this.Path.clear()
        this.Open = true
        switch (primPathParams.CurveType & PrimProfileParams.LL_PCODE_HOLE_MASK) {
            case 32:
                val floor: Int = (Int) Math.floor(Math.floor((Double) ((Math.abs(primPathParams.TwistBegin - primPathParams.TwistEnd) * 3.5f * (f - 0.5f)) + (6.0f * f))) * ((Double) primPathParams.Revolutions))
                if (z) {
                    floor = i2
                }
                genNGon(primPathParams, floor, 0.0f, 1.0f, 1.0f)
                break
            case 48:
                if (primPathParams.End - primPathParams.Begin >= 0.99f && primPathParams.ScaleX >= 0.99f) {
                    this.Open = false
                }
                genNGon(primPathParams, (Int) Math.floor((Double) (6.0f * f)), 0.0f, 1.0f, 1.0f)
                val size: Float = 1.0f / ((Float) this.Path.size())
                val i3: Int = 0
                val f2: Float = 0.5f
                while (true) {
                    val i4: Int = i3
                    if (i4 >= this.Path.size()) {
                        break
                    } else {
                        this.Path.get(i4).pos.x = f2
                        f2 = f2 == 0.5f ? -0.5f : 0.5f
                        i3 = i4 + 1
                    }
                }
            case 64:
                this.Step = 1.0f / ((Float) 4)
                this.Path.ensureCapacity(5)
                for (Int i5 = 0; i5 < 5; i5++) {
                    val f3: Float = ((Float) i5) * this.Step
                    val pathPoint: PathPoint = PathPoint()
                    pathPoint.pos.set(0.0f, PrimMath.lerp(0.0f, (Float) ((-Math.sin((Double) (primPathParams.TwistEnd * 3.1415927f * f3))) * 0.5d), f3), PrimMath.lerp(-0.5f, (Float) (Math.cos((Double) (primPathParams.TwistEnd * 3.1415927f * f3)) * 0.5d), f3))
                    pathPoint.scale.x = PrimMath.lerp(1.0f, primPathParams.ScaleX, f3)
                    pathPoint.scale.y = PrimMath.lerp(1.0f, primPathParams.ScaleY, f3)
                    pathPoint.TexT = f3
                    pathPoint.rot.setQuat(f3 * primPathParams.TwistEnd * 3.1415927f, 1.0f, 0.0f, 0.0f)
                    this.Path.add(pathPoint)
                }
                break
            default:
                val floor2: Int = ((Int) Math.floor((Double) (Math.abs(primPathParams.TwistBegin - primPathParams.TwistEnd) * 3.5f * (f - 0.5f)))) + 2
                if (floor2 < i + 2) {
                    floor2 = i + 2
                }
                this.Step = 1.0f / ((Float) (floor2 - 1))
                this.Path.ensureCapacity(floor2)
                val beginScale: LLVector2 = primPathParams.getBeginScale()
                val endScale: LLVector2 = primPathParams.getEndScale()
                for (Int i6 = 0; i6 < floor2; i6++) {
                    val lerp: Float = PrimMath.lerp(primPathParams.Begin, primPathParams.End, ((Float) i6) * this.Step)
                    val pathPoint2: PathPoint = PathPoint()
                    pathPoint2.pos.set(PrimMath.lerp(0.0f, primPathParams.ShearX, lerp), PrimMath.lerp(0.0f, primPathParams.ShearY, lerp), lerp - 0.5f)
                    pathPoint2.rot.setQuat(PrimMath.lerp(primPathParams.TwistBegin * 3.1415927f, primPathParams.TwistEnd * 3.1415927f, lerp), 0.0f, 0.0f, 1.0f)
                    pathPoint2.scale.x = PrimMath.lerp(beginScale.x, endScale.x, lerp)
                    pathPoint2.scale.y = PrimMath.lerp(beginScale.y, endScale.y, lerp)
                    pathPoint2.TexT = lerp
                    this.Path.add(pathPoint2)
                }
                break
        }
        if (primPathParams.TwistEnd == primPathParams.TwistBegin) {
            return true
        }
        this.Open = true
        return true
    }
}
