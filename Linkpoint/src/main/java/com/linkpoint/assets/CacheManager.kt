package com.linkpoint.assets

import android.content.Context
import android.content.SharedPreferences
import android.os.Environment
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Centralized cache management with configurable sizes
 * 
 * Based on the reference viewer's cache settings, this provides:
 * - Configurable disk cache size (128MB - 10GB)
 * - Configurable memory cache size (100MB - 2GB)  
 * - Per-asset-type cache size limits (textures, meshes, sounds, animations)
 * - Cache location selection (internal vs external storage)
 * - RAM dedication settings for texture memory
 * - Cache statistics and monitoring
 * - One-tap cache clearing
 * - Cache health monitoring to prevent cache-related failures
 */
class CacheManager(private val context: Context) {
    
    companion object {
        private const val TAG = "CacheManager"
        private const val PREFS_NAME = "cache_settings"
        
        // Keys for preferences
        const val KEY_DISK_CACHE_SIZE_MB = "disk_cache_size_mb"
        const val KEY_MEMORY_CACHE_SIZE_MB = "memory_cache_size_mb"
        const val KEY_AUTO_CLEAR_ON_LOW_SPACE = "auto_clear_low_space"
        const val KEY_CACHE_TEXTURES = "cache_textures"
        const val KEY_CACHE_MESHES = "cache_meshes"
        const val KEY_CACHE_SOUNDS = "cache_sounds"
        const val KEY_CACHE_ANIMATIONS = "cache_animations"
        
        // Per-asset-type size limit keys
        const val KEY_TEXTURE_CACHE_SIZE_MB = "texture_cache_size_mb"
        const val KEY_MESH_CACHE_SIZE_MB = "mesh_cache_size_mb"
        const val KEY_SOUND_CACHE_SIZE_MB = "sound_cache_size_mb"
        const val KEY_ANIMATION_CACHE_SIZE_MB = "animation_cache_size_mb"
        
        // Cache location key
        const val KEY_CACHE_LOCATION = "cache_location"
        
        // Custom cache path key (for user-specified location)
        const val KEY_CUSTOM_CACHE_PATH = "custom_cache_path"
        
        // Texture memory (RAM) dedication key
        const val KEY_TEXTURE_MEMORY_MB = "texture_memory_mb"
        
        // Size limits - generous limits for smooth performance
        // Users can allocate up to 50GB for total disk cache
        const val MIN_DISK_CACHE_MB = 128
        const val MAX_DISK_CACHE_MB = 51200  // 50GB - for users with large storage
        const val DEFAULT_DISK_CACHE_MB = 4096  // 4GB default for good performance
        
        const val MIN_MEMORY_CACHE_MB = 100
        const val MAX_MEMORY_CACHE_MB = 4096  // 4GB - for devices with plenty of RAM
        const val DEFAULT_MEMORY_CACHE_MB = 512  // 512MB default - good balance
        
        // Per-asset-type size limits - each can be several GB
        const val MIN_TEXTURE_CACHE_MB = 64
        const val MAX_TEXTURE_CACHE_MB = 20480   // 20GB max for textures
        const val DEFAULT_TEXTURE_CACHE_MB = 2048  // 2GB for textures
        
        const val MIN_MESH_CACHE_MB = 32
        const val MAX_MESH_CACHE_MB = 10240      // 10GB max for meshes
        const val DEFAULT_MESH_CACHE_MB = 1024   // 1GB for meshes
        
        const val MIN_SOUND_CACHE_MB = 16
        const val MAX_SOUND_CACHE_MB = 4096      // 4GB max for sounds
        const val DEFAULT_SOUND_CACHE_MB = 512   // 512MB for sounds
        
        const val MIN_ANIMATION_CACHE_MB = 16
        const val MAX_ANIMATION_CACHE_MB = 4096  // 4GB max for animations
        const val DEFAULT_ANIMATION_CACHE_MB = 512 // 512MB for animations
        
        // Texture memory (RAM) limits - how much RAM for decoded textures
        const val MIN_TEXTURE_MEMORY_MB = 64
        const val MAX_TEXTURE_MEMORY_MB = 2048  // 2GB max for texture RAM
        const val DEFAULT_TEXTURE_MEMORY_MB = 512  // 512MB default
        
        // Low space threshold (500MB free required)
        const val LOW_SPACE_THRESHOLD_MB = 500
        
        // Linkpoint Cache structure directories
        // Root: "Linkpoint" on external storage (/storage/0/Linkpoint/)
        const val LINKPOINT_CACHE_ROOT = "Linkpoint"
        
        // Public cache - shared data per grid (textures, sounds, meshes, animations)
        const val PUBLIC_DIR = "Public"
        
        // Private cache - user-specific data per grid (messages, friends, settings)
        const val PRIVATE_DIR = "Private"
        
        // Cache subdirectories (under Public/<Grid>/)
        private const val TEXTURES_DIR = "textures"
        private const val MESHES_DIR = "meshes"
        private const val SOUNDS_DIR = "sounds"
        private const val ANIMATIONS_DIR = "animations"
        private const val GENERAL_DIR = "asset_cache"
        
        // Private subdirectories (under Private/<Grid>/<UserID>/)
        private const val MESSAGES_DIR = "messages"
        private const val FRIENDS_DIR = "friends"
        private const val SETTINGS_DIR = "settings"
        private const val INVENTORY_DIR = "inventory"
        
        // Cache location options
        const val LOCATION_INTERNAL = "internal"
        // Public (legacy) external storage. Kept for users who previously opted in via
        // MANAGE_EXTERNAL_STORAGE; on targetSdk 30+ without that permission, writes to
        // Environment.getExternalStorageDirectory() silently fail — that was the cause
        // of the "0 B disk cache" observation in the 2026-04-25 capture.
        const val LOCATION_EXTERNAL = "external"
        const val LOCATION_CUSTOM = "custom"
        // App-scoped external Documents — the default. Resolves to
        // /storage/emulated/0/Android/data/<pkg>/files/Documents/Linkpoint/, which is
        // visible in Files apps as "Documents/Linkpoint" on the device, and writable
        // without WRITE_EXTERNAL_STORAGE on every supported Android version.
        const val LOCATION_DOCUMENTS = "documents"

        // Default external path for Linkpoint Cache
        val DEFAULT_EXTERNAL_CACHE_PATH: String
            get() = "${Environment.getExternalStorageDirectory().absolutePath}/$LINKPOINT_CACHE_ROOT"
    }
    
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    // Current grid name (e.g., "Agni", "Aditi")
    private var currentGridName: String = "SecondLife"
    
