package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
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

    fun CalcPayloadSize(): Int {
        return (this.RegionData_Fields.size() * 8) + 5
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleRegionPresenceRequestByHandle(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 15)
        byteBuffer.put((this as Byte).RegionData_Fields.size())
        for (RegionData regionData : this.RegionData_Fields) {
            packLong(byteBuffer, regionData.RegionHandle)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            RegionData regionData = RegionData()
            regionData.RegionHandle = unpackLong(byteBuffer)
            this.RegionData_Fields.add(regionData)
        }
    }
}
