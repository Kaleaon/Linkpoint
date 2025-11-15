package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class LoadUrlMessage : SLMessage() {
    var objectName: ByteArray = ByteArray(0)
    var objectId: UUID = UUID(0L, 0L)
    var ownerId: UUID = UUID(0L, 0L)
    var ownerIsGroup: Boolean = false
    var message: ByteArray = ByteArray(0)
    var url: ByteArray = ByteArray(0)


    override fun packPayload(buffer: ByteBuffer) {
        packVariable(buffer, objectName, 1)
        packUUID(buffer, objectId)
        packUUID(buffer, ownerId)
        packBoolean(buffer, ownerIsGroup)
        packVariable(buffer, message, 1)
        packVariable(buffer, url, 1)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        objectName = unpackVariable(buffer, 1)
        objectId = unpackUUID(buffer)
        ownerId = unpackUUID(buffer)
        ownerIsGroup = unpackBoolean(buffer)
        message = unpackVariable(buffer, 1)
        url = unpackVariable(buffer, 1)
    }

    override fun getMessageID(): Int = 0xFFFF00C2

    override fun getMessageName(): String = "LoadURL"
}
