package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class AvatarPickerRequestMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var queryId: UUID = UUID(0L, 0L)
    var name: ByteArray = ByteArray(0)


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packUUID(buffer, queryId)
        packVariable(buffer, name, 1)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        queryId = unpackUUID(buffer)
        name = unpackVariable(buffer, 1)
    }

    override fun getMessageID(): Int = 0xFFFF001A

    override fun getMessageName(): String = "AvatarPickerRequest"
}
