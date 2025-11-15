package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class GodUpdateRegionInfoMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var simName: ByteArray = ByteArray(0)
    var estateId: Int = 0
    var parentEstateId: Int = 0
    var regionFlags: Int = 0
    var billableFactor: Float = 0f
    var pricePerMeter: Int = 0
    var redirectGridX: Int = 0
    var redirectGridY: Int = 0
    val regionInfo2: MutableList<RegionInfo2Block> = mutableListOf()

    data class RegionInfo2Block(
        var regionFlagsExtended: Long = 0L
    )


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packVariable(buffer, simName, 1)
        packInt(buffer, estateId)
        packInt(buffer, parentEstateId)
        packInt(buffer, regionFlags)
        packFloat(buffer, billableFactor)
        packInt(buffer, pricePerMeter)
        packInt(buffer, redirectGridX)
        packInt(buffer, redirectGridY)
        require(regionInfo2.size <= 0xFF) { "RegionInfo2 size exceeds 255 (" + regionInfo2.size + ")" }
        packByte(buffer, regionInfo2.size)
        regionInfo2.forEach { entry ->
            packLong(buffer, entry.regionFlagsExtended)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        simName = unpackVariable(buffer, 1)
        estateId = unpackInt(buffer)
        parentEstateId = unpackInt(buffer)
        regionFlags = unpackInt(buffer)
        billableFactor = unpackFloat(buffer)
        pricePerMeter = unpackInt(buffer)
        redirectGridX = unpackInt(buffer)
        redirectGridY = unpackInt(buffer)
        run {
            val count = unpackByte(buffer)
            regionInfo2.clear()
            repeat(count) {
                val entry = RegionInfo2Block()
                entry.regionFlagsExtended = unpackLong(buffer)
                regionInfo2 += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF008F

    override fun getMessageName(): String = "GodUpdateRegionInfo"
}
