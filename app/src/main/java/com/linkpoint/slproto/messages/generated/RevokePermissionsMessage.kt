package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class RevokePermissionsMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var objectId: UUID = UUID(0L, 0L)
    var objectPermissions: Int = 0


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packUUID(buffer, objectId)
        packInt(buffer, objectPermissions)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        objectId = unpackUUID(buffer)
        objectPermissions = unpackInt(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF00C1

    override fun getMessageName(): String = "RevokePermissions"
}
