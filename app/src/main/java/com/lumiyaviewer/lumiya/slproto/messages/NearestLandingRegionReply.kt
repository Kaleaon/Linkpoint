package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer

class NearestLandingRegionReply : SLMessage {
    LandingRegionData LandingRegionData_Field = LandingRegionData()

    class LandingRegionData {
        Long RegionHandle
    }

    NearestLandingRegionReply() {
        this.zeroCoded = false
    }

    Int CalcPayloadSize() {
        return 12
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleNearestLandingRegionReply(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -111)
        packLong(byteBuffer, this.LandingRegionData_Field.RegionHandle)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.LandingRegionData_Field.RegionHandle = unpackLong(byteBuffer)
    }
}
