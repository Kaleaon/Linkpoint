// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.net.Inet4Address;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            NeighborList

public static class I
{

    public Inet4Address IP;
    public byte Name[];
    public int Port;
    public Inet4Address PublicIP;
    public int PublicPort;
    public UUID RegionID;
    public int SimAccess;

    public I()
    {
    }
}
