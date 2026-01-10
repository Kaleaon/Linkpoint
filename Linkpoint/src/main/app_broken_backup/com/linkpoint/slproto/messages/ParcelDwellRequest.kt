package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ParcelDwellRequest : SLMessage {
    AgentData AgentData_Field = AgentData()
    Data Data_Field = Data()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class Data {
        Int LocalID
        UUID ParcelID
    }

    ParcelDwellRequest() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 56
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleParcelDwellRequest(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -38)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packInt(byteBuffer, this.Data_Field.LocalID)
        packUUID(byteBuffer, this.Data_Field.ParcelID)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.Data_Field.LocalID = unpackInt(byteBuffer)
        this.Data_Field.ParcelID = unpackUUID(byteBuffer)
    }
}
