package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import com.lumiyaviewer.lumiya.slproto.types.UUID
import com.lumiyaviewer.lumiya.slproto.types.UUIDPool
import java.nio.ByteBuffer

class ChildAgentDying : SLMessage {
    var AgentData_Field: AgentData = AgentData()

    class AgentData {
        var AgentID: UUID = UUIDPool.ZeroUUID
        var SessionID: UUID = UUIDPool.ZeroUUID
    }

    override fun CalcPayloadSize(): Int {
        return 32
    }

    override fun Handle(sLMessageHandler: SLMessageHandler) {
        // sLMessageHandler.HandleChildAgentDying(this)
    }

    override fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put(0.toByte())
        byteBuffer.put((-3).toByte())
        byteBuffer.put(this.AgentData_Field.AgentID.data)
        byteBuffer.put(this.AgentData_Field.SessionID.data)
    }

    override fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = UUID(byteBuffer)
        this.AgentData_Field.SessionID = UUID(byteBuffer)
    }
}
