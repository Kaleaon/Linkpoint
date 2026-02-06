/*
 * Cache Manager - Kotlin implementation of comprehensive cache management
 *
 * Based on Second Life viewer implementation
 * Copyright (C) 2010, Linden Research, Inc.
 * Kotlin implementation Copyright (C) 2024
 */

package lindenlab.llsd.viewer.secondlife.cache

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.IOException
import java.nio.file.*
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.collections.HashMap
import kotlin.streams.toList

/**
 * Comprehensive cache management system for Second Life viewer (Kotlin implementation).
 * 
 * Features:
 * - Configurable cache storage (internal/external) up to 200GB
 * - Intelligent cache cleanup and management  
 * - Multiple cache types with performance monitoring
 * - Coroutine-based async operations
 */
class CacheManager(
    private var storageLocation: StorageLocation = StorageLocation.INTERNAL,
    private var maxCacheSize: Long = DEFAULT_CACHE_SIZE
) {
    companion object {
        private val LOGGER = Logger.getLogger(CacheManager::class.java.name)
        const val MAX_CACHE_SIZE = 200L * 1024 * 1024 * 1024 // 200GB
        const val DEFAULT_CACHE_SIZE = 10L * 1024 * 1024 * 1024 // 10GB
        
        fun formatBytes(bytes: Long): String = when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> String.format("%.1f KB", bytes / 1024.0)
            bytes < 1024 * 1024 * 1024 -> String.format("%.1f MB", bytes / (1024.0 * 1024))
            else -> String.format("%.1f GB", bytes / (1024.0 * 1024 * 1024))
        }
    }

    enum class StorageLocation(val displayName: String, val description: String) {
        INTERNAL("Internal Storage", "Application data directory"),
        EXTERNAL("External Storage", "User-specified external directory"),
        SYSTEM_TEMP("System Temp", "System temporary directory"),
        USER_HOME("User Home", "User home directory")
    }

    enum class CacheType(val folderName: String, val description: String) {
        TEXTURE("textures", "Texture cache"),
        SOUND("sounds", "Audio cache"),
        MESH("meshes", "Mesh cache"),
        ANIMATION("animations", "Animation cache"),
        CLOTHING("clothing", "Avatar clothing cache"),
        OBJECT("objects", "Object cache"),
        INVENTORY("inventory", "Inventory cache"),
        TEMPORARY("temp", "Temporary cache")
    }

    // Cache state
    private val cacheTypeLimits = EnumMap<CacheType, Long>(CacheType::class.java)
    private val cacheTypeSizes = EnumMap<CacheType, AtomicLong>(CacheType::class.java)
    private val cacheDirectories = EnumMap<CacheType, Path>(CacheType::class.java)
    private lateinit var baseCacheDirectory: Path

    // Statistics
    private val totalCacheSize = AtomicLong(0)
    private val totalHits = AtomicLong(0)
    private val totalMisses = AtomicLong(0)
    private val totalWrites = AtomicLong(0)
    private val totalCleanups = AtomicLong(0)

    // Cache index with Kotlin concurrency
    private val cacheIndex = ConcurrentHashMap<String, CacheEntry>()
    private val indexMutex = Mutex()

    // Coroutine scope for async operations
    private val cacheScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val cleanupScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    init {
        maxCacheSize = maxCacheSize.coerceAtMost(MAX_CACHE_SIZE)
        initializeDefaultLimits()
        initializeCacheDirectories()
        startPeriodicCleanup()
        
        LOGGER.info("Kotlin Cache manager initialized with ${storageLocation.displayName} storage, max size: ${formatBytes(maxCacheSize)}")
    }

    private fun initializeDefaultLimits() {
        val textureLimit = maxCacheSize * 60 / 100 // 60% for textures
        val soundLimit = maxCacheSize * 15 / 100   // 15% for sounds
        val meshLimit = maxCacheSize * 15 / 100    // 15% for meshes
        val otherLimit = maxCacheSize * 10 / 100   // 10% for other types

        cacheTypeLimits[CacheType.TEXTURE] = textureLimit
        cacheTypeLimits[CacheType.SOUND] = soundLimit
        cacheTypeLimits[CacheType.MESH] = meshLimit
        cacheTypeLimits[CacheType.ANIMATION] = otherLimit / 5
        cacheTypeLimits[CacheType.CLOTHING] = otherLimit / 5
        cacheTypeLimits[CacheType.OBJECT] = otherLimit / 5
        cacheTypeLimits[CacheType.INVENTORY] = otherLimit / 5
        cacheTypeLimits[CacheType.TEMPORARY] = otherLimit / 5

        // Initialize size counters
        CacheType.values().forEach { type ->
            cacheTypeSizes[type] = AtomicLong(0)
        }
    }

    private fun initializeCacheDirectories() {
        try {
            baseCacheDirectory = getBaseCacheDirectory()
            Files.createDirectories(baseCacheDirectory)

            CacheType.values().forEach { type ->
                val typeDir = baseCacheDirectory.resolve(type.folderName)
                Files.createDirectories(typeDir)
                cacheDirectories[type] = typeDir

                // Calculate existing cache size
                val existingSize = calculateDirectorySize(typeDir)
                cacheTypeSizes[type]?.set(existingSize)
                totalCacheSize.addAndGet(existingSize)
            }

            loadCacheIndex()
        } catch (e: IOException) {
            LOGGER.log(Level.SEVERE, "Failed to initialize cache directories", e)
            throw RuntimeException("Cache initialization failed", e)
        }
    }

    private fun getBaseCacheDirectory(): Path = when (storageLocation) {
        StorageLocation.INTERNAL -> 
            Paths.get(System.getProperty("user.dir"), "cache", "secondlife")
        StorageLocation.EXTERNAL -> 
            Paths.get(System.getProperty("user.home"), "SLCache")
        StorageLocation.SYSTEM_TEMP -> 
            Paths.get(System.getProperty("java.io.tmpdir"), "secondlife-cache")
        StorageLocation.USER_HOME -> 
            Paths.get(System.getProperty("user.home"), ".secondlife", "cache")
    }

    private fun calculateDirectorySize(directory: Path): Long = try {
        Files.walk(directory).use { paths ->
            paths.filter { Files.isRegularFile(it) }
                .mapToLong { path ->
                    try { Files.size(path) } catch (e: IOException) { 0L }
                }
                .sum()
        }
    } catch (e: IOException) {
        LOGGER.log(Level.WARNING, "Failed to calculate directory size: $directory", e)
        0L
    }

    /**
     * Store data in cache asynchronously using Kotlin coroutines
     */
    suspend fun store(type: CacheType, key: String, data: ByteArray): Boolean = withContext(Dispatchers.IO) {
        try {
            val cacheFile = getCacheFilePath(type, key)
            Files.createDirectories(cacheFile.parent)

            // Check cache limits
            val dataSize = data.size.toLong()
            val typeLimit = cacheTypeLimits[type] ?: 0L
            if (dataSize > typeLimit) {
                LOGGER.warning("Data too large for cache type $type: ${formatBytes(dataSize)}")
                return@withContext false
            }

            // Ensure space available
            ensureSpaceAvailable(type, dataSize)

            // Write data
            Files.write(cacheFile, data, StandardOpenOption.CREATE, StandardOpenOption.WRITE)

            // Update cache tracking
            val entry = CacheEntry(key, type, dataSize, System.currentTimeMillis())
            indexMutex.withLock {
                cacheIndex[key] = entry
            }

            cacheTypeSizes[type]?.addAndGet(dataSize)
            totalCacheSize.addAndGet(dataSize)
            totalWrites.incrementAndGet()

            LOGGER.fine("Cached $type item: $key (${formatBytes(dataSize)})")
            true
        } catch (e: IOException) {
            LOGGER.log(Level.WARNING, "Failed to store cache item: $key", e)
            false
        }
    }

    /**
     * Retrieve data from cache asynchronously
     */
    suspend fun retrieve(type: CacheType, key: String): ByteArray? = withContext(Dispatchers.IO) {
        try {
            val cacheFile = getCacheFilePath(type, key)

            if (!Files.exists(cacheFile)) {
                totalMisses.incrementAndGet()
                return@withContext null
            }

            // Update access time
            indexMutex.withLock {
                cacheIndex[key]?.updateAccessTime()
            }

            val data = Files.readAllBytes(cacheFile)
            totalHits.incrementAndGet()

            LOGGER.fine("Retrieved $type item: $key (${formatBytes(data.size.toLong())})")
            data
        } catch (e: IOException) {
            LOGGER.log(Level.WARNING, "Failed to retrieve cache item: $key", e)
            totalMisses.incrementAndGet()
            null
        }
    }

    /**
     * Check if item exists in cache
     */
    fun exists(type: CacheType, key: String): Boolean {
        val cacheFile = getCacheFilePath(type, key)
        return Files.exists(cacheFile)
    }

    /**
     * Remove item from cache asynchronously
     */
    suspend fun remove(type: CacheType, key: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val cacheFile = getCacheFilePath(type, key)

            if (Files.exists(cacheFile)) {
                val fileSize = Files.size(cacheFile)
                Files.delete(cacheFile)

                indexMutex.withLock {
                    cacheIndex.remove(key)
                }

                cacheTypeSizes[type]?.addAndGet(-fileSize)
                totalCacheSize.addAndGet(-fileSize)

                LOGGER.fine("Removed $type item: $key (${formatBytes(fileSize)})")
                true
            } else false
        } catch (e: IOException) {
            LOGGER.log(Level.WARNING, "Failed to remove cache item: $key", e)
            false
        }
    }

    /**
     * Clear all cache for a specific type
     */
    suspend fun clearCache(type: CacheType) = withContext(Dispatchers.IO) {
        try {
            val typeDir = cacheDirectories[type]
            if (typeDir != null && Files.exists(typeDir)) {
                var clearedSize = 0L

                Files.walk(typeDir).use { paths ->
                    paths.filter { Files.isRegularFile(it) }
                        .forEach { path ->
                            try {
                                val size = Files.size(path)
                                Files.delete(path)
                                clearedSize += size
                            } catch (e: IOException) {
                                LOGGER.log(Level.WARNING, "Failed to delete cache file: $path", e)
                            }
                        }
                }

                indexMutex.withLock {
                    cacheIndex.entries.removeIf { it.value.type == type }
                }

                cacheTypeSizes[type]?.set(0)
                totalCacheSize.addAndGet(-clearedSize)

                LOGGER.info("Cleared $type cache (${formatBytes(clearedSize)})")
            }
        } catch (e: IOException) {
            LOGGER.log(Level.SEVERE, "Failed to clear cache for type: $type", e)
        }
    }

    private fun getCacheFilePath(type: CacheType, key: String): Path {
        val typeDir = cacheDirectories[type] ?: error("Cache directory not initialized for $type")
        // Create subdirectories based on key hash for better file system performance
        val hash = key.hashCode().toString(16)
        val subDir = hash.take(2.coerceAtMost(hash.length))
        return typeDir.resolve(subDir).resolve(key)
    }

    private suspend fun ensureSpaceAvailable(type: CacheType, requiredSpace: Long) {
        val currentTypeSize = cacheTypeSizes[type]?.get() ?: 0L
        val typeLimit = cacheTypeLimits[type] ?: 0L

        if (currentTypeSize + requiredSpace > typeLimit) {
            cleanupOldestEntries(type, requiredSpace)
        }

        if (totalCacheSize.get() + requiredSpace > maxCacheSize) {
            performGlobalCleanup(requiredSpace)
        }
    }

    private suspend fun cleanupOldestEntries(type: CacheType, spaceNeeded: Long) {
        indexMutex.withLock {
            val typeEntries = cacheIndex.values
                .filter { it.type == type }
                .sortedBy { it.lastAccessTime }

            var freedSpace = 0L
            for (entry in typeEntries) {
                if (freedSpace >= spaceNeeded) break

                try {
                    val cacheFile = getCacheFilePath(entry.type, entry.key)
                    if (Files.exists(cacheFile)) {
                        val fileSize = Files.size(cacheFile)
                        Files.delete(cacheFile)

                        cacheIndex.remove(entry.key)
                        cacheTypeSizes[type]?.addAndGet(-fileSize)
                        totalCacheSize.addAndGet(-fileSize)
                        freedSpace += fileSize
                    }
                } catch (e: IOException) {
                    LOGGER.log(Level.WARNING, "Failed to cleanup cache entry: ${entry.key}", e)
                }
            }

            if (freedSpace > 0) {
                LOGGER.info("Cleaned up ${formatBytes(freedSpace)} from $type cache")
                totalCleanups.incrementAndGet()
            }
        }
    }

    private suspend fun performGlobalCleanup(spaceNeeded: Long) {
        LOGGER.info("Performing global cache cleanup, need ${formatBytes(spaceNeeded)}")

        indexMutex.withLock {
            val allEntries = cacheIndex.values.sortedBy { it.lastAccessTime }
            var freedSpace = 0L

            for (entry in allEntries) {
                if (freedSpace >= spaceNeeded) break
                remove(entry.type, entry.key)
                freedSpace += entry.size
            }
        }
    }

    private fun startPeriodicCleanup() {
        cleanupScope.launch {
            while (isActive) {
                delay(5 * 60 * 1000) // 5 minutes
                performMaintenanceCleanup()
            }
        }
    }

    private suspend fun performMaintenanceCleanup() {
        LOGGER.fine("Performing maintenance cleanup")

        // Remove expired temporary cache entries
        indexMutex.withLock {
            val now = System.currentTimeMillis()
            val tempCacheExpiry = 24 * 60 * 60 * 1000 // 24 hours

            cacheIndex.entries.removeIf { (_, entry) ->
                entry.type == CacheType.TEMPORARY &&
                        now - entry.creationTime > tempCacheExpiry.also {
                        cacheScope.launch { remove(entry.type, entry.key) }
                    true
                }
            }
        }

        // Verify cache integrity
        verifyCacheIntegrity()
    }

    private fun verifyCacheIntegrity() {
        CacheType.values().forEach { type ->
            try {
                val typeDir = cacheDirectories[type]
                if (typeDir != null && Files.exists(typeDir)) {
                    val actualSize = calculateDirectorySize(typeDir)
                    val trackedSize = cacheTypeSizes[type]?.get() ?: 0L

                    if (kotlin.math.abs(actualSize - trackedSize) > 1024) {
                        LOGGER.warning("Cache size mismatch for $type: tracked=${formatBytes(trackedSize)}, actual=${formatBytes(actualSize)}")
                        cacheTypeSizes[type]?.set(actualSize)
                    }
                }
            } catch (e: Exception) {
                LOGGER.log(Level.WARNING, "Failed to verify cache integrity for $type", e)
            }
        }
    }

    private fun loadCacheIndex() {
        runBlocking {
            indexMutex.withLock {
                cacheIndex.clear()

                CacheType.values().forEach { type ->
                    try {
                        val typeDir = cacheDirectories[type]
                        if (typeDir != null && Files.exists(typeDir)) {
                            Files.walk(typeDir).use { paths ->
                                paths.filter { Files.isRegularFile(it) }
                                    .forEach { path ->
                                        try {
                                            val key = path.fileName.toString()
                                            val size = Files.size(path)
                                            val lastModified = Files.getLastModifiedTime(path).toMillis()

                                            val entry = CacheEntry(key, type, size, lastModified)
                                            cacheIndex[key] = entry
                                        } catch (e: IOException) {
                                            LOGGER.log(Level.WARNING, "Failed to index cache file: $path", e)
                                        }
                                    }
                            }
                        }
                    } catch (e: IOException) {
                        LOGGER.log(Level.WARNING, "Failed to load cache index for $type", e)
                    }
                }
            }
        }
    }

    // Configuration methods
    fun setStorageLocation(location: StorageLocation) {
        if (location != this.storageLocation) {
            LOGGER.info("Changing storage location from $storageLocation to $location")
            this.storageLocation = location
            initializeCacheDirectories()
        }
    }

    fun setMaxCacheSize(maxSize: Long) {
        val newSize = maxSize.coerceAtMost(MAX_CACHE_SIZE)
        if (newSize != this.maxCacheSize) {
            LOGGER.info("Changing max cache size from ${formatBytes(this.maxCacheSize)} to ${formatBytes(newSize)}")
            this.maxCacheSize = newSize
            initializeDefaultLimits()

            // Cleanup if current size exceeds new limit
            if (totalCacheSize.get() > newSize) {
                cacheScope.launch {
                    performGlobalCleanup(totalCacheSize.get() - newSize)
                }
            }
        }
    }

    // Statistics
    fun getStatistics(): CacheStatistics {
        val currentSizes = EnumMap<CacheType, Long>(CacheType::class.java)
        cacheTypeSizes.forEach { (type, atomicLong) ->
            currentSizes[type] = atomicLong.get()
        }

        return CacheStatistics(
            totalCacheSize.get(),
            maxCacheSize,
            totalHits.get(),
            totalMisses.get(),
            totalWrites.get(),
            totalCleanups.get(),
            currentSizes,
            EnumMap(cacheTypeLimits),
            storageLocation,
            baseCacheDirectory.toString()
        )
    }

    fun getCacheTypeSizes(): Map<CacheType, Long> {
        val sizes = EnumMap<CacheType, Long>(CacheType::class.java)
        cacheTypeSizes.forEach { (type, atomicLong) ->
            sizes[type] = atomicLong.get()
        }
        return sizes
    }

    fun getCacheHitRatio(): Double {
        val hits = totalHits.get()
        val misses = totalMisses.get()
        return if (hits + misses == 0L) 0.0 else hits.toDouble() / (hits + misses)
    }

    fun getAvailableSpace(): Long = maxCacheSize - totalCacheSize.get()

    // Shutdown
    fun shutdown() {
        LOGGER.info("Shutting down Kotlin cache manager")
        
        cacheScope.cancel()
        cleanupScope.cancel()
        
        LOGGER.info("Kotlin cache manager shutdown complete")
    }


}

