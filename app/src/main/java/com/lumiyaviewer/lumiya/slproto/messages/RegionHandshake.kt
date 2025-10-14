package com.lumiyaviewer.lumiya.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class RegionHandshake : SLMessage {
    RegionInfo2 RegionInfo2_Field
    RegionInfo3 RegionInfo3_Field
    ArrayList<RegionInfo4> RegionInfo4_Fields = ArrayList<>()
    RegionInfo RegionInfo_Field

    class RegionInfo {
        Float BillableFactor
        UUID CacheID
        Boolean IsEstateManager
        Int RegionFlags
        Int SimAccess
        Byte[] SimName
        UUID SimOwner
        UUID TerrainBase0
        UUID TerrainBase1
        UUID TerrainBase2
        UUID TerrainBase3
        UUID TerrainDetail0
        UUID TerrainDetail1
        UUID TerrainDetail2
        UUID TerrainDetail3
        Float TerrainHeightRange00
        Float TerrainHeightRange01
        Float TerrainHeightRange10
        Float TerrainHeightRange11
        Float TerrainStartHeight00
        Float TerrainStartHeight01
        Float TerrainStartHeight10
        Float TerrainStartHeight11
        Float WaterHeight
    }

    class RegionInfo2 {
        UUID RegionID
    }

    class RegionInfo3 {
        Int CPUClassID
        Int CPURatio
        Byte[] ColoName
        Byte[] ProductName
        Byte[] ProductSKU
    }

    class RegionInfo4 {
        Long RegionFlagsExtended
        Long RegionProtocols
    }

    RegionHandshake() {
        this.zeroCoded = true
        this.RegionInfo_Field = RegionInfo()
        this.RegionInfo2_Field = RegionInfo2()
        this.RegionInfo3_Field = RegionInfo3()
    }

    Int CalcPayloadSize() {
        return this.RegionInfo_Field.SimName.length + 6 + 16 + 1 + 4 + 4 + 16 + 16 + 16 + 16 + 16 + 16 + 16 + 16 + 16 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 16 + this.RegionInfo3_Field.ColoName.length + 9 + 1 + this.RegionInfo3_Field.ProductSKU.length + 1 + this.RegionInfo3_Field.ProductName.length + 1 + (this.RegionInfo4_Fields.size() * 16)
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleRegionHandshake(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -108)
        packInt(byteBuffer, this.RegionInfo_Field.RegionFlags)
        packByte(byteBuffer, (Byte) this.RegionInfo_Field.SimAccess)
        packVariable(byteBuffer, this.RegionInfo_Field.SimName, 1)
        packUUID(byteBuffer, this.RegionInfo_Field.SimOwner)
        packBoolean(byteBuffer, this.RegionInfo_Field.IsEstateManager)
        packFloat(byteBuffer, this.RegionInfo_Field.WaterHeight)
        packFloat(byteBuffer, this.RegionInfo_Field.BillableFactor)
        packUUID(byteBuffer, this.RegionInfo_Field.CacheID)
        packUUID(byteBuffer, this.RegionInfo_Field.TerrainBase0)
        packUUID(byteBuffer, this.RegionInfo_Field.TerrainBase1)
        packUUID(byteBuffer, this.RegionInfo_Field.TerrainBase2)
        packUUID(byteBuffer, this.RegionInfo_Field.TerrainBase3)
        packUUID(byteBuffer, this.RegionInfo_Field.TerrainDetail0)
        packUUID(byteBuffer, this.RegionInfo_Field.TerrainDetail1)
        packUUID(byteBuffer, this.RegionInfo_Field.TerrainDetail2)
        packUUID(byteBuffer, this.RegionInfo_Field.TerrainDetail3)
        packFloat(byteBuffer, this.RegionInfo_Field.TerrainStartHeight00)
        packFloat(byteBuffer, this.RegionInfo_Field.TerrainStartHeight01)
        packFloat(byteBuffer, this.RegionInfo_Field.TerrainStartHeight10)
        packFloat(byteBuffer, this.RegionInfo_Field.TerrainStartHeight11)
        packFloat(byteBuffer, this.RegionInfo_Field.TerrainHeightRange00)
        packFloat(byteBuffer, this.RegionInfo_Field.TerrainHeightRange01)
        packFloat(byteBuffer, this.RegionInfo_Field.TerrainHeightRange10)
        packFloat(byteBuffer, this.RegionInfo_Field.TerrainHeightRange11)
        packUUID(byteBuffer, this.RegionInfo2_Field.RegionID)
        packInt(byteBuffer, this.RegionInfo3_Field.CPUClassID)
        packInt(byteBuffer, this.RegionInfo3_Field.CPURatio)
        packVariable(byteBuffer, this.RegionInfo3_Field.ColoName, 1)
        packVariable(byteBuffer, this.RegionInfo3_Field.ProductSKU, 1)
        packVariable(byteBuffer, this.RegionInfo3_Field.ProductName, 1)
        byteBuffer.put((Byte) this.RegionInfo4_Fields.size())
        for (RegionInfo4 regionInfo4 : this.RegionInfo4_Fields) {
            packLong(byteBuffer, regionInfo4.RegionFlagsExtended)
            packLong(byteBuffer, regionInfo4.RegionProtocols)
        }
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.RegionInfo_Field.RegionFlags = unpackInt(byteBuffer)
        this.RegionInfo_Field.SimAccess = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.RegionInfo_Field.SimName = unpackVariable(byteBuffer, 1)
        this.RegionInfo_Field.SimOwner = unpackUUID(byteBuffer)
        this.RegionInfo_Field.IsEstateManager = unpackBoolean(byteBuffer)
        this.RegionInfo_Field.WaterHeight = unpackFloat(byteBuffer)
        this.RegionInfo_Field.BillableFactor = unpackFloat(byteBuffer)
        this.RegionInfo_Field.CacheID = unpackUUID(byteBuffer)
        this.RegionInfo_Field.TerrainBase0 = unpackUUID(byteBuffer)
        this.RegionInfo_Field.TerrainBase1 = unpackUUID(byteBuffer)
        this.RegionInfo_Field.TerrainBase2 = unpackUUID(byteBuffer)
        this.RegionInfo_Field.TerrainBase3 = unpackUUID(byteBuffer)
        this.RegionInfo_Field.TerrainDetail0 = unpackUUID(byteBuffer)
        this.RegionInfo_Field.TerrainDetail1 = unpackUUID(byteBuffer)
        this.RegionInfo_Field.TerrainDetail2 = unpackUUID(byteBuffer)
        this.RegionInfo_Field.TerrainDetail3 = unpackUUID(byteBuffer)
        this.RegionInfo_Field.TerrainStartHeight00 = unpackFloat(byteBuffer)
        this.RegionInfo_Field.TerrainStartHeight01 = unpackFloat(byteBuffer)
        this.RegionInfo_Field.TerrainStartHeight10 = unpackFloat(byteBuffer)
        this.RegionInfo_Field.TerrainStartHeight11 = unpackFloat(byteBuffer)
        this.RegionInfo_Field.TerrainHeightRange00 = unpackFloat(byteBuffer)
        this.RegionInfo_Field.TerrainHeightRange01 = unpackFloat(byteBuffer)
        this.RegionInfo_Field.TerrainHeightRange10 = unpackFloat(byteBuffer)
        this.RegionInfo_Field.TerrainHeightRange11 = unpackFloat(byteBuffer)
        this.RegionInfo2_Field.RegionID = unpackUUID(byteBuffer)
        this.RegionInfo3_Field.CPUClassID = unpackInt(byteBuffer)
        this.RegionInfo3_Field.CPURatio = unpackInt(byteBuffer)
        this.RegionInfo3_Field.ColoName = unpackVariable(byteBuffer, 1)
        this.RegionInfo3_Field.ProductSKU = unpackVariable(byteBuffer, 1)
        this.RegionInfo3_Field.ProductName = unpackVariable(byteBuffer, 1)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            RegionInfo4 regionInfo4 = RegionInfo4()
            regionInfo4.RegionFlagsExtended = unpackLong(byteBuffer)
            regionInfo4.RegionProtocols = unpackLong(byteBuffer)
            this.RegionInfo4_Fields.add(regionInfo4)
        }
    }
}
