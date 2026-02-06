package com.linkpoint.protocol.circuit

import android.app.ActivityManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Environment
import androidx.core.content.edit
import com.linkpoint.network.NetworkLogger
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.util.Properties

/**
 * Global Options (Enhanced from the reference viewer's GlobalOptions.java)
 * 
 * User-configurable settings that affect networking and performance.
 * Enhanced for modern devices with up to 16GB+ RAM.
 * 
 * ## Modern Device Support
 * 
 * Updated for 2024+ devices:
 * - Standard memory tiers: 512MB, 1GB, 2GB, 3GB, 4GB, 6GB, 8GB, 12GB, 16GB
 * - Texture memory scales from 64MB to 2GB based on device RAM
 * - Parallel downloads scale from 2 to 32 based on device capabilities
 * 
 * ## Persistent Storage
 * 
 * Settings are stored in TWO locations for persistence:
 * 1. SharedPreferences (app-private) - cleared on uninstall
 * 2. External file backup - survives app reinstall
 * 
 * On first launch after reinstall, settings are restored from backup.
 */
class LinkpointGlobalOptions private constructor(private val context: Context) {
    
    companion object {
        private const val PREFS_NAME = "linkpoint_circuit_options"
        private const val TAG = "LinkpointGlobalOptions"
        
        // External storage folder for all Linkpoint data (survives reinstall)
        const val EXTERNAL_FOLDER_NAME = "Linkpoint Viewer"
        const val SETTINGS_FILENAME = "settings.properties"
        const val TEXTURE_CACHE_FOLDER = "textures"
        const val MESH_CACHE_FOLDER = "meshes"
        const val OBJECT_CACHE_FOLDER = "objects"
        const val LOGS_FOLDER = "logs"
        const val SOUNDS_CACHE_FOLDER = "sounds"
        const val ANIMATIONS_CACHE_FOLDER = "animations"
        
        // Preference keys
        private const val KEY_AUTO_RECONNECT = "auto_reconnect"
        private const val KEY_MAX_RECONNECT_ATTEMPTS = "max_reconnect_attempts"
        private const val KEY_KEEP_WIFI_ON = "keep_wifi_on"
        private const val KEY_MAX_TEXTURE_DOWNLOADS = "max_texture_downloads"
        private const val KEY_HIGH_QUALITY_TEXTURES = "high_quality_textures"
        private const val KEY_COMPRESSED_TEXTURES = "compressed_textures"
        private const val KEY_TEXTURE_MEMORY_LIMIT_MB = "texture_memory_limit_mb"
        private const val KEY_MESH_MEMORY_LIMIT_MB = "mesh_memory_limit_mb"
        private const val KEY_OBJECT_CACHE_LIMIT_MB = "object_cache_limit_mb"
        private const val KEY_TEXTURE_CACHE_LIMIT_GB = "texture_cache_limit_gb"
        private const val KEY_MESH_CACHE_LIMIT_GB = "mesh_cache_limit_gb"
        private const val KEY_OBJECT_CACHE_LIMIT_GB = "object_cache_limit_gb"
        private const val KEY_SOUND_CACHE_LIMIT_GB = "sound_cache_limit_gb"
        private const val KEY_ANIMATION_CACHE_LIMIT_GB = "animation_cache_limit_gb"
        private const val KEY_TOTAL_CACHE_LIMIT_GB = "total_cache_limit_gb"
        private const val KEY_DRAW_DISTANCE = "draw_distance"
        private const val KEY_SYSTEM_DEFAULTS_SET = "system_defaults_set"
        private const val KEY_USER_MODIFIED = "user_modified"
        private const val KEY_DETECTED_RAM_MB = "detected_ram_mb"
        
        // Maximum total cache size (100 GB)
        const val MAX_TOTAL_CACHE_GB = 100
        
        // Standard RAM tiers (in MB) for snapping
        val STANDARD_RAM_TIERS_MB = listOf(
            512, 1024, 2048, 3072, 4096, 6144, 8192, 12288, 16384
        )
        
        // Texture memory options (in MB) that users can select - RAM cache
        val TEXTURE_MEMORY_OPTIONS_MB = listOf(
            64, 128, 256, 384, 512, 768, 1024, 1536, 2048, 3072, 4096
        )
        
        // Object memory cache options (in MB) - RAM cache
        val OBJECT_CACHE_OPTIONS_MB = listOf(
            32, 64, 128, 256, 512, 1024, 2048
        )
        
        // Disk cache options (in GB) - for textures on disk
        val TEXTURE_DISK_CACHE_OPTIONS_GB = listOf(
            1, 2, 5, 10, 15, 20, 25, 30, 40, 50
        )
        
        // Disk cache options (in GB) - for meshes on disk
        val MESH_DISK_CACHE_OPTIONS_GB = listOf(
            1, 2, 5, 10, 15, 20, 25, 30
        )
        
        // Disk cache options (in GB) - for objects on disk
        val OBJECT_DISK_CACHE_OPTIONS_GB = listOf(
            1, 2, 5, 10, 15, 20
        )
        
        // Disk cache options (in GB) - for sounds on disk
        val SOUND_DISK_CACHE_OPTIONS_GB = listOf(
            1, 2, 5, 10
        )
        
        // Disk cache options (in GB) - for animations on disk
        val ANIMATION_DISK_CACHE_OPTIONS_GB = listOf(
            1, 2, 5, 10
        )
        
        // Total cache limit options (in GB)
        val TOTAL_CACHE_OPTIONS_GB = listOf(
            5, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100
        )
        
        // Draw distance options (in meters)
        val DRAW_DISTANCE_OPTIONS = listOf(
            32, 64, 96, 128, 192, 256, 384, 512, 768, 1024
        )
        
        @Volatile
        private var instance: LinkpointGlobalOptions? = null
        
        fun getInstance(context: Context): LinkpointGlobalOptions {
            return instance ?: synchronized(this) {
                instance ?: LinkpointGlobalOptions(context.applicationContext).also {
                    instance = it
                }
            }
        }
        
        /**
         * Get the main Linkpoint Viewer folder on external storage.
         * Path: /storage/sdcard0/Linkpoint Viewer
         * 
         * This folder persists across app reinstalls and contains:
         * - settings.properties (user settings backup)
         * - textures/ (texture cache)
         * - meshes/ (mesh cache)
         * - objects/ (object cache)
         * - logs/ (session logs)
         * - sounds/ (sound cache)
         * - animations/ (animation cache)
         */
        fun getExternalFolder(): File {
            // Try primary external storage first (/storage/sdcard0 or /storage/emulated/0)
            val primaryExternal = Environment.getExternalStorageDirectory()
            val linkpointFolder = File(primaryExternal, EXTERNAL_FOLDER_NAME)
            
            // Create folder if it doesn't exist
            if (!linkpointFolder.exists()) {
                linkpointFolder.mkdirs()
            }
            
            return linkpointFolder
        }
        
        /**
         * Get a specific cache subfolder
         */
        fun getCacheFolder(folderName: String): File {
            val folder = File(getExternalFolder(), folderName)
            if (!folder.exists()) {
                folder.mkdirs()
            }
            return folder
        }
        
        /**
         * Get texture cache folder: /storage/sdcard0/Linkpoint Viewer/textures
         */
        fun getTextureCacheFolder(): File = getCacheFolder(TEXTURE_CACHE_FOLDER)
        
        /**
         * Get mesh cache folder: /storage/sdcard0/Linkpoint Viewer/meshes
         */
        fun getMeshCacheFolder(): File = getCacheFolder(MESH_CACHE_FOLDER)
        
        /**
         * Get object cache folder: /storage/sdcard0/Linkpoint Viewer/objects
         */
        fun getObjectCacheFolder(): File = getCacheFolder(OBJECT_CACHE_FOLDER)
        
        /**
         * Get logs folder: /storage/sdcard0/Linkpoint Viewer/logs
         */
        fun getLogsFolder(): File = getCacheFolder(LOGS_FOLDER)
        
        /**
         * Get sounds cache folder: /storage/sdcard0/Linkpoint Viewer/sounds
         */
        fun getSoundsCacheFolder(): File = getCacheFolder(SOUNDS_CACHE_FOLDER)
        
        /**
         * Get animations cache folder: /storage/sdcard0/Linkpoint Viewer/animations
         */
        fun getAnimationsCacheFolder(): File = getCacheFolder(ANIMATIONS_CACHE_FOLDER)
        
        /**
         * Snap a value to the nearest standard tier
         */
        fun snapToNearestTier(value: Int, tiers: List<Int>): Int {
            if (tiers.isEmpty()) return value
            return tiers.minByOrNull { kotlin.math.abs(it - value) } ?: value
        }
        
        /**
         * Get total size of a cache folder in bytes
         */
        fun getCacheFolderSize(folder: File): Long {
            if (!folder.exists() || !folder.isDirectory) return 0
            return folder.walkTopDown().filter { it.isFile }.sumOf { it.length() }
        }
        
        /**
         * Clear a cache folder
         */
        fun clearCacheFolder(folder: File): Boolean {
            return try {
                if (folder.exists() && folder.isDirectory) {
                    folder.listFiles()?.forEach { file ->
                        if (file.isDirectory) {
                            file.deleteRecursively()
                        } else {
                            file.delete()
                        }
                    }
                }
                true
            } catch (e: Exception) {
                false
            }
        }
    }
    
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    init {
        // Check for backup restore first (handles reinstall case)
        if (!prefs.getBoolean(KEY_SYSTEM_DEFAULTS_SET, false)) {
            val restored = tryRestoreFromBackup()
            if (!restored) {
                setDeviceAdaptiveDefaults()
            }
        }
    }
    
