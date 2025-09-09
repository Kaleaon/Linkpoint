package com.lumiyaviewer.lumiya.render.spatial;

import android.opengl.Matrix;
import java.util.Arrays;

public class FrustrumInfo {
    public final float[] mvpMatrix = new float[16];
    public final float viewDistance;
    public final float viewX;
    public final float viewY;
    public final float viewZ;

    public FrustrumInfo(float f, float f2, float f3, float f4, float[] fArr, int i) {
        this.viewX = f;
        this.viewY = f2;
        this.viewZ = f3;
        this.viewDistance = f4;
        System.arraycopy(fArr, i, this.mvpMatrix, 0, 16);
    }

    public FrustrumInfo(float f, float f2, float f3, float f4, float[] fArr, int i, float[] fArr2, int i2) {
        this.viewX = f;
        this.viewY = f2;
        this.viewZ = f3;
        this.viewDistance = f4;
        Matrix.multiplyMM(this.mvpMatrix, 0, fArr2, i2, fArr, i);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof FrustrumInfo)) {
            return false;
        }
        FrustrumInfo frustrumInfo = (FrustrumInfo) obj;
        return frustrumInfo.viewX == this.viewX && frustrumInfo.viewY == this.viewY && frustrumInfo.viewZ == this.viewZ && frustrumInfo.viewDistance == this.viewDistance && Arrays.equals(this.mvpMatrix, frustrumInfo.mvpMatrix);
    }

    public int hashCode() {
        return Float.floatToIntBits(this.viewX) + 0 + Float.floatToIntBits(this.viewY) + Float.floatToIntBits(this.viewZ) + Float.floatToIntBits(this.viewDistance) + Arrays.hashCode(this.mvpMatrix);
    }
}
