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
    // Active manifold frame identifier
    @Volatile var activeManifoldFrameId: String = "flat-2d"

    private fun makeCacheKey(gridX: Int, gridY: Int, frameId: String? = null): String {
        val fid = frameId ?: activeManifoldFrameId
        return "$fid-$gridX-$gridY"
    }

    private fun makeTileCacheKey(zoom: Int, x: Int, y: Int, frameId: String? = null): String {
        val fid = frameId ?: activeManifoldFrameId
        return "$zoom-$fid-$x-$y"
    }

    /**
     * Cache region info from MapBlockReply message with frame-aware cache key.
     */
    fun cacheRegionInfo(gridX: Int, gridY: Int, name: String, mapImageId: UUID, frameId: String? = null) {
        val fid = frameId ?: activeManifoldFrameId
        val key = makeCacheKey(gridX, gridY, fid)
        val regionHandle = (gridX.toLong() shl 32) or gridY.toLong()
        val info = RegionMapInfo(
            name = name,
            gridX = gridX,
            gridY = gridY,
            regionHandle = regionHandle,
            access = 0, // Default access level
            mapImageId = mapImageId,
            manifoldFrameId = fid
        )
        regions[key] = info
        regions["$gridX-$gridY"] = info // Also cache legacy key for un-scoped lookup
        try {
            Log.d(TAG, "Cached region info: $name at ($gridX, $gridY) frame=$fid key=$key")
        } catch (_: Throwable) {}
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
            val file = File(dir, "tile_$key.jpg")
            if (file.exists()) return file
            val parts = key.split("-")
            if (parts.size >= 4) {
                val legacyFile = File(dir, "tile_${parts[0]}-${parts[2]}-${parts[3]}.jpg")
                if (legacyFile.exists()) return legacyFile
            }
            return file
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
     * Get effective region search URL for the active grid.
     */
    fun getEffectiveRegionSearchUrl(query: String): String? {
        try {
            val app = com.linkpoint.LinkpointApp.getInstance()
            val grid = app.gridManager.getSelectedGrid()
            val activeSearchUri = app.sessionManager.getSearchUri() ?: grid.searchUri
            if (!activeSearchUri.isNullOrBlank()) {
                val trimmed = activeSearchUri.trim()
                if (trimmed.contains("{query}")) {
                    return trimmed.replace("{query}", query.encodeUrl())
                }
                val joinChar = if (trimmed.contains("?")) "&" else "?"
                if (trimmed.contains("regions")) {
                    return "$trimmed${joinChar}q=${query.encodeUrl()}"
                }
                val base = trimmed.trimEnd('/')
                return "$base/regions?q=${query.encodeUrl()}"
            }
            val loginUri = app.sessionManager.getLoginUri()
            if (com.linkpoint.network.grid.GridInfoResolver.isSecondLifeUri(loginUri) ||
                com.linkpoint.network.grid.GridInfoResolver.isSecondLifeUri(grid.loginUri)) {
                return "https://search.secondlife.com/regions?q=${query.encodeUrl()}"
            }
        } catch (e: Exception) {
            return "https://search.secondlife.com/regions?q=${query.encodeUrl()}"
        }
        return null
    }

    // Map item markers cached by itemType
    private val mapItemMarkers = ConcurrentHashMap<Int, MutableList<com.linkpoint.protocol.messages.AdditionalMessageParsers.MapItemData>>()

    fun handleMapItemReply(reply: com.linkpoint.protocol.messages.AdditionalMessageParsers.MapItemReplyData) {
        val list = mapItemMarkers.getOrPut(reply.itemType) { java.util.Collections.synchronizedList(mutableListOf()) }
        synchronized(list) {
            val newIds = reply.items.map { it.id }.toSet()
            list.removeAll { it.id in newIds }
            list.addAll(reply.items)
        }
        Log.d(TAG, "Updated map item markers for itemType ${reply.itemType}: ${reply.items.size} items stored")
    }

    fun getMapItems(itemType: Int): List<com.linkpoint.protocol.messages.AdditionalMessageParsers.MapItemData> {
        return mapItemMarkers[itemType]?.toList() ?: emptyList()
    }

    fun clearMapItems() {
        mapItemMarkers.clear()
    }

    /**
     * Send UDP MapBlockRequest packet to query region blocks for a coordinate range.
     */
    fun requestMapBlock(minX: Int, maxX: Int, minY: Int, maxY: Int) {
        try {
            val app = com.linkpoint.LinkpointApp.getInstance()
            val udpConnection = app.udpConnection
            val agentId = app.sessionManager.getAgentId() ?: return
            val sessionIdStr = app.sessionManager.getSessionId() ?: return
            val sessionId = try { UUID.fromString(sessionIdStr) } catch (e: Exception) { UUID.randomUUID() }

            val buffer = java.nio.ByteBuffer.allocate(45).order(java.nio.ByteOrder.LITTLE_ENDIAN)
            buffer.putUUID(agentId)
            buffer.putUUID(sessionId)
            buffer.putInt(0) // Flags
            buffer.putInt(0) // EstateID
            buffer.put(0.toByte()) // Godlike

            buffer.putShort(minX.toShort())
            buffer.putShort(maxX.toShort())
            buffer.putShort(minY.toShort())
            buffer.putShort(maxY.toShort())

            udpConnection.sendPacket(
                com.linkpoint.protocol.messages.ids.MessageIdRegistry.MAP_BLOCK_REQUEST,
                buffer.array(),
                reliable = true
            )
            Log.d(TAG, "Sent UDP MapBlockRequest for region range ($minX, $minY) to ($maxX, $maxY)")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send MapBlockRequest UDP packet", e)
        }
    }

    /**
     * Send UDP MapItemRequest packet to query map item overlays.
     */
    fun requestMapItem(itemType: Int, regionHandle: Long) {
        try {
            val app = com.linkpoint.LinkpointApp.getInstance()
            val udpConnection = app.udpConnection
            val agentId = app.sessionManager.getAgentId() ?: return
            val sessionIdStr = app.sessionManager.getSessionId() ?: return
            val sessionId = try { UUID.fromString(sessionIdStr) } catch (e: Exception) { UUID.randomUUID() }

            val buffer = java.nio.ByteBuffer.allocate(49).order(java.nio.ByteOrder.LITTLE_ENDIAN)
            buffer.putUUID(agentId)
            buffer.putUUID(sessionId)
            buffer.putInt(0) // Flags
            buffer.putInt(0) // EstateID
            buffer.put(0.toByte()) // Godlike

            buffer.putInt(itemType)
            buffer.putLong(regionHandle)

            udpConnection.sendPacket(
                com.linkpoint.protocol.messages.ids.MessageIdRegistry.MAP_ITEM_REQUEST,
                buffer.array(),
                reliable = true
            )
            Log.d(TAG, "Sent UDP MapItemRequest for itemType $itemType on handle $regionHandle")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send MapItemRequest UDP packet", e)
        }
    }

    private fun java.nio.ByteBuffer.putUUID(uuid: UUID) {
        val temp = java.nio.ByteBuffer.allocate(16).order(java.nio.ByteOrder.BIG_ENDIAN)
        temp.putLong(uuid.mostSignificantBits)
        temp.putLong(uuid.leastSignificantBits)
        this.put(temp.array())
    }

    /**
     * Get map tile using frame-aware cache key.
     */
    suspend fun getMapTile(x: Int, y: Int, zoom: Int = ZOOM_REGION, frameId: String? = null): Bitmap? {
        val fid = frameId ?: activeManifoldFrameId
        val key = makeTileCacheKey(zoom, x, y, fid)

        // Check memory LRU cache first
        mapTiles.get(key)?.let {
            if (!it.isRecycled) return it
        }
        mapTiles.get("$zoom-$x-$y")?.let {
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
                    val diskFile = getDiskTileFile(key) ?: getDiskTileFile("$zoom-$x-$y")
                    val fileToRead = if (diskFile != null && diskFile.exists()) diskFile else {
                        val legacyFile = getDiskTileFile("$zoom-$x-$y")
                        if (legacyFile != null && legacyFile.exists()) legacyFile else null
                    }
                    if (fileToRead != null) {
                        try {
                            data = fileToRead.readBytes()
                        } catch (e: Exception) {
                            Log.w(TAG, "Failed to read cached tile from disk: ${fileToRead.name}", e)
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
                val url = getEffectiveRegionSearchUrl(query)
                    ?: return@withContext emptyList()
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
    suspend fun getRegionInfoByGrid(x: Int, y: Int, frameId: String? = null): RegionMapInfo? {
        val fid = frameId ?: activeManifoldFrameId
        val key = makeCacheKey(x, y, fid)
        
        // Return cached if available
        regions[key]?.let { return it }
        regions["$x-$y"]?.let { return it }
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

                var exists = false
                try {
                    val request = Request.Builder()
                        .url(mapUrl)
                        .head() // Just check if it exists
                        .build()

                    val response = httpClient.newCall(request).execute()
                    exists = response.isSuccessful
                } catch (e: Exception) {
                    Log.d(TAG, "HTTP tile head request failed for ($x, $y), falling back to UDP MapBlockRequest: ${e.message}")
                }

                if (!exists) {
                    // Fallback to UDP MapBlockRequest when HTTP tile endpoints are unavailable
                    requestMapBlock(x, x, y, y)
                }

                if (exists) {
                    // Region exists, create info from coordinates
                    val info = RegionMapInfo(
                        name = "Region ($x, $y)",
                        gridX = x,
                        gridY = y,
                        regionHandle = regionHandle,
                        access = DEFAULT_ACCESS_LEVEL,
                        mapImageId = null,
                        manifoldFrameId = fid
                    )
                    regions[key] = info
                    info
                } else {
                    regions[key]
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
    fun getCachedRegionName(regionHandle: Long, frameId: String? = null): String? {
        val (gridX, gridY) = getGridFromHandle(regionHandle)
        val fid = frameId ?: activeManifoldFrameId
        return (regions[makeCacheKey(gridX, gridY, fid)] ?: regions["$gridX-$gridY"])?.name?.takeIf { it.isNotEmpty() }
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
    fun getMemoryCachedTile(x: Int, y: Int, zoom: Int = ZOOM_REGION, frameId: String? = null): Bitmap? {
        val fid = frameId ?: activeManifoldFrameId
        val key = makeTileCacheKey(zoom, x, y, fid)
        val bitmap = mapTiles.get(key) ?: mapTiles.get("$zoom-$x-$y")
        return if (bitmap != null && !bitmap.isRecycled) bitmap else null
    }

    /**
     * Store a tile bitmap directly into the memory LRU cache.
     */
    fun putMemoryCachedTile(x: Int, y: Int, bitmap: Bitmap, zoom: Int = ZOOM_REGION, frameId: String? = null) {
        val fid = frameId ?: activeManifoldFrameId
        val key = makeTileCacheKey(zoom, x, y, fid)
        mapTiles.put(key, bitmap)
        mapTiles.put("$zoom-$x-$y", bitmap)
    }

    /**
     * Clear cached tiles and overlay markers
     */
    fun clearCache() {
        mapTiles.evictAll()
        clearMapItems()
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
    val mapImageId: UUID?,
    val manifoldFrameId: String = "flat-2d"
)

data class RegionSearchResult(
    val name: String,
    val gridX: Int,
    val gridY: Int,
    val access: Int,
    val mapImageId: UUID?
)
