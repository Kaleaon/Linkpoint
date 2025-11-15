package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class LeaveGroupReplyMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var groupId: UUID = UUID(0L, 0L)
    var success: Boolean = false


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, groupId)
        packBoolean(buffer, success)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        groupId = unpackUUID(buffer)
        success = unpackBoolean(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF015C

    override fun getMessageName(): String = "LeaveGroupReply"
}