    // ==================== MEMORY/DEVICE INFO ====================
    
    /**
     * Get detected device RAM in MB, snapped to standard tier
     */
    val detectedRamMb: Int
        get() = prefs.getInt(KEY_DETECTED_RAM_MB, 2048)
    
    /**
     * Get detected device RAM as a readable string
     */
    val detectedRamString: String
        get() {
            val mb = detectedRamMb
            return if (mb >= 1024) "${mb / 1024}GB" else "${mb}MB"
        }
    
    // ==================== SETTINGS PROPERTIES ====================
    
    /**
     * Whether to auto-reconnect on disconnect
     */
    var autoReconnect: Boolean
        get() = prefs.getBoolean(KEY_AUTO_RECONNECT, LinkpointConstants.AUTO_RECONNECT_DEFAULT)
        set(value) {
            prefs.edit { putBoolean(KEY_AUTO_RECONNECT, value) }
            markUserModified()
            saveBackup()
        }
    
    /**
     * Maximum reconnection attempts before giving up
     */
    var maxReconnectAttempts: Int
        get() = prefs.getInt(KEY_MAX_RECONNECT_ATTEMPTS, LinkpointConstants.MAX_RECONNECT_ATTEMPTS)
        set(value) {
            prefs.edit { putInt(KEY_MAX_RECONNECT_ATTEMPTS, value) }
            markUserModified()
            saveBackup()
        }
    
