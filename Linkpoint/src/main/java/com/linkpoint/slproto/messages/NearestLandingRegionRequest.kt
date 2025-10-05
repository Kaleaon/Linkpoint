package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class NearestLandingRegionRequest : SLMessage() {
    val RequestingRegionData_Field = RequestingRegionData()

    class RequestingRegionData {
        var RegionHandle: Long = 0
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int = 12

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleNearestLandingRegionRequest(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(0.toByte())
        buffer.put((-112).toByte())
        packLong(buffer, RequestingRegionData_Field.RegionHandle)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        RequestingRegionData_Field.RegionHandle = unpackLong(buffer)
    }
}