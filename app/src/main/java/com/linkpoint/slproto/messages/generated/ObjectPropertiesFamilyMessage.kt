package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ObjectPropertiesFamilyMessage : SLMessage() {
    var requestFlags: Int = 0
    var objectId: UUID = UUID(0L, 0L)
    var ownerId: UUID = UUID(0L, 0L)
    var groupId: UUID = UUID(0L, 0L)
    var baseMask: Int = 0
    var ownerMask: Int = 0
    var groupMask: Int = 0
    var everyoneMask: Int = 0
    var nextOwnerMask: Int = 0
    var ownershipCost: Int = 0
    var saleType: Int = 0
    var salePrice: Int = 0
    var category: Int = 0
    var lastOwnerId: UUID = UUID(0L, 0L)
    var name: ByteArray = ByteArray(0)
    var description: ByteArray = ByteArray(0)


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packInt(buffer, requestFlags)
        packUUID(buffer, objectId)
        packUUID(buffer, ownerId)
        packUUID(buffer, groupId)
        packInt(buffer, baseMask)
        packInt(buffer, ownerMask)
        packInt(buffer, groupMask)
        packInt(buffer, everyoneMask)
        packInt(buffer, nextOwnerMask)
        packInt(buffer, ownershipCost)
        packByte(buffer, saleType)
        packInt(buffer, salePrice)
        packInt(buffer, category)
        packUUID(buffer, lastOwnerId)
        packVariable(buffer, name, 1)
        packVariable(buffer, description, 1)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        requestFlags = unpackInt(buffer)
        objectId = unpackUUID(buffer)
        ownerId = unpackUUID(buffer)
        groupId = unpackUUID(buffer)
        baseMask = unpackInt(buffer)
        ownerMask = unpackInt(buffer)
        groupMask = unpackInt(buffer)
        everyoneMask = unpackInt(buffer)
        nextOwnerMask = unpackInt(buffer)
        ownershipCost = unpackInt(buffer)
        saleType = unpackByte(buffer)
        salePrice = unpackInt(buffer)
        category = unpackInt(buffer)
        lastOwnerId = unpackUUID(buffer)
        name = unpackVariable(buffer, 1)
        description = unpackVariable(buffer, 1)
    }

    override fun getMessageID(): Int = 0x0000FF0A

    override fun getMessageName(): String = "ObjectPropertiesFamily"
}
