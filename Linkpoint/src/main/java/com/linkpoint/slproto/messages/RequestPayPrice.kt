package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class RequestPayPrice : SLMessage() {
    val ObjectData_Field = ObjectData()

    class ObjectData {
        var ObjectID: UUID? = null
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int = 20

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleRequestPayPrice(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(0.toByte())
        buffer.put((-95).toByte())
        packUUID(buffer, ObjectData_Field.ObjectID)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        ObjectData_Field.ObjectID = unpackUUID(buffer)
    }
}