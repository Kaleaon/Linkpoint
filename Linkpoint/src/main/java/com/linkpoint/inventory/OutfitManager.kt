package com.linkpoint.inventory

import android.util.Log
import com.linkpoint.avatar.AvatarBaker
import com.linkpoint.avatar.WearableData
import com.linkpoint.avatar.WearableType
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Manages outfits and wearables
 * Handles wearing, removing, and outfit management
 */
class OutfitManager(
    private val inventoryManager: InventoryManager,
    private val baker: AvatarBaker,
    private val gestureManager: GestureManager
) {
    companion object {
        private const val TAG = "OutfitManager"
        
        // Attachment points
        const val ATTACH_CHEST = 1
        const val ATTACH_SKULL = 2
        const val ATTACH_LEFT_SHOULDER = 3
        const val ATTACH_RIGHT_SHOULDER = 4
        const val ATTACH_LEFT_HAND = 5
        const val ATTACH_RIGHT_HAND = 6
        const val ATTACH_LEFT_FOOT = 7
        const val ATTACH_RIGHT_FOOT = 8
        const val ATTACH_SPINE = 9
        const val ATTACH_PELVIS = 10
        const val ATTACH_MOUTH = 11
        const val ATTACH_CHIN = 12
        const val ATTACH_LEFT_EAR = 13
        const val ATTACH_RIGHT_EAR = 14
        const val ATTACH_LEFT_EYE = 15
        const val ATTACH_RIGHT_EYE = 16
        const val ATTACH_NOSE = 17
        const val ATTACH_RIGHT_UPPER_ARM = 18
        const val ATTACH_RIGHT_FOREARM = 19
        const val ATTACH_LEFT_UPPER_ARM = 20
        const val ATTACH_LEFT_FOREARM = 21
        const val ATTACH_RIGHT_HIP = 22
        const val ATTACH_RIGHT_UPPER_LEG = 23
        const val ATTACH_RIGHT_LOWER_LEG = 24
        const val ATTACH_LEFT_HIP = 25
        const val ATTACH_LEFT_UPPER_LEG = 26
        const val ATTACH_LEFT_LOWER_LEG = 27
        const val ATTACH_STOMACH = 28
        const val ATTACH_LEFT_PEC = 29
        const val ATTACH_RIGHT_PEC = 30
        const val ATTACH_CENTER_2 = 31
        const val ATTACH_TOP_RIGHT = 32
        const val ATTACH_TOP_CENTER = 33
        const val ATTACH_TOP_LEFT = 34
        const val ATTACH_CENTER = 35
        const val ATTACH_BOTTOM_LEFT = 36
        const val ATTACH_BOTTOM = 37
        const val ATTACH_BOTTOM_RIGHT = 38
        const val ATTACH_NECK = 39
        const val ATTACH_AVATAR_CENTER = 40
        const val ATTACH_LEFT_HAND_RING = 41
        const val ATTACH_RIGHT_HAND_RING = 42
        const val ATTACH_TAIL_BASE = 43
        const val ATTACH_TAIL_TIP = 44
        const val ATTACH_LEFT_WING = 45
        const val ATTACH_RIGHT_WING = 46
        const val ATTACH_JAW = 47
        const val ATTACH_ALT_LEFT_EAR = 48
        const val ATTACH_ALT_RIGHT_EAR = 49
        const val ATTACH_ALT_LEFT_EYE = 50
        const val ATTACH_ALT_RIGHT_EYE = 51
        const val ATTACH_TONGUE = 52
        const val ATTACH_GROIN = 53
        const val ATTACH_HIND_LEFT_FOOT = 54
        const val ATTACH_HIND_RIGHT_FOOT = 55
    }
    
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    // Currently worn items
    private val wornWearables = ConcurrentHashMap<WearableType, UUID>()
    private val wornAttachments = ConcurrentHashMap<Int, UUID>()
    
    private val _isChangingOutfit = MutableStateFlow(false)
    val isChangingOutfit: StateFlow<Boolean> = _isChangingOutfit
    
    private val _currentOutfit = MutableStateFlow<List<UUID>>(emptyList())
    val currentOutfit: StateFlow<List<UUID>> = _currentOutfit
    
    /**
     * Wear an item
     */
    suspend fun wearItem(
        itemId: UUID,
        replace: Boolean = true,
        attachPoint: Int? = null
    ): Boolean = withContext(Dispatchers.Default) {
        val item = inventoryManager.getItem(itemId) ?: return@withContext false
        
        when (item.inventoryType) {
            InventoryType.WEARABLE -> wearWearable(item, replace)
            InventoryType.OBJECT -> {
                if (attachPoint != null) {
                    attachObject(item, attachPoint, replace)
                } else {
                    // Use default attach point from item flags
                    val defaultPoint = (item.flags and 0xFF)
                    attachObject(item, defaultPoint, replace)
                }
            }
            InventoryType.GESTURE -> activateGesture(item)
            else -> false
        }
    }
    
    private suspend fun wearWearable(item: InventoryItem, replace: Boolean): Boolean {
        val wearableType = WearableType.fromValue(item.flags and 0xFF)
        
        if (replace) {
            wornWearables[wearableType] = item.itemId
        } else {
            // Multi-wear for some types
            wornWearables[wearableType] = item.itemId
        }
        
        // Load wearable data
        val wearableData = loadWearableData(item)
        if (wearableData != null) {
            baker.setWearable(wearableType, wearableData)
        }
        
        // Trigger rebake
        baker.bakeAll()
        
        updateCurrentOutfit()
        return true
    }
    
    private suspend fun loadWearableData(item: InventoryItem): WearableData? {
        // Would load wearable asset and parse it
        // For now, return a placeholder
        return WearableData(
            type = WearableType.fromValue(item.flags and 0xFF),
            assetId = item.assetId,
            textures = emptyMap(),
            params = emptyMap()
        )
    }
    
    private suspend fun attachObject(item: InventoryItem, point: Int, replace: Boolean): Boolean {
        if (replace) {
            wornAttachments[point] = item.itemId
        } else {
            // Add to existing attachments at point
            wornAttachments[point] = item.itemId
        }
        
        updateCurrentOutfit()
        
        // TODO: Send RezSingleAttachmentFromInv to server
        return true
    }
    
    private suspend fun activateGesture(item: InventoryItem): Boolean {
        return gestureManager.activateGesture(item.assetId, item.itemId)
    }
    
    /**
     * Remove a wearable
     */
    suspend fun removeWearable(type: WearableType): Boolean {
        wornWearables.remove(type)
        baker.removeWearable(type)
        baker.bakeAll()
        updateCurrentOutfit()
        return true
    }
    
    /**
     * Detach an object
     */
    suspend fun detachFromPoint(point: Int): Boolean {
        wornAttachments.remove(point)
        updateCurrentOutfit()
        // TODO: Send detach to server
        return true
    }
    
    /**
     * Detach by item ID
     */
    suspend fun detachItem(itemId: UUID): Boolean {
        val point = wornAttachments.entries.find { it.value == itemId }?.key ?: return false
        return detachFromPoint(point)
    }
    
    /**
     * Wear an outfit folder
     */
    suspend fun wearOutfit(folderId: UUID, replace: Boolean = true): Boolean {
        _isChangingOutfit.value = true
        
        return withContext(Dispatchers.Default) {
            try {
                // Fetch folder contents
                inventoryManager.fetchFolderContents(folderId)
                val contents = inventoryManager.getFolderContents(folderId)
                
                if (replace) {
                    // Remove all current items
                    wornWearables.clear()
                    wornAttachments.clear()
                }
                
                // Wear each item
                for (node in contents) {
                    if (node is InventoryNode.Item) {
                        wearItem(node.item.itemId, replace = false)
                    }
                }
                
                true
            } catch (e: Exception) {
                Log.e(TAG, "Failed to wear outfit", e)
                false
            } finally {
                _isChangingOutfit.value = false
            }
        }
    }
    
    /**
     * Save current outfit as a new outfit folder
     */
    suspend fun saveOutfit(name: String): UUID? {
        val outfitsFolder = inventoryManager.getSystemFolder(
            InventoryManager.FOLDER_TYPE_MYOUTFITS
        ) ?: return null
        
        // Create new outfit folder
        val outfitFolderId = inventoryManager.createFolder(
            outfitsFolder,
            name,
            InventoryManager.FOLDER_TYPE_OUTFIT
        ) ?: return null
        
        // Copy current outfit items to folder
        for (itemId in wornWearables.values) {
            inventoryManager.copyItem(itemId, outfitFolderId)
        }
        
        for (itemId in wornAttachments.values) {
            inventoryManager.copyItem(itemId, outfitFolderId)
        }
        
        return outfitFolderId
    }
    
    /**
     * Check if item is worn
     */
    fun isWorn(itemId: UUID): Boolean {
        return wornWearables.containsValue(itemId) || wornAttachments.containsValue(itemId)
    }
    
    /**
     * Get worn wearable by type
     */
    fun getWornWearable(type: WearableType): UUID? = wornWearables[type]
    
    /**
     * Get attachment at point
     */
    fun getAttachmentAt(point: Int): UUID? = wornAttachments[point]
    
    /**
     * Get all worn items
     */
    fun getWornItems(): List<UUID> {
        return wornWearables.values.toList() + wornAttachments.values.toList()
    }
    
    private fun updateCurrentOutfit() {
        _currentOutfit.value = getWornItems()
    }
    
    fun shutdown() {
        scope.cancel()
    }
}

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
    const val LSL2 = 10
    const val SNAPSHOT = 15
    const val ATTACHMENT = 17
    const val WEARABLE = 18
    const val ANIMATION = 19
    const val GESTURE = 20
    const val MESH = 22
    const val SETTINGS = 25
    const val MATERIAL = 26
}
