package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class DeRezAck : SLMessage {
    TransactionData TransactionData_Field = TransactionData()

    class TransactionData {
        Boolean Success
        UUID TransactionID
    }

    DeRezAck() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 21
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleDeRezAck(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) 36)
        packUUID(byteBuffer, this.TransactionData_Field.TransactionID)
        packBoolean(byteBuffer, this.TransactionData_Field.Success)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.TransactionData_Field.TransactionID = unpackUUID(byteBuffer)
        this.TransactionData_Field.Success = unpackBoolean(byteBuffer)
    }
}
