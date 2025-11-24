package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class SimStatsMessage : SLMessage() {
    var regionX: Int = 0
    var regionY: Int = 0
    var regionFlags: Int = 0
    var objectCapacity: Int = 0
    val stat: MutableList<StatBlock> = mutableListOf()
    var pid: Int = 0
    val regionInfo: MutableList<RegionInfoBlock> = mutableListOf()

    data class StatBlock(
        var statId: Int = 0,
        var statValue: Float = 0f
    )
    data class RegionInfoBlock(
        var regionFlagsExtended: Long = 0L
    )


    override fun packPayload(buffer: ByteBuffer) {
        packInt(buffer, regionX)
        packInt(buffer, regionY)
        packInt(buffer, regionFlags)
        packInt(buffer, objectCapacity)
        require(stat.size <= 0xFF) { "Stat size exceeds 255 (" + stat.size + ")" }
        packByte(buffer, stat.size)
        stat.forEach { entry ->
            packInt(buffer, entry.statId)
            packFloat(buffer, entry.statValue)
        }
        packInt(buffer, pid)
        require(regionInfo.size <= 0xFF) { "RegionInfo size exceeds 255 (" + regionInfo.size + ")" }
        packByte(buffer, regionInfo.size)
        regionInfo.forEach { entry ->
            packLong(buffer, entry.regionFlagsExtended)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        regionX = unpackInt(buffer)
        regionY = unpackInt(buffer)
        regionFlags = unpackInt(buffer)
        objectCapacity = unpackInt(buffer)
        run {
            val count = unpackByte(buffer)
            stat.clear()
            repeat(count) {
                val entry = StatBlock()
                entry.statId = unpackInt(buffer)
                entry.statValue = unpackFloat(buffer)
                stat += entry
            }
        }
        pid = unpackInt(buffer)
        run {
            val count = unpackByte(buffer)
            regionInfo.clear()
            repeat(count) {
                val entry = RegionInfoBlock()
                entry.regionFlagsExtended = unpackLong(buffer)
                regionInfo += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF008C.toInt()

    override fun getMessageName(): String = "SimStats"
}
