package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class RemoveInventoryItemMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    val inventoryData: MutableList<InventoryDataBlock> = mutableListOf()

    data class InventoryDataBlock(
        var itemId: UUID = UUID(0L, 0L)
    )


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        require(inventoryData.size <= 0xFF) { "InventoryData size exceeds 255 (" + inventoryData.size + ")" }
        packByte(buffer, inventoryData.size)
        inventoryData.forEach { entry ->
            packUUID(buffer, entry.itemId)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        run {
            val count = unpackByte(buffer)
            inventoryData.clear()
            repeat(count) {
                val entry = InventoryDataBlock()
                entry.itemId = unpackUUID(buffer)
                inventoryData += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF010E.toInt()

    override fun getMessageName(): String = "RemoveInventoryItem"
}
