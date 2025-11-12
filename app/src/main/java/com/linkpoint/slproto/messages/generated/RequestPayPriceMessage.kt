package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class RequestPayPriceMessage : SLMessage() {
    var objectId: UUID = UUID(0L, 0L)


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, objectId)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        objectId = unpackUUID(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF00A1

    override fun getMessageName(): String = "RequestPayPrice"
}
