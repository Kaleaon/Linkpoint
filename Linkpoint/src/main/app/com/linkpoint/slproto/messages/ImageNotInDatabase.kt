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

    Int CalcPayloadSize() {
        return 20
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleImageNotInDatabase(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) 86)
        packUUID(byteBuffer, this.ImageID_Field.ID)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.ImageID_Field.ID = unpackUUID(byteBuffer)
    }
}
