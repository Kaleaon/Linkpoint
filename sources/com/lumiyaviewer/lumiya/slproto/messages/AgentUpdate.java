// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class AgentUpdate extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public LLQuaternion BodyRotation;
        public LLVector3 CameraAtAxis;
        public LLVector3 CameraCenter;
        public LLVector3 CameraLeftAxis;
        public LLVector3 CameraUpAxis;
        public int ControlFlags;
        public float Far;
        public int Flags;
        public LLQuaternion HeadRotation;
        public UUID SessionID;
        public int State;

        public AgentData()
        {
        }
    }


    public AgentData AgentData_Field;

    public AgentUpdate()
    {
        zeroCoded = true;
        AgentData_Field = new AgentData();
    }

    public int CalcPayloadSize()
    {
        return 115;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleAgentUpdate(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.put((byte)4);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packLLQuaternion(bytebuffer, AgentData_Field.BodyRotation);
        packLLQuaternion(bytebuffer, AgentData_Field.HeadRotation);
        packByte(bytebuffer, (byte)AgentData_Field.State);
        packLLVector3(bytebuffer, AgentData_Field.CameraCenter);
        packLLVector3(bytebuffer, AgentData_Field.CameraAtAxis);
        packLLVector3(bytebuffer, AgentData_Field.CameraLeftAxis);
        packLLVector3(bytebuffer, AgentData_Field.CameraUpAxis);
        packFloat(bytebuffer, AgentData_Field.Far);
        packInt(bytebuffer, AgentData_Field.ControlFlags);
        packByte(bytebuffer, (byte)AgentData_Field.Flags);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        AgentData_Field.BodyRotation = unpackLLQuaternion(bytebuffer);
        AgentData_Field.HeadRotation = unpackLLQuaternion(bytebuffer);
        AgentData_Field.State = unpackByte(bytebuffer) & 0xff;
        AgentData_Field.CameraCenter = unpackLLVector3(bytebuffer);
        AgentData_Field.CameraAtAxis = unpackLLVector3(bytebuffer);
        AgentData_Field.CameraLeftAxis = unpackLLVector3(bytebuffer);
        AgentData_Field.CameraUpAxis = unpackLLVector3(bytebuffer);
        AgentData_Field.Far = unpackFloat(bytebuffer);
        AgentData_Field.ControlFlags = unpackInt(bytebuffer);
        AgentData_Field.Flags = unpackByte(bytebuffer) & 0xff;
    }
}
