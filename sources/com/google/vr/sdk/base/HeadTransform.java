package com.google.vr.sdk.base;

import android.opengl.Matrix;
import com.google.vr.cardboard.UsedByNative;

@UsedByNative
public class HeadTransform {
    private static final float GIMBAL_LOCK_EPSILON = 0.01f;
    private final float[] headView = new float[16];

    public HeadTransform() {
        Matrix.setIdentityM(this.headView, 0);
    }

    public void getEulerAngles(float[] fArr, int i) {
        float f;
        float atan2;
        if (i + 3 <= fArr.length) {
            float asin = (float) Math.asin((double) this.headView[6]);
            if (Math.sqrt((double) (1.0f - (this.headView[6] * this.headView[6]))) >= 0.009999999776482582d) {
                f = (float) Math.atan2((double) (-this.headView[2]), (double) this.headView[10]);
                atan2 = (float) Math.atan2((double) (-this.headView[4]), (double) this.headView[5]);
            } else {
                f = 0.0f;
                atan2 = (float) Math.atan2((double) this.headView[1], (double) this.headView[0]);
            }
            fArr[i + 0] = -asin;
            fArr[i + 1] = -f;
            fArr[i + 2] = -atan2;
            return;
        }
        throw new IllegalArgumentException("Not enough space to write the result");
    }

    public void getForwardVector(float[] fArr, int i) {
        if (i + 3 <= fArr.length) {
            for (int i2 = 0; i2 < 3; i2++) {
                fArr[i2 + i] = -this.headView[(i2 * 4) + 2];
            }
            return;
        }
        throw new IllegalArgumentException("Not enough space to write the result");
    }

    public void getHeadView(float[] fArr, int i) {
        if (i + 16 <= fArr.length) {
            System.arraycopy(this.headView, 0, fArr, i, 16);
            return;
        }
        throw new IllegalArgumentException("Not enough space to write the result");
    }

    @UsedByNative
    public float[] getHeadView() {
        return this.headView;
    }

    public void getQuaternion(float[] fArr, int i) {
        float f;
        float f2;
        float f3;
        float f4;
        if (i + 4 <= fArr.length) {
            float[] fArr2 = this.headView;
            float f5 = fArr2[0] + fArr2[5] + fArr2[10];
            if (f5 >= 0.0f) {
                float sqrt = (float) Math.sqrt((double) (f5 + 1.0f));
                f4 = 0.5f * sqrt;
                float f6 = 0.5f / sqrt;
                f2 = (fArr2[9] - fArr2[6]) * f6;
                f3 = (fArr2[2] - fArr2[8]) * f6;
                f = f6 * (fArr2[4] - fArr2[1]);
            } else if (fArr2[0] > fArr2[5] && fArr2[0] > fArr2[10]) {
                float sqrt2 = (float) Math.sqrt((double) (((fArr2[0] + 1.0f) - fArr2[5]) - fArr2[10]));
                f2 = sqrt2 * 0.5f;
                float f7 = 0.5f / sqrt2;
                f3 = (fArr2[4] + fArr2[1]) * f7;
                f = (fArr2[2] + fArr2[8]) * f7;
                f4 = f7 * (fArr2[9] - fArr2[6]);
            } else if (fArr2[5] > fArr2[10]) {
                float sqrt3 = (float) Math.sqrt((double) (((fArr2[5] + 1.0f) - fArr2[0]) - fArr2[10]));
                f3 = sqrt3 * 0.5f;
                float f8 = 0.5f / sqrt3;
                f2 = (fArr2[4] + fArr2[1]) * f8;
                f = (fArr2[9] + fArr2[6]) * f8;
                f4 = f8 * (fArr2[2] - fArr2[8]);
            } else {
                float sqrt4 = (float) Math.sqrt((double) (((fArr2[10] + 1.0f) - fArr2[0]) - fArr2[5]));
                f = sqrt4 * 0.5f;
                float f9 = 0.5f / sqrt4;
                f2 = (fArr2[2] + fArr2[8]) * f9;
                f3 = (fArr2[9] + fArr2[6]) * f9;
                f4 = f9 * (fArr2[4] - fArr2[1]);
            }
            fArr[i + 0] = f2;
            fArr[i + 1] = f3;
            fArr[i + 2] = f;
            fArr[i + 3] = f4;
            return;
        }
        throw new IllegalArgumentException("Not enough space to write the result");
    }

    public void getRightVector(float[] fArr, int i) {
        if (i + 3 <= fArr.length) {
            for (int i2 = 0; i2 < 3; i2++) {
                fArr[i2 + i] = this.headView[i2 * 4];
            }
            return;
        }
        throw new IllegalArgumentException("Not enough space to write the result");
    }

    public void getTranslation(float[] fArr, int i) {
        if (i + 3 <= fArr.length) {
            for (int i2 = 0; i2 < 3; i2++) {
                fArr[i2 + i] = this.headView[i2 + 12];
            }
            return;
        }
        throw new IllegalArgumentException("Not enough space to write the result");
    }

    public void getUpVector(float[] fArr, int i) {
        if (i + 3 <= fArr.length) {
            for (int i2 = 0; i2 < 3; i2++) {
                fArr[i2 + i] = this.headView[(i2 * 4) + 1];
            }
            return;
        }
        throw new IllegalArgumentException("Not enough space to write the result");
    }
}
