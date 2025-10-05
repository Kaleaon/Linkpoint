package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class SetAlwaysRun : SLMessage() {
    val AgentData_Field = AgentData()

    class AgentData {
        var AgentID: UUID? = null
        var SessionID: UUID? = null
        var AlwaysRun: Boolean = false
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int = 37

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleSetAlwaysRun(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(0.toByte())
        buffer.put(88.toByte())
        packUUID(buffer, AgentData_Field.AgentID)
        packUUID(buffer, AgentData_Field.SessionID)
        packBoolean(buffer, AgentData_Field.AlwaysRun)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        AgentData_Field.AgentID = unpackUUID(buffer)
        AgentData_Field.SessionID = unpackUUID(buffer)
        AgentData_Field.AlwaysRun = unpackBoolean(buffer)
    }
}