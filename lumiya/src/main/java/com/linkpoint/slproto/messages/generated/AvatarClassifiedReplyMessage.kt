package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class AvatarClassifiedReplyMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var targetId: UUID = UUID(0L, 0L)
    val data: MutableList<DataBlock> = mutableListOf()

    data class DataBlock(
        var classifiedId: UUID = UUID(0L, 0L),
        var name: ByteArray = ByteArray(0)
    )


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, targetId)
        require(data.size <= 0xFF) { "Data size exceeds 255 (" + data.size + ")" }
        packByte(buffer, data.size)
        data.forEach { entry ->
            packUUID(buffer, entry.classifiedId)
            packVariable(buffer, entry.name, 1)
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
                entry.classifiedId = unpackUUID(buffer)
                entry.name = unpackVariable(buffer, 1)
                data += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF002A

    override fun getMessageName(): String = "AvatarClassifiedReply"
}
