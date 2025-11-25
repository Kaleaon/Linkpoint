package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class SetGroupAcceptNoticesMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var groupId: UUID = UUID(0L, 0L)
    var acceptNotices: Boolean = false
    var listInProfile: Boolean = false


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packUUID(buffer, groupId)
        packBoolean(buffer, acceptNotices)
        packBoolean(buffer, listInProfile)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        groupId = unpackUUID(buffer)
        acceptNotices = unpackBoolean(buffer)
        listInProfile = unpackBoolean(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF0172.toInt()

    override fun getMessageName(): String = "SetGroupAcceptNotices"
}
