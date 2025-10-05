package com.linkpoint.slproto.types

class Vector2Array : VectorArray() {
    public Vector2Array(Int i) {
        super(2, i)
    }

    public Vector2Array(VectorArray vectorArray, Int i) {
        super(vectorArray, i)
    }

    val Unit add(Int i, LLVector2 lLVector2) {
        Int i2 = this.offset + (this.numComponents * i)
        Float[] fArr = this.data
        Int i3 = i2 + 0
        fArr[i3] = fArr[i3] + lLVector2.x
        Float[] fArr2 = this.data
        Int i4 = i2 + 1
        fArr2[i4] = fArr2[i4] + lLVector2.y
    }

    val Unit addToVector(Int i, LLVector2 lLVector2) {
        Int i2 = this.offset + (this.numComponents * i)
        lLVector2.x += this.data[i2 + 0]
        lLVector2.y = this.data[i2 + 1] + lLVector2.y
    }

    val Unit get(Int i, LLVector2 lLVector2) {
        Int i2 = this.offset + (this.numComponents * i)
        lLVector2.x = this.data[i2 + 0]
        lLVector2.y = this.data[i2 + 1]
    }

    val Unit getSub(Int i, Vector2Array vector2Array, Int i2, LLVector2 lLVector2) {
        Int i3 = this.offset + (this.numComponents * i)
        Int i4 = vector2Array.offset + (vector2Array.numComponents * i2)
        lLVector2.x = this.data[i3 + 0] - vector2Array.data[i4 + 0]
        lLVector2.y = this.data[i3 + 1] - vector2Array.data[i4 + 1]
    }

    val Unit minMaxVector(Int i, LLVector2 lLVector2, LLVector2 lLVector22) {
        Int i2 = this.offset + (this.numComponents * i)
        Float f = this.data[i2 + 0]
        Float f2 = this.data[i2 + 1]
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

    val Unit minMaxVector(LLVector2 lLVector2, LLVector2 lLVector22) {
        Int i = this.offset
        for (Int i2 = 0; i2 < this.length; i2++) {
            Float f = this.data[i + 0]
            Float f2 = this.data[i + 1]
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

    val Unit set(Int i, Float f, Float f2) {
        Int i2 = this.offset + (this.numComponents * i)
        this.data[i2 + 0] = f
        this.data[i2 + 1] = f2
    }

    public Unit swap(Int i, Int i2) {
        Int i3 = (this.numComponents * i) + this.offset
        Int i4 = (this.numComponents * i2) + this.offset
        for (Int i5 = 0; i5 < 2; i5++) {
            Float f = this.data[i3 + i5]
            this.data[i3 + i5] = this.data[i4 + i5]
            this.data[i4 + i5] = f
        }
    }
}
