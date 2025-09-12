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

public class CopyInventoryFromNotecard extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID SessionID;

        public AgentData()
        {
        }
    }

    public static class InventoryData
    {

        public UUID FolderID;
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


    public AgentData AgentData_Field;
    public ArrayList InventoryData_Fields;
    public NotecardData NotecardData_Field;

    public CopyInventoryFromNotecard()
    {
        InventoryData_Fields = new ArrayList();
        zeroCoded = true;
        AgentData_Field = new AgentData();
        NotecardData_Field = new NotecardData();
    }

    public int CalcPayloadSize()
    {
        return InventoryData_Fields.size() * 32 + 69;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleCopyInventoryFromNotecard(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)9);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packUUID(bytebuffer, NotecardData_Field.NotecardItemID);
        packUUID(bytebuffer, NotecardData_Field.ObjectID);
        bytebuffer.put((byte)InventoryData_Fields.size());
        InventoryData inventorydata;
        for (Iterator iterator = InventoryData_Fields.iterator(); iterator.hasNext(); packUUID(bytebuffer, inventorydata.FolderID))
        {
            inventorydata = (InventoryData)iterator.next();
            packUUID(bytebuffer, inventorydata.ItemID);
        }

    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        NotecardData_Field.NotecardItemID = unpackUUID(bytebuffer);
        NotecardData_Field.ObjectID = unpackUUID(bytebuffer);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            InventoryData inventorydata = new InventoryData();
            inventorydata.ItemID = unpackUUID(bytebuffer);
            inventorydata.FolderID = unpackUUID(bytebuffer);
            InventoryData_Fields.add(inventorydata);
        }

    }
}
