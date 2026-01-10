package com.linkpoint.slproto.prims

import kotlin.math.*

import com.linkpoint.slproto.types.LLQuaternion
import com.linkpoint.slproto.types.LLVector2
import com.linkpoint.slproto.types.LLVector3
import java.util.ArrayList

class PrimPath {
    private val MIN_DETAIL_FACES: Int = 6
    private val tableScale: FloatArray = {1.0f, 1.0f, 1.0f, 0.5f, 0.707107f, 0.53f, 0.525f, 0.5f}
    var Dirty: Boolean = true
    var Open: Boolean = false
    ArrayList<PathPoint> Path = ArrayList<>()
    var Step: Float = 1.0f
    var Total: Int = 0

    class PathPoint {
        var TexT: Float = 0.0f
        LLVector3 pos = LLVector3()
        LLQuaternion rot = LLQuaternion()
        LLVector2 scale = LLVector2()
    }

    private Unit genNGon(PrimPathParams primPathParams, Int i, Float f, Float f2, Float f3) {
        Float f4
        Float f5
        Float f6
        Float f7
        Float f8
        var f9: Float = primPathParams.Revolutions
        var f10: Float = primPathParams.Skew
        var abs: Float = abs(f10)
        var f11: Float = primPathParams.ScaleX * (1.0f - abs)
        var f12: Float = primPathParams.ScaleY
        var f13: Float = 1.0f - primPathParams.TaperX
        var f14: Float = 1.0f - primPathParams.TaperY
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
        var f15: Float = 0.5f
        if (i < 8) {
            f15 = tableScale[i]
        }
        var f16: Float = f15 * (1.0f - f12)
        var f17: Float = primPathParams.RadiusOffset
        if (f17 < 0.0f) {
            f8 = (f17 + 1.0f) * f16
        } else {
            var f18: Float = (1.0f - f17) * f16
            f8 = f16
            f16 = f18
        }
        this.Open = ((primPathParams.End * f2) - primPathParams.Begin < 1.0f || abs > 0.001f || abs(f4 - f5) > 0.001f || abs(f6 - f7) > 0.001f) ? true : abs(f16 - f8) > 0.001f
        LLQuaternion lLQuaternion = LLQuaternion()
        LLQuaternion lLQuaternion2 = LLQuaternion()
        LLVector3 lLVector3 = LLVector3(1.0f, 0.0f, 0.0f)
        var f19: Float = primPathParams.TwistBegin * f3
        var f20: Float = primPathParams.TwistEnd * f3
        var f21: Float = 1.0f / (i.toFloat())
        var f22: Float = primPathParams.Begin
        PathPoint pathPoint = PathPoint()
        var f23: Float = 6.2831855f * f9 * f22
        var sin: Float = (Float) (sin(f23.toDouble()) * (PrimMath.toDouble().lerp(f8, f16, f22)))
        pathPoint.pos.set(PrimMath.lerp(0.0f, primPathParams.ShearX, sin) + 0.0f + (PrimMath.lerp(-f10, f10, f22) * 0.5f), ((Float) (cos(f23.toDouble()) * (PrimMath.toDouble().lerp(f8, f16, f22)))) + PrimMath.lerp(0.0f, primPathParams.ShearY, sin), sin)
        pathPoint.scale.x = PrimMath.lerp(f5, f4, f22) * f11
        pathPoint.scale.y = PrimMath.lerp(f7, f6, f22) * f12
        pathPoint.TexT = f22
        lLQuaternion.setQuat(((PrimMath.lerp(f19, f20, f22) * 2.0f) * 3.1415927f) - 3.1415927f, 0.0f, 0.0f, 1.0f)
        lLQuaternion2.setQuat(f23, lLVector3)
        pathPoint.rot.setMul(lLQuaternion, lLQuaternion2)
        this.Path.add(pathPoint)
        for (Float f24 = ((Float) ((Int) ((f22 + f21) * (i.toFloat())))) / (i.toFloat()); f24 < primPathParams.End; f24 += f21) {
            PathPoint pathPoint2 = PathPoint()
            var f25: Float = 6.2831855f * f9 * f24
            var cos: Float = (Float) (cos(f25.toDouble()) * (PrimMath.toDouble().lerp(f8, f16, f24)))
            var sin2: Float = (Float) (sin(f25.toDouble()) * (PrimMath.toDouble().lerp(f8, f16, f24)))
            pathPoint2.pos.set(PrimMath.lerp(0.0f, primPathParams.ShearX, sin2) + 0.0f + (PrimMath.lerp(-f10, f10, f24) * 0.5f), cos + PrimMath.lerp(0.0f, primPathParams.ShearY, sin2), sin2)
            pathPoint2.scale.x = PrimMath.lerp(f5, f4, f24) * f11
            pathPoint2.scale.y = PrimMath.lerp(f7, f6, f24) * f12
            pathPoint2.TexT = f24
            lLQuaternion.setQuat(((PrimMath.lerp(f19, f20, f24) * 2.0f) * 3.1415927f) - 3.1415927f, 0.0f, 0.0f, 1.0f)
            lLQuaternion2.setQuat(f25, lLVector3)
            pathPoint2.rot.setMul(lLQuaternion, lLQuaternion2)
            this.Path.add(pathPoint2)
        }
        var f26: Float = primPathParams.End
        PathPoint pathPoint3 = PathPoint()
        var f27: Float = f9 * 6.2831855f * f26
        var cos2: Float = (Float) (cos(f27.toDouble()) * (PrimMath.toDouble().lerp(f8, f16, f26)))
        var lerp: Float = (Float) ((PrimMath.toDouble().lerp(f8, f16, f26)) * sin(f27.toDouble()))
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

    fun generate(PrimPathParams primPathParams, Float f, Int i, Boolean z, Int i2): Boolean {
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
                var floor: Int = Math.toInt().floor(floor((Double) ((abs(primPathParams.TwistBegin - primPathParams.TwistEnd) * 3.5f * (f - 0.5f)) + (6.0f * f))) * (primPathParams.toDouble().Revolutions))
                if (z) {
                    floor = i2
                }
                genNGon(primPathParams, floor, 0.0f, 1.0f, 1.0f)
                break
            case 48:
                if (primPathParams.End - primPathParams.Begin >= 0.99f && primPathParams.ScaleX >= 0.99f) {
                    this.Open = false
                }
                genNGon(primPathParams, Math.toInt().floor((Double) (6.0f * f)), 0.0f, 1.0f, 1.0f)
                var size: Float = 1.0f / (this.toFloat().Path.size())
                var i3: Int = 0
                var f2: Float = 0.5f
                while (true) {
                    var i4: Int = i3
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
                for (i5 in 0 until 5) {
                    var f3: Float = (i5.toFloat()) * this.Step
                    PathPoint pathPoint = PathPoint()
                    pathPoint.pos.set(0.0f, PrimMath.lerp(0.0f, (Float) ((-sin((Double) (primPathParams.TwistEnd * 3.1415927f * f3))) * 0.5d), f3), PrimMath.lerp(-0.5f, (Float) (cos((Double) (primPathParams.TwistEnd * 3.1415927f * f3)) * 0.5d), f3))
                    pathPoint.scale.x = PrimMath.lerp(1.0f, primPathParams.ScaleX, f3)
                    pathPoint.scale.y = PrimMath.lerp(1.0f, primPathParams.ScaleY, f3)
                    pathPoint.TexT = f3
                    pathPoint.rot.setQuat(f3 * primPathParams.TwistEnd * 3.1415927f, 1.0f, 0.0f, 0.0f)
                    this.Path.add(pathPoint)
                }
                break
            default:
                var floor2: Int = (Math.toInt().floor((Double) (abs(primPathParams.TwistBegin - primPathParams.TwistEnd) * 3.5f * (f - 0.5f)))) + 2
                if (floor2 < i + 2) {
                    floor2 = i + 2
                }
                this.Step = 1.0f / ((Float) (floor2 - 1))
                this.Path.ensureCapacity(floor2)
                LLVector2 beginScale = primPathParams.getBeginScale()
                LLVector2 endScale = primPathParams.getEndScale()
                for (i6 in 0 until floor2) {
                    var lerp: Float = PrimMath.lerp(primPathParams.Begin, primPathParams.End, (i6.toFloat()) * this.Step)
                    PathPoint pathPoint2 = PathPoint()
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
