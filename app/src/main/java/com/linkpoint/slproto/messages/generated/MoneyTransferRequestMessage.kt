package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class MoneyTransferRequestMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var sourceId: UUID = UUID(0L, 0L)
    var destId: UUID = UUID(0L, 0L)
    var flags: Int = 0
    var amount: Int = 0
    var aggregatePermNextOwner: Int = 0
    var aggregatePermInventory: Int = 0
    var transactionType: Int = 0
    var description: ByteArray = ByteArray(0)


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packUUID(buffer, sourceId)
        packUUID(buffer, destId)
        packByte(buffer, flags)
        packInt(buffer, amount)
        packByte(buffer, aggregatePermNextOwner)
        packByte(buffer, aggregatePermInventory)
        packInt(buffer, transactionType)
        packVariable(buffer, description, 1)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        sourceId = unpackUUID(buffer)
        destId = unpackUUID(buffer)
        flags = unpackByte(buffer)
        amount = unpackInt(buffer)
        aggregatePermNextOwner = unpackByte(buffer)
        aggregatePermInventory = unpackByte(buffer)
        transactionType = unpackInt(buffer)
        description = unpackVariable(buffer, 1)
    }

    override fun getMessageID(): Int = 0xFFFF0137

    override fun getMessageName(): String = "MoneyTransferRequest"
}
