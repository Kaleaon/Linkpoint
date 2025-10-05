package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class CreateInventoryItem : SLMessage() {
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
        public Int NextOwnerMask
        public UUID TransactionID
        public Int Type
        public Int WearableType
    }

    public CreateInventoryItem() {
        this.zeroCoded = true
    }

    public Int CalcPayloadSize() {
        return this.InventoryBlock_Field.Name.length + 44 + 1 + this.InventoryBlock_Field.Description.length + 36
    }

    public Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleCreateInventoryItem(this)
    }

    public Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 49)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packInt(byteBuffer, this.InventoryBlock_Field.CallbackID)
        packUUID(byteBuffer, this.InventoryBlock_Field.FolderID)
        packUUID(byteBuffer, this.InventoryBlock_Field.TransactionID)
        packInt(byteBuffer, this.InventoryBlock_Field.NextOwnerMask)
        packByte(byteBuffer, (Byte) this.InventoryBlock_Field.Type)
        packByte(byteBuffer, (Byte) this.InventoryBlock_Field.InvType)
        packByte(byteBuffer, (Byte) this.InventoryBlock_Field.WearableType)
        packVariable(byteBuffer, this.InventoryBlock_Field.Name, 1)
        packVariable(byteBuffer, this.InventoryBlock_Field.Description, 1)
    }

    public Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.InventoryBlock_Field.CallbackID = unpackInt(byteBuffer)
        this.InventoryBlock_Field.FolderID = unpackUUID(byteBuffer)
        this.InventoryBlock_Field.TransactionID = unpackUUID(byteBuffer)
        this.InventoryBlock_Field.NextOwnerMask = unpackInt(byteBuffer)
        this.InventoryBlock_Field.Type = unpackByte(byteBuffer)
        this.InventoryBlock_Field.InvType = unpackByte(byteBuffer)
        this.InventoryBlock_Field.WearableType = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.InventoryBlock_Field.Name = unpackVariable(byteBuffer, 1)
        this.InventoryBlock_Field.Description = unpackVariable(byteBuffer, 1)
    }
}