    // Current user ID (UUID string)
    private var currentUserId: String? = null
    
    /**
     * Set the current grid name for cache path resolution.
     * Call this when connecting to a grid.
     */
    fun setCurrentGrid(gridName: String) {
        currentGridName = gridName.replace(Regex("[^a-zA-Z0-9_-]"), "_") // Sanitize for file system
        Log.i(TAG, "Current grid set to: $currentGridName")
    }
    
    /**
     * Set the current user ID for private cache path resolution.
     * Call this after login.
     */
    fun setCurrentUser(userId: String?) {
        currentUserId = userId?.replace(Regex("[^a-zA-Z0-9_-]"), "_") // Sanitize for file system
        Log.i(TAG, "Current user set to: ${currentUserId ?: "none"}")
    }
    
    /**
     * Get current grid name
     */
    fun getCurrentGridName(): String = currentGridName
    
    /**
     * Get current user ID
     */
    fun getCurrentUserId(): String? = currentUserId
    
    // ========== Total Cache Size Settings ==========
    
    /**
     * Get configured disk cache size in MB
     */
    fun getDiskCacheSizeMB(): Int {
        return prefs.getInt(KEY_DISK_CACHE_SIZE_MB, DEFAULT_DISK_CACHE_MB)
    }
    
    /**
     * Set disk cache size in MB
     */
    fun setDiskCacheSizeMB(sizeMB: Int) {
        val clampedSize = sizeMB.coerceIn(MIN_DISK_CACHE_MB, MAX_DISK_CACHE_MB)
        prefs.edit().putInt(KEY_DISK_CACHE_SIZE_MB, clampedSize).apply()
        Log.i(TAG, "Disk cache size set to ${clampedSize}MB")
    }
    
    /**
     * Get configured memory cache size in MB
     */
    fun getMemoryCacheSizeMB(): Int {
        return prefs.getInt(KEY_MEMORY_CACHE_SIZE_MB, DEFAULT_MEMORY_CACHE_MB)
    }
    
    /**
     * Set memory cache size in MB
     */
    fun setMemoryCacheSizeMB(sizeMB: Int) {
        val clampedSize = sizeMB.coerceIn(MIN_MEMORY_CACHE_MB, MAX_MEMORY_CACHE_MB)
        prefs.edit().putInt(KEY_MEMORY_CACHE_SIZE_MB, clampedSize).apply()
        Log.i(TAG, "Memory cache size set to ${clampedSize}MB")
    }
    
    // ========== Per-Asset-Type Cache Size Settings ==========
    
    /**
     * Get texture cache size limit in MB
     */
    fun getTextureCacheSizeMB(): Int {
        return prefs.getInt(KEY_TEXTURE_CACHE_SIZE_MB, DEFAULT_TEXTURE_CACHE_MB)
    }
    
