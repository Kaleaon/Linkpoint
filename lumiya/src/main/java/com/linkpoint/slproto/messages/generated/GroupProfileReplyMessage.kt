package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class GroupProfileReplyMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var groupId: UUID = UUID(0L, 0L)
    var name: ByteArray = ByteArray(0)
    var charter: ByteArray = ByteArray(0)
    var showInList: Boolean = false
    var memberTitle: ByteArray = ByteArray(0)
    var powersMask: Long = 0L
    var insigniaId: UUID = UUID(0L, 0L)
    var founderId: UUID = UUID(0L, 0L)
    var membershipFee: Int = 0
    var openEnrollment: Boolean = false
    var money: Int = 0
    var groupMembershipCount: Int = 0
    var groupRolesCount: Int = 0
    var allowPublish: Boolean = false
    var maturePublish: Boolean = false
    var ownerRole: UUID = UUID(0L, 0L)


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, groupId)
        packVariable(buffer, name, 1)
        packVariable(buffer, charter, 2)
        packBoolean(buffer, showInList)
        packVariable(buffer, memberTitle, 1)
        packLong(buffer, powersMask)
        packUUID(buffer, insigniaId)
        packUUID(buffer, founderId)
        packInt(buffer, membershipFee)
        packBoolean(buffer, openEnrollment)
        packInt(buffer, money)
        packInt(buffer, groupMembershipCount)
        packInt(buffer, groupRolesCount)
        packBoolean(buffer, allowPublish)
        packBoolean(buffer, maturePublish)
        packUUID(buffer, ownerRole)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        groupId = unpackUUID(buffer)
        name = unpackVariable(buffer, 1)
        charter = unpackVariable(buffer, 2)
        showInList = unpackBoolean(buffer)
        memberTitle = unpackVariable(buffer, 1)
        powersMask = unpackLong(buffer)
        insigniaId = unpackUUID(buffer)
        founderId = unpackUUID(buffer)
        membershipFee = unpackInt(buffer)
        openEnrollment = unpackBoolean(buffer)
        money = unpackInt(buffer)
        groupMembershipCount = unpackInt(buffer)
        groupRolesCount = unpackInt(buffer)
        allowPublish = unpackBoolean(buffer)
        maturePublish = unpackBoolean(buffer)
        ownerRole = unpackUUID(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF0160

    override fun getMessageName(): String = "GroupProfileReply"
}
