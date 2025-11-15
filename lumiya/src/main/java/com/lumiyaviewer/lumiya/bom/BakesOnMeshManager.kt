package com.lumiyaviewer.lumiya.bom

import android.util.Log
import kotlinx.coroutines.*
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Bakes on Mesh Manager
 * Manages BoM texture baking and caching
 */
class BakesOnMeshManager(
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
) {
    companion object {
        private const val TAG = "BomManager"
        private const val MAX_CACHE_SIZE = 100
        private const val CACHE_EXPIRY_MS = 3600000L // 1 hour
    }
    
    // Avatar configurations
    private val avatarConfigs = ConcurrentHashMap<UUID, BomAvatarConfig>()
    
    // Bake cache
    private val bakeCache = ConcurrentHashMap<String, BomCacheEntry>()
    
    // Pending bake requests
    private val pendingBakes = ConcurrentHashMap<String, Job>()
    
    // Statistics
    private var stats = BomStatistics()
    private var cacheHits = 0
    private var cacheMisses = 0
    
    // Callbacks
    private val bakeCompleteCallbacks = mutableListOf<(UUID, BomBakeType, UUID) -> Unit>()
    
    /**
     * Register avatar for BoM
     */
    fun registerAvatar(avatarId: UUID, config: BomAvatarConfig = BomAvatarConfig(avatarId)) {
        avatarConfigs[avatarId] = config
        Log.i(TAG, "Registered avatar for BoM: $avatarId")
    }
    
    /**
     * Unregister avatar
     */
    fun unregisterAvatar(avatarId: UUID) {
        avatarConfigs.remove(avatarId)
        Log.i(TAG, "Unregistered avatar: $avatarId")
    }
    
    /**
     * Add texture layer to avatar
     */
    fun addTextureLayer(avatarId: UUID, layer: BomTextureLayer) {
        val config = avatarConfigs[avatarId] ?: return
        
        val currentLayers = config.layers[layer.bakeType]?.toMutableList() ?: mutableListOf()
        currentLayers.add(layer)
        currentLayers.sortBy { it.order }
        
        val newLayers = config.layers.toMutableMap()
        newLayers[layer.bakeType] = currentLayers
        
        avatarConfigs[avatarId] = config.copy(layers = newLayers)
        
        Log.d(TAG, "Added layer to avatar $avatarId, bake type ${layer.bakeType}")
        
        // Trigger rebake
        requestBake(avatarId, layer.bakeType)
    }
    
    /**
     * Remove texture layer from avatar
     */
    fun removeTextureLayer(avatarId: UUID, layerId: UUID, bakeType: BomBakeType) {
        val config = avatarConfigs[avatarId] ?: return
        
        val currentLayers = config.layers[bakeType]?.toMutableList() ?: return
        currentLayers.removeIf { it.layerId == layerId }
        
        val newLayers = config.layers.toMutableMap()
        newLayers[bakeType] = currentLayers
        
        avatarConfigs[avatarId] = config.copy(layers = newLayers)
        
        Log.d(TAG, "Removed layer from avatar $avatarId, bake type $bakeType")
        
        // Trigger rebake
        requestBake(avatarId, bakeType)
    }
    
    /**
     * Request bake for specific type
     */
    fun requestBake(avatarId: UUID, bakeType: BomBakeType) {
        val config = avatarConfigs[avatarId] ?: return
        val layers = config.layers[bakeType] ?: emptyList()
        
        if (layers.isEmpty()) {
            Log.d(TAG, "No layers to bake for $avatarId, type $bakeType")
            return
        }
        
        val hash = BomBakeRequest.calculateHash(layers)
        val cacheKey = "$avatarId:${bakeType.index}:$hash"
        
        // Check cache first
        bakeCache[cacheKey]?.let { cached ->
            if (!cached.isExpired()) {
                cacheHits++
                cached.access()
                applyBakedTexture(avatarId, bakeType, cached.textureId, hash)
                Log.d(TAG, "Using cached bake for $avatarId, type $bakeType")
                return
            }
        }
        
        cacheMisses++
        
        // Cancel existing bake if pending
        pendingBakes[cacheKey]?.cancel()
        
        // Start new bake
        val job = scope.launch {
            try {
                val result = performBake(avatarId, bakeType, layers, hash)
                if (result != null) {
                    cacheBake(cacheKey, bakeType, result, hash)
                    applyBakedTexture(avatarId, bakeType, result, hash)
                }
            } finally {
                pendingBakes.remove(cacheKey)
            }
        }
        
        pendingBakes[cacheKey] = job
    }
    
    /**
     * Perform actual baking (sends to server or does local baking)
     */
    private suspend fun performBake(
        avatarId: UUID,
        bakeType: BomBakeType,
        layers: List<BomTextureLayer>,
        hash: String
    ): UUID? = withContext(Dispatchers.IO) {
        try {
            Log.i(TAG, "Performing bake for $avatarId, type $bakeType with ${layers.size} layers")
            
            // In production, this would:
            // 1. Send bake request to SL server via AgentSetAppearance message
            // 2. Wait for server to composite textures
            // 3. Receive baked texture UUID via AvatarAppearance message
            
            // For now, simulate with delay
            delay(500)
            
            // Generate mock baked texture ID
            val bakedTextureId = UUID.randomUUID()
            
            Log.i(TAG, "Bake completed for $avatarId, type $bakeType -> $bakedTextureId")
            
            bakedTextureId
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to perform bake", e)
            null
        }
    }
    
    /**
     * Cache baked texture
     */
    private fun cacheBake(cacheKey: String, bakeType: BomBakeType, textureId: UUID, hash: String) {
        val entry = BomCacheEntry(
            hash = hash,
            bakeType = bakeType,
            textureId = textureId,
            textureData = null // Would store actual texture data
        )
        
        bakeCache[cacheKey] = entry
        
        // Evict old entries if cache too large
        if (bakeCache.size > MAX_CACHE_SIZE) {
            evictOldestCacheEntries()
        }
        
        Log.d(TAG, "Cached bake: $cacheKey")
    }
    
    /**
     * Evict oldest cache entries
     */
    private fun evictOldestCacheEntries() {
        val entries = bakeCache.values.sortedBy { it.lastAccess }
        val toRemove = entries.take(MAX_CACHE_SIZE / 4)
        
        toRemove.forEach { entry ->
            bakeCache.entries.removeIf { 
                it.value.hash == entry.hash && it.value.bakeType == entry.bakeType 
            }
        }
        
        Log.d(TAG, "Evicted ${toRemove.size} cache entries")
    }
    
    /**
     * Apply baked texture to avatar
     */
    private fun applyBakedTexture(avatarId: UUID, bakeType: BomBakeType, textureId: UUID, hash: String) {
        val config = avatarConfigs[avatarId] ?: return
        
        val result = BomBakeResult(
            bakeType = bakeType,
            bakedTextureId = textureId,
            hash = hash
        )
        
        val newBakes = config.bakes.toMutableMap()
        newBakes[bakeType] = result
        
        avatarConfigs[avatarId] = config.copy(bakes = newBakes)
        
        // Notify callbacks
        bakeCompleteCallbacks.forEach { callback ->
            callback(avatarId, bakeType, textureId)
        }
        
        Log.i(TAG, "Applied baked texture to avatar $avatarId, type $bakeType")
    }
    
    /**
     * Get baked texture for avatar
     */
    fun getBakedTexture(avatarId: UUID, bakeType: BomBakeType): UUID? {
        return avatarConfigs[avatarId]?.getBakedTexture(bakeType)
    }
    
    /**
     * Get all baked textures for avatar
     */
    fun getAllBakedTextures(avatarId: UUID): Map<BomBakeType, UUID> {
        val config = avatarConfigs[avatarId] ?: return emptyMap()
        return config.bakes.mapValues { it.value.bakedTextureId }
    }
    
    /**
     * Rebake all stale bakes for avatar
     */
    fun rebakeAll(avatarId: UUID) {
        val config = avatarConfigs[avatarId] ?: return
        val staleBakes = config.getStaleBakes()
        
        staleBakes.forEach { bakeType ->
            requestBake(avatarId, bakeType)
        }
        
        Log.i(TAG, "Rebaking ${staleBakes.size} stale bakes for avatar $avatarId")
    }
    
    /**
     * Add bake complete callback
     */
    fun addBakeCompleteCallback(callback: (UUID, BomBakeType, UUID) -> Unit) {
        bakeCompleteCallbacks.add(callback)
    }
    
    /**
     * Remove bake complete callback
     */
    fun removeBakeCompleteCallback(callback: (UUID, BomBakeType, UUID) -> Unit) {
        bakeCompleteCallbacks.remove(callback)
    }
    
    /**
     * Clear cache
     */
    fun clearCache() {
        bakeCache.clear()
        Log.i(TAG, "Cleared BoM cache")
    }
    
    /**
     * Get statistics
     */
    fun getStatistics(): BomStatistics {
        val hitRate = if (cacheHits + cacheMisses > 0) {
            cacheHits.toFloat() / (cacheHits + cacheMisses)
        } else 0f
        
        return BomStatistics(
            totalBakes = avatarConfigs.values.sumOf { it.bakes.size },
            cachedBakes = bakeCache.size,
            activeBakes = avatarConfigs.size,
            pendingBakes = pendingBakes.size,
            cacheHitRate = hitRate
        )
    }
    
    /**
     * Cleanup
     */
    fun cleanup() {
        pendingBakes.values.forEach { it.cancel() }
        pendingBakes.clear()
        avatarConfigs.clear()
        bakeCache.clear()
        bakeCompleteCallbacks.clear()
        scope.cancel()
        Log.i(TAG, "BoM manager cleaned up")
    }
}