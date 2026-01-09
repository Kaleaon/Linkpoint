package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class RequestPayPrice : SLMessage {
    ObjectData ObjectData_Field = ObjectData()

    class ObjectData {
        UUID ObjectID
    }

    RequestPayPrice() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 20
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleRequestPayPrice(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -95)
        packUUID(byteBuffer, this.ObjectData_Field.ObjectID)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.ObjectData_Field.ObjectID = unpackUUID(byteBuffer)
    }
}
