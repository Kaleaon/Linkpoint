package com.lumiyaviewer.lumiya.render.spatial

import android.opengl.Matrix
import java.util.Arrays

class FrustrumInfo {
    var mvpMatrix: FloatArray
    var viewDistance: Float
    var viewX: Float
    var viewY: Float
    var viewZ: Float

    constructor(f: Float, f2: Float, f3: Float, f4: Float, fArr: FloatArray, i: Int) {
        this.viewX = f
        this.viewY = f2
        this.viewZ = f3
        this.viewDistance = f4
        this.mvpMatrix = FloatArray(16)
        System.arraycopy(fArr, i, this.mvpMatrix, 0, 16)
    }

    constructor(f: Float, f2: Float, f3: Float, f4: Float, fArr: FloatArray, i: Int, fArr2: FloatArray, i2: Int) {
        this.viewX = f
        this.viewY = f2
        this.viewZ = f3
        this.viewDistance = f4
        this.mvpMatrix = FloatArray(16)
        Matrix.multiplyMM(this.mvpMatrix, 0, fArr2, i2, fArr, i)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is FrustrumInfo) {
            return false
        }
        
        // Compare Float fields
        if (other.viewX.compareTo(this.viewX) != 0) return false
        if (other.viewY.compareTo(this.viewY) != 0) return false
        if (other.viewZ.compareTo(this.viewZ) != 0) return false
        if (other.viewDistance.compareTo(this.viewDistance) != 0) return false
        
        // Compare matrix arrays
        return Arrays.equals(this.mvpMatrix, other.mvpMatrix)
    }

    override fun hashCode(): Int {
        return ((((java.lang.Float.floatToIntBits(this.viewX) + 0) + java.lang.Float.floatToIntBits(this.viewY)) + java.lang.Float.floatToIntBits(this.viewZ)) + java.lang.Float.floatToIntBits(this.viewDistance)) + Arrays.hashCode(this.mvpMatrix)
    }
}
