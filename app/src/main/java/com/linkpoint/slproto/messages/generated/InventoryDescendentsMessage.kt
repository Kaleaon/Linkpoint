package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class InventoryDescendentsMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var folderId: UUID = UUID(0L, 0L)
    var ownerId: UUID = UUID(0L, 0L)
    var version: Int = 0
    var descendents: Int = 0
    val folderData: MutableList<FolderDataBlock> = mutableListOf()
    val itemData: MutableList<ItemDataBlock> = mutableListOf()

    data class FolderDataBlock(
        var folderId: UUID = UUID(0L, 0L),
        var parentId: UUID = UUID(0L, 0L),
        var type: Int = 0,
        var name: ByteArray = ByteArray(0)
    )
    data class ItemDataBlock(
        var itemId: UUID = UUID(0L, 0L),
        var folderId: UUID = UUID(0L, 0L),
        var creatorId: UUID = UUID(0L, 0L),
        var ownerId: UUID = UUID(0L, 0L),
        var groupId: UUID = UUID(0L, 0L),
        var baseMask: Int = 0,
        var ownerMask: Int = 0,
        var groupMask: Int = 0,
        var everyoneMask: Int = 0,
        var nextOwnerMask: Int = 0,
        var groupOwned: Boolean = false,
        var assetId: UUID = UUID(0L, 0L),
        var type: Int = 0,
        var invType: Int = 0,
        var flags: Int = 0,
        var saleType: Int = 0,
        var salePrice: Int = 0,
        var name: ByteArray = ByteArray(0),
        var description: ByteArray = ByteArray(0),
        var creationDate: Int = 0,
        var crc: Int = 0
    )


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, folderId)
        packUUID(buffer, ownerId)
        packInt(buffer, version)
        packInt(buffer, descendents)
        require(folderData.size <= 0xFF) { "FolderData size exceeds 255 (" + folderData.size + ")" }
        packByte(buffer, folderData.size)
        folderData.forEach { entry ->
            packUUID(buffer, entry.folderId)
            packUUID(buffer, entry.parentId)
            packByte(buffer, entry.type)
            packVariable(buffer, entry.name, 1)
        }
        require(itemData.size <= 0xFF) { "ItemData size exceeds 255 (" + itemData.size + ")" }
        packByte(buffer, itemData.size)
        itemData.forEach { entry ->
            packUUID(buffer, entry.itemId)
            packUUID(buffer, entry.folderId)
            packUUID(buffer, entry.creatorId)
            packUUID(buffer, entry.ownerId)
            packUUID(buffer, entry.groupId)
            packInt(buffer, entry.baseMask)
            packInt(buffer, entry.ownerMask)
            packInt(buffer, entry.groupMask)
            packInt(buffer, entry.everyoneMask)
            packInt(buffer, entry.nextOwnerMask)
            packBoolean(buffer, entry.groupOwned)
            packUUID(buffer, entry.assetId)
            packByte(buffer, entry.type)
            packByte(buffer, entry.invType)
            packInt(buffer, entry.flags)
            packByte(buffer, entry.saleType)
            packInt(buffer, entry.salePrice)
            packVariable(buffer, entry.name, 1)
            packVariable(buffer, entry.description, 1)
            packInt(buffer, entry.creationDate)
            packInt(buffer, entry.crc)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        folderId = unpackUUID(buffer)
        ownerId = unpackUUID(buffer)
        version = unpackInt(buffer)
        descendents = unpackInt(buffer)
        run {
            val count = unpackByte(buffer)
            folderData.clear()
            repeat(count) {
                val entry = FolderDataBlock()
                entry.folderId = unpackUUID(buffer)
                entry.parentId = unpackUUID(buffer)
                entry.type = unpackByte(buffer)
                entry.name = unpackVariable(buffer, 1)
                folderData += entry
            }
        }
        run {
            val count = unpackByte(buffer)
            itemData.clear()
            repeat(count) {
                val entry = ItemDataBlock()
                entry.itemId = unpackUUID(buffer)
                entry.folderId = unpackUUID(buffer)
                entry.creatorId = unpackUUID(buffer)
                entry.ownerId = unpackUUID(buffer)
                entry.groupId = unpackUUID(buffer)
                entry.baseMask = unpackInt(buffer)
                entry.ownerMask = unpackInt(buffer)
                entry.groupMask = unpackInt(buffer)
                entry.everyoneMask = unpackInt(buffer)
                entry.nextOwnerMask = unpackInt(buffer)
                entry.groupOwned = unpackBoolean(buffer)
                entry.assetId = unpackUUID(buffer)
                entry.type = unpackByte(buffer)
                entry.invType = unpackByte(buffer)
                entry.flags = unpackInt(buffer)
                entry.saleType = unpackByte(buffer)
                entry.salePrice = unpackInt(buffer)
                entry.name = unpackVariable(buffer, 1)
                entry.description = unpackVariable(buffer, 1)
                entry.creationDate = unpackInt(buffer)
                entry.crc = unpackInt(buffer)
                itemData += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF0116

    override fun getMessageName(): String = "InventoryDescendents"
}
