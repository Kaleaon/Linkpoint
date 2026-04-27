package com.lumiyaviewer.lumiya.slproto.types;

public class VectorArray {
    protected float[] data;
    protected int length;
    protected int numComponents;
    protected int offset;

    public VectorArray(int i, int i2) {
        this.data = new float[(i * i2)];
        this.numComponents = i;
        this.length = i2;
        this.offset = 0;
    }

    public VectorArray(VectorArray vectorArray, int i) {
        this.data = vectorArray.data;
        this.numComponents = vectorArray.numComponents;
        this.length = vectorArray.length;
        this.offset = i;
    }

    public final float[] getData() {
        return this.data;
    }

    public final int getElementOffset(int i) {
        return this.offset + (this.numComponents * i);
    }

    public final int getLength() {
        return this.length;
    }

    public final int getNumComponents() {
        return this.numComponents;
    }
}