    /**
     * Keep WiFi awake during connection
     */
    var keepWifiOn: Boolean
        get() = prefs.getBoolean(KEY_KEEP_WIFI_ON, true)
        set(value) {
            prefs.edit { putBoolean(KEY_KEEP_WIFI_ON, value) }
            markUserModified()
            saveBackup()
        }
    
    /**
     * Maximum parallel texture downloads (2-32)
     */
    var maxTextureDownloads: Int
        get() = prefs.getInt(KEY_MAX_TEXTURE_DOWNLOADS, 4)
        set(value) {
            prefs.edit { putInt(KEY_MAX_TEXTURE_DOWNLOADS, value.coerceIn(2, 32)) }
            markUserModified()
            saveBackup()
        }
    
    /**
     * Use high quality textures
     */
    var highQualityTextures: Boolean
        get() = prefs.getBoolean(KEY_HIGH_QUALITY_TEXTURES, true)
        set(value) {
            prefs.edit { putBoolean(KEY_HIGH_QUALITY_TEXTURES, value) }
            markUserModified()
            saveBackup()
        }
    
    /**
     * Use compressed textures (saves memory)
     */
    var compressedTextures: Boolean
        get() = prefs.getBoolean(KEY_COMPRESSED_TEXTURES, true)
        set(value) {
            prefs.edit { putBoolean(KEY_COMPRESSED_TEXTURES, value) }
            markUserModified()
            saveBackup()
        }
    
    /**
     * Texture memory limit in MB (64-2048)
     */
    var textureMemoryLimitMb: Int
        get() = prefs.getInt(KEY_TEXTURE_MEMORY_LIMIT_MB, 256)
        set(value) {
            val snapped = snapToNearestTier(value, TEXTURE_MEMORY_OPTIONS_MB)
            prefs.edit { putInt(KEY_TEXTURE_MEMORY_LIMIT_MB, snapped) }
            markUserModified()
            saveBackup()
        }
    
    /**
     * Mesh memory limit in MB
     */
    var meshMemoryLimitMb: Int
        get() = prefs.getInt(KEY_MESH_MEMORY_LIMIT_MB, 128)
        set(value) {
            prefs.edit { putInt(KEY_MESH_MEMORY_LIMIT_MB, value) }
            markUserModified()
            saveBackup()
        }
    
    /**
     * Object cache memory limit in MB
     */
    var objectCacheLimitMb: Int
        get() = prefs.getInt(KEY_OBJECT_CACHE_LIMIT_MB, 128)
        set(value) {
            val snapped = snapToNearestTier(value, OBJECT_CACHE_OPTIONS_MB)
            prefs.edit { putInt(KEY_OBJECT_CACHE_LIMIT_MB, snapped) }
            markUserModified()
            saveBackup()
        }
    
    /**
     * Draw distance in meters
     */
    var drawDistance: Int
        get() = prefs.getInt(KEY_DRAW_DISTANCE, 128)
        set(value) {
            val snapped = snapToNearestTier(value, DRAW_DISTANCE_OPTIONS)
            prefs.edit { putInt(KEY_DRAW_DISTANCE, snapped) }
            markUserModified()
            saveBackup()
        }
    
    // ==================== DISK CACHE SETTINGS (GB) ====================
    // These control how much storage space is used for persistent caches
    // Maximum total: 100 GB
    
    /**
     * Texture disk cache limit in GB (1-50 GB)
     * Stores downloaded textures on disk for faster loading
     */
    var textureCacheLimitGb: Int
        get() = prefs.getInt(KEY_TEXTURE_CACHE_LIMIT_GB, 10)
        set(value) {
            val snapped = snapToNearestTier(value, TEXTURE_DISK_CACHE_OPTIONS_GB)
            val capped = enforceMaxTotalCache(snapped, "texture")
            prefs.edit { putInt(KEY_TEXTURE_CACHE_LIMIT_GB, capped) }
            markUserModified()
            saveBackup()
        }
    
