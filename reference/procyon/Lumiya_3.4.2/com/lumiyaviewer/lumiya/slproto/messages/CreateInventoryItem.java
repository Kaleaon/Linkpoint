// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class CreateInventoryItem extends SLMessage
{
    public AgentData AgentData_Field;
    public InventoryBlock InventoryBlock_Field;
    
    public CreateInventoryItem() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.InventoryBlock_Field = new InventoryBlock();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.InventoryBlock_Field.Name.length + 44 + 1 + this.InventoryBlock_Field.Description.length + 36;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleCreateInventoryItem(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)49);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packInt(byteBuffer, this.InventoryBlock_Field.CallbackID);
        this.packUUID(byteBuffer, this.InventoryBlock_Field.FolderID);
        this.packUUID(byteBuffer, this.InventoryBlock_Field.TransactionID);
        this.packInt(byteBuffer, this.InventoryBlock_Field.NextOwnerMask);
        this.packByte(byteBuffer, (byte)this.InventoryBlock_Field.Type);
        this.packByte(byteBuffer, (byte)this.InventoryBlock_Field.InvType);
        this.packByte(byteBuffer, (byte)this.InventoryBlock_Field.WearableType);
        this.packVariable(byteBuffer, this.InventoryBlock_Field.Name, 1);
        this.packVariable(byteBuffer, this.InventoryBlock_Field.Description, 1);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.InventoryBlock_Field.CallbackID = this.unpackInt(byteBuffer);
        this.InventoryBlock_Field.FolderID = this.unpackUUID(byteBuffer);
        this.InventoryBlock_Field.TransactionID = this.unpackUUID(byteBuffer);
        this.InventoryBlock_Field.NextOwnerMask = this.unpackInt(byteBuffer);
        this.InventoryBlock_Field.Type = this.unpackByte(byteBuffer);
        this.InventoryBlock_Field.InvType = this.unpackByte(byteBuffer);
        this.InventoryBlock_Field.WearableType = (this.unpackByte(byteBuffer) & 0xFF);
        this.InventoryBlock_Field.Name = this.unpackVariable(byteBuffer, 1);
        this.InventoryBlock_Field.Description = this.unpackVariable(byteBuffer, 1);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID SessionID;
    }
    
    public static class InventoryBlock
    {
        public int CallbackID;
        public byte[] Description;
        public UUID FolderID;
        public int InvType;
        public byte[] Name;
        public int NextOwnerMask;
        public UUID TransactionID;
        public int Type;
        public int WearableType;
    }
}
