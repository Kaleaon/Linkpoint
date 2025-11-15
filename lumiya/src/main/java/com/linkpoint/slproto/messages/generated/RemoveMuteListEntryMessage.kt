package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class RemoveMuteListEntryMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var muteId: UUID = UUID(0L, 0L)
    var muteName: ByteArray = ByteArray(0)


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packUUID(buffer, muteId)
        packVariable(buffer, muteName, 1)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        muteId = unpackUUID(buffer)
        muteName = unpackVariable(buffer, 1)
    }

    override fun getMessageID(): Int = 0xFFFF0108

    override fun getMessageName(): String = "RemoveMuteListEntry"
}
