// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.picking;

import android.opengl.Matrix;
import com.lumiyaviewer.lumiya.slproto.types.LLVector4;

public class IntersectInfo
{

    public final int faceID;
    public final boolean faceKnown;
    public final LLVector4 intersectPoint;
    public final float s;
    public final float t;
    public final float u;
    public final float v;

    public IntersectInfo(IntersectInfo intersectinfo, float af[], int i)
    {
        intersectPoint = intersectinfo.intersectPoint;
        faceID = intersectinfo.faceID;
        s = intersectinfo.s;
        t = intersectinfo.t;
        faceKnown = intersectinfo.faceKnown;
        if (faceKnown)
        {
            intersectinfo = new float[8];
            intersectinfo[0] = s;
            intersectinfo[1] = t;
            intersectinfo[3] = 1.0F;
            Matrix.multiplyMV(intersectinfo, 4, af, i, intersectinfo, 0);
            u = intersectinfo[4];
            v = intersectinfo[5];
            return;
        } else
        {
            u = intersectinfo.u;
            v = intersectinfo.v;
            return;
        }
    }

    public IntersectInfo(LLVector4 llvector4)
    {
        intersectPoint = llvector4;
        faceID = 0;
        u = 0.0F;
        v = 0.0F;
        s = 0.0F;
        t = 0.0F;
        faceKnown = false;
    }

    public IntersectInfo(LLVector4 llvector4, int i, float f, float f1)
    {
        intersectPoint = llvector4;
        faceID = i;
        u = f;
        v = f1;
        s = f;
        t = f1;
        faceKnown = true;
    }
}
