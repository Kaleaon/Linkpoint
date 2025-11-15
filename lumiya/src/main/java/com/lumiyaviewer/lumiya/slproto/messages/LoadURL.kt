package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class LoadURL : SLMessage {
    Data Data_Field = Data()

    class Data {
        ByteArray Message
        UUID ObjectID
        ByteArray ObjectName
        UUID OwnerID
        Boolean OwnerIsGroup
        ByteArray URL
    }

    constructor() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return this.Data_Field.ObjectName.length + 1 + 16 + 16 + 1 + 1 + this.Data_Field.Message.length + 1 + this.Data_Field.URL.length + 4
    }

    fun Handle(sLMessageHandler: SLMessageHandler): Unit {
        sLMessageHandler.HandleLoadURL(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer): Unit {
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

    fun UnpackPayload(byteBuffer: ByteBuffer): Unit {
        this.Data_Field.ObjectName = unpackVariable(byteBuffer, 1)
        this.Data_Field.ObjectID = unpackUUID(byteBuffer)
        this.Data_Field.OwnerID = unpackUUID(byteBuffer)
        this.Data_Field.OwnerIsGroup = unpackBoolean(byteBuffer)
        this.Data_Field.Message = unpackVariable(byteBuffer, 1)
        this.Data_Field.URL = unpackVariable(byteBuffer, 1)
    }
}
