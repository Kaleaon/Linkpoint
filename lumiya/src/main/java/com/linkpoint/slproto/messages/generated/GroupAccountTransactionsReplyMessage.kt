package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class GroupAccountTransactionsReplyMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var groupId: UUID = UUID(0L, 0L)
    var requestId: UUID = UUID(0L, 0L)
    var intervalDays: Int = 0
    var currentInterval: Int = 0
    var startDate: ByteArray = ByteArray(0)
    val historyData: MutableList<HistoryDataBlock> = mutableListOf()

    data class HistoryDataBlock(
        var time: ByteArray = ByteArray(0),
        var user: ByteArray = ByteArray(0),
        var type: Int = 0,
        var item: ByteArray = ByteArray(0),
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
            packVariable(buffer, entry.time, 1)
            packVariable(buffer, entry.user, 1)
            packInt(buffer, entry.type)
            packVariable(buffer, entry.item, 1)
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
                entry.time = unpackVariable(buffer, 1)
                entry.user = unpackVariable(buffer, 1)
                entry.type = unpackInt(buffer)
                entry.item = unpackVariable(buffer, 1)
                entry.amount = unpackInt(buffer)
                historyData += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF0166.toInt()

    override fun getMessageName(): String = "GroupAccountTransactionsReply"
}
