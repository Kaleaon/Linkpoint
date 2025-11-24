package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class MapItemReplyMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var flags: Int = 0
    var itemType: Int = 0
    val data: MutableList<DataBlock> = mutableListOf()

    data class DataBlock(
        var x: Int = 0,
        var y: Int = 0,
        var id: UUID = UUID(0L, 0L),
        var extra: Int = 0,
        var extra2: Int = 0,
        var name: ByteArray = ByteArray(0)
    )


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packInt(buffer, flags)
        packInt(buffer, itemType)
        require(data.size <= 0xFF) { "Data size exceeds 255 (" + data.size + ")" }
        packByte(buffer, data.size)
        data.forEach { entry ->
            packInt(buffer, entry.x)
            packInt(buffer, entry.y)
            packUUID(buffer, entry.id)
            packInt(buffer, entry.extra)
            packInt(buffer, entry.extra2)
            packVariable(buffer, entry.name, 1)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        flags = unpackInt(buffer)
        itemType = unpackInt(buffer)
        run {
            val count = unpackByte(buffer)
            data.clear()
            repeat(count) {
                val entry = DataBlock()
                entry.x = unpackInt(buffer)
                entry.y = unpackInt(buffer)
                entry.id = unpackUUID(buffer)
                entry.extra = unpackInt(buffer)
                entry.extra2 = unpackInt(buffer)
                entry.name = unpackVariable(buffer, 1)
                data += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF019B.toInt()

    override fun getMessageName(): String = "MapItemReply"
}
