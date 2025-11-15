package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class GroupNoticesListReplyMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var groupId: UUID = UUID(0L, 0L)
    val data: MutableList<DataBlock> = mutableListOf()

    data class DataBlock(
        var noticeId: UUID = UUID(0L, 0L),
        var timestamp: Int = 0,
        var fromName: ByteArray = ByteArray(0),
        var subject: ByteArray = ByteArray(0),
        var hasAttachment: Boolean = false,
        var assetType: Int = 0
    )


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, groupId)
        require(data.size <= 0xFF) { "Data size exceeds 255 (" + data.size + ")" }
        packByte(buffer, data.size)
        data.forEach { entry ->
            packUUID(buffer, entry.noticeId)
            packInt(buffer, entry.timestamp)
            packVariable(buffer, entry.fromName, 2)
            packVariable(buffer, entry.subject, 2)
            packBoolean(buffer, entry.hasAttachment)
            packByte(buffer, entry.assetType)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        groupId = unpackUUID(buffer)
        run {
            val count = unpackByte(buffer)
            data.clear()
            repeat(count) {
                val entry = DataBlock()
                entry.noticeId = unpackUUID(buffer)
                entry.timestamp = unpackInt(buffer)
                entry.fromName = unpackVariable(buffer, 2)
                entry.subject = unpackVariable(buffer, 2)
                entry.hasAttachment = unpackBoolean(buffer)
                entry.assetType = unpackByte(buffer)
                data += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF003B

    override fun getMessageName(): String = "GroupNoticesListReply"
}
