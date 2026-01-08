package com.lumiyaviewer.lumiya.slproto.types

import com.google.common.primitives.UnsignedBytes
import java.nio.ByteBuffer
import kotlin.math.sqrt
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.acos

class LLQuaternion(
    var x: Float = 0.0f,
    var y: Float = 0.0f,
    var z: Float = 0.0f,
    var w: Float = 1.0f
) {
    companion object {
        const val FP_MAG_THRESHOLD: Float = 1.0E-7f
    }

    private var inverseMatrix: FloatArray? = null
    private var matrix: FloatArray? = null

    enum class Order {
        XYZ, YZX, ZXY, XZY, YXZ, ZYX
    }

    constructor(other: LLQuaternion) : this(other.x, other.y, other.z, other.w)

    constructor(fArr: FloatArray) : this() {
        val f = fArr[0] + 1.0f + fArr[5] + fArr[10]
        if (f > 0.5f) {
            val sqrt = (sqrt(f.toDouble()) * 2.0).toFloat()
            this.x = (fArr[9] - fArr[6]) / sqrt
            this.y = (fArr[2] - fArr[8]) / sqrt
            this.z = (fArr[4] - fArr[1]) / sqrt
            this.w = sqrt * 0.25f
        } else if (fArr[0] > fArr[5] && fArr[0] > fArr[10]) {
            val sqrt2 = (sqrt(((fArr[0] + 1.0f) - fArr[5] - fArr[10]).toDouble()) * 2.0).toFloat()
            this.x = 0.25f * sqrt2
            this.y = (fArr[4] + fArr[1]) / sqrt2
            this.z = (fArr[2] + fArr[8]) / sqrt2
            this.w = (fArr[9] - fArr[6]) / sqrt2
        } else if (fArr[5] > fArr[10]) {
            val sqrt3 = (sqrt(((fArr[5] + 1.0f) - fArr[0] - fArr[10]).toDouble()) * 2.0).toFloat()
            this.x = (fArr[4] + fArr[1]) / sqrt3
            this.y = 0.25f * sqrt3
            this.z = (fArr[9] + fArr[6]) / sqrt3
            this.w = (fArr[2] - fArr[8]) / sqrt3
        } else {
            val sqrt4 = (sqrt(((fArr[10] + 1.0f) - fArr[0] - fArr[5]).toDouble()) * 2.0).toFloat()
            this.x = (fArr[2] + fArr[8]) / sqrt4
            this.y = (fArr[9] + fArr[6]) / sqrt4
            this.z = 0.25f * sqrt4
            this.w = (fArr[4] - fArr[1]) / sqrt4
        }
    }

    fun fromEuler(f: Float, f2: Float, f3: Float): LLQuaternion {
        val cos = cos((f / 2.0f).toDouble())
        val sin = sin((f / 2.0f).toDouble())
        val cos2 = cos((f2 / 2.0f).toDouble())
        val sin2 = sin((f2 / 2.0f).toDouble())
        val cos3 = cos((f3 / 2.0f).toDouble())
        val sin3 = sin((f3 / 2.0f).toDouble())
        val d = cos * cos2
        val d2 = sin * sin2
        return LLQuaternion(
            ((sin * cos2 * cos3) + (cos * sin2 * sin3)).toFloat(),
            (((cos * sin2) * cos3) - ((sin * cos2) * sin3)).toFloat(),
            ((d * sin3) + (d2 * cos3)).toFloat(),
            ((d * cos3) - (d2 * sin3)).toFloat()
        )
    }

    fun lerp(q1: LLQuaternion, q2: LLQuaternion, f: Float): LLQuaternion {
        return LLQuaternion(
            q1.x + ((q2.x - q1.x) * f),
            q1.y + ((q2.y - q1.y) * f),
            q1.z + ((q2.z - q1.z) * f),
            q1.w + ((q2.w - q1.w) * f)
        )
    }

    fun mayaQ(f: Float, f2: Float, f3: Float, order: Order): LLQuaternion {
        val q1 = LLQuaternion()
        val q2 = LLQuaternion()
        val q3 = LLQuaternion()
        q1.setQuat(f * 0.017453292f, LLVector3(1.0f, 0.0f, 0.0f))
        q2.setQuat(f2 * 0.017453292f, LLVector3(0.0f, 1.0f, 0.0f))
        q3.setQuat(f3 * 0.017453292f, LLVector3(0.0f, 0.0f, 1.0f))
        val q4 = LLQuaternion()
        val q5 = LLQuaternion()
        
        when (order) {
            Order.XYZ -> {
                q4.setMul(q1, q2)
                q5.setMul(q4, q3)
            }
            Order.YZX -> {
                q4.setMul(q2, q3)
                q5.setMul(q4, q1)
            }
            Order.ZXY -> {
                q4.setMul(q3, q1)
                q5.setMul(q4, q2)
            }
            Order.XZY -> {
                q4.setMul(q1, q3)
                q5.setMul(q4, q2)
            }
            Order.YXZ -> {
                q4.setMul(q2, q1)
                q5.setMul(q4, q3)
            }
            Order.ZYX -> {
                q4.setMul(q3, q2)
                q5.setMul(q4, q1)
            }
        }
        return q5
    }

    fun parseFloatVec3(byteBuffer: ByteBuffer): LLQuaternion {
        var f = 0.0f
        val f2 = byteBuffer.float
        val f3 = byteBuffer.float
        val f4 = byteBuffer.float
        val f5 = 1.0f - (((f2 * f2) + (f3 * f3)) + (f4 * f4))
        if (f5 > 0.0f) {
            f = sqrt(f5.toDouble()).toFloat()
        }
        return LLQuaternion(f2, f3, f4, f)
    }

    fun parseU16Vec3(byteBuffer: ByteBuffer, f: Float, f2: Float): LLQuaternion {
        return LLQuaternion(
            LLTersePacking.U16_to_float(byteBuffer.short.toInt() and 0xFFFF, f, f2),
            LLTersePacking.U16_to_float(byteBuffer.short.toInt() and 0xFFFF, f, f2),
            LLTersePacking.U16_to_float(byteBuffer.short.toInt() and 0xFFFF, f, f2),
            LLTersePacking.U16_to_float(byteBuffer.short.toInt() and 0xFFFF, f, f2)
        )
    }

    fun parseU8Vec3(byteBuffer: ByteBuffer, f: Float, f2: Float): LLQuaternion {
        return LLQuaternion(
            LLTersePacking.U8_to_float(byteBuffer.get().toInt() and 0xFF, f, f2),
            LLTersePacking.U8_to_float(byteBuffer.get().toInt() and 0xFF, f, f2),
            LLTersePacking.U8_to_float(byteBuffer.get().toInt() and 0xFF, f, f2),
            LLTersePacking.U8_to_float(byteBuffer.get().toInt() and 0xFF, f, f2)
        )
    }

    fun shortestArc(v1: LLVector3, v2: LLVector3): LLQuaternion {
        val v3 = LLVector3(v1)
        val v4 = LLVector3(v2)
        val normVec = v3.normVec()
        val normVec2 = v4.normVec()
        if (normVec < 1.0E-7f || normVec2 < 1.0E-7f) {
            return LLQuaternion()
        }
        val cross = LLVector3.cross(v3, v4)
        val dot = v3.dot(v4)
        if (dot > 0.9999999f) {
            return LLQuaternion()
        }
        if (dot < -0.9999999f) {
            val v5 = LLVector3(v3)
            v5.mul(v3.x / v3.dot(v3))
            val v6 = LLVector3(1.0f, 0.0f, 0.0f)
            v6.sub(v5)
            if (v6.normVec() < 1.0E-7f) {
                v6.set(0.0f, 0.0f, 1.0f)
            }
            return LLQuaternion(v6.x, v6.y, v6.z, 0.0f)
        }
        val q = LLQuaternion()
        q.setQuat(acos(dot.toDouble()).toFloat(), cross)
        return q
    }

    fun unpackFromVector3(v: LLVector3): LLQuaternion {
        var f = 0.0f
        val magVecSquared = 1.0f - v.magVecSquared()
        val f2 = v.x
        val f3 = v.y
        val f4 = v.z
        if (magVecSquared > 0.0f) {
            f = sqrt(magVecSquared.toDouble()).toFloat()
        }
        return LLQuaternion(f2, f3, f4, f)
    }

    fun addMul(q: LLQuaternion, f: Float) {
        this.x += q.x * f
        this.y += q.y * f
        this.z += q.z * f
        this.w += q.w * f
    }

    fun conjQuat(): LLQuaternion {
        return LLQuaternion(this.x * -1.0f, this.y * -1.0f, this.z * -1.0f, this.w)
    }

    fun getAngleAxis(v: LLVector3): Float {
        var f = -1.0f
        var f2 = this.w
        if (f2 > 1.0f) {
            f2 = 1.0f
        }
        if (f2 >= -1.0f) {
            f = f2
        }
        val sqrt = sqrt((1.0f - (f * f)).toDouble()).toFloat()
        val f3 = if (abs(sqrt) < 5.0E-4f) 1.0f else 1.0f / sqrt
        val acos = (acos(f.toDouble()) * 2.0).toFloat()
        if (acos > 3.1415927f) {
            v.x = (-this.x) * f3
            v.y = (-this.y) * f3
            v.z = f3 * (-this.z)
            return 6.2831855f - acos
        }
        v.x = this.x * f3
        v.y = this.y * f3
        v.z = f3 * this.z
        return acos
    }

    fun getInverseMatrix(fArr: FloatArray, i: Int) {
        val f = this.x * this.x
        val f2 = this.y * this.y
        val f3 = this.z * this.z
        val f4 = (-this.x) * (-this.y)
        val f5 = (-this.x) * (-this.z)
        val f6 = (-this.y) * (-this.z)
        val f7 = this.w * (-this.x)
        val f8 = this.w * (-this.y)
        val f9 = this.w * (-this.z)
        fArr[i + 0] = 1.0f - ((f2 + f3) * 2.0f)
        fArr[i + 1] = (f4 - f9) * 2.0f
        fArr[i + 2] = (f5 + f8) * 2.0f
        fArr[i + 3] = 0.0f
        fArr[i + 4] = (f4 + f9) * 2.0f
        fArr[i + 5] = 1.0f - ((f3 + f) * 2.0f)
        fArr[i + 6] = (f6 - f7) * 2.0f
        fArr[i + 7] = 0.0f
        fArr[i + 8] = (f5 - f8) * 2.0f
        fArr[i + 9] = (f6 + f7) * 2.0f
        fArr[i + 10] = 1.0f - ((f + f2) * 2.0f)
        fArr[i + 11] = 0.0f
        fArr[i + 12] = 0.0f
        fArr[i + 13] = 0.0f
        fArr[i + 14] = 0.0f
        fArr[i + 15] = 1.0f
    }

    fun getInverseMatrix(): FloatArray {
        if (this.inverseMatrix != null) {
            return this.inverseMatrix!!
        }
        val f = this.x * this.x
        val f2 = this.y * this.y
        val f3 = this.z * this.z
        val f4 = (-this.x) * (-this.y)
        val f5 = (-this.x) * (-this.z)
        val f6 = (-this.y) * (-this.z)
        val f7 = this.w * (-this.x)
        val f8 = this.w * (-this.y)
        val f9 = this.w * (-this.z)
        val mat = floatArrayOf(
            1.0f - ((f2 + f3) * 2.0f), (f4 - f9) * 2.0f, (f5 + f8) * 2.0f, 0.0f,
            (f4 + f9) * 2.0f, 1.0f - ((f3 + f) * 2.0f), (f6 - f7) * 2.0f, 0.0f,
            (f5 - f8) * 2.0f, (f6 + f7) * 2.0f, 1.0f - ((f + f2) * 2.0f), 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f
        )
        this.inverseMatrix = mat
        return mat
    }

    fun getMatrix(): FloatArray {
        if (this.matrix != null) {
            return this.matrix!!
        }
        val f = this.x * this.x
        val f2 = this.y * this.y
        val f3 = this.z * this.z
        val f4 = this.x * this.y
        val f5 = this.x * this.z
        val f6 = this.y * this.z
        val f7 = this.w * this.x
        val f8 = this.w * this.y
        val f9 = this.w * this.z
        val mat = floatArrayOf(
            1.0f - ((f2 + f3) * 2.0f), (f4 - f9) * 2.0f, (f5 + f8) * 2.0f, 0.0f,
            (f4 + f9) * 2.0f, 1.0f - ((f3 + f) * 2.0f), (f6 - f7) * 2.0f, 0.0f,
            (f5 - f8) * 2.0f, (f6 + f7) * 2.0f, 1.0f - ((f + f2) * 2.0f), 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f
        )
        this.matrix = mat
        return mat
    }

    fun normalize(): Float {
        val sqrt = sqrt((this.x * this.x).toDouble() + (this.y * this.y) + (this.z * this.z) + (this.w * this.w)).toFloat()
        if (sqrt <= 1.0E-7f) {
            this.x = 0.0f
            this.y = 0.0f
            this.z = 0.0f
            this.w = 1.0f
        } else if (abs(1.0f - sqrt) > 1.0E-6f) {
            val f = 1.0f / sqrt
            this.x *= f
            this.y *= f
            this.z *= f
            this.w *= f
        }
        this.matrix = null
        this.inverseMatrix = null
        return sqrt
    }

    fun set(q: LLQuaternion) {
        this.x = q.x
        this.y = q.y
        this.z = q.z
        this.w = q.w
        this.matrix = null
        this.inverseMatrix = null
    }

    fun setIdentity() {
        this.x = 0.0f
        this.y = 0.0f
        this.z = 0.0f
        this.w = 1.0f
    }

    fun setLerp(q1: LLQuaternion, f: Float, q2: LLQuaternion, f2: Float) {
        this.x = (q1.x * f) + (q2.x * f2)
        this.y = (q1.y * f) + (q2.y * f2)
        this.z = (q1.z * f) + (q2.z * f2)
        this.w = (q1.w * f) + (q2.w * f2)
        this.matrix = null
        this.inverseMatrix = null
    }

    fun setMul(q1: LLQuaternion, q2: LLQuaternion) {
        this.x = (((q2.w * q1.x) + (q2.x * q1.w)) + (q2.y * q1.z)) - (q2.z * q1.y)
        this.y = (((q2.w * q1.y) + (q2.y * q1.w)) + (q2.z * q1.x)) - (q2.x * q1.z)
        this.z = (((q2.w * q1.z) + (q2.z * q1.w)) + (q2.x * q1.y)) - (q2.y * q1.x)
        this.w = (((q2.w * q1.w) - (q2.x * q1.x)) - (q2.y * q1.y)) - (q2.z * q1.z)
        this.matrix = null
        this.inverseMatrix = null
    }

    fun setQuat(f: Float, f2: Float, f3: Float, f4: Float) {
        val v = LLVector3(f2, f3, f4)
        v.normVec()
        val f5 = 0.5f * f
        val sin = sin(f5.toDouble()).toFloat()
        this.x = v.x * sin
        this.y = v.y * sin
        this.z = v.z * sin
        this.w = cos(f5.toDouble()).toFloat()
        normalize()
        this.matrix = null
        this.inverseMatrix = null
    }

    fun setQuat(f: Float, v: LLVector3) {
        val v2 = LLVector3(v)
        v2.normVec()
        val f2 = 0.5f * f
        val sin = sin(f2.toDouble()).toFloat()
        this.x = v2.x * sin
        this.y = v2.y * sin
        this.z = v2.z * sin
        this.w = cos(f2.toDouble()).toFloat()
        normalize()
        this.matrix = null
        this.inverseMatrix = null
    }

    fun setRaw(f: Float, f2: Float, f3: Float, f4: Float) {
        this.x = f
        this.y = f2
        this.z = f3
        this.w = f4
    }

    fun setZero() {
        this.x = 0.0f
        this.y = 0.0f
        this.z = 0.0f
        this.w = 0.0f
    }

    override fun toString(): String {
        return String.format("(%.2f, %.2f, %.2f, %.2f)", x, y, z, w)
    }
}
