package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import java.nio.ByteBuffer
import java.util.UUID

class AgentFOV : SLMessage {
    var AgentData_Field: AgentData? = AgentData()
    var FOVBlock_Field: FOVBlock? = FOVBlock()

    class AgentData {
        var AgentID: UUID? = null
        var CircuitCode: Int = 0
        var SessionID: UUID? = null
    }

    class FOVBlock {
        var GenCounter: Int = 0
        var VerticalAngle: Float = 0f
    }

    constructor() {
        this.zeroCoded = false
    }

    override fun CalcPayloadSize(): Int {
        return 48
    }

    override fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleAgentFOV(this)
    }

    override fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put(0.toByte())
        byteBuffer.put(82.toByte())
        packUUID(byteBuffer, AgentData_Field?.AgentID)
        packUUID(byteBuffer, AgentData_Field?.SessionID)
        packInt(byteBuffer, AgentData_Field?.CircuitCode ?: 0)
        packInt(byteBuffer, FOVBlock_Field?.GenCounter ?: 0)
        packFloat(byteBuffer, FOVBlock_Field?.VerticalAngle ?: 0f)
    }

    override fun UnpackPayload(byteBuffer: ByteBuffer) {
        val agentData = AgentData_Field ?: AgentData()
        agentData.AgentID = unpackUUID(byteBuffer)
        agentData.SessionID = unpackUUID(byteBuffer)
        agentData.CircuitCode = unpackInt(byteBuffer)
        this.AgentData_Field = agentData

        val fovBlock = FOVBlock_Field ?: FOVBlock()
        fovBlock.GenCounter = unpackInt(byteBuffer)
        fovBlock.VerticalAngle = unpackFloat(byteBuffer)
        this.FOVBlock_Field = fovBlock
    }
}
