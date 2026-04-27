// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.utils;

import android.opengl.Matrix;

public class IdentityMatrix
{

    private static final float matrix[];

    public IdentityMatrix()
    {
    }

    public static float[] getMatrix()
    {
        return matrix;
    }

    static 
    {
        matrix = new float[16];
        Matrix.setIdentityM(matrix, 0);
    }
}
