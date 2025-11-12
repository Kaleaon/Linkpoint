package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class UseCachedMuteListMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF013F

    override fun getMessageName(): String = "UseCachedMuteList"
}
