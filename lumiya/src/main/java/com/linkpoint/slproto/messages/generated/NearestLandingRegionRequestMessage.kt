package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class NearestLandingRegionRequestMessage : SLMessage() {
    var regionHandle: Long = 0L


    override fun packPayload(buffer: ByteBuffer) {
        packLong(buffer, regionHandle)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        regionHandle = unpackLong(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF0090

    override fun getMessageName(): String = "NearestLandingRegionRequest"
}
