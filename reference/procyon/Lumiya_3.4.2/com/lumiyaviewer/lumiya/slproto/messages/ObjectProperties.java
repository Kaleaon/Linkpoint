// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class ObjectProperties extends SLMessage
{
    public ArrayList<ObjectData> ObjectData_Fields;
    
    public ObjectProperties() {
        this.ObjectData_Fields = new ArrayList<ObjectData>();
        this.zeroCoded = true;
    }
    
    @Override
    public int CalcPayloadSize() {
        final Iterator<Object> iterator = this.ObjectData_Fields.iterator();
        int n = 3;
        while (iterator.hasNext()) {
            final ObjectData objectData = iterator.next();
            n += objectData.TextureID.length + (objectData.Name.length + 175 + 1 + objectData.Description.length + 1 + objectData.TouchName.length + 1 + objectData.SitName.length + 1);
        }
        return n;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleObjectProperties(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.put((byte)(-1));
        byteBuffer.put((byte)9);
        byteBuffer.put((byte)this.ObjectData_Fields.size());
        for (final ObjectData objectData : this.ObjectData_Fields) {
            this.packUUID(byteBuffer, objectData.ObjectID);
            this.packUUID(byteBuffer, objectData.CreatorID);
            this.packUUID(byteBuffer, objectData.OwnerID);
            this.packUUID(byteBuffer, objectData.GroupID);
            this.packLong(byteBuffer, objectData.CreationDate);
            this.packInt(byteBuffer, objectData.BaseMask);
            this.packInt(byteBuffer, objectData.OwnerMask);
            this.packInt(byteBuffer, objectData.GroupMask);
            this.packInt(byteBuffer, objectData.EveryoneMask);
            this.packInt(byteBuffer, objectData.NextOwnerMask);
            this.packInt(byteBuffer, objectData.OwnershipCost);
            this.packByte(byteBuffer, (byte)objectData.SaleType);
            this.packInt(byteBuffer, objectData.SalePrice);
            this.packByte(byteBuffer, (byte)objectData.AggregatePerms);
            this.packByte(byteBuffer, (byte)objectData.AggregatePermTextures);
            this.packByte(byteBuffer, (byte)objectData.AggregatePermTexturesOwner);
            this.packInt(byteBuffer, objectData.Category);
            this.packShort(byteBuffer, (short)objectData.InventorySerial);
            this.packUUID(byteBuffer, objectData.ItemID);
            this.packUUID(byteBuffer, objectData.FolderID);
            this.packUUID(byteBuffer, objectData.FromTaskID);
            this.packUUID(byteBuffer, objectData.LastOwnerID);
            this.packVariable(byteBuffer, objectData.Name, 1);
            this.packVariable(byteBuffer, objectData.Description, 1);
            this.packVariable(byteBuffer, objectData.TouchName, 1);
            this.packVariable(byteBuffer, objectData.SitName, 1);
            this.packVariable(byteBuffer, objectData.TextureID, 1);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final ObjectData e = new ObjectData();
            e.ObjectID = this.unpackUUID(byteBuffer);
            e.CreatorID = this.unpackUUID(byteBuffer);
            e.OwnerID = this.unpackUUID(byteBuffer);
            e.GroupID = this.unpackUUID(byteBuffer);
            e.CreationDate = this.unpackLong(byteBuffer);
            e.BaseMask = this.unpackInt(byteBuffer);
            e.OwnerMask = this.unpackInt(byteBuffer);
            e.GroupMask = this.unpackInt(byteBuffer);
            e.EveryoneMask = this.unpackInt(byteBuffer);
            e.NextOwnerMask = this.unpackInt(byteBuffer);
            e.OwnershipCost = this.unpackInt(byteBuffer);
            e.SaleType = (this.unpackByte(byteBuffer) & 0xFF);
            e.SalePrice = this.unpackInt(byteBuffer);
            e.AggregatePerms = (this.unpackByte(byteBuffer) & 0xFF);
            e.AggregatePermTextures = (this.unpackByte(byteBuffer) & 0xFF);
            e.AggregatePermTexturesOwner = (this.unpackByte(byteBuffer) & 0xFF);
            e.Category = this.unpackInt(byteBuffer);
            e.InventorySerial = this.unpackShort(byteBuffer);
            e.ItemID = this.unpackUUID(byteBuffer);
            e.FolderID = this.unpackUUID(byteBuffer);
            e.FromTaskID = this.unpackUUID(byteBuffer);
            e.LastOwnerID = this.unpackUUID(byteBuffer);
            e.Name = this.unpackVariable(byteBuffer, 1);
            e.Description = this.unpackVariable(byteBuffer, 1);
            e.TouchName = this.unpackVariable(byteBuffer, 1);
            e.SitName = this.unpackVariable(byteBuffer, 1);
            e.TextureID = this.unpackVariable(byteBuffer, 1);
            this.ObjectData_Fields.add(e);
        }
    }
    
    public static class ObjectData
    {
        public int AggregatePermTextures;
        public int AggregatePermTexturesOwner;
        public int AggregatePerms;
        public int BaseMask;
        public int Category;
        public long CreationDate;
        public UUID CreatorID;
        public byte[] Description;
        public int EveryoneMask;
        public UUID FolderID;
        public UUID FromTaskID;
        public UUID GroupID;
        public int GroupMask;
        public int InventorySerial;
        public UUID ItemID;
        public UUID LastOwnerID;
        public byte[] Name;
        public int NextOwnerMask;
        public UUID ObjectID;
        public UUID OwnerID;
        public int OwnerMask;
        public int OwnershipCost;
        public int SalePrice;
        public int SaleType;
        public byte[] SitName;
        public byte[] TextureID;
        public byte[] TouchName;
    }
}
