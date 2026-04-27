// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto;

import com.lumiyaviewer.lumiya.slproto.auth.SLAuthReply;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.UUID;

public class SLCircuitInfo
{

    public final UUID agentID;
    final int circuitCode;
    public final UUID sessionID;
    final SocketAddress socketAddress;

    SLCircuitInfo(SLAuthReply slauthreply)
    {
        socketAddress = new InetSocketAddress(slauthreply.simAddress, slauthreply.simPort);
        sessionID = slauthreply.sessionID;
        agentID = slauthreply.agentID;
        circuitCode = slauthreply.circuitCode;
    }
}
