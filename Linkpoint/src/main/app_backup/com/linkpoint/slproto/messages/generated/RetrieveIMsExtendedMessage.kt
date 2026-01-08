package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class RetrieveIMsExtendedMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var isPremium: Boolean = false


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packBoolean(buffer, isPremium)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        isPremium = unpackBoolean(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF01AB.toInt()

    override fun getMessageName(): String = "RetrieveIMsExtended"
}
