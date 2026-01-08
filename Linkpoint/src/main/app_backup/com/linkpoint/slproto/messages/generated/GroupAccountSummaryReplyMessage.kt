package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class GroupAccountSummaryReplyMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var groupId: UUID = UUID(0L, 0L)
    var requestId: UUID = UUID(0L, 0L)
    var intervalDays: Int = 0
    var currentInterval: Int = 0
    var startDate: ByteArray = ByteArray(0)
    var balance: Int = 0
    var totalCredits: Int = 0
    var totalDebits: Int = 0
    var objectTaxCurrent: Int = 0
    var lightTaxCurrent: Int = 0
    var landTaxCurrent: Int = 0
    var groupTaxCurrent: Int = 0
    var parcelDirFeeCurrent: Int = 0
    var objectTaxEstimate: Int = 0
    var lightTaxEstimate: Int = 0
    var landTaxEstimate: Int = 0
    var groupTaxEstimate: Int = 0
    var parcelDirFeeEstimate: Int = 0
    var nonExemptMembers: Int = 0
    var lastTaxDate: ByteArray = ByteArray(0)
    var taxDate: ByteArray = ByteArray(0)


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
        packInt(buffer, balance)
        packInt(buffer, totalCredits)
        packInt(buffer, totalDebits)
        packInt(buffer, objectTaxCurrent)
        packInt(buffer, lightTaxCurrent)
        packInt(buffer, landTaxCurrent)
        packInt(buffer, groupTaxCurrent)
        packInt(buffer, parcelDirFeeCurrent)
        packInt(buffer, objectTaxEstimate)
        packInt(buffer, lightTaxEstimate)
        packInt(buffer, landTaxEstimate)
        packInt(buffer, groupTaxEstimate)
        packInt(buffer, parcelDirFeeEstimate)
        packInt(buffer, nonExemptMembers)
        packVariable(buffer, lastTaxDate, 1)
        packVariable(buffer, taxDate, 1)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        groupId = unpackUUID(buffer)
        requestId = unpackUUID(buffer)
        intervalDays = unpackInt(buffer)
        currentInterval = unpackInt(buffer)
        startDate = unpackVariable(buffer, 1)
        balance = unpackInt(buffer)
        totalCredits = unpackInt(buffer)
        totalDebits = unpackInt(buffer)
        objectTaxCurrent = unpackInt(buffer)
        lightTaxCurrent = unpackInt(buffer)
        landTaxCurrent = unpackInt(buffer)
        groupTaxCurrent = unpackInt(buffer)
        parcelDirFeeCurrent = unpackInt(buffer)
        objectTaxEstimate = unpackInt(buffer)
        lightTaxEstimate = unpackInt(buffer)
        landTaxEstimate = unpackInt(buffer)
        groupTaxEstimate = unpackInt(buffer)
        parcelDirFeeEstimate = unpackInt(buffer)
        nonExemptMembers = unpackInt(buffer)
        lastTaxDate = unpackVariable(buffer, 1)
        taxDate = unpackVariable(buffer, 1)
    }

    override fun getMessageID(): Int = 0xFFFF0162.toInt()

    override fun getMessageName(): String = "GroupAccountSummaryReply"
}
