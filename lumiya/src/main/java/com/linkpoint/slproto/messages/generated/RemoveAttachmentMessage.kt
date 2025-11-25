package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class RemoveAttachmentMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var attachmentPoint: Int = 0
    var itemId: UUID = UUID(0L, 0L)


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packByte(buffer, attachmentPoint)
        packUUID(buffer, itemId)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        attachmentPoint = unpackByte(buffer)
        itemId = unpackUUID(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF014C.toInt()

    override fun getMessageName(): String = "RemoveAttachment"
}
