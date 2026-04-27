// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            TeleportLocal

public static class A
{

    public UUID AgentID;
    public int LocationID;
    public LLVector3 LookAt;
    public LLVector3 Position;
    public int TeleportFlags;

    public A()
    {
    }
}
