package com.lumiyaviewer.lumiya.slproto.types;

public class LLVector2 {
    public static final float FP_MAG_THRESHOLD = 1.0E-7f;
    public float x;
    public float y;

    public LLVector2() {
        this.x = 0.0f;
        this.y = 0.0f;
    }

    public LLVector2(float f, float f2) {
        this.x = f;
        this.y = f2;
    }

    public LLVector2(LLVector2 lLVector2) {
        this.x = lLVector2.x;
        this.y = lLVector2.y;
    }

    public static LLVector2 sub(LLVector2 lLVector2, LLVector2 lLVector22) {
        return new LLVector2(lLVector2.x - lLVector22.x, lLVector2.y - lLVector22.y);
    }

    public static LLVector2 sum(LLVector2 lLVector2, LLVector2 lLVector22) {
        return new LLVector2(lLVector2.x + lLVector22.x, lLVector2.y + lLVector22.y);
    }

    public void add(LLVector2 lLVector2) {
        this.x += lLVector2.x;
        this.y += lLVector2.y;
    }

    public float dot(LLVector2 lLVector2) {
        return (this.x * lLVector2.x) + (this.y * lLVector2.y);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof LLVector2)) {
            return false;
        }
        LLVector2 lLVector2 = (LLVector2) obj;
        return this.x == lLVector2.x && this.y == lLVector2.y;
    }

    public int hashCode() {
        return Float.floatToIntBits(this.x) + Float.floatToIntBits(this.y);
    }

    public float magVec() {
        return (float) Math.sqrt((double) ((this.x * this.x) + (this.y * this.y)));
    }

    public void mul(float f) {
        this.x *= f;
        this.y *= f;
    }

    public float normVec() {
        float sqrt = (float) Math.sqrt((double) ((this.x * this.x) + (this.y * this.y)));
        if (sqrt > 1.0E-7f) {
            float f = 1.0f / sqrt;
            this.x *= f;
            this.y = f * this.y;
        } else {
            this.x = 0.0f;
            this.y = 0.0f;
        }
        return sqrt;
    }

    public void set(float f, float f2) {
        this.x = f;
        this.y = f2;
    }

    public void setMax(LLVector2 lLVector2) {
        this.x = Math.max(this.x, lLVector2.x);
        this.y = Math.max(this.y, lLVector2.y);
    }

    public void setMin(LLVector2 lLVector2) {
        this.x = Math.min(this.x, lLVector2.x);
        this.y = Math.min(this.y, lLVector2.y);
    }

    public String toString() {
        return String.format("(%f, %f)", new Object[]{Float.valueOf(this.x), Float.valueOf(this.y)});
    }
}
