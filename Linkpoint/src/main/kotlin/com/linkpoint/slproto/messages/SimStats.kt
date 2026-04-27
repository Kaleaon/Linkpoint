package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList

class SimStats : SLMessage() {
    public PidStat PidStat_Field
    public ArrayList<RegionInfo> RegionInfo_Fields = ArrayList<>()
    public Region Region_Field
    public ArrayList<Stat> Stat_Fields = ArrayList<>()

    @JvmStatic
    class PidStat {
        public Int PID
    }

    @JvmStatic
    class Region {
        public Int ObjectCapacity
        public Int RegionFlags
        public Int RegionX
        public Int RegionY
    }

    @JvmStatic
    class RegionInfo {
        public Long RegionFlagsExtended
    }

    @JvmStatic
    class Stat {
        public Int StatID
        public Float StatValue
    }

    public SimStats() {
        this.zeroCoded = false
        this.Region_Field = Region()
        this.PidStat_Field = PidStat()
    }

    public fun CalcPayloadSize(): Int {
        return (this.Stat_Fields.size() * 8) + 21 + 4 + 1 + (this.RegionInfo_Fields.size() * 8)
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleSimStats(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -116)
        packInt(byteBuffer, this.Region_Field.RegionX)
        packInt(byteBuffer, this.Region_Field.RegionY)
        packInt(byteBuffer, this.Region_Field.RegionFlags)
        packInt(byteBuffer, this.Region_Field.ObjectCapacity)
        byteBuffer.put((Byte) this.Stat_Fields.size())
        for (Stat stat : this.Stat_Fields) {
            packInt(byteBuffer, stat.StatID)
            packFloat(byteBuffer, stat.StatValue)
        }
        packInt(byteBuffer, this.PidStat_Field.PID)
        byteBuffer.put((Byte) this.RegionInfo_Fields.size())
        for (RegionInfo regionInfo : this.RegionInfo_Fields) {
            packLong(byteBuffer, regionInfo.RegionFlagsExtended)
        }
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.Region_Field.RegionX = unpackInt(byteBuffer)
        this.Region_Field.RegionY = unpackInt(byteBuffer)
        this.Region_Field.RegionFlags = unpackInt(byteBuffer)
        this.Region_Field.ObjectCapacity = unpackInt(byteBuffer)
        val b: Byte = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            val stat: Stat = Stat()
            stat.StatID = unpackInt(byteBuffer)
            stat.StatValue = unpackFloat(byteBuffer)
            this.Stat_Fields.add(stat)
        }
        this.PidStat_Field.PID = unpackInt(byteBuffer)
        val b2: Byte = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i2 = 0; i2 < b2; i2++) {
            val regionInfo: RegionInfo = RegionInfo()
            regionInfo.RegionFlagsExtended = unpackLong(byteBuffer)
            this.RegionInfo_Fields.add(regionInfo)
        }
    }
}
