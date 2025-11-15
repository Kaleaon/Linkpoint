package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.net.Inet4Address
import java.nio.ByteBuffer
import java.util.UUID

class FindAgentMessage : SLMessage() {
    var hunter: UUID = UUID(0L, 0L)
    var prey: UUID = UUID(0L, 0L)
    var spaceIp: Inet4Address? = null
    val locationBlock: MutableList<LocationBlock> = mutableListOf()

    data class LocationBlock(
        var globalX: Double = 0.0,
        var globalY: Double = 0.0
    )


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, hunter)
        packUUID(buffer, prey)
        packIPAddress(buffer, spaceIp)
        require(locationBlock.size <= 0xFF) { "LocationBlock size exceeds 255 (" + locationBlock.size + ")" }
        packByte(buffer, locationBlock.size)
        locationBlock.forEach { entry ->
            packDouble(buffer, entry.globalX)
            packDouble(buffer, entry.globalY)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        hunter = unpackUUID(buffer)
        prey = unpackUUID(buffer)
        spaceIp = unpackIPAddress(buffer)
        run {
            val count = unpackByte(buffer)
            locationBlock.clear()
            repeat(count) {
                val entry = LocationBlock()
                entry.globalX = unpackDouble(buffer)
                entry.globalY = unpackDouble(buffer)
                locationBlock += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF0100

    override fun getMessageName(): String = "FindAgent"
}
