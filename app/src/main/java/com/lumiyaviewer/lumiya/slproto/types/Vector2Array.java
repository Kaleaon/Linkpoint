package com.lumiyaviewer.lumiya.slproto.types;

public class Vector2Array extends VectorArray {
    public Vector2Array(int i) {
        super(2, i);
    }

    public Vector2Array(VectorArray vectorArray, int i) {
        super(vectorArray, i);
    }

    public final void add(int i, LLVector2 lLVector2) {
        int i2 = this.offset + (this.numComponents * i);
        float[] fArr = this.data;
        int i3 = i2 + 0;
        fArr[i3] = fArr[i3] + lLVector2.x;
        float[] fArr2 = this.data;
        int i4 = i2 + 1;
        fArr2[i4] = fArr2[i4] + lLVector2.y;
    }

    public final void addToVector(int i, LLVector2 lLVector2) {
        int i2 = this.offset + (this.numComponents * i);
        lLVector2.x += this.data[i2 + 0];
        lLVector2.y = this.data[i2 + 1] + lLVector2.y;
    }

    public final void get(int i, LLVector2 lLVector2) {
        int i2 = this.offset + (this.numComponents * i);
        lLVector2.x = this.data[i2 + 0];
        lLVector2.y = this.data[i2 + 1];
    }

    public final void getSub(int i, Vector2Array vector2Array, int i2, LLVector2 lLVector2) {
        int i3 = this.offset + (this.numComponents * i);
        int i4 = vector2Array.offset + (vector2Array.numComponents * i2);
        lLVector2.x = this.data[i3 + 0] - vector2Array.data[i4 + 0];
        lLVector2.y = this.data[i3 + 1] - vector2Array.data[i4 + 1];
    }

    public final void minMaxVector(int i, LLVector2 lLVector2, LLVector2 lLVector22) {
        int i2 = this.offset + (this.numComponents * i);
        float f = this.data[i2 + 0];
        float f2 = this.data[i2 + 1];
        if (lLVector2.x > f) {
            lLVector2.x = f;
        }
        if (lLVector22.x < f) {
            lLVector22.x = f;
        }
        if (lLVector2.y > f2) {
            lLVector2.y = f2;
        }
        if (lLVector22.y < f2) {
            lLVector22.y = f2;
        }
    }

    public final void minMaxVector(LLVector2 lLVector2, LLVector2 lLVector22) {
        int i = this.offset;
        for (int i2 = 0; i2 < this.length; i2++) {
            float f = this.data[i + 0];
            float f2 = this.data[i + 1];
            if (lLVector2.x > f) {
                lLVector2.x = f;
            }
            if (lLVector22.x < f) {
                lLVector22.x = f;
            }
            if (lLVector2.y > f2) {
                lLVector2.y = f2;
            }
            if (lLVector22.y < f2) {
                lLVector22.y = f2;
            }
            i += this.numComponents;
        }
    }

    public final void set(int i, float f, float f2) {
        int i2 = this.offset + (this.numComponents * i);
        this.data[i2 + 0] = f;
        this.data[i2 + 1] = f2;
    }

    public void swap(int i, int i2) {
        int i3 = (this.numComponents * i) + this.offset;
        int i4 = (this.numComponents * i2) + this.offset;
        for (int i5 = 0; i5 < 2; i5++) {
            float f = this.data[i3 + i5];
            this.data[i3 + i5] = this.data[i4 + i5];
            this.data[i4 + i5] = f;
        }
    }
}
