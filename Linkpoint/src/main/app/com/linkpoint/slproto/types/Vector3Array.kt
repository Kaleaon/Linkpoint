package com.linkpoint.slproto.types

import android.opengl.Matrix

class Vector3Array : VectorArray {
    Vector3Array(Int i) {
        super(3, i)
    }

    Vector3Array(VectorArray vectorArray, Int i) {
        super(vectorArray, i)
    }

    Unit MatrixScale(FloatArray fArr, Int i, Int i2) {
        Int i3 = this.offset + (this.numComponents * i2)
        Matrix.scaleM(fArr, i, this.data[i3 + 0], this.data[i3 + 1], this.data[i3 + 2])
    }

    Unit MatrixTranslate(FloatArray fArr, Int i, FloatArray fArr2, Int i2, Int i3) {
        Int i4 = this.offset + (this.numComponents * i3)
        Matrix.translateM(fArr, i, fArr2, i2, this.data[i4 + 0], this.data[i4 + 1], this.data[i4 + 2])
    }

    Unit add(Int i, LLVector3 lLVector3) {
        Int i2 = this.offset + (this.numComponents * i)
        FloatArray fArr = this.data
        Int i3 = i2 + 0
        fArr[i3] = fArr[i3] + lLVector3.x
        FloatArray fArr2 = this.data
        Int i4 = i2 + 1
        fArr2[i4] = fArr2[i4] + lLVector3.y
        FloatArray fArr3 = this.data
        Int i5 = i2 + 2
        fArr3[i5] = fArr3[i5] + lLVector3.z
    }

    Unit addToVector(Int i, LLVector3 lLVector3) {
        Int i2 = this.offset + (this.numComponents * i)
        lLVector3.x += this.data[i2 + 0]
        lLVector3.y += this.data[i2 + 1]
        lLVector3.z = this.data[i2 + 2] + lLVector3.z
    }

    Unit clear() {
        Int i = this.offset
        for (Int i2 = 0; i2 < this.length; i2++) {
            this.data[i + 0] = 0.0f
            this.data[i + 1] = 0.0f
            this.data[i + 2] = 0.0f
            i += this.numComponents
        }
    }

    Float distToPlane(Int i, LLVector3 lLVector3, LLVector3 lLVector32) {
        Int i2 = this.offset + (this.numComponents * i)
        Float f = this.data[i2 + 0] - lLVector3.x
        Float f2 = this.data[i2 + 1] - lLVector3.y
        Float f3 = this.data[i2 + 2] - lLVector3.z
        return (f3 * lLVector32.z) + (f * lLVector32.x) + (f2 * lLVector32.y)
    }

    Unit fill(Int i, Int i2, LLVector3 lLVector3) {
        Int i3 = (this.numComponents * i) + this.offset
        for (Int i4 = 0; i4 < i2; i4++) {
            this.data[i3 + 0] = lLVector3.x
            this.data[i3 + 1] = lLVector3.y
            this.data[i3 + 2] = lLVector3.z
            i3 += this.numComponents
        }
    }

    LLVector3 get(Int i) {
        Int i2 = this.offset + (this.numComponents * i)
        return LLVector3(this.data[i2 + 0], this.data[i2 + 1], this.data[i2 + 2])
    }

    Unit get(Int i, LLVector3 lLVector3) {
        Int i2 = this.offset + (this.numComponents * i)
        lLVector3.x = this.data[i2 + 0]
        lLVector3.y = this.data[i2 + 1]
        lLVector3.z = this.data[i2 + 2]
    }

    Float getDistanceTo(Int i, LLVector3 lLVector3) {
        Int i2 = this.offset + (this.numComponents * i)
        Float f = this.data[i2 + 0] - lLVector3.x
        Float f2 = this.data[i2 + 1] - lLVector3.y
        Float f3 = this.data[i2 + 2] - lLVector3.z
        return Math.sqrt(((f3 * f3.toDouble()).toFloat() + (f * f) + (f2 * f2)))
    }

    Float getMaxComponent(Int i) {
        Int i2 = (this.numComponents * i) + this.offset
        Float f = this.data[i2 + 0]
        if (this.data[i2 + 1] > f) {
            f = this.data[i2 + 1]
        }
        return this.data[i2 + 2] > f ? this.data[i2 + 2] : f
    }

    Unit getSub(Int i, Int i2, LLVector3 lLVector3) {
        Int i3 = this.offset + (this.numComponents * i)
        Int i4 = this.offset + (this.numComponents * i2)
        lLVector3.x = this.data[i3 + 0] - this.data[i4 + 0]
        lLVector3.y = this.data[i3 + 1] - this.data[i4 + 1]
        lLVector3.z = this.data[i3 + 2] - this.data[i4 + 2]
    }

