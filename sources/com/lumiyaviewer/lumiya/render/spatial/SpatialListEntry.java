// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.spatial;


// Referenced classes of package com.lumiyaviewer.lumiya.render.spatial:
//            SpatialTreeNode

public class SpatialListEntry
{

    public final Object data;
    SpatialListEntry next;
    SpatialTreeNode node;
    SpatialListEntry prev;

    public SpatialListEntry(Object obj)
    {
        node = null;
        next = null;
        prev = null;
        data = obj;
    }

    public final SpatialListEntry getNext()
    {
        return next;
    }
}
