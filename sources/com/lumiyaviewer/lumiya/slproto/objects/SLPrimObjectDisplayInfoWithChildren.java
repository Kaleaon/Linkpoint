// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.objects;

import com.google.common.collect.ImmutableList;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.objects:
//            SLPrimObjectDisplayInfo, SLObjectInfo

public class SLPrimObjectDisplayInfoWithChildren extends SLPrimObjectDisplayInfo
    implements SLObjectDisplayInfo.HasChildrenObjects
{

    public final ImmutableList children;
    private final boolean implicitlyAdded;

    public SLPrimObjectDisplayInfoWithChildren(SLObjectInfo slobjectinfo, float f, ImmutableList immutablelist, boolean flag)
    {
        super(slobjectinfo, f);
        children = immutablelist;
        implicitlyAdded = flag;
    }

    public ImmutableList getChildren()
    {
        return children;
    }

    public boolean isImplicitlyAdded()
    {
        return implicitlyAdded;
    }
}
