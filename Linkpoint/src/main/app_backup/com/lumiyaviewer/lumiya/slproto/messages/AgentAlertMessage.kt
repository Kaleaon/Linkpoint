package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import java.nio.ByteBuffer
import java.util.UUID

class AgentAlertMessage : SLMessage {
    val AgentData_Field = AgentData()
    val AlertData_Field = AlertData()

    class AgentData {
        var AgentID: UUID? = null
    }

    class AlertData {
        var Message: ByteArray? = null
        var Modal: Boolean = false
    }

    constructor() {
        this.zeroCoded = false
    }

    override fun CalcPayloadSize(): Int {
        return (this.AlertData_Field.Message?.size ?: 0) + 2 + 20
    }

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleAgentAlertMessage(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(0.toByte())
        buffer.put((-121).toByte())
        packUUID(buffer, this.AgentData_Field.AgentID)
        packBoolean(buffer, this.AlertData_Field.Modal)
        packVariable(buffer, this.AlertData_Field.Message ?: ByteArray(0), 1)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(buffer)
        this.AlertData_Field.Modal = unpackBoolean(buffer)
        this.AlertData_Field.Message = unpackVariable(buffer, 1)
    }
}
