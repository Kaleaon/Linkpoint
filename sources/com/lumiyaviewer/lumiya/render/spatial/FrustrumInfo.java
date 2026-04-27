// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.spatial;

import android.opengl.Matrix;
import java.util.Arrays;

public class FrustrumInfo
{

    public final float mvpMatrix[];
    public final float viewDistance;
    public final float viewX;
    public final float viewY;
    public final float viewZ;

    public FrustrumInfo(float f, float f1, float f2, float f3, float af[], int i)
    {
        viewX = f;
        viewY = f1;
        viewZ = f2;
        viewDistance = f3;
        mvpMatrix = new float[16];
        System.arraycopy(af, i, mvpMatrix, 0, 16);
    }

    public FrustrumInfo(float f, float f1, float f2, float f3, float af[], int i, float af1[], 
            int j)
    {
        viewX = f;
        viewY = f1;
        viewZ = f2;
        viewDistance = f3;
        mvpMatrix = new float[16];
        Matrix.multiplyMM(mvpMatrix, 0, af1, j, af, i);
    }

    public boolean equals(Object obj)
    {
        if (!(obj instanceof FrustrumInfo))
        {
            return false;
        }
        for (obj = (FrustrumInfo)obj; ((FrustrumInfo) (obj)).viewX != viewX || ((FrustrumInfo) (obj)).viewY != viewY || ((FrustrumInfo) (obj)).viewZ != viewZ || ((FrustrumInfo) (obj)).viewDistance != viewDistance;)
        {
            return false;
        }

        return Arrays.equals(mvpMatrix, ((FrustrumInfo) (obj)).mvpMatrix);
    }

    public int hashCode()
    {
        return Float.floatToIntBits(viewX) + 0 + Float.floatToIntBits(viewY) + Float.floatToIntBits(viewZ) + Float.floatToIntBits(viewDistance) + Arrays.hashCode(mvpMatrix);
    }
}
