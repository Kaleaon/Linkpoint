package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class AttachedSoundGainChangeMessage : SLMessage() {
    var objectId: UUID = UUID(0L, 0L)
    var gain: Float = 0f


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, objectId)
        packFloat(buffer, gain)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        objectId = unpackUUID(buffer)
        gain = unpackFloat(buffer)
    }

    override fun getMessageID(): Int = 0x0000FF0E

    override fun getMessageName(): String = "AttachedSoundGainChange"
}
