package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ParcelPropertiesRequestByID : SLMessage {
    AgentData AgentData_Field = AgentData()
    ParcelData ParcelData_Field = ParcelData()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class ParcelData {
        Int LocalID
        Int SequenceID
    }

    ParcelPropertiesRequestByID() {
        this.zeroCoded = true
    }

    fun CalcPayloadSize(): Int {
        return 44
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleParcelPropertiesRequestByID(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -59)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packInt(byteBuffer, this.ParcelData_Field.SequenceID)
        packInt(byteBuffer, this.ParcelData_Field.LocalID)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.ParcelData_Field.SequenceID = unpackInt(byteBuffer)
        this.ParcelData_Field.LocalID = unpackInt(byteBuffer)
    }
}
