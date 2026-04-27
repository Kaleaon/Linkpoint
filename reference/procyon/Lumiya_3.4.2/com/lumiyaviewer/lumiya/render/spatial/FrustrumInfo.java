// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.spatial;

import java.util.Arrays;
import android.opengl.Matrix;

public class FrustrumInfo
{
    public final float[] mvpMatrix;
    public final float viewDistance;
    public final float viewX;
    public final float viewY;
    public final float viewZ;
    
    public FrustrumInfo(final float viewX, final float viewY, final float viewZ, final float viewDistance, final float[] array, final int n) {
        this.viewX = viewX;
        this.viewY = viewY;
        this.viewZ = viewZ;
        this.viewDistance = viewDistance;
        System.arraycopy(array, n, this.mvpMatrix = new float[16], 0, 16);
    }
    
    public FrustrumInfo(final float viewX, final float viewY, final float viewZ, final float viewDistance, final float[] array, final int n, final float[] array2, final int n2) {
        this.viewX = viewX;
        this.viewY = viewY;
        this.viewZ = viewZ;
        this.viewDistance = viewDistance;
        Matrix.multiplyMM(this.mvpMatrix = new float[16], 0, array2, n2, array, n);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof FrustrumInfo)) {
            return false;
        }
        final FrustrumInfo frustrumInfo = (FrustrumInfo)o;
        return frustrumInfo.viewX == this.viewX && frustrumInfo.viewY == this.viewY && frustrumInfo.viewZ == this.viewZ && frustrumInfo.viewDistance == this.viewDistance && Arrays.equals(this.mvpMatrix, frustrumInfo.mvpMatrix);
    }
    
    @Override
    public int hashCode() {
        return Float.floatToIntBits(this.viewX) + 0 + Float.floatToIntBits(this.viewY) + Float.floatToIntBits(this.viewZ) + Float.floatToIntBits(this.viewDistance) + Arrays.hashCode(this.mvpMatrix);
    }
}
