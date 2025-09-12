// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.net.Inet4Address;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            ViewerStats

public static class 
{

    public UUID AgentID;
    public int AgentsInView;
    public float FPS;
    public Inet4Address IP;
    public double MetersTraveled;
    public float Ping;
    public int RegionsVisited;
    public float RunTime;
    public UUID SessionID;
    public float SimFPS;
    public int StartTime;
    public byte SysCPU[];
    public byte SysGPU[];
    public byte SysOS[];
    public int SysRAM;

    public ()
    {
    }
}
