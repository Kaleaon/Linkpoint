package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class AvatarGroupsReplyMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var avatarId: UUID = UUID(0L, 0L)
    val groupData: MutableList<GroupDataBlock> = mutableListOf()
    var listInProfile: Boolean = false

    data class GroupDataBlock(
        var groupPowers: Long = 0L,
        var acceptNotices: Boolean = false,
        var groupTitle: ByteArray = ByteArray(0),
        var groupId: UUID = UUID(0L, 0L),
        var groupName: ByteArray = ByteArray(0),
        var groupInsigniaId: UUID = UUID(0L, 0L)
    )


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, avatarId)
        require(groupData.size <= 0xFF) { "GroupData size exceeds 255 (" + groupData.size + ")" }
        packByte(buffer, groupData.size)
        groupData.forEach { entry ->
            packLong(buffer, entry.groupPowers)
            packBoolean(buffer, entry.acceptNotices)
            packVariable(buffer, entry.groupTitle, 1)
            packUUID(buffer, entry.groupId)
            packVariable(buffer, entry.groupName, 1)
            packUUID(buffer, entry.groupInsigniaId)
        }
        packBoolean(buffer, listInProfile)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        avatarId = unpackUUID(buffer)
        run {
            val count = unpackByte(buffer)
            groupData.clear()
            repeat(count) {
                val entry = GroupDataBlock()
                entry.groupPowers = unpackLong(buffer)
                entry.acceptNotices = unpackBoolean(buffer)
                entry.groupTitle = unpackVariable(buffer, 1)
                entry.groupId = unpackUUID(buffer)
                entry.groupName = unpackVariable(buffer, 1)
                entry.groupInsigniaId = unpackUUID(buffer)
                groupData += entry
            }
        }
        listInProfile = unpackBoolean(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF00AD

    override fun getMessageName(): String = "AvatarGroupsReply"
}
