package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
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

    Int CalcPayloadSize() {
        return 37
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleSetAlwaysRun(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 88)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packBoolean(byteBuffer, this.AgentData_Field.AlwaysRun)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.AgentData_Field.AlwaysRun = unpackBoolean(byteBuffer)
    }
}
