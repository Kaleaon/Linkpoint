package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import java.nio.ByteBuffer
import java.util.UUID

class AgentDropGroup : SLMessage {
    var AgentData_Field: AgentData? = AgentData()

    class AgentData {
        var AgentID: UUID? = null
        var GroupID: UUID? = null
    }

    constructor() {
        this.zeroCoded = true
    }

    override fun CalcPayloadSize(): Int {
        return 36
    }

    override fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleAgentDropGroup(this)
    }

    override fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put(1.toByte())
        byteBuffer.put((-122).toByte())
        packUUID(byteBuffer, AgentData_Field?.AgentID)
        packUUID(byteBuffer, AgentData_Field?.GroupID)
    }

    override fun UnpackPayload(byteBuffer: ByteBuffer) {
        val data = AgentData_Field ?: AgentData()
        data.AgentID = unpackUUID(byteBuffer)
        data.GroupID = unpackUUID(byteBuffer)
        this.AgentData_Field = data
    }
}
