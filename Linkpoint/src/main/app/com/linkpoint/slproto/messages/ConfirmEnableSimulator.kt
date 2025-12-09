package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ConfirmEnableSimulator : SLMessage {
    AgentData AgentData_Field = AgentData()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    ConfirmEnableSimulator() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 34
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleConfirmEnableSimulator(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.put((byte) -1)
        byteBuffer.put((byte) 8)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
    }
}
