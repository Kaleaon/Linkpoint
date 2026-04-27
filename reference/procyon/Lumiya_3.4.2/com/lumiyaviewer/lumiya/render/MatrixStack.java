// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render;

import java.nio.IntBuffer;
import java.nio.FloatBuffer;
import android.opengl.Matrix;
import android.opengl.GLES20;

public class MatrixStack
{
    private static final int DEFAULT_MAX_DEPTH = 32;
    private static final int MATRIX_SIZE = 16;
    private float[] mMatrix;
    private float[] mTemp;
    private int mTop;
    
    public MatrixStack() {
        this.commonInit(32);
    }
    
    public MatrixStack(final int n) {
        this.commonInit(n);
    }
    
    private void adjust(final int n) {
        this.mTop += n * 16;
    }
    
    private void commonInit(final int n) {
        this.mMatrix = new float[n * 16];
        this.mTemp = new float[32];
        this.glLoadIdentity();
    }
    
    private float fixedToFloat(final int n) {
        return n * 1.5258789E-5f;
    }
    
    private void preflight_adjust(int n) {
        n = this.mTop + n * 16;
        if (n < 0) {
            throw new IllegalArgumentException("stack underflow");
        }
        if (n + 16 > this.mMatrix.length) {
            throw new IllegalArgumentException("stack overflow");
        }
    }
    
    public void getMatrix(final float[] array, final int n) {
        System.arraycopy(this.mMatrix, this.mTop, array, n, 16);
    }
    
    public float[] getMatrixData() {
        return this.mMatrix;
    }
    
    public int getMatrixDataOffset() {
        return this.mTop;
    }
    
    public void glApplyUniformMatrix(final int n) {
        GLES20.glUniformMatrix4fv(n, 1, false, this.mMatrix, this.mTop);
    }
    
    public void glFrustumf(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        Matrix.frustumM(this.mMatrix, this.mTop, n, n2, n3, n4, n5, n6);
    }
    
    public void glFrustumx(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        this.glFrustumf(this.fixedToFloat(n), this.fixedToFloat(n2), this.fixedToFloat(n3), this.fixedToFloat(n4), this.fixedToFloat(n5), this.fixedToFloat(n6));
    }
    
    public void glLoadIdentity() {
        Matrix.setIdentityM(this.mMatrix, this.mTop);
    }
    
    public void glLoadMatrixf(final FloatBuffer floatBuffer) {
        floatBuffer.get(this.mMatrix, this.mTop, 16);
    }
    
    public void glLoadMatrixf(final float[] array, final int n) {
        System.arraycopy(array, n, this.mMatrix, this.mTop, 16);
    }
    
    public void glLoadMatrixx(final IntBuffer intBuffer) {
        for (int i = 0; i < 16; ++i) {
            this.mMatrix[this.mTop + i] = this.fixedToFloat(intBuffer.get());
        }
    }
    
    public void glLoadMatrixx(final int[] array, final int n) {
        for (int i = 0; i < 16; ++i) {
            this.mMatrix[this.mTop + i] = this.fixedToFloat(array[n + i]);
        }
    }
    
    public void glMultMatrixf(final FloatBuffer floatBuffer) {
        floatBuffer.get(this.mTemp, 16, 16);
        this.glMultMatrixf(this.mTemp, 16);
    }
    
    public void glMultMatrixf(final float[] array, final int n) {
        System.arraycopy(this.mMatrix, this.mTop, this.mTemp, 0, 16);
        Matrix.multiplyMM(this.mMatrix, this.mTop, this.mTemp, 0, array, n);
    }
    
    public void glMultMatrixx(final IntBuffer intBuffer) {
        for (int i = 0; i < 16; ++i) {
            this.mTemp[i + 16] = this.fixedToFloat(intBuffer.get());
        }
        this.glMultMatrixf(this.mTemp, 16);
    }
    
    public void glMultMatrixx(final int[] array, final int n) {
        for (int i = 0; i < 16; ++i) {
            this.mTemp[i + 16] = this.fixedToFloat(array[n + i]);
        }
        this.glMultMatrixf(this.mTemp, 16);
    }
    
    public void glOrthof(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        Matrix.orthoM(this.mMatrix, this.mTop, n, n2, n3, n4, n5, n6);
    }
    
    public void glOrthox(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        this.glOrthof(this.fixedToFloat(n), this.fixedToFloat(n2), this.fixedToFloat(n3), this.fixedToFloat(n4), this.fixedToFloat(n5), this.fixedToFloat(n6));
    }
    
    public void glPopMatrix() {
        this.preflight_adjust(-1);
        this.adjust(-1);
    }
    
    public void glPushAndLoadMatrixf(final float[] array, final int n) {
        System.arraycopy(array, n, this.mMatrix, this.mTop + 16, 16);
        this.mTop += 16;
    }
    
    public void glPushAndMultMatrixf(final float[] array, final int n) {
        Matrix.multiplyMM(this.mMatrix, this.mTop + 16, this.mMatrix, this.mTop, array, n);
        this.mTop += 16;
    }
    
    public void glPushMatrix() {
        this.preflight_adjust(1);
        System.arraycopy(this.mMatrix, this.mTop, this.mMatrix, this.mTop + 16, 16);
        this.adjust(1);
    }
    
    public void glRotatef(final float n, final float n2, final float n3, final float n4) {
        Matrix.setRotateM(this.mTemp, 0, n, n2, n3, n4);
        System.arraycopy(this.mMatrix, this.mTop, this.mTemp, 16, 16);
        Matrix.multiplyMM(this.mMatrix, this.mTop, this.mTemp, 16, this.mTemp, 0);
    }
    
    public void glRotatex(final int n, final int n2, final int n3, final int n4) {
        this.glRotatef((float)n, this.fixedToFloat(n2), this.fixedToFloat(n3), this.fixedToFloat(n4));
    }
    
    public void glScalef(final float n, final float n2, final float n3) {
        Matrix.scaleM(this.mMatrix, this.mTop, n, n2, n3);
    }
    
    public void glScalex(final int n, final int n2, final int n3) {
        this.glScalef(this.fixedToFloat(n), this.fixedToFloat(n2), this.fixedToFloat(n3));
    }
    
    public void glTranslatef(final float n, final float n2, final float n3) {
        Matrix.translateM(this.mMatrix, this.mTop, n, n2, n3);
    }
    
    public void glTranslatex(final int n, final int n2, final int n3) {
        this.glTranslatef(this.fixedToFloat(n), this.fixedToFloat(n2), this.fixedToFloat(n3));
    }
    
    public void reset() {
        this.mTop = 0;
    }
}
