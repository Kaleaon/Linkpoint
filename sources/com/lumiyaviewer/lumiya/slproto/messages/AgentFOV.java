// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class AgentFOV extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public int CircuitCode;
        public UUID SessionID;

        public AgentData()
        {
        }
    }

    public static class FOVBlock
    {

        public int GenCounter;
        public float VerticalAngle;

        public FOVBlock()
        {
        }
    }


    public AgentData AgentData_Field;
    public FOVBlock FOVBlock_Field;

    public AgentFOV()
    {
        zeroCoded = false;
        AgentData_Field = new AgentData();
        FOVBlock_Field = new FOVBlock();
    }

    public int CalcPayloadSize()
    {
        return 48;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleAgentFOV(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)82);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packInt(bytebuffer, AgentData_Field.CircuitCode);
        packInt(bytebuffer, FOVBlock_Field.GenCounter);
        packFloat(bytebuffer, FOVBlock_Field.VerticalAngle);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        AgentData_Field.CircuitCode = unpackInt(bytebuffer);
        FOVBlock_Field.GenCounter = unpackInt(bytebuffer);
        FOVBlock_Field.VerticalAngle = unpackFloat(bytebuffer);
    }
}
