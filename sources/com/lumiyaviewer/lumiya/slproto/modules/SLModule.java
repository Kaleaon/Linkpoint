// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules;

import com.lumiyaviewer.lumiya.eventbus.EventBus;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLCircuitInfo;
import com.lumiyaviewer.lumiya.slproto.SLGridConnection;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class SLModule
{

    protected SLAgentCircuit agentCircuit;
    protected SLCircuitInfo circuitInfo;
    protected final EventBus eventBus = EventBus.getInstance();
    protected SLGridConnection gridConn;

    public SLModule(SLAgentCircuit slagentcircuit)
    {
        agentCircuit = slagentcircuit;
        circuitInfo = slagentcircuit.circuitInfo;
        gridConn = slagentcircuit.getGridConnection();
        slagentcircuit.RegisterMessageHandler(this);
    }

    public void HandleCircuitReady()
    {
    }

    public void HandleCloseCircuit()
    {
    }

    public void HandleGlobalOptionsChange()
    {
    }

    public void SendMessage(SLMessage slmessage)
    {
        agentCircuit.SendMessage(slmessage);
    }

    public SLCircuitInfo getCircuitInfo()
    {
        return circuitInfo;
    }
}
