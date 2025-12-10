package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class OfflineNotification : SLMessage {
    ArrayList<AgentBlock> AgentBlock_Fields = ArrayList<>()

    class AgentBlock {
        UUID AgentID
    }

    OfflineNotification() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return (this.AgentBlock_Fields.size() * 16) + 5
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleOfflineNotification(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 67)
        byteBuffer.put((this as Byte).AgentBlock_Fields.size())
        for (AgentBlock agentBlock : this.AgentBlock_Fields) {
            packUUID(byteBuffer, agentBlock.AgentID)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            AgentBlock agentBlock = AgentBlock()
            agentBlock.AgentID = unpackUUID(byteBuffer)
            this.AgentBlock_Fields.add(agentBlock)
        }
    }
}
