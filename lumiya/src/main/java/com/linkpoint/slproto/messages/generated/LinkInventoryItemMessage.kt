package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class LinkInventoryItemMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var callbackId: Int = 0
    var folderId: UUID = UUID(0L, 0L)
    var transactionId: UUID = UUID(0L, 0L)
    var oldItemId: UUID = UUID(0L, 0L)
    var type: Int = 0
    var invType: Int = 0
    var name: ByteArray = ByteArray(0)
    var description: ByteArray = ByteArray(0)


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packInt(buffer, callbackId)
        packUUID(buffer, folderId)
        packUUID(buffer, transactionId)
        packUUID(buffer, oldItemId)
        packByte(buffer, type)
        packByte(buffer, invType)
        packVariable(buffer, name, 1)
        packVariable(buffer, description, 1)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        callbackId = unpackInt(buffer)
        folderId = unpackUUID(buffer)
        transactionId = unpackUUID(buffer)
        oldItemId = unpackUUID(buffer)
        type = unpackByte(buffer)
        invType = unpackByte(buffer)
        name = unpackVariable(buffer, 1)
        description = unpackVariable(buffer, 1)
    }

    override fun getMessageID(): Int = 0xFFFF01AA.toInt()

    override fun getMessageName(): String = "LinkInventoryItem"
}
