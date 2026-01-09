package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ParcelBuy : SLMessage {
    AgentData AgentData_Field = AgentData()
    Data Data_Field = Data()
    ParcelData ParcelData_Field = ParcelData()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class Data {
        Boolean Final
        UUID GroupID
        Boolean IsGroupOwned
        Int LocalID
        Boolean RemoveContribution
    }

    class ParcelData {
        Int Area
        Int Price
    }

    ParcelBuy() {
        this.zeroCoded = true
    }

    fun CalcPayloadSize(): Int {
        return 67
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleParcelBuy(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -43)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.Data_Field.GroupID)
        packBoolean(byteBuffer, this.Data_Field.IsGroupOwned)
        packBoolean(byteBuffer, this.Data_Field.RemoveContribution)
        packInt(byteBuffer, this.Data_Field.LocalID)
        packBoolean(byteBuffer, this.Data_Field.Final)
        packInt(byteBuffer, this.ParcelData_Field.Price)
        packInt(byteBuffer, this.ParcelData_Field.Area)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.Data_Field.GroupID = unpackUUID(byteBuffer)
        this.Data_Field.IsGroupOwned = unpackBoolean(byteBuffer)
        this.Data_Field.RemoveContribution = unpackBoolean(byteBuffer)
        this.Data_Field.LocalID = unpackInt(byteBuffer)
        this.Data_Field.Final = unpackBoolean(byteBuffer)
        this.ParcelData_Field.Price = unpackInt(byteBuffer)
        this.ParcelData_Field.Area = unpackInt(byteBuffer)
    }
}
