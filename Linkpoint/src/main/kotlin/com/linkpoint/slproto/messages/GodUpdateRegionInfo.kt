package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class GodUpdateRegionInfo : SLMessage() {
    public AgentData AgentData_Field
    public ArrayList<RegionInfo2> RegionInfo2_Fields = ArrayList<>()
    public RegionInfo RegionInfo_Field

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID SessionID
    }

    @JvmStatic
    class RegionInfo {
        public Float BillableFactor
        public Int EstateID
        public Int ParentEstateID
        public Int PricePerMeter
        public Int RedirectGridX
        public Int RedirectGridY
        public Int RegionFlags
        public ByteArray SimName
    }

    @JvmStatic
    class RegionInfo2 {
        public Long RegionFlagsExtended
    }

    public GodUpdateRegionInfo() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
        this.RegionInfo_Field = RegionInfo()
    }

    public fun CalcPayloadSize(): Int {
        return this.RegionInfo_Field.SimName.length + 1 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 36 + 1 + (this.RegionInfo2_Fields.size() * 8)
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleGodUpdateRegionInfo(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -113)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packVariable(byteBuffer, this.RegionInfo_Field.SimName, 1)
        packInt(byteBuffer, this.RegionInfo_Field.EstateID)
        packInt(byteBuffer, this.RegionInfo_Field.ParentEstateID)
        packInt(byteBuffer, this.RegionInfo_Field.RegionFlags)
        packFloat(byteBuffer, this.RegionInfo_Field.BillableFactor)
        packInt(byteBuffer, this.RegionInfo_Field.PricePerMeter)
        packInt(byteBuffer, this.RegionInfo_Field.RedirectGridX)
        packInt(byteBuffer, this.RegionInfo_Field.RedirectGridY)
        byteBuffer.put((Byte) this.RegionInfo2_Fields.size())
        for (RegionInfo2 regionInfo2 : this.RegionInfo2_Fields) {
            packLong(byteBuffer, regionInfo2.RegionFlagsExtended)
        }
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.RegionInfo_Field.SimName = unpackVariable(byteBuffer, 1)
        this.RegionInfo_Field.EstateID = unpackInt(byteBuffer)
        this.RegionInfo_Field.ParentEstateID = unpackInt(byteBuffer)
        this.RegionInfo_Field.RegionFlags = unpackInt(byteBuffer)
        this.RegionInfo_Field.BillableFactor = unpackFloat(byteBuffer)
        this.RegionInfo_Field.PricePerMeter = unpackInt(byteBuffer)
        this.RegionInfo_Field.RedirectGridX = unpackInt(byteBuffer)
        this.RegionInfo_Field.RedirectGridY = unpackInt(byteBuffer)
        val b: Byte = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            val regionInfo2: RegionInfo2 = RegionInfo2()
            regionInfo2.RegionFlagsExtended = unpackLong(byteBuffer)
            this.RegionInfo2_Fields.add(regionInfo2)
        }
    }
}
