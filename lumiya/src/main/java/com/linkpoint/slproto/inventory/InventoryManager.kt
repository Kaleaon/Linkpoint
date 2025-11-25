package com.linkpoint.slproto.inventory

import com.linkpoint.slproto.caps.CapsManager
import com.linkpoint.slproto.llsd.*
import kotlinx.coroutines.*
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Inventory management system
 */
class InventoryManager(private val capsManager: CapsManager) {
    private val inventoryCache = ConcurrentHashMap<UUID, InventoryItem>()
    private val folderCache = ConcurrentHashMap<UUID, InventoryFolder>()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    /**
     * Fetch inventory from server
     */
    suspend fun fetchInventory(folderId: UUID): List<InventoryItem> =
        withContext(Dispatchers.IO) {
            val fetchUrl =
                capsManager.getCapability(CapsManager.CAP_FETCH_INVENTORY_DESCENDENTS)
                    ?: throw Exception("FetchInventoryDescendents capability not available")

            val request = LLSDMap()
            request["folder_id"] = LLSDUUID(folderId)
            request["owner_id"] = LLSDUUID(UUID.randomUUID()) // Should be agent ID
            request["sort_order"] = LLSDInteger(0)
            request["fetch_folders"] = LLSDBoolean(true)
            request["fetch_items"] = LLSDBoolean(true)

            val response = capsManager.postLLSD(fetchUrl, request)

            val items = mutableListOf<InventoryItem>()

            if (response is LLSDMap) {
                val itemsArray = response["items"]
                if (itemsArray is LLSDArray) {
                    for (itemData in itemsArray.value) {
                        if (itemData is LLSDMap) {
                            val item = parseInventoryItem(itemData)
                            items.add(item)
                            inventoryCache[item.itemId] = item
                        }
                    }
                }

                val foldersArray = response["folders"]
                if (foldersArray is LLSDArray) {
                    for (folderData in foldersArray.value) {
                        if (folderData is LLSDMap) {
                            val folder = parseInventoryFolder(folderData)
                            folderCache[folder.folderId] = folder
                        }
                    }
                }
            }

            items
        }

    /**
     * Get inventory item from cache
     */
    fun getItem(itemId: UUID): InventoryItem? {
        return inventoryCache[itemId]
    }

    /**
     * Get inventory folder from cache
     */
    fun getFolder(folderId: UUID): InventoryFolder? {
        return folderCache[folderId]
    }

    /**
     * Parse inventory item from LLSD
     */
    private fun parseInventoryItem(data: LLSDMap): InventoryItem {
        return InventoryItem(
            itemId = UUID.fromString((data["item_id"] as? LLSDString)?.value ?: UUID.randomUUID().toString()),
            parentId = UUID.fromString((data["parent_id"] as? LLSDString)?.value ?: UUID.randomUUID().toString()),
            name = (data["name"] as? LLSDString)?.value ?: "Unknown",
            description = (data["desc"] as? LLSDString)?.value ?: "",
            assetId = UUID.fromString((data["asset_id"] as? LLSDString)?.value ?: UUID.randomUUID().toString()),
            type = (data["type"] as? LLSDInteger)?.value?.toInt() ?: 0,
            invType = (data["inv_type"] as? LLSDInteger)?.value?.toInt() ?: 0,
            flags = (data["flags"] as? LLSDInteger)?.value?.toInt() ?: 0,
            creatorId = UUID.fromString((data["creator_id"] as? LLSDString)?.value ?: UUID.randomUUID().toString()),
            ownerId = UUID.fromString((data["owner_id"] as? LLSDString)?.value ?: UUID.randomUUID().toString()),
            groupId = UUID.fromString((data["group_id"] as? LLSDString)?.value ?: UUID.randomUUID().toString()),
            groupOwned = (data["group_owned"] as? LLSDBoolean)?.value ?: false,
            salePrice = (data["sale_price"] as? LLSDInteger)?.value?.toInt() ?: 0,
            saleType = (data["sale_type"] as? LLSDInteger)?.value?.toInt() ?: 0,
            permissions = parsePermissions(data["permissions"] as? LLSDMap),
            creationDate = (data["creation_date"] as? LLSDInteger)?.value?.toInt() ?: 0,
        )
    }

