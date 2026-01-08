package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer

class NearestLandingRegionUpdated : SLMessage {
    RegionData RegionData_Field = RegionData()

    class RegionData {
        Long RegionHandle
    }

    NearestLandingRegionUpdated() {
        this.zeroCoded = false
    }

    Int CalcPayloadSize() {
        return 12
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleNearestLandingRegionUpdated(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -110)
        packLong(byteBuffer, this.RegionData_Field.RegionHandle)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.RegionData_Field.RegionHandle = unpackLong(byteBuffer)
    }
}