    Unit getSub(Int i, Vector3Array vector3Array, Int i2, LLVector3 lLVector3) {
        Int i3 = this.offset + (this.numComponents * i)
        Int i4 = vector3Array.offset + (vector3Array.numComponents * i2)
        lLVector3.x = this.data[i3 + 0] - vector3Array.data[i4 + 0]
        lLVector3.y = this.data[i3 + 1] - vector3Array.data[i4 + 1]
        lLVector3.z = this.data[i3 + 2] - vector3Array.data[i4 + 2]
    }

    Unit minMaxVector(Int i, LLVector3 lLVector3, LLVector3 lLVector32) {
        Int i2 = this.offset + (this.numComponents * i)
        Float f = this.data[i2 + 0]
        Float f2 = this.data[i2 + 1]
        Float f3 = this.data[i2 + 2]
        if (lLVector3.x > f) {
            lLVector3.x = f
        }
        if (lLVector32.x < f) {
            lLVector32.x = f
        }
        if (lLVector3.y > f2) {
            lLVector3.y = f2
        }
        if (lLVector32.y < f2) {
            lLVector32.y = f2
        }
        if (lLVector3.z > f3) {
            lLVector3.z = f3
        }
        if (lLVector32.z < f3) {
            lLVector32.z = f3
        }
    }

    Unit minMaxVector(LLVector3 lLVector3, LLVector3 lLVector32) {
        Int i = this.offset
        for (Int i2 = 0; i2 < this.length; i2++) {
            Float f = this.data[i + 0]
            Float f2 = this.data[i + 1]
            Float f3 = this.data[i + 2]
            if (lLVector3.x > f) {
                lLVector3.x = f
            }
            if (lLVector32.x < f) {
                lLVector32.x = f
            }
            if (lLVector3.y > f2) {
                lLVector3.y = f2
            }
            if (lLVector32.y < f2) {
                lLVector32.y = f2
            }
            if (lLVector3.z > f3) {
                lLVector3.z = f3
            }
            if (lLVector32.z < f3) {
                lLVector32.z = f3
            }
            i += this.numComponents
        }
    }

    Unit mul(Int i, LLQuaternion lLQuaternion) {
        Int i2 = this.offset + (this.numComponents * i)
        Float f = this.data[i2 + 0]
        Float f2 = this.data[i2 + 1]
        Float f3 = this.data[i2 + 2]
        Float f4 = (((-lLQuaternion.x) * f) - (lLQuaternion.y * f2)) - (lLQuaternion.z * f3)
        Float f5 = ((lLQuaternion.w * f) + (lLQuaternion.y * f3)) - (lLQuaternion.z * f2)
        Float f6 = ((lLQuaternion.w * f2) + (lLQuaternion.z * f)) - (lLQuaternion.x * f3)
        Float f7 = ((f2 * lLQuaternion.x) + (f3 * lLQuaternion.w)) - (f * lLQuaternion.y)
        this.data[i2 + 0] = ((((-f4) * lLQuaternion.x) + (lLQuaternion.w * f5)) - (lLQuaternion.z * f6)) + (lLQuaternion.y * f7)
        this.data[i2 + 1] = ((((-f4) * lLQuaternion.y) + (lLQuaternion.w * f6)) - (lLQuaternion.x * f7)) + (lLQuaternion.z * f5)
        this.data[i2 + 2] = (((f7 * lLQuaternion.w) + ((-f4) * lLQuaternion.z)) - (lLQuaternion.y * f5)) + (lLQuaternion.x * f6)
    }

    Unit set(Int i, Float f, Float f2, Float f3) {
        Int i2 = this.offset + (this.numComponents * i)
        this.data[i2 + 0] = f
        this.data[i2 + 1] = f2
        this.data[i2 + 2] = f3
    }

    Unit set(Int i, LLVector3 lLVector3) {
        Int i2 = this.offset + (this.numComponents * i)
        this.data[i2 + 0] = lLVector3.x
        this.data[i2 + 1] = lLVector3.y
        this.data[i2 + 2] = lLVector3.z
    }

    Unit set(Int i, Vector3Array vector3Array, Int i2) {
        Int i3 = this.offset + (this.numComponents * i)
        Int i4 = vector3Array.offset + (vector3Array.numComponents * i2)
        this.data[i3 + 0] = vector3Array.data[i4 + 0]
        this.data[i3 + 1] = vector3Array.data[i4 + 1]
        this.data[i3 + 2] = vector3Array.data[i4 + 2]
    }

    Unit setAdd(Int i, Int i2) {
        Int i3 = (this.numComponents * i) + this.offset
        Int i4 = (this.numComponents * i2) + this.offset
        for (Int i5 = 0; i5 < 3; i5++) {
            FloatArray fArr = this.data
            Int i6 = i3 + i5
            fArr[i6] = fArr[i6] + this.data[i4 + i5]
            this.data[i4 + i5] = this.data[i3 + i5]
        }
    }

    Unit subFromVector(LLVector3 lLVector3, Int i) {
        Int i2 = this.offset + (this.numComponents * i)
        lLVector3.x -= this.data[i2 + 0]
        lLVector3.y -= this.data[i2 + 1]
        lLVector3.z -= this.data[i2 + 2]
    }
}
