// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.types;


public final class LLTersePacking
{

    public LLTersePacking()
    {
    }

    public static final float U16_to_float(int i, float f, float f1)
    {
        return int_dequantize(1.525902E-05F, i, f, f1);
    }

    public static final float U8_to_float(int i, float f, float f1)
    {
        return int_dequantize(0.003921569F, i, f, f1);
    }

    public static final int getSignedByte(int i)
    {
        int j = i & 0xff;
        i = j;
        if (j >= 128)
        {
            i = j - 256;
        }
        return i;
    }

    private static final float int_dequantize(float f, int i, float f1, float f2)
    {
        float f4 = i;
        float f3 = f2 - f1;
        f2 = f4 * f * f3 + f1;
        f1 = f2;
        if (Math.abs(f2) < f3 * f)
        {
            f1 = 0.0F;
        }
        return f1;
    }
}
