package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.util.UUID

/**
 * Bulk update inventory message (0xFFFF0119)
 */
class BulkUpdateInventoryMessage : SLMessage() {
    data class FolderEntry(
        var folderId: UUID = UUID(0L, 0L),
        var parentId: UUID = UUID(0L, 0L),
        var type: Int = 0,
        var name: String = "",
    )

    data class ItemEntry(
        var itemId: UUID = UUID(0L, 0L),
        var callbackId: Int = 0,
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
        var inventoryType: Int = 0,
        var flags: Int = 0,
        var saleType: Int = 0,
        var salePrice: Int = 0,
        var name: String = "",
        var description: String = "",
        var creationDate: Int = 0,
        var crc: Int = 0,
    )

    var agentId: UUID = UUID(0L, 0L)
    var transactionId: UUID = UUID(0L, 0L)
    val folders: MutableList<FolderEntry> = mutableListOf()
    val items: MutableList<ItemEntry> = mutableListOf()

    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, transactionId)
        require(folders.size in 0..0xFF) { "folders size out of range (${folders.size})" }
        packByte(buffer, folders.size)
        folders.forEach { folder ->
            packUUID(buffer, folder.folderId)
            packUUID(buffer, folder.parentId)
            require(folder.type in 0..0xFF) { "folder type out of range (${folder.type})" }
            packByte(buffer, folder.type)
            packVariable(buffer, folder.name.toByteArray(StandardCharsets.UTF_8), 1)
        }
        require(items.size in 0..0xFF) { "items size out of range (${items.size})" }
        packByte(buffer, items.size)
        items.forEach { item ->
            packUUID(buffer, item.itemId)
            packInt(buffer, item.callbackId)
            packUUID(buffer, item.folderId)
            packUUID(buffer, item.creatorId)
            packUUID(buffer, item.ownerId)
            packUUID(buffer, item.groupId)
            packInt(buffer, item.baseMask)
            packInt(buffer, item.ownerMask)
            packInt(buffer, item.groupMask)
            packInt(buffer, item.everyoneMask)
            packInt(buffer, item.nextOwnerMask)
            packBoolean(buffer, item.groupOwned)
            packUUID(buffer, item.assetId)
            require(item.type in 0..0xFF) { "item type out of range (${item.type})" }
            packByte(buffer, item.type)
            require(item.inventoryType in 0..0xFF) { "inventory type out of range (${item.inventoryType})" }
            packByte(buffer, item.inventoryType)
            packInt(buffer, item.flags)
            require(item.saleType in 0..0xFF) { "saleType out of range (${item.saleType})" }
            packByte(buffer, item.saleType)
            packInt(buffer, item.salePrice)
            packVariable(buffer, item.name.toByteArray(StandardCharsets.UTF_8), 1)
            packVariable(buffer, item.description.toByteArray(StandardCharsets.UTF_8), 1)
            packInt(buffer, item.creationDate)
            packInt(buffer, item.crc)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        transactionId = unpackUUID(buffer)
        folders.clear()
        repeat(unpackByte(buffer)) {
            folders += FolderEntry(
                folderId = unpackUUID(buffer),
                parentId = unpackUUID(buffer),
                type = unpackByte(buffer),
                name = String(unpackVariable(buffer, 1), StandardCharsets.UTF_8),
            )
        }
        items.clear()
        repeat(unpackByte(buffer)) {
            items +=
                ItemEntry(
                    itemId = unpackUUID(buffer),
                    callbackId = unpackInt(buffer),
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
                    inventoryType = unpackByte(buffer),
                    flags = unpackInt(buffer),
                    saleType = unpackByte(buffer),
                    salePrice = unpackInt(buffer),
                    name = String(unpackVariable(buffer, 1), StandardCharsets.UTF_8),
                    description = String(unpackVariable(buffer, 1), StandardCharsets.UTF_8),
                    creationDate = unpackInt(buffer),
                    crc = unpackInt(buffer),
                )
        }
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.BULK_UPDATE_INVENTORY

    override fun getMessageName(): String = "BulkUpdateInventory"
}

/**
 * Change inventory item flags message (0xFFFF010F)
 */
