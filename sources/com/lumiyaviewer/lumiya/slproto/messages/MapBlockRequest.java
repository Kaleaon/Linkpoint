// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class MapBlockRequest extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public int EstateID;
        public int Flags;
        public boolean Godlike;
        public UUID SessionID;

        public AgentData()
        {
        }
    }

    public static class PositionData
    {

        public int MaxX;
        public int MaxY;
        public int MinX;
        public int MinY;

        public PositionData()
        {
        }
    }


    public AgentData AgentData_Field;
    public PositionData PositionData_Field;

    public MapBlockRequest()
    {
        zeroCoded = false;
        AgentData_Field = new AgentData();
        PositionData_Field = new PositionData();
    }

    public int CalcPayloadSize()
    {
        return 53;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleMapBlockRequest(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)-105);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packInt(bytebuffer, AgentData_Field.Flags);
        packInt(bytebuffer, AgentData_Field.EstateID);
        packBoolean(bytebuffer, AgentData_Field.Godlike);
        packShort(bytebuffer, (short)PositionData_Field.MinX);
        packShort(bytebuffer, (short)PositionData_Field.MaxX);
        packShort(bytebuffer, (short)PositionData_Field.MinY);
        packShort(bytebuffer, (short)PositionData_Field.MaxY);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        AgentData_Field.Flags = unpackInt(bytebuffer);
        AgentData_Field.EstateID = unpackInt(bytebuffer);
        AgentData_Field.Godlike = unpackBoolean(bytebuffer);
        PositionData_Field.MinX = unpackShort(bytebuffer) & 0xffff;
        PositionData_Field.MaxX = unpackShort(bytebuffer) & 0xffff;
        PositionData_Field.MinY = unpackShort(bytebuffer) & 0xffff;
        PositionData_Field.MaxY = unpackShort(bytebuffer) & 0xffff;
    }
}
