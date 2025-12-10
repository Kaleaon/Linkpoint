package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ImageNotInDatabase : SLMessage {
    ImageID ImageID_Field = ImageID()

    class ImageID {
        UUID ID
    }

    ImageNotInDatabase() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 20
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleImageNotInDatabase(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) 86)
        packUUID(byteBuffer, this.ImageID_Field.ID)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.ImageID_Field.ID = unpackUUID(byteBuffer)
    }
}
