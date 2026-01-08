package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import com.lumiyaviewer.lumiya.slproto.types.LLVector3
import com.lumiyaviewer.lumiya.slproto.types.UUID
import com.lumiyaviewer.lumiya.slproto.types.UUIDPool
import java.nio.ByteBuffer

class ChildAgentPositionUpdate : SLMessage {
    var AgentData_Field: AgentData = AgentData()

    class AgentData {
        var AgentID: UUID = UUIDPool.ZeroUUID
        var SessionID: UUID = UUIDPool.ZeroUUID
        var RegionHandle: Long = 0
        var Position: LLVector3 = LLVector3()
        var Velocity: LLVector3 = LLVector3()
    }

    override fun CalcPayloadSize(): Int {
        return 64
    }

    override fun Handle(sLMessageHandler: SLMessageHandler) {
        // sLMessageHandler.HandleChildAgentPositionUpdate(this)
    }

    override fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put(0.toByte())
        byteBuffer.put((-4).toByte())
        byteBuffer.put(this.AgentData_Field.AgentID.data)
        byteBuffer.put(this.AgentData_Field.SessionID.data)
        byteBuffer.putLong(this.AgentData_Field.RegionHandle)
        packLLVector3(byteBuffer, this.AgentData_Field.Position)
        packLLVector3(byteBuffer, this.AgentData_Field.Velocity)
    }

    override fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = UUID(byteBuffer)
        this.AgentData_Field.SessionID = UUID(byteBuffer)
        this.AgentData_Field.RegionHandle = byteBuffer.long
        this.AgentData_Field.Position = unpackLLVector3(byteBuffer)
        this.AgentData_Field.Velocity = unpackLLVector3(byteBuffer)
    }
}
