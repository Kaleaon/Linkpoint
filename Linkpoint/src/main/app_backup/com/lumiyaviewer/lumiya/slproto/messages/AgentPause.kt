package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import java.nio.ByteBuffer
import java.util.UUID

class AgentPause : SLMessage {
    var AgentData_Field: AgentData? = AgentData()

    class AgentData {
        var AgentID: UUID? = null
        var SerialNum: Int = 0
        var SessionID: UUID? = null
    }

    constructor() {
        this.zeroCoded = false
    }

    override fun CalcPayloadSize(): Int {
        return 40
    }

    override fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleAgentPause(this)
    }

    override fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put(0.toByte())
        byteBuffer.put(78.toByte())
        packUUID(byteBuffer, AgentData_Field?.AgentID)
        packUUID(byteBuffer, AgentData_Field?.SessionID)
        packInt(byteBuffer, AgentData_Field?.SerialNum ?: 0)
    }

    override fun UnpackPayload(byteBuffer: ByteBuffer) {
        val data = AgentData_Field ?: AgentData()
        data.AgentID = unpackUUID(byteBuffer)
        data.SessionID = unpackUUID(byteBuffer)
        data.SerialNum = unpackInt(byteBuffer)
        this.AgentData_Field = data
    }
}
