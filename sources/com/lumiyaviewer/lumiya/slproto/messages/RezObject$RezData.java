// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            RezObject

public static class 
{

    public int BypassRaycast;
    public int EveryoneMask;
    public UUID FromTaskID;
    public int GroupMask;
    public int ItemFlags;
    public int NextOwnerMask;
    public LLVector3 RayEnd;
    public boolean RayEndIsIntersection;
    public LLVector3 RayStart;
    public UUID RayTargetID;
    public boolean RemoveItem;
    public boolean RezSelected;

    public ()
    {
    }
}
