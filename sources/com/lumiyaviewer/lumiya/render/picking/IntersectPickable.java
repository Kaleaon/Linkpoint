// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.picking;

import com.lumiyaviewer.lumiya.render.RenderContext;

// Referenced classes of package com.lumiyaviewer.lumiya.render.picking:
//            ObjectIntersectInfo

public interface IntersectPickable
{

    public abstract ObjectIntersectInfo PickObject(RenderContext rendercontext, float f, float f1, float f2);
}
