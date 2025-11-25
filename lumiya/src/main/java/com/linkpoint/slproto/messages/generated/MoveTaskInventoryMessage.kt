package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class MoveTaskInventoryMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var folderId: UUID = UUID(0L, 0L)
    var localId: Int = 0
    var itemId: UUID = UUID(0L, 0L)


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packUUID(buffer, folderId)
        packInt(buffer, localId)
        packUUID(buffer, itemId)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        folderId = unpackUUID(buffer)
        localId = unpackInt(buffer)
        itemId = unpackUUID(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF0120.toInt()

    override fun getMessageName(): String = "MoveTaskInventory"
}
