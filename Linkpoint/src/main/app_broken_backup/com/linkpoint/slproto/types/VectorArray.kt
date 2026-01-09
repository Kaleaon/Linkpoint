package com.linkpoint.slproto.types

open class VectorArray {
    protected val data: FloatArray
    protected val numComponents: Int
    protected val length: Int
    protected val offset: Int

    constructor(numComponents: Int, length: Int) {
        this.data = FloatArray(numComponents * length)
        this.numComponents = numComponents
        this.length = length
        this.offset = 0
    }

    constructor(source: VectorArray, offset: Int) {
        this.data = source.data
        this.numComponents = source.numComponents
        this.length = source.length
        this.offset = offset
    }

    fun getData(): FloatArray {
        return data
    }

    fun getElementOffset(index: Int): Int {
        return offset + (numComponents * index)
    }

    fun getLength(): Int {
        return length
    }

    fun getNumComponents(): Int {
        return numComponents
    }
}