/**
 * Cache entry with Kotlin properties
 */
data class CacheEntry(
    val key: String,
    val type: CacheManager.CacheType,
    val size: Long,
    val creationTime: Long
) {
    var lastAccessTime: Long = creationTime
        private set
    var accessCount: Int = 0
        private set

    fun updateAccessTime() {
        lastAccessTime = System.currentTimeMillis()
        accessCount++
    }

    val age: Long get() = System.currentTimeMillis() - creationTime
    val timeSinceLastAccess: Long get() = System.currentTimeMillis() - lastAccessTime
}

/**
 * Cache statistics data class
 */
data class CacheStatistics(
    val totalSize: Long,
    val maxSize: Long,
    val totalHits: Long,
    val totalMisses: Long,
    val totalWrites: Long,
    val totalCleanups: Long,
    val typeSizes: Map<CacheManager.CacheType, Long>,
    val typeLimits: Map<CacheManager.CacheType, Long>,
    val storageLocation: CacheManager.StorageLocation,
    val basePath: String
) {
    val usagePercent: Double get() = if (maxSize == 0L) 0.0 else totalSize.toDouble() / maxSize * 100.0
    val hitRatio: Double get() = if (totalRequests == 0L) 0.0 else totalHits.toDouble() / totalRequests
    val availableSpace: Long get() = maxSize - totalSize
    val totalRequests: Long get() = totalHits + totalMisses

    fun getTypeSize(type: CacheManager.CacheType): Long = typeSizes[type] ?: 0L
    fun getTypeLimit(type: CacheManager.CacheType): Long = typeLimits[type] ?: 0L
    fun getTypeUsagePercent(type: CacheManager.CacheType): Double {
        val limit = getTypeLimit(type)
        return if (limit == 0L) 0.0 else getTypeSize(type).toDouble() / limit * 100.0
    }

    override fun toString(): String = buildString {
        appendLine("Cache Statistics:")
        appendLine("  Total Size: ${CacheManager.formatBytes(totalSize)} / ${CacheManager.formatBytes(maxSize)} (${String.format("%.1f", usagePercent)}%)")
        appendLine("  Available: ${CacheManager.formatBytes(availableSpace)}")
        appendLine("  Hit Ratio: ${String.format("%.2f", hitRatio * 100)}%")
        appendLine("  Requests: $totalRequests ($totalHits hits, $totalMisses misses)")
        appendLine("  Writes: $totalWrites")
        appendLine("  Cleanups: $totalCleanups")
        appendLine("  Storage: ${storageLocation.displayName}")
        appendLine("  Path: $basePath")
        appendLine()
        appendLine("  Type Breakdown:")
        CacheManager.CacheType.values().forEach { type ->
            val size = getTypeSize(type)
            val limit = getTypeLimit(type)
            val percent = getTypeUsagePercent(type)
            appendLine("    ${type.name}: ${CacheManager.formatBytes(size)} / ${CacheManager.formatBytes(limit)} (${String.format("%.1f", percent)}%)")
        }
    }
}