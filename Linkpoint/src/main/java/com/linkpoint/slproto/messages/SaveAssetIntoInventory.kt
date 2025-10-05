package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class SaveAssetIntoInventory : SLMessage() {
    val AgentData_Field = AgentData()
    val InventoryData_Field = InventoryData()

    class AgentData {
        var AgentID: UUID? = null
    }

    class InventoryData {
        var ItemID: UUID? = null
        var NewAssetID: UUID? = null
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int = 52

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleSaveAssetIntoInventory(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(1.toByte())
        buffer.put(16.toByte())
        packUUID(buffer, AgentData_Field.AgentID)
        packUUID(buffer, InventoryData_Field.ItemID)
        packUUID(buffer, InventoryData_Field.NewAssetID)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        AgentData_Field.AgentID = unpackUUID(buffer)
        InventoryData_Field.ItemID = unpackUUID(buffer)
        InventoryData_Field.NewAssetID = unpackUUID(buffer)
    }
}