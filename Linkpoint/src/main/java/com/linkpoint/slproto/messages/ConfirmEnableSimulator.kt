package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ConfirmEnableSimulator : SLMessage() {
    val AgentData_Field = AgentData()

    class AgentData {
        var AgentID: UUID? = null
        var SessionID: UUID? = null
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int = 34

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleConfirmEnableSimulator(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.put((-1).toByte())
        buffer.put(8.toByte())
        packUUID(buffer, AgentData_Field.AgentID)
        packUUID(buffer, AgentData_Field.SessionID)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        AgentData_Field.AgentID = unpackUUID(buffer)
        AgentData_Field.SessionID = unpackUUID(buffer)
    }
}