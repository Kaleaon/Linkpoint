package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ClassifiedGodDelete : SLMessage {
    AgentData AgentData_Field = AgentData()
    Data Data_Field = Data()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class Data {
        UUID ClassifiedID
        UUID QueryID
    }

    ClassifiedGodDelete() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 68
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleClassifiedGodDelete(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) 47)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.Data_Field.ClassifiedID)
        packUUID(byteBuffer, this.Data_Field.QueryID)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.Data_Field.ClassifiedID = unpackUUID(byteBuffer)
        this.Data_Field.QueryID = unpackUUID(byteBuffer)
    }
}
