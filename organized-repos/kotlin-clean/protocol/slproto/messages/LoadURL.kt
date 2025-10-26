package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class LoadURL : SLMessage() {
    public Data Data_Field = Data()

    @JvmStatic
    class Data {
        public ByteArray Message
        public UUID ObjectID
        public ByteArray ObjectName
        public UUID OwnerID
        public Boolean OwnerIsGroup
        public ByteArray URL
    }

    public LoadURL() {
        this.zeroCoded = false
    }

    public Int CalcPayloadSize() {
        return this.Data_Field.ObjectName.length + 1 + 16 + 16 + 1 + 1 + this.Data_Field.Message.length + 1 + this.Data_Field.URL.length + 4
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleLoadURL(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -62)
        packVariable(byteBuffer, this.Data_Field.ObjectName, 1)
        packUUID(byteBuffer, this.Data_Field.ObjectID)
        packUUID(byteBuffer, this.Data_Field.OwnerID)
        packBoolean(byteBuffer, this.Data_Field.OwnerIsGroup)
        packVariable(byteBuffer, this.Data_Field.Message, 1)
        packVariable(byteBuffer, this.Data_Field.URL, 1)
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.Data_Field.ObjectName = unpackVariable(byteBuffer, 1)
        this.Data_Field.ObjectID = unpackUUID(byteBuffer)
        this.Data_Field.OwnerID = unpackUUID(byteBuffer)
        this.Data_Field.OwnerIsGroup = unpackBoolean(byteBuffer)
        this.Data_Field.Message = unpackVariable(byteBuffer, 1)
        this.Data_Field.URL = unpackVariable(byteBuffer, 1)
    }
}
