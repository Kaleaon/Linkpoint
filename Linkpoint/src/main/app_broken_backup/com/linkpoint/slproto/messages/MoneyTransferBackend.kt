package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class MoneyTransferBackend : SLMessage {
    MoneyData MoneyData_Field = MoneyData()

    class MoneyData {
        Int AggregatePermInventory
        Int AggregatePermNextOwner
        Int Amount
        ByteArray Description
        UUID DestID
        Int Flags
        Int GridX
        Int GridY
        UUID RegionID
        UUID SourceID
        UUID TransactionID
        Int TransactionTime
        Int TransactionType
    }

    MoneyTransferBackend() {
        this.zeroCoded = true
    }

    fun CalcPayloadSize(): Int {
        return this.MoneyData_Field.Description.size + 88 + 4
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleMoneyTransferBackend(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 56)
        packUUID(byteBuffer, this.MoneyData_Field.TransactionID)
        packInt(byteBuffer, this.MoneyData_Field.TransactionTime)
        packUUID(byteBuffer, this.MoneyData_Field.SourceID)
        packUUID(byteBuffer, this.MoneyData_Field.DestID)
        packByte(byteBuffer, (this as Byte).MoneyData_Field.Flags)
        packInt(byteBuffer, this.MoneyData_Field.Amount)
        packByte(byteBuffer, (this as Byte).MoneyData_Field.AggregatePermNextOwner)
        packByte(byteBuffer, (this as Byte).MoneyData_Field.AggregatePermInventory)
        packInt(byteBuffer, this.MoneyData_Field.TransactionType)
        packUUID(byteBuffer, this.MoneyData_Field.RegionID)
        packInt(byteBuffer, this.MoneyData_Field.GridX)
        packInt(byteBuffer, this.MoneyData_Field.GridY)
        packVariable(byteBuffer, this.MoneyData_Field.Description, 1)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.MoneyData_Field.TransactionID = unpackUUID(byteBuffer)
        this.MoneyData_Field.TransactionTime = unpackInt(byteBuffer)
        this.MoneyData_Field.SourceID = unpackUUID(byteBuffer)
        this.MoneyData_Field.DestID = unpackUUID(byteBuffer)
        this.MoneyData_Field.Flags = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.MoneyData_Field.Amount = unpackInt(byteBuffer)
        this.MoneyData_Field.AggregatePermNextOwner = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.MoneyData_Field.AggregatePermInventory = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.MoneyData_Field.TransactionType = unpackInt(byteBuffer)
        this.MoneyData_Field.RegionID = unpackUUID(byteBuffer)
        this.MoneyData_Field.GridX = unpackInt(byteBuffer)
        this.MoneyData_Field.GridY = unpackInt(byteBuffer)
        this.MoneyData_Field.Description = unpackVariable(byteBuffer, 1)
    }
}
