package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class RezScript extends SLMessage {
    public AgentData AgentData_Field = new AgentData();
    public InventoryBlock InventoryBlock_Field = new InventoryBlock();
    public UpdateBlock UpdateBlock_Field = new UpdateBlock();

    public static class AgentData {
        public UUID AgentID;
        public UUID GroupID;
        public UUID SessionID;
    }

    public static class InventoryBlock {
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

    public static class UpdateBlock {
        public boolean Enabled;
        public int ObjectLocalID;
    }

    public RezScript() {
        this.zeroCoded = true;
    }

    public int CalcPayloadSize() {
        return this.InventoryBlock_Field.Name.length + 129 + 1 + this.InventoryBlock_Field.Description.length + 4 + 4 + 57;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleRezScript(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 48);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packUUID(byteBuffer, this.AgentData_Field.GroupID);
        packInt(byteBuffer, this.UpdateBlock_Field.ObjectLocalID);
        packBoolean(byteBuffer, this.UpdateBlock_Field.Enabled);
        packUUID(byteBuffer, this.InventoryBlock_Field.ItemID);
        packUUID(byteBuffer, this.InventoryBlock_Field.FolderID);
        packUUID(byteBuffer, this.InventoryBlock_Field.CreatorID);
        packUUID(byteBuffer, this.InventoryBlock_Field.OwnerID);
        packUUID(byteBuffer, this.InventoryBlock_Field.GroupID);
        packInt(byteBuffer, this.InventoryBlock_Field.BaseMask);
        packInt(byteBuffer, this.InventoryBlock_Field.OwnerMask);
        packInt(byteBuffer, this.InventoryBlock_Field.GroupMask);
        packInt(byteBuffer, this.InventoryBlock_Field.EveryoneMask);
        packInt(byteBuffer, this.InventoryBlock_Field.NextOwnerMask);
        packBoolean(byteBuffer, this.InventoryBlock_Field.GroupOwned);
        packUUID(byteBuffer, this.InventoryBlock_Field.TransactionID);
        packByte(byteBuffer, (byte) this.InventoryBlock_Field.Type);
        packByte(byteBuffer, (byte) this.InventoryBlock_Field.InvType);
        packInt(byteBuffer, this.InventoryBlock_Field.Flags);
        packByte(byteBuffer, (byte) this.InventoryBlock_Field.SaleType);
        packInt(byteBuffer, this.InventoryBlock_Field.SalePrice);
        packVariable(byteBuffer, this.InventoryBlock_Field.Name, 1);
        packVariable(byteBuffer, this.InventoryBlock_Field.Description, 1);
        packInt(byteBuffer, this.InventoryBlock_Field.CreationDate);
        packInt(byteBuffer, this.InventoryBlock_Field.CRC);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.AgentData_Field.GroupID = unpackUUID(byteBuffer);
        this.UpdateBlock_Field.ObjectLocalID = unpackInt(byteBuffer);
        this.UpdateBlock_Field.Enabled = unpackBoolean(byteBuffer);
        this.InventoryBlock_Field.ItemID = unpackUUID(byteBuffer);
        this.InventoryBlock_Field.FolderID = unpackUUID(byteBuffer);
        this.InventoryBlock_Field.CreatorID = unpackUUID(byteBuffer);
        this.InventoryBlock_Field.OwnerID = unpackUUID(byteBuffer);
        this.InventoryBlock_Field.GroupID = unpackUUID(byteBuffer);
        this.InventoryBlock_Field.BaseMask = unpackInt(byteBuffer);
        this.InventoryBlock_Field.OwnerMask = unpackInt(byteBuffer);
        this.InventoryBlock_Field.GroupMask = unpackInt(byteBuffer);
        this.InventoryBlock_Field.EveryoneMask = unpackInt(byteBuffer);
        this.InventoryBlock_Field.NextOwnerMask = unpackInt(byteBuffer);
        this.InventoryBlock_Field.GroupOwned = unpackBoolean(byteBuffer);
        this.InventoryBlock_Field.TransactionID = unpackUUID(byteBuffer);
        this.InventoryBlock_Field.Type = unpackByte(byteBuffer);
        this.InventoryBlock_Field.InvType = unpackByte(byteBuffer);
        this.InventoryBlock_Field.Flags = unpackInt(byteBuffer);
        this.InventoryBlock_Field.SaleType = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.InventoryBlock_Field.SalePrice = unpackInt(byteBuffer);
        this.InventoryBlock_Field.Name = unpackVariable(byteBuffer, 1);
        this.InventoryBlock_Field.Description = unpackVariable(byteBuffer, 1);
        this.InventoryBlock_Field.CreationDate = unpackInt(byteBuffer);
        this.InventoryBlock_Field.CRC = unpackInt(byteBuffer);
    }
}
