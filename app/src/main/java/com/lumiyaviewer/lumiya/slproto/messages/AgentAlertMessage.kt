package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class AgentAlertMessage : SLMessage {
    AgentData AgentData_Field = AgentData()
    AlertData AlertData_Field = AlertData()

    class AgentData {
        UUID AgentID
    }

    class AlertData {
        Byte[] Message
        Boolean Modal
    }

    constructor() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return this.AlertData_Field.Message.length + 2 + 20
    }

    fun Handle(sLMessageHandler: SLMessageHandler): Unit {
        sLMessageHandler.HandleAgentAlertMessage(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -121)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packBoolean(byteBuffer, this.AlertData_Field.Modal)
        packVariable(byteBuffer, this.AlertData_Field.Message, 1)
    }

    fun UnpackPayload(byteBuffer: ByteBuffer): Unit {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AlertData_Field.Modal = unpackBoolean(byteBuffer)
        this.AlertData_Field.Message = unpackVariable(byteBuffer, 1)
    }
}
