package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.UUID

class TeleportLocalMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var locationId: Int = 0
    var position: LLVector3 = LLVector3()
    var lookAt: LLVector3 = LLVector3()
    var teleportFlags: Int = 0


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packInt(buffer, locationId)
        position.pack(buffer)
        lookAt.pack(buffer)
        packInt(buffer, teleportFlags)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        locationId = unpackInt(buffer)
        position = LLVector3.unpack(buffer)
        lookAt = LLVector3.unpack(buffer)
        teleportFlags = unpackInt(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF0040.toInt()

    override fun getMessageName(): String = "TeleportLocal"
}
