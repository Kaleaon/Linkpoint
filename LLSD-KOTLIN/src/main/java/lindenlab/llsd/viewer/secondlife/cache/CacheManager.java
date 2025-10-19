/*
 * Second Life Cache Management System - Java implementation
 *
 * Based on Second Life viewer implementation
 * Copyright (C) 2010, Linden Research, Inc.
 * Java conversion Copyright (C) 2024
 */

package lindenlab.llsd.viewer.secondlife.cache;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Comprehensive cache management system for Second Life viewer.
 * 
 * Supports:
 * - Configurable cache storage (internal/external)
 * - Up to 200GB cache capacity
 * - Intelligent cache cleanup and management
 * - Multiple cache types (textures, sounds, meshes, etc.)
 * - Performance monitoring and statistics
 */
public class CacheManager {
    private static final Logger LOGGER = Logger.getLogger(CacheManager.class.getName());
    
    // Cache configuration constants
    public static final long MAX_CACHE_SIZE = 200L * 1024 * 1024 * 1024; // 200GB
    public static final long DEFAULT_CACHE_SIZE = 10L * 1024 * 1024 * 1024; // 10GB
    public static final int CLEANUP_THREAD_POOL_SIZE = 2;
    public static final long CLEANUP_INTERVAL_MS = 5 * 60 * 1000; // 5 minutes
    
    // Cache types
    public enum CacheType {
        TEXTURE("textures", "Texture cache"),
        SOUND("sounds", "Audio cache"),
        MESH("meshes", "Mesh cache"),
        ANIMATION("animations", "Animation cache"),
        CLOTHING("clothing", "Avatar clothing cache"),
        OBJECT("objects", "Object cache"),
        INVENTORY("inventory", "Inventory cache"),
        TEMPORARY("temp", "Temporary cache");
        
        private final String folderName;
        private final String description;
        
        CacheType(String folderName, String description) {
            this.folderName = folderName;
            this.description = description;
        }
        
        public String getFolderName() { return folderName; }
        public String getDescription() { return description; }
    }
    
    // Storage location options
    public enum StorageLocation {
        INTERNAL("Internal Storage", "Application data directory"),
        EXTERNAL("External Storage", "User-specified external directory"),
        SYSTEM_TEMP("System Temp", "System temporary directory"),
        USER_HOME("User Home", "User home directory");
        
        private final String displayName;
        private final String description;
        
        StorageLocation(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }
        
        public String getDisplayName() { return displayName; }
        public String getDescription() { return description; }
    }
    
    // Cache configuration
    private StorageLocation storageLocation;
    private Path baseCacheDirectory;
    private long maxCacheSize;
    private final Map<CacheType, Long> cacheTypeLimits;
    private final Map<CacheType, AtomicLong> cacheTypeSizes;
    private final Map<CacheType, Path> cacheDirectories;
    
    // Cache statistics
    private final AtomicLong totalCacheSize = new AtomicLong(0);
    private final AtomicLong totalHits = new AtomicLong(0);
    private final AtomicLong totalMisses = new AtomicLong(0);
    private final AtomicLong totalWrites = new AtomicLong(0);
    private final AtomicLong totalCleanups = new AtomicLong(0);
    
    // Thread management
    private final ScheduledExecutorService cleanupExecutor;
    private final ExecutorService ioExecutor;
    
    // Cache entry tracking
    private final Map<String, CacheEntry> cacheIndex;
    private final Object indexLock = new Object();
    
    public CacheManager() {
        this(StorageLocation.INTERNAL, DEFAULT_CACHE_SIZE);
    }
    
    public CacheManager(StorageLocation location, long maxSize) {
        this.storageLocation = location;
        this.maxCacheSize = Math.min(maxSize, MAX_CACHE_SIZE);
        this.cacheTypeLimits = new EnumMap<>(CacheType.class);
        this.cacheTypeSizes = new EnumMap<>(CacheType.class);
        this.cacheDirectories = new EnumMap<>(CacheType.class);
        this.cacheIndex = new ConcurrentHashMap<>();
        
        // Initialize default limits for each cache type
        initializeDefaultLimits();
        
        // Initialize thread pools
        this.cleanupExecutor = Executors.newScheduledThreadPool(CLEANUP_THREAD_POOL_SIZE);
        this.ioExecutor = Executors.newCachedThreadPool();
        
        // Initialize cache directories
        initializeCacheDirectories();
        
        // Start periodic cleanup
        startPeriodicCleanup();
        
        LOGGER.info("Cache manager initialized with " + location.getDisplayName() + 
                   " storage, max size: " + formatBytes(maxCacheSize));
    }
    
