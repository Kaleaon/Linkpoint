package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ParcelDwellRequestMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var localId: Int = 0
    var parcelId: UUID = UUID(0L, 0L)


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packInt(buffer, localId)
        packUUID(buffer, parcelId)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        localId = unpackInt(buffer)
        parcelId = unpackUUID(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF00DA

    override fun getMessageName(): String = "ParcelDwellRequest"
}
