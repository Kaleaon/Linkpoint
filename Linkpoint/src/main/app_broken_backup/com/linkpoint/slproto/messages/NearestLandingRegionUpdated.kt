package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class NearestLandingRegionUpdated : SLMessage {
    RegionData RegionData_Field = RegionData()

    class RegionData {
        Long RegionHandle
    }

    NearestLandingRegionUpdated() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 12
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleNearestLandingRegionUpdated(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -110)
        packLong(byteBuffer, this.RegionData_Field.RegionHandle)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.RegionData_Field.RegionHandle = unpackLong(byteBuffer)
    }
}
