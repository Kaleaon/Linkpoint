// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.types;

public class LLVector2
{
    public static final float FP_MAG_THRESHOLD = 1.0E-7f;
    public float x;
    public float y;
    
    public LLVector2() {
        this.x = 0.0f;
        this.y = 0.0f;
    }
    
    public LLVector2(final float x, final float y) {
        this.x = x;
        this.y = y;
    }
    
    public LLVector2(final LLVector2 llVector2) {
        this.x = llVector2.x;
        this.y = llVector2.y;
    }
    
    public static LLVector2 sub(final LLVector2 llVector2, final LLVector2 llVector3) {
        return new LLVector2(llVector2.x - llVector3.x, llVector2.y - llVector3.y);
    }
    
    public static LLVector2 sum(final LLVector2 llVector2, final LLVector2 llVector3) {
        return new LLVector2(llVector2.x + llVector3.x, llVector2.y + llVector3.y);
    }
    
    public void add(final LLVector2 llVector2) {
        this.x += llVector2.x;
        this.y += llVector2.y;
    }
    
    public float dot(final LLVector2 llVector2) {
        return this.x * llVector2.x + this.y * llVector2.y;
    }
    
    @Override
    public boolean equals(final Object o) {
        boolean b = true;
        if (o == this) {
            return true;
        }
        if (!(o instanceof LLVector2)) {
            return false;
        }
        final LLVector2 llVector2 = (LLVector2)o;
        if (this.x != llVector2.x || this.y != llVector2.y) {
            b = false;
        }
        return b;
    }
    
    @Override
    public int hashCode() {
        return Float.floatToIntBits(this.x) + Float.floatToIntBits(this.y);
    }
    
    public float magVec() {
        return (float)Math.sqrt(this.x * this.x + this.y * this.y);
    }
    
    public void mul(final float n) {
        this.x *= n;
        this.y *= n;
    }
    
    public float normVec() {
        final float n = (float)Math.sqrt(this.x * this.x + this.y * this.y);
        if (n > 1.0E-7f) {
            final float n2 = 1.0f / n;
            this.x *= n2;
            this.y *= n2;
        }
        else {
            this.x = 0.0f;
            this.y = 0.0f;
        }
        return n;
    }
    
    public void set(final float x, final float y) {
        this.x = x;
        this.y = y;
    }
    
    public void setMax(final LLVector2 llVector2) {
        this.x = Math.max(this.x, llVector2.x);
        this.y = Math.max(this.y, llVector2.y);
    }
    
    public void setMin(final LLVector2 llVector2) {
        this.x = Math.min(this.x, llVector2.x);
        this.y = Math.min(this.y, llVector2.y);
    }
    
    @Override
    public String toString() {
        return String.format("(%f, %f)", this.x, this.y);
    }
}
