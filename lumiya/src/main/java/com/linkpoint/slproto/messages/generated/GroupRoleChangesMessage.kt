package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class GroupRoleChangesMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var groupId: UUID = UUID(0L, 0L)
    val roleChange: MutableList<RoleChangeBlock> = mutableListOf()

    data class RoleChangeBlock(
        var roleId: UUID = UUID(0L, 0L),
        var memberId: UUID = UUID(0L, 0L),
        var change: Int = 0
    )


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packUUID(buffer, groupId)
        require(roleChange.size <= 0xFF) { "RoleChange size exceeds 255 (" + roleChange.size + ")" }
        packByte(buffer, roleChange.size)
        roleChange.forEach { entry ->
            packUUID(buffer, entry.roleId)
            packUUID(buffer, entry.memberId)
            packInt(buffer, entry.change)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        groupId = unpackUUID(buffer)
        run {
            val count = unpackByte(buffer)
            roleChange.clear()
            repeat(count) {
                val entry = RoleChangeBlock()
                entry.roleId = unpackUUID(buffer)
                entry.memberId = unpackUUID(buffer)
                entry.change = unpackInt(buffer)
                roleChange += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF0156

    override fun getMessageName(): String = "GroupRoleChanges"
}
