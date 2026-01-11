package com.linkpoint.assets

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Centralized cache management with configurable sizes
 * 
 * Provides:
 * - Configurable disk cache size (128MB - 4GB)
 * - Configurable memory cache size (25MB - 200MB)
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
        
        // Size limits - generous limits for smooth performance
        const val MIN_DISK_CACHE_MB = 512
        const val MAX_DISK_CACHE_MB = 10240  // 10GB - large cache prevents re-downloading
        const val DEFAULT_DISK_CACHE_MB = 2048  // 2GB default for good performance
        
        const val MIN_MEMORY_CACHE_MB = 100
        const val MAX_MEMORY_CACHE_MB = 2048  // 2GB - for devices with plenty of RAM
        const val DEFAULT_MEMORY_CACHE_MB = 512  // 512MB default - good balance
        
        // Low space threshold (200MB free required)
        const val LOW_SPACE_THRESHOLD_MB = 200
        
        // Cache subdirectories
        private const val TEXTURES_DIR = "textures"
        private const val MESHES_DIR = "meshes"
        private const val SOUNDS_DIR = "sounds"
        private const val ANIMATIONS_DIR = "animations"
        private const val GENERAL_DIR = "asset_cache"
    }
    
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
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
     * Get comprehensive cache statistics
     */
    suspend fun getCacheStats(): CacheStatistics = withContext(Dispatchers.IO) {
        val texturesDir = File(context.cacheDir, TEXTURES_DIR)
        val meshesDir = File(context.cacheDir, MESHES_DIR)
        val soundsDir = File(context.cacheDir, SOUNDS_DIR)
        val animationsDir = File(context.cacheDir, ANIMATIONS_DIR)
        val generalDir = File(context.cacheDir, GENERAL_DIR)
        
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
        
        val totalSize = texturesSize + meshesSize + soundsSize + animationsSize + generalSize
        val totalCount = texturesCount + meshesCount + soundsCount + animationsCount + generalCount
        
        val maxSize = getDiskCacheSizeMB().toLong() * 1024 * 1024
        val usagePercent = if (maxSize > 0) (totalSize.toFloat() / maxSize * 100).toInt() else 0
        
        // Get available space
        val statFs = android.os.StatFs(context.cacheDir.path)
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
     * Clear all cache
     */
    suspend fun clearAllCache(): ClearResult = withContext(Dispatchers.IO) {
        var clearedBytes = 0L
        var clearedFiles = 0
        var errors = 0
        
        val directories = listOf(
            File(context.cacheDir, TEXTURES_DIR),
            File(context.cacheDir, MESHES_DIR),
            File(context.cacheDir, SOUNDS_DIR),
            File(context.cacheDir, ANIMATIONS_DIR),
            File(context.cacheDir, GENERAL_DIR)
        )
        
        for (dir in directories) {
            val result = clearDirectory(dir)
            clearedBytes += result.first
            clearedFiles += result.second
            errors += result.third
        }
        
        Log.i(TAG, "Cleared ${formatSize(clearedBytes)} ($clearedFiles files), $errors errors")
        
        ClearResult(
            clearedBytes = clearedBytes,
            clearedFiles = clearedFiles,
            errors = errors,
            success = errors == 0
        )
    }
    
    /**
     * Clear specific cache type
     */
    suspend fun clearCache(assetType: CacheableAssetType): ClearResult = withContext(Dispatchers.IO) {
        val dirName = when (assetType) {
            CacheableAssetType.TEXTURES -> TEXTURES_DIR
            CacheableAssetType.MESHES -> MESHES_DIR
            CacheableAssetType.SOUNDS -> SOUNDS_DIR
            CacheableAssetType.ANIMATIONS -> ANIMATIONS_DIR
        }
        
        val dir = File(context.cacheDir, dirName)
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
        
        // Need to prune - delete oldest files first
        val targetSize = (maxBytes * 0.8).toLong() // Prune to 80% capacity
        var currentSize = stats.totalSizeBytes
        var prunedBytes = 0L
        var prunedFiles = 0
        
        val allCacheFiles = mutableListOf<File>()
        listOf(TEXTURES_DIR, MESHES_DIR, SOUNDS_DIR, ANIMATIONS_DIR, GENERAL_DIR).forEach { dirName ->
            val dir = File(context.cacheDir, dirName)
            dir.listFiles()?.let { allCacheFiles.addAll(it) }
        }
        
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
        
        // Check for low space
        if (stats.isLowSpace && isAutoClearOnLowSpaceEnabled()) {
            val clearResult = clearAllCache()
            actionTaken = "Auto-cleared due to low space"
            prunedBytes = clearResult.clearedBytes
            Log.w(TAG, "Low space detected - cleared cache: ${formatSize(prunedBytes)}")
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
     * Get cache directory for specific asset type
     */
    fun getCacheDirectory(assetType: CacheableAssetType): File {
        val dirName = when (assetType) {
            CacheableAssetType.TEXTURES -> TEXTURES_DIR
            CacheableAssetType.MESHES -> MESHES_DIR
            CacheableAssetType.SOUNDS -> SOUNDS_DIR
            CacheableAssetType.ANIMATIONS -> ANIMATIONS_DIR
        }
        return File(context.cacheDir, dirName).also { it.mkdirs() }
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
