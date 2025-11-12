package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class DerezContainerMessage : SLMessage() {
    var objectId: UUID = UUID(0L, 0L)
    var delete: Boolean = false


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, objectId)
        packBoolean(buffer, delete)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        objectId = unpackUUID(buffer)
        delete = unpackBoolean(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF0068

    override fun getMessageName(): String = "DerezContainer"
}
