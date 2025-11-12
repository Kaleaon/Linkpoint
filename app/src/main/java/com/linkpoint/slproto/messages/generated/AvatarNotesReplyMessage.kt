package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class AvatarNotesReplyMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var targetId: UUID = UUID(0L, 0L)
    var notes: ByteArray = ByteArray(0)


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, targetId)
        packVariable(buffer, notes, 2)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        targetId = unpackUUID(buffer)
        notes = unpackVariable(buffer, 2)
    }

    override fun getMessageID(): Int = 0xFFFF00B0

    override fun getMessageName(): String = "AvatarNotesReply"
}
