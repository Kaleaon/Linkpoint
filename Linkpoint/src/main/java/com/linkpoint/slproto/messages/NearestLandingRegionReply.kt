package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class NearestLandingRegionReply : SLMessage() {
    val LandingRegionData_Field = LandingRegionData()

    class LandingRegionData {
        var RegionHandle: Long = 0
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int = 12

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleNearestLandingRegionReply(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(0.toByte())
        buffer.put((-111).toByte())
        packLong(buffer, LandingRegionData_Field.RegionHandle)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        LandingRegionData_Field.RegionHandle = unpackLong(buffer)
    }
}