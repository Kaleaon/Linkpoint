package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class GetScriptRunningMessage : SLMessage() {
    var objectId: UUID = UUID(0L, 0L)
    var itemId: UUID = UUID(0L, 0L)


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, objectId)
        packUUID(buffer, itemId)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        objectId = unpackUUID(buffer)
        itemId = unpackUUID(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF00F3.toInt()

    override fun getMessageName(): String = "GetScriptRunning"
}
