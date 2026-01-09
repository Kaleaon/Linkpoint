package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ParcelAccessListRequest : SLMessage {
    AgentData AgentData_Field = AgentData()
    Data Data_Field = Data()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class Data {
        Int Flags
        Int LocalID
        Int SequenceID
    }

    ParcelAccessListRequest() {
        this.zeroCoded = true
    }

    fun CalcPayloadSize(): Int {
        return 48
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleParcelAccessListRequest(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -41)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packInt(byteBuffer, this.Data_Field.SequenceID)
        packInt(byteBuffer, this.Data_Field.Flags)
        packInt(byteBuffer, this.Data_Field.LocalID)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.Data_Field.SequenceID = unpackInt(byteBuffer)
        this.Data_Field.Flags = unpackInt(byteBuffer)
        this.Data_Field.LocalID = unpackInt(byteBuffer)
    }
}
