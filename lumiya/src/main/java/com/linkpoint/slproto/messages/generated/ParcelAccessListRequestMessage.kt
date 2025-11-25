package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ParcelAccessListRequestMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var sequenceId: Int = 0
    var flags: Int = 0
    var localId: Int = 0


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packInt(buffer, sequenceId)
        packInt(buffer, flags)
        packInt(buffer, localId)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        sequenceId = unpackInt(buffer)
        flags = unpackInt(buffer)
        localId = unpackInt(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF00D7.toInt()

    override fun getMessageName(): String = "ParcelAccessListRequest"
}