    private void initializeDefaultLimits() {
        long textureLimit = maxCacheSize * 60 / 100; // 60% for textures
        long soundLimit = maxCacheSize * 15 / 100;   // 15% for sounds
        long meshLimit = maxCacheSize * 15 / 100;    // 15% for meshes
        long otherLimit = maxCacheSize * 10 / 100;   // 10% for other types
        
        cacheTypeLimits.put(CacheType.TEXTURE, textureLimit);
        cacheTypeLimits.put(CacheType.SOUND, soundLimit);
        cacheTypeLimits.put(CacheType.MESH, meshLimit);
        cacheTypeLimits.put(CacheType.ANIMATION, otherLimit / 5);
        cacheTypeLimits.put(CacheType.CLOTHING, otherLimit / 5);
        cacheTypeLimits.put(CacheType.OBJECT, otherLimit / 5);
        cacheTypeLimits.put(CacheType.INVENTORY, otherLimit / 5);
        cacheTypeLimits.put(CacheType.TEMPORARY, otherLimit / 5);
        
        // Initialize size counters
        for (CacheType type : CacheType.values()) {
            cacheTypeSizes.put(type, new AtomicLong(0));
        }
    }
    
    private void initializeCacheDirectories() {
        try {
            baseCacheDirectory = getBaseCacheDirectory();
            Files.createDirectories(baseCacheDirectory);
            
            for (CacheType type : CacheType.values()) {
                Path typeDir = baseCacheDirectory.resolve(type.getFolderName());
                Files.createDirectories(typeDir);
                cacheDirectories.put(type, typeDir);
                
                // Calculate existing cache size
                long existingSize = calculateDirectorySize(typeDir);
                cacheTypeSizes.get(type).set(existingSize);
                totalCacheSize.addAndGet(existingSize);
            }
            
            // Load existing cache index
            loadCacheIndex();
            
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize cache directories", e);
            throw new RuntimeException("Cache initialization failed", e);
        }
    }
    
    private Path getBaseCacheDirectory() {
        switch (storageLocation) {
            case INTERNAL:
                return Paths.get(System.getProperty("user.dir"), "cache", "secondlife");
            case EXTERNAL:
                // Would be user-configured in real implementation
                return Paths.get(System.getProperty("user.home"), "SLCache");
            case SYSTEM_TEMP:
                return Paths.get(System.getProperty("java.io.tmpdir"), "secondlife-cache");
            case USER_HOME:
                return Paths.get(System.getProperty("user.home"), ".secondlife", "cache");
            default:
                throw new IllegalStateException("Unknown storage location: " + storageLocation);
        }
    }
    
    private long calculateDirectorySize(Path directory) {
        try {
            return Files.walk(directory)
                    .filter(Files::isRegularFile)
                    .mapToLong(path -> {
                        try {
                            return Files.size(path);
                        } catch (IOException e) {
                            return 0;
                        }
                    })
                    .sum();
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to calculate directory size: " + directory, e);
            return 0;
        }
    }
    
    /**
     * Store data in cache
     */
    public CompletableFuture<Boolean> store(CacheType type, String key, byte[] data) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Path cacheFile = getCacheFilePath(type, key);
                Path parentDir = cacheFile.getParent();
                Files.createDirectories(parentDir);
                
                // Check cache limits
                long dataSize = data.length;
                if (dataSize > cacheTypeLimits.get(type)) {
                    LOGGER.warning("Data too large for cache type " + type + ": " + formatBytes(dataSize));
                    return false;
                }
                
                // Ensure space available
                ensureSpaceAvailable(type, dataSize);
                