    /**
     * Mesh disk cache limit in GB (1-30 GB)
     * Stores downloaded meshes on disk
     */
    var meshCacheLimitGb: Int
        get() = prefs.getInt(KEY_MESH_CACHE_LIMIT_GB, 5)
        set(value) {
            val snapped = snapToNearestTier(value, MESH_DISK_CACHE_OPTIONS_GB)
            val capped = enforceMaxTotalCache(snapped, "mesh")
            prefs.edit { putInt(KEY_MESH_CACHE_LIMIT_GB, capped) }
            markUserModified()
            saveBackup()
        }
    
    /**
     * Object disk cache limit in GB (1-20 GB)
     * Stores object data on disk for faster region loading
     */
    var objectCacheLimitGb: Int
        get() = prefs.getInt(KEY_OBJECT_CACHE_LIMIT_GB, 5)
        set(value) {
            val snapped = snapToNearestTier(value, OBJECT_DISK_CACHE_OPTIONS_GB)
            val capped = enforceMaxTotalCache(snapped, "object")
            prefs.edit { putInt(KEY_OBJECT_CACHE_LIMIT_GB, capped) }
            markUserModified()
            saveBackup()
        }
    
    /**
     * Sound disk cache limit in GB (1-10 GB)
     * Stores downloaded sounds on disk
     */
    var soundCacheLimitGb: Int
        get() = prefs.getInt(KEY_SOUND_CACHE_LIMIT_GB, 2)
        set(value) {
            val snapped = snapToNearestTier(value, SOUND_DISK_CACHE_OPTIONS_GB)
            val capped = enforceMaxTotalCache(snapped, "sound")
            prefs.edit { putInt(KEY_SOUND_CACHE_LIMIT_GB, capped) }
            markUserModified()
            saveBackup()
        }
    
    /**
     * Animation disk cache limit in GB (1-10 GB)
     * Stores downloaded animations on disk
     */
    var animationCacheLimitGb: Int
        get() = prefs.getInt(KEY_ANIMATION_CACHE_LIMIT_GB, 2)
        set(value) {
            val snapped = snapToNearestTier(value, ANIMATION_DISK_CACHE_OPTIONS_GB)
            val capped = enforceMaxTotalCache(snapped, "animation")
            prefs.edit { putInt(KEY_ANIMATION_CACHE_LIMIT_GB, capped) }
            markUserModified()
            saveBackup()
        }
    
    /**
     * Total disk cache limit in GB (5-100 GB)
     * Maximum total storage used by all caches combined
     */
    var totalCacheLimitGb: Int
        get() = prefs.getInt(KEY_TOTAL_CACHE_LIMIT_GB, 30)
        set(value) {
            val snapped = snapToNearestTier(value.coerceAtMost(MAX_TOTAL_CACHE_GB), TOTAL_CACHE_OPTIONS_GB)
            prefs.edit { putInt(KEY_TOTAL_CACHE_LIMIT_GB, snapped) }
            markUserModified()
            saveBackup()
        }
    
    /**
     * Get the configured total cache limit across all categories
     */
    val configuredTotalCacheLimitGb: Int
        get() = textureCacheLimitGb + meshCacheLimitGb + objectCacheLimitGb + 
                soundCacheLimitGb + animationCacheLimitGb
    
    /**
     * Enforce maximum total cache limit when setting individual cache sizes
     */
    private fun enforceMaxTotalCache(requestedGb: Int, cacheType: String): Int {
        val otherCaches = when (cacheType) {
            "texture" -> meshCacheLimitGb + objectCacheLimitGb + soundCacheLimitGb + animationCacheLimitGb
            "mesh" -> textureCacheLimitGb + objectCacheLimitGb + soundCacheLimitGb + animationCacheLimitGb
            "object" -> textureCacheLimitGb + meshCacheLimitGb + soundCacheLimitGb + animationCacheLimitGb
            "sound" -> textureCacheLimitGb + meshCacheLimitGb + objectCacheLimitGb + animationCacheLimitGb
            "animation" -> textureCacheLimitGb + meshCacheLimitGb + objectCacheLimitGb + soundCacheLimitGb
            else -> 0
        }
        val maxAllowed = (totalCacheLimitGb - otherCaches).coerceAtLeast(1)
        return requestedGb.coerceAtMost(maxAllowed)
    }
    
    /**
     * Whether user has modified settings
     */
    val userModified: Boolean
        get() = prefs.getBoolean(KEY_USER_MODIFIED, false)
    
    private fun markUserModified() {
        prefs.edit { putBoolean(KEY_USER_MODIFIED, true) }
    }
    
    // ==================== DEVICE-ADAPTIVE DEFAULTS ====================
    
