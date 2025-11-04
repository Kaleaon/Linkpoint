// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.spatial;

public class FrustrumPlanes
{
    public static final int INSIDE = 1;
    public static final int INTERSECT = 0;
    private static final int NUM_PLANES = 6;
    public static final int OUTSIDE = -1;
    private final float[] params;
    private final int[] pnIndex;
    
    public FrustrumPlanes(final float[] array) {
        this.params = new float[24];
        this.pnIndex = new int[36];
        for (int i = 0; i < 6; ++i) {
            final int n = i / 2;
            float n2;
            if ((i & 0x1) != 0x0) {
                n2 = -1.0f;
            }
            else {
                n2 = 1.0f;
            }
            this.initPlane(i, array, 2 - n, n2);
        }
    }
    
    private void initPlane(final int n, float[] params, int i, float n2) {
        final int n3 = 0;
        final int n4 = n * 4;
        for (int j = 0; j < 4; ++j) {
            this.params[n4 + j] = params[j * 4 + 3] + params[j * 4 + i] * n2;
        }
        i = 0;
        n2 = 0.0f;
        while (i < 3) {
            final float n5 = this.params[n4 + i];
            n2 += n5 * n5;
            ++i;
        }
        n2 = (float)Math.sqrt(n2);
        int n6 = 0;
        while (true) {
            i = n3;
            if (n6 >= 4) {
                break;
            }
            params = this.params;
            i = n4 + n6;
            params[i] /= n2;
            ++n6;
        }
        while (i < 3) {
            final int[] pnIndex = this.pnIndex;
            int n7;
            if (this.params[n4 + i] >= 0.0f) {
                n7 = i + 3;
            }
            else {
                n7 = i;
            }
            pnIndex[n * 6 + i] = n7;
            final int[] pnIndex2 = this.pnIndex;
            int n8;
            if (this.params[n4 + i] >= 0.0f) {
                n8 = i;
            }
            else {
                n8 = i + 3;
            }
            pnIndex2[n * 6 + i + 3] = n8;
            ++i;
        }
    }
    
    private float planeDistance(final int n, final int n2, final float[] array) {
        float n3 = 0.0f;
        for (int i = 0; i < 3; ++i) {
            n3 += this.params[n + i] * array[this.pnIndex[n2 + i]];
        }
        return this.params[n + 3] + n3;
    }
    
    public int testBoundingBox(final float[] array, final float[] array2) {
        int i = 0;
        int n = 0;
        int n2 = 0;
        while (i < 6) {
            if (this.planeDistance(n2, n, array) < 0.0f) {
                return -1;
            }
            final float planeDistance = this.planeDistance(n2, n + 3, array);
            if (i == 0) {
                array2[0] = planeDistance;
            }
            if (planeDistance < 0.0f) {
                return 0;
            }
            n2 += 4;
            n += 6;
            ++i;
        }
        return 1;
    }
}
