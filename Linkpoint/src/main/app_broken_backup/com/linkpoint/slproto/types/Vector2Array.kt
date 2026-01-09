package com.linkpoint.slproto.types

class Vector2Array : VectorArray {
    Vector2Array(Int i) {
        super(2, i)
    }

    Vector2Array(VectorArray vectorArray, Int i) {
        super(vectorArray, i)
    }

    fun add(Int i, LLVector2 lLVector2)  {
        var i2: Int = this.offset + (this.numComponents * i)
        FloatArray fArr = this.data
        var i3: Int = i2 + 0
        fArr[i3] = fArr[i3] + lLVector2.x
        FloatArray fArr2 = this.data
        var i4: Int = i2 + 1
        fArr2[i4] = fArr2[i4] + lLVector2.y
    }

    fun addToVector(Int i, LLVector2 lLVector2)  {
        var i2: Int = this.offset + (this.numComponents * i)
        lLVector2.x += this.data[i2 + 0]
        lLVector2.y = this.data[i2 + 1] + lLVector2.y
    }

    fun get(Int i, LLVector2 lLVector2)  {
        var i2: Int = this.offset + (this.numComponents * i)
        lLVector2.x = this.data[i2 + 0]
        lLVector2.y = this.data[i2 + 1]
    }

    fun getSub(Int i, Vector2Array vector2Array, Int i2, LLVector2 lLVector2)  {
        var i3: Int = this.offset + (this.numComponents * i)
        var i4: Int = vector2Array.offset + (vector2Array.numComponents * i2)
        lLVector2.x = this.data[i3 + 0] - vector2Array.data[i4 + 0]
        lLVector2.y = this.data[i3 + 1] - vector2Array.data[i4 + 1]
    }

    fun minMaxVector(Int i, LLVector2 lLVector2, LLVector2 lLVector22)  {
        var i2: Int = this.offset + (this.numComponents * i)
        var f: Float = this.data[i2 + 0]
        var f2: Float = this.data[i2 + 1]
        if (lLVector2.x > f) {
            lLVector2.x = f
        }
        if (lLVector22.x < f) {
            lLVector22.x = f
        }
        if (lLVector2.y > f2) {
            lLVector2.y = f2
        }
        if (lLVector22.y < f2) {
            lLVector22.y = f2
        }
    }

    fun minMaxVector(LLVector2 lLVector2, LLVector2 lLVector22)  {
        var i: Int = this.offset
        for (i2 in 0 until this.size) {
            var f: Float = this.data[i + 0]
            var f2: Float = this.data[i + 1]
            if (lLVector2.x > f) {
                lLVector2.x = f
            }
            if (lLVector22.x < f) {
                lLVector22.x = f
            }
            if (lLVector2.y > f2) {
                lLVector2.y = f2
            }
            if (lLVector22.y < f2) {
                lLVector22.y = f2
            }
            i += this.numComponents
        }
    }

    fun set(Int i, Float f, Float f2)  {
        var i2: Int = this.offset + (this.numComponents * i)
        this.data[i2 + 0] = f
        this.data[i2 + 1] = f2
    }

    fun swap(Int i, Int i2)  {
        var i3: Int = (this.numComponents * i) + this.offset
        var i4: Int = (this.numComponents * i2) + this.offset
        for (i5 in 0 until 2) {
            var f: Float = this.data[i3 + i5]
            this.data[i3 + i5] = this.data[i4 + i5]
            this.data[i4 + i5] = f
        }
    }
}
