// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.types;

public class LLVector4
{
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
    
    public LLVector4(final float x, final float y, final float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = 0.0f;
    }
    
    public LLVector4(final float x, final float y, final float z, final float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }
    
    public LLVector4(final LLVector3 llVector3) {
        this.x = llVector3.x;
        this.y = llVector3.y;
        this.z = llVector3.z;
        this.w = 0.0f;
    }
    
    public LLVector4(final LLVector4 llVector4) {
        this.x = llVector4.x;
        this.y = llVector4.y;
        this.z = llVector4.z;
        this.w = llVector4.w;
    }
    
    public static LLVector4 add(final LLVector4 llVector4, final LLVector4 llVector5) {
        return new LLVector4(llVector4.x + llVector5.x, llVector4.y + llVector5.y, llVector4.z + llVector5.z, llVector4.w + llVector5.w);
    }
    
    public static LLVector4 cross3(final LLVector4 llVector4, final LLVector4 llVector5) {
        return new LLVector4(llVector4.y * llVector5.z - llVector4.z * llVector5.y, llVector4.z * llVector5.x - llVector4.x * llVector5.z, llVector4.x * llVector5.y - llVector4.y * llVector5.x, 0.0f);
    }
    
    public static LLVector4 sub(final LLVector4 llVector4, final LLVector4 llVector5) {
        return new LLVector4(llVector4.x - llVector5.x, llVector4.y - llVector5.y, llVector4.z - llVector5.z, llVector4.w - llVector5.w);
    }
    
    public void add(final LLVector4 llVector4) {
        this.x += llVector4.x;
        this.y += llVector4.y;
        this.z += llVector4.z;
        this.w += llVector4.w;
    }
    
    public void clear() {
        this.x = 0.0f;
        this.y = 0.0f;
        this.z = 0.0f;
        this.w = 0.0f;
    }
    
    public float dot3(final LLVector4 llVector4) {
        return this.x * llVector4.x + this.y * llVector4.y + this.z * llVector4.z;
    }
    
    public void mul(final float n) {
        this.x *= n;
        this.y *= n;
        this.z *= n;
        this.w *= n;
    }
    
    public float normalize3() {
        final float n = (float)Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
        if (n > 1.0E-7f) {
            final float n2 = 1.0f / n;
            this.x *= n2;
            this.y *= n2;
            this.z *= n2;
        }
        else {
            this.x = 0.0f;
            this.y = 0.0f;
            this.z = 0.0f;
        }
        return n;
    }
    
    public void set(final float x, final float y, final float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = 0.0f;
    }
    
    public void set(final LLVector4 llVector4) {
        this.x = llVector4.x;
        this.y = llVector4.y;
        this.z = llVector4.z;
        this.w = llVector4.w;
    }
    
    public void setMax(final LLVector4 llVector4) {
        this.x = Math.max(this.x, llVector4.x);
        this.y = Math.max(this.y, llVector4.y);
        this.z = Math.max(this.z, llVector4.z);
        this.w = Math.max(this.w, llVector4.w);
    }
    
    public void setMin(final LLVector4 llVector4) {
        this.x = Math.min(this.x, llVector4.x);
        this.y = Math.min(this.y, llVector4.y);
        this.z = Math.min(this.z, llVector4.z);
        this.w = Math.min(this.w, llVector4.w);
    }
    
    @Override
    public String toString() {
        return String.format("(%f, %f, %f)", this.x, this.y, this.z);
    }
}
