package com.lumiyaviewer.lumiya.slproto.types

import kotlin.math.sqrt
import kotlin.math.max
import kotlin.math.min

class LLVector4 {
    companion object {
        const val FP_MAG_THRESHOLD: Float = 1.0E-7f
    }

    var w: Float = 0.0f
    var x: Float = 0.0f
    var y: Float = 0.0f
    var z: Float = 0.0f

    constructor() {
        this.x = 0.0f
        this.y = 0.0f
        this.z = 0.0f
        this.w = 0.0f
    }

    constructor(f: Float, f2: Float, f3: Float) {
        this.x = f
        this.y = f2
        this.z = f3
        this.w = 0.0f
    }

    constructor(f: Float, f2: Float, f3: Float, f4: Float) {
        this.x = f
        this.y = f2
        this.z = f3
        this.w = f4
    }

    constructor(lLVector3: LLVector3) {
        this.x = lLVector3.x
        this.y = lLVector3.y
        this.z = lLVector3.z
        this.w = 0.0f
    }

    constructor(lLVector4: LLVector4) {
        this.x = lLVector4.x
        this.y = lLVector4.y
        this.z = lLVector4.z
        this.w = lLVector4.w
    }

    fun add(lLVector4: LLVector4, lLVector42: LLVector4): LLVector4 {
        return LLVector4(lLVector4.x + lLVector42.x, lLVector4.y + lLVector42.y, lLVector4.z + lLVector42.z, lLVector4.w + lLVector42.w)
    }

    fun cross3(lLVector4: LLVector4, lLVector42: LLVector4): LLVector4 {
        return LLVector4((lLVector4.y * lLVector42.z) - (lLVector4.z * lLVector42.y), (lLVector4.z * lLVector42.x) - (lLVector4.x * lLVector42.z), (lLVector4.x * lLVector42.y) - (lLVector4.y * lLVector42.x), 0.0f)
    }

    fun sub(lLVector4: LLVector4, lLVector42: LLVector4): LLVector4 {
        return LLVector4(lLVector4.x - lLVector42.x, lLVector4.y - lLVector42.y, lLVector4.z - lLVector42.z, lLVector4.w - lLVector42.w)
    }

    fun add(lLVector4: LLVector4) {
        this.x += lLVector4.x
        this.y += lLVector4.y
        this.z += lLVector4.z
        this.w += lLVector4.w
    }

    fun clear() {
        this.x = 0.0f
        this.y = 0.0f
        this.z = 0.0f
        this.w = 0.0f
    }

    fun dot3(lLVector4: LLVector4): Float {
        return (this.x * lLVector4.x) + (this.y * lLVector4.y) + (this.z * lLVector4.z)
    }

    fun mul(f: Float) {
        this.x *= f
        this.y *= f
        this.z *= f
        this.w *= f
    }

    fun normalize3(): Float {
        val sqrtVal = sqrt(((this.x * this.x).toDouble() + (this.y * this.y).toDouble() + (this.z * this.z).toDouble())).toFloat()
        if (sqrtVal > FP_MAG_THRESHOLD) {
            val f = 1.0f / sqrtVal
            this.x *= f
            this.y *= f
            this.z *= f // Fixed: z = f * z is same as z *= f
        } else {
            this.x = 0.0f
            this.y = 0.0f
            this.z = 0.0f
        }
        return sqrtVal
    }

    fun set(f: Float, f2: Float, f3: Float) {
        this.x = f
        this.y = f2
        this.z = f3
        this.w = 0.0f
    }

    fun set(lLVector4: LLVector4) {
        this.x = lLVector4.x
        this.y = lLVector4.y
        this.z = lLVector4.z
        this.w = lLVector4.w
    }

    fun setMax(lLVector4: LLVector4) {
        this.x = max(this.x, lLVector4.x)
        this.y = max(this.y, lLVector4.y)
        this.z = max(this.z, lLVector4.z)
        this.w = max(this.w, lLVector4.w)
    }

    fun setMin(lLVector4: LLVector4) {
        this.x = min(this.x, lLVector4.x)
        this.y = min(this.y, lLVector4.y)
        this.z = min(this.z, lLVector4.z)
        this.w = min(this.w, lLVector4.w)
    }

    override fun toString(): String {
        return String.format("(%f, %f, %f, %f)", x, y, z, w)
    }
}
