package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class GroupAccountDetailsReplyMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var groupId: UUID = UUID(0L, 0L)
    var requestId: UUID = UUID(0L, 0L)
    var intervalDays: Int = 0
    var currentInterval: Int = 0
    var startDate: ByteArray = ByteArray(0)
    val historyData: MutableList<HistoryDataBlock> = mutableListOf()

    data class HistoryDataBlock(
        var description: ByteArray = ByteArray(0),
        var amount: Int = 0
    )


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, groupId)
        packUUID(buffer, requestId)
        packInt(buffer, intervalDays)
        packInt(buffer, currentInterval)
        packVariable(buffer, startDate, 1)
        require(historyData.size <= 0xFF) { "HistoryData size exceeds 255 (" + historyData.size + ")" }
        packByte(buffer, historyData.size)
        historyData.forEach { entry ->
            packVariable(buffer, entry.description, 1)
            packInt(buffer, entry.amount)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        groupId = unpackUUID(buffer)
        requestId = unpackUUID(buffer)
        intervalDays = unpackInt(buffer)
        currentInterval = unpackInt(buffer)
        startDate = unpackVariable(buffer, 1)
        run {
            val count = unpackByte(buffer)
            historyData.clear()
            repeat(count) {
                val entry = HistoryDataBlock()
                entry.description = unpackVariable(buffer, 1)
                entry.amount = unpackInt(buffer)
                historyData += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF0164

    override fun getMessageName(): String = "GroupAccountDetailsReply"
}
