package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class RequestObjectPropertiesFamilyMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var requestFlags: Int = 0
    var objectId: UUID = UUID(0L, 0L)


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packInt(buffer, requestFlags)
        packUUID(buffer, objectId)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        requestFlags = unpackInt(buffer)
        objectId = unpackUUID(buffer)
    }

    override fun getMessageID(): Int = 0x0000FF05

    override fun getMessageName(): String = "RequestObjectPropertiesFamily"
}
