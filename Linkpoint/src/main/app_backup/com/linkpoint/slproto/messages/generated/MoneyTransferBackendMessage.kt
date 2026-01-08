package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class MoneyTransferBackendMessage : SLMessage() {
    var transactionId: UUID = UUID(0L, 0L)
    var transactionTime: Int = 0
    var sourceId: UUID = UUID(0L, 0L)
    var destId: UUID = UUID(0L, 0L)
    var flags: Int = 0
    var amount: Int = 0
    var aggregatePermNextOwner: Int = 0
    var aggregatePermInventory: Int = 0
    var transactionType: Int = 0
    var regionId: UUID = UUID(0L, 0L)
    var gridX: Int = 0
    var gridY: Int = 0
    var description: ByteArray = ByteArray(0)


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, transactionId)
        packInt(buffer, transactionTime)
        packUUID(buffer, sourceId)
        packUUID(buffer, destId)
        packByte(buffer, flags)
        packInt(buffer, amount)
        packByte(buffer, aggregatePermNextOwner)
        packByte(buffer, aggregatePermInventory)
        packInt(buffer, transactionType)
        packUUID(buffer, regionId)
        packInt(buffer, gridX)
        packInt(buffer, gridY)
        packVariable(buffer, description, 1)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        transactionId = unpackUUID(buffer)
        transactionTime = unpackInt(buffer)
        sourceId = unpackUUID(buffer)
        destId = unpackUUID(buffer)
        flags = unpackByte(buffer)
        amount = unpackInt(buffer)
        aggregatePermNextOwner = unpackByte(buffer)
        aggregatePermInventory = unpackByte(buffer)
        transactionType = unpackInt(buffer)
        regionId = unpackUUID(buffer)
        gridX = unpackInt(buffer)
        gridY = unpackInt(buffer)
        description = unpackVariable(buffer, 1)
    }

    override fun getMessageID(): Int = 0xFFFF0138.toInt()

    override fun getMessageName(): String = "MoneyTransferBackend"
}
