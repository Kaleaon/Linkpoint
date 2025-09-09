package com.lumiyaviewer.lumiya.render.spatial;

public class FrustrumPlanes {
    public static final int INSIDE = 1;
    public static final int INTERSECT = 0;
    private static final int NUM_PLANES = 6;
    public static final int OUTSIDE = -1;
    private final float[] params = new float[24];
    private final int[] pnIndex = new int[36];

    public FrustrumPlanes(float[] fArr) {
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 < 6) {
                initPlane(i2, fArr, 2 - (i2 / 2), (i2 & 1) != 0 ? -1.0f : 1.0f);
                i = i2 + 1;
            } else {
                return;
            }
        }
    }

    private void initPlane(int i, float[] fArr, int i2, float f) {
        int i3 = i * 4;
        for (int i4 = 0; i4 < 4; i4++) {
            this.params[i3 + i4] = fArr[(i4 * 4) + 3] + (fArr[(i4 * 4) + i2] * f);
        }
        float f2 = 0.0f;
        for (int i5 = 0; i5 < 3; i5++) {
            float f3 = this.params[i3 + i5];
            f2 += f3 * f3;
        }
        float sqrt = (float) Math.sqrt((double) f2);
        for (int i6 = 0; i6 < 4; i6++) {
            float[] fArr2 = this.params;
            int i7 = i3 + i6;
            fArr2[i7] = fArr2[i7] / sqrt;
        }
        for (int i8 = 0; i8 < 3; i8++) {
            this.pnIndex[(i * 6) + i8] = this.params[i3 + i8] >= 0.0f ? i8 + 3 : i8;
            this.pnIndex[(i * 6) + i8 + 3] = this.params[i3 + i8] >= 0.0f ? i8 : i8 + 3;
        }
    }

    private float planeDistance(int i, int i2, float[] fArr) {
        float f = 0.0f;
        for (int i3 = 0; i3 < 3; i3++) {
            f += this.params[i + i3] * fArr[this.pnIndex[i2 + i3]];
        }
        return this.params[i + 3] + f;
    }

    public int testBoundingBox(float[] fArr, float[] fArr2) {
        int i = 0;
        int i2 = 0;
        for (int i3 = 0; i3 < 6; i3++) {
            if (planeDistance(i2, i, fArr) < 0.0f) {
                return -1;
            }
            float planeDistance = planeDistance(i2, i + 3, fArr);
            if (i3 == 0) {
                fArr2[0] = planeDistance;
            }
            if (planeDistance < 0.0f) {
                return 0;
            }
            i2 += 4;
            i += 6;
        }
        return 1;
    }
}
