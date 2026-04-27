// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.net.Inet4Address;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            RegionPresenceResponse

public static class 
{

    public Inet4Address ExternalRegionIP;
    public Inet4Address InternalRegionIP;
    public byte Message[];
    public long RegionHandle;
    public UUID RegionID;
    public int RegionPort;
    public double ValidUntil;

    public ()
    {
    }
}
