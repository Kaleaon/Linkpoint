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

public class AgentMovementComplete extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID SessionID;

        public AgentData()
        {
        }
    }

    public static class Data
    {

        public LLVector3 LookAt;
        public LLVector3 Position;
        public long RegionHandle;
        public int Timestamp;

        public Data()
        {
        }
    }

    public static class SimData
    {

        public byte ChannelVersion[];

        public SimData()
        {
        }
    }


    public AgentData AgentData_Field;
    public Data Data_Field;
    public SimData SimData_Field;

    public AgentMovementComplete()
    {
        zeroCoded = false;
        AgentData_Field = new AgentData();
        Data_Field = new Data();
        SimData_Field = new SimData();
    }

    public int CalcPayloadSize()
    {
        return SimData_Field.ChannelVersion.length + 2 + 72;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleAgentMovementComplete(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-6);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packLLVector3(bytebuffer, Data_Field.Position);
        packLLVector3(bytebuffer, Data_Field.LookAt);
        packLong(bytebuffer, Data_Field.RegionHandle);
        packInt(bytebuffer, Data_Field.Timestamp);
        packVariable(bytebuffer, SimData_Field.ChannelVersion, 2);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        Data_Field.Position = unpackLLVector3(bytebuffer);
        Data_Field.LookAt = unpackLLVector3(bytebuffer);
        Data_Field.RegionHandle = unpackLong(bytebuffer);
        Data_Field.Timestamp = unpackInt(bytebuffer);
        SimData_Field.ChannelVersion = unpackVariable(bytebuffer, 2);
    }
}
