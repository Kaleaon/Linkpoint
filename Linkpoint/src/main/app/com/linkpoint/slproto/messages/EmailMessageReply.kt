package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class EmailMessageReply : SLMessage {
    DataBlock DataBlock_Field = DataBlock()

    class DataBlock {
        ByteArray Data
        ByteArray FromAddress
        ByteArray MailFilter
        Int More
        UUID ObjectID
        ByteArray Subject
        Int Time
    }

    EmailMessageReply() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return this.DataBlock_Field.FromAddress.size + 25 + 1 + this.DataBlock_Field.Subject.size + 2 + this.DataBlock_Field.Data.size + 1 + this.DataBlock_Field.MailFilter.size + 4
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleEmailMessageReply(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) 80)
        packUUID(byteBuffer, this.DataBlock_Field.ObjectID)
        packInt(byteBuffer, this.DataBlock_Field.More)
        packInt(byteBuffer, this.DataBlock_Field.Time)
        packVariable(byteBuffer, this.DataBlock_Field.FromAddress, 1)
        packVariable(byteBuffer, this.DataBlock_Field.Subject, 1)
        packVariable(byteBuffer, this.DataBlock_Field.Data, 2)
        packVariable(byteBuffer, this.DataBlock_Field.MailFilter, 1)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.DataBlock_Field.ObjectID = unpackUUID(byteBuffer)
        this.DataBlock_Field.More = unpackInt(byteBuffer)
        this.DataBlock_Field.Time = unpackInt(byteBuffer)
        this.DataBlock_Field.FromAddress = unpackVariable(byteBuffer, 1)
        this.DataBlock_Field.Subject = unpackVariable(byteBuffer, 1)
        this.DataBlock_Field.Data = unpackVariable(byteBuffer, 2)
        this.DataBlock_Field.MailFilter = unpackVariable(byteBuffer, 1)
    }
}
