package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class MuteListUpdateMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var filename: ByteArray = ByteArray(0)


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packVariable(buffer, filename, 1)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        filename = unpackVariable(buffer, 1)
    }

    override fun getMessageID(): Int = 0xFFFF013E.toInt()

    override fun getMessageName(): String = "MuteListUpdate"
}
