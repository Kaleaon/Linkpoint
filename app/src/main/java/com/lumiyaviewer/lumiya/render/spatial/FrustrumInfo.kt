package com.lumiyaviewer.lumiya.render.spatial

import android.opengl.Matrix
import java.util.Arrays

class FrustrumInfo {
    
    val viewX: Float
    val viewY: Float
    val viewZ: Float
    val viewDistance: Float
    val mvpMatrix: FloatArray

    constructor(
        viewX: Float,
        viewY: Float,
        viewZ: Float,
        viewDistance: Float,
        matrix: FloatArray,
        matrixOffset: Int
    ) {
        this.viewX = viewX
        this.viewY = viewY
        this.viewZ = viewZ
        this.viewDistance = viewDistance
        this.mvpMatrix = FloatArray(16)
        System.arraycopy(matrix, matrixOffset, mvpMatrix, 0, 16)
    }

    constructor(
        viewX: Float,
        viewY: Float,
        viewZ: Float,
        viewDistance: Float,
        modelView: FloatArray,
        modelViewOffset: Int,
        projection: FloatArray,
        projectionOffset: Int
    ) {
        this.viewX = viewX
        this.viewY = viewY
        this.viewZ = viewZ
        this.viewDistance = viewDistance
        this.mvpMatrix = FloatArray(16)
        Matrix.multiplyMM(mvpMatrix, 0, projection, projectionOffset, modelView, modelViewOffset)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is FrustrumInfo) {
            return false
        }

        if (viewX.compareTo(other.viewX) != 0) return false
        if (viewY.compareTo(other.viewY) != 0) return false
        if (viewZ.compareTo(other.viewZ) != 0) return false
        if (viewDistance.compareTo(other.viewDistance) != 0) return false

        return Arrays.equals(mvpMatrix, other.mvpMatrix)
    }

    override fun hashCode(): Int {
        var result = viewX.toBits()
        result = 31 * result + viewY.toBits()
        result = 31 * result + viewZ.toBits()
        result = 31 * result + viewDistance.toBits()
        result = 31 * result + Arrays.hashCode(mvpMatrix)
        return result
    }
}
