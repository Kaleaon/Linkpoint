package com.linkpoint.inventory

import android.os.Parcelable
import android.util.Log
import com.linkpoint.assets.AssetType
import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.core.AgentIdentity
import com.linkpoint.protocol.messages.ids.MessageIdRegistry
import com.linkpoint.protocol.messages.UDPConnectionFixed
import com.linkpoint.protocol.types.getUUID
import com.linkpoint.protocol.types.putUUID
import com.linkpoint.protocol.llsd.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.parcelize.Parcelize
import kotlin.coroutines.cancellation.CancellationException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Manages Second Life inventory
 * Handles fetching, caching, and operations on inventory items
 */
class InventoryManager(
    private val capabilityManager: CapabilityManager,
    private val udpConnection: UDPConnectionFixed,
    private val agentId: UUID
) {
    companion object {
        private const val TAG = "InventoryManager"
        
        // Null UUID constant
        private val NULL_UUID = UUID(0L, 0L)
        
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
        // SL protocol folder types that other Linkpoint constants gloss over but that
        // actually appear in inventory-skeleton responses. Needed for warm-fetch.
        const val FOLDER_TYPE_CURRENT_OUTFIT = 46
        const val FOLDER_TYPE_INBOX = 50
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

    private fun outboundIdentity(): AgentIdentity = AgentIdentity(
        agentId = agentId,
        sessionId = udpConnection.getSessionId(),
        circuitCode = udpConnection.getCircuitCode()
    ).requireValid("InventoryManager outbound packet")
    
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
     * Add folder from login response inventory-skeleton.
     * This caches the folder structure immediately on login.
     */
    fun addFolderFromLogin(folderId: UUID, parentId: UUID, name: String, typeDefault: Int, version: Int) {
        val folder = InventoryFolder(
            folderId = folderId,
            parentId = parentId,
            name = name,
            type = typeDefault,
            version = version
        )
        folders[folderId] = folder
        
        // Also register as system folder if it has a type
        if (typeDefault >= 0) {
            systemFolders[typeDefault] = folderId
        }
    }
    
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
     * Remove an item from inventory (called when server notifies us of removal)
     */
    fun removeItem(itemId: UUID) {
        items.remove(itemId)
        Log.d(TAG, "Removed item from cache: $itemId")
    }
    
    /**
     * Remove a folder from inventory (called when server notifies us of removal)
     */
    fun removeFolder(folderId: UUID) {
        folders.remove(folderId)
        systemFolders.entries.removeIf { it.value == folderId }
        Log.d(TAG, "Removed folder from cache: $folderId")
    }
    
    /**
     * Move item to folder.
     * Uses AISv3 capability when available, falls back to UDP MoveInventoryItem.
     */
    suspend fun moveItem(itemId: UUID, newParentId: UUID): Boolean {
        items[itemId]?.let { item ->
            // Try to use AISv3 capability
            val aisUrl = capabilityManager.getCapability(CapabilityManager.CAP_INVENTORY_API)
            if (aisUrl != null) {
                try {
                    val request = LLSDMap().apply {
                        this["items"] = LLSDArray().apply {
                            add(LLSDMap().apply {
                                this["item_id"] = LLSDString(itemId.toString())
                                this["parent_id"] = LLSDString(newParentId.toString())
                            })
                        }
                    }
                    val response = capabilityManager.request(CapabilityManager.CAP_INVENTORY_API, request)
                    if (response != null) {
                        Log.d(TAG, "Moved item $itemId to folder $newParentId via AIS")
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to move item via AIS: ${e.message}")
                }
            } else {
                // UDP Fallback for grids without AISv3
                try {
                    // MoveInventoryItem packet
                    // AgentData block: AgentID (16) + SessionID (16) + Stamp (4) = 36 bytes
                    // InventoryData block count: 1 byte
                    // InventoryData block: ItemID (16) + FolderID (16) + NewName (1 byte length + 0 bytes) = 33 bytes
                    // Total payload: 70 bytes

                    val payload = ByteBuffer.allocate(70).order(ByteOrder.LITTLE_ENDIAN)
                    val identity = outboundIdentity()

                    // AgentData
                    payload.putUUID(identity.agentId)
                    payload.putUUID(identity.sessionId)
                    payload.putInt(0) // Stamp = 0 (false/default)

                    // InventoryData count (Variable block)
                    payload.put(1.toByte())

                    // InventoryData
                    payload.putUUID(itemId)
                    payload.putUUID(newParentId)
                    payload.put(0.toByte()) // NewName length = 0 (empty string to keep name)

                    udpConnection.sendPacket(MessageIdRegistry.MOVE_INVENTORY_ITEM, payload.array(), reliable = true)
                    Log.d(TAG, "Moved item $itemId to folder $newParentId via UDP")

                } catch (e: Exception) {
                    Log.w(TAG, "Failed to move item via UDP: ${e.message}")
                    // Don't return false here, we still update local cache optimistically
                }
            }
            
            // Update local cache
            items[itemId] = item.copy(parentId = newParentId)
            return true
        }
        return false
    }
    
    /**
     * Move folder to new parent.
     * Uses AISv3 capability when available.
     */
    suspend fun moveFolder(folderId: UUID, newParentId: UUID): Boolean {
        folders[folderId]?.let { folder ->
            // Try to use AISv3 capability
            val aisUrl = capabilityManager.getCapability(CapabilityManager.CAP_INVENTORY_API)
            if (aisUrl != null) {
                try {
                    val request = LLSDMap().apply {
                        this["categories"] = LLSDArray().apply {
                            add(LLSDMap().apply {
                                this["category_id"] = LLSDString(folderId.toString())
                                this["parent_id"] = LLSDString(newParentId.toString())
                            })
                        }
                    }
                    capabilityManager.request(CapabilityManager.CAP_INVENTORY_API, request)
                    Log.d(TAG, "Moved folder $folderId to $newParentId via AIS")
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to move folder via AIS: ${e.message}")
                }
            }
            
            // Update local cache
            folders[folderId] = folder.copy(parentId = newParentId)
            return true
        }
        return false
    }
    
    /**
     * Create folder using CreateInventoryCategory capability.
     */
    suspend fun createFolder(parentId: UUID, name: String, type: Int = -1): UUID? {
        return withContext(Dispatchers.IO) {
            try {
                val folderId = UUID.randomUUID()
                
                // Try to use CreateInventoryCategory capability
                val capUrl = capabilityManager.getCapability(CapabilityManager.CAP_CREATE_INVENTORY_CATEGORY)
                if (capUrl != null) {
                    val request = LLSDMap().apply {
                        this["folder_id"] = LLSDString(folderId.toString())
                        this["parent_id"] = LLSDString(parentId.toString())
                        this["name"] = LLSDString(name)
                        this["type"] = LLSDInteger(type)
                    }
                    
                    val response = capabilityManager.request(
                        CapabilityManager.CAP_CREATE_INVENTORY_CATEGORY, 
                        request
                    )
                    
                    if (response is LLSDMap) {
                        val createdId = response.getString("folder_id")
                        if (createdId != null) {
                            val newFolderId = UUID.fromString(createdId)
                            val folder = InventoryFolder(
                                folderId = newFolderId,
                                parentId = parentId,
                                name = name,
                                type = type,
                                version = 0
                            )
                            folders[newFolderId] = folder
                            Log.d(TAG, "Created folder '$name' with ID $newFolderId")
                            return@withContext newFolderId
                        }
                    }
                }
                
                // Fallback: create locally and send UDP packet if available
                val folder = InventoryFolder(
                    folderId = folderId,
                    parentId = parentId,
                    name = name,
                    type = type,
                    version = 0
                )
                folders[folderId] = folder
                
                // Send CreateInventoryFolder message to server via UDP
                try {
                    val nameBytes = name.toByteArray(Charsets.UTF_8)
                    // Limit name length to 255 bytes for Variable 1 encoding
                    val truncatedNameBytes = if (nameBytes.size > 255) nameBytes.copyOf(255) else nameBytes

                    // Payload size: AgentID(16) + SessionID(16) + FolderID(16) + ParentID(16) + Type(1) + NameLength(1) + NameBytes(N)
                    val fixedSize = 16 + 16 + 16 + 16 + 1 + 1  // 66 bytes
                    val payloadSize = fixedSize + truncatedNameBytes.size
                    val payload = ByteBuffer.allocate(payloadSize).order(ByteOrder.LITTLE_ENDIAN)

                    // AgentData block
                    val identity = outboundIdentity()
                    payload.putUUID(identity.agentId)
                    payload.putUUID(identity.sessionId)

                    // FolderData block
                    payload.putUUID(folderId)
                    payload.putUUID(parentId)
                    payload.put(type.toByte())

                    // Variable 1 field: 1-byte length + data
                    payload.put(truncatedNameBytes.size.toByte())
                    payload.put(truncatedNameBytes)

                    udpConnection.sendPacket(MessageIdRegistry.CREATE_INVENTORY_FOLDER, payload.array(), reliable = true)
                    Log.d(TAG, "Created folder '$name' locally and sent CreateInventoryFolder packet")
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to send CreateInventoryFolder packet: ${e.message}")
                    Log.d(TAG, "Created folder '$name' locally only")
                }
                
                folderId
                
            } catch (e: Exception) {
                Log.e(TAG, "Failed to create folder", e)
                null
            }
        }
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
     * Rename item using UpdateInventoryItem capability or UDP fallback.
     */
    suspend fun renameItem(itemId: UUID, newName: String): Boolean {
        val item = items[itemId] ?: return false
        var success = false
        
        // Try to use UpdateInventoryItem capability
        val capUrl = capabilityManager.getCapability(CapabilityManager.CAP_UPDATE_INVENTORY_ITEM)
        if (capUrl != null) {
            try {
                val request = LLSDMap().apply {
                    this["item_id"] = LLSDString(itemId.toString())
                    this["name"] = LLSDString(newName)
                }
                capabilityManager.request(CapabilityManager.CAP_UPDATE_INVENTORY_ITEM, request)
                Log.d(TAG, "Renamed item $itemId to '$newName' via capability")
                success = true
            } catch (e: Exception) {
                Log.w(TAG, "Failed to rename item via capability: ${e.message}")
            }
        }
        
        // Fallback to UDP UpdateInventoryItem if capability not available or failed
        if (!success) {
            try {
                sendUpdateInventoryItemPacket(item.copy(name = newName))
                Log.d(TAG, "Renamed item $itemId to '$newName' via UDP")
                success = true
            } catch (e: Exception) {
                Log.e(TAG, "Failed to rename item via UDP: ${e.message}")
            }
        }
        
        // Update local cache
        items[itemId] = item.copy(name = newName)
        return success
    }
    
    /**
     * Update item description using UpdateInventoryItem capability or UDP fallback.
     */
    suspend fun updateItemDescription(itemId: UUID, description: String): Boolean {
        val item = items[itemId] ?: return false
        var success = false
        
        // Try to use UpdateInventoryItem capability
        val capUrl = capabilityManager.getCapability(CapabilityManager.CAP_UPDATE_INVENTORY_ITEM)
        if (capUrl != null) {
            try {
                val request = LLSDMap().apply {
                    this["item_id"] = LLSDString(itemId.toString())
                    this["desc"] = LLSDString(description)
                }
                capabilityManager.request(CapabilityManager.CAP_UPDATE_INVENTORY_ITEM, request)
                Log.d(TAG, "Updated description for item $itemId")
                success = true
            } catch (e: Exception) {
                Log.w(TAG, "Failed to update item description: ${e.message}")
            }
        }
        
        // Fallback to UDP UpdateInventoryItem if capability not available or failed
        if (!success) {
            try {
                sendUpdateInventoryItemPacket(item.copy(description = description))
                Log.d(TAG, "Updated item description $itemId via UDP")
                success = true
            } catch (e: Exception) {
                Log.e(TAG, "Failed to update item description via UDP: ${e.message}")
            }
        }
        
        // Update local cache
        items[itemId] = item.copy(description = description)
        return success
    }
    
    /**
     * Helper to send UpdateInventoryItem packet via UDP.
     */
    private suspend fun sendUpdateInventoryItemPacket(item: InventoryItem) {
        // Calculate size first
        val nameBytes = item.name.toByteArray(Charsets.UTF_8)
        val descBytes = item.description.toByteArray(Charsets.UTF_8)

        // Limit name and desc to 255 bytes (1 byte length)
        val safeNameBytes = if (nameBytes.size > 255) nameBytes.copyOf(255) else nameBytes
        val safeDescBytes = if (descBytes.size > 255) descBytes.copyOf(255) else descBytes

        // Size = AgentData (16+16+16) + InventoryData Count (1) + InventoryData Block
        // InventoryData Block:
        // UUIDs (16*6) + U32 (4*6) + Bool (1) + UUID (16) + S8 (1) + S8 (1) + U32 (1) + U8 (1) + S32 (1) + Var (1+N) + Var (1+N) + S32 (1) + U32 (1)
        // Fixed part per item: 96 + 24 + 1 + 16 + 1 + 1 + 4 + 1 + 4 + 4 + 4 = 156 bytes
        // Variable part: 1 + nameLen + 1 + descLen
        val agentDataSize = 16 + 16 + 16  // 48 bytes
        val inventoryDataCount = 1
        val inventoryDataFixed = 16*6 + 4*6 + 1 + 16 + 1 + 1 + 4 + 1 + 4 + 4 + 4  // 156 bytes
        val inventoryDataVariable = 1 + safeNameBytes.size + 1 + safeDescBytes.size
        val payloadSize = agentDataSize + inventoryDataCount + inventoryDataFixed + inventoryDataVariable

        val buffer = ByteBuffer.allocate(payloadSize).order(ByteOrder.LITTLE_ENDIAN)

        // AgentData Block
        val identity = outboundIdentity()
        buffer.putUUID(identity.agentId)
        buffer.putUUID(identity.sessionId)
        buffer.putUUID(UUID.randomUUID()) // TransactionID

        // InventoryData Block Count
        buffer.put(1.toByte())

        // InventoryData Block Content
        buffer.putUUID(item.itemId)
        buffer.putUUID(item.parentId)
        buffer.putInt(0) // CallbackID
        buffer.putUUID(item.permissions.creatorId)
        buffer.putUUID(item.permissions.ownerId)
        buffer.putUUID(NULL_UUID) // GroupID - assuming not group owned or we don't know
        buffer.putInt(item.permissions.baseMask)
        buffer.putInt(item.permissions.ownerMask)
        buffer.putInt(item.permissions.groupMask)
        buffer.putInt(item.permissions.everyoneMask)
        buffer.putInt(item.permissions.nextOwnerMask)
        buffer.put(0.toByte()) // GroupOwned - simplified
        buffer.putUUID(NULL_UUID) // TransactionID
        buffer.put(item.assetType.toByte())
        buffer.put(item.inventoryType.toByte())
        buffer.putInt(item.flags)
        buffer.put(item.saleInfo.saleType.toByte())
        buffer.putInt(item.saleInfo.salePrice)

        // Name (Variable 1)
        buffer.put(safeNameBytes.size.toByte())
        buffer.put(safeNameBytes)

        // Description (Variable 1)
        buffer.put(safeDescBytes.size.toByte())
        buffer.put(safeDescBytes)

        buffer.putInt(item.creationDate)
        buffer.putInt(0) // CRC

        udpConnection.sendPacket(MessageIdRegistry.UPDATE_INVENTORY_ITEM, buffer.array(), reliable = true)
    }
    
    /**
     * Copy item.
     * Note: Copy operations typically use CopyInventoryItem UDP message
     * as there's no direct CAPS endpoint for copying.
     */
    suspend fun copyItem(itemId: UUID, destinationId: UUID, newName: String? = null): UUID? {
        val source = items[itemId] ?: return null
        
        // Check permissions
        if ((source.permissions.ownerMask and 0x00008000) == 0) {
            Log.w(TAG, "Cannot copy item $itemId - no copy permission")
            return null
        }
        
        val newItemId = UUID.randomUUID()
        val actualNewName = newName ?: source.name
        val copy = source.copy(
            itemId = newItemId,
            parentId = destinationId,
            name = actualNewName
        )
        items[newItemId] = copy
        
        // Send to server
        sendCopyInventoryItem(
            oldAgentId = source.permissions.ownerId,
            oldItemId = itemId,
            newFolderId = destinationId,
            newName = actualNewName
        )

        Log.d(TAG, "Copied item $itemId to $newItemId")
        return newItemId
    }

    /**
     * Send CopyInventoryItem packet
     */
    private suspend fun sendCopyInventoryItem(oldAgentId: UUID, oldItemId: UUID, newFolderId: UUID, newName: String) {
        val nameBytes = newName.toByteArray(Charsets.UTF_8)
        val nameLength = nameBytes.size.coerceAtMost(255)

        // Packet layout:
        // AgentData block:
        //  AgentID: UUID (16)
        //  SessionID: UUID (16)
        // InventoryData block (Variable count):
        //  Block Count (1 byte)
        //  CallbackID: U32 (4)
        //  OldAgentID: UUID (16)
        //  OldItemID: UUID (16)
        //  NewFolderID: UUID (16)
        //  NewName: Variable 1 (1 + nameLength)

        val payloadSize = 32 + 1 + (4 + 16 + 16 + 16 + 1 + nameLength)
        val payload = ByteBuffer.allocate(payloadSize).order(ByteOrder.LITTLE_ENDIAN)

        // AgentData
        val identity = outboundIdentity()
        payload.putUUID(identity.agentId)
        payload.putUUID(identity.sessionId)

        // InventoryData Block Count
        payload.put(1.toByte())

        // InventoryData fields
        payload.putInt(0) // CallbackID (0 for no callback)
        payload.putUUID(oldAgentId)
        payload.putUUID(oldItemId)
        payload.putUUID(newFolderId)

        // NewName (Variable 1)
        payload.put(nameLength.toByte())
        payload.put(nameBytes, 0, nameLength)

        udpConnection.sendPacket(MessageIdRegistry.COPY_INVENTORY_ITEM, payload.array(), reliable = true)
    }
    
    /**
     * Resolve an inventory link to its target item.
     * 
     * Inventory links (asset type 24 for items, 25 for folders) reference
     * other items/folders. This method resolves the link to the actual item.
     * 
     * @param item The item to resolve (may or may not be a link)
     * @return The resolved item, or the original item if not a link
     */
    fun resolveLink(item: InventoryItem): InventoryItem? {
        // Asset type 24 = LINK (item link), 25 = LINK_FOLDER (folder link)
        if (item.assetType != 24 && item.assetType != 25) {
            return item
        }
        
        // The assetId of a link points to the target item/folder
        val targetId = item.assetId
        return items[targetId]
    }
    
    /**
     * Check if an item is a link.
     */
    fun isLink(item: InventoryItem): Boolean {
        return item.assetType == 24 || item.assetType == 25
    }
    
    /**
     * Get the original item from a potential link chain.
     * Follows links recursively (up to 10 levels to prevent infinite loops).
     */
    fun resolveFullLinkChain(item: InventoryItem, maxDepth: Int = 10): InventoryItem? {
        var current = item
        var depth = 0
        
        while (isLink(current) && depth < maxDepth) {
            val resolved = items[current.assetId] ?: return null
            current = resolved
            depth++
        }
        
        return if (isLink(current)) null else current
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
    
    /**
     * Get all landmarks from the cached inventory.
     * Returns items of type LANDMARK (asset type 3).
     */
    fun getLandmarks(): List<InventoryItem> {
        return items.values.filter { it.assetType == 3 }
            .sortedByDescending { it.creationDate }
    }
    
    /**
     * Fetch landmarks folder and return landmark items.
     * Fetches the system landmarks folder if not already cached.
     */
    suspend fun fetchLandmarks(): List<InventoryItem> {
        val landmarkFolder = systemFolders[FOLDER_TYPE_LANDMARK]
        if (landmarkFolder != null) {
            fetchFolderContents(landmarkFolder, fetchFolders = false, fetchItems = true)
        }
        return getLandmarks()
    }

    /**
     * Warm-fetch a curated set of high-value system folders in parallel.
     *
     * The inventory-skeleton from login only gives us the folder tree; items
     * are populated lazily when a folder is opened. For a just-connected
     * session that leaves every folder at 0 items - including Current Outfit,
     * which determines what the avatar is wearing. This pulls the small set
     * of folders a viewer actually needs before the UI is touched, so the
     * user sees landmarks/favorites/outfits immediately and appearance
     * resolution has the data it needs.
     *
     * Launched on the internal IO scope; returns immediately. Per-folder
     * failures are logged but never propagate - the UI can still lazy-fetch
     * anything that was missed.
     */
    fun warmFetch() {
        val priorityTypes = listOf(
            FOLDER_TYPE_CURRENT_OUTFIT,
            FOLDER_TYPE_FAVORITES,
            FOLDER_TYPE_LANDMARK,
            FOLDER_TYPE_MYOUTFITS,
            FOLDER_TYPE_INBOX
        )
        val toFetch = priorityTypes.mapNotNull { type ->
            systemFolders[type]?.let { type to it }
        }
        if (toFetch.isEmpty()) {
            Log.i(TAG, "Warm-fetch skipped: no priority system folders registered yet")
            return
        }

        scope.launch {
            Log.i(TAG, "Warm-fetch starting for ${toFetch.size} folders: ${toFetch.map { it.first }}")
            toFetch.map { (type, folderId) ->
                async {
                    try {
                        val ok = fetchFolderContents(folderId, fetchFolders = false, fetchItems = true)
                        Log.i(TAG, "Warm-fetch folder type=$type id=$folderId ok=$ok")
                    } catch (e: Exception) {
                        Log.w(TAG, "Warm-fetch folder type=$type id=$folderId failed: ${e.message}")
                    }
                }
            }.awaitAll()
            Log.i(TAG, "Warm-fetch complete: items cached = ${items.size}")
        }
    }
    
    fun shutdown() {
        scope.cancel()
    }
    
    // ==================== UDP MESSAGE HANDLERS ====================
    
    /**
     * Handle InventoryAssetResponse message - asset data for an inventory item.
     */
    fun handleAssetResponse(payload: ByteArray) {
        try {
            val buffer = java.nio.ByteBuffer.wrap(payload).order(java.nio.ByteOrder.LITTLE_ENDIAN)
            
            // QueryData block
            val transactionId = buffer.getUUID()
            val assetType = buffer.int
            val success = buffer.get() != 0.toByte()
            
            Log.d(TAG, "📦 InventoryAssetResponse: type=$assetType, success=$success")
            
            // Note: Asset data follows if success
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing InventoryAssetResponse", e)
        }
    }
    
    /**
     * Handle UpdateInventoryFolder message - folder was updated.
     */
    fun handleFolderUpdate(payload: ByteArray) {
        try {
            val buffer = java.nio.ByteBuffer.wrap(payload).order(java.nio.ByteOrder.LITTLE_ENDIAN)
            
            // AgentData block
            buffer.position(buffer.position() + 32) // Skip AgentID and SessionID
            
            // FolderData block count
            if (buffer.remaining() < 1) return
            val folderCount = buffer.get().toInt() and 0xFF
            
            for (i in 0 until folderCount) {
                if (buffer.remaining() < 34) break
                
                val folderId = buffer.getUUID()
                val parentId = buffer.getUUID()
                val type = buffer.get().toInt()
                val nameLen = buffer.get().toInt() and 0xFF
                val nameBytes = ByteArray(nameLen)
                if (buffer.remaining() >= nameLen) {
                    buffer.get(nameBytes)
                }
                val name = String(nameBytes, Charsets.UTF_8).trimEnd('\u0000')
                
                Log.d(TAG, "📁 Folder updated: $folderId - $name (type=$type)")
                
                // Update cached folder
                val existing = folders[folderId]
                if (existing != null) {
                    folders[folderId] = existing.copy(name = name, parentId = parentId)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing UpdateInventoryFolder", e)
        }
    }
    
    /**
     * Handle MoveInventoryFolder message - folder was moved.
     */
    fun handleFolderMove(payload: ByteArray) {
        try {
            val buffer = java.nio.ByteBuffer.wrap(payload).order(java.nio.ByteOrder.LITTLE_ENDIAN)
            
            // AgentData block
            buffer.position(buffer.position() + 48) // Skip AgentID, SessionID, Stamp
            
            // InventoryData block count
            if (buffer.remaining() < 1) return
            val moveCount = buffer.get().toInt() and 0xFF
            
            for (i in 0 until moveCount) {
                if (buffer.remaining() < 32) break
                
                val folderId = buffer.getUUID()
                val newParentId = buffer.getUUID()
                
                Log.d(TAG, "📁 Folder moved: $folderId -> $newParentId")
                
                // Update cached folder
                val existing = folders[folderId]
                if (existing != null) {
                    folders[folderId] = existing.copy(parentId = newParentId)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing MoveInventoryFolder", e)
        }
    }
    
    /**
     * Handle CreateInventoryItem message - item was created.
     */
    fun handleItemCreated(payload: ByteArray) {
        try {
            val buffer = java.nio.ByteBuffer.wrap(payload).order(java.nio.ByteOrder.LITTLE_ENDIAN)
            
            // AgentData block
            buffer.position(buffer.position() + 32) // Skip AgentID and SimApproved
            
            // InventoryData block
            if (buffer.remaining() < 100) return // Item data is ~100+ bytes
            
            val itemId = buffer.getUUID()
            val folderId = buffer.getUUID()
            
            Log.d(TAG, "📦 Item created: $itemId in folder $folderId")
            
            // Note: Full parsing would extract all item properties
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing CreateInventoryItem", e)
        }
    }
    
    /**
     * Handle SaveAssetIntoInventory message - asset was saved.
     */
    fun handleAssetSaved(payload: ByteArray) {
        try {
            val buffer = java.nio.ByteBuffer.wrap(payload).order(java.nio.ByteOrder.LITTLE_ENDIAN)
            
            // AgentData
            buffer.position(buffer.position() + 16) // Skip AgentID
            
            // InventoryData
            val itemId = buffer.getUUID()
            val newAssetId = buffer.getUUID()
            
            Log.d(TAG, "📦 Asset saved to inventory: item=$itemId, asset=$newAssetId")
            
            // Update cached item with new asset ID
            val existing = items[itemId]
            if (existing != null) {
                items[itemId] = existing.copy(assetId = newAssetId)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing SaveAssetIntoInventory", e)
        }
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
