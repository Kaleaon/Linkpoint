package com.linkpoint.slproto.messages

import com.google.common.base.Ascii
import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class RegionPresenceRequestByRegionID : SLMessage() {
    public ArrayList<RegionData> RegionData_Fields = ArrayList<>()

    @JvmStatic
    class RegionData {
        public UUID RegionID
    }

    public RegionPresenceRequestByRegionID() {
        this.zeroCoded = false
    }

    public Int CalcPayloadSize() {
        return (this.RegionData_Fields.size() * 16) + 5
    }

    public Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleRegionPresenceRequestByRegionID(this)
    }

    public Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put(Ascii.SO)
        byteBuffer.put((Byte) this.RegionData_Fields.size())
        for (RegionData regionData : this.RegionData_Fields) {
            packUUID(byteBuffer, regionData.RegionID)
        }
    }

    public Unit UnpackPayload(ByteBuffer byteBuffer) {
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            RegionData regionData = RegionData()
            regionData.RegionID = unpackUUID(byteBuffer)
            this.RegionData_Fields.add(regionData)
        }
    }
}
