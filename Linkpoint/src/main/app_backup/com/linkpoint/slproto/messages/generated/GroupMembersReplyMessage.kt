package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class GroupMembersReplyMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var groupId: UUID = UUID(0L, 0L)
    var requestId: UUID = UUID(0L, 0L)
    var memberCount: Int = 0
    val memberData: MutableList<MemberDataBlock> = mutableListOf()

    data class MemberDataBlock(
        var agentId: UUID = UUID(0L, 0L),
        var contribution: Int = 0,
        var onlineStatus: ByteArray = ByteArray(0),
        var agentPowers: Long = 0L,
        var title: ByteArray = ByteArray(0),
        var isOwner: Boolean = false
    )


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, groupId)
        packUUID(buffer, requestId)
        packInt(buffer, memberCount)
        require(memberData.size <= 0xFF) { "MemberData size exceeds 255 (" + memberData.size + ")" }
        packByte(buffer, memberData.size)
        memberData.forEach { entry ->
            packUUID(buffer, entry.agentId)
            packInt(buffer, entry.contribution)
            packVariable(buffer, entry.onlineStatus, 1)
            packLong(buffer, entry.agentPowers)
            packVariable(buffer, entry.title, 1)
            packBoolean(buffer, entry.isOwner)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        groupId = unpackUUID(buffer)
        requestId = unpackUUID(buffer)
        memberCount = unpackInt(buffer)
        run {
            val count = unpackByte(buffer)
            memberData.clear()
            repeat(count) {
                val entry = MemberDataBlock()
                entry.agentId = unpackUUID(buffer)
                entry.contribution = unpackInt(buffer)
                entry.onlineStatus = unpackVariable(buffer, 1)
                entry.agentPowers = unpackLong(buffer)
                entry.title = unpackVariable(buffer, 1)
                entry.isOwner = unpackBoolean(buffer)
                memberData += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF016F.toInt()

    override fun getMessageName(): String = "GroupMembersReply"
}
