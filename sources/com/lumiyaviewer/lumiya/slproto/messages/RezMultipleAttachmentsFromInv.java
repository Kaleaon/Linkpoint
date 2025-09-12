// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class RezMultipleAttachmentsFromInv extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID SessionID;

        public AgentData()
        {
        }
    }

    public static class HeaderData
    {

        public UUID CompoundMsgID;
        public boolean FirstDetachAll;
        public int TotalObjects;

        public HeaderData()
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
    public HeaderData HeaderData_Field;
    public ArrayList ObjectData_Fields;

    public RezMultipleAttachmentsFromInv()
    {
        ObjectData_Fields = new ArrayList();
        zeroCoded = true;
        AgentData_Field = new AgentData();
        HeaderData_Field = new HeaderData();
    }

    public int CalcPayloadSize()
    {
        Iterator iterator = ObjectData_Fields.iterator();
        ObjectData objectdata;
        int i;
        int j;
        for (i = 55; iterator.hasNext(); i = objectdata.Description.length + (j + 50 + 1) + i)
        {
            objectdata = (ObjectData)iterator.next();
            j = objectdata.Name.length;
        }

        return i;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleRezMultipleAttachmentsFromInv(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)-116);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packUUID(bytebuffer, HeaderData_Field.CompoundMsgID);
        packByte(bytebuffer, (byte)HeaderData_Field.TotalObjects);
        packBoolean(bytebuffer, HeaderData_Field.FirstDetachAll);
        bytebuffer.put((byte)ObjectData_Fields.size());
        ObjectData objectdata;
        for (Iterator iterator = ObjectData_Fields.iterator(); iterator.hasNext(); packVariable(bytebuffer, objectdata.Description, 1))
        {
            objectdata = (ObjectData)iterator.next();
            packUUID(bytebuffer, objectdata.ItemID);
            packUUID(bytebuffer, objectdata.OwnerID);
            packByte(bytebuffer, (byte)objectdata.AttachmentPt);
            packInt(bytebuffer, objectdata.ItemFlags);
            packInt(bytebuffer, objectdata.GroupMask);
            packInt(bytebuffer, objectdata.EveryoneMask);
            packInt(bytebuffer, objectdata.NextOwnerMask);
            packVariable(bytebuffer, objectdata.Name, 1);
        }

    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        HeaderData_Field.CompoundMsgID = unpackUUID(bytebuffer);
        HeaderData_Field.TotalObjects = unpackByte(bytebuffer) & 0xff;
        HeaderData_Field.FirstDetachAll = unpackBoolean(bytebuffer);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            ObjectData objectdata = new ObjectData();
            objectdata.ItemID = unpackUUID(bytebuffer);
            objectdata.OwnerID = unpackUUID(bytebuffer);
            objectdata.AttachmentPt = unpackByte(bytebuffer) & 0xff;
            objectdata.ItemFlags = unpackInt(bytebuffer);
            objectdata.GroupMask = unpackInt(bytebuffer);
            objectdata.EveryoneMask = unpackInt(bytebuffer);
            objectdata.NextOwnerMask = unpackInt(bytebuffer);
            objectdata.Name = unpackVariable(bytebuffer, 1);
            objectdata.Description = unpackVariable(bytebuffer, 1);
            ObjectData_Fields.add(objectdata);
        }

    }
}