    /**
     * Set texture cache size limit in MB
     */
    fun setTextureCacheSizeMB(sizeMB: Int) {
        val clampedSize = sizeMB.coerceIn(MIN_TEXTURE_CACHE_MB, MAX_TEXTURE_CACHE_MB)
        prefs.edit().putInt(KEY_TEXTURE_CACHE_SIZE_MB, clampedSize).apply()
        Log.i(TAG, "Texture cache size set to ${clampedSize}MB")
    }
    
    /**
     * Get mesh cache size limit in MB
     */
    fun getMeshCacheSizeMB(): Int {
        return prefs.getInt(KEY_MESH_CACHE_SIZE_MB, DEFAULT_MESH_CACHE_MB)
    }
    
    /**
     * Set mesh cache size limit in MB
     */
    fun setMeshCacheSizeMB(sizeMB: Int) {
        val clampedSize = sizeMB.coerceIn(MIN_MESH_CACHE_MB, MAX_MESH_CACHE_MB)
        prefs.edit().putInt(KEY_MESH_CACHE_SIZE_MB, clampedSize).apply()
        Log.i(TAG, "Mesh cache size set to ${clampedSize}MB")
    }
    
    /**
     * Get sound cache size limit in MB
     */
    fun getSoundCacheSizeMB(): Int {
        return prefs.getInt(KEY_SOUND_CACHE_SIZE_MB, DEFAULT_SOUND_CACHE_MB)
    }
    
    /**
     * Set sound cache size limit in MB
     */
    fun setSoundCacheSizeMB(sizeMB: Int) {
        val clampedSize = sizeMB.coerceIn(MIN_SOUND_CACHE_MB, MAX_SOUND_CACHE_MB)
        prefs.edit().putInt(KEY_SOUND_CACHE_SIZE_MB, clampedSize).apply()
        Log.i(TAG, "Sound cache size set to ${clampedSize}MB")
    }
    
    /**
     * Get animation cache size limit in MB
     */
    fun getAnimationCacheSizeMB(): Int {
        return prefs.getInt(KEY_ANIMATION_CACHE_SIZE_MB, DEFAULT_ANIMATION_CACHE_MB)
    }
    
    /**
     * Set animation cache size limit in MB
     */
    fun setAnimationCacheSizeMB(sizeMB: Int) {
        val clampedSize = sizeMB.coerceIn(MIN_ANIMATION_CACHE_MB, MAX_ANIMATION_CACHE_MB)
        prefs.edit().putInt(KEY_ANIMATION_CACHE_SIZE_MB, clampedSize).apply()
        Log.i(TAG, "Animation cache size set to ${clampedSize}MB")
    }
    
    // ========== Texture Memory (RAM) Dedication ==========
    
    /**
     * Get texture memory (RAM) dedication in MB.
     * This is separate from disk cache - it's how much RAM to use for decoded textures.
     */
    fun getTextureMemoryMB(): Int {
        return prefs.getInt(KEY_TEXTURE_MEMORY_MB, DEFAULT_TEXTURE_MEMORY_MB)
    }
    
    /**
     * Set texture memory (RAM) dedication in MB.
     * Higher values = more textures kept in RAM = faster rendering but more memory.
     */
    fun setTextureMemoryMB(sizeMB: Int) {
        val clampedSize = sizeMB.coerceIn(MIN_TEXTURE_MEMORY_MB, MAX_TEXTURE_MEMORY_MB)
        prefs.edit().putInt(KEY_TEXTURE_MEMORY_MB, clampedSize).apply()
        Log.i(TAG, "Texture memory set to ${clampedSize}MB")
    }
    
    // ========== Cache Location Settings ==========
    
    /**
     * Get the configured cache location.
     * Returns "documents" (default), "internal", "external", or "custom".
     */
    fun getCacheLocation(): String {
        return prefs.getString(KEY_CACHE_LOCATION, LOCATION_DOCUMENTS) ?: LOCATION_DOCUMENTS
    }

    /**
     * Set the cache location.
     * Note: Changing this requires app restart to take effect.
     */
    fun setCacheLocation(location: String) {
        if (location != LOCATION_INTERNAL && location != LOCATION_EXTERNAL &&
            location != LOCATION_CUSTOM && location != LOCATION_DOCUMENTS) {
            Log.w(TAG, "Invalid cache location: $location, using documents")
            prefs.edit().putString(KEY_CACHE_LOCATION, LOCATION_DOCUMENTS).apply()
            return
        }
        prefs.edit().putString(KEY_CACHE_LOCATION, location).apply()
        Log.i(TAG, "Cache location set to $location (requires restart)")
    }
    
    /**
     * Get the custom cache path (user-specified location).
     */
    fun getCustomCachePath(): String {
        return prefs.getString(KEY_CUSTOM_CACHE_PATH, DEFAULT_EXTERNAL_CACHE_PATH) ?: DEFAULT_EXTERNAL_CACHE_PATH
    }
    
