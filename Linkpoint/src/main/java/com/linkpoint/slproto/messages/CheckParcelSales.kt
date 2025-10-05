package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class CheckParcelSales : SLMessage() {
    val RegionData_Fields = ArrayList<RegionData>()

    class RegionData {
        var RegionHandle: Long = 0
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int = (RegionData_Fields.size * 8) + 5

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleCheckParcelSales(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(0.toByte())
        buffer.put((-31).toByte())
        buffer.put(RegionData_Fields.size.toByte())
        for (regionData in RegionData_Fields) {
            packLong(buffer, regionData.RegionHandle)
        }
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        val count = buffer.get().toInt() and UnsignedBytes.MAX_VALUE.toInt()
        for (i in 0 until count) {
            val regionData = RegionData()
            regionData.RegionHandle = unpackLong(buffer)
            RegionData_Fields.add(regionData)
        }
    }
}