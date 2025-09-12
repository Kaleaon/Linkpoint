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

public class FetchInventory extends SLMessage
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

        public UUID ItemID;
        public UUID OwnerID;

        public InventoryData()
        {
        }
    }


    public AgentData AgentData_Field;
    public ArrayList InventoryData_Fields;

    public FetchInventory()
    {
        InventoryData_Fields = new ArrayList();
        zeroCoded = true;
        AgentData_Field = new AgentData();
    }

    public int CalcPayloadSize()
    {
        return InventoryData_Fields.size() * 32 + 37;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleFetchInventory(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)23);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        bytebuffer.put((byte)InventoryData_Fields.size());
        InventoryData inventorydata;
        for (Iterator iterator = InventoryData_Fields.iterator(); iterator.hasNext(); packUUID(bytebuffer, inventorydata.ItemID))
        {
            inventorydata = (InventoryData)iterator.next();
            packUUID(bytebuffer, inventorydata.OwnerID);
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
            inventorydata.OwnerID = unpackUUID(bytebuffer);
            inventorydata.ItemID = unpackUUID(bytebuffer);
            InventoryData_Fields.add(inventorydata);
        }

    }
}
