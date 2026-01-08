package com.linkpoint.slproto.types
import java.util.*

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.llsd.LLSDNode
import com.linkpoint.slproto.llsd.types.LLSDDouble
import com.linkpoint.slproto.llsd.types.LLSDMap
import java.nio.ByteBuffer

class LLVector3 {
    val FP_MAG_THRESHOLD: Float = 1.0E-7f
    LLVector3 Zero = LLVector3(0.0f, 0.0f, 0.0f)
    LLVector3 z_axis = LLVector3(0.0f, 0.0f, 1.0f)
    Float x = 0.0f
    Float y = 0.0f
    Float z = 0.0f

    LLVector3() {
    }

    LLVector3(Float f, Float f2, Float f3) {
        this.x = f
        this.y = f2
        this.z = f3
    }

    LLVector3(LLVector3 lLVector3) {
        this.x = lLVector3.x
        this.y = lLVector3.y
        this.z = lLVector3.z
    }

    fun cross(LLVector3 lLVector3, LLVector3 lLVector32): LLVector3 {
        return LLVector3((lLVector3.y * lLVector32.z) - (lLVector32.y * lLVector3.z), (lLVector3.z * lLVector32.x) - (lLVector32.z * lLVector3.x), (lLVector3.x * lLVector32.y) - (lLVector32.x * lLVector3.y))
    }

    fun lerp(LLVector3 lLVector3, LLVector3 lLVector32, Float f): LLVector3 {
        return LLVector3(lLVector3.x + ((lLVector32.x - lLVector3.x) * f), lLVector3.y + ((lLVector32.y - lLVector3.y) * f), lLVector3.z + ((lLVector32.z - lLVector3.z) * f))
    }

    fun parseFloatVec(ByteBuffer byteBuffer): LLVector3 {
        return LLVector3(byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat())
    }

    fun parseU16Vec(ByteBuffer byteBuffer, Float f, Float f2, Float f3, Float f4): LLVector3 {
        return LLVector3(LLTersePacking.U16_to_float(byteBuffer.getShort() & 65535, f, f2), LLTersePacking.U16_to_float(byteBuffer.getShort() & 65535, f, f2), LLTersePacking.U16_to_float(byteBuffer.getShort() & 65535, f3, f4))
    }

    fun parseU8Vec(ByteBuffer byteBuffer, Float f, Float f2, Float f3, Float f4): LLVector3 {
        return LLVector3(LLTersePacking.U8_to_float(byteBuffer.get() & UnsignedBytes.MAX_VALUE, f, f2), LLTersePacking.U8_to_float(byteBuffer.get() & UnsignedBytes.MAX_VALUE, f, f2), LLTersePacking.U8_to_float(byteBuffer.get() & UnsignedBytes.MAX_VALUE, f3, f4))
    }

    fun scaleFromMatrix(FloatArray fArr): LLVector3 {
        return LLVector3(Math.sqrt(((fArr[0] * fArr[0].toDouble()).toFloat() + (fArr[1] * fArr[1]) + (fArr[2] * fArr[2]))), Math.sqrt(((fArr[4] * fArr[4].toDouble()).toFloat() + (fArr[5] * fArr[5]) + (fArr[6] * fArr[6]))), Math.sqrt(((fArr[8] * fArr[8].toDouble()).toFloat() + (fArr[9] * fArr[9]) + (fArr[10] * fArr[10]))))
    }

    fun sub(LLVector3 lLVector3, LLVector3 lLVector32): LLVector3 {
        return LLVector3(lLVector3.x - lLVector32.x, lLVector3.y - lLVector32.y, lLVector3.z - lLVector32.z)
    }

    fun add(LLVector3 lLVector3): Unit {
        this.x += lLVector3.x
        this.y += lLVector3.y
        this.z += lLVector3.z
    }

    fun addMul(ImmutableVector immutableVector, Float f): Unit {
        this.x += immutableVector.x * f
        this.y += immutableVector.y * f
        this.z += immutableVector.z * f
    }

    fun addMul(LLVector3 lLVector3, Float f): Unit {
        this.x += lLVector3.x * f
        this.y += lLVector3.y * f
        this.z += lLVector3.z * f
    }

    fun dot(LLVector3 lLVector3): Float {
        return (this.x * lLVector3.x) + (this.y * lLVector3.y) + (this.z * lLVector3.z)
    }

    fun equals(Any obj): Boolean {
        if (obj == this) {
            return true
        }
        if (!(obj instanceof LLVector3)) {
            return false
        }
        LLVector3 lLVector3 = (LLVector3) obj
        return this.x == lLVector3.x && this.y == lLVector3.y && this.z == lLVector3.z
    }

    fun getDistanceTo(LLVector3 lLVector3): Float {
        Float f = this.x - lLVector3.x
        Float f2 = this.y - lLVector3.y
        Float f3 = this.z - lLVector3.z
        return Math.sqrt(((f * f.toDouble()).toFloat() + (f2 * f2) + (f3 * f3)))
    }

    fun getMax(): Float {
        return Math.max(Math.max(this.x, this.y), this.z)
    }

    fun getRotatedOffset(Float f, Float f2): LLVector3 {
        Float f3 = (3.1415927f * f2) / 180.0f
        return LLVector3(((Math.toFloat().cos(f3.toDouble())) * f) + this.x, ((Math.toFloat().sin(f3.toDouble())) * f) + this.y, this.z)
    }

    fun hashCode(): Int {
        return Float.floatToIntBits(this.x) + Float.floatToIntBits(this.y) + Float.floatToIntBits(this.z)
    }

    fun isZero(): Boolean {
        return this.x == 0.0f && this.y == 0.0f && this.z == 0.0f
    }

