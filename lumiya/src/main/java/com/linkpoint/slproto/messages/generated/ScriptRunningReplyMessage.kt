package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ScriptRunningReplyMessage : SLMessage() {
    var objectId: UUID = UUID(0L, 0L)
    var itemId: UUID = UUID(0L, 0L)
    var running: Boolean = false


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, objectId)
        packUUID(buffer, itemId)
        packBoolean(buffer, running)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        objectId = unpackUUID(buffer)
        itemId = unpackUUID(buffer)
        running = unpackBoolean(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF00F4.toInt()

    override fun getMessageName(): String = "ScriptRunningReply"
}
