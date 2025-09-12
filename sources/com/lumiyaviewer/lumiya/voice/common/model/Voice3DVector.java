// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.voice.common.model;

import android.os.Bundle;

public class Voice3DVector
{

    public final float x;
    public final float y;
    public final float z;

    public Voice3DVector(float f, float f1, float f2)
    {
        x = f;
        y = f1;
        z = f2;
    }

    public Voice3DVector(Bundle bundle)
    {
        x = bundle.getFloat("x");
        y = bundle.getFloat("y");
        z = bundle.getFloat("z");
    }

    public static Voice3DVector fromLLCoords(float f, float f1, float f2)
    {
        return new Voice3DVector(f, f2, -f1);
    }

    public Bundle toBundle()
    {
        Bundle bundle = new Bundle();
        bundle.putFloat("x", x);
        bundle.putFloat("y", y);
        bundle.putFloat("z", z);
        return bundle;
    }

    public String toString()
    {
        return String.format("(%.2f, %.2f, %.2f)", new Object[] {
            Float.valueOf(x), Float.valueOf(y), Float.valueOf(z)
        });
    }
}
