package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class RequestParcelTransferMessage : SLMessage() {
    var transactionId: UUID = UUID(0L, 0L)
    var transactionTime: Int = 0
    var sourceId: UUID = UUID(0L, 0L)
    var destId: UUID = UUID(0L, 0L)
    var ownerId: UUID = UUID(0L, 0L)
    var flags: Int = 0
    var transactionType: Int = 0
    var amount: Int = 0
    var billableArea: Int = 0
    var actualArea: Int = 0
    var final: Boolean = false
    var regionId: UUID = UUID(0L, 0L)
    var gridX: Int = 0
    var gridY: Int = 0


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, transactionId)
        packInt(buffer, transactionTime)
        packUUID(buffer, sourceId)
        packUUID(buffer, destId)
        packUUID(buffer, ownerId)
        packByte(buffer, flags)
        packInt(buffer, transactionType)
        packInt(buffer, amount)
        packInt(buffer, billableArea)
        packInt(buffer, actualArea)
        packBoolean(buffer, final)
        packUUID(buffer, regionId)
        packInt(buffer, gridX)
        packInt(buffer, gridY)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        transactionId = unpackUUID(buffer)
        transactionTime = unpackInt(buffer)
        sourceId = unpackUUID(buffer)
        destId = unpackUUID(buffer)
        ownerId = unpackUUID(buffer)
        flags = unpackByte(buffer)
        transactionType = unpackInt(buffer)
        amount = unpackInt(buffer)
        billableArea = unpackInt(buffer)
        actualArea = unpackInt(buffer)
        final = unpackBoolean(buffer)
        regionId = unpackUUID(buffer)
        gridX = unpackInt(buffer)
        gridY = unpackInt(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF00DC

    override fun getMessageName(): String = "RequestParcelTransfer"
}
