package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ParcelObjectOwnersReplyMessage : SLMessage() {
    val data: MutableList<DataBlock> = mutableListOf()

    data class DataBlock(
        var ownerId: UUID = UUID(0L, 0L),
        var isGroupOwned: Boolean = false,
        var count: Int = 0,
        var onlineStatus: Boolean = false
    )


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        require(data.size <= 0xFF) { "Data size exceeds 255 (" + data.size + ")" }
        packByte(buffer, data.size)
        data.forEach { entry ->
            packUUID(buffer, entry.ownerId)
            packBoolean(buffer, entry.isGroupOwned)
            packInt(buffer, entry.count)
            packBoolean(buffer, entry.onlineStatus)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        run {
            val count = unpackByte(buffer)
            data.clear()
            repeat(count) {
                val entry = DataBlock()
                entry.ownerId = unpackUUID(buffer)
                entry.isGroupOwned = unpackBoolean(buffer)
                entry.count = unpackInt(buffer)
                entry.onlineStatus = unpackBoolean(buffer)
                data += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF0039.toInt()

    override fun getMessageName(): String = "ParcelObjectOwnersReply"
}
