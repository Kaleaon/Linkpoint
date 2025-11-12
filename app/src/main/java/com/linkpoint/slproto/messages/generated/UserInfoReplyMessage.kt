package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class UserInfoReplyMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var imViaEMail: Boolean = false
    var directoryVisibility: ByteArray = ByteArray(0)
    var eMail: ByteArray = ByteArray(0)


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packBoolean(buffer, imViaEMail)
        packVariable(buffer, directoryVisibility, 1)
        packVariable(buffer, eMail, 2)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        imViaEMail = unpackBoolean(buffer)
        directoryVisibility = unpackVariable(buffer, 1)
        eMail = unpackVariable(buffer, 2)
    }

    override fun getMessageID(): Int = 0xFFFF0190

    override fun getMessageName(): String = "UserInfoReply"
}
