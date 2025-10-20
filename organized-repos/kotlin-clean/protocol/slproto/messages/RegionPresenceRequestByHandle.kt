package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList

class RegionPresenceRequestByHandle : SLMessage() {
    public ArrayList<RegionData> RegionData_Fields = ArrayList<>()

    @JvmStatic
    class RegionData {
        public Long RegionHandle
    }

    public RegionPresenceRequestByHandle() {
        this.zeroCoded = false
    }

    public Int CalcPayloadSize() {
        return (this.RegionData_Fields.size() * 8) + 5
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleRegionPresenceRequestByHandle(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 15)
        byteBuffer.put((Byte) this.RegionData_Fields.size())
        for (RegionData regionData : this.RegionData_Fields) {
            packLong(byteBuffer, regionData.RegionHandle)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            RegionData regionData = RegionData()
            regionData.RegionHandle = unpackLong(byteBuffer)
            this.RegionData_Fields.add(regionData)
        }
    }
}
