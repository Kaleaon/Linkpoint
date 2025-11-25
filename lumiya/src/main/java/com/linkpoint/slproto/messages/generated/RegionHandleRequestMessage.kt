package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class RegionHandleRequestMessage : SLMessage() {
    var regionId: UUID = UUID(0L, 0L)


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, regionId)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        regionId = unpackUUID(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF0135.toInt()

    override fun getMessageName(): String = "RegionHandleRequest"
}
