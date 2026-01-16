package com.linkpoint.world

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.linkpoint.assets.TextureManager
import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.llsd.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

/**
 * World map manager
 * Handles map tile loading and region information
 */
class WorldMap(
    private val capabilityManager: CapabilityManager
) {
    companion object {
        private const val TAG = "WorldMap"
        
        // Map tile URLs
        private const val MAP_URL_TEMPLATE = "https://map.secondlife.com/map-{zoom}-{x}-{y}-objects.jpg"
        
        // Zoom levels (1 = full grid, higher = more detail)
        const val ZOOM_GRID = 1
        const val ZOOM_AREA = 2
        const val ZOOM_REGION = 3
        const val ZOOM_DETAIL = 4
    }
    
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    
    // Cached map tiles
    private val mapTiles = ConcurrentHashMap<String, Bitmap>()
    
    // Known regions
    private val regions = ConcurrentHashMap<String, RegionMapInfo>()
    
    private val _currentPosition = MutableStateFlow<MapPosition?>(null)
    val currentPosition: StateFlow<MapPosition?> = _currentPosition
    
    /**
     * Set current position on map
     */
    fun setCurrentPosition(x: Int, y: Int, localX: Float = 128f, localY: Float = 128f) {
        _currentPosition.value = MapPosition(x, y, localX, localY)
    }
    
    /**
     * Get map tile
     */
    suspend fun getMapTile(x: Int, y: Int, zoom: Int = ZOOM_REGION): Bitmap? {
        val key = "$zoom-$x-$y"
        
        mapTiles[key]?.let { return it }
        
        return withContext(Dispatchers.IO) {
            try {
                val url = MAP_URL_TEMPLATE
                    .replace("{zoom}", zoom.toString())
                    .replace("{x}", x.toString())
                    .replace("{y}", y.toString())
                
                val request = Request.Builder().url(url).build()
                val response = httpClient.newCall(request).execute()
                
                if (!response.isSuccessful) return@withContext null
                
                val data = response.body?.bytes() ?: return@withContext null
                val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
                
                if (bitmap != null) {
                    mapTiles[key] = bitmap
                }
                
                bitmap
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load map tile: $x,$y zoom $zoom", e)
                null
            }
        }
    }
    
    /**
     * Search for regions by name
     */
    suspend fun searchRegions(query: String): List<RegionSearchResult> {
        return withContext(Dispatchers.IO) {
            try {
                val url = "https://search.secondlife.com/regions?q=${query.encodeUrl()}"
                val request = Request.Builder().url(url).build()
                val response = httpClient.newCall(request).execute()
                
                if (!response.isSuccessful) return@withContext emptyList()
                
                val body = response.body?.string() ?: return@withContext emptyList()
                parseRegionSearchResults(body)
            } catch (e: Exception) {
                Log.e(TAG, "Region search failed", e)
                emptyList()
            }
        }
    }
    
    private fun parseRegionSearchResults(json: String): List<RegionSearchResult> {
        // Parse JSON response (simplified)
        return emptyList()
    }
    
    /**
     * Get region info by handle
     */
    suspend fun getRegionInfo(regionHandle: Long): RegionMapInfo? {
        val x = ((regionHandle shr 32) and 0xFFFF).toInt()
        val y = (regionHandle and 0xFFFF).toInt()
        
        return getRegionInfoByGrid(x, y)
    }
    
    /**
     * Get region info by grid coordinates
     */
    suspend fun getRegionInfoByGrid(x: Int, y: Int): RegionMapInfo? {
        val key = "$x-$y"
        
        regions[key]?.let { return it }
        
        // Would query simulator for region info
        return null
    }
    
    /**
     * Get region info by name
     */
    suspend fun getRegionInfoByName(name: String): RegionMapInfo? {
        // Search for region
        val results = searchRegions(name)
        return results.firstOrNull { it.name.equals(name, ignoreCase = true) }?.let {
            RegionMapInfo(
                name = it.name,
                gridX = it.gridX,
                gridY = it.gridY,
                regionHandle = calculateRegionHandle(it.gridX, it.gridY),
                access = it.access,
                mapImageId = it.mapImageId
            )
        }
    }
    
    /**
     * Calculate region handle from grid coordinates
     */
    fun calculateRegionHandle(gridX: Int, gridY: Int): Long {
        return (gridX.toLong() shl 32) or gridY.toLong()
    }
    
    /**
     * Get grid coordinates from region handle
     */
    fun getGridFromHandle(regionHandle: Long): Pair<Int, Int> {
        val x = ((regionHandle shr 32) and 0xFFFF).toInt()
        val y = (regionHandle and 0xFFFF).toInt()
        return x to y
    }
    
    /**
     * Get nearby regions
     */
    suspend fun getNearbyRegions(centerX: Int, centerY: Int, radius: Int): List<RegionMapInfo> {
        val results = mutableListOf<RegionMapInfo>()
        
        for (x in (centerX - radius)..(centerX + radius)) {
            for (y in (centerY - radius)..(centerY + radius)) {
                getRegionInfoByGrid(x, y)?.let { results.add(it) }
            }
        }
        
        return results
    }
    
    /**
     * Get nearby users
     * Returns list of users within the specified range, sorted by distance
     * 
     * @param maxDistance Maximum distance in meters to search for users (default 96m - reasonable default search radius)
     * @param maxResults Maximum number of results to return (default 100)
     * @return List of nearby users sorted by distance
     */
    suspend fun getNearbyUsers(maxDistance: Float = 96f, maxResults: Int = 100): List<NearbyUser> {
        require(maxDistance > 0) { "maxDistance must be positive" }
        require(maxResults > 0) { "maxResults must be positive" }
        
        // Note: Using Dispatchers.IO because the full implementation will perform
        // network I/O to query the simulator for nearby avatar data
        return withContext(Dispatchers.IO) {
            // TODO: This needs to be implemented properly by querying the simulator
            // for nearby avatars using the ObjectUpdate messages and avatar positions.
            // The implementation should:
            // 1. Query the simulator for avatar positions within maxDistance
            // 2. Calculate distances from current position
            // 3. Check friend status using FriendsManager
            // 4. Sort by distance and limit to maxResults
            // For now, returning an empty list until the full protocol implementation
            // is complete.
            emptyList()
        }
    }
    
    /**
     * Clear cached tiles
     */
    fun clearCache() {
        mapTiles.values.forEach { it.recycle() }
        mapTiles.clear()
    }
    
    fun shutdown() {
        scope.cancel()
        clearCache()
    }
    
    private fun String.encodeUrl(): String {
        return java.net.URLEncoder.encode(this, "UTF-8")
    }
}

data class MapPosition(
    val gridX: Int,
    val gridY: Int,
    val localX: Float,
    val localY: Float
)

data class RegionMapInfo(
    val name: String,
    val gridX: Int,
    val gridY: Int,
    val regionHandle: Long,
    val access: Int,
    val mapImageId: UUID?
)

data class RegionSearchResult(
    val name: String,
    val gridX: Int,
    val gridY: Int,
    val access: Int,
    val mapImageId: UUID?
)

/**
 * Represents a nearby user/avatar in the virtual world
 * @param agentId Unique identifier for the user/avatar
 * @param name Display name of the user
 * @param distance Distance in meters from the current user (must be non-negative)
 * @param isFriend Whether this user is in the current user's friends list
 */
data class NearbyUser(
    val agentId: UUID,
    val name: String,
    val distance: Float,
    val isFriend: Boolean
) {
    init {
        require(distance >= 0) { "Distance must be non-negative, got $distance" }
    }
}
