// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.sdk.base.sensors.internal;

public class Matrix3x3d
{
    public double[] m;
    
    public Matrix3x3d() {
        this.m = new double[9];
    }
    
    public Matrix3x3d(final double n, final double n2, final double n3, final double n4, final double n5, final double n6, final double n7, final double n8, final double n9) {
        (this.m = new double[9])[0] = n;
        this.m[1] = n2;
        this.m[2] = n3;
        this.m[3] = n4;
        this.m[4] = n5;
        this.m[5] = n6;
        this.m[6] = n7;
        this.m[7] = n8;
        this.m[8] = n9;
    }
    
    public Matrix3x3d(final Matrix3x3d matrix3x3d) {
        (this.m = new double[9])[0] = matrix3x3d.m[0];
        this.m[1] = matrix3x3d.m[1];
        this.m[2] = matrix3x3d.m[2];
        this.m[3] = matrix3x3d.m[3];
        this.m[4] = matrix3x3d.m[4];
        this.m[5] = matrix3x3d.m[5];
        this.m[6] = matrix3x3d.m[6];
        this.m[7] = matrix3x3d.m[7];
        this.m[8] = matrix3x3d.m[8];
    }
    
    public static void add(final Matrix3x3d matrix3x3d, final Matrix3x3d matrix3x3d2, final Matrix3x3d matrix3x3d3) {
        matrix3x3d3.m[0] = matrix3x3d.m[0] + matrix3x3d2.m[0];
        matrix3x3d3.m[1] = matrix3x3d.m[1] + matrix3x3d2.m[1];
        matrix3x3d3.m[2] = matrix3x3d.m[2] + matrix3x3d2.m[2];
        matrix3x3d3.m[3] = matrix3x3d.m[3] + matrix3x3d2.m[3];
        matrix3x3d3.m[4] = matrix3x3d.m[4] + matrix3x3d2.m[4];
        matrix3x3d3.m[5] = matrix3x3d.m[5] + matrix3x3d2.m[5];
        matrix3x3d3.m[6] = matrix3x3d.m[6] + matrix3x3d2.m[6];
        matrix3x3d3.m[7] = matrix3x3d.m[7] + matrix3x3d2.m[7];
        matrix3x3d3.m[8] = matrix3x3d.m[8] + matrix3x3d2.m[8];
    }
    
    public static void mult(final Matrix3x3d matrix3x3d, final Matrix3x3d matrix3x3d2, final Matrix3x3d matrix3x3d3) {
        matrix3x3d3.set(matrix3x3d.m[2] * matrix3x3d2.m[6] + (matrix3x3d.m[0] * matrix3x3d2.m[0] + matrix3x3d.m[1] * matrix3x3d2.m[3]), matrix3x3d.m[2] * matrix3x3d2.m[7] + (matrix3x3d.m[0] * matrix3x3d2.m[1] + matrix3x3d.m[1] * matrix3x3d2.m[4]), matrix3x3d.m[2] * matrix3x3d2.m[8] + (matrix3x3d.m[0] * matrix3x3d2.m[2] + matrix3x3d.m[1] * matrix3x3d2.m[5]), matrix3x3d.m[5] * matrix3x3d2.m[6] + (matrix3x3d.m[3] * matrix3x3d2.m[0] + matrix3x3d.m[4] * matrix3x3d2.m[3]), matrix3x3d.m[5] * matrix3x3d2.m[7] + (matrix3x3d.m[3] * matrix3x3d2.m[1] + matrix3x3d.m[4] * matrix3x3d2.m[4]), matrix3x3d.m[5] * matrix3x3d2.m[8] + (matrix3x3d.m[3] * matrix3x3d2.m[2] + matrix3x3d.m[4] * matrix3x3d2.m[5]), matrix3x3d.m[8] * matrix3x3d2.m[6] + (matrix3x3d.m[6] * matrix3x3d2.m[0] + matrix3x3d.m[7] * matrix3x3d2.m[3]), matrix3x3d.m[8] * matrix3x3d2.m[7] + (matrix3x3d.m[6] * matrix3x3d2.m[1] + matrix3x3d.m[7] * matrix3x3d2.m[4]), matrix3x3d.m[8] * matrix3x3d2.m[8] + (matrix3x3d.m[6] * matrix3x3d2.m[2] + matrix3x3d.m[7] * matrix3x3d2.m[5]));
    }
    
