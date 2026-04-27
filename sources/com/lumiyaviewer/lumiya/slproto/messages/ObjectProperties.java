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

public class ObjectProperties extends SLMessage
{
    public static class ObjectData
    {

        public int AggregatePermTextures;
        public int AggregatePermTexturesOwner;
        public int AggregatePerms;
        public int BaseMask;
        public int Category;
        public long CreationDate;
        public UUID CreatorID;
        public byte Description[];
        public int EveryoneMask;
        public UUID FolderID;
        public UUID FromTaskID;
        public UUID GroupID;
        public int GroupMask;
        public int InventorySerial;
        public UUID ItemID;
        public UUID LastOwnerID;
        public byte Name[];
        public int NextOwnerMask;
        public UUID ObjectID;
        public UUID OwnerID;
        public int OwnerMask;
        public int OwnershipCost;
        public int SalePrice;
        public int SaleType;
        public byte SitName[];
        public byte TextureID[];
        public byte TouchName[];

        public ObjectData()
        {
        }
    }


    public ArrayList ObjectData_Fields;

    public ObjectProperties()
    {
        ObjectData_Fields = new ArrayList();
        zeroCoded = true;
    }

    public int CalcPayloadSize()
    {
        Iterator iterator = ObjectData_Fields.iterator();
        ObjectData objectdata;
        int i;
        int j;
        int k;
        int l;
        int i1;
        for (i = 3; iterator.hasNext(); i = objectdata.TextureID.length + (j + 175 + 1 + k + 1 + l + 1 + i1 + 1) + i)
        {
            objectdata = (ObjectData)iterator.next();
            j = objectdata.Name.length;
            k = objectdata.Description.length;
            l = objectdata.TouchName.length;
            i1 = objectdata.SitName.length;
        }

        return i;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleObjectProperties(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.put((byte)-1);
        bytebuffer.put((byte)9);
        bytebuffer.put((byte)ObjectData_Fields.size());
        ObjectData objectdata;
        for (Iterator iterator = ObjectData_Fields.iterator(); iterator.hasNext(); packVariable(bytebuffer, objectdata.TextureID, 1))
        {
            objectdata = (ObjectData)iterator.next();
            packUUID(bytebuffer, objectdata.ObjectID);
            packUUID(bytebuffer, objectdata.CreatorID);
            packUUID(bytebuffer, objectdata.OwnerID);
            packUUID(bytebuffer, objectdata.GroupID);
            packLong(bytebuffer, objectdata.CreationDate);
            packInt(bytebuffer, objectdata.BaseMask);
            packInt(bytebuffer, objectdata.OwnerMask);
            packInt(bytebuffer, objectdata.GroupMask);
            packInt(bytebuffer, objectdata.EveryoneMask);
            packInt(bytebuffer, objectdata.NextOwnerMask);
            packInt(bytebuffer, objectdata.OwnershipCost);
            packByte(bytebuffer, (byte)objectdata.SaleType);
            packInt(bytebuffer, objectdata.SalePrice);
            packByte(bytebuffer, (byte)objectdata.AggregatePerms);
            packByte(bytebuffer, (byte)objectdata.AggregatePermTextures);
            packByte(bytebuffer, (byte)objectdata.AggregatePermTexturesOwner);
            packInt(bytebuffer, objectdata.Category);
            packShort(bytebuffer, (short)objectdata.InventorySerial);
            packUUID(bytebuffer, objectdata.ItemID);
            packUUID(bytebuffer, objectdata.FolderID);
            packUUID(bytebuffer, objectdata.FromTaskID);
            packUUID(bytebuffer, objectdata.LastOwnerID);
            packVariable(bytebuffer, objectdata.Name, 1);
            packVariable(bytebuffer, objectdata.Description, 1);
            packVariable(bytebuffer, objectdata.TouchName, 1);
            packVariable(bytebuffer, objectdata.SitName, 1);
        }

    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            ObjectData objectdata = new ObjectData();
            objectdata.ObjectID = unpackUUID(bytebuffer);
            objectdata.CreatorID = unpackUUID(bytebuffer);
            objectdata.OwnerID = unpackUUID(bytebuffer);
            objectdata.GroupID = unpackUUID(bytebuffer);
            objectdata.CreationDate = unpackLong(bytebuffer);
            objectdata.BaseMask = unpackInt(bytebuffer);
            objectdata.OwnerMask = unpackInt(bytebuffer);
            objectdata.GroupMask = unpackInt(bytebuffer);
            objectdata.EveryoneMask = unpackInt(bytebuffer);
            objectdata.NextOwnerMask = unpackInt(bytebuffer);
            objectdata.OwnershipCost = unpackInt(bytebuffer);
            objectdata.SaleType = unpackByte(bytebuffer) & 0xff;
            objectdata.SalePrice = unpackInt(bytebuffer);
            objectdata.AggregatePerms = unpackByte(bytebuffer) & 0xff;
            objectdata.AggregatePermTextures = unpackByte(bytebuffer) & 0xff;
            objectdata.AggregatePermTexturesOwner = unpackByte(bytebuffer) & 0xff;
            objectdata.Category = unpackInt(bytebuffer);
            objectdata.InventorySerial = unpackShort(bytebuffer);
            objectdata.ItemID = unpackUUID(bytebuffer);
            objectdata.FolderID = unpackUUID(bytebuffer);
            objectdata.FromTaskID = unpackUUID(bytebuffer);
            objectdata.LastOwnerID = unpackUUID(bytebuffer);
            objectdata.Name = unpackVariable(bytebuffer, 1);
            objectdata.Description = unpackVariable(bytebuffer, 1);
            objectdata.TouchName = unpackVariable(bytebuffer, 1);
            objectdata.SitName = unpackVariable(bytebuffer, 1);
            objectdata.TextureID = unpackVariable(bytebuffer, 1);
            ObjectData_Fields.add(objectdata);
        }

    }
}
