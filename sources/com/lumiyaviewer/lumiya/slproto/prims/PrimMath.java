// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.prims;

import com.lumiyaviewer.lumiya.slproto.types.LLVector3;

public class PrimMath
{

    public static final float F_DEG_TO_RAD = 0.01745329F;
    public static final float F_PI = 3.141593F;

    public PrimMath()
    {
    }

    public static float lerp(float f, float f1, float f2)
    {
        return (f1 - f) * f2 + f;
    }

    public static float[] lookAt(LLVector3 llvector3, LLVector3 llvector3_1, LLVector3 llvector3_2)
    {
        float af[] = new float[16];
        llvector3 = LLVector3.sub(llvector3_1, llvector3);
        llvector3.normVec();
        llvector3_1 = new LLVector3(llvector3);
        llvector3_1.setCross(llvector3_2);
        af[0] = llvector3_1.x;
        af[4] = llvector3_1.y;
        af[8] = llvector3_1.z;
        af[1] = llvector3_2.x;
        af[5] = llvector3_2.y;
        af[9] = llvector3_2.z;
        af[2] = -llvector3.x;
        af[6] = -llvector3.y;
        af[10] = -llvector3.z;
        af[15] = 1.0F;
        return af;
    }
}
