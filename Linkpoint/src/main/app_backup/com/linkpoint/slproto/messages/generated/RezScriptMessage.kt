package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class RezScriptMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var groupId: UUID = UUID(0L, 0L)
    var objectLocalId: Int = 0
    var enabled: Boolean = false
    var itemId: UUID = UUID(0L, 0L)
    var folderId: UUID = UUID(0L, 0L)
    var creatorId: UUID = UUID(0L, 0L)
    var ownerId: UUID = UUID(0L, 0L)
    var groupOwnerId: UUID = UUID(0L, 0L) // Renamed to avoid conflict with agent block groupId
    var baseMask: Int = 0
    var ownerMask: Int = 0
    var groupMask: Int = 0
    var everyoneMask: Int = 0
    var nextOwnerMask: Int = 0
    var groupOwned: Boolean = false
    var transactionId: UUID = UUID(0L, 0L)
    var type: Int = 0
    var invType: Int = 0
    var flags: Int = 0
    var saleType: Int = 0
    var salePrice: Int = 0
    var name: ByteArray = ByteArray(0)
    var description: ByteArray = ByteArray(0)
    var creationDate: Int = 0
    var crc: Int = 0


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packUUID(buffer, groupId)
        packInt(buffer, objectLocalId)
        packBoolean(buffer, enabled)
        packUUID(buffer, itemId)
        packUUID(buffer, folderId)
        packUUID(buffer, creatorId)
        packUUID(buffer, ownerId)
        packUUID(buffer, groupOwnerId)
        packInt(buffer, baseMask)
        packInt(buffer, ownerMask)
        packInt(buffer, groupMask)
        packInt(buffer, everyoneMask)
        packInt(buffer, nextOwnerMask)
        packBoolean(buffer, groupOwned)
        packUUID(buffer, transactionId)
        packByte(buffer, type)
        packByte(buffer, invType)
        packInt(buffer, flags)
        packByte(buffer, saleType)
        packInt(buffer, salePrice)
        packVariable(buffer, name, 1)
        packVariable(buffer, description, 1)
        packInt(buffer, creationDate)
        packInt(buffer, crc)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        groupId = unpackUUID(buffer)
        objectLocalId = unpackInt(buffer)
        enabled = unpackBoolean(buffer)
        itemId = unpackUUID(buffer)
        folderId = unpackUUID(buffer)
        creatorId = unpackUUID(buffer)
        ownerId = unpackUUID(buffer)
        groupOwnerId = unpackUUID(buffer)
        baseMask = unpackInt(buffer)
        ownerMask = unpackInt(buffer)
        groupMask = unpackInt(buffer)
        everyoneMask = unpackInt(buffer)
        nextOwnerMask = unpackInt(buffer)
        groupOwned = unpackBoolean(buffer)
        transactionId = unpackUUID(buffer)
        type = unpackByte(buffer)
        invType = unpackByte(buffer)
        flags = unpackInt(buffer)
        saleType = unpackByte(buffer)
        salePrice = unpackInt(buffer)
        name = unpackVariable(buffer, 1)
        description = unpackVariable(buffer, 1)
        creationDate = unpackInt(buffer)
        crc = unpackInt(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF0130.toInt()

    override fun getMessageName(): String = "RezScript"
}