    /**
     * Parse inventory folder from LLSD
     */
    private fun parseInventoryFolder(data: LLSDMap): InventoryFolder {
        return InventoryFolder(
            folderId = UUID.fromString((data["folder_id"] as? LLSDString)?.value ?: UUID.randomUUID().toString()),
            parentId = UUID.fromString((data["parent_id"] as? LLSDString)?.value ?: UUID.randomUUID().toString()),
            name = (data["name"] as? LLSDString)?.value ?: "Unknown",
            type = (data["type"] as? LLSDInteger)?.value?.toInt() ?: -1,
            preferredType = (data["preferred_type"] as? LLSDInteger)?.value?.toInt() ?: -1,
        )
    }

    /**
     * Parse permissions from LLSD
     */
    private fun parsePermissions(data: LLSDMap?): InventoryPermissions {
        if (data == null) return InventoryPermissions()

        return InventoryPermissions(
            baseMask = (data["base_mask"] as? LLSDInteger)?.value?.toInt() ?: 0,
            ownerMask = (data["owner_mask"] as? LLSDInteger)?.value?.toInt() ?: 0,
            groupMask = (data["group_mask"] as? LLSDInteger)?.value?.toInt() ?: 0,
            everyoneMask = (data["everyone_mask"] as? LLSDInteger)?.value?.toInt() ?: 0,
            nextOwnerMask = (data["next_owner_mask"] as? LLSDInteger)?.value?.toInt() ?: 0,
            isOwnerGroup = (data["is_owner_group"] as? LLSDBoolean)?.value ?: false,
        )
    }

