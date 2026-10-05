package com.linkpoint.world

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.util.LruCache
import com.linkpoint.assets.AssetCache
import com.linkpoint.assets.AssetType
import com.linkpoint.assets.CacheManager
import com.linkpoint.assets.TextureManager
import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.llsd.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

/**
 * World map manager
 * Handles map tile loading and region information
 */
class WorldMap(
    private val capabilityManager: CapabilityManager,
    private val assetCache: AssetCache? = null,
    private val cacheManager: CacheManager? = null,
    private val tileCacheDir: File? = null
) {
    companion object {
        private const val TAG = "WorldMap"

        // Map tile URLs
        private const val MAP_URL_TEMPLATE = "https://map.secondlife.com/map-{zoom}-{x}-{y}-objects.jpg"

        // Byte-bounded memory LRU cache size limit for decoded tile bitmaps (24 MB)
        const val TILE_MEMORY_CACHE_MAX_BYTES = 24 * 1024 * 1024

        // Zoom levels (1 = full grid, higher = more detail)
        const val ZOOM_GRID = 1
        const val ZOOM_AREA = 2
        const val ZOOM_REGION = 3
        const val ZOOM_DETAIL = 4

        // Default search radius for nearby users (meters)
        private const val DEFAULT_NEARBY_RADIUS = 96f

        // Default access level when not specified by API (0 = unknown/PG)
        private const val DEFAULT_ACCESS_LEVEL = 0

        // Region search result parsing pattern
        private val REGION_SEARCH_PATTERN = """\{"name"\s*:\s*"([^"]+)"\s*,\s*"x"\s*:\s*(\d+)\s*,\s*"y"\s*:\s*(\d+)""".toRegex()
    }

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // Reusable HTTP client for map requests
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

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

    /**
     * Cache region info from MapBlockReply message.
     */
    fun cacheRegionInfo(gridX: Int, gridY: Int, name: String, mapImageId: UUID) {
        val key = "$gridX-$gridY"
        val regionHandle = (gridX.toLong() shl 32) or gridY.toLong()
        val info = RegionMapInfo(
            name = name,
            gridX = gridX,
            gridY = gridY,
            regionHandle = regionHandle,
            access = 0, // Default access level
            mapImageId = mapImageId
        )
        regions[key] = info
        Log.d(TAG, "Cached region info: $name at ($gridX, $gridY)")
    }

    // Byte-bounded memory LRU cache for decoded tile bitmaps
    private val mapTiles = object : LruCache<String, Bitmap>(TILE_MEMORY_CACHE_MAX_BYTES) {
        override fun sizeOf(key: String, bitmap: Bitmap): Int {
            val bytes = bitmap.byteCount
            return if (bytes > 0) bytes else (bitmap.width * bitmap.height * 4)
        }
    }

    private fun getDiskTileFile(key: String): File? {
        val dir = tileCacheDir ?: cacheManager?.let { File(it.getPublicCacheDirectory(), "map_tiles") }
        if (dir != null) {
            if (!dir.exists()) dir.mkdirs()
            return File(dir, "tile_$key.jpg")
        }
        return null
    }

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
     * Get the effective map tile URL template using dynamically resolved grid mapUri or SL default.
     */
    fun getEffectiveMapUrlTemplate(): String {
        try {
            val app = com.linkpoint.LinkpointApp.getInstance()
            val customMapUri = app.sessionManager.getMapUri() ?: app.gridManager.getSelectedGrid().mapUri
            if (!customMapUri.isNullOrBlank()) {
                val trimmed = customMapUri.trim()
                if (trimmed.contains("{x}") && trimmed.contains("{y}")) {
                    return trimmed
                }
                val base = trimmed.trimEnd('/')
                return "$base/map-{zoom}-{x}-{y}-objects.jpg"
            }
        } catch (e: Exception) {
            // Fallback to default template if app context is not initialized in unit tests
        }
        return MAP_URL_TEMPLATE
    }

    /**
     * Get map tile
     */
    suspend fun getMapTile(x: Int, y: Int, zoom: Int = ZOOM_REGION): Bitmap? {
        val key = "$zoom-$x-$y"

        // Check memory LRU cache first
        mapTiles.get(key)?.let {
            if (!it.isRecycled) return it
        }

        return withContext(Dispatchers.IO) {
            try {
                val tileUuid = UUID.nameUUIDFromBytes("maptile_$key".toByteArray())
                var data: ByteArray? = null

                // Tier 2: Check disk cache (AssetCache or disk tile file)
                if (assetCache != null) {
                    data = assetCache.get(tileUuid, AssetType.IMAGE_JPEG)
                }

                if (data == null) {
                    val diskFile = getDiskTileFile(key)
                    if (diskFile != null && diskFile.exists()) {
                        try {
                            data = diskFile.readBytes()
                        } catch (e: Exception) {
                            Log.w(TAG, "Failed to read cached tile from disk: $key", e)
                        }
                    }
                }

                // If not in disk cache, fetch over network
                if (data == null) {
                    val url = getEffectiveMapUrlTemplate()
                        .replace("{zoom}", zoom.toString())
                        .replace("{x}", x.toString())
                        .replace("{y}", y.toString())

                    val request = Request.Builder().url(url).build()
                    val response = httpClient.newCall(request).execute()

                    if (!response.isSuccessful) return@withContext null

                    data = response.body?.bytes() ?: return@withContext null

                    // Persist downloaded raw JPEG tile bytes to disk
                    if (assetCache != null) {
                        assetCache.put(tileUuid, AssetType.IMAGE_JPEG, data)
                    }
                    val diskFile = getDiskTileFile(key)
                    if (diskFile != null) {
                        try {
                            diskFile.writeBytes(data)
                        } catch (e: Exception) {
                            Log.w(TAG, "Failed to write tile to disk: $key", e)
                        }
                    }
                }

                val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)

                if (bitmap != null) {
                    mapTiles.put(key, bitmap)
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
        // Parse JSON response from region search
        // Note: Using regex for simplicity - consider using kotlinx.serialization for complex responses
        try {
            val results = mutableListOf<RegionSearchResult>()
            // Simple JSON parsing - look for region objects in the response
            // Format: [{"name": "Region Name", "x": 1000, "y": 1000, ...}, ...]
            REGION_SEARCH_PATTERN.findAll(json).forEach { match ->
                val name = match.groupValues[1]
                val x = match.groupValues[2].toIntOrNull() ?: 0
                val y = match.groupValues[3].toIntOrNull() ?: 0
                results.add(RegionSearchResult(
                    name = name,
                    gridX = x,
                    gridY = y,
                    access = DEFAULT_ACCESS_LEVEL,
                    mapImageId = null
                ))
            }
            return results
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse region search results", e)
            return emptyList()
        }
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
     * Get region info by grid coordinates.
     * Returns cached info if available, otherwise queries the simulator.
     */
    suspend fun getRegionInfoByGrid(x: Int, y: Int): RegionMapInfo? {
        val key = "$x-$y"

        // Return cached if available
        regions[key]?.let { return it }

        // Query region info via capability if available
        return withContext(Dispatchers.IO) {
            try {
                // Compute region handle from grid coordinates
                val regionHandle = (x.toLong() shl 32) or y.toLong()

                // Try MapBlockRequest capability or search by coordinates
                // Most viewers use the map image URL to verify region existence
                val mapUrl = getEffectiveMapUrlTemplate()
                    .replace("{zoom}", "1")
                    .replace("{x}", x.toString())
                    .replace("{y}", y.toString())

                val request = Request.Builder()
                    .url(mapUrl)
                    .head() // Just check if it exists
                    .build()

                val response = httpClient.newCall(request).execute()
                if (response.isSuccessful) {
                    // Region exists, create info from coordinates
                    val info = RegionMapInfo(
                        name = "Region ($x, $y)",
                        gridX = x,
                        gridY = y,
                        regionHandle = regionHandle,
                        access = DEFAULT_ACCESS_LEVEL,
                        mapImageId = null
                    )
                    regions[key] = info
                    info
                } else {
                    null
                }
            } catch (e: Exception) {
                Log.w(TAG, "Failed to query region info at $x, $y: ${e.message}")
                null
            }
        }
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
     * Synchronous cache lookup for `regionHandle → simulator name`. Returns
     * null when nothing is cached so callers can pick a fallback (grid coord
     * string, "Unknown", etc) instead of blocking on the simulator query in
     * `getRegionInfo`. Used by [com.linkpoint.teleport.TeleportManager] so
     * `TeleportEvent.Completed` can ship the real region name when the
     * MapBlockReply / EQG cap has already cached one for that grid square.
     */
    fun getCachedRegionName(regionHandle: Long): String? {
        val (gridX, gridY) = getGridFromHandle(regionHandle)
        return regions["$gridX-$gridY"]?.name?.takeIf { it.isNotEmpty() }
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
                .filter { it.position.distance(myPosition) <= maxDistance }
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
                .sortedBy { it.distance }
                .take(maxResults)
        }
    }

    /**
     * Get memory LRU cache current size in bytes
     */
    fun getMemoryTileCacheSize(): Int = mapTiles.size()

    /**
     * Get memory LRU cache max size in bytes
     */
    fun getMemoryTileCacheMaxSize(): Int = mapTiles.maxSize()

    /**
     * Get bitmap from memory LRU cache directly without network or disk I/O
     */
    fun getMemoryCachedTile(x: Int, y: Int, zoom: Int = ZOOM_REGION): Bitmap? {
        val bitmap = mapTiles.get("$zoom-$x-$y")
        return if (bitmap != null && !bitmap.isRecycled) bitmap else null
    }

    /**
     * Store a tile bitmap directly into the memory LRU cache.
     */
    fun putMemoryCachedTile(x: Int, y: Int, bitmap: Bitmap, zoom: Int = ZOOM_REGION) {
        mapTiles.put("$zoom-$x-$y", bitmap)
    }

    /**
     * Clear cached tiles
     */
    fun clearCache() {
        mapTiles.evictAll()
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
