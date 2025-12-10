package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
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

    fun CalcPayloadSize(): Int {
        return 20
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleRebakeAvatarTextures(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 87)
        packUUID(byteBuffer, this.TextureData_Field.TextureID)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.TextureData_Field.TextureID = unpackUUID(byteBuffer)
    }
}
