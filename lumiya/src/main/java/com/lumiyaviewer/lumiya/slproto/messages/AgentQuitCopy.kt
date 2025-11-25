package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import java.nio.ByteBuffer
import java.util.UUID

class AgentQuitCopy : SLMessage {
    var AgentData_Field: AgentData? = AgentData()
    var FuseBlock_Field: FuseBlock? = FuseBlock()

    class AgentData {
        var AgentID: UUID? = null
        var SessionID: UUID? = null
    }

    class FuseBlock {
        var ViewerCircuitCode: Int = 0
    }

    constructor() {
        this.zeroCoded = false
    }

    override fun CalcPayloadSize(): Int {
        return 40
    }

    override fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleAgentQuitCopy(this)
    }

    override fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put(0.toByte())
        byteBuffer.put(85.toByte())
        packUUID(byteBuffer, AgentData_Field?.AgentID)
        packUUID(byteBuffer, AgentData_Field?.SessionID)
        packInt(byteBuffer, FuseBlock_Field?.ViewerCircuitCode ?: 0)
    }

    override fun UnpackPayload(byteBuffer: ByteBuffer) {
        val agentData = AgentData_Field ?: AgentData()
        agentData.AgentID = unpackUUID(byteBuffer)
        agentData.SessionID = unpackUUID(byteBuffer)
        this.AgentData_Field = agentData

        val fuseBlock = FuseBlock_Field ?: FuseBlock()
        fuseBlock.ViewerCircuitCode = unpackInt(byteBuffer)
        this.FuseBlock_Field = fuseBlock
    }
}
