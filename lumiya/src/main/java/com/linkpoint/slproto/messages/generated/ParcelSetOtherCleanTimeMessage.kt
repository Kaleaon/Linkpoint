package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ParcelSetOtherCleanTimeMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var localId: Int = 0
    var otherCleanTime: Int = 0


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packInt(buffer, localId)
        packInt(buffer, otherCleanTime)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        localId = unpackInt(buffer)
        otherCleanTime = unpackInt(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF00C8

    override fun getMessageName(): String = "ParcelSetOtherCleanTime"
}
