package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class SetSimPresenceInDatabaseMessage : SLMessage() {
    var regionId: UUID = UUID(0L, 0L)
    var hostName: ByteArray = ByteArray(0)
    var gridX: Int = 0
    var gridY: Int = 0
    var pid: Int = 0
    var agentCount: Int = 0
    var timeToLive: Int = 0
    var status: ByteArray = ByteArray(0)


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, regionId)
        packVariable(buffer, hostName, 1)
        packInt(buffer, gridX)
        packInt(buffer, gridY)
        packInt(buffer, pid)
        packInt(buffer, agentCount)
        packInt(buffer, timeToLive)
        packVariable(buffer, status, 1)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        regionId = unpackUUID(buffer)
        hostName = unpackVariable(buffer, 1)
        gridX = unpackInt(buffer)
        gridY = unpackInt(buffer)
        pid = unpackInt(buffer)
        agentCount = unpackInt(buffer)
        timeToLive = unpackInt(buffer)
        status = unpackVariable(buffer, 1)
    }

    override fun getMessageID(): Int = 0xFFFF0017

    override fun getMessageName(): String = "SetSimPresenceInDatabase"
}
