package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class CreateInventoryItem extends SLMessage {
    public AgentData AgentData_Field = new AgentData();
    public InventoryBlock InventoryBlock_Field = new InventoryBlock();

    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    public static class InventoryBlock {
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

    public CreateInventoryItem() {
        this.zeroCoded = true;
    }

    public int CalcPayloadSize() {
        return this.InventoryBlock_Field.Name.length + 44 + 1 + this.InventoryBlock_Field.Description.length + 36;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleCreateInventoryItem(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 49);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packInt(byteBuffer, this.InventoryBlock_Field.CallbackID);
        packUUID(byteBuffer, this.InventoryBlock_Field.FolderID);
        packUUID(byteBuffer, this.InventoryBlock_Field.TransactionID);
        packInt(byteBuffer, this.InventoryBlock_Field.NextOwnerMask);
        packByte(byteBuffer, (byte) this.InventoryBlock_Field.Type);
        packByte(byteBuffer, (byte) this.InventoryBlock_Field.InvType);
        packByte(byteBuffer, (byte) this.InventoryBlock_Field.WearableType);
        packVariable(byteBuffer, this.InventoryBlock_Field.Name, 1);
        packVariable(byteBuffer, this.InventoryBlock_Field.Description, 1);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.InventoryBlock_Field.CallbackID = unpackInt(byteBuffer);
        this.InventoryBlock_Field.FolderID = unpackUUID(byteBuffer);
        this.InventoryBlock_Field.TransactionID = unpackUUID(byteBuffer);
        this.InventoryBlock_Field.NextOwnerMask = unpackInt(byteBuffer);
        this.InventoryBlock_Field.Type = unpackByte(byteBuffer);
        this.InventoryBlock_Field.InvType = unpackByte(byteBuffer);
        this.InventoryBlock_Field.WearableType = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.InventoryBlock_Field.Name = unpackVariable(byteBuffer, 1);
        this.InventoryBlock_Field.Description = unpackVariable(byteBuffer, 1);
    }
}
