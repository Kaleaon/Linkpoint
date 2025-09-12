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

public class MoveInventoryItem extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID SessionID;
        public boolean Stamp;

        public AgentData()
        {
        }
    }

    public static class InventoryData
    {

        public UUID FolderID;
        public UUID ItemID;
        public byte NewName[];

        public InventoryData()
        {
        }
    }


    public AgentData AgentData_Field;
    public ArrayList InventoryData_Fields;

    public MoveInventoryItem()
    {
        InventoryData_Fields = new ArrayList();
        zeroCoded = true;
        AgentData_Field = new AgentData();
    }

    public int CalcPayloadSize()
    {
        Iterator iterator = InventoryData_Fields.iterator();
        int i;
        for (i = 38; iterator.hasNext(); i = ((InventoryData)iterator.next()).NewName.length + 33 + i) { }
        return i;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleMoveInventoryItem(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)12);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packBoolean(bytebuffer, AgentData_Field.Stamp);
        bytebuffer.put((byte)InventoryData_Fields.size());
        InventoryData inventorydata;
        for (Iterator iterator = InventoryData_Fields.iterator(); iterator.hasNext(); packVariable(bytebuffer, inventorydata.NewName, 1))
        {
            inventorydata = (InventoryData)iterator.next();
            packUUID(bytebuffer, inventorydata.ItemID);
            packUUID(bytebuffer, inventorydata.FolderID);
        }

    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        AgentData_Field.Stamp = unpackBoolean(bytebuffer);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            InventoryData inventorydata = new InventoryData();
            inventorydata.ItemID = unpackUUID(bytebuffer);
            inventorydata.FolderID = unpackUUID(bytebuffer);
            inventorydata.NewName = unpackVariable(bytebuffer, 1);
            InventoryData_Fields.add(inventorydata);
        }

    }
}
