package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList

class SimStats : SLMessage {
    PidStat PidStat_Field
    ArrayList<RegionInfo> RegionInfo_Fields = ArrayList<>()
    Region Region_Field
    ArrayList<Stat> Stat_Fields = ArrayList<>()

    class PidStat {
        Int PID
    }

    class Region {
        Int ObjectCapacity
        Int RegionFlags
        Int RegionX
        Int RegionY
    }

    class RegionInfo {
        Long RegionFlagsExtended
    }

    class Stat {
        Int StatID
        Float StatValue
    }

    SimStats() {
        this.zeroCoded = false
        this.Region_Field = Region()
        this.PidStat_Field = PidStat()
    }

    fun CalcPayloadSize(): Int {
        return (this.Stat_Fields.size() * 8) + 21 + 4 + 1 + (this.RegionInfo_Fields.size() * 8)
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleSimStats(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -116)
        packInt(byteBuffer, this.Region_Field.RegionX)
        packInt(byteBuffer, this.Region_Field.RegionY)
        packInt(byteBuffer, this.Region_Field.RegionFlags)
        packInt(byteBuffer, this.Region_Field.ObjectCapacity)
        byteBuffer.put((this as Byte).Stat_Fields.size())
        for (Stat stat : this.Stat_Fields) {
            packInt(byteBuffer, stat.StatID)
            packFloat(byteBuffer, stat.StatValue)
        }
        packInt(byteBuffer, this.PidStat_Field.PID)
        byteBuffer.put((this as Byte).RegionInfo_Fields.size())
        for (RegionInfo regionInfo : this.RegionInfo_Fields) {
            packLong(byteBuffer, regionInfo.RegionFlagsExtended)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.Region_Field.RegionX = unpackInt(byteBuffer)
        this.Region_Field.RegionY = unpackInt(byteBuffer)
        this.Region_Field.RegionFlags = unpackInt(byteBuffer)
        this.Region_Field.ObjectCapacity = unpackInt(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            Stat stat = Stat()
            stat.StatID = unpackInt(byteBuffer)
            stat.StatValue = unpackFloat(byteBuffer)
            this.Stat_Fields.add(stat)
        }
        this.PidStat_Field.PID = unpackInt(byteBuffer)
        Byte b2 = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i2 in 0 until b2) {
            RegionInfo regionInfo = RegionInfo()
            regionInfo.RegionFlagsExtended = unpackLong(byteBuffer)
            this.RegionInfo_Fields.add(regionInfo)
        }
    }
}
