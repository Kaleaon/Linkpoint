package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class RebakeAvatarTexturesMessage : SLMessage() {
    var textureId: UUID = UUID(0L, 0L)


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, textureId)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        textureId = unpackUUID(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF0057.toInt()

    override fun getMessageName(): String = "RebakeAvatarTextures"
}
