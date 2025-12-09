package com.linkpoint.slproto.messages

import com.google.common.base.Ascii
import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class RegionPresenceRequestByRegionID : SLMessage {
    ArrayList<RegionData> RegionData_Fields = ArrayList<>()

    class RegionData {
        UUID RegionID
    }

    RegionPresenceRequestByRegionID() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return (this.RegionData_Fields.size() * 16) + 5
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleRegionPresenceRequestByRegionID(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put(Ascii.SO)
        byteBuffer.put((this as Byte).RegionData_Fields.size())
        for (RegionData regionData : this.RegionData_Fields) {
            packUUID(byteBuffer, regionData.RegionID)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            RegionData regionData = RegionData()
            regionData.RegionID = unpackUUID(byteBuffer)
            this.RegionData_Fields.add(regionData)
        }
    }
}
