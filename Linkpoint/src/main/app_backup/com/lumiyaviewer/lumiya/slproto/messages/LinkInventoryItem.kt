package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class LinkInventoryItem : SLMessage {
    AgentData AgentData_Field = AgentData()
    InventoryBlock InventoryBlock_Field = InventoryBlock()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class InventoryBlock {
        Int CallbackID
        ByteArray Description
        UUID FolderID
        Int InvType
        ByteArray Name
        UUID OldItemID
        UUID TransactionID
        Int Type
    }

    LinkInventoryItem() {
        this.zeroCoded = true
    }

    Int CalcPayloadSize() {
        return this.InventoryBlock_Field.Name.length + 55 + 1 + this.InventoryBlock_Field.Description.length + 36
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleLinkInventoryItem(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
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

    Unit UnpackPayload(ByteBuffer byteBuffer) {
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
