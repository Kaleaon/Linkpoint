package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class PickGodDeleteMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var pickId: UUID = UUID(0L, 0L)
    var queryId: UUID = UUID(0L, 0L)


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packUUID(buffer, pickId)
        packUUID(buffer, queryId)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        pickId = unpackUUID(buffer)
        queryId = unpackUUID(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF00BB

    override fun getMessageName(): String = "PickGodDelete"
}
