// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class UpdateTaskInventory extends SLMessage
{
    public AgentData AgentData_Field;
    public InventoryData InventoryData_Field;
    public UpdateData UpdateData_Field;
    
    public UpdateTaskInventory() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.UpdateData_Field = new UpdateData();
        this.InventoryData_Field = new InventoryData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.InventoryData_Field.Name.length + 129 + 1 + this.InventoryData_Field.Description.length + 4 + 4 + 41;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleUpdateTaskInventory(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)30);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packInt(byteBuffer, this.UpdateData_Field.LocalID);
        this.packByte(byteBuffer, (byte)this.UpdateData_Field.Key);
        this.packUUID(byteBuffer, this.InventoryData_Field.ItemID);
        this.packUUID(byteBuffer, this.InventoryData_Field.FolderID);
        this.packUUID(byteBuffer, this.InventoryData_Field.CreatorID);
        this.packUUID(byteBuffer, this.InventoryData_Field.OwnerID);
        this.packUUID(byteBuffer, this.InventoryData_Field.GroupID);
        this.packInt(byteBuffer, this.InventoryData_Field.BaseMask);
        this.packInt(byteBuffer, this.InventoryData_Field.OwnerMask);
        this.packInt(byteBuffer, this.InventoryData_Field.GroupMask);
        this.packInt(byteBuffer, this.InventoryData_Field.EveryoneMask);
        this.packInt(byteBuffer, this.InventoryData_Field.NextOwnerMask);
        this.packBoolean(byteBuffer, this.InventoryData_Field.GroupOwned);
        this.packUUID(byteBuffer, this.InventoryData_Field.TransactionID);
        this.packByte(byteBuffer, (byte)this.InventoryData_Field.Type);
        this.packByte(byteBuffer, (byte)this.InventoryData_Field.InvType);
        this.packInt(byteBuffer, this.InventoryData_Field.Flags);
        this.packByte(byteBuffer, (byte)this.InventoryData_Field.SaleType);
        this.packInt(byteBuffer, this.InventoryData_Field.SalePrice);
        this.packVariable(byteBuffer, this.InventoryData_Field.Name, 1);
        this.packVariable(byteBuffer, this.InventoryData_Field.Description, 1);
        this.packInt(byteBuffer, this.InventoryData_Field.CreationDate);
        this.packInt(byteBuffer, this.InventoryData_Field.CRC);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.UpdateData_Field.LocalID = this.unpackInt(byteBuffer);
        this.UpdateData_Field.Key = (this.unpackByte(byteBuffer) & 0xFF);
        this.InventoryData_Field.ItemID = this.unpackUUID(byteBuffer);
        this.InventoryData_Field.FolderID = this.unpackUUID(byteBuffer);
        this.InventoryData_Field.CreatorID = this.unpackUUID(byteBuffer);
        this.InventoryData_Field.OwnerID = this.unpackUUID(byteBuffer);
        this.InventoryData_Field.GroupID = this.unpackUUID(byteBuffer);
        this.InventoryData_Field.BaseMask = this.unpackInt(byteBuffer);
        this.InventoryData_Field.OwnerMask = this.unpackInt(byteBuffer);
        this.InventoryData_Field.GroupMask = this.unpackInt(byteBuffer);
        this.InventoryData_Field.EveryoneMask = this.unpackInt(byteBuffer);
        this.InventoryData_Field.NextOwnerMask = this.unpackInt(byteBuffer);
        this.InventoryData_Field.GroupOwned = this.unpackBoolean(byteBuffer);
        this.InventoryData_Field.TransactionID = this.unpackUUID(byteBuffer);
        this.InventoryData_Field.Type = this.unpackByte(byteBuffer);
        this.InventoryData_Field.InvType = this.unpackByte(byteBuffer);
        this.InventoryData_Field.Flags = this.unpackInt(byteBuffer);
        this.InventoryData_Field.SaleType = (this.unpackByte(byteBuffer) & 0xFF);
        this.InventoryData_Field.SalePrice = this.unpackInt(byteBuffer);
        this.InventoryData_Field.Name = this.unpackVariable(byteBuffer, 1);
        this.InventoryData_Field.Description = this.unpackVariable(byteBuffer, 1);
        this.InventoryData_Field.CreationDate = this.unpackInt(byteBuffer);
        this.InventoryData_Field.CRC = this.unpackInt(byteBuffer);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID SessionID;
    }
    
    public static class InventoryData
    {
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
        public UUID TransactionID;
        public int Type;
    }
    
    public static class UpdateData
    {
        public int Key;
        public int LocalID;
    }
}
