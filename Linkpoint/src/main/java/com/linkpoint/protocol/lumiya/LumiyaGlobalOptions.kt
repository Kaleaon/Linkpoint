package com.linkpoint.protocol.lumiya

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
 * Global Options (Enhanced from Lumiya's GlobalOptions.java)
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
class LumiyaGlobalOptions private constructor(private val context: Context) {
    
    companion object {
        private const val PREFS_NAME = "linkpoint_lumiya_options"
        private const val BACKUP_FILENAME = "linkpoint_settings_backup.properties"
        private const val TAG = "LumiyaGlobalOptions"
        
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
        private const val KEY_DRAW_DISTANCE = "draw_distance"
        private const val KEY_SYSTEM_DEFAULTS_SET = "system_defaults_set"
        private const val KEY_USER_MODIFIED = "user_modified"
        private const val KEY_DETECTED_RAM_MB = "detected_ram_mb"
        
        // Standard RAM tiers (in MB) for snapping
        val STANDARD_RAM_TIERS_MB = listOf(
            512, 1024, 2048, 3072, 4096, 6144, 8192, 12288, 16384
        )
        
        // Texture memory options (in MB) that users can select
        val TEXTURE_MEMORY_OPTIONS_MB = listOf(
            64, 128, 256, 384, 512, 768, 1024, 1536, 2048
        )
        
        // Object cache options (in MB)
        val OBJECT_CACHE_OPTIONS_MB = listOf(
            32, 64, 128, 256, 512, 1024
        )
        
        // Draw distance options (in meters)
        val DRAW_DISTANCE_OPTIONS = listOf(
            32, 64, 96, 128, 192, 256, 384, 512
        )
        
        @Volatile
        private var instance: LumiyaGlobalOptions? = null
        
        fun getInstance(context: Context): LumiyaGlobalOptions {
            return instance ?: synchronized(this) {
                instance ?: LumiyaGlobalOptions(context.applicationContext).also {
                    instance = it
                }
            }
        }
        
        /**
         * Snap a value to the nearest standard tier
         */
        fun snapToNearestTier(value: Int, tiers: List<Int>): Int {
            if (tiers.isEmpty()) return value
            return tiers.minByOrNull { kotlin.math.abs(it - value) } ?: value
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
        get() = prefs.getBoolean(KEY_AUTO_RECONNECT, LumiyaConstants.AUTO_RECONNECT_DEFAULT)
        set(value) {
            prefs.edit { putBoolean(KEY_AUTO_RECONNECT, value) }
            markUserModified()
            saveBackup()
        }
    
    /**
     * Maximum reconnection attempts before giving up
     */
    var maxReconnectAttempts: Int
        get() = prefs.getInt(KEY_MAX_RECONNECT_ATTEMPTS, LumiyaConstants.MAX_RECONNECT_ATTEMPTS)
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
            "LumiyaOptions: Device detected - RAM: ${totalMemoryMb}MB (snapped to ${snappedRamMb}MB), Cores: $cpuCores")
        
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
            
            // Mark defaults as set
            putBoolean(KEY_SYSTEM_DEFAULTS_SET, true)
            putBoolean(KEY_USER_MODIFIED, false)
        }
        
        // Save backup immediately
        saveBackup()
        
        NetworkLogger.log(NetworkLogger.Level.INFO, NetworkLogger.Category.CONNECTION,
            "LumiyaOptions: Defaults set - HQ: $highQualityTextures, " +
            "TextureMem: ${textureMemoryLimitMb}MB, MeshMem: ${meshMemoryLimitMb}MB, " +
            "ObjCache: ${objectCacheLimitMb}MB, DrawDist: ${drawDistance}m, " +
            "MaxDownloads: $maxTextureDownloads")
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
     * Get backup file location (external storage, survives reinstall)
     */
    private fun getBackupFile(): File? {
        return try {
            val dir = context.getExternalFilesDir(null) 
                ?: Environment.getExternalStorageDirectory()
            File(dir, BACKUP_FILENAME)
        } catch (e: Exception) {
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
            
            file.outputStream().use { out ->
                props.store(out, "Linkpoint Settings Backup - Do not delete")
            }
            
            NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.CONNECTION,
                "LumiyaOptions: Settings backed up to ${file.absolutePath}")
        } catch (e: Exception) {
            NetworkLogger.log(NetworkLogger.Level.WARN, NetworkLogger.Category.CONNECTION,
                "LumiyaOptions: Failed to save backup: ${e.message}")
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
                
                // Update detected RAM for current device
                val currentRam = getTotalMemoryMb()
                val snappedRam = snapToNearestTier(currentRam, STANDARD_RAM_TIERS_MB)
                putInt(KEY_DETECTED_RAM_MB, snappedRam)
                
                putBoolean(KEY_SYSTEM_DEFAULTS_SET, true)
                putBoolean(KEY_USER_MODIFIED, true)
            }
            
            NetworkLogger.log(NetworkLogger.Level.INFO, NetworkLogger.Category.CONNECTION,
                "LumiyaOptions: Restored settings from backup")
            return true
            
        } catch (e: Exception) {
            NetworkLogger.log(NetworkLogger.Level.WARN, NetworkLogger.Category.CONNECTION,
                "LumiyaOptions: Failed to restore backup: ${e.message}")
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
        "userModified" to userModified
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
}