    /**
     * Set the custom cache path.
     * @param path The absolute path to use for cache (e.g., "/sdcard/Linkpoint")
     */
    fun setCustomCachePath(path: String) {
        prefs.edit().putString(KEY_CUSTOM_CACHE_PATH, path).apply()
        Log.i(TAG, "Custom cache path set to: $path")
    }
    
    /**
     * Check if external storage is available for caching.
     */
    fun isExternalStorageAvailable(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }
    
    /**
     * Get the Linkpoint Cache root directory.
     * Structure: <root>/Linkpoint/
     *
     * Default ([LOCATION_DOCUMENTS]) is `Android/data/<pkg>/files/Documents/Linkpoint/`,
     * which appears in the device Files app as `Documents/Linkpoint`. This is the only
     * external-visible location writable without [android.Manifest.permission.MANAGE_EXTERNAL_STORAGE]
     * on Android 11+, and it's where the existing crash/log subsystem already lives
     * (see CrashReporter / NetworkLogger).
     */
    fun getLinkpointCacheRoot(): File {
        val rootDir = when (getCacheLocation()) {
            LOCATION_CUSTOM -> File(getCustomCachePath())
            LOCATION_DOCUMENTS -> getDocumentsCacheRoot()
            LOCATION_EXTERNAL -> {
                // Legacy path for users who previously toggled "external" with the
                // MANAGE_EXTERNAL_STORAGE permission. Falls back to the documents
                // directory rather than internal so the user keeps a visible cache.
                if (isExternalStorageAvailable()) {
                    File(Environment.getExternalStorageDirectory(), LINKPOINT_CACHE_ROOT)
                } else {
                    Log.w(TAG, "External storage not available, falling back to Documents")
                    getDocumentsCacheRoot()
                }
            }
            else -> File(context.filesDir, LINKPOINT_CACHE_ROOT)
        }
        if (!rootDir.exists()) {
            rootDir.mkdirs()
        }
        return rootDir
    }

    /**
     * Resolve the app-scoped Documents/Linkpoint directory. Falls back to internal
     * storage only if [Context.getExternalFilesDir] returns null (the device has no
     * usable external volume — extremely rare but possible during boot or with a
     * removed SD card).
     */
    private fun getDocumentsCacheRoot(): File {
        val docsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        return if (docsDir != null) {
            File(docsDir, LINKPOINT_CACHE_ROOT)
        } else {
            Log.w(TAG, "External files dir unavailable; using internal storage for cache")
            File(context.filesDir, LINKPOINT_CACHE_ROOT)
        }
    }
    
    /**
     * Get the Public cache directory for the current grid.
     * Structure: Linkpoint/Public/<GridName>/
     * 
     * Public cache holds shared data: textures, sounds, meshes, animations.
     * This data is not user-specific and can be shared across users.
     */
    fun getPublicCacheDirectory(): File {
        val publicDir = File(getLinkpointCacheRoot(), "$PUBLIC_DIR/$currentGridName")
        if (!publicDir.exists()) {
            publicDir.mkdirs()
        }
        return publicDir
    }
    
    /**
     * Get the Private cache directory for the current user on the current grid.
     * Structure: Linkpoint/Private/<GridName>/<UserID>/
     * 
     * Private cache holds user-specific data: messages, friends lists, settings.
     */
    fun getPrivateCacheDirectory(): File {
        val userId = currentUserId ?: "anonymous"
        val privateDir = File(getLinkpointCacheRoot(), "$PRIVATE_DIR/$currentGridName/$userId")
        if (!privateDir.exists()) {
            privateDir.mkdirs()
        }
        return privateDir
    }
    
    /**
     * Get the base cache directory based on current location setting.
     * This is now the Linkpoint Cache root for backward compatibility.
     */
    fun getBaseCacheDirectory(): File {
        return getLinkpointCacheRoot()
    }
    
    /**
     * Get public cache directory for a specific asset type.
     * Structure: Linkpoint/Public/<GridName>/<assetType>/
     */
    fun getPublicAssetDirectory(assetType: CacheableAssetType): File {
        val dirName = when (assetType) {
            CacheableAssetType.TEXTURES -> TEXTURES_DIR
            CacheableAssetType.MESHES -> MESHES_DIR
            CacheableAssetType.SOUNDS -> SOUNDS_DIR
            CacheableAssetType.ANIMATIONS -> ANIMATIONS_DIR
        }
        val dir = File(getPublicCacheDirectory(), dirName)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }
    
