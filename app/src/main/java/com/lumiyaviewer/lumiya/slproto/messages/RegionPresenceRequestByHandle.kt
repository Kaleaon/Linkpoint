package com.lumiyaviewer.lumiya.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList

class RegionPresenceRequestByHandle : SLMessage {
    ArrayList<RegionData> RegionData_Fields = ArrayList<>()

    class RegionData {
        Long RegionHandle
    }

    RegionPresenceRequestByHandle() {
        this.zeroCoded = false
    }

    Int CalcPayloadSize() {
        return (this.RegionData_Fields.size() * 8) + 5
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleRegionPresenceRequestByHandle(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 15)
        byteBuffer.put((Byte) this.RegionData_Fields.size())
        for (RegionData regionData : this.RegionData_Fields) {
            packLong(byteBuffer, regionData.RegionHandle)
        }
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            RegionData regionData = RegionData()
            regionData.RegionHandle = unpackLong(byteBuffer)
            this.RegionData_Fields.add(regionData)
        }
    }
}
