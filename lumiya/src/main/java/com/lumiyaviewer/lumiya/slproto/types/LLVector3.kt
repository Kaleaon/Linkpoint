package com.lumiyaviewer.lumiya.slproto.types

import com.google.common.primitives.UnsignedBytes
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDDouble
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap
import java.nio.ByteBuffer
import kotlin.math.sqrt
import kotlin.math.max
import kotlin.math.cos
import kotlin.math.sin

class LLVector3(
    var x: Float = 0.0f,
    var y: Float = 0.0f,
    var z: Float = 0.0f
) {

    companion object {
        const val FP_MAG_THRESHOLD: Float = 1.0E-7f
        val Zero = LLVector3(0.0f, 0.0f, 0.0f)
        val z_axis = LLVector3(0.0f, 0.0f, 1.0f)

        fun cross(v1: LLVector3, v2: LLVector3): LLVector3 {
            return LLVector3(
                (v1.y * v2.z) - (v2.y * v1.z),
                (v1.z * v2.x) - (v2.z * v1.x),
                (v1.x * v2.y) - (v2.x * v1.y)
            )
        }

        fun parseFloatVec(byteBuffer: ByteBuffer): LLVector3 {
            return LLVector3(byteBuffer.float, byteBuffer.float, byteBuffer.float)
        }
    }

    constructor(other: LLVector3) : this(other.x, other.y, other.z)

    // Compatibility wrapper for instance method cross if needed, 
    // but static/companion is better. Keeping instance for now if used elsewhere.
    fun cross(v1: LLVector3, v2: LLVector3): LLVector3 = Companion.cross(v1, v2)

    fun lerp(v1: LLVector3, v2: LLVector3, f: Float): LLVector3 {
        return LLVector3(
            v1.x + ((v2.x - v1.x) * f),
            v1.y + ((v2.y - v1.y) * f),
            v1.z + ((v2.z - v1.z) * f)
        )
    }

    fun parseU16Vec(byteBuffer: ByteBuffer, minX: Float, maxX: Float, minY: Float, maxY: Float): LLVector3 {
        // Assuming LLTersePacking is available
        return LLVector3(
            LLTersePacking.U16_to_float(byteBuffer.short.toInt() and 0xFFFF, minX, maxX),
            LLTersePacking.U16_to_float(byteBuffer.short.toInt() and 0xFFFF, minX, maxX),
            LLTersePacking.U16_to_float(byteBuffer.short.toInt() and 0xFFFF, minY, maxY)
        )
    }

    fun parseU8Vec(byteBuffer: ByteBuffer, minX: Float, maxX: Float, minY: Float, maxY: Float): LLVector3 {
        return LLVector3(
            LLTersePacking.U8_to_float(byteBuffer.get().toInt() and 0xFF, minX, maxX),
            LLTersePacking.U8_to_float(byteBuffer.get().toInt() and 0xFF, minX, maxX),
            LLTersePacking.U8_to_float(byteBuffer.get().toInt() and 0xFF, minY, maxY)
        )
    }

    fun scaleFromMatrix(fArr: FloatArray): LLVector3 {
        return LLVector3(
            sqrt((fArr[0] * fArr[0].toDouble()).toFloat() + (fArr[1] * fArr[1]) + (fArr[2] * fArr[2])),
            sqrt((fArr[4] * fArr[4].toDouble()).toFloat() + (fArr[5] * fArr[5]) + (fArr[6] * fArr[6])),
            sqrt((fArr[8] * fArr[8].toDouble()).toFloat() + (fArr[9] * fArr[9]) + (fArr[10] * fArr[10]))
        )
    }

    fun sub(v1: LLVector3, v2: LLVector3): LLVector3 {
        return LLVector3(v1.x - v2.x, v1.y - v2.y, v1.z - v2.z)
    }

    fun add(v: LLVector3) {
        this.x += v.x
        this.y += v.y
        this.z += v.z
    }

    fun addMul(immutableVector: ImmutableVector, f: Float) {
        this.x += immutableVector.x * f
        this.y += immutableVector.y * f
        this.z += immutableVector.z * f
    }

    fun addMul(v: LLVector3, f: Float) {
        this.x += v.x * f
        this.y += v.y * f
        this.z += v.z * f
    }

    fun dot(v: LLVector3): Float {
        return (this.x * v.x) + (this.y * v.y) + (this.z * v.z)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LLVector3) return false
        return this.x == other.x && this.y == other.y && this.z == other.z
    }

    fun getDistanceTo(v: LLVector3): Float {
        val dx = this.x - v.x
        val dy = this.y - v.y
        val dz = this.z - v.z
        return sqrt((dx * dx).toDouble() + (dy * dy) + (dz * dz)).toFloat()
    }

    fun getMax(): Float {
        return max(max(this.x, this.y), this.z)
    }

    fun getRotatedOffset(f: Float, f2: Float): LLVector3 {
        val f3 = (3.1415927f * f2) / 180.0f
        return LLVector3(
            (cos(f3.toDouble()).toFloat() * f) + this.x,
            (sin(f3.toDouble()).toFloat() * f) + this.y,
            this.z
        )
    }

    override fun hashCode(): Int {
        return java.lang.Float.floatToIntBits(this.x) + java.lang.Float.floatToIntBits(this.y) + java.lang.Float.floatToIntBits(this.z)
    }

    fun isZero(): Boolean {
        return this.x == 0.0f && this.y == 0.0f && this.z == 0.0f
    }

    fun magVec(): Float {
        return sqrt((this.x * this.x).toDouble() + (this.y * this.y) + (this.z * this.z)).toFloat()
    }

    fun magVecSquared(): Float {
        return (this.x * this.x) + (this.y * this.y) + (this.z * this.z)
    }

    fun mul(f: Float) {
        this.x *= f
        this.y *= f
        this.z *= f
    }

    fun mul(q: LLQuaternion) {
        val f = (((-q.x) * this.x) - (q.y * this.y)) - (q.z * this.z)
        val f2 = ((q.w * this.x) + (q.y * this.z)) - (q.z * this.y)
        val f3 = ((q.w * this.y) + (q.z * this.x)) - (q.x * this.z)
        val f4 = ((q.w * this.z) + (q.x * this.y)) - (q.y * this.x)
        this.x = ((((-f) * q.x) + (q.w * f2)) - (q.z * f3)) + (q.y * f4)
        this.y = ((((-f) * q.y) + (q.w * f3)) - (q.x * f4)) + (q.z * f2)
        this.z = ((((-f) * q.z) + (f4 * q.w)) - (f2 * q.y)) + (q.x * f3)
    }

    fun mul(v: LLVector3) {
        this.x *= v.x
        this.y *= v.y
        this.z *= v.z
    }

    fun mulWeighted(immutableVector: ImmutableVector, f: Float) {
        this.x *= (immutableVector.x * f) + 1.0f
        this.y *= (immutableVector.y * f) + 1.0f
        this.z *= (immutableVector.z * f) + 1.0f
    }

    fun mulWeighted(v: LLVector3, f: Float) {
        this.x *= (v.x * f) + 1.0f
        this.y *= (v.y * f) + 1.0f
        this.z *= (v.z * f) + 1.0f
    }

    fun normVec(): Float {
        val sqrt = sqrt((this.x * this.x).toDouble() + (this.y * this.y) + (this.z * this.z)).toFloat()
        if (sqrt > 1.0E-7f) {
            val f = 1.0f / sqrt
            this.x *= f
            this.y *= f
            this.z *= f
        } else {
            this.x = 0.0f
            this.y = 0.0f
            this.z = 0.0f
        }
        return sqrt
    }

    fun set(f: Float, f2: Float, f3: Float) {
        this.x = f
        this.y = f2
        this.z = f3
    }

    fun set(v: LLVector3?) {
        if (v != null) {
            this.x = v.x
            this.y = v.y
            this.z = v.z
        }
    }

    fun setAdd(v1: LLVector3, v2: LLVector3) {
        this.x = v1.x + v2.x
        this.y = v1.y + v2.y
        this.z = v1.z + v2.z
    }

    fun setCross(v: LLVector3) {
        val f = (this.y * v.z) - (v.y * this.z)
        val f2 = (this.z * v.x) - (v.z * this.x)
        this.z = (this.x * v.y) - (v.x * this.y)
        this.x = f
        this.y = f2
    }

    fun setLerp(v1: LLVector3, f: Float, v2: LLVector3, f2_val: Float) {
        this.x = (v1.x * f) + (v2.x * f2_val)
        this.y = (v1.y * f) + (v2.y * f2_val)
        this.z = (v1.z * f) + (v2.z * f2_val)
    }

    fun setLerp(v1: LLVector3, v2: LLVector3, f: Float) {
        this.x = v1.x + ((v2.x - v1.x) * f)
        this.y = v1.y + ((v2.y - v1.y) * f)
        this.z = v1.z + ((v2.z - v1.z) * f)
    }

    fun setMul(v: LLVector3, f: Float) {
        this.x = v.x * f
        this.y = v.y * f
        this.z = v.z * f
    }

    fun setMul(v1: LLVector3, v2: LLVector3) {
        this.x = v1.x * v2.x
        this.y = v1.y * v2.y
        this.z = v1.z * v2.z
    }

    fun setSub(v1: LLVector3, v2: LLVector3) {
        this.x = v1.x - v2.x
        this.y = v1.y - v2.y
        this.z = v1.z - v2.z
    }

    fun sub(v: LLVector3) {
        this.x -= v.x
        this.y -= v.y
        this.z -= v.z
    }

    fun toLLSD(): LLSDNode {
        val map = LLSDMap()
        map["X"] = LLSDDouble(this.x.toDouble())
        map["Y"] = LLSDDouble(this.y.toDouble())
        map["Z"] = LLSDDouble(this.z.toDouble())
        return map
    }

    override fun toString(): String {
        return String.format("(%f, %f, %f)", x, y, z)
    }
}
