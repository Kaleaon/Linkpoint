// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.picking;

import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo;

// Referenced classes of package com.lumiyaviewer.lumiya.render.picking:
//            IntersectInfo

public class ObjectIntersectInfo
{

    public final IntersectInfo intersectInfo;
    public final SLObjectInfo objInfo;
    public final float pickDepth;

    public ObjectIntersectInfo(IntersectInfo intersectinfo, SLObjectInfo slobjectinfo, float f)
    {
        intersectInfo = intersectinfo;
        objInfo = slobjectinfo;
        pickDepth = f;
    }
}
