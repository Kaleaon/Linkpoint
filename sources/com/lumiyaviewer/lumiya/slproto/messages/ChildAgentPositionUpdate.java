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

public class ChildAgentPositionUpdate extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public LLVector3 AgentPos;
        public LLVector3 AgentVel;
        public LLVector3 AtAxis;
        public LLVector3 Center;
        public boolean ChangedGrid;
        public LLVector3 LeftAxis;
        public long RegionHandle;
        public UUID SessionID;
        public LLVector3 Size;
        public LLVector3 UpAxis;
        public int ViewerCircuitCode;

        public AgentData()
        {
        }
    }


    public AgentData AgentData_Field;

    public ChildAgentPositionUpdate()
    {
        zeroCoded = false;
        AgentData_Field = new AgentData();
    }

    public int CalcPayloadSize()
    {
        return 130;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleChildAgentPositionUpdate(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.put((byte)27);
        packLong(bytebuffer, AgentData_Field.RegionHandle);
        packInt(bytebuffer, AgentData_Field.ViewerCircuitCode);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packLLVector3(bytebuffer, AgentData_Field.AgentPos);
        packLLVector3(bytebuffer, AgentData_Field.AgentVel);
        packLLVector3(bytebuffer, AgentData_Field.Center);
        packLLVector3(bytebuffer, AgentData_Field.Size);
        packLLVector3(bytebuffer, AgentData_Field.AtAxis);
        packLLVector3(bytebuffer, AgentData_Field.LeftAxis);
        packLLVector3(bytebuffer, AgentData_Field.UpAxis);
        packBoolean(bytebuffer, AgentData_Field.ChangedGrid);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.RegionHandle = unpackLong(bytebuffer);
        AgentData_Field.ViewerCircuitCode = unpackInt(bytebuffer);
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        AgentData_Field.AgentPos = unpackLLVector3(bytebuffer);
        AgentData_Field.AgentVel = unpackLLVector3(bytebuffer);
        AgentData_Field.Center = unpackLLVector3(bytebuffer);
        AgentData_Field.Size = unpackLLVector3(bytebuffer);
        AgentData_Field.AtAxis = unpackLLVector3(bytebuffer);
        AgentData_Field.LeftAxis = unpackLLVector3(bytebuffer);
        AgentData_Field.UpAxis = unpackLLVector3(bytebuffer);
        AgentData_Field.ChangedGrid = unpackBoolean(bytebuffer);
    }
}
