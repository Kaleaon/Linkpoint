package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class AttachedSoundMessage : SLMessage() {
    var soundId: UUID = UUID(0L, 0L)
    var objectId: UUID = UUID(0L, 0L)
    var ownerId: UUID = UUID(0L, 0L)
    var gain: Float = 0f
    var flags: Int = 0


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, soundId)
        packUUID(buffer, objectId)
        packUUID(buffer, ownerId)
        packFloat(buffer, gain)
        packByte(buffer, flags)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        soundId = unpackUUID(buffer)
        objectId = unpackUUID(buffer)
        ownerId = unpackUUID(buffer)
        gain = unpackFloat(buffer)
        flags = unpackByte(buffer)
    }

    override fun getMessageID(): Int = 0x0000FF0D

    override fun getMessageName(): String = "AttachedSound"
}
