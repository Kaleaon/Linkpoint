package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class InviteGroupResponseMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var inviteeId: UUID = UUID(0L, 0L)
    var groupId: UUID = UUID(0L, 0L)
    var roleId: UUID = UUID(0L, 0L)
    var membershipFee: Int = 0
    var groupLimit: Int = 0


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, inviteeId)
        packUUID(buffer, groupId)
        packUUID(buffer, roleId)
        packInt(buffer, membershipFee)
        packInt(buffer, groupLimit)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        inviteeId = unpackUUID(buffer)
        groupId = unpackUUID(buffer)
        roleId = unpackUUID(buffer)
        membershipFee = unpackInt(buffer)
        groupLimit = unpackInt(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF015E

    override fun getMessageName(): String = "InviteGroupResponse"
}
