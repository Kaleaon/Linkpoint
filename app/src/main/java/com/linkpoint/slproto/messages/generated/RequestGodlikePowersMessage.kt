package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class RequestGodlikePowersMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var godlike: Boolean = false
    var token: UUID = UUID(0L, 0L)


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packBoolean(buffer, godlike)
        packUUID(buffer, token)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        godlike = unpackBoolean(buffer)
        token = unpackUUID(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF0101

    override fun getMessageName(): String = "RequestGodlikePowers"
}
