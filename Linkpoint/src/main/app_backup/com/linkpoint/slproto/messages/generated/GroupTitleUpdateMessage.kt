package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class GroupTitleUpdateMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var groupId: UUID = UUID(0L, 0L)
    var titleRoleId: UUID = UUID(0L, 0L)


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packUUID(buffer, groupId)
        packUUID(buffer, titleRoleId)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        groupId = unpackUUID(buffer)
        titleRoleId = unpackUUID(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF0179.toInt()

    override fun getMessageName(): String = "GroupTitleUpdate"
}
