package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class GroupRoleMembersReplyMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var groupId: UUID = UUID(0L, 0L)
    var requestId: UUID = UUID(0L, 0L)
    var totalPairs: Int = 0
    val memberData: MutableList<MemberDataBlock> = mutableListOf()

    data class MemberDataBlock(
        var roleId: UUID = UUID(0L, 0L),
        var memberId: UUID = UUID(0L, 0L)
    )


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, groupId)
        packUUID(buffer, requestId)
        packInt(buffer, totalPairs)
        require(memberData.size <= 0xFF) { "MemberData size exceeds 255 (" + memberData.size + ")" }
        packByte(buffer, memberData.size)
        memberData.forEach { entry ->
            packUUID(buffer, entry.roleId)
            packUUID(buffer, entry.memberId)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        groupId = unpackUUID(buffer)
        requestId = unpackUUID(buffer)
        totalPairs = unpackInt(buffer)
        run {
            val count = unpackByte(buffer)
            memberData.clear()
            repeat(count) {
                val entry = MemberDataBlock()
                entry.roleId = unpackUUID(buffer)
                entry.memberId = unpackUUID(buffer)
                memberData += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF0176

    override fun getMessageName(): String = "GroupRoleMembersReply"
}
