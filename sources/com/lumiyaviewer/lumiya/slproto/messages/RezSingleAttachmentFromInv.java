// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class RezSingleAttachmentFromInv extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID SessionID;

        public AgentData()
        {
        }
    }

    public static class ObjectData
    {

        public int AttachmentPt;
        public byte Description[];
        public int EveryoneMask;
        public int GroupMask;
        public int ItemFlags;
        public UUID ItemID;
        public byte Name[];
        public int NextOwnerMask;
        public UUID OwnerID;

        public ObjectData()
        {
        }
    }


    public AgentData AgentData_Field;
    public ObjectData ObjectData_Field;

    public RezSingleAttachmentFromInv()
    {
        zeroCoded = true;
        AgentData_Field = new AgentData();
        ObjectData_Field = new ObjectData();
    }

    public int CalcPayloadSize()
    {
        return ObjectData_Field.Name.length + 50 + 1 + ObjectData_Field.Description.length + 36;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleRezSingleAttachmentFromInv(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)-117);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packUUID(bytebuffer, ObjectData_Field.ItemID);
        packUUID(bytebuffer, ObjectData_Field.OwnerID);
        packByte(bytebuffer, (byte)ObjectData_Field.AttachmentPt);
        packInt(bytebuffer, ObjectData_Field.ItemFlags);
        packInt(bytebuffer, ObjectData_Field.GroupMask);
        packInt(bytebuffer, ObjectData_Field.EveryoneMask);
        packInt(bytebuffer, ObjectData_Field.NextOwnerMask);
        packVariable(bytebuffer, ObjectData_Field.Name, 1);
        packVariable(bytebuffer, ObjectData_Field.Description, 1);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        ObjectData_Field.ItemID = unpackUUID(bytebuffer);
        ObjectData_Field.OwnerID = unpackUUID(bytebuffer);
        ObjectData_Field.AttachmentPt = unpackByte(bytebuffer) & 0xff;
        ObjectData_Field.ItemFlags = unpackInt(bytebuffer);
        ObjectData_Field.GroupMask = unpackInt(bytebuffer);
        ObjectData_Field.EveryoneMask = unpackInt(bytebuffer);
        ObjectData_Field.NextOwnerMask = unpackInt(bytebuffer);
        ObjectData_Field.Name = unpackVariable(bytebuffer, 1);
        ObjectData_Field.Description = unpackVariable(bytebuffer, 1);
    }
}
