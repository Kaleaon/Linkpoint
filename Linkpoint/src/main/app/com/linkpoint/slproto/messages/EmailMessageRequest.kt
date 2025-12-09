package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class EmailMessageRequest : SLMessage {
    DataBlock DataBlock_Field = DataBlock()

    class DataBlock {
        ByteArray FromAddress
        UUID ObjectID
        ByteArray Subject
    }

    EmailMessageRequest() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return this.DataBlock_Field.FromAddress.size + 17 + 1 + this.DataBlock_Field.Subject.size + 4
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleEmailMessageRequest(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) 79)
        packUUID(byteBuffer, this.DataBlock_Field.ObjectID)
        packVariable(byteBuffer, this.DataBlock_Field.FromAddress, 1)
        packVariable(byteBuffer, this.DataBlock_Field.Subject, 1)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.DataBlock_Field.ObjectID = unpackUUID(byteBuffer)
        this.DataBlock_Field.FromAddress = unpackVariable(byteBuffer, 1)
        this.DataBlock_Field.Subject = unpackVariable(byteBuffer, 1)
    }
}
