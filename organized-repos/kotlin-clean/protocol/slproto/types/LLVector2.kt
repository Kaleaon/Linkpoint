package com.linkpoint.slproto.types

import com.linkpoint.slproto.llsd.LLSD
import kotlin.math.*

/**
 * LLVector2 - 2D vector class
 * 
 * Based on Firestorm's v2math.h/cpp
 * Used primarily for texture coordinates
 */
class LLVector2 {
    
    val mV = FloatArray(LENGTHOFVECTOR2)
    
    companion object {
        const val LENGTHOFVECTOR2 = 2
        const val VX = 0
        const val VY = 1
        
        val zero = LLVector2(0f, 0f)
        val x_axis = LLVector2(1f, 0f)
        val y_axis = LLVector2(0f, 1f)
        
        const val FP_MAG_THRESHOLD = 1e-7f
    }
    
    var x: Float
        get() = mV[VX]
        set(value) { mV[VX] = value }
    
    var y: Float
        get() = mV[VY]
        set(value) { mV[VY] = value }
    
    constructor() {
        mV[VX] = 0f
        mV[VY] = 0f
    }
    
    constructor(x: Float, y: Float) {
        mV[VX] = x
        mV[VY] = y
    }
    
    constructor(vec: FloatArray) {
        mV[VX] = vec[VX]
        mV[VY] = vec[VY]
    }
    
    constructor(other: LLVector2) {
        mV[VX] = other.mV[VX]
        mV[VY] = other.mV[VY]
    }
    
    fun set(x: Float, y: Float): LLVector2 {
        mV[VX] = x
        mV[VY] = y
        return this
    }
    
    fun set(other: LLVector2): LLVector2 {
        mV[VX] = other.mV[VX]
        mV[VY] = other.mV[VY]
        return this
    }
    
    fun clear() {
        mV[VX] = 0f
        mV[VY] = 0f
    }
    
    fun length(): Float = sqrt(lengthSquared())
    fun lengthSquared(): Float = mV[VX] * mV[VX] + mV[VY] * mV[VY]
    
    fun normalize(): Float {
        val mag = length()
        if (mag > FP_MAG_THRESHOLD) {
            val oomag = 1f / mag
            mV[VX] *= oomag
            mV[VY] *= oomag
        }
        return mag
    }
    
    operator fun plus(other: LLVector2) = LLVector2(mV[VX] + other.mV[VX], mV[VY] + other.mV[VY])
    operator fun minus(other: LLVector2) = LLVector2(mV[VX] - other.mV[VX], mV[VY] - other.mV[VY])
    operator fun times(scalar: Float) = LLVector2(mV[VX] * scalar, mV[VY] * scalar)
    operator fun div(scalar: Float) = LLVector2(mV[VX] / scalar, mV[VY] / scalar)
    operator fun unaryMinus() = LLVector2(-mV[VX], -mV[VY])
    
    operator fun get(index: Int): Float = mV[index]
    operator fun set(index: Int, value: Float) { mV[index] = value }
    
    infix fun dot(other: LLVector2): Float = mV[VX] * other.mV[VX] + mV[VY] * other.mV[VY]
    
    override fun toString() = "LLVector2($x, $y)"
}
