package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class EmailMessageRequest : SLMessage() {
    public DataBlock DataBlock_Field = DataBlock()

    @JvmStatic
    class DataBlock {
        public ByteArray FromAddress
        public UUID ObjectID
        public ByteArray Subject
    }

    public EmailMessageRequest() {
        this.zeroCoded = false
    }

    public fun CalcPayloadSize(): Int {
        return this.DataBlock_Field.FromAddress.length + 17 + 1 + this.DataBlock_Field.Subject.length + 4
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleEmailMessageRequest(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 79)
        packUUID(byteBuffer, this.DataBlock_Field.ObjectID)
        packVariable(byteBuffer, this.DataBlock_Field.FromAddress, 1)
        packVariable(byteBuffer, this.DataBlock_Field.Subject, 1)
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.DataBlock_Field.ObjectID = unpackUUID(byteBuffer)
        this.DataBlock_Field.FromAddress = unpackVariable(byteBuffer, 1)
        this.DataBlock_Field.Subject = unpackVariable(byteBuffer, 1)
    }
}
