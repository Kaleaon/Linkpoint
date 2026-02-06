package com.linkpoint.bom

import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import com.linkpoint.assets.TextureManager
import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.llsd.*
import kotlinx.coroutines.*
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Bakes on Mesh (BoM) Manager - Handles server-baked textures for mesh bodies.
 * 
 * Bakes on Mesh was introduced in 2019 and allows system layers (skins, tattoos,
 * clothing layers) to be baked by the server and applied to mesh bodies/heads.
 * 
 * Before BoM, mesh bodies used appliers which required creators to make
 * specific textures for each mesh body brand. BoM unifies this by using
 * the standard avatar bake channels.
 * 
 * Reference: https://wiki.secondlife.com/wiki/Bakes_on_Mesh
 */
class BakesOnMeshManager(
    private val capabilityManager: CapabilityManager,
    private val textureManager: TextureManager
) {
    companion object {
        private const val TAG = "BakesOnMeshManager"
        
        // Bake channel indices for mesh bodies
        // These correspond to the baked texture channels on the avatar
        const val BAKE_HEAD = 0
        const val BAKE_UPPER = 1
        const val BAKE_LOWER = 2
        const val BAKE_EYES = 3
        const val BAKE_SKIRT = 4
        const val BAKE_HAIR = 5
        
        // New channels added for BoM aux textures
        const val BAKE_LEFT_ARM = 6
        const val BAKE_LEFT_LEG = 7
        const val BAKE_AUX1 = 8
        const val BAKE_AUX2 = 9
        const val BAKE_AUX3 = 10
        
        // Special texture UUIDs that trigger BoM
        // When a mesh uses these UUIDs, it fetches from the avatar's baked textures
        val BOM_HEAD_UUID = UUID.fromString("9a81c8cf-2d7f-4adf-b7c2-c1a1ea9d5826")
        val BOM_UPPER_UUID = UUID.fromString("4934f1bf-3b1f-cf4f-dbdf-a72550d05bc6")
        val BOM_LOWER_UUID = UUID.fromString("18eca0a5-bead-4c81-6324-dfe2188b78f5")
        val BOM_EYES_UUID = UUID.fromString("87f7f9ec-6913-4e38-4ddf-0a6ae0fb8c8d")
        val BOM_SKIRT_UUID = UUID.fromString("fcad83df-dbc0-41c4-abf0-1c9b86d5cbdd")
        val BOM_HAIR_UUID = UUID.fromString("21f8eb7b-4bca-9055-3b74-4b5b2c65af07")
        val BOM_LEFT_ARM_UUID = UUID.fromString("5aa5c70d-d787-571b-b937-4e6d8ead6b24")
        val BOM_LEFT_LEG_UUID = UUID.fromString("f13e0bc7-3c18-b50b-7af9-3b2bdc43af89")
        val BOM_AUX1_UUID = UUID.fromString("3cd52f54-57da-4bb6-d2d9-7a093a3faa9e")
        val BOM_AUX2_UUID = UUID.fromString("f0d96a04-76e4-7c5b-e01c-6f9a8fed0565")
        val BOM_AUX3_UUID = UUID.fromString("4ea5c3c3-ff14-6f8a-f8f3-74b6cfad4231")
        
        // Map of BoM UUIDs to bake channels
        val BOM_UUID_TO_CHANNEL = mapOf(
            BOM_HEAD_UUID to BAKE_HEAD,
            BOM_UPPER_UUID to BAKE_UPPER,
            BOM_LOWER_UUID to BAKE_LOWER,
            BOM_EYES_UUID to BAKE_EYES,
            BOM_SKIRT_UUID to BAKE_SKIRT,
            BOM_HAIR_UUID to BAKE_HAIR,
            BOM_LEFT_ARM_UUID to BAKE_LEFT_ARM,
            BOM_LEFT_LEG_UUID to BAKE_LEFT_LEG,
            BOM_AUX1_UUID to BAKE_AUX1,
            BOM_AUX2_UUID to BAKE_AUX2,
            BOM_AUX3_UUID to BAKE_AUX3
        )
    }
    
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    // Cache of baked textures by avatar ID and channel
    private val bakedTextureCache = ConcurrentHashMap<UUID, MutableMap<Int, UUID>>()
    
    // Listeners for bake updates
    private val bakeListeners = mutableListOf<BakeListener>()
    
    /**
     * Check if a texture UUID is a BoM reference.
     */
    fun isBomTexture(textureId: UUID): Boolean {
        return BOM_UUID_TO_CHANNEL.containsKey(textureId)
    }
    
    /**
     * Get the bake channel for a BoM texture UUID.
     */
    fun getBakeChannel(bomTextureId: UUID): Int? {
        return BOM_UUID_TO_CHANNEL[bomTextureId]
    }
    
    /**
     * Resolve a BoM texture to the actual baked texture for an avatar.
     * 
     * @param bomTextureId The BoM reference UUID
     * @param avatarId The avatar whose baked texture to use
     * @return The actual baked texture UUID, or the original if not found
     */
    fun resolveBomTexture(bomTextureId: UUID, avatarId: UUID): UUID {
        val channel = getBakeChannel(bomTextureId) ?: return bomTextureId
        return bakedTextureCache[avatarId]?.get(channel) ?: bomTextureId
    }
    
    /**
     * Update the baked texture for an avatar's channel.
     */
    fun updateBakedTexture(avatarId: UUID, channel: Int, textureId: UUID) {
        val avatarCache = bakedTextureCache.getOrPut(avatarId) { mutableMapOf() }
        avatarCache[channel] = textureId
        
        Log.d(TAG, "Updated bake for avatar $avatarId channel $channel: $textureId")
        
        // Notify listeners
        bakeListeners.forEach { it.onBakeUpdated(avatarId, channel, textureId) }
    }
    
    /**
     * Get all baked textures for an avatar.
     */
    fun getBakedTextures(avatarId: UUID): Map<Int, UUID>? {
        return bakedTextureCache[avatarId]?.toMap()
    }
    
    /**
     * Request rebake for an avatar (triggers server-side baking).
     */
    suspend fun requestRebake(avatarId: UUID): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // Use the UploadAgentBakedTexture capability to trigger rebake
                val request = LLSDMap().apply {
                    this["agent_id"] = LLSDUUID(avatarId)
                }
                
                val response = capabilityManager.request("UploadAgentBakedTexture", request)
                
                if (response is LLSDMap) {
                    // Server will send new baked textures via AgentCachedTexture
                    Log.i(TAG, "Rebake requested for avatar $avatarId")
                    true
                } else {
                    false
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to request rebake", e)
                false
            }
        }
    }
    
    /**
     * Handle AgentCachedTexture message (baked texture update from server).
     */
    fun handleCachedTexture(avatarId: UUID, textureIndex: Int, textureId: UUID) {
        updateBakedTexture(avatarId, textureIndex, textureId)
    }
    
    /**
     * Handle AgentSetAppearance response with baked textures.
     */
    fun handleAppearanceUpdate(avatarId: UUID, bakedTextures: Map<Int, UUID>) {
        for ((channel, textureId) in bakedTextures) {
            updateBakedTexture(avatarId, channel, textureId)
        }
    }
    
    /**
     * Clear baked textures for an avatar (e.g., when they leave).
     */
    fun clearBakedTextures(avatarId: UUID) {
        bakedTextureCache.remove(avatarId)
    }
    
    /**
     * Add a listener for bake updates.
     */
    fun addBakeListener(listener: BakeListener) {
        bakeListeners.add(listener)
    }
    
    /**
     * Remove a bake listener.
     */
    fun removeBakeListener(listener: BakeListener) {
        bakeListeners.remove(listener)
    }
    
    /**
     * Get channel name for display.
     */
    fun getChannelName(channel: Int): String {
        return when (channel) {
            BAKE_HEAD -> "Head"
            BAKE_UPPER -> "Upper Body"
            BAKE_LOWER -> "Lower Body"
            BAKE_EYES -> "Eyes"
            BAKE_SKIRT -> "Skirt"
            BAKE_HAIR -> "Hair"
            BAKE_LEFT_ARM -> "Left Arm"
            BAKE_LEFT_LEG -> "Left Leg"
            BAKE_AUX1 -> "Aux 1"
            BAKE_AUX2 -> "Aux 2"
            BAKE_AUX3 -> "Aux 3"
            else -> "Unknown"
        }
    }
    
    fun shutdown() {
        scope.cancel()
        bakedTextureCache.clear()
        bakeListeners.clear()
    }
    
    /**
     * Listener for bake texture updates.
     */
    interface BakeListener {
        fun onBakeUpdated(avatarId: UUID, channel: Int, textureId: UUID)
    }
}
