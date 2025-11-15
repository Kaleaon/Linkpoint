package com.lumiyaviewer.lumiya.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class CopyInventoryFromNotecard : SLMessage {
    AgentData AgentData_Field
    ArrayList<InventoryData> InventoryData_Fields = ArrayList<>()
    NotecardData NotecardData_Field

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class InventoryData {
        UUID FolderID
        UUID ItemID
    }

    class NotecardData {
        UUID NotecardItemID
        UUID ObjectID
    }

    CopyInventoryFromNotecard() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
        this.NotecardData_Field = NotecardData()
    }

    Int CalcPayloadSize() {
        return (this.InventoryData_Fields.size() * 32) + 69
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleCopyInventoryFromNotecard(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) 9)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.NotecardData_Field.NotecardItemID)
        packUUID(byteBuffer, this.NotecardData_Field.ObjectID)
        byteBuffer.put((byte) this.InventoryData_Fields.size())
        for (InventoryData inventoryData : this.InventoryData_Fields) {
            packUUID(byteBuffer, inventoryData.ItemID)
            packUUID(byteBuffer, inventoryData.FolderID)
        }
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.NotecardData_Field.NotecardItemID = unpackUUID(byteBuffer)
        this.NotecardData_Field.ObjectID = unpackUUID(byteBuffer)
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            InventoryData inventoryData = InventoryData()
            inventoryData.ItemID = unpackUUID(byteBuffer)
            inventoryData.FolderID = unpackUUID(byteBuffer)
            this.InventoryData_Fields.add(inventoryData)
        }
    }
}