    public static void mult(final Matrix3x3d matrix3x3d, final Vector3d vector3d, final Vector3d vector3d2) {
        final double n = matrix3x3d.m[0];
        final double x = vector3d.x;
        final double n2 = matrix3x3d.m[1];
        final double y = vector3d.y;
        final double n3 = matrix3x3d.m[2];
        final double z = vector3d.z;
        final double n4 = matrix3x3d.m[3];
        final double x2 = vector3d.x;
        final double n5 = matrix3x3d.m[4];
        final double y2 = vector3d.y;
        final double n6 = matrix3x3d.m[5];
        final double z2 = vector3d.z;
        final double n7 = matrix3x3d.m[6];
        final double x3 = vector3d.x;
        final double n8 = matrix3x3d.m[7];
        final double y3 = vector3d.y;
        final double n9 = matrix3x3d.m[8];
        final double z3 = vector3d.z;
        vector3d2.x = n * x + n2 * y + n3 * z;
        vector3d2.y = n4 * x2 + n5 * y2 + n6 * z2;
        vector3d2.z = n7 * x3 + n8 * y3 + n9 * z3;
    }
    
    public double determinant() {
        return this.get(0, 0) * (this.get(1, 1) * this.get(2, 2) - this.get(2, 1) * this.get(1, 2)) - this.get(0, 1) * (this.get(1, 0) * this.get(2, 2) - this.get(1, 2) * this.get(2, 0)) + this.get(0, 2) * (this.get(1, 0) * this.get(2, 1) - this.get(1, 1) * this.get(2, 0));
    }
    
    public double get(final int n, final int n2) {
        return this.m[n * 3 + n2];
    }
    
    public void getColumn(final int n, final Vector3d vector3d) {
        vector3d.x = this.m[n];
        vector3d.y = this.m[n + 3];
        vector3d.z = this.m[n + 6];
    }
    
    public boolean invert(final Matrix3x3d matrix3x3d) {
        final double determinant = this.determinant();
        if (determinant == 0.0) {
            return false;
        }
        final double n = 1.0 / determinant;
        matrix3x3d.set((this.m[4] * this.m[8] - this.m[7] * this.m[5]) * n, -(this.m[1] * this.m[8] - this.m[2] * this.m[7]) * n, (this.m[1] * this.m[5] - this.m[2] * this.m[4]) * n, -(this.m[3] * this.m[8] - this.m[5] * this.m[6]) * n, (this.m[0] * this.m[8] - this.m[2] * this.m[6]) * n, -(this.m[0] * this.m[5] - this.m[3] * this.m[2]) * n, (this.m[3] * this.m[7] - this.m[6] * this.m[4]) * n, -(this.m[0] * this.m[7] - this.m[6] * this.m[1]) * n, (this.m[0] * this.m[4] - this.m[3] * this.m[1]) * n);
        return true;
    }
    
    public double maxNorm() {
        double a = Math.abs(this.m[0]);
        for (int i = 1; i < this.m.length; ++i) {
            a = Math.max(a, Math.abs(this.m[i]));
        }
        return a;
    }
    
    public void minusEquals(final Matrix3x3d matrix3x3d) {
        final double[] m = this.m;
        m[0] -= matrix3x3d.m[0];
        final double[] i = this.m;
        i[1] -= matrix3x3d.m[1];
        final double[] j = this.m;
        j[2] -= matrix3x3d.m[2];
        final double[] k = this.m;
        k[3] -= matrix3x3d.m[3];
        final double[] l = this.m;
        l[4] -= matrix3x3d.m[4];
        final double[] m2 = this.m;
        m2[5] -= matrix3x3d.m[5];
        final double[] m3 = this.m;
        m3[6] -= matrix3x3d.m[6];
        final double[] m4 = this.m;
        m4[7] -= matrix3x3d.m[7];
        final double[] m5 = this.m;
        m5[8] -= matrix3x3d.m[8];
    }
    
    public void plusEquals(final Matrix3x3d matrix3x3d) {
        final double[] m = this.m;
        m[0] += matrix3x3d.m[0];
        final double[] i = this.m;
        i[1] += matrix3x3d.m[1];
        final double[] j = this.m;
        j[2] += matrix3x3d.m[2];
        final double[] k = this.m;
        k[3] += matrix3x3d.m[3];
        final double[] l = this.m;
        l[4] += matrix3x3d.m[4];
        final double[] m2 = this.m;
        m2[5] += matrix3x3d.m[5];
        final double[] m3 = this.m;
        m3[6] += matrix3x3d.m[6];
        final double[] m4 = this.m;
        m4[7] += matrix3x3d.m[7];
        final double[] m5 = this.m;
        m5[8] += matrix3x3d.m[8];
    }
    
