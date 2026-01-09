package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class NearestLandingRegionRequest : SLMessage {
    RequestingRegionData RequestingRegionData_Field = RequestingRegionData()

    class RequestingRegionData {
        Long RegionHandle
    }

    NearestLandingRegionRequest() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 12
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleNearestLandingRegionRequest(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -112)
        packLong(byteBuffer, this.RequestingRegionData_Field.RegionHandle)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.RequestingRegionData_Field.RegionHandle = unpackLong(byteBuffer)
    }
}
