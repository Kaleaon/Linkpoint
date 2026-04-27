// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.net.Inet4Address;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            TeleportFinish

public static class 
{

    public UUID AgentID;
    public int LocationID;
    public long RegionHandle;
    public byte SeedCapability[];
    public int SimAccess;
    public Inet4Address SimIP;
    public int SimPort;
    public int TeleportFlags;

    public ()
    {
    }
}
