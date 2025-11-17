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

    LLVector3 cross(LLVector3 lLVector3, LLVector3 lLVector32) {
        return LLVector3((lLVector3.y * lLVector32.z) - (lLVector32.y * lLVector3.z), (lLVector3.z * lLVector32.x) - (lLVector32.z * lLVector3.x), (lLVector3.x * lLVector32.y) - (lLVector32.x * lLVector3.y))
    }

    LLVector3 lerp(LLVector3 lLVector3, LLVector3 lLVector32, Float f) {
        return LLVector3(lLVector3.x + ((lLVector32.x - lLVector3.x) * f), lLVector3.y + ((lLVector32.y - lLVector3.y) * f), lLVector3.z + ((lLVector32.z - lLVector3.z) * f))
    }

    LLVector3 parseFloatVec(ByteBuffer byteBuffer) {
        return LLVector3(byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat())
    }

    LLVector3 parseU16Vec(ByteBuffer byteBuffer, Float f, Float f2, Float f3, Float f4) {
        return LLVector3(LLTersePacking.U16_to_float(byteBuffer.getShort() & 65535, f, f2), LLTersePacking.U16_to_float(byteBuffer.getShort() & 65535, f, f2), LLTersePacking.U16_to_float(byteBuffer.getShort() & 65535, f3, f4))
    }

    LLVector3 parseU8Vec(ByteBuffer byteBuffer, Float f, Float f2, Float f3, Float f4) {
        return LLVector3(LLTersePacking.U8_to_float(byteBuffer.get() & UnsignedBytes.MAX_VALUE, f, f2), LLTersePacking.U8_to_float(byteBuffer.get() & UnsignedBytes.MAX_VALUE, f, f2), LLTersePacking.U8_to_float(byteBuffer.get() & UnsignedBytes.MAX_VALUE, f3, f4))
    }

    LLVector3 scaleFromMatrix(FloatArray fArr) {
        return LLVector3(Math.sqrt(((fArr[0] * fArr[0].toDouble()).toFloat() + (fArr[1] * fArr[1]) + (fArr[2] * fArr[2]))), Math.sqrt(((fArr[4] * fArr[4].toDouble()).toFloat() + (fArr[5] * fArr[5]) + (fArr[6] * fArr[6]))), Math.sqrt(((fArr[8] * fArr[8].toDouble()).toFloat() + (fArr[9] * fArr[9]) + (fArr[10] * fArr[10]))))
    }

    LLVector3 sub(LLVector3 lLVector3, LLVector3 lLVector32) {
        return LLVector3(lLVector3.x - lLVector32.x, lLVector3.y - lLVector32.y, lLVector3.z - lLVector32.z)
    }

    Unit add(LLVector3 lLVector3) {
        this.x += lLVector3.x
        this.y += lLVector3.y
        this.z += lLVector3.z
    }

    Unit addMul(ImmutableVector immutableVector, Float f) {
        this.x += immutableVector.x * f
        this.y += immutableVector.y * f
        this.z += immutableVector.z * f
    }

    Unit addMul(LLVector3 lLVector3, Float f) {
        this.x += lLVector3.x * f
        this.y += lLVector3.y * f
        this.z += lLVector3.z * f
    }

    Float dot(LLVector3 lLVector3) {
        return (this.x * lLVector3.x) + (this.y * lLVector3.y) + (this.z * lLVector3.z)
    }

    Boolean equals(Any obj) {
        if (obj == this) {
            return true
        }
        if (!(obj instanceof LLVector3)) {
            return false
        }
        LLVector3 lLVector3 = (LLVector3) obj
        return this.x == lLVector3.x && this.y == lLVector3.y && this.z == lLVector3.z
    }

    Float getDistanceTo(LLVector3 lLVector3) {
        Float f = this.x - lLVector3.x
        Float f2 = this.y - lLVector3.y
        Float f3 = this.z - lLVector3.z
        return Math.sqrt(((f * f.toDouble()).toFloat() + (f2 * f2) + (f3 * f3)))
    }

    Float getMax() {
        return Math.max(Math.max(this.x, this.y), this.z)
    }

    LLVector3 getRotatedOffset(Float f, Float f2) {
        Float f3 = (3.1415927f * f2) / 180.0f
        return LLVector3(((Math.toFloat().cos(f3.toDouble())) * f) + this.x, ((Math.toFloat().sin(f3.toDouble())) * f) + this.y, this.z)
    }

    Int hashCode() {
        return Float.floatToIntBits(this.x) + Float.floatToIntBits(this.y) + Float.floatToIntBits(this.z)
    }

    Boolean isZero() {
        return this.x == 0.0f && this.y == 0.0f && this.z == 0.0f
    }

    Float magVec() {
        return Math.sqrt(((this.x * this.x.toDouble()).toFloat() + (this.y * this.y) + (this.z * this.z)))
    }

    Float magVecSquared() {
        return (this.x * this.x) + (this.y * this.y) + (this.z * this.z)
    }

    Unit mul(Float f) {
        this.x *= f
        this.y *= f
        this.z *= f
    }

    Unit mul(LLQuaternion lLQuaternion) {
        Float f = (((-lLQuaternion.x) * this.x) - (lLQuaternion.y * this.y)) - (lLQuaternion.z * this.z)
        Float f2 = ((lLQuaternion.w * this.x) + (lLQuaternion.y * this.z)) - (lLQuaternion.z * this.y)
        Float f3 = ((lLQuaternion.w * this.y) + (lLQuaternion.z * this.x)) - (lLQuaternion.x * this.z)
        Float f4 = ((lLQuaternion.w * this.z) + (lLQuaternion.x * this.y)) - (lLQuaternion.y * this.x)
        this.x = ((((-f) * lLQuaternion.x) + (lLQuaternion.w * f2)) - (lLQuaternion.z * f3)) + (lLQuaternion.y * f4)
        this.y = ((((-f) * lLQuaternion.y) + (lLQuaternion.w * f3)) - (lLQuaternion.x * f4)) + (lLQuaternion.z * f2)
        this.z = ((((-f) * lLQuaternion.z) + (f4 * lLQuaternion.w)) - (f2 * lLQuaternion.y)) + (lLQuaternion.x * f3)
    }

    Unit mul(LLVector3 lLVector3) {
        this.x *= lLVector3.x
        this.y *= lLVector3.y
        this.z *= lLVector3.z
    }

    Unit mulWeighted(ImmutableVector immutableVector, Float f) {
        this.x *= (immutableVector.x * f) + 1.0f
        this.y *= (immutableVector.y * f) + 1.0f
        this.z *= (immutableVector.z * f) + 1.0f
    }

    Unit mulWeighted(LLVector3 lLVector3, Float f) {
        this.x *= (lLVector3.x * f) + 1.0f
        this.y *= (lLVector3.y * f) + 1.0f
        this.z *= (lLVector3.z * f) + 1.0f
    }

    Float normVec() {
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

    Unit set(Float f, Float f2, Float f3) {
        this.x = f
        this.y = f2
        this.z = f3
    }

    Unit set(LLVector3 lLVector3) {
        if (lLVector3 != null) {
            this.x = lLVector3.x
            this.y = lLVector3.y
            this.z = lLVector3.z
        }
    }

    Unit setAdd(LLVector3 lLVector3, LLVector3 lLVector32) {
        this.x = lLVector3.x + lLVector32.x
        this.y = lLVector3.y + lLVector32.y
        this.z = lLVector3.z + lLVector32.z
    }

    Unit setCross(LLVector3 lLVector3) {
        Float f = (this.y * lLVector3.z) - (lLVector3.y * this.z)
        Float f2 = (this.z * lLVector3.x) - (lLVector3.z * this.x)
        this.x = f
        this.y = f2
        this.z = (this.x * lLVector3.y) - (lLVector3.x * this.y)
    }

    Unit setLerp(LLVector3 lLVector3, Float f, LLVector3 lLVector32, Float f2) {
        this.x = (lLVector3.x * f) + (lLVector32.x * f2)
        this.y = (lLVector3.y * f) + (lLVector32.y * f2)
        this.z = (lLVector3.z * f) + (lLVector32.z * f2)
    }

    Unit setLerp(LLVector3 lLVector3, LLVector3 lLVector32, Float f) {
        this.x = lLVector3.x + ((lLVector32.x - lLVector3.x) * f)
        this.y = lLVector3.y + ((lLVector32.y - lLVector3.y) * f)
        this.z = lLVector3.z + ((lLVector32.z - lLVector3.z) * f)
    }

    Unit setMul(LLVector3 lLVector3, Float f) {
        this.x = lLVector3.x * f
        this.y = lLVector3.y * f
        this.z = lLVector3.z * f
    }

    Unit setMul(LLVector3 lLVector3, LLVector3 lLVector32) {
        this.x = lLVector3.x * lLVector32.x
        this.y = lLVector3.y * lLVector32.y
        this.z = lLVector3.z * lLVector32.z
    }

    Unit setSub(LLVector3 lLVector3, LLVector3 lLVector32) {
        this.x = lLVector3.x - lLVector32.x
        this.y = lLVector3.y - lLVector32.y
        this.z = lLVector3.z - lLVector32.z
    }

    Unit sub(LLVector3 lLVector3) {
        this.x -= lLVector3.x
        this.y -= lLVector3.y
        this.z -= lLVector3.z
    }

    LLSDNode toLLSD() {
        return LLSDMap(LLSDMap.LLSDMapEntry("X", LLSDDouble(this.toDouble().x)), LLSDMap.LLSDMapEntry("Y", LLSDDouble(this.toDouble().y)), LLSDMap.LLSDMapEntry("Z", LLSDDouble(this.toDouble().z)))
    }

    String toString() {
        return String.format("(%f, %f, %f)", Any[]{Float.valueOf(this.x), Float.valueOf(this.y), Float.valueOf(this.z)})
    }
}
