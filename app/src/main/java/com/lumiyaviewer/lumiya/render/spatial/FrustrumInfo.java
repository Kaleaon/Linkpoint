package com.lumiyaviewer.lumiya.render.spatial;

import android.opengl.Matrix;
import java.util.Arrays;

public class FrustrumInfo {
    public final float[] mvpMatrix;
    public final float viewDistance;
    public final float viewX;
    public final float viewY;
    public final float viewZ;

    public FrustrumInfo(float f, float f2, float f3, float f4, float[] fArr, int i) {
        this.viewX = f;
        this.viewY = f2;
        this.viewZ = f3;
        this.viewDistance = f4;
        this.mvpMatrix = new float[16];
        System.arraycopy(fArr, i, this.mvpMatrix, 0, 16);
    }

    public FrustrumInfo(float f, float f2, float f3, float f4, float[] fArr, int i, float[] fArr2, int i2) {
        this.viewX = f;
        this.viewY = f2;
        this.viewZ = f3;
        this.viewDistance = f4;
        this.mvpMatrix = new float[16];
        Matrix.multiplyMM(this.mvpMatrix, 0, fArr2, i2, fArr, i);
    }

    /* DevToolsApp WARNING: Missing block: B:7:0x0018, code:
            return false;
     */
    public boolean equals(java.lang.Object obj) {
        if (!(obj instanceof FrustrumInfo)) {
            return false;
        }
        
        FrustrumInfo other = (FrustrumInfo) obj;
        
        // Compare all float fields
        if (Float.compare(other.viewX, this.viewX) != 0) return false;
        if (Float.compare(other.viewY, this.viewY) != 0) return false;
        if (Float.compare(other.viewZ, this.viewZ) != 0) return false;
        if (Float.compare(other.viewDistance, this.viewDistance) != 0) return false;
        
        // Compare matrix arrays
        return Arrays.equals(this.mvpMatrix, other.mvpMatrix);
    }
    }

    public int hashCode() {
        return ((((Float.floatToIntBits(this.viewX) + 0) + Float.floatToIntBits(this.viewY)) + Float.floatToIntBits(this.viewZ)) + Float.floatToIntBits(this.viewDistance)) + Arrays.hashCode(this.mvpMatrix);
    }
}
