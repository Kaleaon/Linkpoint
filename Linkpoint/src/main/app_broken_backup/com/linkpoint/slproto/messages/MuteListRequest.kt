package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class MuteListRequest : SLMessage {
    AgentData AgentData_Field = AgentData()
    MuteData MuteData_Field = MuteData()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class MuteData {
        Int MuteCRC
    }

    MuteListRequest() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 40
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleMuteListRequest(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 6)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packInt(byteBuffer, this.MuteData_Field.MuteCRC)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.MuteData_Field.MuteCRC = unpackInt(byteBuffer)
    }
}
