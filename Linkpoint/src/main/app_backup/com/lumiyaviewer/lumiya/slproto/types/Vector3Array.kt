package com.lumiyaviewer.lumiya.slproto.types

class Vector3Array(val size: Int) {
    fun get(index: Int): LLVector3 = LLVector3()
    fun set(index: Int, v: LLVector3) {}
    fun getElementOffset(index: Int): Int = 0
    fun getData(): FloatArray = FloatArray(0)
}
