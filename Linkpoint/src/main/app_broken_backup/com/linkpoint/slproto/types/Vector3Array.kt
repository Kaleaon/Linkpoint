package com.linkpoint.slproto.types

import android.opengl.Matrix
import kotlin.math.sqrt

/**
 * Array of Vector3 values stored in a continuous float array
 * Used for efficient vertex/normal storage and manipulation
 */
class Vector3Array : VectorArray {
    
    constructor(length: Int) : super(3, length)
    
    constructor(source: VectorArray, offset: Int) : super(source, offset)
    
    fun matrixScale(matrix: FloatArray, matrixOffset: Int, index: Int) {
        val i = offset + (numComponents * index)
        Matrix.scaleM(matrix, matrixOffset, data[i + 0], data[i + 1], data[i + 2])
    }
    
    fun matrixTranslate(destMatrix: FloatArray, destOffset: Int, srcMatrix: FloatArray, srcOffset: Int, index: Int) {
        val i = offset + (numComponents * index)
        Matrix.translateM(destMatrix, destOffset, srcMatrix, srcOffset, data[i + 0], data[i + 1], data[i + 2])
    }
    
    fun add(index: Int, vec: LLVector3) {
        val i = offset + (numComponents * index)
        data[i + 0] += vec.x
        data[i + 1] += vec.y
        data[i + 2] += vec.z
    }
    
    fun addToVector(index: Int, vec: LLVector3) {
        val i = offset + (numComponents * index)
        vec.x += data[i + 0]
        vec.y += data[i + 1]
        vec.z += data[i + 2]
    }
    
    fun clear() {
        var i = offset
        for (j in 0 until size) {
            data[i + 0] = 0.0f
            data[i + 1] = 0.0f
            data[i + 2] = 0.0f
            i += numComponents
        }
    }
    
    fun distToPlane(index: Int, point: LLVector3, normal: LLVector3): Float {
        val i = offset + (numComponents * index)
        val dx = data[i + 0] - point.x
        val dy = data[i + 1] - point.y
        val dz = data[i + 2] - point.z
        return (dx * normal.x) + (dy * normal.y) + (dz * normal.z)
    }
    
    fun fill(startIndex: Int, count: Int, vec: LLVector3) {
        var i = (numComponents * startIndex) + offset
        for (j in 0 until count) {
            data[i + 0] = vec.x
            data[i + 1] = vec.y
            data[i + 2] = vec.z
            i += numComponents
        }
    }
    
    operator fun get(index: Int): LLVector3 {
        val i = offset + (numComponents * index)
        return LLVector3(data[i + 0], data[i + 1], data[i + 2])
    }
    
    fun get(index: Int, vec: LLVector3) {
        val i = offset + (numComponents * index)
        vec.x = data[i + 0]
        vec.y = data[i + 1]
        vec.z = data[i + 2]
    }
    
    fun getDistanceTo(index: Int, vec: LLVector3): Float {
        val i = offset + (numComponents * index)
        val dx = data[i + 0] - vec.x
        val dy = data[i + 1] - vec.y
        val dz = data[i + 2] - vec.z
        return sqrt((dx * dx + dy * dy + dz * dz).toDouble()).toFloat()
    }
    
    fun getMaxComponent(index: Int): Float {
        val i = (numComponents * index) + offset
        var max = data[i + 0]
        if (data[i + 1] > max) max = data[i + 1]
        if (data[i + 2] > max) max = data[i + 2]
        return max
    }
    
    fun getSub(index1: Int, index2: Int, result: LLVector3) {
        val i1 = offset + (numComponents * index1)
        val i2 = offset + (numComponents * index2)
        result.x = data[i1 + 0] - data[i2 + 0]
        result.y = data[i1 + 1] - data[i2 + 1]
        result.z = data[i1 + 2] - data[i2 + 2]
    }
    
    fun getSub(index: Int, other: Vector3Array, otherIndex: Int, result: LLVector3) {
        val i1 = offset + (numComponents * index)
        val i2 = other.offset + (other.numComponents * otherIndex)
        result.x = data[i1 + 0] - other.data[i2 + 0]
        result.y = data[i1 + 1] - other.data[i2 + 1]
        result.z = data[i1 + 2] - other.data[i2 + 2]
    }
    
    fun minMaxVector(index: Int, min: LLVector3, max: LLVector3) {
        val i = offset + (numComponents * index)
        val x = data[i + 0]
        val y = data[i + 1]
        val z = data[i + 2]
        if (min.x > x) min.x = x
        if (max.x < x) max.x = x
        if (min.y > y) min.y = y
        if (max.y < y) max.y = y
        if (min.z > z) min.z = z
        if (max.z < z) max.z = z
    }
    
    fun minMaxVector(min: LLVector3, max: LLVector3) {
        var i = offset
        for (j in 0 until size) {
            val x = data[i + 0]
            val y = data[i + 1]
            val z = data[i + 2]
            if (min.x > x) min.x = x
            if (max.x < x) max.x = x
            if (min.y > y) min.y = y
            if (max.y < y) max.y = y
            if (min.z > z) min.z = z
            if (max.z < z) max.z = z
            i += numComponents
        }
    }
    
    fun mul(index: Int, quat: LLQuaternion) {
        val i = offset + (numComponents * index)
        val x = data[i + 0]
        val y = data[i + 1]
        val z = data[i + 2]
        
        val qx = quat.x
        val qy = quat.y
        val qz = quat.z
        val qw = quat.w
        
        val f = ((-qx * x) - (qy * y)) - (qz * z)
        val f2 = ((qw * x) + (qy * z)) - (qz * y)
        val f3 = ((qw * y) + (qz * x)) - (qx * z)
        val f4 = ((y * qx) + (z * qw)) - (x * qy)
        
        data[i + 0] = (((-f * qx) + (qw * f2)) - (qz * f3)) + (qy * f4)
        data[i + 1] = (((-f * qy) + (qw * f3)) - (qx * f4)) + (qz * f2)
        data[i + 2] = (((f4 * qw) + ((-f) * qz)) - (f2 * qy)) + (qx * f3)
    }
    
    fun set(index: Int, x: Float, y: Float, z: Float) {
        val i = offset + (numComponents * index)
        data[i + 0] = x
        data[i + 1] = y
        data[i + 2] = z
    }
    
    fun set(index: Int, vec: LLVector3) {
        val i = offset + (numComponents * index)
        data[i + 0] = vec.x
        data[i + 1] = vec.y
        data[i + 2] = vec.z
    }
    
    fun set(index: Int, other: Vector3Array, otherIndex: Int) {
        val i1 = offset + (numComponents * index)
        val i2 = other.offset + (other.numComponents * otherIndex)
        data[i1 + 0] = other.data[i2 + 0]
        data[i1 + 1] = other.data[i2 + 1]
        data[i1 + 2] = other.data[i2 + 2]
    }
    
    fun setAdd(index1: Int, index2: Int) {
        val i1 = (numComponents * index1) + offset
        val i2 = (numComponents * index2) + offset
        for (j in 0 until 3) {
            data[i1 + j] += data[i2 + j]
            data[i2 + j] = data[i1 + j]
        }
    }
    
    fun subFromVector(vec: LLVector3, index: Int) {
        val i = offset + (numComponents * index)
        vec.x -= data[i + 0]
        vec.y -= data[i + 1]
        vec.z -= data[i + 2]
    }
}
