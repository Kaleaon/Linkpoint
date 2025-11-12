package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class MapNameRequestMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var flags: Int = 0
    var estateId: Int = 0
    var godlike: Boolean = false
    var name: ByteArray = ByteArray(0)


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packInt(buffer, flags)
        packInt(buffer, estateId)
        packBoolean(buffer, godlike)
        packVariable(buffer, name, 1)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        flags = unpackInt(buffer)
        estateId = unpackInt(buffer)
        godlike = unpackBoolean(buffer)
        name = unpackVariable(buffer, 1)
    }

    override fun getMessageID(): Int = 0xFFFF0198

    override fun getMessageName(): String = "MapNameRequest"
}
