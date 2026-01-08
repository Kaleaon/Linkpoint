package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class MoveTaskInventory : SLMessage {
    AgentData AgentData_Field = AgentData()
    InventoryData InventoryData_Field = InventoryData()

    class AgentData {
        UUID AgentID
        UUID FolderID
        UUID SessionID
    }

    class InventoryData {
        UUID ItemID
        Int LocalID
    }

    MoveTaskInventory() {
        this.zeroCoded = false
    }

    Int CalcPayloadSize() {
        return 72
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleMoveTaskInventory(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 32)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.AgentData_Field.FolderID)
        packInt(byteBuffer, this.InventoryData_Field.LocalID)
        packUUID(byteBuffer, this.InventoryData_Field.ItemID)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.AgentData_Field.FolderID = unpackUUID(byteBuffer)
        this.InventoryData_Field.LocalID = unpackInt(byteBuffer)
        this.InventoryData_Field.ItemID = unpackUUID(byteBuffer)
    }
}
