package com.linkpoint.slproto.types

class LLVector4 {
    const val FP_MAG_THRESHOLD: Float = 1.0E-7f
    public Float w
    public Float x
    public Float y
    public Float z

    public LLVector4() {
        this.x = 0.0f
        this.y = 0.0f
        this.z = 0.0f
        this.w = 0.0f
    }

    public LLVector4(Float f, Float f2, Float f3) {
        this.x = f
        this.y = f2
        this.z = f3
        this.w = 0.0f
    }

    public LLVector4(Float f, Float f2, Float f3, Float f4) {
        this.x = f
        this.y = f2
        this.z = f3
        this.w = f4
    }

    public LLVector4(LLVector3 lLVector3) {
        this.x = lLVector3.x
        this.y = lLVector3.y
        this.z = lLVector3.z
        this.w = 0.0f
    }

    public LLVector4(LLVector4 lLVector4) {
        this.x = lLVector4.x
        this.y = lLVector4.y
        this.z = lLVector4.z
        this.w = lLVector4.w
    }

    @JvmStatic
    LLVector4 add(LLVector4 lLVector4, LLVector4 lLVector42) {
        return LLVector4(lLVector4.x + lLVector42.x, lLVector4.y + lLVector42.y, lLVector4.z + lLVector42.z, lLVector4.w + lLVector42.w)
    }

    @JvmStatic
    LLVector4 cross3(LLVector4 lLVector4, LLVector4 lLVector42) {
        return LLVector4((lLVector4.y * lLVector42.z) - (lLVector4.z * lLVector42.y), (lLVector4.z * lLVector42.x) - (lLVector4.x * lLVector42.z), (lLVector4.x * lLVector42.y) - (lLVector4.y * lLVector42.x), 0.0f)
    }

    @JvmStatic
    LLVector4 sub(LLVector4 lLVector4, LLVector4 lLVector42) {
        return LLVector4(lLVector4.x - lLVector42.x, lLVector4.y - lLVector42.y, lLVector4.z - lLVector42.z, lLVector4.w - lLVector42.w)
    }

    fun add(LLVector4 lLVector4) {
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

    public Float dot3(LLVector4 lLVector4) {
        return (this.x * lLVector4.x) + (this.y * lLVector4.y) + (this.z * lLVector4.z)
    }

    fun mul(Float f) {
        this.x *= f
        this.y *= f
        this.z *= f
        this.w *= f
    }

    public Float normalize3() {
        Float sqrt = (Float) Math.sqrt((Double) ((this.x * this.x) + (this.y * this.y) + (this.z * this.z)))
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

    fun set(Float f, Float f2, Float f3) {
        this.x = f
        this.y = f2
        this.z = f3
        this.w = 0.0f
    }

    fun set(LLVector4 lLVector4) {
        this.x = lLVector4.x
        this.y = lLVector4.y
        this.z = lLVector4.z
        this.w = lLVector4.w
    }

    fun setMax(LLVector4 lLVector4) {
        this.x = Math.max(this.x, lLVector4.x)
        this.y = Math.max(this.y, lLVector4.y)
        this.z = Math.max(this.z, lLVector4.z)
        this.w = Math.max(this.w, lLVector4.w)
    }

    fun setMin(LLVector4 lLVector4) {
        this.x = Math.min(this.x, lLVector4.x)
        this.y = Math.min(this.y, lLVector4.y)
        this.z = Math.min(this.z, lLVector4.z)
        this.w = Math.min(this.w, lLVector4.w)
    }

    public String toString() {
        return String.format("(%f, %f, %f)", Array<Any>{Float.valueOf(this.x), Float.valueOf(this.y), Float.valueOf(this.z)})
    }
}
