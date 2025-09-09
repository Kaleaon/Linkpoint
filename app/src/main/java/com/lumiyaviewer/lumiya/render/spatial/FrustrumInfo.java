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
    public boolean equals(java.lang.Object r4) {
        /*
        r3 = this;
        r2 = 0;
        r0 = r4 instanceof com.lumiyaviewer.lumiya.render.spatial.FrustrumInfo;
        if (r0 != 0) goto L_0x0006;
    L_0x0005:
        return r2;
    L_0x0006:
        r4 = (com.lumiyaviewer.lumiya.render.spatial.FrustrumInfo) r4;
        r0 = r4.viewX;
        r1 = r3.viewX;
        r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1));
        if (r0 != 0) goto L_0x0018;
    L_0x0010:
        r0 = r4.viewY;
        r1 = r3.viewY;
        r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1));
        if (r0 == 0) goto L_0x0019;
    L_0x0018:
        return r2;
    L_0x0019:
        r0 = r4.viewZ;
        r1 = r3.viewZ;
        r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1));
        if (r0 != 0) goto L_0x0018;
    L_0x0021:
        r0 = r4.viewDistance;
        r1 = r3.viewDistance;
        r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1));
        if (r0 != 0) goto L_0x0018;
    L_0x0029:
        r0 = r3.mvpMatrix;
        r1 = r4.mvpMatrix;
        r0 = java.util.Arrays.equals(r0, r1);
        if (r0 != 0) goto L_0x0034;
    L_0x0033:
        return r2;
    L_0x0034:
        r0 = 1;
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.render.spatial.FrustrumInfo.equals(java.lang.Object):boolean");
    }

    public int hashCode() {
        return ((((Float.floatToIntBits(this.viewX) + 0) + Float.floatToIntBits(this.viewY)) + Float.floatToIntBits(this.viewZ)) + Float.floatToIntBits(this.viewDistance)) + Arrays.hashCode(this.mvpMatrix);
    }
}