    /**
     * Get private cache directory for a specific data type.
     * Structure: Linkpoint/Private/<GridName>/<UserID>/<dataType>/
     */
    fun getPrivateDataDirectory(dataType: PrivateDataType): File {
        val dirName = when (dataType) {
            PrivateDataType.MESSAGES -> MESSAGES_DIR
            PrivateDataType.FRIENDS -> FRIENDS_DIR
            PrivateDataType.SETTINGS -> SETTINGS_DIR
            PrivateDataType.INVENTORY -> INVENTORY_DIR
        }
        val dir = File(getPrivateCacheDirectory(), dirName)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }
    
    /**
     * Get available cache locations with their details.
     */
    fun getAvailableCacheLocations(): List<CacheLocationInfo> {
        val locations = mutableListOf<CacheLocationInfo>()

        // Documents (default): Android/data/<pkg>/files/Documents/Linkpoint
        try {
            val docsDir = getDocumentsCacheRoot()
            val docsStatFs = android.os.StatFs(docsDir.parentFile?.path ?: context.filesDir.path)
            locations.add(CacheLocationInfo(
                id = LOCATION_DOCUMENTS,
                name = "Documents/Linkpoint (recommended)",
                path = docsDir.absolutePath,
                availableBytes = docsStatFs.availableBytes,
                totalBytes = docsStatFs.totalBytes,
                isAvailable = true
            ))
        } catch (e: Exception) {
            Log.w(TAG, "Failed to get Documents storage stats", e)
        }

        // Internal storage
        val internalDir = File(context.filesDir, LINKPOINT_CACHE_ROOT)
        try {
            val internalStatFs = android.os.StatFs(context.filesDir.path)
            locations.add(CacheLocationInfo(
                id = LOCATION_INTERNAL,
                name = "Internal Storage",
                path = internalDir.absolutePath,
                availableBytes = internalStatFs.availableBytes,
                totalBytes = internalStatFs.totalBytes,
                isAvailable = true
            ))
        } catch (e: Exception) {
            Log.w(TAG, "Failed to get internal storage stats", e)
        }
        
        // External storage (Linkpoint Cache on sdcard)
        if (isExternalStorageAvailable()) {
            val externalDir = File(Environment.getExternalStorageDirectory(), LINKPOINT_CACHE_ROOT)
            try {
                val externalStatFs = android.os.StatFs(Environment.getExternalStorageDirectory().path)
                locations.add(CacheLocationInfo(
                    id = LOCATION_EXTERNAL,
                    name = "External Storage (SD Card)",
                    path = externalDir.absolutePath,
                    availableBytes = externalStatFs.availableBytes,
                    totalBytes = externalStatFs.totalBytes,
                    isAvailable = true
                ))
            } catch (e: Exception) {
                Log.w(TAG, "Failed to get external storage stats", e)
            }
        } else {
            locations.add(CacheLocationInfo(
                id = LOCATION_EXTERNAL,
                name = "External Storage (Not Available)",
                path = "",
                availableBytes = 0,
                totalBytes = 0,
                isAvailable = false
            ))
        }
        
        // Custom location
        val customPath = getCustomCachePath()
        val customDir = File(customPath)
        try {
            if (customDir.exists() || customDir.parentFile?.exists() == true) {
                val parentPath = if (customDir.exists()) customDir.path else customDir.parentFile?.path ?: customPath
                val customStatFs = android.os.StatFs(parentPath)
                locations.add(CacheLocationInfo(
                    id = LOCATION_CUSTOM,
                    name = "Custom Location",
                    path = customPath,
                    availableBytes = customStatFs.availableBytes,
                    totalBytes = customStatFs.totalBytes,
                    isAvailable = true
                ))
            } else {
                locations.add(CacheLocationInfo(
                    id = LOCATION_CUSTOM,
                    name = "Custom Location",
                    path = customPath,
                    availableBytes = 0,
                    totalBytes = 0,
                    isAvailable = false
                ))
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to get custom location stats", e)
            locations.add(CacheLocationInfo(
                id = LOCATION_CUSTOM,
                name = "Custom Location",
                path = customPath,
                availableBytes = 0,
                totalBytes = 0,
                isAvailable = false
            ))
        }
        
        return locations
    }
    
    // ========== Auto-Clear Settings ==========
    
    /**
     * Check if auto-clear on low space is enabled
     */
    fun isAutoClearOnLowSpaceEnabled(): Boolean {
        return prefs.getBoolean(KEY_AUTO_CLEAR_ON_LOW_SPACE, true)
    }
    
    /**
     * Set auto-clear on low space
     */
    fun setAutoClearOnLowSpace(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_AUTO_CLEAR_ON_LOW_SPACE, enabled).apply()
    }
    
    // ========== Per-Asset-Type Caching Enable/Disable ==========
    