class ChangeInventoryItemFlagsMessage : SLMessage() {
    data class InventoryEntry(
        var itemId: UUID = UUID(0L, 0L),
        var flags: Int = 0,
    )

    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    val inventory: MutableList<InventoryEntry> = mutableListOf()

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        require(inventory.size in 0..0xFF) { "inventory size out of range (${inventory.size})" }
        packByte(buffer, inventory.size)
        inventory.forEach { entry ->
            packUUID(buffer, entry.itemId)
            packInt(buffer, entry.flags)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        inventory.clear()
        repeat(unpackByte(buffer)) {
            inventory += InventoryEntry(itemId = unpackUUID(buffer), flags = unpackInt(buffer))
        }
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.CHANGE_INVENTORY_ITEM_FLAGS

    override fun getMessageName(): String = "ChangeInventoryItemFlags"
}

/**
 * Copy inventory item message (0xFFFF010D)
 */
class CopyInventoryItemMessage : SLMessage() {
    data class InventoryEntry(
        var callbackId: Int = 0,
        var oldAgentId: UUID = UUID(0L, 0L),
        var oldItemId: UUID = UUID(0L, 0L),
        var newFolderId: UUID = UUID(0L, 0L),
        var newName: String = "",
    )

    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    val inventory: MutableList<InventoryEntry> = mutableListOf()

    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        require(inventory.size in 0..0xFF) { "inventory size out of range (${inventory.size})" }
        packByte(buffer, inventory.size)
        inventory.forEach { entry ->
            packInt(buffer, entry.callbackId)
            packUUID(buffer, entry.oldAgentId)
            packUUID(buffer, entry.oldItemId)
            packUUID(buffer, entry.newFolderId)
            packVariable(buffer, entry.newName.toByteArray(StandardCharsets.UTF_8), 1)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        inventory.clear()
        repeat(unpackByte(buffer)) {
            inventory +=
                InventoryEntry(
                    callbackId = unpackInt(buffer),
                    oldAgentId = unpackUUID(buffer),
                    oldItemId = unpackUUID(buffer),
                    newFolderId = unpackUUID(buffer),
                    newName = String(unpackVariable(buffer, 1), StandardCharsets.UTF_8),
                )
        }
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.COPY_INVENTORY_ITEM

    override fun getMessageName(): String = "CopyInventoryItem"
}

/**
 * Copy inventory from notecard message (0xFFFF0109)
 */
class CopyInventoryFromNotecardMessage : SLMessage() {
    data class InventoryEntry(
        var itemId: UUID = UUID(0L, 0L),
        var folderId: UUID = UUID(0L, 0L),
    )

    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var notecardItemId: UUID = UUID(0L, 0L)
    var objectId: UUID = UUID(0L, 0L)
    val inventory: MutableList<InventoryEntry> = mutableListOf()

    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packUUID(buffer, notecardItemId)
        packUUID(buffer, objectId)
        require(inventory.size in 0..0xFF) { "inventory size out of range (${inventory.size})" }
        packByte(buffer, inventory.size)
        inventory.forEach { entry ->
            packUUID(buffer, entry.itemId)
            packUUID(buffer, entry.folderId)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        notecardItemId = unpackUUID(buffer)
        objectId = unpackUUID(buffer)
        inventory.clear()
        repeat(unpackByte(buffer)) {
            inventory += InventoryEntry(itemId = unpackUUID(buffer), folderId = unpackUUID(buffer))
        }
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.COPY_INVENTORY_FROM_NOTECARD

    override fun getMessageName(): String = "CopyInventoryFromNotecard"
}

/**
 * Move inventory item message (0xFFFF010C)
 */
class MoveInventoryItemMessage : SLMessage() {
    data class InventoryEntry(
        var itemId: UUID = UUID(0L, 0L),
        var folderId: UUID = UUID(0L, 0L),
        var newName: String = "",
    )

    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var stamp: Boolean = false
    val inventory: MutableList<InventoryEntry> = mutableListOf()

    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packBoolean(buffer, stamp)
        require(inventory.size in 0..0xFF) { "inventory size out of range (${inventory.size})" }
        packByte(buffer, inventory.size)
        inventory.forEach { entry ->
            packUUID(buffer, entry.itemId)
            packUUID(buffer, entry.folderId)
            packVariable(buffer, entry.newName.toByteArray(StandardCharsets.UTF_8), 1)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        stamp = unpackBoolean(buffer)
        inventory.clear()
        repeat(unpackByte(buffer)) {
            inventory +=
                InventoryEntry(
                    itemId = unpackUUID(buffer),
                    folderId = unpackUUID(buffer),
                    newName = String(unpackVariable(buffer, 1), StandardCharsets.UTF_8),
                )
        }
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.MOVE_INVENTORY_ITEM

    override fun getMessageName(): String = "MoveInventoryItem"
}

/**
 * Move inventory folder message (0xFFFF0113)
 */
class MoveInventoryFolderMessage : SLMessage() {
    data class InventoryEntry(
        var folderId: UUID = UUID(0L, 0L),
        var parentId: UUID = UUID(0L, 0L),
    )

    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var stamp: Boolean = false
    val inventory: MutableList<InventoryEntry> = mutableListOf()

    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packBoolean(buffer, stamp)
        require(inventory.size in 0..0xFF) { "inventory size out of range (${inventory.size})" }
        packByte(buffer, inventory.size)
        inventory.forEach { entry ->
            packUUID(buffer, entry.folderId)
            packUUID(buffer, entry.parentId)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        stamp = unpackBoolean(buffer)
        inventory.clear()
        repeat(unpackByte(buffer)) {
            inventory += InventoryEntry(folderId = unpackUUID(buffer), parentId = unpackUUID(buffer))
        }
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.MOVE_INVENTORY_FOLDER

    override fun getMessageName(): String = "MoveInventoryFolder"
}

/**
 * Create inventory item message (0xFFFF0131)
 */
class CreateInventoryItemMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var callbackId: Int = 0
    var folderId: UUID = UUID(0L, 0L)
    var transactionId: UUID = UUID(0L, 0L)
    var nextOwnerMask: Int = 0
    var type: Int = 0
    var inventoryType: Int = 0
    var wearableType: Int = 0
    var name: String = ""
    var description: String = ""

    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packInt(buffer, callbackId)
        packUUID(buffer, folderId)
        packUUID(buffer, transactionId)
        packInt(buffer, nextOwnerMask)
        require(type in 0..0xFF) { "type out of range ($type)" }
        packByte(buffer, type)
        require(inventoryType in 0..0xFF) { "inventoryType out of range ($inventoryType)" }
        packByte(buffer, inventoryType)
        require(wearableType in 0..0xFF) { "wearableType out of range ($wearableType)" }
        packByte(buffer, wearableType)
        packVariable(buffer, name.toByteArray(StandardCharsets.UTF_8), 1)
        packVariable(buffer, description.toByteArray(StandardCharsets.UTF_8), 1)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        callbackId = unpackInt(buffer)
        folderId = unpackUUID(buffer)
        transactionId = unpackUUID(buffer)
        nextOwnerMask = unpackInt(buffer)
        type = unpackByte(buffer)
        inventoryType = unpackByte(buffer)
        wearableType = unpackByte(buffer)
        name = String(unpackVariable(buffer, 1), StandardCharsets.UTF_8)
        description = String(unpackVariable(buffer, 1), StandardCharsets.UTF_8)
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.CREATE_INVENTORY_ITEM

