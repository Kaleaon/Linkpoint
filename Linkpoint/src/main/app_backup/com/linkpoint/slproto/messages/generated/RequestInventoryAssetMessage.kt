package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class RequestInventoryAssetMessage : SLMessage() {
    var queryId: UUID = UUID(0L, 0L)
    var agentId: UUID = UUID(0L, 0L)
    var ownerId: UUID = UUID(0L, 0L)
    var itemId: UUID = UUID(0L, 0L)


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, queryId)
        packUUID(buffer, agentId)
        packUUID(buffer, ownerId)
        packUUID(buffer, itemId)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        queryId = unpackUUID(buffer)
        agentId = unpackUUID(buffer)
        ownerId = unpackUUID(buffer)
        itemId = unpackUUID(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF011A.toInt()

    override fun getMessageName(): String = "RequestInventoryAsset"
}
