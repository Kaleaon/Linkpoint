package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.UUID

class SoundTriggerMessage : SLMessage() {
    var soundId: UUID = UUID(0L, 0L)
    var ownerId: UUID = UUID(0L, 0L)
    var objectId: UUID = UUID(0L, 0L)
    var parentId: UUID = UUID(0L, 0L)
    var handle: Long = 0L
    var position: LLVector3 = LLVector3()
    var gain: Float = 0f


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, soundId)
        packUUID(buffer, ownerId)
        packUUID(buffer, objectId)
        packUUID(buffer, parentId)
        packLong(buffer, handle)
        position.pack(buffer)
        packFloat(buffer, gain)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        soundId = unpackUUID(buffer)
        ownerId = unpackUUID(buffer)
        objectId = unpackUUID(buffer)
        parentId = unpackUUID(buffer)
        handle = unpackLong(buffer)
        position = LLVector3.unpack(buffer)
        gain = unpackFloat(buffer)
    }

    override fun getMessageID(): Int = 0x0000001D

    override fun getMessageName(): String = "SoundTrigger"
}
