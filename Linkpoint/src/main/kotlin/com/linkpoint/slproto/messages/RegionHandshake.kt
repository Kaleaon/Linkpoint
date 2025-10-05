package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class RegionHandshake : SLMessage() {
    public RegionInfo2 RegionInfo2_Field
    public RegionInfo3 RegionInfo3_Field
    public ArrayList<RegionInfo4> RegionInfo4_Fields = ArrayList<>()
    public RegionInfo RegionInfo_Field

    @JvmStatic
    class RegionInfo {
        public Float BillableFactor
        public UUID CacheID
        public Boolean IsEstateManager
        public Int RegionFlags
        public Int SimAccess
        public Byte[] SimName
        public UUID SimOwner
        public UUID TerrainBase0
        public UUID TerrainBase1
        public UUID TerrainBase2
        public UUID TerrainBase3
        public UUID TerrainDetail0
        public UUID TerrainDetail1
        public UUID TerrainDetail2
        public UUID TerrainDetail3
        public Float TerrainHeightRange00
        public Float TerrainHeightRange01
        public Float TerrainHeightRange10
        public Float TerrainHeightRange11
        public Float TerrainStartHeight00
        public Float TerrainStartHeight01
        public Float TerrainStartHeight10
        public Float TerrainStartHeight11
        public Float WaterHeight
    }

    @JvmStatic
    class RegionInfo2 {
        public UUID RegionID
    }

    @JvmStatic
    class RegionInfo3 {
        public Int CPUClassID
        public Int CPURatio
        public Byte[] ColoName
        public Byte[] ProductName
        public Byte[] ProductSKU
    }

    @JvmStatic
    class RegionInfo4 {
        public Long RegionFlagsExtended
        public Long RegionProtocols
    }

    public RegionHandshake() {
        this.zeroCoded = true
        this.RegionInfo_Field = RegionInfo()
        this.RegionInfo2_Field = RegionInfo2()
        this.RegionInfo3_Field = RegionInfo3()
    }

    public Int CalcPayloadSize() {
        return this.RegionInfo_Field.SimName.length + 6 + 16 + 1 + 4 + 4 + 16 + 16 + 16 + 16 + 16 + 16 + 16 + 16 + 16 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 16 + this.RegionInfo3_Field.ColoName.length + 9 + 1 + this.RegionInfo3_Field.ProductSKU.length + 1 + this.RegionInfo3_Field.ProductName.length + 1 + (this.RegionInfo4_Fields.size() * 16)
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleRegionHandshake(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
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

    fun UnpackPayload(ByteBuffer byteBuffer) {
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
