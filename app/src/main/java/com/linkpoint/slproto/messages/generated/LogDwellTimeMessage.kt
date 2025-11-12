package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class LogDwellTimeMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var duration: Float = 0f
    var simName: ByteArray = ByteArray(0)
    var regionX: Int = 0
    var regionY: Int = 0
    var avgAgentsInView: Int = 0
    var avgViewerFps: Int = 0


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packFloat(buffer, duration)
        packVariable(buffer, simName, 1)
        packInt(buffer, regionX)
        packInt(buffer, regionY)
        packByte(buffer, avgAgentsInView)
        packByte(buffer, avgViewerFps)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        duration = unpackFloat(buffer)
        simName = unpackVariable(buffer, 1)
        regionX = unpackInt(buffer)
        regionY = unpackInt(buffer)
        avgAgentsInView = unpackByte(buffer)
        avgViewerFps = unpackByte(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF0012

    override fun getMessageName(): String = "LogDwellTime"
}
