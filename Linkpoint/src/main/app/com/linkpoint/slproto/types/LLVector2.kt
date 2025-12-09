package com.linkpoint.slproto.types

class LLVector2 {
    val FP_MAG_THRESHOLD: Float = 1.0E-7f
    Float x
    Float y

    LLVector2() {
        this.x = 0.0f
        this.y = 0.0f
    }

    LLVector2(Float f, Float f2) {
        this.x = f
        this.y = f2
    }

    LLVector2(LLVector2 lLVector2) {
        this.x = lLVector2.x
        this.y = lLVector2.y
    }

    fun sub(LLVector2 lLVector2, LLVector2 lLVector22): LLVector2 {
        return LLVector2(lLVector2.x - lLVector22.x, lLVector2.y - lLVector22.y)
    }

    fun sum(LLVector2 lLVector2, LLVector2 lLVector22): LLVector2 {
        return LLVector2(lLVector2.x + lLVector22.x, lLVector2.y + lLVector22.y)
    }

    fun add(LLVector2 lLVector2): Unit {
        this.x += lLVector2.x
        this.y += lLVector2.y
    }

    fun dot(LLVector2 lLVector2): Float {
        return (this.x * lLVector2.x) + (this.y * lLVector2.y)
    }

    fun equals(Any obj): Boolean {
        if (obj == this) {
            return true
        }
        if (!(obj instanceof LLVector2)) {
            return false
        }
        LLVector2 lLVector2 = (LLVector2) obj
        return this.x == lLVector2.x && this.y == lLVector2.y
    }

    fun hashCode(): Int {
        return Float.floatToIntBits(this.x) + Float.floatToIntBits(this.y)
    }

    fun magVec(): Float {
        return Math.sqrt(((this.x * this.x.toDouble()).toFloat() + (this.y * this.y)))
    }

    fun mul(Float f): Unit {
        this.x *= f
        this.y *= f
    }

    fun normVec(): Float {
        Float sqrt = Math.sqrt(((this.x * this.x.toDouble()).toFloat() + (this.y * this.y)))
        if (sqrt > 1.0E-7f) {
            Float f = 1.0f / sqrt
            this.x *= f
            this.y = f * this.y
        } else {
            this.x = 0.0f
            this.y = 0.0f
        }
        return sqrt
    }

    fun set(Float f, Float f2): Unit {
        this.x = f
        this.y = f2
    }

    fun setMax(LLVector2 lLVector2): Unit {
        this.x = Math.max(this.x, lLVector2.x)
        this.y = Math.max(this.y, lLVector2.y)
    }

    fun setMin(LLVector2 lLVector2): Unit {
        this.x = Math.min(this.x, lLVector2.x)
        this.y = Math.min(this.y, lLVector2.y)
    }

    fun toString(): String {
        return String.format("(%f, %f)", Any[]{Float.valueOf(this.x), Float.valueOf(this.y)})
    }
}
