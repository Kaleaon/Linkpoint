// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.objects;

import com.google.common.collect.ImmutableList;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.objects:
//            SLObjectDisplayInfo, SLObjectInfo

public class SLAvatarObjectDisplayInfo extends SLObjectDisplayInfo
    implements SLObjectDisplayInfo.HasChildrenObjects
{

    public final ImmutableList children;
    private final boolean implicitlyAdded;
    public final UUID uuid;

    public SLAvatarObjectDisplayInfo(String s, SLObjectInfo slobjectinfo, float f, ImmutableList immutablelist, boolean flag)
    {
        super(slobjectinfo.localID, s, f, slobjectinfo.hierLevel);
        children = immutablelist;
        implicitlyAdded = flag;
        uuid = slobjectinfo.getId();
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
