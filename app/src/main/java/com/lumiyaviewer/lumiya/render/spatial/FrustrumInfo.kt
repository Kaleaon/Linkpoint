package com.lumiyaviewer.lumiya.render.spatial

import android.opengl.Matrix
import java.util.Arrays

class FrustrumInfo {
    Float[] mvpMatrix
    Float viewDistance
    Float viewX
    Float viewY
    Float viewZ

    constructor(f: Float, f2: Float, f3: Float, f4: Float, fArr: FloatArray, i: Int) {
        this.viewX = f
        this.viewY = f2
        this.viewZ = f3
        this.viewDistance = f4
        this.mvpMatrix = Float[16]
        System.arraycopy(fArr, i, this.mvpMatrix, 0, 16)
    }

    constructor(f: Float, f2: Float, f3: Float, f4: Float, fArr: FloatArray, i: Int, fArr2: FloatArray, i2: Int) {
        this.viewX = f
        this.viewY = f2
        this.viewZ = f3
        this.viewDistance = f4
        this.mvpMatrix = Float[16]
        Matrix.multiplyMM(this.mvpMatrix, 0, fArr2, i2, fArr, i)
    }

    /* DevToolsApp WARNING: Missing block: B:7:0x0018, code:
            return false
     */
    fun equals(obj: java.lang.Any): Boolean {
        if (!(obj instanceof FrustrumInfo)) {
            return false
        }
        
        FrustrumInfo other = (FrustrumInfo) obj
        
        // Compare all Float fields
        if (Float.compare(other.viewX, this.viewX) != 0) return false
        if (Float.compare(other.viewY, this.viewY) != 0) return false
        if (Float.compare(other.viewZ, this.viewZ) != 0) return false
        if (Float.compare(other.viewDistance, this.viewDistance) != 0) return false
        
        // Compare matrix arrays
        return Arrays == this.mvpMatrix, other.mvpMatrix
    }
    }

    fun hashCode(): Int {
        return ((((Float.floatToIntBits(this.viewX) + 0) + Float.floatToIntBits(this.viewY)) + Float.floatToIntBits(this.viewZ)) + Float.floatToIntBits(this.viewDistance)) + Arrays.hashCode(this.mvpMatrix)
    }
}
