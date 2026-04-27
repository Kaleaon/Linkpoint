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

public class BulkUpdateInventory extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID TransactionID;

        public AgentData()
        {
        }
    }

    public static class FolderData
    {

        public UUID FolderID;
        public byte Name[];
        public UUID ParentID;
        public int Type;

        public FolderData()
        {
        }
    }

    public static class ItemData
    {

        public UUID AssetID;
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
        public int Type;

        public ItemData()
        {
        }
    }


    public AgentData AgentData_Field;
    public ArrayList FolderData_Fields;
    public ArrayList ItemData_Fields;

    public BulkUpdateInventory()
    {
        FolderData_Fields = new ArrayList();
        ItemData_Fields = new ArrayList();
        zeroCoded = true;
        AgentData_Field = new AgentData();
    }

    public int CalcPayloadSize()
    {
        Iterator iterator = FolderData_Fields.iterator();
        int i;
        for (i = 37; iterator.hasNext(); i = ((FolderData)iterator.next()).Name.length + 34 + i) { }
        iterator = ItemData_Fields.iterator();
        ItemData itemdata;
        int j;
        for (i++; iterator.hasNext(); i = itemdata.Description.length + (j + 133 + 1) + 4 + 4 + i)
        {
            itemdata = (ItemData)iterator.next();
            j = itemdata.Name.length;
        }

        return i;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleBulkUpdateInventory(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)25);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.TransactionID);
        bytebuffer.put((byte)FolderData_Fields.size());
        FolderData folderdata;
        for (Iterator iterator = FolderData_Fields.iterator(); iterator.hasNext(); packVariable(bytebuffer, folderdata.Name, 1))
        {
            folderdata = (FolderData)iterator.next();
            packUUID(bytebuffer, folderdata.FolderID);
            packUUID(bytebuffer, folderdata.ParentID);
            packByte(bytebuffer, (byte)folderdata.Type);
        }

        bytebuffer.put((byte)ItemData_Fields.size());
        ItemData itemdata;
        for (Iterator iterator1 = ItemData_Fields.iterator(); iterator1.hasNext(); packInt(bytebuffer, itemdata.CRC))
        {
            itemdata = (ItemData)iterator1.next();
            packUUID(bytebuffer, itemdata.ItemID);
            packInt(bytebuffer, itemdata.CallbackID);
            packUUID(bytebuffer, itemdata.FolderID);
            packUUID(bytebuffer, itemdata.CreatorID);
            packUUID(bytebuffer, itemdata.OwnerID);
            packUUID(bytebuffer, itemdata.GroupID);
            packInt(bytebuffer, itemdata.BaseMask);
            packInt(bytebuffer, itemdata.OwnerMask);
            packInt(bytebuffer, itemdata.GroupMask);
            packInt(bytebuffer, itemdata.EveryoneMask);
            packInt(bytebuffer, itemdata.NextOwnerMask);
            packBoolean(bytebuffer, itemdata.GroupOwned);
            packUUID(bytebuffer, itemdata.AssetID);
            packByte(bytebuffer, (byte)itemdata.Type);
            packByte(bytebuffer, (byte)itemdata.InvType);
            packInt(bytebuffer, itemdata.Flags);
            packByte(bytebuffer, (byte)itemdata.SaleType);
            packInt(bytebuffer, itemdata.SalePrice);
            packVariable(bytebuffer, itemdata.Name, 1);
            packVariable(bytebuffer, itemdata.Description, 1);
            packInt(bytebuffer, itemdata.CreationDate);
        }

    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        boolean flag = false;
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.TransactionID = unpackUUID(bytebuffer);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            FolderData folderdata = new FolderData();
            folderdata.FolderID = unpackUUID(bytebuffer);
            folderdata.ParentID = unpackUUID(bytebuffer);
            folderdata.Type = unpackByte(bytebuffer);
            folderdata.Name = unpackVariable(bytebuffer, 1);
            FolderData_Fields.add(folderdata);
        }

        byte0 = bytebuffer.get();
        for (int j = ((flag) ? 1 : 0); j < (byte0 & 0xff); j++)
        {
            ItemData itemdata = new ItemData();
            itemdata.ItemID = unpackUUID(bytebuffer);
            itemdata.CallbackID = unpackInt(bytebuffer);
            itemdata.FolderID = unpackUUID(bytebuffer);
            itemdata.CreatorID = unpackUUID(bytebuffer);
            itemdata.OwnerID = unpackUUID(bytebuffer);
            itemdata.GroupID = unpackUUID(bytebuffer);
            itemdata.BaseMask = unpackInt(bytebuffer);
            itemdata.OwnerMask = unpackInt(bytebuffer);
            itemdata.GroupMask = unpackInt(bytebuffer);
            itemdata.EveryoneMask = unpackInt(bytebuffer);
            itemdata.NextOwnerMask = unpackInt(bytebuffer);
            itemdata.GroupOwned = unpackBoolean(bytebuffer);
            itemdata.AssetID = unpackUUID(bytebuffer);
            itemdata.Type = unpackByte(bytebuffer);
            itemdata.InvType = unpackByte(bytebuffer);
            itemdata.Flags = unpackInt(bytebuffer);
            itemdata.SaleType = unpackByte(bytebuffer) & 0xff;
            itemdata.SalePrice = unpackInt(bytebuffer);
            itemdata.Name = unpackVariable(bytebuffer, 1);
            itemdata.Description = unpackVariable(bytebuffer, 1);
            itemdata.CreationDate = unpackInt(bytebuffer);
            itemdata.CRC = unpackInt(bytebuffer);
            ItemData_Fields.add(itemdata);
        }

    }
}