    override fun getMessageName(): String = "CreateInventoryItem"
}

/**
 * Create inventory folder message (0xFFFF0111)
 */
class CreateInventoryFolderMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var folderId: UUID = UUID(0L, 0L)
    var parentId: UUID = UUID(0L, 0L)
    var type: Int = 0
    var name: String = ""

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packUUID(buffer, folderId)
        packUUID(buffer, parentId)
        require(type in 0..0xFF) { "type out of range ($type)" }
        packByte(buffer, type)
        packVariable(buffer, name.toByteArray(StandardCharsets.UTF_8), 1)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        folderId = unpackUUID(buffer)
        parentId = unpackUUID(buffer)
        type = unpackByte(buffer)
        name = String(unpackVariable(buffer, 1), StandardCharsets.UTF_8)
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.CREATE_INVENTORY_FOLDER

    override fun getMessageName(): String = "CreateInventoryFolder"
}

/**
 * Create new outfit attachments message (0xFFFF018E)
 */
class CreateNewOutfitAttachmentsMessage : SLMessage() {
    data class ObjectEntry(
        var oldItemId: UUID = UUID(0L, 0L),
        var oldFolderId: UUID = UUID(0L, 0L),
    )

    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var newFolderId: UUID = UUID(0L, 0L)
    val objects: MutableList<ObjectEntry> = mutableListOf()

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packUUID(buffer, newFolderId)
        require(objects.size in 0..0xFF) { "objects size out of range (${objects.size})" }
        packByte(buffer, objects.size)
        objects.forEach { entry ->
            packUUID(buffer, entry.oldItemId)
            packUUID(buffer, entry.oldFolderId)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        newFolderId = unpackUUID(buffer)
        objects.clear()
        repeat(unpackByte(buffer)) {
            objects += ObjectEntry(oldItemId = unpackUUID(buffer), oldFolderId = unpackUUID(buffer))
        }
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.CREATE_NEW_OUTFIT_ATTACHMENTS

    override fun getMessageName(): String = "CreateNewOutfitAttachments"
}

/**
 * Detach attachment into inventory message (0xFFFF018D)
 */
class DetachAttachmentIntoInvMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var itemId: UUID = UUID(0L, 0L)

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, itemId)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        itemId = unpackUUID(buffer)
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.DETACH_ATTACHMENT_INTO_INV

