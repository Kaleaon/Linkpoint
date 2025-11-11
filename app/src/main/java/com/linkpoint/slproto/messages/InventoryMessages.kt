package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.util.UUID

/**
 * FetchInventory - request inventory items by ID.
 */
class FetchInventoryMessage : SLMessage() {
    var agentId: UUID = UUID.randomUUID()
    var sessionId: UUID = UUID.randomUUID()

    data class InventoryData(
        var ownerId: UUID = UUID.randomUUID(),
        var itemId: UUID = UUID.randomUUID(),
    )

    val inventoryItems: MutableList<InventoryData> = mutableListOf()

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        require(inventoryItems.size <= 0xFF) { "Inventory request exceeds 255 entries (${inventoryItems.size})" }
        packByte(buffer, inventoryItems.size)
        inventoryItems.forEach { entry ->
            packUUID(buffer, entry.ownerId)
            packUUID(buffer, entry.itemId)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        val count = unpackByte(buffer)
        inventoryItems.clear()
        repeat(count) {
            val owner = unpackUUID(buffer)
            val item = unpackUUID(buffer)
            inventoryItems += InventoryData(owner, item)
        }
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.FETCH_INVENTORY

    override fun getMessageName(): String = "FetchInventory"
}

/**
 * FetchInventoryReply - response listing inventory details.
 */
class FetchInventoryReplyMessage : SLMessage() {
    var agentId: UUID = UUID.randomUUID()

    data class InventoryData(
        var itemId: UUID = UUID.randomUUID(),
        var folderId: UUID = UUID.randomUUID(),
        var creatorId: UUID = UUID.randomUUID(),
        var ownerId: UUID = UUID.randomUUID(),
        var groupId: UUID = UUID.randomUUID(),
        var baseMask: Int = 0,
        var ownerMask: Int = 0,
        var groupMask: Int = 0,
        var everyoneMask: Int = 0,
        var nextOwnerMask: Int = 0,
        var groupOwned: Boolean = false,
        var assetId: UUID = UUID.randomUUID(),
        var type: Int = 0,
        var invType: Int = 0,
        var flags: Int = 0,
        var saleType: Int = 0,
        var salePrice: Int = 0,
        var name: String = "",
        var description: String = "",
        var creationDate: Int = 0,
        var crc: Int = 0,
    )

    val inventoryItems: MutableList<InventoryData> = mutableListOf()

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        require(inventoryItems.size <= 0xFF) { "Inventory reply exceeds 255 entries (${inventoryItems.size})" }
        packByte(buffer, inventoryItems.size)
        inventoryItems.forEach { entry ->
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
            packByte(buffer, entry.type and 0xFF)
            packByte(buffer, entry.invType and 0xFF)
            packInt(buffer, entry.flags)
            packByte(buffer, entry.saleType and 0xFF)
            packInt(buffer, entry.salePrice)
            packVariable(buffer, entry.name.toByteArray(StandardCharsets.UTF_8), 1)
            packVariable(buffer, entry.description.toByteArray(StandardCharsets.UTF_8), 1)
            packInt(buffer, entry.creationDate)
            packInt(buffer, entry.crc)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        val count = unpackByte(buffer)
        inventoryItems.clear()
        repeat(count) {
            val entry =
                InventoryData(
                    itemId = unpackUUID(buffer),
                    folderId = unpackUUID(buffer),
                    creatorId = unpackUUID(buffer),
                    ownerId = unpackUUID(buffer),
                    groupId = unpackUUID(buffer),
                    baseMask = unpackInt(buffer),
                    ownerMask = unpackInt(buffer),
                    groupMask = unpackInt(buffer),
                    everyoneMask = unpackInt(buffer),
                    nextOwnerMask = unpackInt(buffer),
                    groupOwned = unpackBoolean(buffer),
                    assetId = unpackUUID(buffer),
                    type = unpackByte(buffer),
                    invType = unpackByte(buffer),
                    flags = unpackInt(buffer),
                    saleType = unpackByte(buffer),
                    salePrice = unpackInt(buffer),
                    name = String(unpackVariable(buffer, 1), StandardCharsets.UTF_8),
                    description = String(unpackVariable(buffer, 1), StandardCharsets.UTF_8),
                    creationDate = unpackInt(buffer),
                    crc = unpackInt(buffer),
                )
            inventoryItems += entry
        }
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.FETCH_INVENTORY_REPLY

    override fun getMessageName(): String = "FetchInventoryReply"
}
