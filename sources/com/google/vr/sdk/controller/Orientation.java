package com.google.vr.sdk.controller;

import java.util.Locale;

public class Orientation {
    public float w;
    public float x;
    public float y;
    public float z;

    Orientation() {
        set(0.0f, 0.0f, 0.0f, 1.0f);
    }

    Orientation(float f, float f2, float f3, float f4) {
        set(f, f2, f3, f4);
    }

    /* access modifiers changed from: package-private */
    public void multiply(Orientation orientation) {
        float f = this.x;
        float f2 = this.y;
        float f3 = this.z;
        float f4 = this.w;
        this.x = (((orientation.w * f) + (orientation.x * f4)) + (orientation.z * f2)) - (orientation.y * f3);
        this.y = (((orientation.w * f2) + (orientation.y * f4)) + (orientation.x * f3)) - (orientation.z * f);
        this.z = (((orientation.w * f3) + (orientation.z * f4)) + (orientation.y * f)) - (orientation.x * f2);
        this.w = (((f4 * orientation.w) - (f * orientation.x)) - (f2 * orientation.y)) - (orientation.z * f3);
    }

    /* access modifiers changed from: package-private */
    public void set(float f, float f2, float f3, float f4) {
        this.x = f;
        this.y = f2;
        this.z = f3;
        this.w = f4;
    }

    /* access modifiers changed from: package-private */
    public void set(Orientation orientation) {
        set(orientation.x, orientation.y, orientation.z, orientation.w);
    }

    public String toAxisAngleString() {
        float f = 0.0f;
        float degrees = (float) Math.toDegrees(Math.acos((double) this.w) * 2.0d);
        float sqrt = (float) Math.sqrt((double) (1.0f - (this.w * this.w)));
        float f2 = sqrt > 0.0f ? this.x / sqrt : 0.0f;
        float f3 = sqrt > 0.0f ? this.y / sqrt : 0.0f;
        if (sqrt > 0.0f) {
            f = this.z / sqrt;
        }
        return String.format(Locale.US, "(%5.2f, %5.2f, %5.2f), %3.0f°", new Object[]{Float.valueOf(f2), Float.valueOf(f3), Float.valueOf(f), Float.valueOf(degrees)});
    }

    /* access modifiers changed from: package-private */
    public float[] toEulerAngles(float[] fArr) {
        float f = (this.z * this.y) + (this.x * this.w);
        if (Math.abs(f) < 0.4999f) {
            fArr[0] = (float) Math.asin((double) (f * 2.0f));
            fArr[1] = (float) Math.atan2((double) (((this.y * 2.0f) * this.w) - ((this.z * 2.0f) * this.x)), (double) ((1.0f - ((this.y * 2.0f) * this.y)) - ((this.x * 2.0f) * this.x)));
            fArr[2] = (float) Math.atan2((double) (((this.z * 2.0f) * this.w) - ((this.y * 2.0f) * this.x)), (double) ((1.0f - ((this.z * 2.0f) * this.z)) - ((this.x * 2.0f) * this.x)));
        } else {
            fArr[0] = (float) Math.copySign(1.5707963267948966d, (double) f);
            fArr[1] = (float) (((double) Math.copySign(2.0f, f)) * Math.atan2((double) this.z, (double) this.w));
            fArr[2] = 0.0f;
        }
        return fArr;
    }

    public float[] toRotationMatrix(float[] fArr) {
        fArr[0] = (1.0f - ((this.y * 2.0f) * this.y)) - ((this.z * 2.0f) * this.z);
        fArr[1] = (this.x * 2.0f * this.y) + (this.z * 2.0f * this.w);
        fArr[2] = ((this.x * 2.0f) * this.z) - ((this.y * 2.0f) * this.w);
        fArr[3] = 0.0f;
        fArr[4] = ((this.x * 2.0f) * this.y) - ((this.z * 2.0f) * this.w);
        fArr[5] = (1.0f - ((this.x * 2.0f) * this.x)) - ((this.z * 2.0f) * this.z);
        fArr[6] = (this.y * 2.0f * this.z) + (this.x * 2.0f * this.w);
        fArr[7] = 0.0f;
        fArr[8] = (this.x * 2.0f * this.z) + (this.y * 2.0f * this.w);
        fArr[9] = ((this.y * 2.0f) * this.z) - ((this.x * 2.0f) * this.w);
        fArr[10] = (1.0f - ((this.x * 2.0f) * this.x)) - ((this.y * 2.0f) * this.y);
        fArr[11] = 0.0f;
        fArr[12] = 0.0f;
        fArr[13] = 0.0f;
        fArr[14] = 0.0f;
        fArr[15] = 1.0f;
        return fArr;
    }

    public String toString() {
        return String.format(Locale.US, "%5.2fi %5.2fj %5.2fk %5.2f", new Object[]{Float.valueOf(this.x), Float.valueOf(this.y), Float.valueOf(this.z), Float.valueOf(this.w)});
    }
}
