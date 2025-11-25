package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import java.nio.ByteBuffer

class CheckParcelSales : SLMessage {
    var RegionData_Fields: ArrayList<RegionData> = ArrayList()

    class RegionData {
        var RegionHandle: Long = 0
    }

    override fun CalcPayloadSize(): Int {
        return (this.RegionData_Fields.size * 8) + 5
    }

    override fun Handle(sLMessageHandler: SLMessageHandler) {
        // sLMessageHandler.HandleCheckParcelSales(this)
    }

    override fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put(0.toByte())
        byteBuffer.put((-31).toByte())
        byteBuffer.put(this.RegionData_Fields.size.toByte())
        for (regionData in this.RegionData_Fields) {
            byteBuffer.putLong(regionData.RegionHandle)
        }
    }

    override fun UnpackPayload(byteBuffer: ByteBuffer) {
        val b = byteBuffer.get().toInt() and 0xFF
        for (i in 0 until b) {
            val regionData = RegionData()
            regionData.RegionHandle = byteBuffer.long
            this.RegionData_Fields.add(regionData)
        }
    }
}
