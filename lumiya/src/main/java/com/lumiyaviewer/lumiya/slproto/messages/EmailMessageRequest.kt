package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class EmailMessageRequest : SLMessage {
    DataBlock DataBlock_Field = DataBlock()

    class DataBlock {
        byte[] FromAddress
        UUID ObjectID
        byte[] Subject
    }

    EmailMessageRequest() {
        this.zeroCoded = false
    }

    Int CalcPayloadSize() {
        return this.DataBlock_Field.FromAddress.length + 17 + 1 + this.DataBlock_Field.Subject.length + 4
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleEmailMessageRequest(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) 79)
        packUUID(byteBuffer, this.DataBlock_Field.ObjectID)
        packVariable(byteBuffer, this.DataBlock_Field.FromAddress, 1)
        packVariable(byteBuffer, this.DataBlock_Field.Subject, 1)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.DataBlock_Field.ObjectID = unpackUUID(byteBuffer)
        this.DataBlock_Field.FromAddress = unpackVariable(byteBuffer, 1)
        this.DataBlock_Field.Subject = unpackVariable(byteBuffer, 1)
    }
}
