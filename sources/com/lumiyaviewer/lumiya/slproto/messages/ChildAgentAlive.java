// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class ChildAgentAlive extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public long RegionHandle;
        public UUID SessionID;
        public int ViewerCircuitCode;

        public AgentData()
        {
        }
    }


    public AgentData AgentData_Field;

    public ChildAgentAlive()
    {
        zeroCoded = false;
        AgentData_Field = new AgentData();
    }

    public int CalcPayloadSize()
    {
        return 45;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleChildAgentAlive(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.put((byte)26);
        packLong(bytebuffer, AgentData_Field.RegionHandle);
        packInt(bytebuffer, AgentData_Field.ViewerCircuitCode);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.RegionHandle = unpackLong(bytebuffer);
        AgentData_Field.ViewerCircuitCode = unpackInt(bytebuffer);
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
    }
}