    override fun getMessageName(): String = "DetachAttachmentIntoInv"
}

/**
 * Transfer inventory message (0xFFFF0127)
 */
class TransferInventoryMessage : SLMessage() {
    data class InventoryEntry(
        var inventoryId: UUID = UUID(0L, 0L),
        var type: Int = 0,
    )

    var sourceId: UUID = UUID(0L, 0L)
    var destId: UUID = UUID(0L, 0L)
    var transactionId: UUID = UUID(0L, 0L)
    val inventory: MutableList<InventoryEntry> = mutableListOf()

    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, sourceId)
        packUUID(buffer, destId)
        packUUID(buffer, transactionId)
        require(inventory.size in 0..0xFF) { "inventory size out of range (${inventory.size})" }
        packByte(buffer, inventory.size)
        inventory.forEach { entry ->
            packUUID(buffer, entry.inventoryId)
            require(entry.type in 0..0xFF) { "inventory type out of range (${entry.type})" }
            packByte(buffer, entry.type)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        sourceId = unpackUUID(buffer)
        destId = unpackUUID(buffer)
        transactionId = unpackUUID(buffer)
        inventory.clear()
        repeat(unpackByte(buffer)) {
            inventory += InventoryEntry(inventoryId = unpackUUID(buffer), type = unpackByte(buffer))
        }
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.TRANSFER_INVENTORY

    override fun getMessageName(): String = "TransferInventory"
}

/**
 * Transfer inventory acknowledgement message (0xFFFF0128)
 */
class TransferInventoryAckMessage : SLMessage() {
    var transactionId: UUID = UUID(0L, 0L)
    var inventoryId: UUID = UUID(0L, 0L)

    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, transactionId)
        packUUID(buffer, inventoryId)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        transactionId = unpackUUID(buffer)
        inventoryId = unpackUUID(buffer)
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.TRANSFER_INVENTORY_ACK

    override fun getMessageName(): String = "TransferInventoryAck"
}

/**
 * Add circuit code message (0xFFFF0002)
 */
class AddCircuitCodeMessage : SLMessage() {
    var code: Int = 0
    var sessionId: UUID = UUID(0L, 0L)
    var agentId: UUID = UUID(0L, 0L)

    override fun packPayload(buffer: ByteBuffer) {
        packInt(buffer, code)
        packUUID(buffer, sessionId)
        packUUID(buffer, agentId)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        code = unpackInt(buffer)
        sessionId = unpackUUID(buffer)
        agentId = unpackUUID(buffer)
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.ADD_CIRCUIT_CODE

    override fun getMessageName(): String = "AddCircuitCode"
}

/**
 * Abort transfer message (0xFFFF009D)
 */
class AbortXferMessage : SLMessage() {
    var xferId: Long = 0L
    var result: Int = 0

    override fun packPayload(buffer: ByteBuffer) {
        packLong(buffer, xferId)
        packInt(buffer, result)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        xferId = unpackLong(buffer)
        result = unpackInt(buffer)
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.ABORT_XFER

    override fun getMessageName(): String = "AbortXfer"
}

/**
 * Asset upload request message (0xFFFF014D)
 */
class AssetUploadRequestMessage : SLMessage() {
    var transactionId: UUID = UUID(0L, 0L)
    var assetType: Int = 0
    var tempfile: Boolean = false
    var storeLocal: Boolean = false
    var assetData: ByteArray = ByteArray(0)

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, transactionId)
        require(assetType in 0..0xFF) { "assetType out of range ($assetType)" }
        packByte(buffer, assetType)
        packBoolean(buffer, tempfile)
        packBoolean(buffer, storeLocal)
        packVariable(buffer, assetData, 2)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        transactionId = unpackUUID(buffer)
        assetType = unpackByte(buffer)
        tempfile = unpackBoolean(buffer)
        storeLocal = unpackBoolean(buffer)
        assetData = unpackVariable(buffer, 2)
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.ASSET_UPLOAD_REQUEST

