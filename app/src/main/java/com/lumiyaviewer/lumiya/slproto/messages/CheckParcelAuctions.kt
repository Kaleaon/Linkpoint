package com.lumiyaviewer.lumiya.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList

class CheckParcelAuctions : SLMessage {
    ArrayList<RegionData> RegionData_Fields = ArrayList<>()

    class RegionData {
        Long RegionHandle
    }

    CheckParcelAuctions() {
        this.zeroCoded = false
    }

    Int CalcPayloadSize() {
        return (this.RegionData_Fields.size() * 8) + 5
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleCheckParcelAuctions(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) -23)
        byteBuffer.put((byte) this.RegionData_Fields.size())
        for (RegionData regionData : this.RegionData_Fields) {
            packLong(byteBuffer, regionData.RegionHandle)
        }
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            RegionData regionData = RegionData()
            regionData.RegionHandle = unpackLong(byteBuffer)
            this.RegionData_Fields.add(regionData)
        }
    }
}
