package com.linkpoint.slproto.types

class VectorArray {
    protected FloatArray data
    protected Int length
    protected Int numComponents
    protected Int offset

    public VectorArray(Int i, Int i2) {
        this.data = Float[(i * i2)]
        this.numComponents = i
        this.length = i2
        this.offset = 0
    }

    public VectorArray(VectorArray vectorArray, Int i) {
        this.data = vectorArray.data
        this.numComponents = vectorArray.numComponents
        this.length = vectorArray.length
        this.offset = i
    }

    val FloatArray getData() {
        return this.data
    }

    val Int getElementOffset(Int i) {
        return this.offset + (this.numComponents * i)
    }

    val Int getLength() {
        return this.length
    }

    val Int getNumComponents() {
        return this.numComponents
    }
}
