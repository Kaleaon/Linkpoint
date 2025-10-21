package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class EmailMessageReply : SLMessage() {
    public DataBlock DataBlock_Field = DataBlock()

    @JvmStatic
    class DataBlock {
        public Byte[] Data
        public Byte[] FromAddress
        public Byte[] MailFilter
        public Int More
        public UUID ObjectID
        public Byte[] Subject
        public Int Time
    }

    public EmailMessageReply() {
        this.zeroCoded = false
    }

    public Int CalcPayloadSize() {
        return this.DataBlock_Field.FromAddress.length + 25 + 1 + this.DataBlock_Field.Subject.length + 2 + this.DataBlock_Field.Data.length + 1 + this.DataBlock_Field.MailFilter.length + 4
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleEmailMessageReply(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 80)
        packUUID(byteBuffer, this.DataBlock_Field.ObjectID)
        packInt(byteBuffer, this.DataBlock_Field.More)
        packInt(byteBuffer, this.DataBlock_Field.Time)
        packVariable(byteBuffer, this.DataBlock_Field.FromAddress, 1)
        packVariable(byteBuffer, this.DataBlock_Field.Subject, 1)
        packVariable(byteBuffer, this.DataBlock_Field.Data, 2)
        packVariable(byteBuffer, this.DataBlock_Field.MailFilter, 1)
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.DataBlock_Field.ObjectID = unpackUUID(byteBuffer)
        this.DataBlock_Field.More = unpackInt(byteBuffer)
        this.DataBlock_Field.Time = unpackInt(byteBuffer)
        this.DataBlock_Field.FromAddress = unpackVariable(byteBuffer, 1)
        this.DataBlock_Field.Subject = unpackVariable(byteBuffer, 1)
        this.DataBlock_Field.Data = unpackVariable(byteBuffer, 2)
        this.DataBlock_Field.MailFilter = unpackVariable(byteBuffer, 1)
    }
}
