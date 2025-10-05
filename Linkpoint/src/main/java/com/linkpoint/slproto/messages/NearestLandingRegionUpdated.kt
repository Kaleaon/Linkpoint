package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class NearestLandingRegionUpdated : SLMessage() {
    val RegionData_Field = RegionData()

    class RegionData {
        var RegionHandle: Long = 0
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int = 12

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleNearestLandingRegionUpdated(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(0.toByte())
        buffer.put((-110).toByte())
        packLong(buffer, RegionData_Field.RegionHandle)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        RegionData_Field.RegionHandle = unpackLong(buffer)
    }
}