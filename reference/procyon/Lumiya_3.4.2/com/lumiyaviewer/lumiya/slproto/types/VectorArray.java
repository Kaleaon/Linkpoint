// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.types;

public class VectorArray
{
    protected float[] data;
    protected int length;
    protected int numComponents;
    protected int offset;
    
    public VectorArray(final int numComponents, final int length) {
        this.data = new float[numComponents * length];
        this.numComponents = numComponents;
        this.length = length;
        this.offset = 0;
    }
    
    public VectorArray(final VectorArray vectorArray, final int offset) {
        this.data = vectorArray.data;
        this.numComponents = vectorArray.numComponents;
        this.length = vectorArray.length;
        this.offset = offset;
    }
    
    public final float[] getData() {
        return this.data;
    }
    
    public final int getElementOffset(final int n) {
        return this.offset + this.numComponents * n;
    }
    
    public final int getLength() {
        return this.length;
    }
    
    public final int getNumComponents() {
        return this.numComponents;
    }
}
