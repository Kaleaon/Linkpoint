// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class AgentRequestSit extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID SessionID;

        public AgentData()
        {
        }
    }

    public static class TargetObject
    {

        public LLVector3 Offset;
        public UUID TargetID;

        public TargetObject()
        {
        }
    }


    public AgentData AgentData_Field;
    public TargetObject TargetObject_Field;

    public AgentRequestSit()
    {
        zeroCoded = true;
        AgentData_Field = new AgentData();
        TargetObject_Field = new TargetObject();
    }

    public int CalcPayloadSize()
    {
        return 61;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleAgentRequestSit(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.put((byte)6);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packUUID(bytebuffer, TargetObject_Field.TargetID);
        packLLVector3(bytebuffer, TargetObject_Field.Offset);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        TargetObject_Field.TargetID = unpackUUID(bytebuffer);
        TargetObject_Field.Offset = unpackLLVector3(bytebuffer);
    }
}
