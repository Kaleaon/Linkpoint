package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class OnlineNotification : SLMessage {
    ArrayList<AgentBlock> AgentBlock_Fields = ArrayList<>()

    class AgentBlock {
        UUID AgentID
    }

    OnlineNotification() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return (this.AgentBlock_Fields.size() * 16) + 5
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleOnlineNotification(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 66)
        byteBuffer.put((this as Byte).AgentBlock_Fields.size())
        for (AgentBlock agentBlock : this.AgentBlock_Fields) {
            packUUID(byteBuffer, agentBlock.AgentID)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            AgentBlock agentBlock = AgentBlock()
            agentBlock.AgentID = unpackUUID(byteBuffer)
            this.AgentBlock_Fields.add(agentBlock)
        }
    }
}
