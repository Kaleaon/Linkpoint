package com.linkpoint.inventory

import com.linkpoint.Debug
import com.linkpoint.slproto.caps.CAPSManager
import com.linkpoint.slproto.llsd.LLSD
import com.linkpoint.slproto.llsd.LLSDXMLParser
import com.linkpoint.assets.AssetType
import kotlinx.coroutines.*
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * InventoryType - Types of inventory items
 */
enum class InventoryType(val typeId: Int) {
    TEXTURE(0),
    SOUND(1),
    CALLING_CARD(2),
    LANDMARK(3),
    OBJECT(6),
    NOTECARD(7),
    CATEGORY(8),
    ROOT_CATEGORY(9),
    LSL_TEXT(10),
    BODYPART(13),
    ANIMATION(19),
    GESTURE(20),
    MESH(22),
    WIDGET(23),
    PERSON(24)
    
    companion object {
        fun fromId(id: Int) = values().find { it.typeId == id }
    }
}

/**
 * FolderType - Types of inventory folders
 */
enum class FolderType(val typeId: Int, val folderName: String) {
    TEXTURE(-1, "Textures"),
    SOUND(1, "Sounds"),
    CALLING_CARD(2, "Calling Cards"),
    LANDMARK(3, "Landmarks"),
    CLOTHING(5, "Clothing"),
    OBJECT(6, "Objects"),
    NOTECARD(7, "Notecards"),
    ROOT_FOLDER(8, "My Inventory"),
    LSL_TEXT(10, "Scripts"),
    BODYPART(13, "Body Parts"),
    TRASH(14, "Trash"),
    SNAPSHOT(15, "Photo Album"),
    LOST_AND_FOUND(16, "Lost And Found"),
    ANIMATION(20, "Animations"),
    GESTURE(21, "Gestures"),
    FAVORITES(23, "Favorites"),
    CURRENT_OUTFIT(46, "Current Outfit"),
    OUTFIT(47, "Outfits"),
    MY_OUTFITS(48, "My Outfits"),
    MESH(49, "Meshes"),
    INBOX(50, "Received Items"),
    OUTBOX(51, "Merchant Outbox")
    
    companion object {
        fun fromId(id: Int) = values().find { it.typeId == id }
    }
}

/**
 * InventoryItem - Represents a single inventory item
 */
data class InventoryItem(
    val itemID: UUID,
    val parentID: UUID,
    val assetID: UUID,
    val name: String,
    val description: String,
    val type: InventoryType,
    val assetType: AssetType,
    val permissions: ItemPermissions,
    val saleInfo: SaleInfo?,
    val creationDate: Long,
    val flags: Int = 0
)

/**
 * InventoryFolder - Represents an inventory folder
 */
data class InventoryFolder(
    val folderID: UUID,
    val parentID: UUID,
    val name: String,
    val type: FolderType,
    val version: Int = 0
) {
    // Children (managed by InventorySystem)
    internal val childFolders = mutableListOf<UUID>()
    internal val childItems = mutableListOf<UUID>()
}

/**
 * ItemPermissions - Item permission bits
 */
data class ItemPermissions(
    val baseMask: Int,
    val ownerMask: Int,
    val groupMask: Int,
    val everyoneMask: Int,
    val nextOwnerMask: Int,
    val creatorID: UUID,
    val ownerID: UUID,
    val lastOwnerID: UUID,
    val groupID: UUID
) {
    fun canCopy() = (ownerMask and PERM_COPY) != 0
    fun canModify() = (ownerMask and PERM_MODIFY) != 0
    fun canTransfer() = (ownerMask and PERM_TRANSFER) != 0
    fun canMove() = (ownerMask and PERM_MOVE) != 0
    
    companion object {
        const val PERM_NONE = 0
        const val PERM_TRANSFER = 1 shl 13
        const val PERM_MODIFY = 1 shl 14
        const val PERM_COPY = 1 shl 15
        const val PERM_MOVE = 1 shl 19
        const val PERM_ALL = PERM_TRANSFER or PERM_MODIFY or PERM_COPY or PERM_MOVE
    }
}

/**
 * SaleInfo - Item sale information
 */
