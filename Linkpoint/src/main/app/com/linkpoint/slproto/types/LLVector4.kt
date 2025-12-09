package com.linkpoint.slproto.types

class LLVector4 {
    val FP_MAG_THRESHOLD: Float = 1.0E-7f
    Float w
    Float x
    Float y
    Float z

    LLVector4() {
        this.x = 0.0f
        this.y = 0.0f
        this.z = 0.0f
        this.w = 0.0f
    }

    LLVector4(Float f, Float f2, Float f3) {
        this.x = f
        this.y = f2
        this.z = f3
        this.w = 0.0f
    }

    LLVector4(Float f, Float f2, Float f3, Float f4) {
        this.x = f
        this.y = f2
        this.z = f3
        this.w = f4
    }

    LLVector4(LLVector3 lLVector3) {
        this.x = lLVector3.x
        this.y = lLVector3.y
        this.z = lLVector3.z
        this.w = 0.0f
    }

    LLVector4(LLVector4 lLVector4) {
        this.x = lLVector4.x
        this.y = lLVector4.y
        this.z = lLVector4.z
        this.w = lLVector4.w
    }

    fun add(LLVector4 lLVector4, LLVector4 lLVector42): LLVector4 {
        return LLVector4(lLVector4.x + lLVector42.x, lLVector4.y + lLVector42.y, lLVector4.z + lLVector42.z, lLVector4.w + lLVector42.w)
    }

    fun cross3(LLVector4 lLVector4, LLVector4 lLVector42): LLVector4 {
        return LLVector4((lLVector4.y * lLVector42.z) - (lLVector4.z * lLVector42.y), (lLVector4.z * lLVector42.x) - (lLVector4.x * lLVector42.z), (lLVector4.x * lLVector42.y) - (lLVector4.y * lLVector42.x), 0.0f)
    }

    fun sub(LLVector4 lLVector4, LLVector4 lLVector42): LLVector4 {
        return LLVector4(lLVector4.x - lLVector42.x, lLVector4.y - lLVector42.y, lLVector4.z - lLVector42.z, lLVector4.w - lLVector42.w)
    }

    fun add(LLVector4 lLVector4): Unit {
        this.x += lLVector4.x
        this.y += lLVector4.y
        this.z += lLVector4.z
        this.w += lLVector4.w
    }

    fun clear(): Unit {
        this.x = 0.0f
        this.y = 0.0f
        this.z = 0.0f
        this.w = 0.0f
    }

    fun dot3(LLVector4 lLVector4): Float {
        return (this.x * lLVector4.x) + (this.y * lLVector4.y) + (this.z * lLVector4.z)
    }

    fun mul(Float f): Unit {
        this.x *= f
        this.y *= f
        this.z *= f
        this.w *= f
    }

    fun normalize3(): Float {
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
        this.w = 0.0f
    }

    fun set(LLVector4 lLVector4): Unit {
        this.x = lLVector4.x
        this.y = lLVector4.y
        this.z = lLVector4.z
        this.w = lLVector4.w
    }

    fun setMax(LLVector4 lLVector4): Unit {
        this.x = Math.max(this.x, lLVector4.x)
        this.y = Math.max(this.y, lLVector4.y)
        this.z = Math.max(this.z, lLVector4.z)
        this.w = Math.max(this.w, lLVector4.w)
    }

    fun setMin(LLVector4 lLVector4): Unit {
        this.x = Math.min(this.x, lLVector4.x)
        this.y = Math.min(this.y, lLVector4.y)
        this.z = Math.min(this.z, lLVector4.z)
        this.w = Math.min(this.w, lLVector4.w)
    }

    fun toString(): String {
        return String.format("(%f, %f, %f)", Any[]{Float.valueOf(this.x), Float.valueOf(this.y), Float.valueOf(this.z)})
    }
}
