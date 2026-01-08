package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class SimulatorShutdownRequestMessage : SLMessage() {
    val regionPresenceRequestByRegionId: MutableList<RegionPresenceRequestByRegionIdBlock> = mutableListOf()

    class RegionPresenceRequestByRegionIdBlock

    override fun packPayload(buffer: ByteBuffer) {
        regionPresenceRequestByRegionId.forEach { entry ->
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        run {
            val count = unpackByte(buffer)
            regionPresenceRequestByRegionId.clear()
            repeat(count) {
                val entry = RegionPresenceRequestByRegionIdBlock()
                regionPresenceRequestByRegionId += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF000D.toInt()

    override fun getMessageName(): String = "SimulatorShutdownRequest"
}
