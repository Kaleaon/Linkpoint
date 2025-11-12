package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class AvatarPropertiesRequestBackendMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var avatarId: UUID = UUID(0L, 0L)
    var godLevel: Int = 0
    var webProfilesDisabled: Boolean = false


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, avatarId)
        packByte(buffer, godLevel)
        packBoolean(buffer, webProfilesDisabled)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        avatarId = unpackUUID(buffer)
        godLevel = unpackByte(buffer)
        webProfilesDisabled = unpackBoolean(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF00AA

    override fun getMessageName(): String = "AvatarPropertiesRequestBackend"
}
