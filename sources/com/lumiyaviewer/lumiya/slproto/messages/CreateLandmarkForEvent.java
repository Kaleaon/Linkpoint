// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class CreateLandmarkForEvent extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID SessionID;

        public AgentData()
        {
        }
    }

    public static class EventData
    {

        public int EventID;

        public EventData()
        {
        }
    }

    public static class InventoryBlock
    {

        public UUID FolderID;
        public byte Name[];

        public InventoryBlock()
        {
        }
    }


    public AgentData AgentData_Field;
    public EventData EventData_Field;
    public InventoryBlock InventoryBlock_Field;

    public CreateLandmarkForEvent()
    {
        zeroCoded = true;
        AgentData_Field = new AgentData();
        EventData_Field = new EventData();
        InventoryBlock_Field = new InventoryBlock();
    }

    public int CalcPayloadSize()
    {
        return InventoryBlock_Field.Name.length + 17 + 40;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleCreateLandmarkForEvent(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)50);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packInt(bytebuffer, EventData_Field.EventID);
        packUUID(bytebuffer, InventoryBlock_Field.FolderID);
        packVariable(bytebuffer, InventoryBlock_Field.Name, 1);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        EventData_Field.EventID = unpackInt(bytebuffer);
        InventoryBlock_Field.FolderID = unpackUUID(bytebuffer);
        InventoryBlock_Field.Name = unpackVariable(bytebuffer, 1);
    }
}
