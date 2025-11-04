// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.sdk.controller;

import java.util.Locale;

public class Orientation
{
    public float w;
    public float x;
    public float y;
    public float z;
    
    Orientation() {
        this.set(0.0f, 0.0f, 0.0f, 1.0f);
    }
    
    Orientation(final float n, final float n2, final float n3, final float n4) {
        this.set(n, n2, n3, n4);
    }
    
    void multiply(final Orientation orientation) {
        final float x = this.x;
        final float y = this.y;
        final float z = this.z;
        final float w = this.w;
        this.x = orientation.w * x + orientation.x * w + orientation.z * y - orientation.y * z;
        this.y = orientation.w * y + orientation.y * w + orientation.x * z - orientation.z * x;
        this.z = orientation.w * z + orientation.z * w + orientation.y * x - orientation.x * y;
        this.w = w * orientation.w - x * orientation.x - y * orientation.y - orientation.z * z;
    }
    
    void set(final float x, final float y, final float z, final float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }
    
    void set(final Orientation orientation) {
        this.set(orientation.x, orientation.y, orientation.z, orientation.w);
    }
    
    public String toAxisAngleString() {
        float f = 0.0f;
        final float f2 = (float)Math.toDegrees(Math.acos(this.w) * 2.0);
        final float n = (float)Math.sqrt(1.0f - this.w * this.w);
        float f3;
        if (n > 0.0f) {
            f3 = this.x / n;
        }
        else {
            f3 = 0.0f;
        }
        float f4;
        if (n > 0.0f) {
            f4 = this.y / n;
        }
        else {
            f4 = 0.0f;
        }
        if (n > 0.0f) {
            f = this.z / n;
        }
        return String.format(Locale.US, "(%5.2f, %5.2f, %5.2f), %3.0f°", f3, f4, f, f2);
    }
    
    float[] toEulerAngles(final float[] array) {
        final float n = this.z * this.y + this.x * this.w;
        if (Math.abs(n) < 0.4999f) {
            array[0] = (float)Math.asin(n * 2.0f);
            array[1] = (float)Math.atan2(this.y * 2.0f * this.w - this.z * 2.0f * this.x, 1.0f - this.y * 2.0f * this.y - this.x * 2.0f * this.x);
            array[2] = (float)Math.atan2(this.z * 2.0f * this.w - this.y * 2.0f * this.x, 1.0f - this.z * 2.0f * this.z - this.x * 2.0f * this.x);
        }
        else {
            array[0] = (float)Math.copySign(1.5707963267948966, n);
            array[1] = (float)(Math.copySign(2.0f, n) * Math.atan2(this.z, this.w));
            array[2] = 0.0f;
        }
        return array;
    }
    
    public float[] toRotationMatrix(final float[] array) {
        array[0] = 1.0f - this.y * 2.0f * this.y - this.z * 2.0f * this.z;
        array[1] = this.x * 2.0f * this.y + this.z * 2.0f * this.w;
        array[2] = this.x * 2.0f * this.z - this.y * 2.0f * this.w;
        array[3] = 0.0f;
        array[4] = this.x * 2.0f * this.y - this.z * 2.0f * this.w;
        array[5] = 1.0f - this.x * 2.0f * this.x - this.z * 2.0f * this.z;
        array[6] = this.y * 2.0f * this.z + this.x * 2.0f * this.w;
        array[7] = 0.0f;
        array[8] = this.x * 2.0f * this.z + this.y * 2.0f * this.w;
        array[9] = this.y * 2.0f * this.z - this.x * 2.0f * this.w;
        array[10] = 1.0f - this.x * 2.0f * this.x - this.y * 2.0f * this.y;
        array[12] = (array[11] = 0.0f);
        array[14] = (array[13] = 0.0f);
        array[15] = 1.0f;
        return array;
    }
    
    @Override
    public String toString() {
        return String.format(Locale.US, "%5.2fi %5.2fj %5.2fk %5.2f", this.x, this.y, this.z, this.w);
    }
}
