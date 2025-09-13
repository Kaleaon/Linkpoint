package com.lumiyaviewer.lumiya.slproto.types;

public class LLVector4 {
    public static final float FP_MAG_THRESHOLD = 1.0E-7f;
    public float w;
    public float x;
    public float y;
    public float z;

    public LLVector4() {
        this.x = 0.0f;
        this.y = 0.0f;
        this.z = 0.0f;
        this.w = 0.0f;
    }

    public LLVector4(float f, float f2, float f3) {
        this.x = f;
        this.y = f2;
        this.z = f3;
        this.w = 0.0f;
    }

    public LLVector4(float f, float f2, float f3, float f4) {
        this.x = f;
        this.y = f2;
        this.z = f3;
        this.w = f4;
    }

    public LLVector4(LLVector3 lLVector3) {
        this.x = lLVector3.x;
        this.y = lLVector3.y;
        this.z = lLVector3.z;
        this.w = 0.0f;
    }

    public LLVector4(LLVector4 lLVector4) {
        this.x = lLVector4.x;
        this.y = lLVector4.y;
        this.z = lLVector4.z;
        this.w = lLVector4.w;
    }

    public static LLVector4 add(LLVector4 lLVector4, LLVector4 lLVector42) {
        return new LLVector4(lLVector4.x + lLVector42.x, lLVector4.y + lLVector42.y, lLVector4.z + lLVector42.z, lLVector4.w + lLVector42.w);
    }

    public static LLVector4 cross3(LLVector4 lLVector4, LLVector4 lLVector42) {
        return new LLVector4((lLVector4.y * lLVector42.z) - (lLVector4.z * lLVector42.y), (lLVector4.z * lLVector42.x) - (lLVector4.x * lLVector42.z), (lLVector4.x * lLVector42.y) - (lLVector4.y * lLVector42.x), 0.0f);
    }

    public static LLVector4 sub(LLVector4 lLVector4, LLVector4 lLVector42) {
        return new LLVector4(lLVector4.x - lLVector42.x, lLVector4.y - lLVector42.y, lLVector4.z - lLVector42.z, lLVector4.w - lLVector42.w);
    }

    public void add(LLVector4 lLVector4) {
        this.x += lLVector4.x;
        this.y += lLVector4.y;
        this.z += lLVector4.z;
        this.w += lLVector4.w;
    }

    public void clear() {
        this.x = 0.0f;
        this.y = 0.0f;
        this.z = 0.0f;
        this.w = 0.0f;
    }

    public float dot3(LLVector4 lLVector4) {
        return (this.x * lLVector4.x) + (this.y * lLVector4.y) + (this.z * lLVector4.z);
    }

    public void mul(float f) {
        this.x *= f;
        this.y *= f;
        this.z *= f;
        this.w *= f;
    }

    public float normalize3() {
        float sqrt = (float) Math.sqrt((double) ((this.x * this.x) + (this.y * this.y) + (this.z * this.z)));
        if (sqrt > 1.0E-7f) {
            float f = 1.0f / sqrt;
            this.x *= f;
            this.y *= f;
            this.z = f * this.z;
        } else {
            this.x = 0.0f;
            this.y = 0.0f;
            this.z = 0.0f;
        }
        return sqrt;
    }

    public void set(float f, float f2, float f3) {
        this.x = f;
        this.y = f2;
        this.z = f3;
        this.w = 0.0f;
    }

    public void set(LLVector4 lLVector4) {
        this.x = lLVector4.x;
        this.y = lLVector4.y;
        this.z = lLVector4.z;
        this.w = lLVector4.w;
    }

    public void setMax(LLVector4 lLVector4) {
        this.x = Math.max(this.x, lLVector4.x);
        this.y = Math.max(this.y, lLVector4.y);
        this.z = Math.max(this.z, lLVector4.z);
        this.w = Math.max(this.w, lLVector4.w);
    }

    public void setMin(LLVector4 lLVector4) {
        this.x = Math.min(this.x, lLVector4.x);
        this.y = Math.min(this.y, lLVector4.y);
        this.z = Math.min(this.z, lLVector4.z);
        this.w = Math.min(this.w, lLVector4.w);
    }

    public String toString() {
        return String.format("(%f, %f, %f)", new Object[]{Float.valueOf(this.x), Float.valueOf(this.y), Float.valueOf(this.z)});
    }
}