    /**
     * Set device-adaptive defaults based on hardware
     * 
     * Modern devices (2024+) can have:
     * - 4GB-16GB RAM
     * - 4-8+ CPU cores
     * - High-speed storage
     * 
     * We allocate memory generously while leaving headroom for the system.
     */
    private fun setDeviceAdaptiveDefaults() {
        val totalMemoryMb = getTotalMemoryMb()
        val snappedRamMb = snapToNearestTier(totalMemoryMb, STANDARD_RAM_TIERS_MB)
        val cpuCores = Runtime.getRuntime().availableProcessors()
        
        NetworkLogger.log(NetworkLogger.Level.INFO, NetworkLogger.Category.CONNECTION,
            "LinkpointOptions: Device detected - RAM: ${totalMemoryMb}MB (snapped to ${snappedRamMb}MB), Cores: $cpuCores")
        
        prefs.edit {
            // Store detected RAM
            putInt(KEY_DETECTED_RAM_MB, snappedRamMb)
            
            // High-quality textures for devices with 2GB+ RAM
            val highQuality = snappedRamMb >= 2048
            putBoolean(KEY_HIGH_QUALITY_TEXTURES, highQuality)
            
            // Texture memory: Use ~15-25% of RAM, capped at options
            val textureMemory = when {
                snappedRamMb >= 16384 -> 2048  // 16GB+: 2GB textures
                snappedRamMb >= 12288 -> 1536  // 12GB: 1.5GB textures
                snappedRamMb >= 8192 -> 1024   // 8GB: 1GB textures
                snappedRamMb >= 6144 -> 768    // 6GB: 768MB textures
                snappedRamMb >= 4096 -> 512    // 4GB: 512MB textures
                snappedRamMb >= 3072 -> 384    // 3GB: 384MB textures
                snappedRamMb >= 2048 -> 256    // 2GB: 256MB textures
                snappedRamMb >= 1024 -> 128    // 1GB: 128MB textures
                else -> 64                      // <1GB: 64MB textures
            }
            putInt(KEY_TEXTURE_MEMORY_LIMIT_MB, textureMemory)
            
            // Mesh memory: ~50% of texture memory
            val meshMemory = textureMemory / 2
            putInt(KEY_MESH_MEMORY_LIMIT_MB, meshMemory)
            
            // Object cache: ~50% of texture memory
            val objectCache = snapToNearestTier(textureMemory / 2, OBJECT_CACHE_OPTIONS_MB)
            putInt(KEY_OBJECT_CACHE_LIMIT_MB, objectCache)
            
            // Draw distance based on RAM (more RAM = can handle more objects)
            val drawDist = when {
                snappedRamMb >= 8192 -> 512    // 8GB+: 512m
                snappedRamMb >= 4096 -> 384    // 4GB: 384m
                snappedRamMb >= 2048 -> 256    // 2GB: 256m
                snappedRamMb >= 1024 -> 128    // 1GB: 128m
                else -> 64                      // <1GB: 64m
            }
            putInt(KEY_DRAW_DISTANCE, drawDist)
            
            // Parallel downloads based on cores and RAM
            val maxDownloads = when {
                cpuCores >= 8 && snappedRamMb >= 8192 -> 32
                cpuCores >= 8 && snappedRamMb >= 4096 -> 24
                cpuCores >= 6 && snappedRamMb >= 4096 -> 16
                cpuCores >= 4 && snappedRamMb >= 2048 -> 12
                cpuCores >= 4 && snappedRamMb >= 1024 -> 8
                cpuCores >= 2 -> 4
                else -> 2
            }
            putInt(KEY_MAX_TEXTURE_DOWNLOADS, maxDownloads)
            
            // ==================== DISK CACHE DEFAULTS ====================
            // Set generous disk cache limits based on device tier
            // Total limit is 100GB max, individual caches scale with device
            
            // Total cache limit (default generous for modern devices)
            val totalCacheGb = when {
                snappedRamMb >= 8192 -> 100   // High-end: full 100GB
                snappedRamMb >= 4096 -> 70    // Mid-high: 70GB
                snappedRamMb >= 2048 -> 50    // Mid: 50GB
                else -> 30                     // Low: 30GB
            }
            putInt(KEY_TOTAL_CACHE_LIMIT_GB, totalCacheGb)
            
            // Texture disk cache (largest - textures are biggest assets)
            val textureCacheGb = when {
                snappedRamMb >= 8192 -> 50    // High-end: 50GB
                snappedRamMb >= 4096 -> 30    // Mid-high: 30GB
                snappedRamMb >= 2048 -> 20    // Mid: 20GB
                else -> 10                     // Low: 10GB
            }
            putInt(KEY_TEXTURE_CACHE_LIMIT_GB, textureCacheGb)
            
            // Mesh disk cache
            val meshCacheGb = when {
                snappedRamMb >= 8192 -> 25    // High-end: 25GB
                snappedRamMb >= 4096 -> 15    // Mid-high: 15GB
                snappedRamMb >= 2048 -> 10    // Mid: 10GB
                else -> 5                      // Low: 5GB
            }
            putInt(KEY_MESH_CACHE_LIMIT_GB, meshCacheGb)
            
            // Object disk cache
            val objectCacheGb = when {
                snappedRamMb >= 8192 -> 15    // High-end: 15GB
                snappedRamMb >= 4096 -> 10    // Mid-high: 10GB
                snappedRamMb >= 2048 -> 5     // Mid: 5GB
                else -> 2                      // Low: 2GB
            }
            putInt(KEY_OBJECT_CACHE_LIMIT_GB, objectCacheGb)
            
            // Sound disk cache
            val soundCacheGb = when {
                snappedRamMb >= 4096 -> 5     // High/mid: 5GB
                else -> 2                      // Low: 2GB
            }
            putInt(KEY_SOUND_CACHE_LIMIT_GB, soundCacheGb)
            
            // Animation disk cache
            val animationCacheGb = when {
                snappedRamMb >= 4096 -> 5     // High/mid: 5GB
                else -> 2                      // Low: 2GB
            }
            putInt(KEY_ANIMATION_CACHE_LIMIT_GB, animationCacheGb)
            
            // Mark defaults as set
            putBoolean(KEY_SYSTEM_DEFAULTS_SET, true)
            putBoolean(KEY_USER_MODIFIED, false)
        }
        
        // Save backup immediately
        saveBackup()
        
        NetworkLogger.log(NetworkLogger.Level.INFO, NetworkLogger.Category.CONNECTION,
            "LinkpointOptions: Defaults set - HQ: $highQualityTextures, " +
            "TextureMem: ${textureMemoryLimitMb}MB, MeshMem: ${meshMemoryLimitMb}MB, " +
            "ObjCache: ${objectCacheLimitMb}MB, DrawDist: ${drawDistance}m, " +
            "MaxDownloads: $maxTextureDownloads, TotalCache: ${totalCacheLimitGb}GB")
    }
    
