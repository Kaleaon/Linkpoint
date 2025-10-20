package com.linkpoint.slproto.types

class LLVector2 {
    const val FP_MAG_THRESHOLD: Float = 1.0E-7f
    public Float x
    public Float y

    public LLVector2() {
        this.x = 0.0f
        this.y = 0.0f
    }

    public LLVector2(Float f, Float f2) {
        this.x = f
        this.y = f2
    }

    public LLVector2(LLVector2 lLVector2) {
        this.x = lLVector2.x
        this.y = lLVector2.y
    }

    @JvmStatic
    LLVector2 sub(LLVector2 lLVector2, LLVector2 lLVector22) {
        return LLVector2(lLVector2.x - lLVector22.x, lLVector2.y - lLVector22.y)
    }

    @JvmStatic
    LLVector2 sum(LLVector2 lLVector2, LLVector2 lLVector22) {
        return LLVector2(lLVector2.x + lLVector22.x, lLVector2.y + lLVector22.y)
    }

    fun add(LLVector2 lLVector2) {
        this.x += lLVector2.x
        this.y += lLVector2.y
    }

    public Float dot(LLVector2 lLVector2) {
        return (this.x * lLVector2.x) + (this.y * lLVector2.y)
    }

    public Boolean equals(Object obj) {
        if (obj == this) {
            return true
        }
        if (!(obj instanceof LLVector2)) {
            return false
        }
        LLVector2 lLVector2 = (LLVector2) obj
        return this.x == lLVector2.x && this.y == lLVector2.y
    }

    public Int hashCode() {
        return Float.floatToIntBits(this.x) + Float.floatToIntBits(this.y)
    }

    public Float magVec() {
        return (Float) Math.sqrt((Double) ((this.x * this.x) + (this.y * this.y)))
    }

    fun mul(Float f) {
        this.x *= f
        this.y *= f
    }

    public Float normVec() {
        Float sqrt = (Float) Math.sqrt((Double) ((this.x * this.x) + (this.y * this.y)))
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

    fun set(Float f, Float f2) {
        this.x = f
        this.y = f2
    }

    fun setMax(LLVector2 lLVector2) {
        this.x = Math.max(this.x, lLVector2.x)
        this.y = Math.max(this.y, lLVector2.y)
    }

    fun setMin(LLVector2 lLVector2) {
        this.x = Math.min(this.x, lLVector2.x)
        this.y = Math.min(this.y, lLVector2.y)
    }

    public String toString() {
        return String.format("(%f, %f)", Object[]{Float.valueOf(this.x), Float.valueOf(this.y)})
    }
}
