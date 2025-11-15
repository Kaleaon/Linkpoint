package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class SaveAssetIntoInventoryMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var itemId: UUID = UUID(0L, 0L)
    var newAssetId: UUID = UUID(0L, 0L)


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, itemId)
        packUUID(buffer, newAssetId)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        itemId = unpackUUID(buffer)
        newAssetId = unpackUUID(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF0110

    override fun getMessageName(): String = "SaveAssetIntoInventory"
}
