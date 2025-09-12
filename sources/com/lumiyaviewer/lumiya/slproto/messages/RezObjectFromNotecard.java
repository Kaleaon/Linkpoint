// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class RezObjectFromNotecard extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID GroupID;
        public UUID SessionID;

        public AgentData()
        {
        }
    }

    public static class InventoryData
    {

        public UUID ItemID;

        public InventoryData()
        {
        }
    }

    public static class NotecardData
    {

        public UUID NotecardItemID;
        public UUID ObjectID;

        public NotecardData()
        {
        }
    }

    public static class RezData
    {

        public int BypassRaycast;
        public int EveryoneMask;
        public UUID FromTaskID;
        public int GroupMask;
        public int ItemFlags;
        public int NextOwnerMask;
        public LLVector3 RayEnd;
        public boolean RayEndIsIntersection;
        public LLVector3 RayStart;
        public UUID RayTargetID;
        public boolean RemoveItem;
        public boolean RezSelected;

        public RezData()
        {
        }
    }


    public AgentData AgentData_Field;
    public ArrayList InventoryData_Fields;
    public NotecardData NotecardData_Field;
    public RezData RezData_Field;

    public RezObjectFromNotecard()
    {
        InventoryData_Fields = new ArrayList();
        zeroCoded = true;
        AgentData_Field = new AgentData();
        RezData_Field = new RezData();
        NotecardData_Field = new NotecardData();
    }

    public int CalcPayloadSize()
    {
        return InventoryData_Fields.size() * 16 + 161;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleRezObjectFromNotecard(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)38);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packUUID(bytebuffer, AgentData_Field.GroupID);
        packUUID(bytebuffer, RezData_Field.FromTaskID);
        packByte(bytebuffer, (byte)RezData_Field.BypassRaycast);
        packLLVector3(bytebuffer, RezData_Field.RayStart);
        packLLVector3(bytebuffer, RezData_Field.RayEnd);
        packUUID(bytebuffer, RezData_Field.RayTargetID);
        packBoolean(bytebuffer, RezData_Field.RayEndIsIntersection);
        packBoolean(bytebuffer, RezData_Field.RezSelected);
        packBoolean(bytebuffer, RezData_Field.RemoveItem);
        packInt(bytebuffer, RezData_Field.ItemFlags);
        packInt(bytebuffer, RezData_Field.GroupMask);
        packInt(bytebuffer, RezData_Field.EveryoneMask);
        packInt(bytebuffer, RezData_Field.NextOwnerMask);
        packUUID(bytebuffer, NotecardData_Field.NotecardItemID);
        packUUID(bytebuffer, NotecardData_Field.ObjectID);
        bytebuffer.put((byte)InventoryData_Fields.size());
        for (Iterator iterator = InventoryData_Fields.iterator(); iterator.hasNext(); packUUID(bytebuffer, ((InventoryData)iterator.next()).ItemID)) { }
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        AgentData_Field.GroupID = unpackUUID(bytebuffer);
        RezData_Field.FromTaskID = unpackUUID(bytebuffer);
        RezData_Field.BypassRaycast = unpackByte(bytebuffer) & 0xff;
        RezData_Field.RayStart = unpackLLVector3(bytebuffer);
        RezData_Field.RayEnd = unpackLLVector3(bytebuffer);
        RezData_Field.RayTargetID = unpackUUID(bytebuffer);
        RezData_Field.RayEndIsIntersection = unpackBoolean(bytebuffer);
        RezData_Field.RezSelected = unpackBoolean(bytebuffer);
        RezData_Field.RemoveItem = unpackBoolean(bytebuffer);
        RezData_Field.ItemFlags = unpackInt(bytebuffer);
        RezData_Field.GroupMask = unpackInt(bytebuffer);
        RezData_Field.EveryoneMask = unpackInt(bytebuffer);
        RezData_Field.NextOwnerMask = unpackInt(bytebuffer);
        NotecardData_Field.NotecardItemID = unpackUUID(bytebuffer);
        NotecardData_Field.ObjectID = unpackUUID(bytebuffer);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            InventoryData inventorydata = new InventoryData();
            inventorydata.ItemID = unpackUUID(bytebuffer);
            InventoryData_Fields.add(inventorydata);
        }

    }
}
