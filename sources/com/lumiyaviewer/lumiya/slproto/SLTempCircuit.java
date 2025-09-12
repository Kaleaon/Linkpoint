// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto;

import com.lumiyaviewer.lumiya.slproto.auth.SLAuthReply;
import com.lumiyaviewer.lumiya.slproto.messages.UseCircuitCode;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto:
//            SLCircuit, SLGridConnection, SLCircuitInfo, SLMessage

public class SLTempCircuit extends SLCircuit
{

    private List pendingMessages;

    public SLTempCircuit(SLGridConnection slgridconnection, SLCircuitInfo slcircuitinfo, SLAuthReply slauthreply)
        throws IOException
    {
        super(slgridconnection, slcircuitinfo, slauthreply, null);
        pendingMessages = new LinkedList();
    }

    public void DefaultMessageHandler(SLMessage slmessage)
    {
        pendingMessages.add(slmessage);
    }

    public void ProcessNetworkError()
    {
        gridConn.removeTempCircuit(this);
    }

    public void ProcessTimeout()
    {
        gridConn.removeTempCircuit(this);
    }

    public void SendUseCode()
    {
        UseCircuitCode usecircuitcode = new UseCircuitCode();
        usecircuitcode.CircuitCode_Field.Code = circuitInfo.circuitCode;
        usecircuitcode.CircuitCode_Field.SessionID = circuitInfo.sessionID;
        usecircuitcode.CircuitCode_Field.ID = circuitInfo.agentID;
        usecircuitcode.isReliable = true;
        SendMessage(usecircuitcode);
    }

    public List getPendingMessages()
    {
        return pendingMessages;
    }
}
