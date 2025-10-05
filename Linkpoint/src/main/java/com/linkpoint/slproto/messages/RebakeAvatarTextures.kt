package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class RebakeAvatarTextures : SLMessage() {
    val TextureData_Field = TextureData()

    class TextureData {
        var TextureID: UUID? = null
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int = 20

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleRebakeAvatarTextures(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(0.toByte())
        buffer.put(87.toByte())
        packUUID(buffer, TextureData_Field.TextureID)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        TextureData_Field.TextureID = unpackUUID(buffer)
    }
}