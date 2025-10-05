package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class SystemKickUser : SLMessage() {
    val AgentInfo_Fields = ArrayList<AgentInfo>()

    class AgentInfo {
        var AgentID: UUID? = null
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int = (AgentInfo_Fields.size * 16) + 5

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleSystemKickUser(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(0.toByte())
        buffer.put((-90).toByte())
        buffer.put(AgentInfo_Fields.size.toByte())
        for (agentInfo in AgentInfo_Fields) {
            packUUID(buffer, agentInfo.AgentID)
        }
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        val count = buffer.get().toInt() and UnsignedBytes.MAX_VALUE.toInt()
        for (i in 0 until count) {
            val agentInfo = AgentInfo()
            agentInfo.AgentID = unpackUUID(buffer)
            AgentInfo_Fields.add(agentInfo)
        }
    }
}