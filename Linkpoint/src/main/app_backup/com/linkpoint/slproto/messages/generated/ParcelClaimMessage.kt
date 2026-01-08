package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ParcelClaimMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var groupId: UUID = UUID(0L, 0L)
    var isGroupOwned: Boolean = false
    var final: Boolean = false
    val parcelData: MutableList<ParcelDataBlock> = mutableListOf()

    data class ParcelDataBlock(
        var west: Float = 0f,
        var south: Float = 0f,
        var east: Float = 0f,
        var north: Float = 0f
    )


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packUUID(buffer, groupId)
        packBoolean(buffer, isGroupOwned)
        packBoolean(buffer, final)
        require(parcelData.size <= 0xFF) { "ParcelData size exceeds 255 (" + parcelData.size + ")" }
        packByte(buffer, parcelData.size)
        parcelData.forEach { entry ->
            packFloat(buffer, entry.west)
            packFloat(buffer, entry.south)
            packFloat(buffer, entry.east)
            packFloat(buffer, entry.north)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        groupId = unpackUUID(buffer)
        isGroupOwned = unpackBoolean(buffer)
        final = unpackBoolean(buffer)
        run {
            val count = unpackByte(buffer)
            parcelData.clear()
            repeat(count) {
                val entry = ParcelDataBlock()
                entry.west = unpackFloat(buffer)
                entry.south = unpackFloat(buffer)
                entry.east = unpackFloat(buffer)
                entry.north = unpackFloat(buffer)
                parcelData += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF00D1.toInt()

    override fun getMessageName(): String = "ParcelClaim"
}
