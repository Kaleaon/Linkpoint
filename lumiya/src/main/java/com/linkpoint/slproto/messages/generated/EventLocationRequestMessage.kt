package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class EventLocationRequestMessage : SLMessage() {
    var queryId: UUID = UUID(0L, 0L)
    var eventId: Int = 0


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, queryId)
        packInt(buffer, eventId)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        queryId = unpackUUID(buffer)
        eventId = unpackInt(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF0133

    override fun getMessageName(): String = "EventLocationRequest"
}
