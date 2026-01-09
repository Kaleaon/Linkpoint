package com.linkpoint.slproto.types

import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

/**
 * 2D Vector class for Second Life coordinate system
 * Used for UV coordinates, 2D positions, etc.
 */
class LLVector2(
    var x: Float = 0.0f,
    var y: Float = 0.0f
) {
    companion object {
        const val FP_MAG_THRESHOLD: Float = 1.0E-7f
        
        @JvmField
        val Zero = LLVector2(0.0f, 0.0f)
        
        @JvmField
        val One = LLVector2(1.0f, 1.0f)
        
        @JvmStatic
        fun sub(a: LLVector2, b: LLVector2): LLVector2 {
            return LLVector2(a.x - b.x, a.y - b.y)
        }
        
        @JvmStatic
        fun sum(a: LLVector2, b: LLVector2): LLVector2 {
            return LLVector2(a.x + b.x, a.y + b.y)
        }
    }
    
    constructor(other: LLVector2) : this(other.x, other.y)
    
    fun add(other: LLVector2) {
        x += other.x
        y += other.y
    }
    
    fun dot(other: LLVector2): Float {
        return (x * other.x) + (y * other.y)
    }
    
    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is LLVector2) return false
        return x == other.x && y == other.y
    }
    
    override fun hashCode(): Int {
        return java.lang.Float.floatToIntBits(x) + java.lang.Float.floatToIntBits(y)
    }
    
    fun magVec(): Float {
        return sqrt((x * x + y * y).toDouble()).toFloat()
    }
    
    fun magVecSquared(): Float {
        return x * x + y * y
    }
    
    fun mul(scale: Float) {
        x *= scale
        y *= scale
    }
    
    fun normVec(): Float {
        val magnitude = sqrt((x * x + y * y).toDouble()).toFloat()
        if (magnitude > FP_MAG_THRESHOLD) {
            val invMag = 1.0f / magnitude
            x *= invMag
            y *= invMag
        } else {
            x = 0.0f
            y = 0.0f
        }
        return magnitude
    }
    
    fun set(newX: Float, newY: Float) {
        x = newX
        y = newY
    }
    
    fun set(other: LLVector2?) {
        if (other != null) {
            x = other.x
            y = other.y
        }
    }
    
    fun setMax(other: LLVector2) {
        x = max(x, other.x)
        y = max(y, other.y)
    }
    
    fun setMin(other: LLVector2) {
        x = min(x, other.x)
        y = min(y, other.y)
    }
    
    fun isZero(): Boolean {
        return x == 0.0f && y == 0.0f
    }
    
    override fun toString(): String {
        return String.format("(%.4f, %.4f)", x, y)
    }
    
    // Operator overloads
    operator fun plus(other: LLVector2): LLVector2 {
        return LLVector2(x + other.x, y + other.y)
    }
    
    operator fun minus(other: LLVector2): LLVector2 {
        return LLVector2(x - other.x, y - other.y)
    }
    
    operator fun times(scale: Float): LLVector2 {
        return LLVector2(x * scale, y * scale)
    }
    
    operator fun times(other: LLVector2): LLVector2 {
        return LLVector2(x * other.x, y * other.y)
    }
    
    operator fun div(scale: Float): LLVector2 {
        return LLVector2(x / scale, y / scale)
    }
    
    operator fun unaryMinus(): LLVector2 {
        return LLVector2(-x, -y)
    }
}