    override fun getMessageName(): String = "AssetUploadRequest"
}

/**
 * Asset upload complete message (0xFFFF014E)
 */
class AssetUploadCompleteMessage : SLMessage() {
    var assetId: UUID = UUID(0L, 0L)
    var assetType: Int = 0
    var success: Boolean = false

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, assetId)
        require(assetType in 0..0xFF) { "assetType out of range ($assetType)" }
        packByte(buffer, assetType)
        packBoolean(buffer, success)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        assetId = unpackUUID(buffer)
        assetType = unpackByte(buffer)
        success = unpackBoolean(buffer)
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.ASSET_UPLOAD_COMPLETE

    override fun getMessageName(): String = "AssetUploadComplete"
}

/**
 * Create trusted circuit message (0xFFFF0188)
 */
class CreateTrustedCircuitMessage : SLMessage() {
    var endPointId: UUID = UUID(0L, 0L)
    var digest: ByteArray = ByteArray(32)

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, endPointId)
        packFixed(buffer, digest, 32)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        endPointId = unpackUUID(buffer)
        digest = unpackFixed(buffer, 32)
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.CREATE_TRUSTED_CIRCUIT

    override fun getMessageName(): String = "CreateTrustedCircuit"
}

/**
 * Deny trusted circuit message (0xFFFF0189)
 */
class DenyTrustedCircuitMessage : SLMessage() {
    var endPointId: UUID = UUID(0L, 0L)

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, endPointId)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        endPointId = unpackUUID(buffer)
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.DENY_TRUSTED_CIRCUIT

    override fun getMessageName(): String = "DenyTrustedCircuit"
}

/**
 * Create landmark for event message (0xFFFF0132)
 */
class CreateLandmarkForEventMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var eventId: Int = 0
    var folderId: UUID = UUID(0L, 0L)
    var name: String = ""

    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packInt(buffer, eventId)
        packUUID(buffer, folderId)
        packVariable(buffer, name.toByteArray(StandardCharsets.UTF_8), 1)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        eventId = unpackInt(buffer)
        folderId = unpackUUID(buffer)
        name = String(unpackVariable(buffer, 1), StandardCharsets.UTF_8)
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.CREATE_LANDMARK_FOR_EVENT

    override fun getMessageName(): String = "CreateLandmarkForEvent"
}

/**
 * Buy object inventory message (0xFFFF0067)
 */
class BuyObjectInventoryMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var objectId: UUID = UUID(0L, 0L)
    var itemId: UUID = UUID(0L, 0L)
    var folderId: UUID = UUID(0L, 0L)

    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packUUID(buffer, objectId)
        packUUID(buffer, itemId)
        packUUID(buffer, folderId)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        objectId = unpackUUID(buffer)
        itemId = unpackUUID(buffer)
        folderId = unpackUUID(buffer)
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.BUY_OBJECT_INVENTORY

    override fun getMessageName(): String = "BuyObjectInventory"
}

/**
 * Fetch inventory descendents message (0xFFFF0115)
 */
class FetchInventoryDescendentsMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var folderId: UUID = UUID(0L, 0L)
    var ownerId: UUID = UUID(0L, 0L)
    var sortOrder: Int = 0
    var fetchFolders: Boolean = false
    var fetchItems: Boolean = false

    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packUUID(buffer, folderId)
        packUUID(buffer, ownerId)
        packInt(buffer, sortOrder)
        packBoolean(buffer, fetchFolders)
        packBoolean(buffer, fetchItems)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        folderId = unpackUUID(buffer)
        ownerId = unpackUUID(buffer)
        sortOrder = unpackInt(buffer)
        fetchFolders = unpackBoolean(buffer)
        fetchItems = unpackBoolean(buffer)
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.FETCH_INVENTORY_DESCENDENTS

    override fun getMessageName(): String = "FetchInventoryDescendents"
}
