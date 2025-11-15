package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

/**
 * Coarse location update message (0x06)
 */
class CoarseLocationUpdateMessage : SLMessage() {
    data class Location(
        var x: Int = 0,
        var y: Int = 0,
        var z: Int = 0,
    )

    val locations: MutableList<Location> = mutableListOf()
    val agentIds: MutableList<UUID> = mutableListOf()
    var youIndex: Int = 0
    var preyIndex: Int = 0

    override fun packPayload(buffer: ByteBuffer) {
        require(locations.size <= 0xFF) { "Location list too large (${locations.size})" }
        packByte(buffer, locations.size)
        locations.forEach { location ->
            require(location.x in 0..0xFF) { "Location.x out of range (${location.x})" }
            require(location.y in 0..0xFF) { "Location.y out of range (${location.y})" }
            require(location.z in 0..0xFF) { "Location.z out of range (${location.z})" }
            packByte(buffer, location.x)
            packByte(buffer, location.y)
            packByte(buffer, location.z)
        }
        packUInt16(buffer, youIndex)
        packUInt16(buffer, preyIndex)
        require(agentIds.size <= 0xFF) { "Agent list too large (${agentIds.size})" }
        packByte(buffer, agentIds.size)
        agentIds.forEach { packUUID(buffer, it) }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        val locationCount = unpackByte(buffer)
        locations.clear()
        repeat(locationCount) {
            val x = unpackByte(buffer)
            val y = unpackByte(buffer)
            val z = unpackByte(buffer)
            locations += Location(x, y, z)
        }
        youIndex = unpackUInt16(buffer)
        preyIndex = unpackUInt16(buffer)
        val agentCount = unpackByte(buffer)
        agentIds.clear()
        repeat(agentCount) { agentIds += unpackUUID(buffer) }
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.COARSE_LOCATION_UPDATE

    override fun getMessageName(): String = "CoarseLocationUpdate"
}
