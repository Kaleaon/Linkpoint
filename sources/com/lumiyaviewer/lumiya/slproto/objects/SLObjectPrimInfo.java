// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.objects;

import com.lumiyaviewer.lumiya.render.spatial.DrawListObjectEntry;
import com.lumiyaviewer.lumiya.render.spatial.DrawListPrimEntry;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.objects:
//            SLObjectInfo

public class SLObjectPrimInfo extends SLObjectInfo
{

    public SLObjectPrimInfo()
    {
    }

    protected DrawListObjectEntry createDrawListEntry()
    {
        return new DrawListPrimEntry(this);
    }

    public boolean isAvatar()
    {
        return false;
    }
}
