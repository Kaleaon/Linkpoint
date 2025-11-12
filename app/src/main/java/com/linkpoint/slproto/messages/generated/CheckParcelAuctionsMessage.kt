package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class CheckParcelAuctionsMessage : SLMessage() {
    val regionData: MutableList<RegionDataBlock> = mutableListOf()

    data class RegionDataBlock(
        var regionHandle: Long = 0L
    )


    override fun packPayload(buffer: ByteBuffer) {
        require(regionData.size <= 0xFF) { "RegionData size exceeds 255 (" + regionData.size + ")" }
        packByte(buffer, regionData.size)
        regionData.forEach { entry ->
            packLong(buffer, entry.regionHandle)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        run {
            val count = unpackByte(buffer)
            regionData.clear()
            repeat(count) {
                val entry = RegionDataBlock()
                entry.regionHandle = unpackLong(buffer)
                regionData += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF00E9

    override fun getMessageName(): String = "CheckParcelAuctions"
}
