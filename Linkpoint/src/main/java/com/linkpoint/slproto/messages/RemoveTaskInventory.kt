package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class RemoveTaskInventory : SLMessage() {
    val AgentData_Field = AgentData()
    val InventoryData_Field = InventoryData()

    class AgentData {
        var AgentID: UUID? = null
        var SessionID: UUID? = null
    }

    class InventoryData {
        var LocalID: Int = 0
        var ItemID: UUID? = null
    }

    init {
        zeroCoded = true
    }

    override fun CalcPayloadSize(): Int = 56

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleRemoveTaskInventory(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(1.toByte())
        buffer.put(31.toByte())
        packUUID(buffer, AgentData_Field.AgentID)
        packUUID(buffer, AgentData_Field.SessionID)
        packInt(buffer, InventoryData_Field.LocalID)
        packUUID(buffer, InventoryData_Field.ItemID)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        AgentData_Field.AgentID = unpackUUID(buffer)
        AgentData_Field.SessionID = unpackUUID(buffer)
        InventoryData_Field.LocalID = unpackInt(buffer)
        InventoryData_Field.ItemID = unpackUUID(buffer)
    }
}