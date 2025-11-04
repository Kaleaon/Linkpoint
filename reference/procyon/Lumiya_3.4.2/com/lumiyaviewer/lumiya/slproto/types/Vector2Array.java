// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.types;

public class Vector2Array extends VectorArray
{
    public Vector2Array(final int n) {
        super(2, n);
    }
    
    public Vector2Array(final VectorArray vectorArray, final int n) {
        super(vectorArray, n);
    }
    
    public final void add(int n, final LLVector2 llVector2) {
        n = this.offset + this.numComponents * n;
        final float[] data = this.data;
        final int n2 = n + 0;
        data[n2] += llVector2.x;
        final float[] data2 = this.data;
        ++n;
        data2[n] += llVector2.y;
    }
    
    public final void addToVector(int n, final LLVector2 llVector2) {
        n = this.offset + this.numComponents * n;
        llVector2.x += this.data[n + 0];
        llVector2.y += this.data[n + 1];
    }
    
    public final void get(int n, final LLVector2 llVector2) {
        n = this.offset + this.numComponents * n;
        llVector2.x = this.data[n + 0];
        llVector2.y = this.data[n + 1];
    }
    
    public final void getSub(int n, final Vector2Array vector2Array, int n2, final LLVector2 llVector2) {
        n = this.offset + this.numComponents * n;
        n2 = vector2Array.offset + vector2Array.numComponents * n2;
        llVector2.x = this.data[n + 0] - vector2Array.data[n2 + 0];
        llVector2.y = this.data[n + 1] - vector2Array.data[n2 + 1];
    }
    
    public final void minMaxVector(int n, final LLVector2 llVector2, final LLVector2 llVector3) {
        n = this.offset + this.numComponents * n;
        final float n2 = this.data[n + 0];
        final float n3 = this.data[n + 1];
        if (llVector2.x > n2) {
            llVector2.x = n2;
        }
        if (llVector3.x < n2) {
            llVector3.x = n2;
        }
        if (llVector2.y > n3) {
            llVector2.y = n3;
        }
        if (llVector3.y < n3) {
            llVector3.y = n3;
        }
    }
    
    public final void minMaxVector(final LLVector2 llVector2, final LLVector2 llVector3) {
        int offset = this.offset;
        for (int i = 0; i < this.length; ++i) {
            final float n = this.data[offset + 0];
            final float n2 = this.data[offset + 1];
            if (llVector2.x > n) {
                llVector2.x = n;
            }
            if (llVector3.x < n) {
                llVector3.x = n;
            }
            if (llVector2.y > n2) {
                llVector2.y = n2;
            }
            if (llVector3.y < n2) {
                llVector3.y = n2;
            }
            offset += this.numComponents;
        }
    }
    
    public final void set(int n, final float n2, final float n3) {
        n = this.offset + this.numComponents * n;
        this.data[n + 0] = n2;
        this.data[n + 1] = n3;
    }
    
    public void swap(int i, int n) {
        final int n2 = this.numComponents * i + this.offset;
        i = this.offset;
        n = this.numComponents * n + i;
        float n3;
        for (i = 0; i < 2; ++i) {
            n3 = this.data[n2 + i];
            this.data[n2 + i] = this.data[n + i];
            this.data[n + i] = n3;
        }
    }
}
