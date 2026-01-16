package com.linkpoint.inventory

import android.os.Parcelable
import android.util.Log
import com.linkpoint.assets.AssetType
import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.llsd.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.parcelize.Parcelize
import kotlin.coroutines.cancellation.CancellationException
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Manages Second Life inventory
 * Handles fetching, caching, and operations on inventory items
 */
class InventoryManager(
    private val capabilityManager: CapabilityManager,
    private val agentId: UUID
) {
    companion object {
        private const val TAG = "InventoryManager"
        
        // Folder types
        const val FOLDER_TYPE_TEXTURE = 0
        const val FOLDER_TYPE_SOUND = 1
        const val FOLDER_TYPE_CALLINGCARD = 2
        const val FOLDER_TYPE_LANDMARK = 3
        const val FOLDER_TYPE_CLOTHING = 5
        const val FOLDER_TYPE_OBJECT = 6
        const val FOLDER_TYPE_NOTECARD = 7
        const val FOLDER_TYPE_CATEGORY = 8
        const val FOLDER_TYPE_ROOT = 9
        const val FOLDER_TYPE_SCRIPT = 10
        const val FOLDER_TYPE_BODYPART = 13
        const val FOLDER_TYPE_TRASH = 14
        const val FOLDER_TYPE_SNAPSHOT = 15
        const val FOLDER_TYPE_LOSTFOUND = 16
        const val FOLDER_TYPE_ANIMATION = 20
        const val FOLDER_TYPE_GESTURE = 21
        const val FOLDER_TYPE_FAVORITES = 23
        const val FOLDER_TYPE_MESH = 49
        const val FOLDER_TYPE_OUTBOX = 52
        const val FOLDER_TYPE_OUTFIT = 54
        const val FOLDER_TYPE_MYOUTFITS = 55
        const val FOLDER_TYPE_SETTINGS = 56
        const val FOLDER_TYPE_MATERIAL = 57
    }
    
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    // Cached inventory
    private val folders = ConcurrentHashMap<UUID, InventoryFolder>()
    private val items = ConcurrentHashMap<UUID, InventoryItem>()
    
    // Special folders
    private var rootFolderId: UUID? = null
    private val systemFolders = ConcurrentHashMap<Int, UUID>()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _currentFolder = MutableStateFlow<UUID?>(null)
    val currentFolder: StateFlow<UUID?> = _currentFolder
    
    /**
     * Set root folder ID
     */
    fun setRootFolder(folderId: UUID) {
        rootFolderId = folderId
        systemFolders[FOLDER_TYPE_ROOT] = folderId
    }
    
    /**
     * Register system folder
     */
    fun registerSystemFolder(type: Int, folderId: UUID) {
        systemFolders[type] = folderId
    }
    
    /**
     * Get system folder
     */
    fun getSystemFolder(type: Int): UUID? = systemFolders[type]
    
    /**
     * Exception thrown when a fetch operation should be retried.
     */
    private class RetryableException(message: String) : Exception(message)
    
    /**
     * Fetch folder contents with retry support.
     * 
     * The CapabilityManager now handles retries internally with Firestorm-style
     * exponential backoff and Retry-After header support. This method provides
     * additional retry logic for cases where the capability itself returns null.
     */
    suspend fun fetchFolderContents(folderId: UUID, fetchFolders: Boolean = true, fetchItems: Boolean = true): Boolean {
        _isLoading.value = true
        
        return withContext(Dispatchers.IO) {
            var attempts = 0
            val maxAttempts = 3
            
            try {
                while (attempts < maxAttempts) {
                    try {
                        val request = LLSDMap().apply {
                            this["folders"] = LLSDArray().apply {
                                add(LLSDMap().apply {
                                    this["folder_id"] = LLSDString(folderId.toString())
                                    this["owner_id"] = LLSDString(agentId.toString())
                                    this["fetch_folders"] = LLSDBoolean(fetchFolders)
                                    this["fetch_items"] = LLSDBoolean(fetchItems)
                                    this["sort_order"] = LLSDInteger(1)
                                })
                            }
                        }
                        
                        val response = capabilityManager.request(
                            CapabilityManager.CAP_FETCH_INVENTORY_DESCENDENTS,
                            request
                        )
                        
                        if (response is LLSDMap) {
                            parseInventoryResponse(response)
                            return@withContext true
                        } else {
                            // Null response - throw to trigger retry logic
                            throw RetryableException("Empty response for folder $folderId")
                        }
                    } catch (e: CancellationException) {
                        // Re-throw CancellationException to not interfere with coroutine cancellation
                        throw e
                    } catch (e: RetryableException) {
                        // Handle retryable errors in one place
                        attempts++
                        if (attempts < maxAttempts) {
                            Log.w(TAG, "${e.message}, retrying (attempt $attempts)")
                            delay(1000L * attempts)
                        }
                    } catch (e: Exception) {
                        // Other exceptions are also retryable
                        Log.e(TAG, "Failed to fetch folder: $folderId", e)
                        attempts++
                        if (attempts < maxAttempts) {
                            delay(1000L * attempts)
                        }
                    }
                }
                false
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Fetch specific items
     */
    suspend fun fetchItems(itemIds: List<UUID>): List<InventoryItem> {
        if (itemIds.isEmpty()) return emptyList()
        
        return withContext(Dispatchers.IO) {
            try {
                val request = LLSDMap().apply {
                    this["items"] = LLSDArray().apply {
                        for (itemId in itemIds) {
                            add(LLSDMap().apply {
                                this["item_id"] = LLSDString(itemId.toString())
                                this["owner_id"] = LLSDString(agentId.toString())
                            })
                        }
                    }
                }
                
                val response = capabilityManager.request(
                    CapabilityManager.CAP_FETCH_INVENTORY,
                    request
                )
                
                val result = mutableListOf<InventoryItem>()
                if (response is LLSDMap) {
                    val items = response.getArray("items")
                    items?.value?.forEach { item ->
                        if (item is LLSDMap) {
                            val parsed = parseItem(item)
                            if (parsed != null) {
                                result.add(parsed)
                            }
                        }
                    }
                }
                result
            } catch (e: Exception) {
                Log.e(TAG, "Failed to fetch items", e)
                emptyList()
            }
        }
    }
    
    private fun parseInventoryResponse(response: LLSDMap) {
        // Parse folders
        val foldersArray = response.getArray("folders")
        foldersArray?.value?.forEach { folder ->
            if (folder is LLSDMap) {
                val categories = folder.getArray("categories")
                categories?.value?.forEach { cat ->
                    if (cat is LLSDMap) {
                        parseFolder(cat)?.let { folders[it.folderId] = it }
                    }
                }
                
                val items = folder.getArray("items")
                items?.value?.forEach { item ->
                    if (item is LLSDMap) {
                        parseItem(item)?.let { this.items[it.itemId] = it }
                    }
                }
            }
        }
    }
    
    private fun parseFolder(data: LLSDMap): InventoryFolder? {
        return try {
            InventoryFolder(
                folderId = UUID.fromString(data.getString("category_id") ?: data.getString("folder_id") ?: return null),
                parentId = UUID.fromString(data.getString("parent_id") ?: "00000000-0000-0000-0000-000000000000"),
                name = data.getString("name") ?: "Unknown",
                type = data.getInt("type_default") ?: -1,
                version = data.getInt("version") ?: 0
            )
        } catch (e: Exception) {
            null
        }
    }
    
    private fun parseItem(data: LLSDMap): InventoryItem? {
        return try {
            val permissions = data.getMap("permissions")
            
            InventoryItem(
                itemId = UUID.fromString(data.getString("item_id") ?: return null),
                assetId = UUID.fromString(data.getString("asset_id") ?: "00000000-0000-0000-0000-000000000000"),
                parentId = UUID.fromString(data.getString("parent_id") ?: "00000000-0000-0000-0000-000000000000"),
                name = data.getString("name") ?: "Unknown",
                description = data.getString("desc") ?: "",
                assetType = data.getInt("type") ?: 0,
                inventoryType = data.getInt("inv_type") ?: 0,
                flags = data.getInt("flags") ?: 0,
                permissions = ItemPermissions(
                    baseMask = permissions?.getInt("base_mask") ?: 0,
                    ownerMask = permissions?.getInt("owner_mask") ?: 0,
                    groupMask = permissions?.getInt("group_mask") ?: 0,
                    everyoneMask = permissions?.getInt("everyone_mask") ?: 0,
                    nextOwnerMask = permissions?.getInt("next_owner_mask") ?: 0,
                    ownerId = UUID.fromString(permissions?.getString("owner_id") ?: agentId.toString()),
                    creatorId = UUID.fromString(permissions?.getString("creator_id") ?: agentId.toString())
                ),
                saleInfo = SaleInfo(
                    saleType = data.getMap("sale_info")?.getInt("sale_type") ?: 0,
                    salePrice = data.getMap("sale_info")?.getInt("sale_price") ?: 0
                ),
                creationDate = data.getInt("created_at") ?: 0
            )
        } catch (e: Exception) {
            Log.w(TAG, "Failed to parse item", e)
            null
        }
    }
    
    /**
     * Get folder contents (cached)
     */
    fun getFolderContents(folderId: UUID): List<InventoryNode> {
        val result = mutableListOf<InventoryNode>()
        
        // Add subfolders
        folders.values.filter { it.parentId == folderId }.forEach {
            result.add(InventoryNode.Folder(it))
        }
        
        // Add items
        items.values.filter { it.parentId == folderId }.forEach {
            result.add(InventoryNode.Item(it))
        }
        
        return result.sortedWith(compareBy({ it !is InventoryNode.Folder }, { it.name }))
    }
    
    /**
     * Get item by ID (cached)
     */
    fun getItem(itemId: UUID): InventoryItem? = items[itemId]
    
    /**
     * Get folder by ID (cached)
     */
    fun getFolder(folderId: UUID): InventoryFolder? = folders[folderId]
    
    /**
     * Get all subfolders of a folder (cached)
     */
    fun getFolders(parentId: UUID): List<InventoryFolder> {
        return folders.values.filter { it.parentId == parentId }
            .sortedBy { it.name }
    }
    
    /**
     * Get all items in a folder (cached)
     */
    fun getItems(parentId: UUID): List<InventoryItem> {
        return items.values.filter { it.parentId == parentId }
            .sortedBy { it.name }
    }
    
    /**
     * Move item to folder
     */
    suspend fun moveItem(itemId: UUID, newParentId: UUID): Boolean {
        items[itemId]?.let { item ->
            items[itemId] = item.copy(parentId = newParentId)
            // TODO: Send to server
            return true
        }
        return false
    }
    
    /**
     * Move folder
     */
    suspend fun moveFolder(folderId: UUID, newParentId: UUID): Boolean {
        folders[folderId]?.let { folder ->
            folders[folderId] = folder.copy(parentId = newParentId)
            // TODO: Send to server
            return true
        }
        return false
    }
    
    /**
     * Create folder
     */
    suspend fun createFolder(parentId: UUID, name: String, type: Int = -1): UUID? {
        val folderId = UUID.randomUUID()
        val folder = InventoryFolder(
            folderId = folderId,
            parentId = parentId,
            name = name,
            type = type,
            version = 0
        )
        folders[folderId] = folder
        // TODO: Send to server
        return folderId
    }
    
    /**
     * Delete item (move to trash)
     */
    suspend fun deleteItem(itemId: UUID): Boolean {
        val trashFolder = systemFolders[FOLDER_TYPE_TRASH] ?: return false
        return moveItem(itemId, trashFolder)
    }
    
    /**
     * Delete folder (move to trash)
     */
    suspend fun deleteFolder(folderId: UUID): Boolean {
        val trashFolder = systemFolders[FOLDER_TYPE_TRASH] ?: return false
        return moveFolder(folderId, trashFolder)
    }
    
    /**
     * Rename item
     */
    suspend fun renameItem(itemId: UUID, newName: String): Boolean {
        items[itemId]?.let { item ->
            items[itemId] = item.copy(name = newName)
            // TODO: Send to server
            return true
        }
        return false
    }
    
    /**
     * Update item description
     */
    suspend fun updateItemDescription(itemId: UUID, description: String): Boolean {
        items[itemId]?.let { item ->
            items[itemId] = item.copy(description = description)
            // TODO: Send to server
            return true
        }
        return false
    }
    
    /**
     * Copy item
     */
    suspend fun copyItem(itemId: UUID, destinationId: UUID, newName: String? = null): UUID? {
        val source = items[itemId] ?: return null
        
        val newItemId = UUID.randomUUID()
        val copy = source.copy(
            itemId = newItemId,
            parentId = destinationId,
            name = newName ?: source.name
        )
        items[newItemId] = copy
        // TODO: Send to server
        return newItemId
    }
    
    /**
     * Search inventory
     */
    fun search(query: String): List<InventoryNode> {
        val lowerQuery = query.lowercase()
        val results = mutableListOf<InventoryNode>()
        
        items.values.filter { it.name.lowercase().contains(lowerQuery) }
            .forEach { results.add(InventoryNode.Item(it)) }
        
        folders.values.filter { it.name.lowercase().contains(lowerQuery) }
            .forEach { results.add(InventoryNode.Folder(it)) }
        
        return results.sortedBy { it.name }
    }
    
    /**
     * Navigate to folder
     */
    fun navigateTo(folderId: UUID) {
        _currentFolder.value = folderId
        scope.launch {
            fetchFolderContents(folderId)
        }
    }
    
    fun shutdown() {
        scope.cancel()
    }
    
    // ==================== DIAGNOSTIC METHODS ====================
    
    /**
     * Get the total count of cached folders
     */
    fun getFolderCount(): Int = folders.size
    
    /**
     * Get the total count of cached items
     */
    fun getItemCount(): Int = items.size
    
    /**
     * Get comprehensive diagnostic data for debug reports
     */
    fun getDiagnostics(): InventoryDiagnostics {
        return InventoryDiagnostics(
            folderCount = folders.size,
            itemCount = items.size,
            rootFolderId = rootFolderId,
            systemFolderCount = systemFolders.size,
            isLoading = _isLoading.value,
            currentFolderId = _currentFolder.value
        )
    }
    
    /**
     * Diagnostic data class for inventory manager state
     */
    data class InventoryDiagnostics(
        val folderCount: Int,
        val itemCount: Int,
        val rootFolderId: UUID?,
        val systemFolderCount: Int,
        val isLoading: Boolean,
        val currentFolderId: UUID?
    )
}

@Parcelize
data class InventoryFolder(
    val folderId: UUID,
    val parentId: UUID,
    val name: String,
    val type: Int,
    val version: Int
) : Parcelable

@Parcelize
data class InventoryItem(
    val itemId: UUID,
    val assetId: UUID,
    val parentId: UUID,
    val name: String,
    val description: String,
    val assetType: Int,
    val inventoryType: Int,
    val flags: Int,
    val permissions: ItemPermissions,
    val saleInfo: SaleInfo,
    val creationDate: Int
) : Parcelable {
    val assetTypeEnum: AssetType
        get() = AssetType.fromValue(assetType)
}

@Parcelize
data class ItemPermissions(
    val baseMask: Int,
    val ownerMask: Int,
    val groupMask: Int,
    val everyoneMask: Int,
    val nextOwnerMask: Int,
    val ownerId: UUID,
    val creatorId: UUID
) : Parcelable {
    companion object {
        const val PERM_TRANSFER = 0x00002000
        const val PERM_MODIFY = 0x00004000
        const val PERM_COPY = 0x00008000
        const val PERM_MOVE = 0x00080000
    }
    
    val canTransfer: Boolean get() = (ownerMask and PERM_TRANSFER) != 0
    val canModify: Boolean get() = (ownerMask and PERM_MODIFY) != 0
    val canCopy: Boolean get() = (ownerMask and PERM_COPY) != 0
}

@Parcelize
data class SaleInfo(
    val saleType: Int,
    val salePrice: Int
) : Parcelable

sealed class InventoryNode {
    abstract val name: String
    abstract val id: UUID
    
    data class Folder(val folder: InventoryFolder) : InventoryNode() {
        override val name: String get() = folder.name
        override val id: UUID get() = folder.folderId
    }
    
    data class Item(val item: InventoryItem) : InventoryNode() {
        override val name: String get() = item.name
        override val id: UUID get() = item.itemId
    }
}
