package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.UUID

class UserReportInternalMessage : SLMessage() {
    var reportType: Int = 0
    var category: Int = 0
    var reporterId: UUID = UUID(0L, 0L)
    var viewerPosition: LLVector3 = LLVector3()
    var agentPosition: LLVector3 = LLVector3()
    var screenshotId: UUID = UUID(0L, 0L)
    var objectId: UUID = UUID(0L, 0L)
    var ownerId: UUID = UUID(0L, 0L)
    var lastOwnerId: UUID = UUID(0L, 0L)
    var creatorId: UUID = UUID(0L, 0L)
    var regionId: UUID = UUID(0L, 0L)
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
        packByte(buffer, reportType)
        packByte(buffer, category)
        packUUID(buffer, reporterId)
        viewerPosition.pack(buffer)
        agentPosition.pack(buffer)
        packUUID(buffer, screenshotId)
        packUUID(buffer, objectId)
        packUUID(buffer, ownerId)
        packUUID(buffer, lastOwnerId)
        packUUID(buffer, creatorId)
        packUUID(buffer, regionId)
        packUUID(buffer, abuserId)
        packVariable(buffer, abuseRegionName, 1)
        packUUID(buffer, abuseRegionId)
        packVariable(buffer, summary, 1)
        packVariable(buffer, details, 2)
        packVariable(buffer, versionString, 1)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        reportType = unpackByte(buffer)
        category = unpackByte(buffer)
        reporterId = unpackUUID(buffer)
        viewerPosition = LLVector3.unpack(buffer)
        agentPosition = LLVector3.unpack(buffer)
        screenshotId = unpackUUID(buffer)
        objectId = unpackUUID(buffer)
        ownerId = unpackUUID(buffer)
        lastOwnerId = unpackUUID(buffer)
        creatorId = unpackUUID(buffer)
        regionId = unpackUUID(buffer)
        abuserId = unpackUUID(buffer)
        abuseRegionName = unpackVariable(buffer, 1)
        abuseRegionId = unpackUUID(buffer)
        summary = unpackVariable(buffer, 1)
        details = unpackVariable(buffer, 2)
        versionString = unpackVariable(buffer, 1)
    }

    override fun getMessageID(): Int = 0xFFFF0015

    override fun getMessageName(): String = "UserReportInternal"
}