    /**
     * Check if specific asset type caching is enabled
     */
    fun isCacheEnabled(assetType: CacheableAssetType): Boolean {
        val key = when (assetType) {
            CacheableAssetType.TEXTURES -> KEY_CACHE_TEXTURES
            CacheableAssetType.MESHES -> KEY_CACHE_MESHES
            CacheableAssetType.SOUNDS -> KEY_CACHE_SOUNDS
            CacheableAssetType.ANIMATIONS -> KEY_CACHE_ANIMATIONS
        }
        return prefs.getBoolean(key, true)
    }
    
    /**
     * Enable/disable caching for specific asset type
     */
    fun setCacheEnabled(assetType: CacheableAssetType, enabled: Boolean) {
        val key = when (assetType) {
            CacheableAssetType.TEXTURES -> KEY_CACHE_TEXTURES
            CacheableAssetType.MESHES -> KEY_CACHE_MESHES
            CacheableAssetType.SOUNDS -> KEY_CACHE_SOUNDS
            CacheableAssetType.ANIMATIONS -> KEY_CACHE_ANIMATIONS
        }
        prefs.edit().putBoolean(key, enabled).apply()
    }
    
    /**
     * Get comprehensive cache statistics for the current grid.
     * Scans both Public and Private cache directories.
     */
    suspend fun getCacheStats(): CacheStatistics = withContext(Dispatchers.IO) {
        // Public cache directories (shared per grid)
        val texturesDir = getPublicAssetDirectory(CacheableAssetType.TEXTURES)
        val meshesDir = getPublicAssetDirectory(CacheableAssetType.MESHES)
        val soundsDir = getPublicAssetDirectory(CacheableAssetType.SOUNDS)
        val animationsDir = getPublicAssetDirectory(CacheableAssetType.ANIMATIONS)
        val generalDir = File(getPublicCacheDirectory(), GENERAL_DIR)
        
        val texturesSize = calculateDirectorySize(texturesDir)
        val meshesSize = calculateDirectorySize(meshesDir)
        val soundsSize = calculateDirectorySize(soundsDir)
        val animationsSize = calculateDirectorySize(animationsDir)
        val generalSize = calculateDirectorySize(generalDir)
        
        val texturesCount = countFiles(texturesDir)
        val meshesCount = countFiles(meshesDir)
        val soundsCount = countFiles(soundsDir)
        val animationsCount = countFiles(animationsDir)
        val generalCount = countFiles(generalDir)
        
        // Also include private cache size
        val privateSize = calculateDirectorySize(getPrivateCacheDirectory())
        
        val totalSize = texturesSize + meshesSize + soundsSize + animationsSize + generalSize + privateSize
        val totalCount = texturesCount + meshesCount + soundsCount + animationsCount + generalCount
        
        val maxSize = getDiskCacheSizeMB().toLong() * 1024 * 1024
        val usagePercent = if (maxSize > 0) (totalSize.toFloat() / maxSize * 100).toInt() else 0
        
        // Get available space from cache root
        val cacheRoot = getLinkpointCacheRoot()
        val statFs = try {
            if (cacheRoot.exists()) {
                android.os.StatFs(cacheRoot.path)
            } else {
                android.os.StatFs(context.filesDir.path)
            }
        } catch (e: Exception) {
            android.os.StatFs(context.filesDir.path)
        }
        val availableSpace = statFs.availableBytes
        
        CacheStatistics(
            totalSizeBytes = totalSize,
            totalFileCount = totalCount,
            texturesSizeBytes = texturesSize,
            texturesCount = texturesCount,
            meshesSizeBytes = meshesSize,
            meshesCount = meshesCount,
            soundsSizeBytes = soundsSize,
            soundsCount = soundsCount,
            animationsSizeBytes = animationsSize,
            animationsCount = animationsCount,
            generalSizeBytes = generalSize,
            generalCount = generalCount,
            maxSizeBytes = maxSize,
            usagePercent = usagePercent,
            availableSpaceBytes = availableSpace,
            isLowSpace = availableSpace < LOW_SPACE_THRESHOLD_MB * 1024 * 1024
        )
    }
    
    /**
     * Clear all cache (both public and private for current grid)
     */
    suspend fun clearAllCache(): ClearResult = withContext(Dispatchers.IO) {
        var clearedBytes = 0L
        var clearedFiles = 0
        var errors = 0
        
        // Clear public cache directories for current grid
        val publicDirectories = listOf(
            getPublicAssetDirectory(CacheableAssetType.TEXTURES),
            getPublicAssetDirectory(CacheableAssetType.MESHES),
            getPublicAssetDirectory(CacheableAssetType.SOUNDS),
            getPublicAssetDirectory(CacheableAssetType.ANIMATIONS),
            File(getPublicCacheDirectory(), GENERAL_DIR)
        )
        
        for (dir in publicDirectories) {
            val result = clearDirectory(dir)
            clearedBytes += result.first
            clearedFiles += result.second
            errors += result.third
        }
        
        // Clear private cache for current user
        val privateResult = clearDirectory(getPrivateCacheDirectory())
        clearedBytes += privateResult.first
        clearedFiles += privateResult.second
        errors += privateResult.third
        
        Log.i(TAG, "Cleared ${formatSize(clearedBytes)} ($clearedFiles files), $errors errors")
        
        ClearResult(
            clearedBytes = clearedBytes,
            clearedFiles = clearedFiles,
            errors = errors,
            success = errors == 0
        )
    }
    
