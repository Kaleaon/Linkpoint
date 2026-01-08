package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class AgentSit : SLMessage {
    AgentData AgentData_Field = AgentData()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    AgentSit() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 33
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleAgentSit(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.put((byte) 7)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
    }
}
