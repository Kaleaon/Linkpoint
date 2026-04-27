// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render;

import android.opengl.GLES20;
import android.opengl.Matrix;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class MatrixStack
{

    private static final int DEFAULT_MAX_DEPTH = 32;
    private static final int MATRIX_SIZE = 16;
    private float mMatrix[];
    private float mTemp[];
    private int mTop;

    public MatrixStack()
    {
        commonInit(32);
    }

    public MatrixStack(int i)
    {
        commonInit(i);
    }

    private void adjust(int i)
    {
        mTop = mTop + i * 16;
    }

    private void commonInit(int i)
    {
        mMatrix = new float[i * 16];
        mTemp = new float[32];
        glLoadIdentity();
    }

    private float fixedToFloat(int i)
    {
        return (float)i * 1.525879E-05F;
    }

    private void preflight_adjust(int i)
    {
        i = mTop + i * 16;
        if (i < 0)
        {
            throw new IllegalArgumentException("stack underflow");
        }
        if (i + 16 > mMatrix.length)
        {
            throw new IllegalArgumentException("stack overflow");
        } else
        {
            return;
        }
    }

    public void getMatrix(float af[], int i)
    {
        System.arraycopy(mMatrix, mTop, af, i, 16);
    }

    public float[] getMatrixData()
    {
        return mMatrix;
    }

    public int getMatrixDataOffset()
    {
        return mTop;
    }

    public void glApplyUniformMatrix(int i)
    {
        GLES20.glUniformMatrix4fv(i, 1, false, mMatrix, mTop);
    }

    public void glFrustumf(float f, float f1, float f2, float f3, float f4, float f5)
    {
        Matrix.frustumM(mMatrix, mTop, f, f1, f2, f3, f4, f5);
    }

    public void glFrustumx(int i, int j, int k, int l, int i1, int j1)
    {
        glFrustumf(fixedToFloat(i), fixedToFloat(j), fixedToFloat(k), fixedToFloat(l), fixedToFloat(i1), fixedToFloat(j1));
    }

    public void glLoadIdentity()
    {
        Matrix.setIdentityM(mMatrix, mTop);
    }

    public void glLoadMatrixf(FloatBuffer floatbuffer)
    {
        floatbuffer.get(mMatrix, mTop, 16);
    }

    public void glLoadMatrixf(float af[], int i)
    {
        System.arraycopy(af, i, mMatrix, mTop, 16);
    }

    public void glLoadMatrixx(IntBuffer intbuffer)
    {
        for (int i = 0; i < 16; i++)
        {
            mMatrix[mTop + i] = fixedToFloat(intbuffer.get());
        }

    }

    public void glLoadMatrixx(int ai[], int i)
    {
        for (int j = 0; j < 16; j++)
        {
            mMatrix[mTop + j] = fixedToFloat(ai[i + j]);
        }

    }

    public void glMultMatrixf(FloatBuffer floatbuffer)
    {
        floatbuffer.get(mTemp, 16, 16);
        glMultMatrixf(mTemp, 16);
    }

    public void glMultMatrixf(float af[], int i)
    {
        System.arraycopy(mMatrix, mTop, mTemp, 0, 16);
        Matrix.multiplyMM(mMatrix, mTop, mTemp, 0, af, i);
    }

    public void glMultMatrixx(IntBuffer intbuffer)
    {
        for (int i = 0; i < 16; i++)
        {
            mTemp[i + 16] = fixedToFloat(intbuffer.get());
        }

        glMultMatrixf(mTemp, 16);
    }

    public void glMultMatrixx(int ai[], int i)
    {
        for (int j = 0; j < 16; j++)
        {
            mTemp[j + 16] = fixedToFloat(ai[i + j]);
        }

        glMultMatrixf(mTemp, 16);
    }

    public void glOrthof(float f, float f1, float f2, float f3, float f4, float f5)
    {
        Matrix.orthoM(mMatrix, mTop, f, f1, f2, f3, f4, f5);
    }

    public void glOrthox(int i, int j, int k, int l, int i1, int j1)
    {
        glOrthof(fixedToFloat(i), fixedToFloat(j), fixedToFloat(k), fixedToFloat(l), fixedToFloat(i1), fixedToFloat(j1));
    }

    public void glPopMatrix()
    {
        preflight_adjust(-1);
        adjust(-1);
    }

    public void glPushAndLoadMatrixf(float af[], int i)
    {
        System.arraycopy(af, i, mMatrix, mTop + 16, 16);
        mTop = mTop + 16;
    }

    public void glPushAndMultMatrixf(float af[], int i)
    {
        Matrix.multiplyMM(mMatrix, mTop + 16, mMatrix, mTop, af, i);
        mTop = mTop + 16;
    }

    public void glPushMatrix()
    {
        preflight_adjust(1);
        System.arraycopy(mMatrix, mTop, mMatrix, mTop + 16, 16);
        adjust(1);
    }

    public void glRotatef(float f, float f1, float f2, float f3)
    {
        Matrix.setRotateM(mTemp, 0, f, f1, f2, f3);
        System.arraycopy(mMatrix, mTop, mTemp, 16, 16);
        Matrix.multiplyMM(mMatrix, mTop, mTemp, 16, mTemp, 0);
    }

    public void glRotatex(int i, int j, int k, int l)
    {
        glRotatef(i, fixedToFloat(j), fixedToFloat(k), fixedToFloat(l));
    }

    public void glScalef(float f, float f1, float f2)
    {
        Matrix.scaleM(mMatrix, mTop, f, f1, f2);
    }

    public void glScalex(int i, int j, int k)
    {
        glScalef(fixedToFloat(i), fixedToFloat(j), fixedToFloat(k));
    }

    public void glTranslatef(float f, float f1, float f2)
    {
        Matrix.translateM(mMatrix, mTop, f, f1, f2);
    }

    public void glTranslatex(int i, int j, int k)
    {
        glTranslatef(fixedToFloat(i), fixedToFloat(j), fixedToFloat(k));
    }

    public void reset()
    {
        mTop = 0;
    }
}
