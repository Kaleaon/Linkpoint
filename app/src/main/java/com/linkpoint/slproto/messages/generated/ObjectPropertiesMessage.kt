package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ObjectPropertiesMessage : SLMessage() {
    val objectData: MutableList<ObjectDataBlock> = mutableListOf()

    data class ObjectDataBlock(
        var objectId: UUID = UUID(0L, 0L),
        var creatorId: UUID = UUID(0L, 0L),
        var ownerId: UUID = UUID(0L, 0L),
        var groupId: UUID = UUID(0L, 0L),
        var creationDate: Long = 0L,
        var baseMask: Int = 0,
        var ownerMask: Int = 0,
        var groupMask: Int = 0,
        var everyoneMask: Int = 0,
        var nextOwnerMask: Int = 0,
        var ownershipCost: Int = 0,
        var saleType: Int = 0,
        var salePrice: Int = 0,
        var aggregatePerms: Int = 0,
        var aggregatePermTextures: Int = 0,
        var aggregatePermTexturesOwner: Int = 0,
        var category: Int = 0,
        var inventorySerial: Int = 0,
        var itemId: UUID = UUID(0L, 0L),
        var folderId: UUID = UUID(0L, 0L),
        var fromTaskId: UUID = UUID(0L, 0L),
        var lastOwnerId: UUID = UUID(0L, 0L),
        var name: ByteArray = ByteArray(0),
        var description: ByteArray = ByteArray(0),
        var touchName: ByteArray = ByteArray(0),
        var sitName: ByteArray = ByteArray(0),
        var textureId: ByteArray = ByteArray(0)
    )


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        require(objectData.size <= 0xFF) { "ObjectData size exceeds 255 (" + objectData.size + ")" }
        packByte(buffer, objectData.size)
        objectData.forEach { entry ->
            packUUID(buffer, entry.objectId)
            packUUID(buffer, entry.creatorId)
            packUUID(buffer, entry.ownerId)
            packUUID(buffer, entry.groupId)
            packLong(buffer, entry.creationDate)
            packInt(buffer, entry.baseMask)
            packInt(buffer, entry.ownerMask)
            packInt(buffer, entry.groupMask)
            packInt(buffer, entry.everyoneMask)
            packInt(buffer, entry.nextOwnerMask)
            packInt(buffer, entry.ownershipCost)
            packByte(buffer, entry.saleType)
            packInt(buffer, entry.salePrice)
            packByte(buffer, entry.aggregatePerms)
            packByte(buffer, entry.aggregatePermTextures)
            packByte(buffer, entry.aggregatePermTexturesOwner)
            packInt(buffer, entry.category)
            packShort(buffer, entry.inventorySerial)
            packUUID(buffer, entry.itemId)
            packUUID(buffer, entry.folderId)
            packUUID(buffer, entry.fromTaskId)
            packUUID(buffer, entry.lastOwnerId)
            packVariable(buffer, entry.name, 1)
            packVariable(buffer, entry.description, 1)
            packVariable(buffer, entry.touchName, 1)
            packVariable(buffer, entry.sitName, 1)
            packVariable(buffer, entry.textureId, 1)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        run {
            val count = unpackByte(buffer)
            objectData.clear()
            repeat(count) {
                val entry = ObjectDataBlock()
                entry.objectId = unpackUUID(buffer)
                entry.creatorId = unpackUUID(buffer)
                entry.ownerId = unpackUUID(buffer)
                entry.groupId = unpackUUID(buffer)
                entry.creationDate = unpackLong(buffer)
                entry.baseMask = unpackInt(buffer)
                entry.ownerMask = unpackInt(buffer)
                entry.groupMask = unpackInt(buffer)
                entry.everyoneMask = unpackInt(buffer)
                entry.nextOwnerMask = unpackInt(buffer)
                entry.ownershipCost = unpackInt(buffer)
                entry.saleType = unpackByte(buffer)
                entry.salePrice = unpackInt(buffer)
                entry.aggregatePerms = unpackByte(buffer)
                entry.aggregatePermTextures = unpackByte(buffer)
                entry.aggregatePermTexturesOwner = unpackByte(buffer)
                entry.category = unpackInt(buffer)
                entry.inventorySerial = unpackShort(buffer)
                entry.itemId = unpackUUID(buffer)
                entry.folderId = unpackUUID(buffer)
                entry.fromTaskId = unpackUUID(buffer)
                entry.lastOwnerId = unpackUUID(buffer)
                entry.name = unpackVariable(buffer, 1)
                entry.description = unpackVariable(buffer, 1)
                entry.touchName = unpackVariable(buffer, 1)
                entry.sitName = unpackVariable(buffer, 1)
                entry.textureId = unpackVariable(buffer, 1)
                objectData += entry
            }
        }
    }

    override fun getMessageID(): Int = 0x0000FF09

    override fun getMessageName(): String = "ObjectProperties"
}
