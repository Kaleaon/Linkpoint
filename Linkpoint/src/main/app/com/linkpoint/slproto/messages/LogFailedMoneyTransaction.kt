package com.linkpoint.slproto.messages

import com.google.common.base.Ascii
import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.net.Inet4Address
import java.nio.ByteBuffer
import java.util.UUID

class LogFailedMoneyTransaction : SLMessage {
    TransactionData TransactionData_Field = TransactionData()

    class TransactionData {
        Int Amount
        UUID DestID
        Int FailureType
        Int Flags
        Int GridX
        Int GridY
        Inet4Address SimulatorIP
        UUID SourceID
        UUID TransactionID
        Int TransactionTime
        Int TransactionType
    }

    constructor() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 78
    }

    fun Handle(sLMessageHandler: SLMessageHandler): Unit {
        sLMessageHandler.HandleLogFailedMoneyTransaction(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put(Ascii.DC4)
        packUUID(byteBuffer, this.TransactionData_Field.TransactionID)
        packInt(byteBuffer, this.TransactionData_Field.TransactionTime)
        packInt(byteBuffer, this.TransactionData_Field.TransactionType)
        packUUID(byteBuffer, this.TransactionData_Field.SourceID)
        packUUID(byteBuffer, this.TransactionData_Field.DestID)
        packByte(byteBuffer, (Byte) this.TransactionData_Field.Flags)
        packInt(byteBuffer, this.TransactionData_Field.Amount)
        packIPAddress(byteBuffer, this.TransactionData_Field.SimulatorIP)
        packInt(byteBuffer, this.TransactionData_Field.GridX)
        packInt(byteBuffer, this.TransactionData_Field.GridY)
        packByte(byteBuffer, (Byte) this.TransactionData_Field.FailureType)
    }

    fun UnpackPayload(byteBuffer: ByteBuffer): Unit {
        this.TransactionData_Field.TransactionID = unpackUUID(byteBuffer)
        this.TransactionData_Field.TransactionTime = unpackInt(byteBuffer)
        this.TransactionData_Field.TransactionType = unpackInt(byteBuffer)
        this.TransactionData_Field.SourceID = unpackUUID(byteBuffer)
        this.TransactionData_Field.DestID = unpackUUID(byteBuffer)
        this.TransactionData_Field.Flags = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.TransactionData_Field.Amount = unpackInt(byteBuffer)
        this.TransactionData_Field.SimulatorIP = unpackIPAddress(byteBuffer)
        this.TransactionData_Field.GridX = unpackInt(byteBuffer)
        this.TransactionData_Field.GridY = unpackInt(byteBuffer)
        this.TransactionData_Field.FailureType = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
    }
}
