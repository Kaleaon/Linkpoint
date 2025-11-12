package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class AvatarPickerReplyMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var queryId: UUID = UUID(0L, 0L)
    val data: MutableList<DataBlock> = mutableListOf()

    data class DataBlock(
        var avatarId: UUID = UUID(0L, 0L),
        var firstName: ByteArray = ByteArray(0),
        var lastName: ByteArray = ByteArray(0)
    )


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, queryId)
        require(data.size <= 0xFF) { "Data size exceeds 255 (" + data.size + ")" }
        packByte(buffer, data.size)
        data.forEach { entry ->
            packUUID(buffer, entry.avatarId)
            packVariable(buffer, entry.firstName, 1)
            packVariable(buffer, entry.lastName, 1)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        queryId = unpackUUID(buffer)
        run {
            val count = unpackByte(buffer)
            data.clear()
            repeat(count) {
                val entry = DataBlock()
                entry.avatarId = unpackUUID(buffer)
                entry.firstName = unpackVariable(buffer, 1)
                entry.lastName = unpackVariable(buffer, 1)
                data += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF001C

    override fun getMessageName(): String = "AvatarPickerReply"
}
