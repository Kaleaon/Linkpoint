package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class GroupRoleDataReplyMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var groupId: UUID = UUID(0L, 0L)
    var requestId: UUID = UUID(0L, 0L)
    var roleCount: Int = 0
    val roleData: MutableList<RoleDataBlock> = mutableListOf()

    data class RoleDataBlock(
        var roleId: UUID = UUID(0L, 0L),
        var name: ByteArray = ByteArray(0),
        var title: ByteArray = ByteArray(0),
        var description: ByteArray = ByteArray(0),
        var powers: Long = 0L,
        var members: Int = 0
    )


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, groupId)
        packUUID(buffer, requestId)
        packInt(buffer, roleCount)
        require(roleData.size <= 0xFF) { "RoleData size exceeds 255 (" + roleData.size + ")" }
        packByte(buffer, roleData.size)
        roleData.forEach { entry ->
            packUUID(buffer, entry.roleId)
            packVariable(buffer, entry.name, 1)
            packVariable(buffer, entry.title, 1)
            packVariable(buffer, entry.description, 1)
            packLong(buffer, entry.powers)
            packInt(buffer, entry.members)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        groupId = unpackUUID(buffer)
        requestId = unpackUUID(buffer)
        roleCount = unpackInt(buffer)
        run {
            val count = unpackByte(buffer)
            roleData.clear()
            repeat(count) {
                val entry = RoleDataBlock()
                entry.roleId = unpackUUID(buffer)
                entry.name = unpackVariable(buffer, 1)
                entry.title = unpackVariable(buffer, 1)
                entry.description = unpackVariable(buffer, 1)
                entry.powers = unpackLong(buffer)
                entry.members = unpackInt(buffer)
                roleData += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF0174

    override fun getMessageName(): String = "GroupRoleDataReply"
}