    fun magVec(): Float {
        return Math.sqrt(((this.x * this.x.toDouble()).toFloat() + (this.y * this.y) + (this.z * this.z)))
    }

    fun magVecSquared(): Float {
        return (this.x * this.x) + (this.y * this.y) + (this.z * this.z)
    }

    fun mul(Float f): Unit {
        this.x *= f
        this.y *= f
        this.z *= f
    }

    fun mul(LLQuaternion lLQuaternion): Unit {
        Float f = (((-lLQuaternion.x) * this.x) - (lLQuaternion.y * this.y)) - (lLQuaternion.z * this.z)
        Float f2 = ((lLQuaternion.w * this.x) + (lLQuaternion.y * this.z)) - (lLQuaternion.z * this.y)
        Float f3 = ((lLQuaternion.w * this.y) + (lLQuaternion.z * this.x)) - (lLQuaternion.x * this.z)
        Float f4 = ((lLQuaternion.w * this.z) + (lLQuaternion.x * this.y)) - (lLQuaternion.y * this.x)
        this.x = ((((-f) * lLQuaternion.x) + (lLQuaternion.w * f2)) - (lLQuaternion.z * f3)) + (lLQuaternion.y * f4)
        this.y = ((((-f) * lLQuaternion.y) + (lLQuaternion.w * f3)) - (lLQuaternion.x * f4)) + (lLQuaternion.z * f2)
        this.z = ((((-f) * lLQuaternion.z) + (f4 * lLQuaternion.w)) - (f2 * lLQuaternion.y)) + (lLQuaternion.x * f3)
    }

    fun mul(LLVector3 lLVector3): Unit {
        this.x *= lLVector3.x
        this.y *= lLVector3.y
        this.z *= lLVector3.z
    }

    fun mulWeighted(ImmutableVector immutableVector, Float f): Unit {
        this.x *= (immutableVector.x * f) + 1.0f
        this.y *= (immutableVector.y * f) + 1.0f
        this.z *= (immutableVector.z * f) + 1.0f
    }

    fun mulWeighted(LLVector3 lLVector3, Float f): Unit {
        this.x *= (lLVector3.x * f) + 1.0f
        this.y *= (lLVector3.y * f) + 1.0f
        this.z *= (lLVector3.z * f) + 1.0f
    }

    fun normVec(): Float {
        Float sqrt = Math.sqrt(((this.x * this.x.toDouble()).toFloat() + (this.y * this.y) + (this.z * this.z)))
        if (sqrt > 1.0E-7f) {
            Float f = 1.0f / sqrt
            this.x *= f
            this.y *= f
            this.z = f * this.z
        } else {
            this.x = 0.0f
            this.y = 0.0f
            this.z = 0.0f
        }
        return sqrt
    }

    fun set(Float f, Float f2, Float f3): Unit {
        this.x = f
        this.y = f2
        this.z = f3
    }

    fun set(LLVector3 lLVector3): Unit {
        if (lLVector3 != null) {
            this.x = lLVector3.x
            this.y = lLVector3.y
            this.z = lLVector3.z
        }
    }

    fun setAdd(LLVector3 lLVector3, LLVector3 lLVector32): Unit {
        this.x = lLVector3.x + lLVector32.x
        this.y = lLVector3.y + lLVector32.y
        this.z = lLVector3.z + lLVector32.z
    }

    fun setCross(LLVector3 lLVector3): Unit {
        Float f = (this.y * lLVector3.z) - (lLVector3.y * this.z)
        Float f2 = (this.z * lLVector3.x) - (lLVector3.z * this.x)
        this.x = f
        this.y = f2
        this.z = (this.x * lLVector3.y) - (lLVector3.x * this.y)
    }

    fun setLerp(LLVector3 lLVector3, Float f, LLVector3 lLVector32, Float f2): Unit {
        this.x = (lLVector3.x * f) + (lLVector32.x * f2)
        this.y = (lLVector3.y * f) + (lLVector32.y * f2)
        this.z = (lLVector3.z * f) + (lLVector32.z * f2)
    }

    fun setLerp(LLVector3 lLVector3, LLVector3 lLVector32, Float f): Unit {
        this.x = lLVector3.x + ((lLVector32.x - lLVector3.x) * f)
        this.y = lLVector3.y + ((lLVector32.y - lLVector3.y) * f)
        this.z = lLVector3.z + ((lLVector32.z - lLVector3.z) * f)
    }

    fun setMul(LLVector3 lLVector3, Float f): Unit {
        this.x = lLVector3.x * f
        this.y = lLVector3.y * f
        this.z = lLVector3.z * f
    }

    fun setMul(LLVector3 lLVector3, LLVector3 lLVector32): Unit {
        this.x = lLVector3.x * lLVector32.x
        this.y = lLVector3.y * lLVector32.y
        this.z = lLVector3.z * lLVector32.z
    }

    fun setSub(LLVector3 lLVector3, LLVector3 lLVector32): Unit {
        this.x = lLVector3.x - lLVector32.x
        this.y = lLVector3.y - lLVector32.y
        this.z = lLVector3.z - lLVector32.z
    }

    fun sub(LLVector3 lLVector3): Unit {
        this.x -= lLVector3.x
        this.y -= lLVector3.y
        this.z -= lLVector3.z
    }

    fun toLLSD(): LLSDNode {
        return LLSDMap(LLSDMap.LLSDMapEntry("X", LLSDDouble(this.toDouble().x)), LLSDMap.LLSDMapEntry("Y", LLSDDouble(this.toDouble().y)), LLSDMap.LLSDMapEntry("Z", LLSDDouble(this.toDouble().z)))
    }

    fun toString(): String {
        return String.format("(%f, %f, %f)", Any[]{Float.valueOf(this.x), Float.valueOf(this.y), Float.valueOf(this.z)})
    }
}
