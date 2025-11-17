package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class InternalScriptMail : SLMessage {
    DataBlock DataBlock_Field = DataBlock()

    class DataBlock {
        byte[] Body
        byte[] From
        byte[] Subject
        UUID To
    }

    InternalScriptMail() {
        this.zeroCoded = false
    }

    Int CalcPayloadSize() {
        return this.DataBlock_Field.From.length + 1 + 16 + 1 + this.DataBlock_Field.Subject.length + 2 + this.DataBlock_Field.Body.length + 2
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleInternalScriptMail(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put((byte) -1)
        byteBuffer.put((byte) 16)
        packVariable(byteBuffer, this.DataBlock_Field.From, 1)
        packUUID(byteBuffer, this.DataBlock_Field.To)
        packVariable(byteBuffer, this.DataBlock_Field.Subject, 1)
        packVariable(byteBuffer, this.DataBlock_Field.Body, 2)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.DataBlock_Field.From = unpackVariable(byteBuffer, 1)
        this.DataBlock_Field.To = unpackUUID(byteBuffer)
        this.DataBlock_Field.Subject = unpackVariable(byteBuffer, 1)
        this.DataBlock_Field.Body = unpackVariable(byteBuffer, 2)
    }
}
