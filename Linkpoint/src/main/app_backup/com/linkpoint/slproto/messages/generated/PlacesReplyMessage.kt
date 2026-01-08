package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class PlacesReplyMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var queryId: UUID = UUID(0L, 0L)
    var transactionId: UUID = UUID(0L, 0L)
    val queryData: MutableList<QueryDataBlock> = mutableListOf()

    data class QueryDataBlock(
        var ownerId: UUID = UUID(0L, 0L),
        var name: ByteArray = ByteArray(0),
        var desc: ByteArray = ByteArray(0),
        var actualArea: Int = 0,
        var billableArea: Int = 0,
        var flags: Int = 0,
        var globalX: Float = 0f,
        var globalY: Float = 0f,
        var globalZ: Float = 0f,
        var simName: ByteArray = ByteArray(0),
        var snapshotId: UUID = UUID(0L, 0L),
        var dwell: Float = 0f,
        var price: Int = 0
    )


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, queryId)
        packUUID(buffer, transactionId)
        require(queryData.size <= 0xFF) { "QueryData size exceeds 255 (" + queryData.size + ")" }
        packByte(buffer, queryData.size)
        queryData.forEach { entry ->
            packUUID(buffer, entry.ownerId)
            packVariable(buffer, entry.name, 1)
            packVariable(buffer, entry.desc, 1)
            packInt(buffer, entry.actualArea)
            packInt(buffer, entry.billableArea)
            packByte(buffer, entry.flags)
            packFloat(buffer, entry.globalX)
            packFloat(buffer, entry.globalY)
            packFloat(buffer, entry.globalZ)
            packVariable(buffer, entry.simName, 1)
            packUUID(buffer, entry.snapshotId)
            packFloat(buffer, entry.dwell)
            packInt(buffer, entry.price)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        queryId = unpackUUID(buffer)
        transactionId = unpackUUID(buffer)
        run {
            val count = unpackByte(buffer)
            queryData.clear()
            repeat(count) {
                val entry = QueryDataBlock()
                entry.ownerId = unpackUUID(buffer)
                entry.name = unpackVariable(buffer, 1)
                entry.desc = unpackVariable(buffer, 1)
                entry.actualArea = unpackInt(buffer)
                entry.billableArea = unpackInt(buffer)
                entry.flags = unpackByte(buffer)
                entry.globalX = unpackFloat(buffer)
                entry.globalY = unpackFloat(buffer)
                entry.globalZ = unpackFloat(buffer)
                entry.simName = unpackVariable(buffer, 1)
                entry.snapshotId = unpackUUID(buffer)
                entry.dwell = unpackFloat(buffer)
                entry.price = unpackInt(buffer)
                queryData += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF001E.toInt()

    override fun getMessageName(): String = "PlacesReply"
}
