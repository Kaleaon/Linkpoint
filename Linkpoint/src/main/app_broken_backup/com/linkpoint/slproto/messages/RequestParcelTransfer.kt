package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class RequestParcelTransfer : SLMessage {
    Data Data_Field = Data()
    RegionData RegionData_Field = RegionData()

    class Data {
        Int ActualArea
        Int Amount
        Int BillableArea
        UUID DestID
        Boolean Final
        Int Flags
        UUID OwnerID
        UUID SourceID
        UUID TransactionID
        Int TransactionTime
        Int TransactionType
    }

    class RegionData {
        Int GridX
        Int GridY
        UUID RegionID
    }

    RequestParcelTransfer() {
        this.zeroCoded = true
    }

    fun CalcPayloadSize(): Int {
        return 114
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleRequestParcelTransfer(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -36)
        packUUID(byteBuffer, this.Data_Field.TransactionID)
        packInt(byteBuffer, this.Data_Field.TransactionTime)
        packUUID(byteBuffer, this.Data_Field.SourceID)
        packUUID(byteBuffer, this.Data_Field.DestID)
        packUUID(byteBuffer, this.Data_Field.OwnerID)
        packByte(byteBuffer, (this as Byte).Data_Field.Flags)
        packInt(byteBuffer, this.Data_Field.TransactionType)
        packInt(byteBuffer, this.Data_Field.Amount)
        packInt(byteBuffer, this.Data_Field.BillableArea)
        packInt(byteBuffer, this.Data_Field.ActualArea)
        packBoolean(byteBuffer, this.Data_Field.Final)
        packUUID(byteBuffer, this.RegionData_Field.RegionID)
        packInt(byteBuffer, this.RegionData_Field.GridX)
        packInt(byteBuffer, this.RegionData_Field.GridY)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.Data_Field.TransactionID = unpackUUID(byteBuffer)
        this.Data_Field.TransactionTime = unpackInt(byteBuffer)
        this.Data_Field.SourceID = unpackUUID(byteBuffer)
        this.Data_Field.DestID = unpackUUID(byteBuffer)
        this.Data_Field.OwnerID = unpackUUID(byteBuffer)
        this.Data_Field.Flags = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.Data_Field.TransactionType = unpackInt(byteBuffer)
        this.Data_Field.Amount = unpackInt(byteBuffer)
        this.Data_Field.BillableArea = unpackInt(byteBuffer)
        this.Data_Field.ActualArea = unpackInt(byteBuffer)
        this.Data_Field.Final = unpackBoolean(byteBuffer)
        this.RegionData_Field.RegionID = unpackUUID(byteBuffer)
        this.RegionData_Field.GridX = unpackInt(byteBuffer)
        this.RegionData_Field.GridY = unpackInt(byteBuffer)
    }
}