    public void scale(final double n) {
        final double[] m = this.m;
        m[0] *= n;
        final double[] i = this.m;
        i[1] *= n;
        final double[] j = this.m;
        j[2] *= n;
        final double[] k = this.m;
        k[3] *= n;
        final double[] l = this.m;
        l[4] *= n;
        final double[] m2 = this.m;
        m2[5] *= n;
        final double[] m3 = this.m;
        m3[6] *= n;
        final double[] m4 = this.m;
        m4[7] *= n;
        final double[] m5 = this.m;
        m5[8] *= n;
    }
    
    public void set(final double n, final double n2, final double n3, final double n4, final double n5, final double n6, final double n7, final double n8, final double n9) {
        this.m[0] = n;
        this.m[1] = n2;
        this.m[2] = n3;
        this.m[3] = n4;
        this.m[4] = n5;
        this.m[5] = n6;
        this.m[6] = n7;
        this.m[7] = n8;
        this.m[8] = n9;
    }
    
    public void set(final int n, final int n2, final double n3) {
        this.m[n * 3 + n2] = n3;
    }
    
    public void set(final Matrix3x3d matrix3x3d) {
        this.m[0] = matrix3x3d.m[0];
        this.m[1] = matrix3x3d.m[1];
        this.m[2] = matrix3x3d.m[2];
        this.m[3] = matrix3x3d.m[3];
        this.m[4] = matrix3x3d.m[4];
        this.m[5] = matrix3x3d.m[5];
        this.m[6] = matrix3x3d.m[6];
        this.m[7] = matrix3x3d.m[7];
        this.m[8] = matrix3x3d.m[8];
    }
    
    public void setColumn(final int n, final Vector3d vector3d) {
        this.m[n] = vector3d.x;
        this.m[n + 3] = vector3d.y;
        this.m[n + 6] = vector3d.z;
    }
    
    public void setIdentity() {
        final double[] m = this.m;
        final double[] i = this.m;
        final double[] j = this.m;
        final double[] k = this.m;
        this.m[6] = (this.m[7] = 0.0);
        j[3] = (k[5] = 0.0);
        m[1] = (i[2] = 0.0);
        final double[] l = this.m;
        final double[] m2 = this.m;
        this.m[8] = 1.0;
        l[0] = (m2[4] = 1.0);
    }
    
    public void setSameDiagonal(final double n) {
        final double[] m = this.m;
        final double[] i = this.m;
        this.m[8] = n;
        m[0] = (i[4] = n);
    }
    
    public void setZero() {
        final double[] m = this.m;
        final double[] i = this.m;
        final double[] j = this.m;
        final double[] k = this.m;
        final double[] l = this.m;
        final double[] m2 = this.m;
        final double[] m3 = this.m;
        final double[] m4 = this.m;
        this.m[8] = 0.0;
        m3[6] = (m4[7] = 0.0);
        l[4] = (m2[5] = 0.0);
        j[2] = (k[3] = 0.0);
        m[0] = (i[1] = 0.0);
    }
    
    public void transpose() {
        final double n = this.m[1];
        this.m[1] = this.m[3];
        this.m[3] = n;
        final double n2 = this.m[2];
        this.m[2] = this.m[6];
        this.m[6] = n2;
        final double n3 = this.m[5];
        this.m[5] = this.m[7];
        this.m[7] = n3;
    }
    
    public void transpose(final Matrix3x3d matrix3x3d) {
        final double n = this.m[1];
        final double n2 = this.m[2];
        final double n3 = this.m[5];
        matrix3x3d.m[0] = this.m[0];
        matrix3x3d.m[1] = this.m[3];
        matrix3x3d.m[2] = this.m[6];
        matrix3x3d.m[3] = n;
        matrix3x3d.m[4] = this.m[4];
        matrix3x3d.m[5] = this.m[7];
        matrix3x3d.m[6] = n2;
        matrix3x3d.m[7] = n3;
        matrix3x3d.m[8] = this.m[8];
    }
}
