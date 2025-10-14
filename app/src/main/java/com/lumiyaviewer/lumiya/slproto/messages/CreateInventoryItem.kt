package com.lumiyaviewer.lumiya.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class CreateInventoryItem : SLMessage {
    AgentData AgentData_Field = AgentData()
    InventoryBlock InventoryBlock_Field = InventoryBlock()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class InventoryBlock {
        Int CallbackID
        byte[] Description
        UUID FolderID
        Int InvType
        byte[] Name
        Int NextOwnerMask
        UUID TransactionID
        Int Type
        Int WearableType
    }

    CreateInventoryItem() {
        this.zeroCoded = true
    }

    Int CalcPayloadSize() {
        return this.InventoryBlock_Field.Name.length + 44 + 1 + this.InventoryBlock_Field.Description.length + 36
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleCreateInventoryItem(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) 49)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packInt(byteBuffer, this.InventoryBlock_Field.CallbackID)
        packUUID(byteBuffer, this.InventoryBlock_Field.FolderID)
        packUUID(byteBuffer, this.InventoryBlock_Field.TransactionID)
        packInt(byteBuffer, this.InventoryBlock_Field.NextOwnerMask)
        packByte(byteBuffer, (byte) this.InventoryBlock_Field.Type)
        packByte(byteBuffer, (byte) this.InventoryBlock_Field.InvType)
        packByte(byteBuffer, (byte) this.InventoryBlock_Field.WearableType)
        packVariable(byteBuffer, this.InventoryBlock_Field.Name, 1)
        packVariable(byteBuffer, this.InventoryBlock_Field.Description, 1)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
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
