package com.linkpoint.inventory

import android.util.Log
import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.llsd.*
import com.linkpoint.protocol.messages.MessageIds
import com.linkpoint.protocol.messages.UDPConnection
import com.linkpoint.protocol.transfer.TransferManager
import com.linkpoint.protocol.types.LLVector3
import com.linkpoint.protocol.types.putUUID
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * LandmarkManager - Handles landmark creation, storage, and teleportation.
 * 
 * Features:
 * - Create landmarks at current location
 * - Parse landmark assets
 * - Store favorite landmarks
 * - Teleport to landmarks
 * 
 * Based on Lumiya's landmark implementation.
 */
class LandmarkManager(
    private val capabilityManager: CapabilityManager,
    private val transferManager: TransferManager,
    private val inventoryManager: InventoryManager,
    private val udpConnection: UDPConnection,
    private val agentId: UUID
) {
    companion object {
        private const val TAG = "LandmarkManager"
        
        // Landmark asset version
        private const val LANDMARK_VERSION = 3
    }
    
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    // Cached landmarks
    private val landmarks = ConcurrentHashMap<UUID, Landmark>()
    
    // Favorites
    private val _favorites = MutableStateFlow<List<Landmark>>(emptyList())
    val favorites: StateFlow<List<Landmark>> = _favorites
    
    // Recent landmarks
    private val _recentLandmarks = MutableStateFlow<List<Landmark>>(emptyList())
    val recentLandmarks: StateFlow<List<Landmark>> = _recentLandmarks
    
    /**
     * Create a landmark at the current location.
     */
    suspend fun createLandmark(
        name: String,
        description: String = "",
        regionName: String,
        regionHandle: Long,
        position: LLVector3
    ): UUID? {
        return withContext(Dispatchers.IO) {
            try {
                // Create landmark asset data
                val assetData = buildLandmarkAsset(regionHandle, position)
                
                // Create the inventory item via CreateInventoryItem capability
                val createCap = capabilityManager.getCapability("CreateInventoryItem")
                if (createCap != null) {
                    val request = LLSDMap().apply {
                        this["type"] = LLSDInteger(3) // Landmark asset type
                        this["name"] = LLSDString(name)
                        this["description"] = LLSDString(description)
                        this["asset_data"] = LLSDBinary(assetData)
                        
                        // Put in Landmarks folder
                        inventoryManager.getSystemFolder(InventoryManager.FOLDER_TYPE_LANDMARK)?.let { folderId ->
                            this["folder_id"] = LLSDUUID(folderId)
                        }
                    }
                    
                    val response = capabilityManager.request("CreateInventoryItem", request)
                    if (response is LLSDMap) {
                        val itemId = response.getUUID("item_id")
                        if (itemId != null) {
                            val landmark = Landmark(
                                itemId = itemId,
                                assetId = response.getUUID("asset_id") ?: itemId,
                                name = name,
                                description = description,
                                regionName = regionName,
                                regionHandle = regionHandle,
                                position = position
                            )
                            landmarks[itemId] = landmark
                            addToRecent(landmark)
                            Log.i(TAG, "Created landmark: $name at $regionName")
                            return@withContext itemId
                        }
                    }
                }
                
                // Fallback to UDP CreateInventoryItem
                createLandmarkViaUDP(name, description, regionHandle, position)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to create landmark", e)
                null
            }
        }
    }
    
    /**
     * Parse a landmark asset to extract location data.
     */
    suspend fun parseLandmark(assetId: UUID): Landmark? {
        return withContext(Dispatchers.IO) {
            try {
                // Check cache first
                landmarks[assetId]?.let { return@withContext it }
                
                // Fetch asset data - asset type 3 is Landmark
                val assetData = transferManager.fetchAsset(assetId, 3)
                    ?: return@withContext null
                
                parseLandmarkData(assetId, assetData)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to parse landmark $assetId", e)
                null
            }
        }
    }
    
    /**
     * Add landmark to favorites.
     */
    fun addToFavorites(landmark: Landmark) {
        val current = _favorites.value.toMutableList()
        if (!current.any { it.itemId == landmark.itemId }) {
            current.add(0, landmark)
            _favorites.value = current
        }
    }
    
    /**
     * Remove landmark from favorites.
     */
    fun removeFromFavorites(landmarkId: UUID) {
        _favorites.value = _favorites.value.filter { it.itemId != landmarkId }
    }
    
    /**
     * Get cached landmark.
     */
    fun getLandmark(itemId: UUID): Landmark? = landmarks[itemId]
    
    // ==================== PRIVATE METHODS ====================
    
    private fun buildLandmarkAsset(regionHandle: Long, position: LLVector3): ByteArray {
        // Landmark format (text-based):
        // Landmark version 2
        // region_id <uuid>
        // local_pos <x> <y> <z>
        // region_handle <handle>
        
        val builder = StringBuilder()
        builder.appendLine("Landmark version $LANDMARK_VERSION")
        builder.appendLine("region_handle $regionHandle")
        builder.appendLine("local_pos ${position.x} ${position.y} ${position.z}")
        
        return builder.toString().toByteArray(Charsets.UTF_8)
    }
    
    private fun parseLandmarkData(assetId: UUID, data: ByteArray): Landmark? {
        try {
            val text = String(data, Charsets.UTF_8)
            val lines = text.lines()
            
            var regionHandle: Long = 0
            var position = LLVector3(128f, 128f, 25f)
            var regionId: UUID? = null
            
            for (line in lines) {
                val parts = line.trim().split(" ")
                when {
                    line.startsWith("region_handle") && parts.size >= 2 -> {
                        regionHandle = parts[1].toLongOrNull() ?: 0
                    }
                    line.startsWith("local_pos") && parts.size >= 4 -> {
                        position = LLVector3(
                            parts[1].toFloatOrNull() ?: 128f,
                            parts[2].toFloatOrNull() ?: 128f,
                            parts[3].toFloatOrNull() ?: 25f
                        )
                    }
                    line.startsWith("region_id") && parts.size >= 2 -> {
                        regionId = try { UUID.fromString(parts[1]) } catch (e: Exception) { null }
                    }
                }
            }
            
            val landmark = Landmark(
                itemId = assetId,
                assetId = assetId,
                name = "Landmark",
                description = "",
                regionName = "", // Would need to resolve from region handle
                regionHandle = regionHandle,
                position = position
            )
            
            landmarks[assetId] = landmark
            return landmark
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing landmark data", e)
            return null
        }
    }
    
    private suspend fun createLandmarkViaUDP(
        name: String,
        description: String,
        regionHandle: Long,
        position: LLVector3
    ): UUID? {
        // This would use CreateInventoryItem UDP message
        // For now, return null as capability is preferred
        return null
    }
    
    private fun addToRecent(landmark: Landmark) {
        val current = _recentLandmarks.value.toMutableList()
        current.removeAll { it.itemId == landmark.itemId }
        current.add(0, landmark)
        if (current.size > 20) {
            current.removeAt(current.lastIndex)
        }
        _recentLandmarks.value = current
    }
    
    fun shutdown() {
        scope.cancel()
    }
}

/**
 * Represents a landmark location.
 */
data class Landmark(
    val itemId: UUID,
    val assetId: UUID,
    val name: String,
    val description: String,
    val regionName: String,
    val regionHandle: Long,
    val position: LLVector3,
    val isFavorite: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
) {
    val globalX: Int get() = ((regionHandle shr 32) and 0xFFFF).toInt() * 256 + position.x.toInt()
    val globalY: Int get() = (regionHandle and 0xFFFF).toInt() * 256 + position.y.toInt()
    
    val slurl: String get() = "secondlife://$regionName/${position.x.toInt()}/${position.y.toInt()}/${position.z.toInt()}"
}
