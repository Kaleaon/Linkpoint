package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ScriptResetMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var objectId: UUID = UUID(0L, 0L)
    var itemId: UUID = UUID(0L, 0L)


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packUUID(buffer, objectId)
        packUUID(buffer, itemId)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        objectId = unpackUUID(buffer)
        itemId = unpackUUID(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF00F6.toInt()

    override fun getMessageName(): String = "ScriptReset"
}
