package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
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

    fun CalcPayloadSize(): Int {
        return 72
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleMoveTaskInventory(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 32)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.AgentData_Field.FolderID)
        packInt(byteBuffer, this.InventoryData_Field.LocalID)
        packUUID(byteBuffer, this.InventoryData_Field.ItemID)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.AgentData_Field.FolderID = unpackUUID(byteBuffer)
        this.InventoryData_Field.LocalID = unpackInt(byteBuffer)
        this.InventoryData_Field.ItemID = unpackUUID(byteBuffer)
    }
}
