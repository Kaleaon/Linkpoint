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

public class CopyInventoryItem extends SLMessage
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

        public int CallbackID;
        public UUID NewFolderID;
        public byte NewName[];
        public UUID OldAgentID;
        public UUID OldItemID;

        public InventoryData()
        {
        }
    }


    public AgentData AgentData_Field;
    public ArrayList InventoryData_Fields;

    public CopyInventoryItem()
    {
        InventoryData_Fields = new ArrayList();
        zeroCoded = true;
        AgentData_Field = new AgentData();
    }

    public int CalcPayloadSize()
    {
        Iterator iterator = InventoryData_Fields.iterator();
        int i;
        for (i = 37; iterator.hasNext(); i = ((InventoryData)iterator.next()).NewName.length + 53 + i) { }
        return i;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleCopyInventoryItem(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)13);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        bytebuffer.put((byte)InventoryData_Fields.size());
        InventoryData inventorydata;
        for (Iterator iterator = InventoryData_Fields.iterator(); iterator.hasNext(); packVariable(bytebuffer, inventorydata.NewName, 1))
        {
            inventorydata = (InventoryData)iterator.next();
            packInt(bytebuffer, inventorydata.CallbackID);
            packUUID(bytebuffer, inventorydata.OldAgentID);
            packUUID(bytebuffer, inventorydata.OldItemID);
            packUUID(bytebuffer, inventorydata.NewFolderID);
        }

    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            InventoryData inventorydata = new InventoryData();
            inventorydata.CallbackID = unpackInt(bytebuffer);
            inventorydata.OldAgentID = unpackUUID(bytebuffer);
            inventorydata.OldItemID = unpackUUID(bytebuffer);
            inventorydata.NewFolderID = unpackUUID(bytebuffer);
            inventorydata.NewName = unpackVariable(bytebuffer, 1);
            InventoryData_Fields.add(inventorydata);
        }

    }
}
