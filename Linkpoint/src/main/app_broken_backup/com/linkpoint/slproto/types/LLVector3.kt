package com.linkpoint.slproto.types

import com.linkpoint.slproto.llsd.LLSDNode
import com.linkpoint.slproto.llsd.types.LLSDDouble
import com.linkpoint.slproto.llsd.types.LLSDMap
import java.nio.ByteBuffer
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * 3D Vector class for Second Life coordinate system
 * Used throughout the protocol for positions, directions, and scales
 */
class LLVector3(
    var x: Float = 0.0f,
    var y: Float = 0.0f,
    var z: Float = 0.0f
) {
    companion object {
        const val FP_MAG_THRESHOLD: Float = 1.0E-7f
        
        @JvmField
        val Zero = LLVector3(0.0f, 0.0f, 0.0f)
        
        @JvmField
        val ZAxis = LLVector3(0.0f, 0.0f, 1.0f)
        
        @JvmField
        val XAxis = LLVector3(1.0f, 0.0f, 0.0f)
        
        @JvmField
        val YAxis = LLVector3(0.0f, 1.0f, 0.0f)
        
        @JvmStatic
        fun cross(a: LLVector3, b: LLVector3): LLVector3 {
            return LLVector3(
                (a.y * b.z) - (b.y * a.z),
                (a.z * b.x) - (b.z * a.x),
                (a.x * b.y) - (b.x * a.y)
            )
        }
        
        @JvmStatic
        fun lerp(a: LLVector3, b: LLVector3, t: Float): LLVector3 {
            return LLVector3(
                a.x + ((b.x - a.x) * t),
                a.y + ((b.y - a.y) * t),
                a.z + ((b.z - a.z) * t)
            )
        }
        
        @JvmStatic
        fun parseFloatVec(buffer: ByteBuffer): LLVector3 {
            return LLVector3(buffer.getFloat(), buffer.getFloat(), buffer.getFloat())
        }
        
        @JvmStatic
        fun parseU16Vec(buffer: ByteBuffer, minX: Float, maxX: Float, minZ: Float, maxZ: Float): LLVector3 {
            return LLVector3(
                LLTersePacking.U16_to_float(buffer.getShort().toInt() and 0xFFFF, minX, maxX),
                LLTersePacking.U16_to_float(buffer.getShort().toInt() and 0xFFFF, minX, maxX),
                LLTersePacking.U16_to_float(buffer.getShort().toInt() and 0xFFFF, minZ, maxZ)
            )
        }
        
        @JvmStatic
        fun parseU8Vec(buffer: ByteBuffer, minX: Float, maxX: Float, minZ: Float, maxZ: Float): LLVector3 {
            return LLVector3(
                LLTersePacking.U8_to_float(buffer.get().toInt() and 0xFF, minX, maxX),
                LLTersePacking.U8_to_float(buffer.get().toInt() and 0xFF, minX, maxX),
                LLTersePacking.U8_to_float(buffer.get().toInt() and 0xFF, minZ, maxZ)
            )
        }
        
        @JvmStatic
        fun scaleFromMatrix(matrix: FloatArray): LLVector3 {
            return LLVector3(
                sqrt((matrix[0] * matrix[0] + matrix[1] * matrix[1] + matrix[2] * matrix[2]).toDouble()).toFloat(),
                sqrt((matrix[4] * matrix[4] + matrix[5] * matrix[5] + matrix[6] * matrix[6]).toDouble()).toFloat(),
                sqrt((matrix[8] * matrix[8] + matrix[9] * matrix[9] + matrix[10] * matrix[10]).toDouble()).toFloat()
            )
        }
        
        @JvmStatic
        fun sub(a: LLVector3, b: LLVector3): LLVector3 {
            return LLVector3(a.x - b.x, a.y - b.y, a.z - b.z)
        }
    }
    
    constructor(other: LLVector3) : this(other.x, other.y, other.z)
    
    fun add(other: LLVector3) {
        x += other.x
        y += other.y
        z += other.z
    }
    
    fun addMul(vec: LLVector3, scale: Float) {
        x += vec.x * scale
        y += vec.y * scale
        z += vec.z * scale
    }
    
    fun dot(other: LLVector3): Float {
        return (x * other.x) + (y * other.y) + (z * other.z)
    }
    
    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is LLVector3) return false
        return x == other.x && y == other.y && z == other.z
    }
    
    fun getDistanceTo(other: LLVector3): Float {
        val dx = x - other.x
        val dy = y - other.y
        val dz = z - other.z
        return sqrt((dx * dx + dy * dy + dz * dz).toDouble()).toFloat()
    }
    
    fun getMax(): Float {
        return max(max(x, y), z)
    }
    
    fun getRotatedOffset(distance: Float, angleDegrees: Float): LLVector3 {
        val radians = (3.1415927f * angleDegrees) / 180.0f
        return LLVector3(
            (cos(radians.toDouble()).toFloat() * distance) + x,
            (sin(radians.toDouble()).toFloat() * distance) + y,
            z
        )
    }
    
    override fun hashCode(): Int {
        return java.lang.Float.floatToIntBits(x) + 
               java.lang.Float.floatToIntBits(y) + 
               java.lang.Float.floatToIntBits(z)
    }
    
    fun isZero(): Boolean {
        return x == 0.0f && y == 0.0f && z == 0.0f
    }
    
    fun magVec(): Float {
        return sqrt((x * x + y * y + z * z).toDouble()).toFloat()
    }
    
    fun magVecSquared(): Float {
        return (x * x) + (y * y) + (z * z)
    }
    
    fun mul(scale: Float) {
        x *= scale
        y *= scale
        z *= scale
    }
    
    fun mul(quaternion: LLQuaternion) {
        val qx = quaternion.x
        val qy = quaternion.y
        val qz = quaternion.z
        val qw = quaternion.w
        
        val f = (((-qx) * x) - (qy * y)) - (qz * z)
        val f2 = ((qw * x) + (qy * z)) - (qz * y)
        val f3 = ((qw * y) + (qz * x)) - (qx * z)
        val f4 = ((qw * z) + (qx * y)) - (qy * x)
        
        x = ((((-f) * qx) + (qw * f2)) - (qz * f3)) + (qy * f4)
        y = ((((-f) * qy) + (qw * f3)) - (qx * f4)) + (qz * f2)
        z = ((((-f) * qz) + (f4 * qw)) - (f2 * qy)) + (qx * f3)
    }
    
    fun mul(other: LLVector3) {
        x *= other.x
        y *= other.y
        z *= other.z
    }
    
    fun mulWeighted(vec: LLVector3, weight: Float) {
        x *= (vec.x * weight) + 1.0f
        y *= (vec.y * weight) + 1.0f
        z *= (vec.z * weight) + 1.0f
    }
    
    fun normVec(): Float {
        val magnitude = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
        if (magnitude > FP_MAG_THRESHOLD) {
            val invMag = 1.0f / magnitude
            x *= invMag
            y *= invMag
            z *= invMag
        } else {
            x = 0.0f
            y = 0.0f
            z = 0.0f
        }
        return magnitude
    }
    
    fun set(newX: Float, newY: Float, newZ: Float) {
        x = newX
        y = newY
        z = newZ
    }
    
    fun set(other: LLVector3?) {
        if (other != null) {
            x = other.x
            y = other.y
            z = other.z
        }
    }
    
    fun setAdd(a: LLVector3, b: LLVector3) {
        x = a.x + b.x
        y = a.y + b.y
        z = a.z + b.z
    }
    
    fun setCross(other: LLVector3) {
        val newX = (y * other.z) - (other.y * z)
        val newY = (z * other.x) - (other.z * x)
        z = (x * other.y) - (other.x * y)
        x = newX
        y = newY
    }
    
    fun setLerp(a: LLVector3, weightA: Float, b: LLVector3, weightB: Float) {
        x = (a.x * weightA) + (b.x * weightB)
        y = (a.y * weightA) + (b.y * weightB)
        z = (a.z * weightA) + (b.z * weightB)
    }
    
    fun setLerp(a: LLVector3, b: LLVector3, t: Float) {
        x = a.x + ((b.x - a.x) * t)
        y = a.y + ((b.y - a.y) * t)
        z = a.z + ((b.z - a.z) * t)
    }
    
    fun setMul(vec: LLVector3, scale: Float) {
        x = vec.x * scale
        y = vec.y * scale
        z = vec.z * scale
    }
    
    fun setMul(a: LLVector3, b: LLVector3) {
        x = a.x * b.x
        y = a.y * b.y
        z = a.z * b.z
    }
    
    fun setSub(a: LLVector3, b: LLVector3) {
        x = a.x - b.x
        y = a.y - b.y
        z = a.z - b.z
    }
    
    fun sub(other: LLVector3) {
        x -= other.x
        y -= other.y
        z -= other.z
    }
    
    fun toLLSD(): LLSDNode {
        return LLSDMap(
            LLSDMap.LLSDMapEntry("X", LLSDDouble(x.toDouble())),
            LLSDMap.LLSDMapEntry("Y", LLSDDouble(y.toDouble())),
            LLSDMap.LLSDMapEntry("Z", LLSDDouble(z.toDouble()))
        )
    }
    
    override fun toString(): String {
        return String.format("(%.4f, %.4f, %.4f)", x, y, z)
    }
    
    // Operator overloads for Kotlin
    operator fun plus(other: LLVector3): LLVector3 {
        return LLVector3(x + other.x, y + other.y, z + other.z)
    }
    
    operator fun minus(other: LLVector3): LLVector3 {
        return LLVector3(x - other.x, y - other.y, z - other.z)
    }
    
    operator fun times(scale: Float): LLVector3 {
        return LLVector3(x * scale, y * scale, z * scale)
    }
    
    operator fun times(other: LLVector3): LLVector3 {
        return LLVector3(x * other.x, y * other.y, z * other.z)
    }
    
    operator fun div(scale: Float): LLVector3 {
        return LLVector3(x / scale, y / scale, z / scale)
    }
    
    operator fun unaryMinus(): LLVector3 {
        return LLVector3(-x, -y, -z)
    }
}
