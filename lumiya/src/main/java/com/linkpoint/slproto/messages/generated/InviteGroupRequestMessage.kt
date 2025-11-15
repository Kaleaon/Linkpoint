package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class InviteGroupRequestMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var groupId: UUID = UUID(0L, 0L)
    val inviteData: MutableList<InviteDataBlock> = mutableListOf()

    data class InviteDataBlock(
        var inviteeId: UUID = UUID(0L, 0L),
        var roleId: UUID = UUID(0L, 0L)
    )


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packUUID(buffer, groupId)
        require(inviteData.size <= 0xFF) { "InviteData size exceeds 255 (" + inviteData.size + ")" }
        packByte(buffer, inviteData.size)
        inviteData.forEach { entry ->
            packUUID(buffer, entry.inviteeId)
            packUUID(buffer, entry.roleId)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        groupId = unpackUUID(buffer)
        run {
            val count = unpackByte(buffer)
            inviteData.clear()
            repeat(count) {
                val entry = InviteDataBlock()
                entry.inviteeId = unpackUUID(buffer)
                entry.roleId = unpackUUID(buffer)
                inviteData += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF015D

    override fun getMessageName(): String = "InviteGroupRequest"
}
