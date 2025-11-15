package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class EventGodDeleteMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var eventId: Int = 0
    var queryId: UUID = UUID(0L, 0L)
    var queryText: ByteArray = ByteArray(0)
    var queryFlags: Int = 0
    var queryStart: Int = 0


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packInt(buffer, eventId)
        packUUID(buffer, queryId)
        packVariable(buffer, queryText, 1)
        packInt(buffer, queryFlags)
        packInt(buffer, queryStart)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        eventId = unpackInt(buffer)
        queryId = unpackUUID(buffer)
        queryText = unpackVariable(buffer, 1)
        queryFlags = unpackInt(buffer)
        queryStart = unpackInt(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF00B7

    override fun getMessageName(): String = "EventGodDelete"
}
