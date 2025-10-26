package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class ParcelClaim : SLMessage() {
    public AgentData AgentData_Field
    public Data Data_Field
    public ArrayList<ParcelData> ParcelData_Fields = ArrayList<>()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID SessionID
    }

    @JvmStatic
    class Data {
        public Boolean Final
        public UUID GroupID
        public Boolean IsGroupOwned
    }

    @JvmStatic
    class ParcelData {
        public Float East
        public Float North
        public Float South
        public Float West
    }

    public ParcelClaim() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
        this.Data_Field = Data()
    }

    public fun CalcPayloadSize(): Int {
        return (this.ParcelData_Fields.size() * 16) + 55
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleParcelClaim(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -47)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.Data_Field.GroupID)
        packBoolean(byteBuffer, this.Data_Field.IsGroupOwned)
        packBoolean(byteBuffer, this.Data_Field.Final)
        byteBuffer.put((Byte) this.ParcelData_Fields.size())
        for (ParcelData parcelData : this.ParcelData_Fields) {
            packFloat(byteBuffer, parcelData.West)
            packFloat(byteBuffer, parcelData.South)
            packFloat(byteBuffer, parcelData.East)
            packFloat(byteBuffer, parcelData.North)
        }
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.Data_Field.GroupID = unpackUUID(byteBuffer)
        this.Data_Field.IsGroupOwned = unpackBoolean(byteBuffer)
        this.Data_Field.Final = unpackBoolean(byteBuffer)
        val b: Byte = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            val parcelData: ParcelData = ParcelData()
            parcelData.West = unpackFloat(byteBuffer)
            parcelData.South = unpackFloat(byteBuffer)
            parcelData.East = unpackFloat(byteBuffer)
            parcelData.North = unpackFloat(byteBuffer)
            this.ParcelData_Fields.add(parcelData)
        }
    }
}
