package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class OnlineNotification : SLMessage() {
    val AgentBlock_Fields = ArrayList<AgentBlock>()

    class AgentBlock {
        var AgentID: UUID? = null
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int = (AgentBlock_Fields.size * 16) + 5

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleOnlineNotification(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(1.toByte())
        buffer.put(66.toByte())
        buffer.put(AgentBlock_Fields.size.toByte())
        for (agentBlock in AgentBlock_Fields) {
            packUUID(buffer, agentBlock.AgentID)
        }
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        val count = buffer.get().toInt() and UnsignedBytes.MAX_VALUE.toInt()
        for (i in 0 until count) {
            val agentBlock = AgentBlock()
            agentBlock.AgentID = unpackUUID(buffer)
            AgentBlock_Fields.add(agentBlock)
        }
    }
}