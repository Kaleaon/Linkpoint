// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3d;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class EventInfoReply extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;

        public AgentData()
        {
        }
    }

    public static class EventData
    {

        public int Amount;
        public byte Category[];
        public int Cover;
        public byte Creator[];
        public byte Date[];
        public int DateUTC;
        public byte Desc[];
        public int Duration;
        public int EventFlags;
        public int EventID;
        public LLVector3d GlobalPos;
        public byte Name[];
        public byte SimName[];

        public EventData()
        {
        }
    }


    public AgentData AgentData_Field;
    public EventData EventData_Field;

    public EventInfoReply()
    {
        zeroCoded = false;
        AgentData_Field = new AgentData();
        EventData_Field = new EventData();
    }

    public int CalcPayloadSize()
    {
        return EventData_Field.Creator.length + 5 + 1 + EventData_Field.Name.length + 1 + EventData_Field.Category.length + 2 + EventData_Field.Desc.length + 1 + EventData_Field.Date.length + 4 + 4 + 4 + 4 + 1 + EventData_Field.SimName.length + 24 + 4 + 20;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleEventInfoReply(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-76);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packInt(bytebuffer, EventData_Field.EventID);
        packVariable(bytebuffer, EventData_Field.Creator, 1);
        packVariable(bytebuffer, EventData_Field.Name, 1);
        packVariable(bytebuffer, EventData_Field.Category, 1);
        packVariable(bytebuffer, EventData_Field.Desc, 2);
        packVariable(bytebuffer, EventData_Field.Date, 1);
        packInt(bytebuffer, EventData_Field.DateUTC);
        packInt(bytebuffer, EventData_Field.Duration);
        packInt(bytebuffer, EventData_Field.Cover);
        packInt(bytebuffer, EventData_Field.Amount);
        packVariable(bytebuffer, EventData_Field.SimName, 1);
        packLLVector3d(bytebuffer, EventData_Field.GlobalPos);
        packInt(bytebuffer, EventData_Field.EventFlags);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        EventData_Field.EventID = unpackInt(bytebuffer);
        EventData_Field.Creator = unpackVariable(bytebuffer, 1);
        EventData_Field.Name = unpackVariable(bytebuffer, 1);
        EventData_Field.Category = unpackVariable(bytebuffer, 1);
        EventData_Field.Desc = unpackVariable(bytebuffer, 2);
        EventData_Field.Date = unpackVariable(bytebuffer, 1);
        EventData_Field.DateUTC = unpackInt(bytebuffer);
        EventData_Field.Duration = unpackInt(bytebuffer);
        EventData_Field.Cover = unpackInt(bytebuffer);
        EventData_Field.Amount = unpackInt(bytebuffer);
        EventData_Field.SimName = unpackVariable(bytebuffer, 1);
        EventData_Field.GlobalPos = unpackLLVector3d(bytebuffer);
        EventData_Field.EventFlags = unpackInt(bytebuffer);
    }
}
