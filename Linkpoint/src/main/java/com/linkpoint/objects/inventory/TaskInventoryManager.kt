package com.linkpoint.objects.inventory

import android.util.Log
import com.linkpoint.protocol.messages.MessageIds
import com.linkpoint.protocol.messages.UDPConnectionFixed
import com.linkpoint.protocol.transfer.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Task Inventory Manager - Handles object inventory (contents).
 * 
 * Based on the reference viewer's SLTaskInventory.java
 * 
 * Task inventory is the inventory inside objects (prims).
 * It includes scripts, notecards, textures, and other items
 * that can be placed inside objects.
 * 
 * Flow:
 * 1. Send RequestTaskInventory message
 * 2. Server sends ReplyTaskInventory with Xfer filename
 * 3. Receive task inventory data via Xfer protocol
 * 4. Parse inventory listing
 */
class TaskInventoryManager(
    private val udpConnection: UDPConnectionFixed,
    private val xferManager: XferManager,
    private val agentId: UUID
) {
    companion object {
        private const val TAG = "TaskInventoryManager"

        // Task inventory markers
        const val INVENTORY_HEADER = "inv_object"
        const val ITEM_HEADER = "inv_item"
    }
    
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    // Cache of task inventories by object local ID
    private val inventoryCache = ConcurrentHashMap<Int, TaskInventory>()
    
    // Pending inventory requests
    private val pendingRequests = ConcurrentHashMap<Int, MutableList<TaskInventoryCallback>>()
    
    // Mapping of xfer filenames to object local IDs
    private val xferToObject = ConcurrentHashMap<String, Int>()
    
    init {
        // Register xfer handler for task inventory files
        // Task inventory files have format like "task_inv_<uuid>" or similar
        // We'll only process files we're tracking in xferToObject
        xferManager.registerHandler("task_inv") { filename, result ->
            handleXferComplete(filename, result)
        }
    }
    
    /**
     * Request task inventory for an object.
     */
    fun requestTaskInventory(
        localId: Int,
        objectId: UUID,
        callback: TaskInventoryCallback? = null
    ) {
        // Check cache
        inventoryCache[localId]?.let { cached ->
            callback?.onInventoryLoaded(cached)
            return
        }
        
        // Add to pending callbacks
        val callbacks = pendingRequests.getOrPut(localId) { mutableListOf() }
        callback?.let { callbacks.add(it) }
        
        if (callbacks.size == 1) {
            scope.launch {
                sendRequestTaskInventory(localId, objectId)
            }
        }
    }
    
    /**
     * Handle ReplyTaskInventory message.
     */
    fun handleReplyTaskInventory(payload: ByteArray) {
        try {
            val buffer = ByteBuffer.wrap(payload).order(ByteOrder.LITTLE_ENDIAN)
            
            val localId = buffer.int
            
            // Read filename (variable length)
            val filenameLen = buffer.get().toInt() and 0xFF
            val filenameBytes = ByteArray(filenameLen)
            buffer.get(filenameBytes)
            val filename = String(filenameBytes, Charsets.UTF_8).trim('\u0000')
            
            Log.d(TAG, "ReplyTaskInventory: localId=$localId filename=$filename")
            
            if (filename.isNotEmpty()) {
                xferToObject[filename] = localId
            } else {
                // Empty filename means object has no inventory
                val inventory = TaskInventory(localId, emptyList())
                inventoryCache[localId] = inventory
                notifyCallbacks(localId, inventory, null)
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing ReplyTaskInventory", e)
        }
    }
    
    /**
     * Handle Xfer completion for task inventory.
     */
    private fun handleXferComplete(filename: String, result: XferResult) {
        val localId = xferToObject.remove(filename) ?: return
        
        when (result) {
            is XferResult.Success -> {
                try {
                    val inventory = parseTaskInventory(localId, result.data)
                    inventoryCache[localId] = inventory
                    notifyCallbacks(localId, inventory, null)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to parse task inventory for object $localId", e)
                    notifyCallbacks(localId, null, "Parse error: ${e.message}")
                }
            }
            is XferResult.Error -> {
                Log.e(TAG, "Failed to load task inventory for object $localId: ${result.message}")
                notifyCallbacks(localId, null, result.message)
            }
        }
    }
    
    /**
     * Send RequestTaskInventory message.
     */
    private suspend fun sendRequestTaskInventory(localId: Int, objectId: UUID) {
        val payload = ByteBuffer.allocate(20).order(ByteOrder.LITTLE_ENDIAN)
        
        // AgentData block
        writeUUID(payload, agentId)
        
        // InventoryData block
        payload.putInt(localId)
        
        try {
            udpConnection.sendPacket(MessageIds.REQUEST_TASK_INVENTORY, payload.array(), reliable = true)
            Log.d(TAG, "Sent RequestTaskInventory for object $localId")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send RequestTaskInventory", e)
            notifyCallbacks(localId, null, "Failed to send request: ${e.message}")
        }
    }
    
    /**
     * Parse task inventory data.
     */
    private fun parseTaskInventory(localId: Int, data: ByteArray): TaskInventory {
        val content = String(data, Charsets.UTF_8)
        val lines = content.lines()
        
        Log.d(TAG, "Parsing task inventory: ${data.size} bytes, ${lines.size} lines")
        
        val items = mutableListOf<TaskInventoryItem>()
        
        var i = 0
        while (i < lines.size) {
            val line = lines[i].trim()
            
            when {
                line.startsWith(ITEM_HEADER) -> {
                    val itemResult = parseTaskInventoryItem(lines, i)
                    items.add(itemResult.first)
                    i = itemResult.second
                }
                else -> i++
            }
        }
        
        return TaskInventory(localId, items)
    }
    
    /**
     * Parse a single task inventory item.
     */
    private fun parseTaskInventoryItem(lines: List<String>, startIndex: Int): Pair<TaskInventoryItem, Int> {
        var i = startIndex
        var braceDepth = 0
        
        var itemId: UUID? = null
        var parentId: UUID? = null
        var assetId: UUID? = null
        var assetType: Int = 0
        var invType: Int = 0
        var name = ""
        var description = ""
        var creatorId: UUID? = null
        var ownerId: UUID? = null
        var baseMask: Int = 0
        var ownerMask: Int = 0
        var groupMask: Int = 0
        var everyoneMask: Int = 0
        var nextOwnerMask: Int = 0
        var flags: Int = 0
        
        while (i < lines.size) {
            val line = lines[i].trim()
            
            when {
                line == "{" -> braceDepth++
                line == "}" -> {
                    braceDepth--
                    if (braceDepth == 0) {
                        i++
                        break
                    }
                }
                line.startsWith("item_id") -> {
                    itemId = parseUUID(line.substringAfter("item_id").trim())
                }
                line.startsWith("parent_id") -> {
                    parentId = parseUUID(line.substringAfter("parent_id").trim())
                }
                line.startsWith("asset_id") -> {
                    assetId = parseUUID(line.substringAfter("asset_id").trim())
                }
                line.startsWith("type") && !line.startsWith("type\t") -> {
                    assetType = line.substringAfter("type").trim().toIntOrNull() ?: 0
                }
                line.startsWith("inv_type") -> {
                    invType = line.substringAfter("inv_type").trim().toIntOrNull() ?: 0
                }
                line.startsWith("name") -> {
                    name = line.substringAfter("name").trim().removeSurrounding("|")
                }
                line.startsWith("desc") -> {
                    description = line.substringAfter("desc").trim().removeSurrounding("|")
                }
                line.startsWith("creator_id") -> {
                    creatorId = parseUUID(line.substringAfter("creator_id").trim())
                }
                line.startsWith("owner_id") -> {
                    ownerId = parseUUID(line.substringAfter("owner_id").trim())
                }
                line.startsWith("base_mask") -> {
                    baseMask = parseHexInt(line.substringAfter("base_mask").trim())
                }
                line.startsWith("owner_mask") -> {
                    ownerMask = parseHexInt(line.substringAfter("owner_mask").trim())
                }
                line.startsWith("group_mask") -> {
                    groupMask = parseHexInt(line.substringAfter("group_mask").trim())
                }
                line.startsWith("everyone_mask") -> {
                    everyoneMask = parseHexInt(line.substringAfter("everyone_mask").trim())
                }
                line.startsWith("next_owner_mask") -> {
                    nextOwnerMask = parseHexInt(line.substringAfter("next_owner_mask").trim())
                }
                line.startsWith("flags") -> {
                    flags = parseHexInt(line.substringAfter("flags").trim())
                }
            }
            i++
        }
        
        return Pair(
            TaskInventoryItem(
                itemId = itemId ?: UUID(0, 0),
                parentId = parentId ?: UUID(0, 0),
                assetId = assetId ?: UUID(0, 0),
                assetType = AssetType.fromCode(assetType) ?: AssetType.OBJECT,
                inventoryType = InventoryType.fromCode(invType) ?: InventoryType.OBJECT,
                name = name,
                description = description,
                creatorId = creatorId ?: UUID(0, 0),
                ownerId = ownerId ?: UUID(0, 0),
                permissions = ItemPermissions(
                    baseMask = baseMask,
                    ownerMask = ownerMask,
                    groupMask = groupMask,
                    everyoneMask = everyoneMask,
                    nextOwnerMask = nextOwnerMask
                ),
                flags = flags
            ),
            i
        )
    }
    
    private fun parseUUID(str: String): UUID? {
        return try {
            UUID.fromString(str.trim())
        } catch (e: Exception) {
            null
        }
    }
    
    private fun parseHexInt(str: String): Int {
        return try {
            str.toLong(16).toInt()
        } catch (e: Exception) {
            0
        }
    }
    
    private fun writeUUID(buffer: ByteBuffer, uuid: UUID) {
        buffer.putLong(uuid.mostSignificantBits)
        buffer.putLong(uuid.leastSignificantBits)
    }
    
    private fun notifyCallbacks(localId: Int, inventory: TaskInventory?, error: String?) {
        val callbacks = pendingRequests.remove(localId) ?: return
        
        if (inventory != null) {
            callbacks.forEach { it.onInventoryLoaded(inventory) }
        } else if (error != null) {
            callbacks.forEach { it.onInventoryError(error) }
        }
    }
    
    /**
     * Clear inventory cache for an object.
     */
    fun clearCache(localId: Int) {
        inventoryCache.remove(localId)
    }
    
    /**
     * Clear all inventory cache.
     */
    fun clearAllCache() {
        inventoryCache.clear()
    }
    
    /**
     * Get cached inventory.
     */
    fun getCachedInventory(localId: Int): TaskInventory? = inventoryCache[localId]
    
    /**
     * Shutdown the manager.
     */
    fun shutdown() {
        scope.cancel()
        inventoryCache.clear()
        pendingRequests.clear()
        xferToObject.clear()
    }
}

/**
 * Represents the inventory of an object (task).
 */
data class TaskInventory(
    val localId: Int,
    val items: List<TaskInventoryItem>
) {
    val scripts: List<TaskInventoryItem>
        get() = items.filter { it.assetType == AssetType.LSL_TEXT || it.assetType == AssetType.LSL_BYTECODE }
    
    val notecards: List<TaskInventoryItem>
        get() = items.filter { it.assetType == AssetType.NOTECARD }
    
    val textures: List<TaskInventoryItem>
        get() = items.filter { it.assetType == AssetType.TEXTURE }
    
    val sounds: List<TaskInventoryItem>
        get() = items.filter { it.assetType == AssetType.SOUND }
    
    val animations: List<TaskInventoryItem>
        get() = items.filter { it.assetType == AssetType.ANIMATION }
    
    val objects: List<TaskInventoryItem>
        get() = items.filter { it.assetType == AssetType.OBJECT }
}

/**
 * Represents an item in task inventory.
 */
data class TaskInventoryItem(
    val itemId: UUID,
    val parentId: UUID,
    val assetId: UUID,
    val assetType: AssetType,
    val inventoryType: InventoryType,
    val name: String,
    val description: String,
    val creatorId: UUID,
    val ownerId: UUID,
    val permissions: ItemPermissions,
    val flags: Int
) {
    val isScript: Boolean
        get() = assetType == AssetType.LSL_TEXT || assetType == AssetType.LSL_BYTECODE
    
    val isNotecard: Boolean
        get() = assetType == AssetType.NOTECARD
}

/**
 * Inventory type enum.
 */
enum class InventoryType(val code: Int) {
    TEXTURE(0),
    SOUND(1),
    CALLING_CARD(2),
    LANDMARK(3),
    SCRIPT(4),  // Deprecated
    CLOTHING(5),
    OBJECT(6),
    NOTECARD(7),
    CATEGORY(8),
    ROOT_CATEGORY(9),
    LSL(10),
    SNAPSHOT(15),
    ATTACHMENT(17),
    WEARABLE(18),
    ANIMATION(19),
    GESTURE(20),
    MESH(22),
    SETTINGS(25),
    MATERIAL(57);
    
    companion object {
        fun fromCode(code: Int): InventoryType? = values().find { it.code == code }
    }
}

/**
 * Item permissions.
 */
data class ItemPermissions(
    val baseMask: Int,
    val ownerMask: Int,
    val groupMask: Int,
    val everyoneMask: Int,
    val nextOwnerMask: Int
) {
    companion object {
        const val PERM_TRANSFER = 0x00002000
        const val PERM_MODIFY = 0x00004000
        const val PERM_COPY = 0x00008000
        const val PERM_MOVE = 0x00080000
        const val PERM_ALL = 0x7FFFFFFF
    }
    
    val canTransfer: Boolean get() = (ownerMask and PERM_TRANSFER) != 0
    val canModify: Boolean get() = (ownerMask and PERM_MODIFY) != 0
    val canCopy: Boolean get() = (ownerMask and PERM_COPY) != 0
}

/**
 * Callback for task inventory loading.
 */
interface TaskInventoryCallback {
    fun onInventoryLoaded(inventory: TaskInventory)
    fun onInventoryError(error: String)
}
