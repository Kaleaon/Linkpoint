package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class NearestLandingRegionUpdatedMessage : SLMessage() {
    var regionHandle: Long = 0L


    override fun packPayload(buffer: ByteBuffer) {
        packLong(buffer, regionHandle)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        regionHandle = unpackLong(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF0092.toInt()

    override fun getMessageName(): String = "NearestLandingRegionUpdated"
}