    /**
     * Get total system memory in MB
     */
    private fun getTotalMemoryMb(): Int {
        return try {
            // Method 1: ActivityManager (most reliable on modern Android)
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            if (activityManager != null) {
                val memInfo = ActivityManager.MemoryInfo()
                activityManager.getMemoryInfo(memInfo)
                return (memInfo.totalMem / (1024 * 1024)).toInt()
            }
            
            // Method 2: /proc/meminfo fallback
            BufferedReader(FileReader("/proc/meminfo"), 8192).use { reader ->
                var line = reader.readLine()
                while (line != null) {
                    if (line.startsWith("MemTotal:")) {
                        val parts = line.split("\\s+".toRegex())
                        if (parts.size >= 2) {
                            return (parts[1].toLongOrNull() ?: 0L).toInt() / 1024
                        }
                    }
                    line = reader.readLine()
                }
                2048 // Default to 2GB
            }
        } catch (e: Exception) {
            // Fallback: use Runtime memory info
            (Runtime.getRuntime().maxMemory() / (1024 * 1024)).toInt()
        }
    }
    
    // ==================== PERSISTENT BACKUP ====================
    
    /**
     * Get backup file location in the external Linkpoint Viewer folder.
     * Path: /storage/sdcard0/Linkpoint Viewer/settings.properties
     * 
     * This survives app reinstalls.
     */
    private fun getBackupFile(): File? {
        return try {
            val linkpointFolder = getExternalFolder()
            File(linkpointFolder, SETTINGS_FILENAME)
        } catch (e: Exception) {
            NetworkLogger.log(NetworkLogger.Level.WARN, NetworkLogger.Category.CONNECTION,
                "LinkpointOptions: Cannot access external folder: ${e.message}")
            null
        }
    }
    
