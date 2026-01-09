package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class DerezContainer : SLMessage {
    Data Data_Field = Data()

    class Data {
        Boolean Delete
        UUID ObjectID
    }

    DerezContainer() {
        this.zeroCoded = true
    }

    fun CalcPayloadSize(): Int {
        return 21
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleDerezContainer(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) 104)
        packUUID(byteBuffer, this.Data_Field.ObjectID)
        packBoolean(byteBuffer, this.Data_Field.Delete)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.Data_Field.ObjectID = unpackUUID(byteBuffer)
        this.Data_Field.Delete = unpackBoolean(byteBuffer)
    }
}
