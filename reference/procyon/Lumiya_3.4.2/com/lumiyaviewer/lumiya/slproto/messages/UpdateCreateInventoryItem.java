// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class UpdateCreateInventoryItem extends SLMessage
{
    public AgentData AgentData_Field;
    public ArrayList<InventoryData> InventoryData_Fields;
    
    public UpdateCreateInventoryItem() {
        this.InventoryData_Fields = new ArrayList<InventoryData>();
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
    }
    
    @Override
    public int CalcPayloadSize() {
        final Iterator<Object> iterator = this.InventoryData_Fields.iterator();
        int n = 38;
        while (iterator.hasNext()) {
            final InventoryData inventoryData = iterator.next();
            n += inventoryData.Description.length + (inventoryData.Name.length + 133 + 1) + 4 + 4;
        }
        return n;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleUpdateCreateInventoryItem(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)11);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packBoolean(byteBuffer, this.AgentData_Field.SimApproved);
        this.packUUID(byteBuffer, this.AgentData_Field.TransactionID);
        byteBuffer.put((byte)this.InventoryData_Fields.size());
        for (final InventoryData inventoryData : this.InventoryData_Fields) {
            this.packUUID(byteBuffer, inventoryData.ItemID);
            this.packUUID(byteBuffer, inventoryData.FolderID);
            this.packInt(byteBuffer, inventoryData.CallbackID);
            this.packUUID(byteBuffer, inventoryData.CreatorID);
            this.packUUID(byteBuffer, inventoryData.OwnerID);
            this.packUUID(byteBuffer, inventoryData.GroupID);
            this.packInt(byteBuffer, inventoryData.BaseMask);
            this.packInt(byteBuffer, inventoryData.OwnerMask);
            this.packInt(byteBuffer, inventoryData.GroupMask);
            this.packInt(byteBuffer, inventoryData.EveryoneMask);
            this.packInt(byteBuffer, inventoryData.NextOwnerMask);
            this.packBoolean(byteBuffer, inventoryData.GroupOwned);
            this.packUUID(byteBuffer, inventoryData.AssetID);
            this.packByte(byteBuffer, (byte)inventoryData.Type);
            this.packByte(byteBuffer, (byte)inventoryData.InvType);
            this.packInt(byteBuffer, inventoryData.Flags);
            this.packByte(byteBuffer, (byte)inventoryData.SaleType);
            this.packInt(byteBuffer, inventoryData.SalePrice);
            this.packVariable(byteBuffer, inventoryData.Name, 1);
            this.packVariable(byteBuffer, inventoryData.Description, 1);
            this.packInt(byteBuffer, inventoryData.CreationDate);
            this.packInt(byteBuffer, inventoryData.CRC);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SimApproved = this.unpackBoolean(byteBuffer);
        this.AgentData_Field.TransactionID = this.unpackUUID(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final InventoryData e = new InventoryData();
            e.ItemID = this.unpackUUID(byteBuffer);
            e.FolderID = this.unpackUUID(byteBuffer);
            e.CallbackID = this.unpackInt(byteBuffer);
            e.CreatorID = this.unpackUUID(byteBuffer);
            e.OwnerID = this.unpackUUID(byteBuffer);
            e.GroupID = this.unpackUUID(byteBuffer);
            e.BaseMask = this.unpackInt(byteBuffer);
            e.OwnerMask = this.unpackInt(byteBuffer);
            e.GroupMask = this.unpackInt(byteBuffer);
            e.EveryoneMask = this.unpackInt(byteBuffer);
            e.NextOwnerMask = this.unpackInt(byteBuffer);
            e.GroupOwned = this.unpackBoolean(byteBuffer);
            e.AssetID = this.unpackUUID(byteBuffer);
            e.Type = this.unpackByte(byteBuffer);
            e.InvType = this.unpackByte(byteBuffer);
            e.Flags = this.unpackInt(byteBuffer);
            e.SaleType = (this.unpackByte(byteBuffer) & 0xFF);
            e.SalePrice = this.unpackInt(byteBuffer);
            e.Name = this.unpackVariable(byteBuffer, 1);
            e.Description = this.unpackVariable(byteBuffer, 1);
            e.CreationDate = this.unpackInt(byteBuffer);
            e.CRC = this.unpackInt(byteBuffer);
            this.InventoryData_Fields.add(e);
        }
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public boolean SimApproved;
        public UUID TransactionID;
    }
    
    public static class InventoryData
    {
        public UUID AssetID;
        public int BaseMask;
        public int CRC;
        public int CallbackID;
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
