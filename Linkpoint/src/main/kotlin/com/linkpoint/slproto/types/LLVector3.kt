package com.linkpoint.slproto.types

import com.linkpoint.slproto.llsd.LLSD
import kotlin.math.*

/**
 * LLVector3 - 3D vector math class
 * 
 * Based on Firestorm's v3math.h/cpp
 * Provides all vector operations needed for Second Life
 */
class LLVector3 {
    
    // Components stored in array for compatibility
    val mV = FloatArray(LENGTHOFVECTOR3)
    
    companion object {
        const val LENGTHOFVECTOR3 = 3
        const val VX = 0
        const val VY = 1
        const val VZ = 2
        
        // Standard axis vectors
        val zero = LLVector3(0f, 0f, 0f)
        val x_axis = LLVector3(1f, 0f, 0f)
        val y_axis = LLVector3(0f, 1f, 0f)
        val z_axis = LLVector3(0f, 0f, 1f)
        val x_axis_neg = LLVector3(-1f, 0f, 0f)
        val y_axis_neg = LLVector3(0f, -1f, 0f)
        val z_axis_neg = LLVector3(0f, 0f, -1f)
        val all_one = LLVector3(1f, 1f, 1f)
        
        const val FP_MAG_THRESHOLD = 1e-7f
    }
    
    // Accessors
    var x: Float
        get() = mV[VX]
        set(value) { mV[VX] = value }
    
    var y: Float
        get() = mV[VY]
        set(value) { mV[VY] = value }
    
    var z: Float
        get() = mV[VZ]
        set(value) { mV[VZ] = value }
    
    // Constructors
    constructor() {
        mV[VX] = 0f
        mV[VY] = 0f
        mV[VZ] = 0f
    }
    
    constructor(x: Float, y: Float, z: Float) {
        mV[VX] = x
        mV[VY] = y
        mV[VZ] = z
    }
    
    constructor(vec: FloatArray) {
        mV[VX] = vec[VX]
        mV[VY] = vec[VY]
        mV[VZ] = vec[VZ]
    }
    
    constructor(other: LLVector3) {
        mV[VX] = other.mV[VX]
        mV[VY] = other.mV[VY]
        mV[VZ] = other.mV[VZ]
    }
    
    constructor(llsd: LLSD) {
        mV[VX] = llsd[0].asFloat()
        mV[VY] = llsd[1].asFloat()
        mV[VZ] = llsd[2].asFloat()
    }
    
    // LLSD conversion
    fun getValue(): LLSD {
        val array = LLSD()
        array.add(LLSD(mV[VX]))
        array.add(LLSD(mV[VY]))
        array.add(LLSD(mV[VZ]))
        return array
    }
    
    fun setValue(llsd: LLSD) {
        mV[VX] = llsd[0].asFloat()
        mV[VY] = llsd[1].asFloat()
        mV[VZ] = llsd[2].asFloat()
    }
    
    // Validation
    fun isFinite(): Boolean {
        return mV[VX].isFinite() && mV[VY].isFinite() && mV[VZ].isFinite()
    }
    
    fun isExactlyZero(): Boolean {
        return mV[VX] == 0f && mV[VY] == 0f && mV[VZ] == 0f
    }
    
    // Setters
    fun set(x: Float, y: Float, z: Float): LLVector3 {
        mV[VX] = x
        mV[VY] = y
        mV[VZ] = z
        return this
    }
    
    fun set(other: LLVector3): LLVector3 {
        mV[VX] = other.mV[VX]
        mV[VY] = other.mV[VY]
        mV[VZ] = other.mV[VZ]
        return this
    }
    
    fun clear() {
        mV[VX] = 0f
        mV[VY] = 0f
        mV[VZ] = 0f
    }
    
    fun setZero() = clear()
    
    // Vector operations
    fun length(): Float {
        return sqrt(lengthSquared())
    }
    
    fun lengthSquared(): Float {
        return mV[VX] * mV[VX] + mV[VY] * mV[VY] + mV[VZ] * mV[VZ]
    }
    
    fun normalize(): Float {
        val mag = length()
        
        if (mag > FP_MAG_THRESHOLD) {
            val oomag = 1f / mag
            mV[VX] *= oomag
            mV[VY] *= oomag
            mV[VZ] *= oomag
        } else {
            mV[VX] = 0f
            mV[VY] = 0f
            mV[VZ] = 0f
        }
        
        return mag
    }
    