    /**
     * Create a new inventory folder
     */
    suspend fun createFolder(
        parentId: UUID,
        name: String,
        type: Int
    ): InventoryFolder? =
        withContext(Dispatchers.IO) {
            try {
                val createUrl = 
                    capsManager.getCapability(CapsManager.CAP_CREATE_INVENTORY_FOLDER)
                        ?: throw Exception("CreateInventoryFolder capability not available")

                val request = LLSDMap()
                request["parent_id"] = LLSDUUID(parentId)
                request["name"] = LLSDString(name)
                request["type"] = LLSDInteger(type)

                val response = capsManager.postLLSD(createUrl, request)

                if (response is LLSDMap) {
                    val folderId = response["folder_id"]
                    if (folderId is LLSDUUID) {
                        val folder = InventoryFolder(
                            folderId = folderId.value,
                            parentId = parentId,
                            name = name,
                            type = type,
                            preferredType = -1
                        )
                        folderCache[folderId.value] = folder
                        folder
                    } else {
                        throw Exception("Invalid folder_id in response")
                    }
                } else {
                    throw Exception("Invalid response from CreateInventoryFolder")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

    /**
     * Move inventory item
     */
    suspend fun moveItem(
        itemId: UUID,
        newParentId: UUID
    ): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val moveUrl = 
                    capsManager.getCapability(CapsManager.CAP_MOVE_INVENTORY_ITEM)
                        ?: throw Exception("MoveInventoryItem capability not available")

                val request = LLSDMap()
                request["item_id"] = LLSDUUID(itemId)
                request["new_parent_id"] = LLSDUUID(newParentId)

                val response = capsManager.postLLSD(moveUrl, request)

                if (response is LLSDMap) {
                    val success = response["success"]
                    if (success is LLSDBoolean && success.value) {
                        // Update cached item
                        inventoryCache[itemId]?.let { item ->
                            val updatedItem = item.copy(parentId = newParentId)
                            inventoryCache[itemId] = updatedItem
                        }
                        true
                    } else {
                        val error = response["error"]
                        if (error is LLSDString) {
                            throw Exception("Move failed: ${error.value}")
                        } else {
                            throw Exception("Move operation failed")
                        }
                    }
                } else {
                    throw Exception("Invalid response from MoveInventoryItem")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

    /**
     * Delete inventory item
     */
    suspend fun deleteItem(itemId: UUID): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val deleteUrl = 
                    capsManager.getCapability(CapsManager.CAP_DELETE_INVENTORY_ITEM)
                        ?: throw Exception("DeleteInventoryItem capability not available")

                val request = LLSDMap()
                request["item_id"] = LLSDUUID(itemId)

                val response = capsManager.postLLSD(deleteUrl, request)

                if (response is LLSDMap) {
                    val success = response["success"]
                    if (success is LLSDBoolean && success.value) {
                        // Remove from cache only after successful server deletion
                        inventoryCache.remove(itemId)
                        true
                    } else {
                        val error = response["error"]
                        if (error is LLSDString) {
                            throw Exception("Delete failed: ${error.value}")
                        } else {
                            throw Exception("Delete operation failed")
                        }
                    }
                } else {
                    throw Exception("Invalid response from DeleteInventoryItem")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

    /**
     * Cleanup
     */
    fun cleanup() {
        scope.cancel()
    }
}

/**
 * Inventory item data class
 */
data class InventoryItem(
    val itemId: UUID,
    val parentId: UUID,
    val name: String,
    val description: String,
    val assetId: UUID,
    val type: Int,
    val invType: Int,
    val flags: Int,
    val creatorId: UUID,
    val ownerId: UUID,
    val groupId: UUID,
    val groupOwned: Boolean,
    val salePrice: Int,
    val saleType: Int,
    val permissions: InventoryPermissions,
    val creationDate: Int,
)

/**
 * Inventory folder data class
 */
data class InventoryFolder(
    val folderId: UUID,
    val parentId: UUID,
    val name: String,
    val type: Int,
    val preferredType: Int,
)

/**
 * Inventory permissions
 */
data class InventoryPermissions(
    val baseMask: Int = 0,
    val ownerMask: Int = 0,
    val groupMask: Int = 0,
    val everyoneMask: Int = 0,
    val nextOwnerMask: Int = 0,
    val isOwnerGroup: Boolean = false,
) {
    companion object {
        const val PERM_TRANSFER = 0x00002000
        const val PERM_MODIFY = 0x00004000
        const val PERM_COPY = 0x00008000
        const val PERM_MOVE = 0x00080000
        const val PERM_ALL = 0x7FFFFFFF
    }

    fun canTransfer(): Boolean = (ownerMask and PERM_TRANSFER) != 0

    fun canModify(): Boolean = (ownerMask and PERM_MODIFY) != 0

    fun canCopy(): Boolean = (ownerMask and PERM_COPY) != 0

    fun canMove(): Boolean = (ownerMask and PERM_MOVE) != 0
}

/**
 * Inventory type constants
 */
object InventoryType {
    const val TEXTURE = 0
    const val SOUND = 1
    const val CALLINGCARD = 2
    const val LANDMARK = 3
    const val SCRIPT = 4
    const val CLOTHING = 5
    const val OBJECT = 6
    const val NOTECARD = 7
    const val CATEGORY = 8
    const val ROOT_CATEGORY = 9
    const val LSL_TEXT = 10
    const val LSL_BYTECODE = 11
    const val TEXTURE_TGA = 12
    const val BODYPART = 13
    const val TRASH = 14
    const val SNAPSHOT = 15
    const val LOST_AND_FOUND = 16
    const val ATTACHMENT = 17
    const val WEARABLE = 18
    const val ANIMATION = 19
    const val GESTURE = 20
    const val MESH = 22
    const val SETTINGS = 25
    const val MATERIAL = 57
}
