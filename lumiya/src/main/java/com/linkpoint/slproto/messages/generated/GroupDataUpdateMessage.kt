package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class GroupDataUpdateMessage : SLMessage() {
    val agentGroupData: MutableList<AgentGroupDataBlock> = mutableListOf()

    data class AgentGroupDataBlock(
        var agentId: UUID = UUID(0L, 0L),
        var groupId: UUID = UUID(0L, 0L),
        var agentPowers: Long = 0L,
        var groupTitle: ByteArray = ByteArray(0)
    )


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        require(agentGroupData.size <= 0xFF) { "AgentGroupData size exceeds 255 (" + agentGroupData.size + ")" }
        packByte(buffer, agentGroupData.size)
        agentGroupData.forEach { entry ->
            packUUID(buffer, entry.agentId)
            packUUID(buffer, entry.groupId)
            packLong(buffer, entry.agentPowers)
            packVariable(buffer, entry.groupTitle, 1)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        run {
            val count = unpackByte(buffer)
            agentGroupData.clear()
            repeat(count) {
                val entry = AgentGroupDataBlock()
                entry.agentId = unpackUUID(buffer)
                entry.groupId = unpackUUID(buffer)
                entry.agentPowers = unpackLong(buffer)
                entry.groupTitle = unpackVariable(buffer, 1)
                agentGroupData += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF0184.toInt()

    override fun getMessageName(): String = "GroupDataUpdate"
}
