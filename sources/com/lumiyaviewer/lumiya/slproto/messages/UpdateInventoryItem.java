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

public class UpdateInventoryItem extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID SessionID;
        public UUID TransactionID;

        public AgentData()
        {
        }
    }

    public static class InventoryData
    {

        public int BaseMask;
        public int CRC;
        public int CallbackID;
        public int CreationDate;
        public UUID CreatorID;
        public byte Description[];
        public int EveryoneMask;
        public int Flags;
        public UUID FolderID;
        public UUID GroupID;
        public int GroupMask;
        public boolean GroupOwned;
        public int InvType;
        public UUID ItemID;
        public byte Name[];
        public int NextOwnerMask;
        public UUID OwnerID;
        public int OwnerMask;
        public int SalePrice;
        public int SaleType;
        public UUID TransactionID;
        public int Type;

        public InventoryData()
        {
        }
    }


    public AgentData AgentData_Field;
    public ArrayList InventoryData_Fields;

    public UpdateInventoryItem()
    {
        InventoryData_Fields = new ArrayList();
        zeroCoded = true;
        AgentData_Field = new AgentData();
    }

    public int CalcPayloadSize()
    {
        Iterator iterator = InventoryData_Fields.iterator();
        InventoryData inventorydata;
        int i;
        int j;
        for (i = 53; iterator.hasNext(); i = inventorydata.Description.length + (j + 133 + 1) + 4 + 4 + i)
        {
            inventorydata = (InventoryData)iterator.next();
            j = inventorydata.Name.length;
        }

        return i;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleUpdateInventoryItem(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)10);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packUUID(bytebuffer, AgentData_Field.TransactionID);
        bytebuffer.put((byte)InventoryData_Fields.size());
        InventoryData inventorydata;
        for (Iterator iterator = InventoryData_Fields.iterator(); iterator.hasNext(); packInt(bytebuffer, inventorydata.CRC))
        {
            inventorydata = (InventoryData)iterator.next();
            packUUID(bytebuffer, inventorydata.ItemID);
            packUUID(bytebuffer, inventorydata.FolderID);
            packInt(bytebuffer, inventorydata.CallbackID);
            packUUID(bytebuffer, inventorydata.CreatorID);
            packUUID(bytebuffer, inventorydata.OwnerID);
            packUUID(bytebuffer, inventorydata.GroupID);
            packInt(bytebuffer, inventorydata.BaseMask);
            packInt(bytebuffer, inventorydata.OwnerMask);
            packInt(bytebuffer, inventorydata.GroupMask);
            packInt(bytebuffer, inventorydata.EveryoneMask);
            packInt(bytebuffer, inventorydata.NextOwnerMask);
            packBoolean(bytebuffer, inventorydata.GroupOwned);
            packUUID(bytebuffer, inventorydata.TransactionID);
            packByte(bytebuffer, (byte)inventorydata.Type);
            packByte(bytebuffer, (byte)inventorydata.InvType);
            packInt(bytebuffer, inventorydata.Flags);
            packByte(bytebuffer, (byte)inventorydata.SaleType);
            packInt(bytebuffer, inventorydata.SalePrice);
            packVariable(bytebuffer, inventorydata.Name, 1);
            packVariable(bytebuffer, inventorydata.Description, 1);
            packInt(bytebuffer, inventorydata.CreationDate);
        }

    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        AgentData_Field.TransactionID = unpackUUID(bytebuffer);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            InventoryData inventorydata = new InventoryData();
            inventorydata.ItemID = unpackUUID(bytebuffer);
            inventorydata.FolderID = unpackUUID(bytebuffer);
            inventorydata.CallbackID = unpackInt(bytebuffer);
            inventorydata.CreatorID = unpackUUID(bytebuffer);
            inventorydata.OwnerID = unpackUUID(bytebuffer);
            inventorydata.GroupID = unpackUUID(bytebuffer);
            inventorydata.BaseMask = unpackInt(bytebuffer);
            inventorydata.OwnerMask = unpackInt(bytebuffer);
            inventorydata.GroupMask = unpackInt(bytebuffer);
            inventorydata.EveryoneMask = unpackInt(bytebuffer);
            inventorydata.NextOwnerMask = unpackInt(bytebuffer);
            inventorydata.GroupOwned = unpackBoolean(bytebuffer);
            inventorydata.TransactionID = unpackUUID(bytebuffer);
            inventorydata.Type = unpackByte(bytebuffer);
            inventorydata.InvType = unpackByte(bytebuffer);
            inventorydata.Flags = unpackInt(bytebuffer);
            inventorydata.SaleType = unpackByte(bytebuffer) & 0xff;
            inventorydata.SalePrice = unpackInt(bytebuffer);
            inventorydata.Name = unpackVariable(bytebuffer, 1);
            inventorydata.Description = unpackVariable(bytebuffer, 1);
            inventorydata.CreationDate = unpackInt(bytebuffer);
            inventorydata.CRC = unpackInt(bytebuffer);
            InventoryData_Fields.add(inventorydata);
        }

    }
}
