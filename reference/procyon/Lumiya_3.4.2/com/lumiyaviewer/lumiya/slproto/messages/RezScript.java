// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class RezScript extends SLMessage
{
    public AgentData AgentData_Field;
    public InventoryBlock InventoryBlock_Field;
    public UpdateBlock UpdateBlock_Field;
    
    public RezScript() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.UpdateBlock_Field = new UpdateBlock();
        this.InventoryBlock_Field = new InventoryBlock();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.InventoryBlock_Field.Name.length + 129 + 1 + this.InventoryBlock_Field.Description.length + 4 + 4 + 57;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleRezScript(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)48);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packUUID(byteBuffer, this.AgentData_Field.GroupID);
        this.packInt(byteBuffer, this.UpdateBlock_Field.ObjectLocalID);
        this.packBoolean(byteBuffer, this.UpdateBlock_Field.Enabled);
        this.packUUID(byteBuffer, this.InventoryBlock_Field.ItemID);
        this.packUUID(byteBuffer, this.InventoryBlock_Field.FolderID);
        this.packUUID(byteBuffer, this.InventoryBlock_Field.CreatorID);
        this.packUUID(byteBuffer, this.InventoryBlock_Field.OwnerID);
        this.packUUID(byteBuffer, this.InventoryBlock_Field.GroupID);
        this.packInt(byteBuffer, this.InventoryBlock_Field.BaseMask);
        this.packInt(byteBuffer, this.InventoryBlock_Field.OwnerMask);
        this.packInt(byteBuffer, this.InventoryBlock_Field.GroupMask);
        this.packInt(byteBuffer, this.InventoryBlock_Field.EveryoneMask);
        this.packInt(byteBuffer, this.InventoryBlock_Field.NextOwnerMask);
        this.packBoolean(byteBuffer, this.InventoryBlock_Field.GroupOwned);
        this.packUUID(byteBuffer, this.InventoryBlock_Field.TransactionID);
        this.packByte(byteBuffer, (byte)this.InventoryBlock_Field.Type);
        this.packByte(byteBuffer, (byte)this.InventoryBlock_Field.InvType);
        this.packInt(byteBuffer, this.InventoryBlock_Field.Flags);
        this.packByte(byteBuffer, (byte)this.InventoryBlock_Field.SaleType);
        this.packInt(byteBuffer, this.InventoryBlock_Field.SalePrice);
        this.packVariable(byteBuffer, this.InventoryBlock_Field.Name, 1);
        this.packVariable(byteBuffer, this.InventoryBlock_Field.Description, 1);
        this.packInt(byteBuffer, this.InventoryBlock_Field.CreationDate);
        this.packInt(byteBuffer, this.InventoryBlock_Field.CRC);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.GroupID = this.unpackUUID(byteBuffer);
        this.UpdateBlock_Field.ObjectLocalID = this.unpackInt(byteBuffer);
        this.UpdateBlock_Field.Enabled = this.unpackBoolean(byteBuffer);
        this.InventoryBlock_Field.ItemID = this.unpackUUID(byteBuffer);
        this.InventoryBlock_Field.FolderID = this.unpackUUID(byteBuffer);
        this.InventoryBlock_Field.CreatorID = this.unpackUUID(byteBuffer);
        this.InventoryBlock_Field.OwnerID = this.unpackUUID(byteBuffer);
        this.InventoryBlock_Field.GroupID = this.unpackUUID(byteBuffer);
        this.InventoryBlock_Field.BaseMask = this.unpackInt(byteBuffer);
        this.InventoryBlock_Field.OwnerMask = this.unpackInt(byteBuffer);
        this.InventoryBlock_Field.GroupMask = this.unpackInt(byteBuffer);
        this.InventoryBlock_Field.EveryoneMask = this.unpackInt(byteBuffer);
        this.InventoryBlock_Field.NextOwnerMask = this.unpackInt(byteBuffer);
        this.InventoryBlock_Field.GroupOwned = this.unpackBoolean(byteBuffer);
        this.InventoryBlock_Field.TransactionID = this.unpackUUID(byteBuffer);
        this.InventoryBlock_Field.Type = this.unpackByte(byteBuffer);
        this.InventoryBlock_Field.InvType = this.unpackByte(byteBuffer);
        this.InventoryBlock_Field.Flags = this.unpackInt(byteBuffer);
        this.InventoryBlock_Field.SaleType = (this.unpackByte(byteBuffer) & 0xFF);
        this.InventoryBlock_Field.SalePrice = this.unpackInt(byteBuffer);
        this.InventoryBlock_Field.Name = this.unpackVariable(byteBuffer, 1);
        this.InventoryBlock_Field.Description = this.unpackVariable(byteBuffer, 1);
        this.InventoryBlock_Field.CreationDate = this.unpackInt(byteBuffer);
        this.InventoryBlock_Field.CRC = this.unpackInt(byteBuffer);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID GroupID;
        public UUID SessionID;
    }
    
    public static class InventoryBlock
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
    
    public static class UpdateBlock
    {
        public boolean Enabled;
        public int ObjectLocalID;
    }
}
