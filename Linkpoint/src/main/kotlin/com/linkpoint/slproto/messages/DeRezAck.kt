package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class DeRezAck : SLMessage() {
    public TransactionData TransactionData_Field = TransactionData()

    @JvmStatic
    class TransactionData {
        public Boolean Success
        public UUID TransactionID
    }

    public DeRezAck() {
        this.zeroCoded = false
    }

    public fun CalcPayloadSize(): Int {
        return 21
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleDeRezAck(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 36)
        packUUID(byteBuffer, this.TransactionData_Field.TransactionID)
        packBoolean(byteBuffer, this.TransactionData_Field.Success)
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.TransactionData_Field.TransactionID = unpackUUID(byteBuffer)
        this.TransactionData_Field.Success = unpackBoolean(byteBuffer)
    }
}
