package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class GroupTitlesReplyMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var groupId: UUID = UUID(0L, 0L)
    var requestId: UUID = UUID(0L, 0L)
    val groupData: MutableList<GroupDataBlock> = mutableListOf()

    data class GroupDataBlock(
        var title: ByteArray = ByteArray(0),
        var roleId: UUID = UUID(0L, 0L),
        var selected: Boolean = false
    )


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, groupId)
        packUUID(buffer, requestId)
        require(groupData.size <= 0xFF) { "GroupData size exceeds 255 (" + groupData.size + ")" }
        packByte(buffer, groupData.size)
        groupData.forEach { entry ->
            packVariable(buffer, entry.title, 1)
            packUUID(buffer, entry.roleId)
            packBoolean(buffer, entry.selected)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        groupId = unpackUUID(buffer)
        requestId = unpackUUID(buffer)
        run {
            val count = unpackByte(buffer)
            groupData.clear()
            repeat(count) {
                val entry = GroupDataBlock()
                entry.title = unpackVariable(buffer, 1)
                entry.roleId = unpackUUID(buffer)
                entry.selected = unpackBoolean(buffer)
                groupData += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF0178

    override fun getMessageName(): String = "GroupTitlesReply"
}
