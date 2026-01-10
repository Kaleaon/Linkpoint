package com.linkpoint.slproto.types

import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

/**
 * 4D Vector class for Second Life coordinate system
 * Used for colors (RGBA), homogeneous coordinates, etc.
 */
class LLVector4(
    var x: Float = 0.0f,
    var y: Float = 0.0f,
    var z: Float = 0.0f,
    var w: Float = 0.0f
) {
    companion object {
        const val FP_MAG_THRESHOLD: Float = 1.0E-7f
        
        @JvmField
        val Zero = LLVector4(0.0f, 0.0f, 0.0f, 0.0f)
        
        @JvmField
        val One = LLVector4(1.0f, 1.0f, 1.0f, 1.0f)
        
        @JvmStatic
        fun add(a: LLVector4, b: LLVector4): LLVector4 {
            return LLVector4(a.x + b.x, a.y + b.y, a.z + b.z, a.w + b.w)
        }
        
        @JvmStatic
        fun cross3(a: LLVector4, b: LLVector4): LLVector4 {
            return LLVector4(
                (a.y * b.z) - (a.z * b.y),
                (a.z * b.x) - (a.x * b.z),
                (a.x * b.y) - (a.y * b.x),
                0.0f
            )
        }
        
        @JvmStatic
        fun sub(a: LLVector4, b: LLVector4): LLVector4 {
            return LLVector4(a.x - b.x, a.y - b.y, a.z - b.z, a.w - b.w)
        }
    }
    
    constructor(x: Float, y: Float, z: Float) : this(x, y, z, 0.0f)
    
    constructor(vec3: LLVector3) : this(vec3.x, vec3.y, vec3.z, 0.0f)
    
    constructor(other: LLVector4) : this(other.x, other.y, other.z, other.w)
    
    fun add(other: LLVector4) {
        x += other.x
        y += other.y
        z += other.z
        w += other.w
    }
    
    fun clear() {
        x = 0.0f
        y = 0.0f
        z = 0.0f
        w = 0.0f
    }
    
    fun dot3(other: LLVector4): Float {
        return (x * other.x) + (y * other.y) + (z * other.z)
    }
    
    fun dot4(other: LLVector4): Float {
        return (x * other.x) + (y * other.y) + (z * other.z) + (w * other.w)
    }
    
    fun mul(scale: Float) {
        x *= scale
        y *= scale
        z *= scale
        w *= scale
    }
    
    fun normalize3(): Float {
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
    
    fun normalize4(): Float {
        val magnitude = sqrt((x * x + y * y + z * z + w * w).toDouble()).toFloat()
        if (magnitude > FP_MAG_THRESHOLD) {
            val invMag = 1.0f / magnitude
            x *= invMag
            y *= invMag
            z *= invMag
            w *= invMag
        } else {
            x = 0.0f
            y = 0.0f
            z = 0.0f
            w = 0.0f
        }
        return magnitude
    }
    
    fun magVec3(): Float {
        return sqrt((x * x + y * y + z * z).toDouble()).toFloat()
    }
    
    fun magVec4(): Float {
        return sqrt((x * x + y * y + z * z + w * w).toDouble()).toFloat()
    }
    
    fun set(newX: Float, newY: Float, newZ: Float) {
        x = newX
        y = newY
        z = newZ
        w = 0.0f
    }
    
    fun set(newX: Float, newY: Float, newZ: Float, newW: Float) {
        x = newX
        y = newY
        z = newZ
        w = newW
    }
    
    fun set(other: LLVector4?) {
        if (other != null) {
            x = other.x
            y = other.y
            z = other.z
            w = other.w
        }
    }
    
    fun setMax(other: LLVector4) {
        x = max(x, other.x)
        y = max(y, other.y)
        z = max(z, other.z)
        w = max(w, other.w)
    }
    
    fun setMin(other: LLVector4) {
        x = min(x, other.x)
        y = min(y, other.y)
        z = min(z, other.z)
        w = min(w, other.w)
    }
    
    fun toVector3(): LLVector3 {
        return LLVector3(x, y, z)
    }
    
    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is LLVector4) return false
        return x == other.x && y == other.y && z == other.z && w == other.w
    }
    
    override fun hashCode(): Int {
        var result = java.lang.Float.floatToIntBits(x)
        result = 31 * result + java.lang.Float.floatToIntBits(y)
        result = 31 * result + java.lang.Float.floatToIntBits(z)
        result = 31 * result + java.lang.Float.floatToIntBits(w)
        return result
    }
    
    override fun toString(): String {
        return String.format("(%.4f, %.4f, %.4f, %.4f)", x, y, z, w)
    }
    
    // Operator overloads
    operator fun plus(other: LLVector4): LLVector4 {
        return LLVector4(x + other.x, y + other.y, z + other.z, w + other.w)
    }
    
    operator fun minus(other: LLVector4): LLVector4 {
        return LLVector4(x - other.x, y - other.y, z - other.z, w - other.w)
    }
    
    operator fun times(scale: Float): LLVector4 {
        return LLVector4(x * scale, y * scale, z * scale, w * scale)
    }
    
    operator fun div(scale: Float): LLVector4 {
        return LLVector4(x / scale, y / scale, z / scale, w / scale)
    }
    
    operator fun unaryMinus(): LLVector4 {
        return LLVector4(-x, -y, -z, -w)
    }
}
