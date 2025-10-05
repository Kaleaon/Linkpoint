package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class LinkInventoryItem : SLMessage() {
    public AgentData AgentData_Field = AgentData()
    public InventoryBlock InventoryBlock_Field = InventoryBlock()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID SessionID
    }

    @JvmStatic
    class InventoryBlock {
        public Int CallbackID
        public Byte[] Description
        public UUID FolderID
        public Int InvType
        public Byte[] Name
        public UUID OldItemID
        public UUID TransactionID
        public Int Type
    }

    public LinkInventoryItem() {
        this.zeroCoded = true
    }

    public Int CalcPayloadSize() {
        return this.InventoryBlock_Field.Name.length + 55 + 1 + this.InventoryBlock_Field.Description.length + 36
    }

    public Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleLinkInventoryItem(this)
    }

    public Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) -86)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packInt(byteBuffer, this.InventoryBlock_Field.CallbackID)
        packUUID(byteBuffer, this.InventoryBlock_Field.FolderID)
        packUUID(byteBuffer, this.InventoryBlock_Field.TransactionID)
        packUUID(byteBuffer, this.InventoryBlock_Field.OldItemID)
        packByte(byteBuffer, (Byte) this.InventoryBlock_Field.Type)
        packByte(byteBuffer, (Byte) this.InventoryBlock_Field.InvType)
        packVariable(byteBuffer, this.InventoryBlock_Field.Name, 1)
        packVariable(byteBuffer, this.InventoryBlock_Field.Description, 1)
    }

    public Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.InventoryBlock_Field.CallbackID = unpackInt(byteBuffer)
        this.InventoryBlock_Field.FolderID = unpackUUID(byteBuffer)
        this.InventoryBlock_Field.TransactionID = unpackUUID(byteBuffer)
        this.InventoryBlock_Field.OldItemID = unpackUUID(byteBuffer)
        this.InventoryBlock_Field.Type = unpackByte(byteBuffer)
        this.InventoryBlock_Field.InvType = unpackByte(byteBuffer)
        this.InventoryBlock_Field.Name = unpackVariable(byteBuffer, 1)
        this.InventoryBlock_Field.Description = unpackVariable(byteBuffer, 1)
    }
}
