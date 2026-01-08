package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class MapBlockReplyMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var flags: Int = 0
    val data: MutableList<DataBlock> = mutableListOf()
    val size: MutableList<SizeBlock> = mutableListOf()

    data class DataBlock(
        var x: Int = 0,
        var y: Int = 0,
        var name: ByteArray = ByteArray(0),
        var access: Int = 0,
        var regionFlags: Int = 0,
        var waterHeight: Int = 0,
        var agents: Int = 0,
        var mapImageId: UUID = UUID(0L, 0L)
    )
    data class SizeBlock(
        var sizeX: Int = 0,
        var sizeY: Int = 0
    )


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packInt(buffer, flags)
        require(data.size <= 0xFF) { "Data size exceeds 255 (" + data.size + ")" }
        packByte(buffer, data.size)
        data.forEach { entry ->
            packUInt16(buffer, entry.x)
            packUInt16(buffer, entry.y)
            packVariable(buffer, entry.name, 1)
            packByte(buffer, entry.access)
            packInt(buffer, entry.regionFlags)
            packByte(buffer, entry.waterHeight)
            packByte(buffer, entry.agents)
            packUUID(buffer, entry.mapImageId)
        }
        require(size.size <= 0xFF) { "Size size exceeds 255 (" + size.size + ")" }
        packByte(buffer, size.size)
        size.forEach { entry ->
            packUInt16(buffer, entry.sizeX)
            packUInt16(buffer, entry.sizeY)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        flags = unpackInt(buffer)
        run {
            val count = unpackByte(buffer)
            data.clear()
            repeat(count) {
                val entry = DataBlock()
                entry.x = unpackUInt16(buffer)
                entry.y = unpackUInt16(buffer)
                entry.name = unpackVariable(buffer, 1)
                entry.access = unpackByte(buffer)
                entry.regionFlags = unpackInt(buffer)
                entry.waterHeight = unpackByte(buffer)
                entry.agents = unpackByte(buffer)
                entry.mapImageId = unpackUUID(buffer)
                data += entry
            }
        }
        run {
            val count = unpackByte(buffer)
            size.clear()
            repeat(count) {
                val entry = SizeBlock()
                entry.sizeX = unpackUInt16(buffer)
                entry.sizeY = unpackUInt16(buffer)
                size += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF0199.toInt()

    override fun getMessageName(): String = "MapBlockReply"
}
