package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class CreateGroupReplyMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var groupId: UUID = UUID(0L, 0L)
    var success: Boolean = false
    var message: ByteArray = ByteArray(0)


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, groupId)
        packBoolean(buffer, success)
        packVariable(buffer, message, 1)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        groupId = unpackUUID(buffer)
        success = unpackBoolean(buffer)
        message = unpackVariable(buffer, 1)
    }

    override fun getMessageID(): Int = 0xFFFF0154

    override fun getMessageName(): String = "CreateGroupReply"
}
