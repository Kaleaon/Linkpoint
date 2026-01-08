package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class RegionIdAndHandleReplyMessage : SLMessage() {
    var regionId: UUID = UUID(0L, 0L)
    var regionHandle: Long = 0L


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, regionId)
        packLong(buffer, regionHandle)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        regionId = unpackUUID(buffer)
        regionHandle = unpackLong(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF0136.toInt()

    override fun getMessageName(): String = "RegionIDAndHandleReply"
}
