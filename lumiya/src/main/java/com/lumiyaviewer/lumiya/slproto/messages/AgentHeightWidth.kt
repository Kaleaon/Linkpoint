package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import java.nio.ByteBuffer
import java.util.UUID

class AgentHeightWidth : SLMessage {
    var AgentData_Field: AgentData? = AgentData()
    var HeightWidthBlock_Field: HeightWidthBlock? = HeightWidthBlock()

    class AgentData {
        var AgentID: UUID? = null
        var CircuitCode: Int = 0
        var SessionID: UUID? = null
    }

    class HeightWidthBlock {
        var GenCounter: Int = 0
        var Height: Int = 0
        var Width: Int = 0
    }

    constructor() {
        this.zeroCoded = false
    }

    override fun CalcPayloadSize(): Int {
        return 48
    }

    override fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleAgentHeightWidth(this)
    }

    override fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put(0.toByte())
        byteBuffer.put(83.toByte())
        packUUID(byteBuffer, AgentData_Field?.AgentID)
        packUUID(byteBuffer, AgentData_Field?.SessionID)
        packInt(byteBuffer, AgentData_Field?.CircuitCode ?: 0)
        packInt(byteBuffer, HeightWidthBlock_Field?.GenCounter ?: 0)
        packShort(byteBuffer, (HeightWidthBlock_Field?.Height ?: 0).toShort())
        packShort(byteBuffer, (HeightWidthBlock_Field?.Width ?: 0).toShort())
    }

    override fun UnpackPayload(byteBuffer: ByteBuffer) {
        val agentData = AgentData_Field ?: AgentData()
        agentData.AgentID = unpackUUID(byteBuffer)
        agentData.SessionID = unpackUUID(byteBuffer)
        agentData.CircuitCode = unpackInt(byteBuffer)
        this.AgentData_Field = agentData

        val block = HeightWidthBlock_Field ?: HeightWidthBlock()
        block.GenCounter = unpackInt(byteBuffer)
        block.Height = unpackShort(byteBuffer).toInt() and 0xFFFF
        block.Width = unpackShort(byteBuffer).toInt() and 0xFFFF
        this.HeightWidthBlock_Field = block
    }
}