    /**
     * Clear specific cache type (public cache)
     */
    suspend fun clearCache(assetType: CacheableAssetType): ClearResult = withContext(Dispatchers.IO) {
        val dir = getPublicAssetDirectory(assetType)
        val result = clearDirectory(dir)
        
        Log.i(TAG, "Cleared ${assetType.name}: ${formatSize(result.first)} (${result.second} files)")
        
        ClearResult(
            clearedBytes = result.first,
            clearedFiles = result.second,
            errors = result.third,
            success = result.third == 0
        )
    }
    
    /**
     * Clear private cache for current user
     */
    suspend fun clearPrivateCache(): ClearResult = withContext(Dispatchers.IO) {
        val result = clearDirectory(getPrivateCacheDirectory())
        
        Log.i(TAG, "Cleared private cache: ${formatSize(result.first)} (${result.second} files)")
        
        ClearResult(
            clearedBytes = result.first,
            clearedFiles = result.second,
            errors = result.third,
            success = result.third == 0
        )
    }
    
    /**
     * Prune cache to configured size limit
     */
    suspend fun pruneCache(): PruneResult = withContext(Dispatchers.IO) {
        val maxBytes = getDiskCacheSizeMB().toLong() * 1024 * 1024
        val stats = getCacheStats()
        
        if (stats.totalSizeBytes <= maxBytes) {
            return@withContext PruneResult(
                prunedBytes = 0,
                prunedFiles = 0,
                needed = false
            )
        }
        
        // Need to prune - delete oldest files first from public cache
        val targetSize = (maxBytes * 0.8).toLong() // Prune to 80% capacity
        var currentSize = stats.totalSizeBytes
        var prunedBytes = 0L
        var prunedFiles = 0
        
        val allCacheFiles = mutableListOf<File>()
        // Collect files from public cache (don't prune private user data)
        CacheableAssetType.values().forEach { assetType ->
            val dir = getPublicAssetDirectory(assetType)
            dir.listFiles()?.let { allCacheFiles.addAll(it) }
        }
        File(getPublicCacheDirectory(), GENERAL_DIR).listFiles()?.let { allCacheFiles.addAll(it) }
        
        // Sort by last modified (oldest first)
        allCacheFiles.sortBy { it.lastModified() }
        
        for (file in allCacheFiles) {
            if (currentSize <= targetSize) break
            
            val fileSize = file.length()
            if (file.delete()) {
                currentSize -= fileSize
                prunedBytes += fileSize
                prunedFiles++
            }
        }
        
        Log.i(TAG, "Pruned cache: ${formatSize(prunedBytes)} ($prunedFiles files)")
        
        PruneResult(
            prunedBytes = prunedBytes,
            prunedFiles = prunedFiles,
            needed = true
        )
    }
    
    /**
     * Check cache health and auto-prune if needed
     */
    suspend fun checkAndMaintainCache(): MaintenanceResult = withContext(Dispatchers.IO) {
        val stats = getCacheStats()
        var actionTaken = "None"
        var prunedBytes = 0L
        
        // Check for low space - use less aggressive pruning instead of clearing everything
        if (stats.isLowSpace && isAutoClearOnLowSpaceEnabled()) {
            // Prune to 50% instead of clearing everything to preserve recently used assets
            val pruneResult = pruneCache()
            actionTaken = "Auto-pruned due to low space"
            prunedBytes = pruneResult.prunedBytes
            Log.w(TAG, "Low space detected - pruned cache: ${formatSize(prunedBytes)}")
        } 
        // Check if over limit
        else if (stats.usagePercent > 100) {
            val pruneResult = pruneCache()
            actionTaken = "Pruned to 80% capacity"
            prunedBytes = pruneResult.prunedBytes
        }
        // Check if approaching limit
        else if (stats.usagePercent > 90) {
            val pruneResult = pruneCache()
            actionTaken = "Preemptive prune (>90% full)"
            prunedBytes = pruneResult.prunedBytes
        }
        
        MaintenanceResult(
            actionTaken = actionTaken,
            prunedBytes = prunedBytes,
            cacheHealthy = !stats.isLowSpace && stats.usagePercent < 100
        )
    }
    
