package com.linkpoint.slproto.types

open class VectorArray {
    protected val data: FloatArray
    protected val numComponents: Int
    protected val length: Int
    protected val offset: Int
    
    val size: Int get() = length

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

    fun getData(): FloatArray = data

    fun getElementOffset(index: Int): Int = offset + (numComponents * index)

    fun getLength(): Int = length

    fun getNumComponents(): Int = numComponents
}
