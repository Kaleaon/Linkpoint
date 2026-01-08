package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class SystemKickUserMessage : SLMessage() {
    val agentInfo: MutableList<AgentInfoBlock> = mutableListOf()

    data class AgentInfoBlock(
        var agentId: UUID = UUID(0L, 0L)
    )


    override fun packPayload(buffer: ByteBuffer) {
        require(agentInfo.size <= 0xFF) { "AgentInfo size exceeds 255 (" + agentInfo.size + ")" }
        packByte(buffer, agentInfo.size)
        agentInfo.forEach { entry ->
            packUUID(buffer, entry.agentId)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        run {
            val count = unpackByte(buffer)
            agentInfo.clear()
            repeat(count) {
                val entry = AgentInfoBlock()
                entry.agentId = unpackUUID(buffer)
                agentInfo += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF00A6.toInt()

    override fun getMessageName(): String = "SystemKickUser"
}
