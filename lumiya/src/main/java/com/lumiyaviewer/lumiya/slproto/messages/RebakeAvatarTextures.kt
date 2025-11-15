package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class RebakeAvatarTextures : SLMessage {
    TextureData TextureData_Field = TextureData()

    class TextureData {
        UUID TextureID
    }

    RebakeAvatarTextures() {
        this.zeroCoded = false
    }

    Int CalcPayloadSize() {
        return 20
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleRebakeAvatarTextures(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 87)
        packUUID(byteBuffer, this.TextureData_Field.TextureID)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.TextureData_Field.TextureID = unpackUUID(byteBuffer)
    }
}
