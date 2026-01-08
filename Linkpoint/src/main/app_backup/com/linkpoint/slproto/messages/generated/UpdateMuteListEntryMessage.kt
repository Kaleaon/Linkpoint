package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class UpdateMuteListEntryMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var muteId: UUID = UUID(0L, 0L)
    var muteName: ByteArray = ByteArray(0)
    var muteType: Int = 0
    var muteFlags: Int = 0


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packUUID(buffer, muteId)
        packVariable(buffer, muteName, 1)
        packInt(buffer, muteType)
        packInt(buffer, muteFlags)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        muteId = unpackUUID(buffer)
        muteName = unpackVariable(buffer, 1)
        muteType = unpackInt(buffer)
        muteFlags = unpackInt(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF0107.toInt()

    override fun getMessageName(): String = "UpdateMuteListEntry"
}
