package com.linkpoint.render.spatial

import android.opengl.Matrix
import java.util.Arrays

class FrustrumInfo {
    val FloatArray mvpMatrix
    val Float viewDistance
    val Float viewX
    val Float viewY
    val Float viewZ

    public FrustrumInfo(Float f, Float f2, Float f3, Float f4, FloatArray fArr, Int i) {
        this.viewX = f
        this.viewY = f2
        this.viewZ = f3
        this.viewDistance = f4
        this.mvpMatrix = Float[16]
        System.arraycopy(fArr, i, this.mvpMatrix, 0, 16)
    }

    public FrustrumInfo(Float f, Float f2, Float f3, Float f4, FloatArray fArr, Int i, FloatArray fArr2, Int i2) {
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
     public override fun equals(java.lang.Object obj): Boolean {
        if (!(obj instanceof FrustrumInfo)) {
            return false
        }
        
        val other: FrustrumInfo = (FrustrumInfo) obj
        
        // Compare all Float fields
        if (Float.compare(other.viewX, this.viewX) != 0) return false
        if (Float.compare(other.viewY, this.viewY) != 0) return false
        if (Float.compare(other.viewZ, this.viewZ) != 0) return false
        if (Float.compare(other.viewDistance, this.viewDistance) != 0) return false
        
        // Compare matrix arrays
        return Arrays.equals(this.mvpMatrix, other.mvpMatrix)
    }
    }

     public override fun hashCode(): Int {
        return ((((Float.floatToIntBits(this.viewX) + 0) + Float.floatToIntBits(this.viewY)) + Float.floatToIntBits(this.viewZ)) + Float.floatToIntBits(this.viewDistance)) + Arrays.hashCode(this.mvpMatrix)
    }
}
