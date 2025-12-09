package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class SetAlwaysRun : SLMessage {
    AgentData AgentData_Field = AgentData()

    class AgentData {
        UUID AgentID
        Boolean AlwaysRun
        UUID SessionID
    }

    SetAlwaysRun() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 37
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleSetAlwaysRun(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 88)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packBoolean(byteBuffer, this.AgentData_Field.AlwaysRun)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.AgentData_Field.AlwaysRun = unpackBoolean(byteBuffer)
    }
}
