package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class AvatarPicksReplyMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var targetId: UUID = UUID(0L, 0L)
    val data: MutableList<DataBlock> = mutableListOf()

    data class DataBlock(
        var pickId: UUID = UUID(0L, 0L),
        var pickName: ByteArray = ByteArray(0)
    )


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, targetId)
        require(data.size <= 0xFF) { "Data size exceeds 255 (" + data.size + ")" }
        packByte(buffer, data.size)
        data.forEach { entry ->
            packUUID(buffer, entry.pickId)
            packVariable(buffer, entry.pickName, 1)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        targetId = unpackUUID(buffer)
        run {
            val count = unpackByte(buffer)
            data.clear()
            repeat(count) {
                val entry = DataBlock()
                entry.pickId = unpackUUID(buffer)
                entry.pickName = unpackVariable(buffer, 1)
                data += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF00B2

    override fun getMessageName(): String = "AvatarPicksReply"
}
