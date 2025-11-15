package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.net.Inet4Address
import java.nio.ByteBuffer
import java.util.UUID

class LogFailedMoneyTransactionMessage : SLMessage() {
    var transactionId: UUID = UUID(0L, 0L)
    var transactionTime: Int = 0
    var transactionType: Int = 0
    var sourceId: UUID = UUID(0L, 0L)
    var destId: UUID = UUID(0L, 0L)
    var flags: Int = 0
    var amount: Int = 0
    var simulatorIp: Inet4Address? = null
    var gridX: Int = 0
    var gridY: Int = 0
    var failureType: Int = 0


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, transactionId)
        packInt(buffer, transactionTime)
        packInt(buffer, transactionType)
        packUUID(buffer, sourceId)
        packUUID(buffer, destId)
        packByte(buffer, flags)
        packInt(buffer, amount)
        packIPAddress(buffer, simulatorIp)
        packInt(buffer, gridX)
        packInt(buffer, gridY)
        packByte(buffer, failureType)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        transactionId = unpackUUID(buffer)
        transactionTime = unpackInt(buffer)
        transactionType = unpackInt(buffer)
        sourceId = unpackUUID(buffer)
        destId = unpackUUID(buffer)
        flags = unpackByte(buffer)
        amount = unpackInt(buffer)
        simulatorIp = unpackIPAddress(buffer)
        gridX = unpackInt(buffer)
        gridY = unpackInt(buffer)
        failureType = unpackByte(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF0014

    override fun getMessageName(): String = "LogFailedMoneyTransaction"
}
