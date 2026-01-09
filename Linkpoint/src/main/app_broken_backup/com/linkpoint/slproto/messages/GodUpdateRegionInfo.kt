package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class GodUpdateRegionInfo : SLMessage {
    AgentData AgentData_Field
    ArrayList<RegionInfo2> RegionInfo2_Fields = ArrayList<>()
    RegionInfo RegionInfo_Field

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class RegionInfo {
        float BillableFactor
        Int EstateID
        Int ParentEstateID
        Int PricePerMeter
        Int RedirectGridX
        Int RedirectGridY
        Int RegionFlags
        ByteArray SimName
    }

    class RegionInfo2 {
        Long RegionFlagsExtended
    }

    GodUpdateRegionInfo() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
        this.RegionInfo_Field = RegionInfo()
    }

    fun CalcPayloadSize(): Int {
        return this.RegionInfo_Field.SimName.size + 1 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 36 + 1 + (this.RegionInfo2_Fields.size() * 8)
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleGodUpdateRegionInfo(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) -113)
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
        byteBuffer.put((this as byte).RegionInfo2_Fields.size())
        for (RegionInfo2 regionInfo2 : this.RegionInfo2_Fields) {
            packLong(byteBuffer, regionInfo2.RegionFlagsExtended)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
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
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            RegionInfo2 regionInfo2 = RegionInfo2()
            regionInfo2.RegionFlagsExtended = unpackLong(byteBuffer)
            this.RegionInfo2_Fields.add(regionInfo2)
        }
    }
}
