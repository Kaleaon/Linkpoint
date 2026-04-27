package com.lumiyaviewer.lumiya.slproto.types;

import android.opengl.Matrix;

public class Vector3Array extends VectorArray {
    public Vector3Array(int i) {
        super(3, i);
    }

    public Vector3Array(VectorArray vectorArray, int i) {
        super(vectorArray, i);
    }

    public final void MatrixScale(float[] fArr, int i, int i2) {
        int i3 = this.offset + (this.numComponents * i2);
        Matrix.scaleM(fArr, i, this.data[i3 + 0], this.data[i3 + 1], this.data[i3 + 2]);
    }

    public final void MatrixTranslate(float[] fArr, int i, float[] fArr2, int i2, int i3) {
        int i4 = this.offset + (this.numComponents * i3);
        Matrix.translateM(fArr, i, fArr2, i2, this.data[i4 + 0], this.data[i4 + 1], this.data[i4 + 2]);
    }

    public final void add(int i, LLVector3 lLVector3) {
        int i2 = this.offset + (this.numComponents * i);
        float[] fArr = this.data;
        int i3 = i2 + 0;
        fArr[i3] = fArr[i3] + lLVector3.x;
        float[] fArr2 = this.data;
        int i4 = i2 + 1;
        fArr2[i4] = fArr2[i4] + lLVector3.y;
        float[] fArr3 = this.data;
        int i5 = i2 + 2;
        fArr3[i5] = fArr3[i5] + lLVector3.z;
    }

    public final void addToVector(int i, LLVector3 lLVector3) {
        int i2 = this.offset + (this.numComponents * i);
        lLVector3.x += this.data[i2 + 0];
        lLVector3.y += this.data[i2 + 1];
        lLVector3.z = this.data[i2 + 2] + lLVector3.z;
    }

    public final void clear() {
        int i = this.offset;
        for (int i2 = 0; i2 < this.length; i2++) {
            this.data[i + 0] = 0.0f;
            this.data[i + 1] = 0.0f;
            this.data[i + 2] = 0.0f;
            i += this.numComponents;
        }
    }

    public final float distToPlane(int i, LLVector3 lLVector3, LLVector3 lLVector32) {
        int i2 = this.offset + (this.numComponents * i);
        float f = this.data[i2 + 0] - lLVector3.x;
        float f2 = this.data[i2 + 1] - lLVector3.y;
        float f3 = this.data[i2 + 2] - lLVector3.z;
        return (f3 * lLVector32.z) + (f * lLVector32.x) + (f2 * lLVector32.y);
    }

    public final void fill(int i, int i2, LLVector3 lLVector3) {
        int i3 = (this.numComponents * i) + this.offset;
        for (int i4 = 0; i4 < i2; i4++) {
            this.data[i3 + 0] = lLVector3.x;
            this.data[i3 + 1] = lLVector3.y;
            this.data[i3 + 2] = lLVector3.z;
            i3 += this.numComponents;
        }
    }

    public final LLVector3 get(int i) {
        int i2 = this.offset + (this.numComponents * i);
        return new LLVector3(this.data[i2 + 0], this.data[i2 + 1], this.data[i2 + 2]);
    }

    public final void get(int i, LLVector3 lLVector3) {
        int i2 = this.offset + (this.numComponents * i);
        lLVector3.x = this.data[i2 + 0];
        lLVector3.y = this.data[i2 + 1];
        lLVector3.z = this.data[i2 + 2];
    }

    public final float getDistanceTo(int i, LLVector3 lLVector3) {
        int i2 = this.offset + (this.numComponents * i);
        float f = this.data[i2 + 0] - lLVector3.x;
        float f2 = this.data[i2 + 1] - lLVector3.y;
        float f3 = this.data[i2 + 2] - lLVector3.z;
        return (float) Math.sqrt((double) ((f3 * f3) + (f * f) + (f2 * f2)));
    }

    public final float getMaxComponent(int i) {
        int i2 = (this.numComponents * i) + this.offset;
        float f = this.data[i2 + 0];
        if (this.data[i2 + 1] > f) {
            f = this.data[i2 + 1];
        }
        return this.data[i2 + 2] > f ? this.data[i2 + 2] : f;
    }

    public final void getSub(int i, int i2, LLVector3 lLVector3) {
        int i3 = this.offset + (this.numComponents * i);
        int i4 = this.offset + (this.numComponents * i2);
        lLVector3.x = this.data[i3 + 0] - this.data[i4 + 0];
        lLVector3.y = this.data[i3 + 1] - this.data[i4 + 1];
        lLVector3.z = this.data[i3 + 2] - this.data[i4 + 2];
    }

    public final void getSub(int i, Vector3Array vector3Array, int i2, LLVector3 lLVector3) {
        int i3 = this.offset + (this.numComponents * i);
        int i4 = vector3Array.offset + (vector3Array.numComponents * i2);
        lLVector3.x = this.data[i3 + 0] - vector3Array.data[i4 + 0];
        lLVector3.y = this.data[i3 + 1] - vector3Array.data[i4 + 1];
        lLVector3.z = this.data[i3 + 2] - vector3Array.data[i4 + 2];
    }

