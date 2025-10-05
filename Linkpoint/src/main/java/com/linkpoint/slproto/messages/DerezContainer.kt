package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class DerezContainer : SLMessage() {
    val Data_Field = Data()

    class Data {
        var ObjectID: UUID? = null
        var Delete: Boolean = false
    }

    init {
        zeroCoded = true
    }

    override fun CalcPayloadSize(): Int = 21

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleDerezContainer(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(0.toByte())
        buffer.put(104.toByte())
        packUUID(buffer, Data_Field.ObjectID)
        packBoolean(buffer, Data_Field.Delete)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        Data_Field.ObjectID = unpackUUID(buffer)
        Data_Field.Delete = unpackBoolean(buffer)
    }
}