package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class ParcelClaim : SLMessage {
    AgentData AgentData_Field
    Data Data_Field
    ArrayList<ParcelData> ParcelData_Fields = ArrayList<>()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class Data {
        Boolean Final
        UUID GroupID
        Boolean IsGroupOwned
    }

    class ParcelData {
        Float East
        Float North
        Float South
        Float West
    }

    ParcelClaim() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
        this.Data_Field = Data()
    }

    fun CalcPayloadSize(): Int {
        return (this.ParcelData_Fields.size() * 16) + 55
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleParcelClaim(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -47)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.Data_Field.GroupID)
        packBoolean(byteBuffer, this.Data_Field.IsGroupOwned)
        packBoolean(byteBuffer, this.Data_Field.Final)
        byteBuffer.put((this as Byte).ParcelData_Fields.size())
        for (ParcelData parcelData : this.ParcelData_Fields) {
            packFloat(byteBuffer, parcelData.West)
            packFloat(byteBuffer, parcelData.South)
            packFloat(byteBuffer, parcelData.East)
            packFloat(byteBuffer, parcelData.North)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.Data_Field.GroupID = unpackUUID(byteBuffer)
        this.Data_Field.IsGroupOwned = unpackBoolean(byteBuffer)
        this.Data_Field.Final = unpackBoolean(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            ParcelData parcelData = ParcelData()
            parcelData.West = unpackFloat(byteBuffer)
            parcelData.South = unpackFloat(byteBuffer)
            parcelData.East = unpackFloat(byteBuffer)
            parcelData.North = unpackFloat(byteBuffer)
            this.ParcelData_Fields.add(parcelData)
        }
    }
}
