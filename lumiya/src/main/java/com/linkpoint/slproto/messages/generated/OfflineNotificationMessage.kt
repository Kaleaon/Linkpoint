package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class OfflineNotificationMessage : SLMessage() {
    val agentBlock: MutableList<AgentBlock> = mutableListOf()

    data class AgentBlock(
        var agentId: UUID = UUID(0L, 0L)
    )


    override fun packPayload(buffer: ByteBuffer) {
        require(agentBlock.size <= 0xFF) { "AgentBlock size exceeds 255 (" + agentBlock.size + ")" }
        packByte(buffer, agentBlock.size)
        agentBlock.forEach { entry ->
            packUUID(buffer, entry.agentId)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        run {
            val count = unpackByte(buffer)
            agentBlock.clear()
            repeat(count) {
                val entry = AgentBlock()
                entry.agentId = unpackUUID(buffer)
                agentBlock += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF0143

    override fun getMessageName(): String = "OfflineNotification"
}
