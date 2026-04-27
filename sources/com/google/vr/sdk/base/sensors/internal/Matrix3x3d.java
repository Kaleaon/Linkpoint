package com.google.vr.sdk.base.sensors.internal;

public class Matrix3x3d {
    public double[] m = new double[9];

    public Matrix3x3d() {
    }

    public Matrix3x3d(double d, double d2, double d3, double d4, double d5, double d6, double d7, double d8, double d9) {
        this.m[0] = d;
        this.m[1] = d2;
        this.m[2] = d3;
        this.m[3] = d4;
        this.m[4] = d5;
        this.m[5] = d6;
        this.m[6] = d7;
        this.m[7] = d8;
        this.m[8] = d9;
    }

    public Matrix3x3d(Matrix3x3d matrix3x3d) {
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

    public static void add(Matrix3x3d matrix3x3d, Matrix3x3d matrix3x3d2, Matrix3x3d matrix3x3d3) {
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

    public static void mult(Matrix3x3d matrix3x3d, Matrix3x3d matrix3x3d2, Matrix3x3d matrix3x3d3) {
        Matrix3x3d matrix3x3d4 = matrix3x3d3;
        matrix3x3d4.set((matrix3x3d.m[2] * matrix3x3d2.m[6]) + (matrix3x3d.m[0] * matrix3x3d2.m[0]) + (matrix3x3d.m[1] * matrix3x3d2.m[3]), (matrix3x3d.m[2] * matrix3x3d2.m[7]) + (matrix3x3d.m[0] * matrix3x3d2.m[1]) + (matrix3x3d.m[1] * matrix3x3d2.m[4]), (matrix3x3d.m[2] * matrix3x3d2.m[8]) + (matrix3x3d.m[0] * matrix3x3d2.m[2]) + (matrix3x3d.m[1] * matrix3x3d2.m[5]), (matrix3x3d.m[5] * matrix3x3d2.m[6]) + (matrix3x3d.m[3] * matrix3x3d2.m[0]) + (matrix3x3d.m[4] * matrix3x3d2.m[3]), (matrix3x3d.m[5] * matrix3x3d2.m[7]) + (matrix3x3d.m[3] * matrix3x3d2.m[1]) + (matrix3x3d.m[4] * matrix3x3d2.m[4]), (matrix3x3d.m[5] * matrix3x3d2.m[8]) + (matrix3x3d.m[3] * matrix3x3d2.m[2]) + (matrix3x3d.m[4] * matrix3x3d2.m[5]), (matrix3x3d.m[8] * matrix3x3d2.m[6]) + (matrix3x3d.m[6] * matrix3x3d2.m[0]) + (matrix3x3d.m[7] * matrix3x3d2.m[3]), (matrix3x3d.m[8] * matrix3x3d2.m[7]) + (matrix3x3d.m[6] * matrix3x3d2.m[1]) + (matrix3x3d.m[7] * matrix3x3d2.m[4]), (matrix3x3d.m[8] * matrix3x3d2.m[8]) + (matrix3x3d.m[6] * matrix3x3d2.m[2]) + (matrix3x3d.m[7] * matrix3x3d2.m[5]));
    }

    public static void mult(Matrix3x3d matrix3x3d, Vector3d vector3d, Vector3d vector3d2) {
        double d = (matrix3x3d.m[0] * vector3d.x) + (matrix3x3d.m[1] * vector3d.y) + (matrix3x3d.m[2] * vector3d.z);
        double d2 = (matrix3x3d.m[3] * vector3d.x) + (matrix3x3d.m[4] * vector3d.y) + (matrix3x3d.m[5] * vector3d.z);
        vector3d2.x = d;
        vector3d2.y = d2;
        vector3d2.z = (matrix3x3d.m[6] * vector3d.x) + (matrix3x3d.m[7] * vector3d.y) + (matrix3x3d.m[8] * vector3d.z);
    }

    public double determinant() {
        return ((get(0, 0) * ((get(1, 1) * get(2, 2)) - (get(2, 1) * get(1, 2)))) - (get(0, 1) * ((get(1, 0) * get(2, 2)) - (get(1, 2) * get(2, 0))))) + (get(0, 2) * ((get(1, 0) * get(2, 1)) - (get(1, 1) * get(2, 0))));
    }

    public double get(int i, int i2) {
        return this.m[(i * 3) + i2];
    }

    public void getColumn(int i, Vector3d vector3d) {
        vector3d.x = this.m[i];
        vector3d.y = this.m[i + 3];
        vector3d.z = this.m[i + 6];
    }

    public boolean invert(Matrix3x3d matrix3x3d) {
        double determinant = determinant();
        if (determinant == 0.0d) {
            return false;
        }
        double d = 1.0d / determinant;
        matrix3x3d.set(((this.m[4] * this.m[8]) - (this.m[7] * this.m[5])) * d, (-((this.m[1] * this.m[8]) - (this.m[2] * this.m[7]))) * d, ((this.m[1] * this.m[5]) - (this.m[2] * this.m[4])) * d, (-((this.m[3] * this.m[8]) - (this.m[5] * this.m[6]))) * d, ((this.m[0] * this.m[8]) - (this.m[2] * this.m[6])) * d, (-((this.m[0] * this.m[5]) - (this.m[3] * this.m[2]))) * d, ((this.m[3] * this.m[7]) - (this.m[6] * this.m[4])) * d, (-((this.m[0] * this.m[7]) - (this.m[6] * this.m[1]))) * d, ((this.m[0] * this.m[4]) - (this.m[3] * this.m[1])) * d);
        return true;
    }

    public double maxNorm() {
        double abs = Math.abs(this.m[0]);
        for (int i = 1; i < this.m.length; i++) {
            abs = Math.max(abs, Math.abs(this.m[i]));
        }
        return abs;
    }

    public void minusEquals(Matrix3x3d matrix3x3d) {
        double[] dArr = this.m;
        dArr[0] = dArr[0] - matrix3x3d.m[0];
        double[] dArr2 = this.m;
        dArr2[1] = dArr2[1] - matrix3x3d.m[1];
        double[] dArr3 = this.m;
        dArr3[2] = dArr3[2] - matrix3x3d.m[2];
        double[] dArr4 = this.m;
        dArr4[3] = dArr4[3] - matrix3x3d.m[3];
        double[] dArr5 = this.m;
        dArr5[4] = dArr5[4] - matrix3x3d.m[4];
        double[] dArr6 = this.m;
        dArr6[5] = dArr6[5] - matrix3x3d.m[5];
        double[] dArr7 = this.m;
        dArr7[6] = dArr7[6] - matrix3x3d.m[6];
        double[] dArr8 = this.m;
        dArr8[7] = dArr8[7] - matrix3x3d.m[7];
        double[] dArr9 = this.m;
        dArr9[8] = dArr9[8] - matrix3x3d.m[8];
    }

    public void plusEquals(Matrix3x3d matrix3x3d) {
        double[] dArr = this.m;
        dArr[0] = dArr[0] + matrix3x3d.m[0];
        double[] dArr2 = this.m;
        dArr2[1] = dArr2[1] + matrix3x3d.m[1];
        double[] dArr3 = this.m;
        dArr3[2] = dArr3[2] + matrix3x3d.m[2];
        double[] dArr4 = this.m;
        dArr4[3] = dArr4[3] + matrix3x3d.m[3];
        double[] dArr5 = this.m;
        dArr5[4] = dArr5[4] + matrix3x3d.m[4];
        double[] dArr6 = this.m;
        dArr6[5] = dArr6[5] + matrix3x3d.m[5];
        double[] dArr7 = this.m;
        dArr7[6] = dArr7[6] + matrix3x3d.m[6];
        double[] dArr8 = this.m;
        dArr8[7] = dArr8[7] + matrix3x3d.m[7];
        double[] dArr9 = this.m;
        dArr9[8] = dArr9[8] + matrix3x3d.m[8];
    }

    public void scale(double d) {
        double[] dArr = this.m;
        dArr[0] = dArr[0] * d;
        double[] dArr2 = this.m;
        dArr2[1] = dArr2[1] * d;
        double[] dArr3 = this.m;
        dArr3[2] = dArr3[2] * d;
        double[] dArr4 = this.m;
        dArr4[3] = dArr4[3] * d;
        double[] dArr5 = this.m;
        dArr5[4] = dArr5[4] * d;
        double[] dArr6 = this.m;
        dArr6[5] = dArr6[5] * d;
        double[] dArr7 = this.m;
        dArr7[6] = dArr7[6] * d;
        double[] dArr8 = this.m;
        dArr8[7] = dArr8[7] * d;
        double[] dArr9 = this.m;
        dArr9[8] = dArr9[8] * d;
    }

    public void set(double d, double d2, double d3, double d4, double d5, double d6, double d7, double d8, double d9) {
        this.m[0] = d;
        this.m[1] = d2;
        this.m[2] = d3;
        this.m[3] = d4;
        this.m[4] = d5;
        this.m[5] = d6;
        this.m[6] = d7;
        this.m[7] = d8;
        this.m[8] = d9;
    }

    public void set(int i, int i2, double d) {
        this.m[(i * 3) + i2] = d;
    }

    public void set(Matrix3x3d matrix3x3d) {
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

    public void setColumn(int i, Vector3d vector3d) {
        this.m[i] = vector3d.x;
        this.m[i + 3] = vector3d.y;
        this.m[i + 6] = vector3d.z;
    }

    public void setIdentity() {
        double[] dArr = this.m;
        double[] dArr2 = this.m;
        double[] dArr3 = this.m;
        double[] dArr4 = this.m;
        double[] dArr5 = this.m;
        this.m[7] = 0.0d;
        dArr5[6] = 0.0d;
        dArr4[5] = 0.0d;
        dArr3[3] = 0.0d;
        dArr2[2] = 0.0d;
        dArr[1] = 0.0d;
        double[] dArr6 = this.m;
        double[] dArr7 = this.m;
        this.m[8] = 1.0d;
        dArr7[4] = 1.0d;
        dArr6[0] = 1.0d;
    }

    public void setSameDiagonal(double d) {
        double[] dArr = this.m;
        double[] dArr2 = this.m;
        this.m[8] = d;
        dArr2[4] = d;
        dArr[0] = d;
    }

    public void setZero() {
        double[] dArr = this.m;
        double[] dArr2 = this.m;
        double[] dArr3 = this.m;
        double[] dArr4 = this.m;
        double[] dArr5 = this.m;
        double[] dArr6 = this.m;
        double[] dArr7 = this.m;
        double[] dArr8 = this.m;
        this.m[8] = 0.0d;
        dArr8[7] = 0.0d;
        dArr7[6] = 0.0d;
        dArr6[5] = 0.0d;
        dArr5[4] = 0.0d;
        dArr4[3] = 0.0d;
        dArr3[2] = 0.0d;
        dArr2[1] = 0.0d;
        dArr[0] = 0.0d;
    }

    public void transpose() {
        double d = this.m[1];
        this.m[1] = this.m[3];
        this.m[3] = d;
        double d2 = this.m[2];
        this.m[2] = this.m[6];
        this.m[6] = d2;
        double d3 = this.m[5];
        this.m[5] = this.m[7];
        this.m[7] = d3;
    }

    public void transpose(Matrix3x3d matrix3x3d) {
        double d = this.m[1];
        double d2 = this.m[2];
        double d3 = this.m[5];
        matrix3x3d.m[0] = this.m[0];
        matrix3x3d.m[1] = this.m[3];
        matrix3x3d.m[2] = this.m[6];
        matrix3x3d.m[3] = d;
        matrix3x3d.m[4] = this.m[4];
        matrix3x3d.m[5] = this.m[7];
        matrix3x3d.m[6] = d2;
        matrix3x3d.m[7] = d3;
        matrix3x3d.m[8] = this.m[8];
    }
}
