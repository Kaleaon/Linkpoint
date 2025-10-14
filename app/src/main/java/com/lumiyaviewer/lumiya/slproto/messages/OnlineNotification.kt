package com.lumiyaviewer.lumiya.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.lumiyaviewer.lumiya.slproto.SLMessage
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

    Int CalcPayloadSize() {
        return (this.AgentBlock_Fields.size() * 16) + 5
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleOnlineNotification(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 66)
        byteBuffer.put((Byte) this.AgentBlock_Fields.size())
        for (AgentBlock agentBlock : this.AgentBlock_Fields) {
            packUUID(byteBuffer, agentBlock.AgentID)
        }
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            AgentBlock agentBlock = AgentBlock()
            agentBlock.AgentID = unpackUUID(byteBuffer)
            this.AgentBlock_Fields.add(agentBlock)
        }
    }
}
