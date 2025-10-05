package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class MoneyTransferBackend : SLMessage() {
    public MoneyData MoneyData_Field = MoneyData()

    @JvmStatic
    class MoneyData {
        public Int AggregatePermInventory
        public Int AggregatePermNextOwner
        public Int Amount
        public Byte[] Description
        public UUID DestID
        public Int Flags
        public Int GridX
        public Int GridY
        public UUID RegionID
        public UUID SourceID
        public UUID TransactionID
        public Int TransactionTime
        public Int TransactionType
    }

    public MoneyTransferBackend() {
        this.zeroCoded = true
    }

    public Int CalcPayloadSize() {
        return this.MoneyData_Field.Description.length + 88 + 4
    }

    public Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleMoneyTransferBackend(this)
    }

    public Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 56)
        packUUID(byteBuffer, this.MoneyData_Field.TransactionID)
        packInt(byteBuffer, this.MoneyData_Field.TransactionTime)
        packUUID(byteBuffer, this.MoneyData_Field.SourceID)
        packUUID(byteBuffer, this.MoneyData_Field.DestID)
        packByte(byteBuffer, (Byte) this.MoneyData_Field.Flags)
        packInt(byteBuffer, this.MoneyData_Field.Amount)
        packByte(byteBuffer, (Byte) this.MoneyData_Field.AggregatePermNextOwner)
        packByte(byteBuffer, (Byte) this.MoneyData_Field.AggregatePermInventory)
        packInt(byteBuffer, this.MoneyData_Field.TransactionType)
        packUUID(byteBuffer, this.MoneyData_Field.RegionID)
        packInt(byteBuffer, this.MoneyData_Field.GridX)
        packInt(byteBuffer, this.MoneyData_Field.GridY)
        packVariable(byteBuffer, this.MoneyData_Field.Description, 1)
    }

    public Unit UnpackPayload(ByteBuffer byteBuffer) {
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
