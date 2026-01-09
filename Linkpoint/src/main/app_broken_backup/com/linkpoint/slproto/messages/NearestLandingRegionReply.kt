package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class NearestLandingRegionReply : SLMessage {
    LandingRegionData LandingRegionData_Field = LandingRegionData()

    class LandingRegionData {
        Long RegionHandle
    }

    NearestLandingRegionReply() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 12
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleNearestLandingRegionReply(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -111)
        packLong(byteBuffer, this.LandingRegionData_Field.RegionHandle)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.LandingRegionData_Field.RegionHandle = unpackLong(byteBuffer)
    }
}
