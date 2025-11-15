package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.UUID

class UserReportMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var reportType: Int = 0
    var category: Int = 0
    var position: LLVector3 = LLVector3()
    var checkFlags: Int = 0
    var screenshotId: UUID = UUID(0L, 0L)
    var objectId: UUID = UUID(0L, 0L)
    var abuserId: UUID = UUID(0L, 0L)
    var abuseRegionName: ByteArray = ByteArray(0)
    var abuseRegionId: UUID = UUID(0L, 0L)
    var summary: ByteArray = ByteArray(0)
    var details: ByteArray = ByteArray(0)
    var versionString: ByteArray = ByteArray(0)


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packByte(buffer, reportType)
        packByte(buffer, category)
        position.pack(buffer)
        packByte(buffer, checkFlags)
        packUUID(buffer, screenshotId)
        packUUID(buffer, objectId)
        packUUID(buffer, abuserId)
        packVariable(buffer, abuseRegionName, 1)
        packUUID(buffer, abuseRegionId)
        packVariable(buffer, summary, 1)
        packVariable(buffer, details, 2)
        packVariable(buffer, versionString, 1)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        reportType = unpackByte(buffer)
        category = unpackByte(buffer)
        position = LLVector3.unpack(buffer)
        checkFlags = unpackByte(buffer)
        screenshotId = unpackUUID(buffer)
        objectId = unpackUUID(buffer)
        abuserId = unpackUUID(buffer)
        abuseRegionName = unpackVariable(buffer, 1)
        abuseRegionId = unpackUUID(buffer)
        summary = unpackVariable(buffer, 1)
        details = unpackVariable(buffer, 2)
        versionString = unpackVariable(buffer, 1)
    }

    override fun getMessageID(): Int = 0xFFFF0085

    override fun getMessageName(): String = "UserReport"
}
