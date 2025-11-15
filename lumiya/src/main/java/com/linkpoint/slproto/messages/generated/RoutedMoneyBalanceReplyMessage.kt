package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.net.Inet4Address
import java.nio.ByteBuffer
import java.util.UUID

class RoutedMoneyBalanceReplyMessage : SLMessage() {
    var targetIp: Inet4Address? = null
    var targetPort: Int = 0
    var agentId: UUID = UUID(0L, 0L)
    var transactionId: UUID = UUID(0L, 0L)
    var transactionSuccess: Boolean = false
    var moneyBalance: Int = 0
    var squareMetersCredit: Int = 0
    var squareMetersCommitted: Int = 0
    var description: ByteArray = ByteArray(0)
    var transactionType: Int = 0
    var sourceId: UUID = UUID(0L, 0L)
    var isSourceGroup: Boolean = false
    var destId: UUID = UUID(0L, 0L)
    var isDestGroup: Boolean = false
    var amount: Int = 0
    var itemDescription: ByteArray = ByteArray(0)


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packIPAddress(buffer, targetIp)
        packUInt16(buffer, targetPort)
        packUUID(buffer, agentId)
        packUUID(buffer, transactionId)
        packBoolean(buffer, transactionSuccess)
        packInt(buffer, moneyBalance)
        packInt(buffer, squareMetersCredit)
        packInt(buffer, squareMetersCommitted)
        packVariable(buffer, description, 1)
        packInt(buffer, transactionType)
        packUUID(buffer, sourceId)
        packBoolean(buffer, isSourceGroup)
        packUUID(buffer, destId)
        packBoolean(buffer, isDestGroup)
        packInt(buffer, amount)
        packVariable(buffer, itemDescription, 1)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        targetIp = unpackIPAddress(buffer)
        targetPort = unpackUInt16(buffer)
        agentId = unpackUUID(buffer)
        transactionId = unpackUUID(buffer)
        transactionSuccess = unpackBoolean(buffer)
        moneyBalance = unpackInt(buffer)
        squareMetersCredit = unpackInt(buffer)
        squareMetersCommitted = unpackInt(buffer)
        description = unpackVariable(buffer, 1)
        transactionType = unpackInt(buffer)
        sourceId = unpackUUID(buffer)
        isSourceGroup = unpackBoolean(buffer)
        destId = unpackUUID(buffer)
        isDestGroup = unpackBoolean(buffer)
        amount = unpackInt(buffer)
        itemDescription = unpackVariable(buffer, 1)
    }

    override fun getMessageID(): Int = 0xFFFF013B

    override fun getMessageName(): String = "RoutedMoneyBalanceReply"
}
