package com.lumiyaviewer.lumiya.render;

import android.opengl.GLES20;
import android.opengl.Matrix;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class MatrixStack {
    private static final int DEFAULT_MAX_DEPTH = 32;
    private static final int MATRIX_SIZE = 16;
    private float[] mMatrix;
    private float[] mTemp;
    private int mTop;

    public MatrixStack() {
        commonInit(32);
    }

    public MatrixStack(int i) {
        commonInit(i);
    }

    private void adjust(int i) {
        this.mTop += i * 16;
    }

    private void commonInit(int i) {
        this.mMatrix = new float[(i * 16)];
        this.mTemp = new float[32];
        glLoadIdentity();
    }

    private float fixedToFloat(int i) {
        return ((float) i) * 1.5258789E-5f;
    }

    private void preflight_adjust(int i) {
        int i2 = this.mTop + (i * 16);
        if (i2 < 0) {
            throw new IllegalArgumentException("stack underflow");
        } else if (i2 + 16 > this.mMatrix.length) {
            throw new IllegalArgumentException("stack overflow");
        }
    }

    public void getMatrix(float[] fArr, int i) {
        System.arraycopy(this.mMatrix, this.mTop, fArr, i, 16);
    }

    public float[] getMatrixData() {
        return this.mMatrix;
    }

    public int getMatrixDataOffset() {
        return this.mTop;
    }

    public void glApplyUniformMatrix(int i) {
        GLES20.glUniformMatrix4fv(i, 1, false, this.mMatrix, this.mTop);
    }

    public void glFrustumf(float f, float f2, float f3, float f4, float f5, float f6) {
        Matrix.frustumM(this.mMatrix, this.mTop, f, f2, f3, f4, f5, f6);
    }

    public void glFrustumx(int i, int i2, int i3, int i4, int i5, int i6) {
        glFrustumf(fixedToFloat(i), fixedToFloat(i2), fixedToFloat(i3), fixedToFloat(i4), fixedToFloat(i5), fixedToFloat(i6));
    }

    public void glLoadIdentity() {
        Matrix.setIdentityM(this.mMatrix, this.mTop);
    }

    public void glLoadMatrixf(FloatBuffer floatBuffer) {
        floatBuffer.get(this.mMatrix, this.mTop, 16);
    }

    public void glLoadMatrixf(float[] fArr, int i) {
        System.arraycopy(fArr, i, this.mMatrix, this.mTop, 16);
    }

    public void glLoadMatrixx(IntBuffer intBuffer) {
        for (int i = 0; i < 16; i++) {
            this.mMatrix[this.mTop + i] = fixedToFloat(intBuffer.get());
        }
    }

    public void glLoadMatrixx(int[] iArr, int i) {
        for (int i2 = 0; i2 < 16; i2++) {
            this.mMatrix[this.mTop + i2] = fixedToFloat(iArr[i + i2]);
        }
    }

    public void glMultMatrixf(FloatBuffer floatBuffer) {
        floatBuffer.get(this.mTemp, 16, 16);
        glMultMatrixf(this.mTemp, 16);
    }

    public void glMultMatrixf(float[] fArr, int i) {
        System.arraycopy(this.mMatrix, this.mTop, this.mTemp, 0, 16);
        Matrix.multiplyMM(this.mMatrix, this.mTop, this.mTemp, 0, fArr, i);
    }

    public void glMultMatrixx(IntBuffer intBuffer) {
        for (int i = 0; i < 16; i++) {
            this.mTemp[i + 16] = fixedToFloat(intBuffer.get());
        }
        glMultMatrixf(this.mTemp, 16);
    }

    public void glMultMatrixx(int[] iArr, int i) {
        for (int i2 = 0; i2 < 16; i2++) {
            this.mTemp[i2 + 16] = fixedToFloat(iArr[i + i2]);
        }
        glMultMatrixf(this.mTemp, 16);
    }

    public void glOrthof(float f, float f2, float f3, float f4, float f5, float f6) {
        Matrix.orthoM(this.mMatrix, this.mTop, f, f2, f3, f4, f5, f6);
    }

    public void glOrthox(int i, int i2, int i3, int i4, int i5, int i6) {
        glOrthof(fixedToFloat(i), fixedToFloat(i2), fixedToFloat(i3), fixedToFloat(i4), fixedToFloat(i5), fixedToFloat(i6));
    }

    public void glPopMatrix() {
        preflight_adjust(-1);
        adjust(-1);
    }

    public void glPushAndLoadMatrixf(float[] fArr, int i) {
        System.arraycopy(fArr, i, this.mMatrix, this.mTop + 16, 16);
        this.mTop += 16;
    }

    public void glPushAndMultMatrixf(float[] fArr, int i) {
        Matrix.multiplyMM(this.mMatrix, this.mTop + 16, this.mMatrix, this.mTop, fArr, i);
        this.mTop += 16;
    }

    public void glPushMatrix() {
        preflight_adjust(1);
        System.arraycopy(this.mMatrix, this.mTop, this.mMatrix, this.mTop + 16, 16);
        adjust(1);
    }

    public void glRotatef(float f, float f2, float f3, float f4) {
        Matrix.setRotateM(this.mTemp, 0, f, f2, f3, f4);
        System.arraycopy(this.mMatrix, this.mTop, this.mTemp, 16, 16);
        Matrix.multiplyMM(this.mMatrix, this.mTop, this.mTemp, 16, this.mTemp, 0);
    }

    public void glRotatex(int i, int i2, int i3, int i4) {
        glRotatef((float) i, fixedToFloat(i2), fixedToFloat(i3), fixedToFloat(i4));
    }

    public void glScalef(float f, float f2, float f3) {
        Matrix.scaleM(this.mMatrix, this.mTop, f, f2, f3);
    }

    public void glScalex(int i, int i2, int i3) {
        glScalef(fixedToFloat(i), fixedToFloat(i2), fixedToFloat(i3));
    }

    public void glTranslatef(float f, float f2, float f3) {
        Matrix.translateM(this.mMatrix, this.mTop, f, f2, f3);
    }

    public void glTranslatex(int i, int i2, int i3) {
        glTranslatef(fixedToFloat(i), fixedToFloat(i2), fixedToFloat(i3));
    }

    public void reset() {
        this.mTop = 0;
    }
}