    fun normalized(): LLVector3 {
        val copy = LLVector3(this)
        copy.normalize()
        return copy
    }
    
    fun distance(other: LLVector3): Float {
        val dx = mV[VX] - other.mV[VX]
        val dy = mV[VY] - other.mV[VY]
        val dz = mV[VZ] - other.mV[VZ]
        return sqrt(dx * dx + dy * dy + dz * dz)
    }
    
    fun distanceSquared(other: LLVector3): Float {
        val dx = mV[VX] - other.mV[VX]
        val dy = mV[VY] - other.mV[VY]
        val dz = mV[VZ] - other.mV[VZ]
        return dx * dx + dy * dy + dz * dz
    }
    
    // Clamping
    fun clamp(min: Float, max: Float): Boolean {
        var changed = false
        
        if (mV[VX] < min) { mV[VX] = min; changed = true }
        if (mV[VX] > max) { mV[VX] = max; changed = true }
        
        if (mV[VY] < min) { mV[VY] = min; changed = true }
        if (mV[VY] > max) { mV[VY] = max; changed = true }
        
        if (mV[VZ] < min) { mV[VZ] = min; changed = true }
        if (mV[VZ] > max) { mV[VZ] = max; changed = true }
        
        return changed
    }
    
    fun clamp(minVec: LLVector3, maxVec: LLVector3): Boolean {
        var changed = false
        
        if (mV[VX] < minVec.mV[VX]) { mV[VX] = minVec.mV[VX]; changed = true }
        if (mV[VX] > maxVec.mV[VX]) { mV[VX] = maxVec.mV[VX]; changed = true }
        
        if (mV[VY] < minVec.mV[VY]) { mV[VY] = minVec.mV[VY]; changed = true }
        if (mV[VY] > maxVec.mV[VY]) { mV[VY] = maxVec.mV[VY]; changed = true }
        
        if (mV[VZ] < minVec.mV[VZ]) { mV[VZ] = minVec.mV[VZ]; changed = true }
        if (mV[VZ] > maxVec.mV[VZ]) { mV[VZ] = maxVec.mV[VZ]; changed = true }
        
        return changed
    }
    
    fun clampLength(lengthLimit: Float): Boolean {
        val len = length()
        if (len > lengthLimit && len > 0f) {
            val scale = lengthLimit / len
            mV[VX] *= scale
            mV[VY] *= scale
            mV[VZ] *= scale
            return true
        }
        return false
    }
    
    // Quantization (for network transmission)
    fun quantize16(lowerXY: Float, upperXY: Float, lowerZ: Float, upperZ: Float) {
        val rangeXY = upperXY - lowerXY
        val rangeZ = upperZ - lowerZ
        
        // Clamp first
        mV[VX] = mV[VX].coerceIn(lowerXY, upperXY)
        mV[VY] = mV[VY].coerceIn(lowerXY, upperXY)
        mV[VZ] = mV[VZ].coerceIn(lowerZ, upperZ)
        
        // Quantize to 16-bit
        val quantX = ((mV[VX] - lowerXY) / rangeXY * 65535f).toInt()
        val quantY = ((mV[VY] - lowerXY) / rangeXY * 65535f).toInt()
        val quantZ = ((mV[VZ] - lowerZ) / rangeZ * 65535f).toInt()
        
        // Dequantize
        mV[VX] = lowerXY + (quantX.toFloat() / 65535f) * rangeXY
        mV[VY] = lowerXY + (quantY.toFloat() / 65535f) * rangeXY
        mV[VZ] = lowerZ + (quantZ.toFloat() / 65535f) * rangeZ
    }
    
    // Absolute value
    fun abs(): Boolean {
        var changed = false
        if (mV[VX] < 0f) { mV[VX] = -mV[VX]; changed = true }
        if (mV[VY] < 0f) { mV[VY] = -mV[VY]; changed = true }
        if (mV[VZ] < 0f) { mV[VZ] = -mV[VZ]; changed = true }
        return changed
    }
    
