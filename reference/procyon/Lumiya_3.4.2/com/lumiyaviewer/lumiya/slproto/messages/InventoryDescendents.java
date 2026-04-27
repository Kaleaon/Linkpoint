// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class InventoryDescendents extends SLMessage
{
    public AgentData AgentData_Field;
    public ArrayList<FolderData> FolderData_Fields;
    public ArrayList<ItemData> ItemData_Fields;
    
    public InventoryDescendents() {
        this.FolderData_Fields = new ArrayList<FolderData>();
        this.ItemData_Fields = new ArrayList<ItemData>();
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
    }
    
    @Override
    public int CalcPayloadSize() {
        final Iterator<Object> iterator = this.FolderData_Fields.iterator();
        int n = 61;
        while (iterator.hasNext()) {
            n += iterator.next().Name.length + 34;
        }
        final Iterator<Object> iterator2 = this.ItemData_Fields.iterator();
        ++n;
        while (iterator2.hasNext()) {
            final ItemData itemData = iterator2.next();
            n += itemData.Description.length + (itemData.Name.length + 129 + 1) + 4 + 4;
        }
        return n;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleInventoryDescendents(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)22);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.FolderID);
        this.packUUID(byteBuffer, this.AgentData_Field.OwnerID);
        this.packInt(byteBuffer, this.AgentData_Field.Version);
        this.packInt(byteBuffer, this.AgentData_Field.Descendents);
        byteBuffer.put((byte)this.FolderData_Fields.size());
        for (final FolderData folderData : this.FolderData_Fields) {
            this.packUUID(byteBuffer, folderData.FolderID);
            this.packUUID(byteBuffer, folderData.ParentID);
            this.packByte(byteBuffer, (byte)folderData.Type);
            this.packVariable(byteBuffer, folderData.Name, 1);
        }
        byteBuffer.put((byte)this.ItemData_Fields.size());
        for (final ItemData itemData : this.ItemData_Fields) {
            this.packUUID(byteBuffer, itemData.ItemID);
            this.packUUID(byteBuffer, itemData.FolderID);
            this.packUUID(byteBuffer, itemData.CreatorID);
            this.packUUID(byteBuffer, itemData.OwnerID);
            this.packUUID(byteBuffer, itemData.GroupID);
            this.packInt(byteBuffer, itemData.BaseMask);
            this.packInt(byteBuffer, itemData.OwnerMask);
            this.packInt(byteBuffer, itemData.GroupMask);
            this.packInt(byteBuffer, itemData.EveryoneMask);
            this.packInt(byteBuffer, itemData.NextOwnerMask);
            this.packBoolean(byteBuffer, itemData.GroupOwned);
            this.packUUID(byteBuffer, itemData.AssetID);
            this.packByte(byteBuffer, (byte)itemData.Type);
            this.packByte(byteBuffer, (byte)itemData.InvType);
            this.packInt(byteBuffer, itemData.Flags);
            this.packByte(byteBuffer, (byte)itemData.SaleType);
            this.packInt(byteBuffer, itemData.SalePrice);
            this.packVariable(byteBuffer, itemData.Name, 1);
            this.packVariable(byteBuffer, itemData.Description, 1);
            this.packInt(byteBuffer, itemData.CreationDate);
            this.packInt(byteBuffer, itemData.CRC);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        final int n = 0;
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.FolderID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.OwnerID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.Version = this.unpackInt(byteBuffer);
        this.AgentData_Field.Descendents = this.unpackInt(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final FolderData e = new FolderData();
            e.FolderID = this.unpackUUID(byteBuffer);
            e.ParentID = this.unpackUUID(byteBuffer);
            e.Type = this.unpackByte(byteBuffer);
            e.Name = this.unpackVariable(byteBuffer, 1);
            this.FolderData_Fields.add(e);
        }
        final byte value2 = byteBuffer.get();
        for (int j = n; j < (value2 & 0xFF); ++j) {
            final ItemData e2 = new ItemData();
            e2.ItemID = this.unpackUUID(byteBuffer);
            e2.FolderID = this.unpackUUID(byteBuffer);
            e2.CreatorID = this.unpackUUID(byteBuffer);
            e2.OwnerID = this.unpackUUID(byteBuffer);
            e2.GroupID = this.unpackUUID(byteBuffer);
            e2.BaseMask = this.unpackInt(byteBuffer);
            e2.OwnerMask = this.unpackInt(byteBuffer);
            e2.GroupMask = this.unpackInt(byteBuffer);
            e2.EveryoneMask = this.unpackInt(byteBuffer);
            e2.NextOwnerMask = this.unpackInt(byteBuffer);
            e2.GroupOwned = this.unpackBoolean(byteBuffer);
            e2.AssetID = this.unpackUUID(byteBuffer);
            e2.Type = this.unpackByte(byteBuffer);
            e2.InvType = this.unpackByte(byteBuffer);
            e2.Flags = this.unpackInt(byteBuffer);
            e2.SaleType = (this.unpackByte(byteBuffer) & 0xFF);
            e2.SalePrice = this.unpackInt(byteBuffer);
            e2.Name = this.unpackVariable(byteBuffer, 1);
            e2.Description = this.unpackVariable(byteBuffer, 1);
            e2.CreationDate = this.unpackInt(byteBuffer);
            e2.CRC = this.unpackInt(byteBuffer);
            this.ItemData_Fields.add(e2);
        }
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public int Descendents;
        public UUID FolderID;
        public UUID OwnerID;
        public int Version;
    }
    
    public static class FolderData
    {
        public UUID FolderID;
        public byte[] Name;
        public UUID ParentID;
        public int Type;
    }
    
    public static class ItemData
    {
        public UUID AssetID;
        public int BaseMask;
        public int CRC;
        public int CreationDate;
        public UUID CreatorID;
        public byte[] Description;
        public int EveryoneMask;
        public int Flags;
        public UUID FolderID;
        public UUID GroupID;
        public int GroupMask;
        public boolean GroupOwned;
        public int InvType;
        public UUID ItemID;
        public byte[] Name;
        public int NextOwnerMask;
        public UUID OwnerID;
        public int OwnerMask;
        public int SalePrice;
        public int SaleType;
        public int Type;
    }
}
