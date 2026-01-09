package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ParcelDwellReply : SLMessage {
    AgentData AgentData_Field = AgentData()
    Data Data_Field = Data()

    class AgentData {
        UUID AgentID
    }

    class Data {
        Float Dwell
        Int LocalID
        UUID ParcelID
    }

    ParcelDwellReply() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 44
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleParcelDwellReply(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -37)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packInt(byteBuffer, this.Data_Field.LocalID)
        packUUID(byteBuffer, this.Data_Field.ParcelID)
        packFloat(byteBuffer, this.Data_Field.Dwell)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.Data_Field.LocalID = unpackInt(byteBuffer)
        this.Data_Field.ParcelID = unpackUUID(byteBuffer)
        this.Data_Field.Dwell = unpackFloat(byteBuffer)
    }
}
