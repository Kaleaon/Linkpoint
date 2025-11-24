package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.net.Inet4Address
import java.nio.ByteBuffer
import java.util.UUID

class RegionPresenceResponseMessage : SLMessage() {
    val regionData: MutableList<RegionDataBlock> = mutableListOf()

    data class RegionDataBlock(
        var regionId: UUID = UUID(0L, 0L),
        var regionHandle: Long = 0L,
        var internalRegionIp: Inet4Address? = null,
        var externalRegionIp: Inet4Address? = null,
        var regionPort: Int = 0,
        var validUntil: Double = 0.0,
        var message: ByteArray = ByteArray(0)
    )


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        require(regionData.size <= 0xFF) { "RegionData size exceeds 255 (" + regionData.size + ")" }
        packByte(buffer, regionData.size)
        regionData.forEach { entry ->
            packUUID(buffer, entry.regionId)
            packLong(buffer, entry.regionHandle)
            packIPAddress(buffer, entry.internalRegionIp)
            packIPAddress(buffer, entry.externalRegionIp)
            packUInt16(buffer, entry.regionPort)
            packDouble(buffer, entry.validUntil)
            packVariable(buffer, entry.message, 1)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        run {
            val count = unpackByte(buffer)
            regionData.clear()
            repeat(count) {
                val entry = RegionDataBlock()
                entry.regionId = unpackUUID(buffer)
                entry.regionHandle = unpackLong(buffer)
                entry.internalRegionIp = unpackIPAddress(buffer)
                entry.externalRegionIp = unpackIPAddress(buffer)
                entry.regionPort = unpackUInt16(buffer)
                entry.validUntil = unpackDouble(buffer)
                entry.message = unpackVariable(buffer, 1)
                regionData += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF0010.toInt()

    override fun getMessageName(): String = "RegionPresenceResponse"
}
