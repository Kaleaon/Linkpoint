package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
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

    Int CalcPayloadSize() {
        return 21
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleDeRezAck(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) 36)
        packUUID(byteBuffer, this.TransactionData_Field.TransactionID)
        packBoolean(byteBuffer, this.TransactionData_Field.Success)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.TransactionData_Field.TransactionID = unpackUUID(byteBuffer)
        this.TransactionData_Field.Success = unpackBoolean(byteBuffer)
    }
}
