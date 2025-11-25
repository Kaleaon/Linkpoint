package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import java.nio.ByteBuffer
import java.util.UUID

class AgentSit : SLMessage {
    var AgentData_Field: AgentData? = AgentData()

    class AgentData {
        var AgentID: UUID? = null
        var SessionID: UUID? = null
    }

    constructor() {
        this.zeroCoded = true
    }

    override fun CalcPayloadSize(): Int {
        return 33
    }

    override fun Handle(sLMessageHandler: SLMessageHandler) {
        // sLMessageHandler.HandleAgentSit(this)
    }

    override fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put(0.toByte())
        byteBuffer.put(10.toByte())
        packUUID(byteBuffer, AgentData_Field?.AgentID)
        packUUID(byteBuffer, AgentData_Field?.SessionID)
    }

    override fun UnpackPayload(byteBuffer: ByteBuffer) {
        val agentData = AgentData_Field ?: AgentData()
        agentData.AgentID = unpackUUID(byteBuffer)
        agentData.SessionID = unpackUUID(byteBuffer)
        this.AgentData_Field = agentData
    }
}
