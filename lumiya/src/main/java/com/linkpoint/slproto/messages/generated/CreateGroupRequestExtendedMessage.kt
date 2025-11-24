package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class CreateGroupRequestExtendedMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var groupLimit: Int = 0
    var name: ByteArray = ByteArray(0)
    var charter: ByteArray = ByteArray(0)
    var showInList: Boolean = false
    var insigniaId: UUID = UUID(0L, 0L)
    var membershipFee: Int = 0
    var openEnrollment: Boolean = false
    var allowPublish: Boolean = false
    var maturePublish: Boolean = false


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packInt(buffer, groupLimit)
        packVariable(buffer, name, 1)
        packVariable(buffer, charter, 2)
        packBoolean(buffer, showInList)
        packUUID(buffer, insigniaId)
        packInt(buffer, membershipFee)
        packBoolean(buffer, openEnrollment)
        packBoolean(buffer, allowPublish)
        packBoolean(buffer, maturePublish)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        groupLimit = unpackInt(buffer)
        name = unpackVariable(buffer, 1)
        charter = unpackVariable(buffer, 2)
        showInList = unpackBoolean(buffer)
        insigniaId = unpackUUID(buffer)
        membershipFee = unpackInt(buffer)
        openEnrollment = unpackBoolean(buffer)
        allowPublish = unpackBoolean(buffer)
        maturePublish = unpackBoolean(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF01AD.toInt()

    override fun getMessageName(): String = "CreateGroupRequestExtended"
}
