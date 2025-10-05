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

    public Int CalcPayloadSize() {
        return 21
    }

    public Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleDeRezAck(this)
    }

    public Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 36)
        packUUID(byteBuffer, this.TransactionData_Field.TransactionID)
        packBoolean(byteBuffer, this.TransactionData_Field.Success)
    }

    public Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.TransactionData_Field.TransactionID = unpackUUID(byteBuffer)
        this.TransactionData_Field.Success = unpackBoolean(byteBuffer)
    }
}