    /**
     * Get cache directory for specific asset type.
     * Returns the public cache directory for the current grid.
     */
    fun getCacheDirectory(assetType: CacheableAssetType): File {
        return getPublicAssetDirectory(assetType)
    }
    
    private fun calculateDirectorySize(dir: File): Long {
        if (!dir.exists()) return 0
        return dir.walkTopDown().filter { it.isFile }.sumOf { it.length() }
    }
    
    private fun countFiles(dir: File): Int {
        if (!dir.exists()) return 0
        return dir.listFiles()?.size ?: 0
    }
    
    private fun clearDirectory(dir: File): Triple<Long, Int, Int> {
        var clearedBytes = 0L
        var clearedFiles = 0
        var errors = 0
        
        if (!dir.exists()) return Triple(0, 0, 0)
        
        dir.listFiles()?.forEach { file ->
            val size = file.length()
            if (file.delete()) {
                clearedBytes += size
                clearedFiles++
            } else {
                errors++
            }
        }
        
        return Triple(clearedBytes, clearedFiles, errors)
    }
    
    /**
     * Format byte size to human-readable string
     */
    fun formatSize(bytes: Long): String {
        return when {
            bytes >= 1024 * 1024 * 1024 -> String.format("%.2f GB", bytes / (1024.0 * 1024 * 1024))
            bytes >= 1024 * 1024 -> String.format("%.1f MB", bytes / (1024.0 * 1024))
            bytes >= 1024 -> String.format("%.1f KB", bytes / 1024.0)
            else -> "$bytes B"
        }
    }
}

enum class CacheableAssetType {
    TEXTURES,
    MESHES,
    SOUNDS,
    ANIMATIONS
}

data class CacheStatistics(
    val totalSizeBytes: Long,
    val totalFileCount: Int,
    val texturesSizeBytes: Long,
    val texturesCount: Int,
    val meshesSizeBytes: Long,
    val meshesCount: Int,
    val soundsSizeBytes: Long,
    val soundsCount: Int,
    val animationsSizeBytes: Long,
    val animationsCount: Int,
    val generalSizeBytes: Long,
    val generalCount: Int,
    val maxSizeBytes: Long,
    val usagePercent: Int,
    val availableSpaceBytes: Long,
    val isLowSpace: Boolean
) {
    fun getFormattedTotalSize(): String {
        return when {
            totalSizeBytes >= 1024 * 1024 * 1024 -> String.format("%.2f GB", totalSizeBytes / (1024.0 * 1024 * 1024))
            totalSizeBytes >= 1024 * 1024 -> String.format("%.1f MB", totalSizeBytes / (1024.0 * 1024))
            else -> String.format("%.1f KB", totalSizeBytes / 1024.0)
        }
    }
    
    fun getFormattedMaxSize(): String {
        return when {
            maxSizeBytes >= 1024 * 1024 * 1024 -> String.format("%.1f GB", maxSizeBytes / (1024.0 * 1024 * 1024))
            else -> String.format("%.0f MB", maxSizeBytes / (1024.0 * 1024))
        }
    }
}

data class ClearResult(
    val clearedBytes: Long,
    val clearedFiles: Int,
    val errors: Int,
    val success: Boolean
)

data class PruneResult(
    val prunedBytes: Long,
    val prunedFiles: Int,
    val needed: Boolean
)

data class MaintenanceResult(
    val actionTaken: String,
    val prunedBytes: Long,
    val cacheHealthy: Boolean
)

/**
 * Information about a cache location option.
 * Used in the cache location selection UI.
 */
data class CacheLocationInfo(
    val id: String,
    val name: String,
    val path: String,
    val availableBytes: Long,
    val totalBytes: Long,
    val isAvailable: Boolean
) {
    /**
     * Get formatted available space string
     */
    fun getFormattedAvailableSpace(): String {
        return when {
            availableBytes >= 1024 * 1024 * 1024 -> String.format("%.1f GB free", availableBytes / (1024.0 * 1024 * 1024))
            availableBytes >= 1024 * 1024 -> String.format("%.0f MB free", availableBytes / (1024.0 * 1024))
            else -> "Not available"
        }
    }
    
    /**
     * Get display text for UI
     */
    fun getDisplayText(): String {
        return if (isAvailable) {
            "$name (${getFormattedAvailableSpace()})"
        } else {
            name
        }
    }
}

/**
 * Types of private user data stored in the private cache.
 * Private cache structure: Linkpoint/Private/<Grid>/<UserID>/<DataType>/
 */
enum class PrivateDataType {
    /** Instant messages and chat history */
    MESSAGES,
    /** Friends list and online status cache */
    FRIENDS,
    /** User preferences and settings */
    SETTINGS,
    /** Cached inventory data */
    INVENTORY
}
