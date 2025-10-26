package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class OfflineNotification : SLMessage() {
    public ArrayList<AgentBlock> AgentBlock_Fields = ArrayList<>()

    @JvmStatic
    class AgentBlock {
        public UUID AgentID
    }

    public OfflineNotification() {
        this.zeroCoded = false
    }

    public fun CalcPayloadSize(): Int {
        return (this.AgentBlock_Fields.size() * 16) + 5
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleOfflineNotification(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 67)
        byteBuffer.put((Byte) this.AgentBlock_Fields.size())
        for (AgentBlock agentBlock : this.AgentBlock_Fields) {
            packUUID(byteBuffer, agentBlock.AgentID)
        }
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        val b: Byte = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            val agentBlock: AgentBlock = AgentBlock()
            agentBlock.AgentID = unpackUUID(byteBuffer)
            this.AgentBlock_Fields.add(agentBlock)
        }
    }
}
