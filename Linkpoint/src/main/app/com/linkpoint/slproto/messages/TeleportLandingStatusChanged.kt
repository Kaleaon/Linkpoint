package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class TeleportLandingStatusChanged : SLMessage {
    RegionData RegionData_Field = RegionData()

    class RegionData {
        Long RegionHandle
    }

    TeleportLandingStatusChanged() {
        this.zeroCoded = false
    }

    Int CalcPayloadSize() {
        return 12
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleTeleportLandingStatusChanged(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -109)
        packLong(byteBuffer, this.RegionData_Field.RegionHandle)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.RegionData_Field.RegionHandle = unpackLong(byteBuffer)
    }
}