    public final void minMaxVector(int i, LLVector3 lLVector3, LLVector3 lLVector32) {
        int i2 = this.offset + (this.numComponents * i);
        float f = this.data[i2 + 0];
        float f2 = this.data[i2 + 1];
        float f3 = this.data[i2 + 2];
        if (lLVector3.x > f) {
            lLVector3.x = f;
        }
        if (lLVector32.x < f) {
            lLVector32.x = f;
        }
        if (lLVector3.y > f2) {
            lLVector3.y = f2;
        }
        if (lLVector32.y < f2) {
            lLVector32.y = f2;
        }
        if (lLVector3.z > f3) {
            lLVector3.z = f3;
        }
        if (lLVector32.z < f3) {
            lLVector32.z = f3;
        }
    }

    public final void minMaxVector(LLVector3 lLVector3, LLVector3 lLVector32) {
        int i = this.offset;
        for (int i2 = 0; i2 < this.length; i2++) {
            float f = this.data[i + 0];
            float f2 = this.data[i + 1];
            float f3 = this.data[i + 2];
            if (lLVector3.x > f) {
                lLVector3.x = f;
            }
            if (lLVector32.x < f) {
                lLVector32.x = f;
            }
            if (lLVector3.y > f2) {
                lLVector3.y = f2;
            }
            if (lLVector32.y < f2) {
                lLVector32.y = f2;
            }
            if (lLVector3.z > f3) {
                lLVector3.z = f3;
            }
            if (lLVector32.z < f3) {
                lLVector32.z = f3;
            }
            i += this.numComponents;
        }
    }

    public final void mul(int i, LLQuaternion lLQuaternion) {
        int i2 = this.offset + (this.numComponents * i);
        float f = this.data[i2 + 0];
        float f2 = this.data[i2 + 1];
        float f3 = this.data[i2 + 2];
        float f4 = (((-lLQuaternion.x) * f) - (lLQuaternion.y * f2)) - (lLQuaternion.z * f3);
        float f5 = ((lLQuaternion.w * f) + (lLQuaternion.y * f3)) - (lLQuaternion.z * f2);
        float f6 = ((lLQuaternion.w * f2) + (lLQuaternion.z * f)) - (lLQuaternion.x * f3);
        float f7 = ((f2 * lLQuaternion.x) + (f3 * lLQuaternion.w)) - (f * lLQuaternion.y);
        this.data[i2 + 0] = ((((-f4) * lLQuaternion.x) + (lLQuaternion.w * f5)) - (lLQuaternion.z * f6)) + (lLQuaternion.y * f7);
        this.data[i2 + 1] = ((((-f4) * lLQuaternion.y) + (lLQuaternion.w * f6)) - (lLQuaternion.x * f7)) + (lLQuaternion.z * f5);
        this.data[i2 + 2] = (((f7 * lLQuaternion.w) + ((-f4) * lLQuaternion.z)) - (lLQuaternion.y * f5)) + (lLQuaternion.x * f6);
    }

    public final void set(int i, float f, float f2, float f3) {
        int i2 = this.offset + (this.numComponents * i);
        this.data[i2 + 0] = f;
        this.data[i2 + 1] = f2;
        this.data[i2 + 2] = f3;
    }

    public final void set(int i, LLVector3 lLVector3) {
        int i2 = this.offset + (this.numComponents * i);
        this.data[i2 + 0] = lLVector3.x;
        this.data[i2 + 1] = lLVector3.y;
        this.data[i2 + 2] = lLVector3.z;
    }

    public final void set(int i, Vector3Array vector3Array, int i2) {
        int i3 = this.offset + (this.numComponents * i);
        int i4 = vector3Array.offset + (vector3Array.numComponents * i2);
        this.data[i3 + 0] = vector3Array.data[i4 + 0];
        this.data[i3 + 1] = vector3Array.data[i4 + 1];
        this.data[i3 + 2] = vector3Array.data[i4 + 2];
    }

    public final void setAdd(int i, int i2) {
        int i3 = (this.numComponents * i) + this.offset;
        int i4 = (this.numComponents * i2) + this.offset;
        for (int i5 = 0; i5 < 3; i5++) {
            float[] fArr = this.data;
            int i6 = i3 + i5;
            fArr[i6] = fArr[i6] + this.data[i4 + i5];
            this.data[i4 + i5] = this.data[i3 + i5];
        }
    }

    public final void subFromVector(LLVector3 lLVector3, int i) {
        int i2 = this.offset + (this.numComponents * i);
        lLVector3.x -= this.data[i2 + 0];
        lLVector3.y -= this.data[i2 + 1];
        lLVector3.z -= this.data[i2 + 2];
    }
}