    /**
     * Save settings to external backup file
     */
    private fun saveBackup() {
        try {
            val file = getBackupFile() ?: return
            val props = Properties()
            
            // Memory settings
            props.setProperty(KEY_AUTO_RECONNECT, autoReconnect.toString())
            props.setProperty(KEY_MAX_RECONNECT_ATTEMPTS, maxReconnectAttempts.toString())
            props.setProperty(KEY_KEEP_WIFI_ON, keepWifiOn.toString())
            props.setProperty(KEY_MAX_TEXTURE_DOWNLOADS, maxTextureDownloads.toString())
            props.setProperty(KEY_HIGH_QUALITY_TEXTURES, highQualityTextures.toString())
            props.setProperty(KEY_COMPRESSED_TEXTURES, compressedTextures.toString())
            props.setProperty(KEY_TEXTURE_MEMORY_LIMIT_MB, textureMemoryLimitMb.toString())
            props.setProperty(KEY_MESH_MEMORY_LIMIT_MB, meshMemoryLimitMb.toString())
            props.setProperty(KEY_OBJECT_CACHE_LIMIT_MB, objectCacheLimitMb.toString())
            props.setProperty(KEY_DRAW_DISTANCE, drawDistance.toString())
            props.setProperty(KEY_DETECTED_RAM_MB, detectedRamMb.toString())
            props.setProperty(KEY_USER_MODIFIED, userModified.toString())
            
            // Disk cache settings (new)
            props.setProperty(KEY_TEXTURE_CACHE_LIMIT_GB, textureCacheLimitGb.toString())
            props.setProperty(KEY_MESH_CACHE_LIMIT_GB, meshCacheLimitGb.toString())
            props.setProperty(KEY_OBJECT_CACHE_LIMIT_GB, objectCacheLimitGb.toString())
            props.setProperty(KEY_SOUND_CACHE_LIMIT_GB, soundCacheLimitGb.toString())
            props.setProperty(KEY_ANIMATION_CACHE_LIMIT_GB, animationCacheLimitGb.toString())
            props.setProperty(KEY_TOTAL_CACHE_LIMIT_GB, totalCacheLimitGb.toString())
            
            file.outputStream().use { out ->
                props.store(out, "Linkpoint Settings Backup - Do not delete")
            }
            
            NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.CONNECTION,
                "LinkpointOptions: Settings backed up to ${file.absolutePath}")
        } catch (e: Exception) {
            NetworkLogger.log(NetworkLogger.Level.WARN, NetworkLogger.Category.CONNECTION,
                "LinkpointOptions: Failed to save backup: ${e.message}")
        }
    }
    
    /**
     * Try to restore settings from backup file
     * @return true if backup was found and restored
     */
    private fun tryRestoreFromBackup(): Boolean {
        try {
            val file = getBackupFile() ?: return false
            if (!file.exists()) return false
            
            val props = Properties()
            file.inputStream().use { input ->
                props.load(input)
            }
            
            // Check if backup has user modifications
            val wasModified = props.getProperty(KEY_USER_MODIFIED, "false").toBoolean()
            if (!wasModified) {
                // Backup exists but user never customized - recalculate for current device
                return false
            }
            
            prefs.edit {
                // Memory settings
                props.getProperty(KEY_AUTO_RECONNECT)?.let { 
                    putBoolean(KEY_AUTO_RECONNECT, it.toBoolean()) 
                }
                props.getProperty(KEY_MAX_RECONNECT_ATTEMPTS)?.let { 
                    putInt(KEY_MAX_RECONNECT_ATTEMPTS, it.toInt()) 
                }
                props.getProperty(KEY_KEEP_WIFI_ON)?.let { 
                    putBoolean(KEY_KEEP_WIFI_ON, it.toBoolean()) 
                }
                props.getProperty(KEY_MAX_TEXTURE_DOWNLOADS)?.let { 
                    putInt(KEY_MAX_TEXTURE_DOWNLOADS, it.toInt()) 
                }
                props.getProperty(KEY_HIGH_QUALITY_TEXTURES)?.let { 
                    putBoolean(KEY_HIGH_QUALITY_TEXTURES, it.toBoolean()) 
                }
                props.getProperty(KEY_COMPRESSED_TEXTURES)?.let { 
                    putBoolean(KEY_COMPRESSED_TEXTURES, it.toBoolean()) 
                }
                props.getProperty(KEY_TEXTURE_MEMORY_LIMIT_MB)?.let { 
                    putInt(KEY_TEXTURE_MEMORY_LIMIT_MB, it.toInt()) 
                }
                props.getProperty(KEY_MESH_MEMORY_LIMIT_MB)?.let { 
                    putInt(KEY_MESH_MEMORY_LIMIT_MB, it.toInt()) 
                }
                props.getProperty(KEY_OBJECT_CACHE_LIMIT_MB)?.let { 
                    putInt(KEY_OBJECT_CACHE_LIMIT_MB, it.toInt()) 
                }
                props.getProperty(KEY_DRAW_DISTANCE)?.let { 
                    putInt(KEY_DRAW_DISTANCE, it.toInt()) 
                }
                
                // Disk cache settings (new)
                props.getProperty(KEY_TEXTURE_CACHE_LIMIT_GB)?.let { 
                    putInt(KEY_TEXTURE_CACHE_LIMIT_GB, it.toInt()) 
                }
                props.getProperty(KEY_MESH_CACHE_LIMIT_GB)?.let { 
                    putInt(KEY_MESH_CACHE_LIMIT_GB, it.toInt()) 
                }
                props.getProperty(KEY_OBJECT_CACHE_LIMIT_GB)?.let { 
                    putInt(KEY_OBJECT_CACHE_LIMIT_GB, it.toInt()) 
                }
                props.getProperty(KEY_SOUND_CACHE_LIMIT_GB)?.let { 
                    putInt(KEY_SOUND_CACHE_LIMIT_GB, it.toInt()) 
                }
                props.getProperty(KEY_ANIMATION_CACHE_LIMIT_GB)?.let { 
                    putInt(KEY_ANIMATION_CACHE_LIMIT_GB, it.toInt()) 
                }
                props.getProperty(KEY_TOTAL_CACHE_LIMIT_GB)?.let { 
                    putInt(KEY_TOTAL_CACHE_LIMIT_GB, it.toInt()) 
                }
                
                // Update detected RAM for current device
                val currentRam = getTotalMemoryMb()
                val snappedRam = snapToNearestTier(currentRam, STANDARD_RAM_TIERS_MB)
                putInt(KEY_DETECTED_RAM_MB, snappedRam)
                
                putBoolean(KEY_SYSTEM_DEFAULTS_SET, true)
                putBoolean(KEY_USER_MODIFIED, true)
            }
            
            NetworkLogger.log(NetworkLogger.Level.INFO, NetworkLogger.Category.CONNECTION,
                "LinkpointOptions: Restored settings from backup")
            return true
            
        } catch (e: Exception) {
            NetworkLogger.log(NetworkLogger.Level.WARN, NetworkLogger.Category.CONNECTION,
                "LinkpointOptions: Failed to restore backup: ${e.message}")
            return false
        }
    }
    
    /**
     * Reset all settings to device-adaptive defaults
     */
    fun resetToDefaults() {
        prefs.edit { clear() }
        setDeviceAdaptiveDefaults()
    }
    
    /**
     * Get all settings as a map (for debugging/display)
     */
    fun getAllSettings(): Map<String, Any> = mapOf(
        "detectedRam" to detectedRamString,
        "autoReconnect" to autoReconnect,
        "maxReconnectAttempts" to maxReconnectAttempts,
        "keepWifiOn" to keepWifiOn,
        "maxTextureDownloads" to maxTextureDownloads,
        "highQualityTextures" to highQualityTextures,
        "compressedTextures" to compressedTextures,
        "textureMemoryLimitMb" to textureMemoryLimitMb,
        "meshMemoryLimitMb" to meshMemoryLimitMb,
        "objectCacheLimitMb" to objectCacheLimitMb,
        "drawDistance" to drawDistance,
        "userModified" to userModified,
        "externalFolder" to getExternalFolder().absolutePath
    )
    
    /**
     * Get recommended texture memory for current device
     */
    fun getRecommendedTextureMemoryMb(): Int {
        val ram = detectedRamMb
        return when {
            ram >= 16384 -> 2048
            ram >= 12288 -> 1536
            ram >= 8192 -> 1024
            ram >= 6144 -> 768
            ram >= 4096 -> 512
            ram >= 3072 -> 384
            ram >= 2048 -> 256
            ram >= 1024 -> 128
            else -> 64
        }
    }
    
    /**
     * Get maximum safe texture memory for current device (leaves 50% for system)
     */
    fun getMaxSafeTextureMemoryMb(): Int {
        return (detectedRamMb * 0.25).toInt().coerceAtMost(2048)
    }
    
    // ==================== CACHE MANAGEMENT ====================
    
    /**
     * Get cache statistics for display
     */
    fun getCacheStats(): Map<String, Long> {
        return mapOf(
            "textures" to getCacheFolderSize(getTextureCacheFolder()),
            "meshes" to getCacheFolderSize(getMeshCacheFolder()),
            "objects" to getCacheFolderSize(getObjectCacheFolder()),
            "sounds" to getCacheFolderSize(getSoundsCacheFolder()),
            "animations" to getCacheFolderSize(getAnimationsCacheFolder()),
            "logs" to getCacheFolderSize(getLogsFolder())
        )
    }
    
    /**
     * Get total cache size in bytes
     */
    fun getTotalCacheSize(): Long {
        return getCacheStats().values.sum()
    }
    
    /**
     * Get total cache size as a readable string
     */
    fun getTotalCacheSizeString(): String {
        val bytes = getTotalCacheSize()
        return when {
            bytes >= 1024 * 1024 * 1024 -> "%.2f GB".format(bytes / (1024.0 * 1024.0 * 1024.0))
            bytes >= 1024 * 1024 -> "%.1f MB".format(bytes / (1024.0 * 1024.0))
            bytes >= 1024 -> "%.1f KB".format(bytes / 1024.0)
            else -> "$bytes bytes"
        }
    }
    
    /**
     * Clear all caches
     */
    fun clearAllCaches() {
        clearCacheFolder(getTextureCacheFolder())
        clearCacheFolder(getMeshCacheFolder())
        clearCacheFolder(getObjectCacheFolder())
        clearCacheFolder(getSoundsCacheFolder())
        clearCacheFolder(getAnimationsCacheFolder())
        // Don't clear logs by default
        
        NetworkLogger.log(NetworkLogger.Level.INFO, NetworkLogger.Category.CONNECTION,
            "LinkpointOptions: All caches cleared")
    }
    
    /**
     * Clear texture cache only
     */
    fun clearTextureCache() {
        clearCacheFolder(getTextureCacheFolder())
        NetworkLogger.log(NetworkLogger.Level.INFO, NetworkLogger.Category.CONNECTION,
            "LinkpointOptions: Texture cache cleared")
    }
    
    /**
     * Clear logs folder
     */
    fun clearLogs() {
        clearCacheFolder(getLogsFolder())
        NetworkLogger.log(NetworkLogger.Level.INFO, NetworkLogger.Category.CONNECTION,
            "LinkpointOptions: Logs cleared")
    }
    
    /**
     * Initialize all external folders (call on app startup)
     */
    fun initializeExternalFolders() {
        try {
            // Create all cache folders
            getExternalFolder()
            getTextureCacheFolder()
            getMeshCacheFolder()
            getObjectCacheFolder()
            getLogsFolder()
            getSoundsCacheFolder()
            getAnimationsCacheFolder()
            
            NetworkLogger.log(NetworkLogger.Level.INFO, NetworkLogger.Category.CONNECTION,
                "LinkpointOptions: External folders initialized at ${getExternalFolder().absolutePath}")
        } catch (e: Exception) {
            NetworkLogger.log(NetworkLogger.Level.ERROR, NetworkLogger.Category.CONNECTION,
                "LinkpointOptions: Failed to initialize external folders: ${e.message}")
        }
    }
}