                // Write data
                Files.write(cacheFile, data, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
                
                // Update cache tracking
                CacheEntry entry = new CacheEntry(key, type, dataSize, System.currentTimeMillis());
                synchronized (indexLock) {
                    cacheIndex.put(key, entry);
                }
                
                cacheTypeSizes.get(type).addAndGet(dataSize);
                totalCacheSize.addAndGet(dataSize);
                totalWrites.incrementAndGet();
                
                LOGGER.fine("Cached " + type + " item: " + key + " (" + formatBytes(dataSize) + ")");
                return true;
                
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Failed to store cache item: " + key, e);
                return false;
            }
        }, ioExecutor);
    }
    
    /**
     * Retrieve data from cache
     */
    public CompletableFuture<byte[]> retrieve(CacheType type, String key) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Path cacheFile = getCacheFilePath(type, key);
                
                if (!Files.exists(cacheFile)) {
                    totalMisses.incrementAndGet();
                    return null;
                }
                
                // Update access time
                synchronized (indexLock) {
                    CacheEntry entry = cacheIndex.get(key);
                    if (entry != null) {
                        entry.updateAccessTime();
                    }
                }
                
                byte[] data = Files.readAllBytes(cacheFile);
                totalHits.incrementAndGet();
                
                LOGGER.fine("Retrieved " + type + " item: " + key + " (" + formatBytes(data.length) + ")");
                return data;
                
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Failed to retrieve cache item: " + key, e);
                totalMisses.incrementAndGet();
                return null;
            }
        }, ioExecutor);
    }
    
    /**
     * Check if item exists in cache
     */
    public boolean exists(CacheType type, String key) {
        Path cacheFile = getCacheFilePath(type, key);
        return Files.exists(cacheFile);
    }
    
    /**
     * Remove item from cache
     */
    public CompletableFuture<Boolean> remove(CacheType type, String key) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Path cacheFile = getCacheFilePath(type, key);
                
                if (Files.exists(cacheFile)) {
                    long fileSize = Files.size(cacheFile);
                    Files.delete(cacheFile);
                    
                    synchronized (indexLock) {
                        cacheIndex.remove(key);
                    }
                    
                    cacheTypeSizes.get(type).addAndGet(-fileSize);
                    totalCacheSize.addAndGet(-fileSize);
                    
                    LOGGER.fine("Removed " + type + " item: " + key + " (" + formatBytes(fileSize) + ")");
                    return true;
                }
                return false;
                
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Failed to remove cache item: " + key, e);
                return false;
            }
        }, ioExecutor);
    }
    
    /**
     * Clear all cache for a specific type
     */
    public CompletableFuture<Void> clearCache(CacheType type) {
        return CompletableFuture.runAsync(() -> {
            try {
                Path typeDir = cacheDirectories.get(type);
                if (Files.exists(typeDir)) {
                    long clearedSize = 0;
                    
                    Files.walk(typeDir)
                            .filter(Files::isRegularFile)
                            .forEach(path -> {
                                try {
                                    long size = Files.size(path);
                                    Files.delete(path);
                                    synchronized (indexLock) {
                                        // Remove from index - would need better key mapping in real implementation
                                        cacheIndex.entrySet().removeIf(entry -> entry.getValue().getType() == type);
                                    }
                                } catch (IOException e) {
                                    LOGGER.log(Level.WARNING, "Failed to delete cache file: " + path, e);
                                }
                            });
                    
                    cacheTypeSizes.get(type).set(0);
                    totalCacheSize.addAndGet(-clearedSize);
                    
                    LOGGER.info("Cleared " + type + " cache (" + formatBytes(clearedSize) + ")");
                }
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Failed to clear cache for type: " + type, e);
            }
        }, ioExecutor);
    }
    
    /**
     * Clear all caches
     */
    public CompletableFuture<Void> clearAllCache() {
        return CompletableFuture.runAsync(() -> {
            long totalCleared = totalCacheSize.get();
            
            for (CacheType type : CacheType.values()) {
                clearCache(type).join();
            }
            
            synchronized (indexLock) {
                cacheIndex.clear();
            }
            
            totalCacheSize.set(0);
            LOGGER.info("Cleared all caches (" + formatBytes(totalCleared) + ")");
        }, ioExecutor);
    }
    
    private Path getCacheFilePath(CacheType type, String key) {
        Path typeDir = cacheDirectories.get(type);
        // Create subdirectories based on key hash for better file system performance
        String hash = Integer.toHexString(key.hashCode());
        String subDir = hash.substring(0, Math.min(2, hash.length()));
        return typeDir.resolve(subDir).resolve(key);
    }
    
    private void ensureSpaceAvailable(CacheType type, long requiredSpace) {
        long currentTypeSize = cacheTypeSizes.get(type).get();
        long typeLimit = cacheTypeLimits.get(type);
        
        if (currentTypeSize + requiredSpace > typeLimit) {
            // Need to cleanup old entries
            cleanupOldestEntries(type, requiredSpace);
        }
        
        // Also check global limit
        if (totalCacheSize.get() + requiredSpace > maxCacheSize) {
            performGlobalCleanup(requiredSpace);
        }
    }
    
    private void cleanupOldestEntries(CacheType type, long spaceNeeded) {
        synchronized (indexLock) {
            List<CacheEntry> typeEntries = cacheIndex.values().stream()
                    .filter(entry -> entry.getType() == type)
                    .sorted(Comparator.comparing(CacheEntry::getLastAccessTime))
                    .collect(Collectors.toList());
            
            long freedSpace = 0;
            for (CacheEntry entry : typeEntries) {
                if (freedSpace >= spaceNeeded) break;
                
                try {
                    Path cacheFile = getCacheFilePath(entry.getType(), entry.getKey());
                    if (Files.exists(cacheFile)) {
                        long fileSize = Files.size(cacheFile);
                        Files.delete(cacheFile);
                        
                        cacheIndex.remove(entry.getKey());
                        cacheTypeSizes.get(type).addAndGet(-fileSize);
                        totalCacheSize.addAndGet(-fileSize);
                        freedSpace += fileSize;
                    }
                } catch (IOException e) {
                    LOGGER.log(Level.WARNING, "Failed to cleanup cache entry: " + entry.getKey(), e);
                }
            }
            
            if (freedSpace > 0) {
                LOGGER.info("Cleaned up " + formatBytes(freedSpace) + " from " + type + " cache");
                totalCleanups.incrementAndGet();
            }
        }
    }
    
    private void performGlobalCleanup(long spaceNeeded) {
        // Global cleanup strategy: remove oldest entries across all types
        // This is a simplified implementation
        LOGGER.info("Performing global cache cleanup, need " + formatBytes(spaceNeeded));
        
        synchronized (indexLock) {
            List<CacheEntry> allEntries = new ArrayList<>(cacheIndex.values());
            allEntries.sort(Comparator.comparing(CacheEntry::getLastAccessTime));
            
            long freedSpace = 0;
            for (CacheEntry entry : allEntries) {
                if (freedSpace >= spaceNeeded) break;
                
                remove(entry.getType(), entry.getKey()).join();
                freedSpace += entry.getSize();
            }
        }
    }
    
    private void startPeriodicCleanup() {
        cleanupExecutor.scheduleWithFixedDelay(
                this::performMaintenanceCleanup,
                CLEANUP_INTERVAL_MS,
                CLEANUP_INTERVAL_MS,
                TimeUnit.MILLISECONDS
        );
    }
    
    private void performMaintenanceCleanup() {
        LOGGER.fine("Performing maintenance cleanup");
        
        // Remove expired temporary cache entries
        synchronized (indexLock) {
            long now = System.currentTimeMillis();
            long tempCacheExpiry = 24 * 60 * 60 * 1000; // 24 hours
            
            cacheIndex.entrySet().removeIf(entry -> {
                CacheEntry cacheEntry = entry.getValue();
                if (cacheEntry.getType() == CacheType.TEMPORARY &&
                    now - cacheEntry.getCreationTime() > tempCacheExpiry) {
                    
                    remove(cacheEntry.getType(), cacheEntry.getKey());
                    return true;
                }
                return false;
            });
        }
        
        // Verify cache integrity
        verifyCacheIntegrity();
    }
    
    private void verifyCacheIntegrity() {
        // Verify that cache index matches actual files
        // This is a maintenance operation to keep index in sync
        for (CacheType type : CacheType.values()) {
            try {
                Path typeDir = cacheDirectories.get(type);
                if (!Files.exists(typeDir)) continue;
                
                long actualSize = calculateDirectorySize(typeDir);
                long trackedSize = cacheTypeSizes.get(type).get();
                
                if (Math.abs(actualSize - trackedSize) > 1024) { // Allow small discrepancies
                    LOGGER.warning("Cache size mismatch for " + type + 
                                 ": tracked=" + formatBytes(trackedSize) + 
                                 ", actual=" + formatBytes(actualSize));
                    cacheTypeSizes.get(type).set(actualSize);
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Failed to verify cache integrity for " + type, e);
            }
        }
    }
    
    private void loadCacheIndex() {
        // In a real implementation, this would load a persistent index
        // For now, we'll rebuild from filesystem
        synchronized (indexLock) {
            cacheIndex.clear();
            
            for (CacheType type : CacheType.values()) {
                try {
                    Path typeDir = cacheDirectories.get(type);
                    if (!Files.exists(typeDir)) continue;
                    
                    Files.walk(typeDir)
                            .filter(Files::isRegularFile)
                            .forEach(path -> {
                                try {
                                    String key = path.getFileName().toString();
                                    long size = Files.size(path);
                                    long lastModified = Files.getLastModifiedTime(path).toMillis();
                                    
                                    CacheEntry entry = new CacheEntry(key, type, size, lastModified);
                                    cacheIndex.put(key, entry);
                                } catch (IOException e) {
                                    LOGGER.log(Level.WARNING, "Failed to index cache file: " + path, e);
                                }
                            });
                } catch (IOException e) {
                    LOGGER.log(Level.WARNING, "Failed to load cache index for " + type, e);
                }
            }
        }
    }
    
    // Configuration methods
    
    public void setStorageLocation(StorageLocation location) {
        if (location != this.storageLocation) {
            LOGGER.info("Changing storage location from " + this.storageLocation + " to " + location);
            // In real implementation, this would migrate cache data
            this.storageLocation = location;
            initializeCacheDirectories();
        }
    }
    
    public void setMaxCacheSize(long maxSize) {
        long newSize = Math.min(maxSize, MAX_CACHE_SIZE);
        if (newSize != this.maxCacheSize) {
            LOGGER.info("Changing max cache size from " + formatBytes(this.maxCacheSize) + 
                       " to " + formatBytes(newSize));
            this.maxCacheSize = newSize;
            initializeDefaultLimits();
            
            // Cleanup if current size exceeds new limit
            if (totalCacheSize.get() > newSize) {
                performGlobalCleanup(totalCacheSize.get() - newSize);
            }
        }
    }
    
    public void setCacheTypeLimit(CacheType type, long limit) {
        cacheTypeLimits.put(type, limit);
        
        // Cleanup if current size exceeds new limit
        long currentSize = cacheTypeSizes.get(type).get();
        if (currentSize > limit) {
            cleanupOldestEntries(type, currentSize - limit);
        }
    }
    
    // Statistics and information methods
    
    public CacheStatistics getStatistics() {
        Map<CacheType, Long> currentSizes = new EnumMap<>(CacheType.class);
        for (Map.Entry<CacheType, AtomicLong> entry : cacheTypeSizes.entrySet()) {
            currentSizes.put(entry.getKey(), entry.getValue().get());
        }
        
        return new CacheStatistics(
                totalCacheSize.get(),
                maxCacheSize,
                totalHits.get(),
                totalMisses.get(),
                totalWrites.get(),
                totalCleanups.get(),
                currentSizes,
                new EnumMap<>(cacheTypeLimits),
                storageLocation,
                baseCacheDirectory.toString()
        );
    }
    
    public Map<CacheType, Long> getCacheTypeSizes() {
        Map<CacheType, Long> sizes = new EnumMap<>(CacheType.class);
        for (Map.Entry<CacheType, AtomicLong> entry : cacheTypeSizes.entrySet()) {
            sizes.put(entry.getKey(), entry.getValue().get());
        }
        return sizes;
    }
    
    public double getCacheHitRatio() {
        long hits = totalHits.get();
        long misses = totalMisses.get();
        return (hits + misses) == 0 ? 0.0 : (double) hits / (hits + misses);
    }
    
    public long getAvailableSpace() {
        return maxCacheSize - totalCacheSize.get();
    }
    
    public static String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024));
        return String.format("%.1f GB", bytes / (1024.0 * 1024 * 1024));
    }
    
    // Shutdown
    
    public void shutdown() {
        LOGGER.info("Shutting down cache manager");
        
        cleanupExecutor.shutdown();
        ioExecutor.shutdown();
        
        try {
            if (!cleanupExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                cleanupExecutor.shutdownNow();
            }
            if (!ioExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                ioExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            cleanupExecutor.shutdownNow();
            ioExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        
        // Save cache index in real implementation
        LOGGER.info("Cache manager shutdown complete");
    }
}