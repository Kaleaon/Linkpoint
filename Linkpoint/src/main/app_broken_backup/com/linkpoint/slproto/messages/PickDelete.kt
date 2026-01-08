package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class PickDelete : SLMessage {
    AgentData AgentData_Field = AgentData()
    Data Data_Field = Data()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class Data {
        UUID PickID
    }

    PickDelete() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 52
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandlePickDelete(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -70)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.Data_Field.PickID)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.Data_Field.PickID = unpackUUID(byteBuffer)
    }
}
