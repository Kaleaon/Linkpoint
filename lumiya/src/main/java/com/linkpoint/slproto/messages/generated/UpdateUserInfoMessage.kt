package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class UpdateUserInfoMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var imViaEMail: Boolean = false
    var directoryVisibility: ByteArray = ByteArray(0)


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packBoolean(buffer, imViaEMail)
        packVariable(buffer, directoryVisibility, 1)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        imViaEMail = unpackBoolean(buffer)
        directoryVisibility = unpackVariable(buffer, 1)
    }

    override fun getMessageID(): Int = 0xFFFF0191

    override fun getMessageName(): String = "UpdateUserInfo"
}
