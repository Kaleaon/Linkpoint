package com.linkpoint.slproto.messages

import com.google.common.base.Ascii
import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.net.Inet4Address
import java.nio.ByteBuffer
import java.util.UUID

class LogFailedMoneyTransaction : SLMessage() {
    public TransactionData TransactionData_Field = TransactionData()

    @JvmStatic
    class TransactionData {
        public Int Amount
        public UUID DestID
        public Int FailureType
        public Int Flags
        public Int GridX
        public Int GridY
        public Inet4Address SimulatorIP
        public UUID SourceID
        public UUID TransactionID
        public Int TransactionTime
        public Int TransactionType
    }

    public LogFailedMoneyTransaction() {
        this.zeroCoded = false
    }

    public Int CalcPayloadSize() {
        return 78
    }

    public Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleLogFailedMoneyTransaction(this)
    }

    public Unit PackPayload(ByteBuffer byteBuffer) {
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

    public Unit UnpackPayload(ByteBuffer byteBuffer) {
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
