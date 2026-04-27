package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class LinkInventoryItem extends SLMessage {
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
        public UUID OldItemID;
        public UUID TransactionID;
        public int Type;
    }

    public LinkInventoryItem() {
        this.zeroCoded = true;
    }

    public int CalcPayloadSize() {
        return this.InventoryBlock_Field.Name.length + 55 + 1 + this.InventoryBlock_Field.Description.length + 36;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleLinkInventoryItem(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) -86);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packInt(byteBuffer, this.InventoryBlock_Field.CallbackID);
        packUUID(byteBuffer, this.InventoryBlock_Field.FolderID);
        packUUID(byteBuffer, this.InventoryBlock_Field.TransactionID);
        packUUID(byteBuffer, this.InventoryBlock_Field.OldItemID);
        packByte(byteBuffer, (byte) this.InventoryBlock_Field.Type);
        packByte(byteBuffer, (byte) this.InventoryBlock_Field.InvType);
        packVariable(byteBuffer, this.InventoryBlock_Field.Name, 1);
        packVariable(byteBuffer, this.InventoryBlock_Field.Description, 1);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.InventoryBlock_Field.CallbackID = unpackInt(byteBuffer);
        this.InventoryBlock_Field.FolderID = unpackUUID(byteBuffer);
        this.InventoryBlock_Field.TransactionID = unpackUUID(byteBuffer);
        this.InventoryBlock_Field.OldItemID = unpackUUID(byteBuffer);
        this.InventoryBlock_Field.Type = unpackByte(byteBuffer);
        this.InventoryBlock_Field.InvType = unpackByte(byteBuffer);
        this.InventoryBlock_Field.Name = unpackVariable(byteBuffer, 1);
        this.InventoryBlock_Field.Description = unpackVariable(byteBuffer, 1);
    }
}