data class SaleInfo(
    val saleType: Int,  // 0=not for sale, 1=original, 2=copy, 3=contents
    val salePrice: Int
)

/**
 * InventorySystem - Manages Second Life inventory
 * 
 * Features:
 * - Fetch inventory via HTTP CAPS
 * - Hierarchical folder structure
 * - Item search and filtering
 * - Create/delete/move operations
 * - Caching
 * 
 * Based on Firestorm's LLInventoryModel
 */
class InventorySystem(
    private val capsManager: CAPSManager,
    private val agentID: UUID
) {
    
    companion object {
        const val FETCH_TIMEOUT_MS = 60000L
    }
    
    // Inventory storage
    private val folders = ConcurrentHashMap<UUID, InventoryFolder>()
    private val items = ConcurrentHashMap<UUID, InventoryItem>()
    
    // Special folders
    private var rootFolderID: UUID? = null
    private var libraryRootID: UUID? = null
    
    // Loading state
    private var isLoaded = false
    private val loadingDeferred = CompletableDeferred<Boolean>()
    
    /**
     * Fetch inventory from server
     */
    suspend fun fetchInventory() = withContext(Dispatchers.IO) {
        try {
            val fetchURL = capsManager.getCapability("FetchInventoryDescendents2")
                ?: capsManager.getCapability("FetchInventoryDescendents")
                ?: throw Exception("No inventory fetch capability")
            
            // Start with root folder
            val rootFolder = fetchRootFolder()
            if (rootFolder != null) {
                rootFolderID = rootFolder.folderID
                folders[rootFolder.folderID] = rootFolder
                
                // Fetch all descendants
                fetchDescendents(rootFolder.folderID, fetchURL)
            }
            
            isLoaded = true
            loadingDeferred.complete(true)
            Debug.Log("Inventory loaded: ${folders.size} folders, ${items.size} items")
            
        } catch (e: Exception) {
            Debug.Log("Failed to fetch inventory: ${e.message}")
            loadingDeferred.complete(false)
        }
    }
    
    /**
     * Fetch root folder
     */
    private suspend fun fetchRootFolder(): InventoryFolder? {
        // Root folder is provided in login response
        // For now, create a default one
        return InventoryFolder(
            folderID = UUID.randomUUID(),
            parentID = UUID(0, 0),
            name = "My Inventory",
            type = FolderType.ROOT_FOLDER
        )
    }
    
    /**
     * Fetch folder descendants recursively
     */
    private suspend fun fetchDescendents(folderID: UUID, fetchURL: String) {
        try {
            // Build request
            val request = LLSD.emptyMap()
            request["agent_id"] = LLSD(agentID)
            request["folder_id"] = LLSD(folderID)
            request["fetch_folders"] = LLSD(true)
            request["fetch_items"] = LLSD(true)
            request["sort_order"] = LLSD(0)
            
            // Make CAPS request
            val response = capsManager.postLLSD(fetchURL, request)
            
            // Parse response
            if (response.isMap() && response.has("folders")) {
                val foldersArray = response["folders"]
                if (foldersArray.isArray()) {
                    for (i in 0 until foldersArray.size()) {
                        val folderData = foldersArray[i]
                        parseFolder(folderData)
                    }
                }
            }
            
            if (response.isMap() && response.has("items")) {
                val itemsArray = response["items"]
                if (itemsArray.isArray()) {
                    for (i in 0 until itemsArray.size()) {
                        val itemData = itemsArray[i]
                        parseItem(itemData)
                    }
                }
            }
            
            // Recursively fetch child folders
            val folder = folders[folderID]
            if (folder != null) {
                for (childID in folder.childFolders) {
                    fetchDescendents(childID, fetchURL)
                }
            }
            
        } catch (e: Exception) {
            Debug.Log("Failed to fetch descendants for $folderID: ${e.message}")
        }
    }
    
    /**
     * Parse folder from LLSD
     */
    private fun parseFolder(llsd: LLSD) {
        val folderID = llsd["folder_id"].asUUID()
        val parentID = llsd["parent_id"].asUUID()
        val name = llsd["name"].asString()
        val typeId = llsd["type"].asInteger()
        val version = llsd["version"].asInteger()
        
        val folder = InventoryFolder(
            folderID = folderID,
            parentID = parentID,
            name = name,
            type = FolderType.fromId(typeId) ?: FolderType.ROOT_FOLDER,
            version = version
        )
        
        folders[folderID] = folder
        
        // Add to parent's children
        val parent = folders[parentID]
        if (parent != null && !parent.childFolders.contains(folderID)) {
            parent.childFolders.add(folderID)
        }
    }
    
    /**
     * Parse item from LLSD
     */
    private fun parseItem(llsd: LLSD) {
        val itemID = llsd["item_id"].asUUID()
        val parentID = llsd["parent_id"].asUUID()
        val assetID = llsd["asset_id"].asUUID()
        val name = llsd["name"].asString()
        val description = llsd["desc"].asString()
        val typeId = llsd["type"].asInteger()
        val assetTypeId = llsd["asset_type"].asInteger()
        val creationDate = llsd["created_at"].asInteger().toLong()
        val flags = llsd["flags"].asInteger()
        
        // Parse permissions
        val permissions = ItemPermissions(
            baseMask = llsd["permissions"]["base_mask"].asInteger(),
            ownerMask = llsd["permissions"]["owner_mask"].asInteger(),
            groupMask = llsd["permissions"]["group_mask"].asInteger(),
            everyoneMask = llsd["permissions"]["everyone_mask"].asInteger(),
            nextOwnerMask = llsd["permissions"]["next_owner_mask"].asInteger(),
            creatorID = llsd["permissions"]["creator_id"].asUUID(),
            ownerID = llsd["permissions"]["owner_id"].asUUID(),
            lastOwnerID = llsd["permissions"]["last_owner_id"].asUUID(),
            groupID = llsd["permissions"]["group_id"].asUUID()
        )
        
        // Parse sale info
        val saleInfo = if (llsd.has("sale_info")) {
            SaleInfo(
                saleType = llsd["sale_info"]["sale_type"].asInteger(),
                salePrice = llsd["sale_info"]["sale_price"].asInteger()
            )
        } else null
        
        val item = InventoryItem(
            itemID = itemID,
            parentID = parentID,
            assetID = assetID,
            name = name,
            description = description,
            type = InventoryType.fromId(typeId) ?: InventoryType.OBJECT,
            assetType = AssetType.fromId(assetTypeId) ?: AssetType.OBJECT,
            permissions = permissions,
            saleInfo = saleInfo,
            creationDate = creationDate,
            flags = flags
        )
        
        items[itemID] = item
        
        // Add to parent's children
        val parent = folders[parentID]
        if (parent != null && !parent.childItems.contains(itemID)) {
            parent.childItems.add(itemID)
        }
    }
    
    /**
     * Get folder by ID
     */
    fun getFolder(folderID: UUID): InventoryFolder? = folders[folderID]
    
    /**
     * Get item by ID
     */
    fun getItem(itemID: UUID): InventoryItem? = items[itemID]
    
    /**
     * Get root folder
     */
    fun getRootFolder(): InventoryFolder? = rootFolderID?.let { folders[it] }
    
    /**
     * Get children of folder
     */
    fun getFolderChildren(folderID: UUID): List<InventoryFolder> {
        val folder = folders[folderID] ?: return emptyList()
        return folder.childFolders.mapNotNull { folders[it] }
    }
    
    /**
     * Get items in folder
     */
    fun getFolderItems(folderID: UUID): List<InventoryItem> {
        val folder = folders[folderID] ?: return emptyList()
        return folder.childItems.mapNotNull { items[it] }
    }
    
    /**
     * Search items by name
     */
    fun searchItems(query: String): List<InventoryItem> {
        return items.values.filter { it.name.contains(query, ignoreCase = true) }
    }
    
    /**
     * Find folder by type
     */
    fun findFolderByType(type: FolderType): InventoryFolder? {
        return folders.values.find { it.type == type }
    }
    
    /**
     * Wait for inventory to load
     */
    suspend fun awaitLoaded(): Boolean {
        return loadingDeferred.await()
    }
    
    /**
     * Check if inventory is loaded
     */
    fun isLoaded(): Boolean = isLoaded
}
