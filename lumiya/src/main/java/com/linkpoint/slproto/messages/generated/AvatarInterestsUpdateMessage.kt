package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class AvatarInterestsUpdateMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var wantToMask: Int = 0
    var wantToText: ByteArray = ByteArray(0)
    var skillsMask: Int = 0
    var skillsText: ByteArray = ByteArray(0)
    var languagesText: ByteArray = ByteArray(0)


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packInt(buffer, wantToMask)
        packVariable(buffer, wantToText, 1)
        packInt(buffer, skillsMask)
        packVariable(buffer, skillsText, 1)
        packVariable(buffer, languagesText, 1)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        wantToMask = unpackInt(buffer)
        wantToText = unpackVariable(buffer, 1)
        skillsMask = unpackInt(buffer)
        skillsText = unpackVariable(buffer, 1)
        languagesText = unpackVariable(buffer, 1)
    }

    override fun getMessageID(): Int = 0xFFFF00AF

    override fun getMessageName(): String = "AvatarInterestsUpdate"
}
