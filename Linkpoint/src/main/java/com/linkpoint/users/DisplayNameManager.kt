package com.linkpoint.users

import android.util.Log
import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.llsd.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Display Names Manager - Handles fetching and caching of display names.
 * 
 * Based on the reference viewer's SLDisplayNameFetcher.java
 * 
 * Display names in Second Life are user-chosen names that appear alongside
 * or instead of the legacy username. They can be changed periodically.
 * 
 * Uses GetDisplayNames capability to fetch names in batches.
 */
class DisplayNameManager(
    private val capabilityManager: CapabilityManager
) {
    companion object {
        private const val TAG = "DisplayNameManager"
        
        // Max IDs per request (SL limit is typically 90)
        private const val MAX_IDS_PER_REQUEST = 80
        
        // Request batch delay
        private const val BATCH_DELAY_MS = 100L
    }
    
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    // Cache of display names by agent ID
    private val nameCache = ConcurrentHashMap<UUID, CachedDisplayName>()
    
    // Pending requests (IDs waiting to be fetched)
    private val pendingIds = mutableSetOf<UUID>()
    
    // Callbacks waiting for specific IDs
    private val callbacks = ConcurrentHashMap<UUID, MutableList<DisplayNameCallback>>()
    
    // Batch fetch job
    private var batchJob: Job? = null
    
    /**
     * Get display name for an agent, fetching if necessary.
     */
    fun getDisplayName(agentId: UUID, callback: DisplayNameCallback? = null): DisplayName? {
        // Check cache first
        val cached = nameCache[agentId]
        if (cached != null && !cached.isExpired()) {
            callback?.onDisplayName(cached.name)
            return cached.name
        }
        
        // Add callback and queue for fetch
        if (callback != null) {
            callbacks.getOrPut(agentId) { mutableListOf() }.add(callback)
        }
        
        synchronized(pendingIds) {
            pendingIds.add(agentId)
            scheduleBatchFetch()
        }
        
        return null
    }
    
    /**
     * Get display names for multiple agents.
     */
    fun getDisplayNames(agentIds: List<UUID>, callback: DisplayNamesCallback? = null) {
        val result = mutableMapOf<UUID, DisplayName>()
        val toFetch = mutableListOf<UUID>()
        
        for (id in agentIds) {
            val cached = nameCache[id]
            if (cached != null && !cached.isExpired()) {
                result[id] = cached.name
            } else {
                toFetch.add(id)
            }
        }
        
        if (toFetch.isEmpty()) {
            callback?.onDisplayNames(result)
            return
        }
        
        // Fetch remaining names
        scope.launch {
            val fetched = fetchDisplayNames(toFetch)
            result.putAll(fetched)
            callback?.onDisplayNames(result)
        }
    }
    
    /**
     * Schedule a batch fetch of pending IDs.
     */
    private fun scheduleBatchFetch() {
        batchJob?.cancel()
        batchJob = scope.launch {
            delay(BATCH_DELAY_MS)
            
            val idsToFetch: List<UUID>
            synchronized(pendingIds) {
                idsToFetch = pendingIds.toList()
                pendingIds.clear()
            }
            
            if (idsToFetch.isNotEmpty()) {
                val names = fetchDisplayNames(idsToFetch)
                notifyCallbacks(names)
            }
        }
    }
    
    /**
     * Fetch display names from capability.
     */
    private suspend fun fetchDisplayNames(agentIds: List<UUID>): Map<UUID, DisplayName> {
        val result = mutableMapOf<UUID, DisplayName>()
        
        try {
            val cap = capabilityManager.getCapability(CapabilityManager.CAP_GET_DISPLAY_NAMES)
            if (cap == null) {
                Log.w(TAG, "GetDisplayNames capability not available")
                // Return legacy names as fallback
                return agentIds.associateWith { id ->
                    DisplayName(
                        agentId = id,
                        username = "Resident",
                        displayName = null,
                        isDefault = true,
                        nextUpdate = 0L
                    )
                }
            }
            
            // Fetch in batches
            for (batch in agentIds.chunked(MAX_IDS_PER_REQUEST)) {
                val batchResult = fetchBatch(cap, batch)
                result.putAll(batchResult)
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching display names", e)
        }
        
        return result
    }
    
    /**
     * Fetch a batch of display names.
     */
    private suspend fun fetchBatch(capUrl: String, agentIds: List<UUID>): Map<UUID, DisplayName> {
        val result = mutableMapOf<UUID, DisplayName>()
        
        try {
            // Build URL with query parameters
            val idsParam = agentIds.joinToString(",") { it.toString() }
            val url = "$capUrl?ids=$idsParam"
            
            // Make request
            val response = capabilityManager.request(CapabilityManager.CAP_GET_DISPLAY_NAMES)
            
            if (response is LLSDMap) {
                parseDisplayNamesResponse(response, result)
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching display name batch", e)
        }
        
        return result
    }
    
    /**
     * Parse display names response.
     */
    private fun parseDisplayNamesResponse(response: LLSDMap, result: MutableMap<UUID, DisplayName>) {
        val agents = response.getArray("agents")
        
        agents?.value?.forEach { item ->
            if (item is LLSDMap) {
                val agentId = item.getUUID("id") ?: return@forEach
                val username = item.getString("username") ?: ""
                val displayName = item.getString("display_name")
                val isDefault = item.getBoolean("is_display_name_default") ?: true
                val nextUpdate = item.getInt("display_name_next_update")?.toLong() ?: 0L
                val legacyFirstName = item.getString("legacy_first_name") ?: ""
                val legacyLastName = item.getString("legacy_last_name") ?: ""
                
                val name = DisplayName(
                    agentId = agentId,
                    username = username.ifEmpty { "$legacyFirstName $legacyLastName".trim() },
                    displayName = if (isDefault) null else displayName,
                    isDefault = isDefault,
                    nextUpdate = nextUpdate,
                    legacyFirstName = legacyFirstName,
                    legacyLastName = legacyLastName
                )
                
                // Cache the result
                nameCache[agentId] = CachedDisplayName(name, System.currentTimeMillis())
                result[agentId] = name
            }
        }
        
        // Handle bad IDs (agents not found)
        val badIds = response.getArray("bad_ids")
        badIds?.value?.forEach { item ->
            if (item is LLSDString) {
                try {
                    val agentId = UUID.fromString(item.value)
                    val name = DisplayName(
                        agentId = agentId,
                        username = "(Unknown)",
                        displayName = null,
                        isDefault = true,
                        nextUpdate = 0L
                    )
                    nameCache[agentId] = CachedDisplayName(name, System.currentTimeMillis())
                    result[agentId] = name
                } catch (e: Exception) {
                    // Invalid UUID, ignore
                }
            }
        }
    }
    
    /**
     * Notify callbacks of fetched names.
     */
    private fun notifyCallbacks(names: Map<UUID, DisplayName>) {
        for ((agentId, name) in names) {
            val callbackList = callbacks.remove(agentId) ?: continue
            callbackList.forEach { it.onDisplayName(name) }
        }
    }
    
    /**
     * Manually cache a display name (e.g., from avatar data).
     */
    fun cacheDisplayName(name: DisplayName) {
        nameCache[name.agentId] = CachedDisplayName(name, System.currentTimeMillis())
    }
    
    /**
     * Get cached display name without fetching.
     */
    fun getCachedDisplayName(agentId: UUID): DisplayName? {
        return nameCache[agentId]?.name
    }
    
    /**
     * Clear the cache.
     */
    fun clearCache() {
        nameCache.clear()
    }
    
    /**
     * Shutdown the manager.
     */
    fun shutdown() {
        batchJob?.cancel()
        scope.cancel()
        nameCache.clear()
        callbacks.clear()
    }
}

/**
 * Display name data.
 */
data class DisplayName(
    val agentId: UUID,
    val username: String,
    val displayName: String?,
    val isDefault: Boolean,
    val nextUpdate: Long,
    val legacyFirstName: String = "",
    val legacyLastName: String = ""
) {
    /**
     * Get the name to display (display name if set, otherwise username).
     */
    fun getEffectiveName(): String {
        return displayName ?: username
    }
    
    /**
     * Get the full display with both names.
     */
    fun getFullDisplay(): String {
        return if (displayName != null && !isDefault) {
            "$displayName ($username)"
        } else {
            username
        }
    }
    
    /**
     * Get legacy full name.
     */
    fun getLegacyName(): String {
        return if (legacyLastName.isNotEmpty() && legacyLastName != "Resident") {
            "$legacyFirstName $legacyLastName"
        } else {
            legacyFirstName.ifEmpty { username }
        }
    }
}

/**
 * Cached display name with timestamp.
 */
internal data class CachedDisplayName(
    val name: DisplayName,
    val cachedAt: Long
) {
    fun isExpired(): Boolean {
        return System.currentTimeMillis() - cachedAt > CACHE_EXPIRY_MS
    }
    
    companion object {
        private const val CACHE_EXPIRY_MS = 60 * 60 * 1000L
    }
}

/**
 * Callback for single display name.
 */
fun interface DisplayNameCallback {
    fun onDisplayName(name: DisplayName)
}

/**
 * Callback for multiple display names.
 */
fun interface DisplayNamesCallback {
    fun onDisplayNames(names: Map<UUID, DisplayName>)
}
