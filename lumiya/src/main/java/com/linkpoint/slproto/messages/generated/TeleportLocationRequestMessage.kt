package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.UUID

class TeleportLocationRequestMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var regionHandle: Long = 0L
    var position: LLVector3 = LLVector3()
    var lookAt: LLVector3 = LLVector3()


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packLong(buffer, regionHandle)
        position.pack(buffer)
        lookAt.pack(buffer)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        regionHandle = unpackLong(buffer)
        position = LLVector3.unpack(buffer)
        lookAt = LLVector3.unpack(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF003F.toInt()

    override fun getMessageName(): String = "TeleportLocationRequest"
}
