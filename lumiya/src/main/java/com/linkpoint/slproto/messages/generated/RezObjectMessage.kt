package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.UUID

class RezObjectMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var groupId: UUID = UUID(0L, 0L)
    var fromTaskId: UUID = UUID(0L, 0L)
    var bypassRaycast: Int = 0
    var rayStart: LLVector3 = LLVector3()
    var rayEnd: LLVector3 = LLVector3()
    var rayTargetId: UUID = UUID(0L, 0L)
    var rayEndIsIntersection: Boolean = false
    var rezSelected: Boolean = false
    var removeItem: Boolean = false
    var itemFlags: Int = 0
    var groupMask: Int = 0
    var everyoneMask: Int = 0
    var nextOwnerMask: Int = 0
    var itemId: UUID = UUID(0L, 0L)
    var folderId: UUID = UUID(0L, 0L)
    var creatorId: UUID = UUID(0L, 0L)
    var ownerId: UUID = UUID(0L, 0L)
    var groupId: UUID = UUID(0L, 0L)
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
        packUUID(buffer, fromTaskId)
        packByte(buffer, bypassRaycast)
        rayStart.pack(buffer)
        rayEnd.pack(buffer)
        packUUID(buffer, rayTargetId)
        packBoolean(buffer, rayEndIsIntersection)
        packBoolean(buffer, rezSelected)
        packBoolean(buffer, removeItem)
        packInt(buffer, itemFlags)
        packInt(buffer, groupMask)
        packInt(buffer, everyoneMask)
        packInt(buffer, nextOwnerMask)
        packUUID(buffer, itemId)
        packUUID(buffer, folderId)
        packUUID(buffer, creatorId)
        packUUID(buffer, ownerId)
        packUUID(buffer, groupId)
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
        fromTaskId = unpackUUID(buffer)
        bypassRaycast = unpackByte(buffer)
        rayStart = LLVector3.unpack(buffer)
        rayEnd = LLVector3.unpack(buffer)
        rayTargetId = unpackUUID(buffer)
        rayEndIsIntersection = unpackBoolean(buffer)
        rezSelected = unpackBoolean(buffer)
        removeItem = unpackBoolean(buffer)
        itemFlags = unpackInt(buffer)
        groupMask = unpackInt(buffer)
        everyoneMask = unpackInt(buffer)
        nextOwnerMask = unpackInt(buffer)
        itemId = unpackUUID(buffer)
        folderId = unpackUUID(buffer)
        creatorId = unpackUUID(buffer)
        ownerId = unpackUUID(buffer)
        groupId = unpackUUID(buffer)
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

    override fun getMessageID(): Int = 0xFFFF0125

    override fun getMessageName(): String = "RezObject"
}
