package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class LiveHelpGroupRequestMessage : SLMessage() {
    var requestId: UUID = UUID(0L, 0L)
    var agentId: UUID = UUID(0L, 0L)


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, requestId)
        packUUID(buffer, agentId)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        requestId = unpackUUID(buffer)
        agentId = unpackUUID(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF017B.toInt()

    override fun getMessageName(): String = "LiveHelpGroupRequest"
}
