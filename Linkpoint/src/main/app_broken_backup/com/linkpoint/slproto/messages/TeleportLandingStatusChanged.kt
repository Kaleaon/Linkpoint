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

    fun CalcPayloadSize(): Int {
        return 12
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleTeleportLandingStatusChanged(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -109)
        packLong(byteBuffer, this.RegionData_Field.RegionHandle)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.RegionData_Field.RegionHandle = unpackLong(byteBuffer)
    }
}
