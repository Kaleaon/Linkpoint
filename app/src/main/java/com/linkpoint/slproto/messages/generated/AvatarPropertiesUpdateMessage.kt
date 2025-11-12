package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class AvatarPropertiesUpdateMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var imageId: UUID = UUID(0L, 0L)
    var flImageId: UUID = UUID(0L, 0L)
    var aboutText: ByteArray = ByteArray(0)
    var flAboutText: ByteArray = ByteArray(0)
    var allowPublish: Boolean = false
    var maturePublish: Boolean = false
    var profileUrl: ByteArray = ByteArray(0)


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packUUID(buffer, imageId)
        packUUID(buffer, flImageId)
        packVariable(buffer, aboutText, 2)
        packVariable(buffer, flAboutText, 1)
        packBoolean(buffer, allowPublish)
        packBoolean(buffer, maturePublish)
        packVariable(buffer, profileUrl, 1)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        imageId = unpackUUID(buffer)
        flImageId = unpackUUID(buffer)
        aboutText = unpackVariable(buffer, 2)
        flAboutText = unpackVariable(buffer, 1)
        allowPublish = unpackBoolean(buffer)
        maturePublish = unpackBoolean(buffer)
        profileUrl = unpackVariable(buffer, 1)
    }

    override fun getMessageID(): Int = 0xFFFF00AE

    override fun getMessageName(): String = "AvatarPropertiesUpdate"
}
