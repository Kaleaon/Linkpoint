// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.picking;

import com.lumiyaviewer.lumiya.slproto.types.LLVector4;

// Referenced classes of package com.lumiyaviewer.lumiya.render.picking:
//            GLRayTrace

public static class t
{

    public final LLVector4 intersectPoint;
    public final float s;
    public final float t;

    public String toString()
    {
        return (new StringBuilder()).append("RayIntersectInfo{intersectPoint=").append(intersectPoint).append(", s=").append(s).append(", t=").append(t).append('}').toString();
    }

    (LLVector4 llvector4, float f, float f1)
    {
        intersectPoint = llvector4;
        s = f;
        t = f1;
    }
}
