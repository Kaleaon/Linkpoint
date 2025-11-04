// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.sdk.base;

import android.opengl.Matrix;
import com.google.vr.cardboard.UsedByNative;

@UsedByNative
public class HeadTransform
{
    private static final float GIMBAL_LOCK_EPSILON = 0.01f;
    private final float[] headView;
    
    public HeadTransform() {
        Matrix.setIdentityM(this.headView = new float[16], 0);
    }
    
    public void getEulerAngles(final float[] array, final int n) {
        if (n + 3 <= array.length) {
            final float n2 = (float)Math.asin(this.headView[6]);
            float n3;
            float n4;
            if (Math.sqrt(1.0f - this.headView[6] * this.headView[6]) >= 0.009999999776482582) {
                n3 = (float)Math.atan2(-this.headView[2], this.headView[10]);
                n4 = (float)Math.atan2(-this.headView[4], this.headView[5]);
            }
            else {
                n3 = 0.0f;
                n4 = (float)Math.atan2(this.headView[1], this.headView[0]);
            }
            array[n + 0] = -n2;
            array[n + 1] = -n3;
            array[n + 2] = -n4;
            return;
        }
        throw new IllegalArgumentException("Not enough space to write the result");
    }
    
    public void getForwardVector(final float[] array, final int n) {
        if (n + 3 <= array.length) {
            for (int i = 0; i < 3; ++i) {
                array[i + n] = -this.headView[i * 4 + 2];
            }
            return;
        }
        throw new IllegalArgumentException("Not enough space to write the result");
    }
    
    public void getHeadView(final float[] array, final int n) {
        if (n + 16 <= array.length) {
            System.arraycopy(this.headView, 0, array, n, 16);
            return;
        }
        throw new IllegalArgumentException("Not enough space to write the result");
    }
    
    @UsedByNative
    public float[] getHeadView() {
        return this.headView;
    }
    
    public void getQuaternion(final float[] array, final int n) {
        if (n + 4 <= array.length) {
            final float[] headView = this.headView;
            final float n2 = headView[0] + headView[5] + headView[10];
            float n4;
            float n6;
            float n7;
            float n8;
            if (n2 >= 0.0f) {
                final float n3 = (float)Math.sqrt(n2 + 1.0f);
                n4 = 0.5f * n3;
                final float n5 = 0.5f / n3;
                n6 = (headView[9] - headView[6]) * n5;
                n7 = (headView[2] - headView[8]) * n5;
                n8 = n5 * (headView[4] - headView[1]);
            }
            else if (headView[0] > headView[5] && headView[0] > headView[10]) {
                final float n9 = (float)Math.sqrt(headView[0] + 1.0f - headView[5] - headView[10]);
                n6 = n9 * 0.5f;
                final float n10 = 0.5f / n9;
                n7 = (headView[4] + headView[1]) * n10;
                n8 = (headView[2] + headView[8]) * n10;
                n4 = n10 * (headView[9] - headView[6]);
            }
            else if (headView[5] > headView[10]) {
                final float n11 = (float)Math.sqrt(headView[5] + 1.0f - headView[0] - headView[10]);
                n7 = n11 * 0.5f;
                final float n12 = 0.5f / n11;
                n6 = (headView[4] + headView[1]) * n12;
                n8 = (headView[9] + headView[6]) * n12;
                n4 = n12 * (headView[2] - headView[8]);
            }
            else {
                final float n13 = (float)Math.sqrt(headView[10] + 1.0f - headView[0] - headView[5]);
                n8 = n13 * 0.5f;
                final float n14 = 0.5f / n13;
                n6 = (headView[2] + headView[8]) * n14;
                n7 = (headView[9] + headView[6]) * n14;
                n4 = n14 * (headView[4] - headView[1]);
            }
            array[n + 0] = n6;
            array[n + 1] = n7;
            array[n + 2] = n8;
            array[n + 3] = n4;
            return;
        }
        throw new IllegalArgumentException("Not enough space to write the result");
    }
    
    public void getRightVector(final float[] array, final int n) {
        if (n + 3 <= array.length) {
            for (int i = 0; i < 3; ++i) {
                array[i + n] = this.headView[i * 4];
            }
            return;
        }
        throw new IllegalArgumentException("Not enough space to write the result");
    }
    
    public void getTranslation(final float[] array, final int n) {
        if (n + 3 <= array.length) {
            for (int i = 0; i < 3; ++i) {
                array[i + n] = this.headView[i + 12];
            }
            return;
        }
        throw new IllegalArgumentException("Not enough space to write the result");
    }
    
    public void getUpVector(final float[] array, final int n) {
        if (n + 3 <= array.length) {
            for (int i = 0; i < 3; ++i) {
                array[i + n] = this.headView[i * 4 + 1];
            }
            return;
        }
        throw new IllegalArgumentException("Not enough space to write the result");
    }
}
