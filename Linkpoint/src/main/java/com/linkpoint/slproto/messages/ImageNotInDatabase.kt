package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ImageNotInDatabase : SLMessage() {
    val ImageID_Field = ImageID()

    class ImageID {
        var ID: UUID? = null
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int = 20

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleImageNotInDatabase(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(0.toByte())
        buffer.put(86.toByte())
        packUUID(buffer, ImageID_Field.ID)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        ImageID_Field.ID = unpackUUID(buffer)
    }
}