    // Operators
    operator fun plus(other: LLVector3): LLVector3 {
        return LLVector3(
            mV[VX] + other.mV[VX],
            mV[VY] + other.mV[VY],
            mV[VZ] + other.mV[VZ]
        )
    }
    
    operator fun minus(other: LLVector3): LLVector3 {
        return LLVector3(
            mV[VX] - other.mV[VX],
            mV[VY] - other.mV[VY],
            mV[VZ] - other.mV[VZ]
        )
    }
    
    operator fun times(scalar: Float): LLVector3 {
        return LLVector3(
            mV[VX] * scalar,
            mV[VY] * scalar,
            mV[VZ] * scalar
        )
    }
    
    operator fun times(other: LLVector3): LLVector3 {
        // Component-wise multiply
        return LLVector3(
            mV[VX] * other.mV[VX],
            mV[VY] * other.mV[VY],
            mV[VZ] * other.mV[VZ]
        )
    }
    
    operator fun div(scalar: Float): LLVector3 {
        if (abs(scalar) < FP_MAG_THRESHOLD) {
            return LLVector3(0f, 0f, 0f)
        }
        return LLVector3(
            mV[VX] / scalar,
            mV[VY] / scalar,
            mV[VZ] / scalar
        )
    }
    
    operator fun unaryMinus(): LLVector3 {
        return LLVector3(-mV[VX], -mV[VY], -mV[VZ])
    }
    
    operator fun get(index: Int): Float = mV[index]
    operator fun set(index: Int, value: Float) { mV[index] = value }
    
    // Dot product
    infix fun dot(other: LLVector3): Float {
        return mV[VX] * other.mV[VX] + mV[VY] * other.mV[VY] + mV[VZ] * other.mV[VZ]
    }
    
    // Cross product
    infix fun cross(other: LLVector3): LLVector3 {
        return LLVector3(
            mV[VY] * other.mV[VZ] - mV[VZ] * other.mV[VY],
            mV[VZ] * other.mV[VX] - mV[VX] * other.mV[VZ],
            mV[VX] * other.mV[VY] - mV[VY] * other.mV[VX]
        )
    }
    
    // Rotation
    fun rotVec(angle: Float, axis: LLVector3): LLVector3 {
        val quat = LLQuaternion(angle, axis)
        return this * quat
    }
    
    fun rotVec(quat: LLQuaternion): LLVector3 {
        return this * quat
    }
    
    // Scaling
    fun scaleVec(scale: LLVector3): LLVector3 {
        mV[VX] *= scale.mV[VX]
        mV[VY] *= scale.mV[VY]
        mV[VZ] *= scale.mV[VZ]
        return this
    }
    
    // Interpolation
    fun lerp(other: LLVector3, t: Float): LLVector3 {
        return LLVector3(
            mV[VX] + (other.mV[VX] - mV[VX]) * t,
            mV[VY] + (other.mV[VY] - mV[VY]) * t,
            mV[VZ] + (other.mV[VZ] - mV[VZ]) * t
        )
    }
    
    // Comparison
    fun equals(other: LLVector3, epsilon: Float): Boolean {
        return abs(mV[VX] - other.mV[VX]) < epsilon &&
               abs(mV[VY] - other.mV[VY]) < epsilon &&
               abs(mV[VZ] - other.mV[VZ]) < epsilon
    }
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LLVector3) return false
        return mV[VX] == other.mV[VX] &&
               mV[VY] == other.mV[VY] &&
               mV[VZ] == other.mV[VZ]
    }
    
    override fun hashCode(): Int {
        var result = mV[VX].hashCode()
        result = 31 * result + mV[VY].hashCode()
        result = 31 * result + mV[VZ].hashCode()
        return result
    }
    
    override fun toString(): String {
        return "LLVector3(${mV[VX]}, ${mV[VY]}, ${mV[VZ]})"
    }
    
    // Conversion to rotation (for single-axis rotations)
    fun toQuaternion(): LLQuaternion {
        // Interpret as axis-angle where length is angle
        val angle = length()
        return if (angle > FP_MAG_THRESHOLD) {
            val axis = this / angle
            LLQuaternion(angle, axis)
        } else {
            LLQuaternion() // Identity
        }
    }
}

// Extension functions for operator overloading
operator fun Float.times(vec: LLVector3): LLVector3 {
    return vec * this
}
