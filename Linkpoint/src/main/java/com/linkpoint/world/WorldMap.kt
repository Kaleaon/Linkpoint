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
        
        // Default search radius for nearby users (meters)
        private const val DEFAULT_NEARBY_RADIUS = 96f
    }
    
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    // Avatar and friends managers - set after login via setters
    private var avatarManagerProvider: (() -> com.linkpoint.avatar.AvatarManager?)? = null
    private var friendsManagerProvider: (() -> FriendsManager?)? = null
    
    /**
     * Set the avatar manager provider (called after login)
     */
    fun setAvatarManagerProvider(provider: () -> com.linkpoint.avatar.AvatarManager?) {
        avatarManagerProvider = provider
    }
    
    /**
     * Set the friends manager provider (called after login)
     */
    fun setFriendsManagerProvider(provider: () -> FriendsManager?) {
        friendsManagerProvider = provider
    }
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
     * Get nearby users/avatars in the current region
     * Returns avatars from AvatarManager, with friend status from FriendsManager
     * 
     * @param maxDistance Maximum distance in meters to search for users (default 96m)
     * @param maxResults Maximum number of results to return (default 100)
     * @return List of nearby users sorted by distance
     */
    suspend fun getNearbyUsers(maxDistance: Float = DEFAULT_NEARBY_RADIUS, maxResults: Int = 100): List<NearbyUser> {
        return withContext(Dispatchers.IO) {
            val avatarManager = avatarManagerProvider?.invoke()
            val friendsManager = friendsManagerProvider?.invoke()
            
            if (avatarManager == null) {
                Log.w(TAG, "getNearbyUsers: AvatarManager not available")
                return@withContext emptyList()
            }
            
            val myAvatar = avatarManager.getMyAvatar()
            if (myAvatar == null) {
                Log.w(TAG, "getNearbyUsers: Local avatar not loaded yet")
                return@withContext emptyList()
            }
            
            val myPosition = myAvatar.position
            val myAgentId = myAvatar.agentId
            
            // Get all avatars except ourselves
            val allAvatars = avatarManager.getAllAvatars()
                .filter { it.agentId != myAgentId }
            
            // Convert avatars to NearbyUser with distance and friend status
            allAvatars
                .map { avatar ->
                    val distance = avatar.position.distance(myPosition)
                    val isFriend = friendsManager?.isFriend(avatar.agentId) ?: false
                    val displayName = avatar.displayName ?: avatar.userName ?: "Unknown"
                    
                    NearbyUser(
                        agentId = avatar.agentId,
                        name = displayName,
                        distance = distance,
                        isFriend = isFriend,
                        position = floatArrayOf(avatar.position.x, avatar.position.y, avatar.position.z),
                        profilePictureId = null // Could be populated from profile data if available
                    )
                }
                .filter { it.distance <= maxDistance }
                .sortedBy { it.distance }
                .take(maxResults)
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
