package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class RemoveTaskInventory : SLMessage {
    AgentData AgentData_Field = AgentData()
    InventoryData InventoryData_Field = InventoryData()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class InventoryData {
        UUID ItemID
        Int LocalID
    }

    RemoveTaskInventory() {
        this.zeroCoded = true
    }

    fun CalcPayloadSize(): Int {
        return 56
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleRemoveTaskInventory(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 31)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packInt(byteBuffer, this.InventoryData_Field.LocalID)
        packUUID(byteBuffer, this.InventoryData_Field.ItemID)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.InventoryData_Field.LocalID = unpackInt(byteBuffer)
        this.InventoryData_Field.ItemID = unpackUUID(byteBuffer)
    }
}
