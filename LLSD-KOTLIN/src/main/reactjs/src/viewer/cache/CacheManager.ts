/*
 * Cache Manager - TypeScript/React implementation of comprehensive cache management
 *
 * Based on Second Life viewer implementation
 * Copyright (C) 2010, Linden Research, Inc.
 * TypeScript implementation Copyright (C) 2024
 */

/**
 * Comprehensive cache management system for Second Life viewer (TypeScript/React implementation).
 * 
 * Features:
 * - IndexedDB-based persistent storage for web browsers
 * - Configurable cache storage up to 200GB (browser permitting)
 * - Promise-based async operations with TypeScript types
 * - Real-time performance monitoring with React hooks integration
 */

export enum StorageLocation {
    BROWSER_CACHE = 'BROWSER_CACHE',
    INDEXEDDB = 'INDEXEDDB',
    LOCAL_STORAGE = 'LOCAL_STORAGE',
    SESSION_STORAGE = 'SESSION_STORAGE'
}

export enum CacheType {
    TEXTURE = 'TEXTURE',
    SOUND = 'SOUND',
    MESH = 'MESH',
    ANIMATION = 'ANIMATION',
    CLOTHING = 'CLOTHING',
    OBJECT = 'OBJECT',
    INVENTORY = 'INVENTORY',
    TEMPORARY = 'TEMPORARY'
}

export interface CacheEntry {
    key: string;
    type: CacheType;
    data: ArrayBuffer;
    size: number;
    creationTime: number;
    lastAccessTime: number;
    accessCount: number;
}

export interface CacheStatistics {
    totalSize: number;
    maxSize: number;
    totalHits: number;
    totalMisses: number;
    totalWrites: number;
    totalCleanups: number;
    typeSizes: Map<CacheType, number>;
    typeLimits: Map<CacheType, number>;
    storageLocation: StorageLocation;
    basePath: string;
    usagePercent: number;
    hitRatio: number;
    availableSpace: number;
    totalRequests: number;
}

export interface CacheConfiguration {
    storageLocation: StorageLocation;
    maxCacheSize: number;
    cleanupInterval: number;
    enableAutoCleanup: boolean;
}

export class CacheManager {
    public static readonly MAX_CACHE_SIZE = 200 * 1024 * 1024 * 1024; // 200GB
    public static readonly DEFAULT_CACHE_SIZE = 10 * 1024 * 1024 * 1024; // 10GB

    private configuration: CacheConfiguration;
    private cacheDatabase: IDBDatabase | null = null;
    private cacheIndex = new Map<string, CacheEntry>();
    
    // Statistics tracking
    private statistics = {
        totalSize: 0,
        totalHits: 0,
        totalMisses: 0,
        totalWrites: 0,
        totalCleanups: 0,
        typeSizes: new Map<CacheType, number>(),
        typeLimits: new Map<CacheType, number>()
    };

    // React-style event listeners for cache updates
    private listeners: Array<(stats: CacheStatistics) => void> = [];

    constructor(
        storageLocation: StorageLocation = StorageLocation.INDEXEDDB,
        maxCacheSize: number = CacheManager.DEFAULT_CACHE_SIZE
    ) {
        this.configuration = {
            storageLocation,
            maxCacheSize: Math.min(maxCacheSize, CacheManager.MAX_CACHE_SIZE),
            cleanupInterval: 5 * 60 * 1000, // 5 minutes
            enableAutoCleanup: true
        };

        this.initializeDefaultLimits();
        this.initializeCacheStorage();
        this.startPeriodicCleanup();

        console.log(`TypeScript Cache manager initialized with ${storageLocation} storage, max size: ${CacheManager.formatBytes(this.configuration.maxCacheSize)}`);
    }

    private initializeDefaultLimits(): void {
        const maxSize = this.configuration.maxCacheSize;
        
        // Distribute cache space across types
        this.statistics.typeLimits.set(CacheType.TEXTURE, Math.floor(maxSize * 0.60)); // 60%
        this.statistics.typeLimits.set(CacheType.SOUND, Math.floor(maxSize * 0.15));   // 15%
        this.statistics.typeLimits.set(CacheType.MESH, Math.floor(maxSize * 0.15));    // 15%
        this.statistics.typeLimits.set(CacheType.ANIMATION, Math.floor(maxSize * 0.02)); // 2%
        this.statistics.typeLimits.set(CacheType.CLOTHING, Math.floor(maxSize * 0.02));  // 2%
        this.statistics.typeLimits.set(CacheType.OBJECT, Math.floor(maxSize * 0.03));    // 3%
        this.statistics.typeLimits.set(CacheType.INVENTORY, Math.floor(maxSize * 0.02)); // 2%
        this.statistics.typeLimits.set(CacheType.TEMPORARY, Math.floor(maxSize * 0.01)); // 1%

        // Initialize size counters
        Object.values(CacheType).forEach(type => {
            this.statistics.typeSizes.set(type, 0);
        });
    }

    private async initializeCacheStorage(): Promise<void> {
        switch (this.configuration.storageLocation) {
            case StorageLocation.INDEXEDDB:
                await this.initializeIndexedDB();
                break;
            case StorageLocation.LOCAL_STORAGE:
            case StorageLocation.SESSION_STORAGE:
                this.initializeWebStorage();
                break;
            case StorageLocation.BROWSER_CACHE:
                await this.initializeCacheAPI();
                break;
        }

        await this.loadCacheIndex();
    }

    private async initializeIndexedDB(): Promise<void> {
        return new Promise((resolve, reject) => {
            const request = indexedDB.open('SecondLifeCache', 1);

            request.onerror = () => reject(new Error('Failed to open IndexedDB'));
            
            request.onsuccess = (event) => {
                this.cacheDatabase = (event.target as IDBOpenDBRequest).result;
                resolve();
            };

            request.onupgradeneeded = (event) => {
                const db = (event.target as IDBOpenDBRequest).result;
                
                // Create object stores for each cache type
                Object.values(CacheType).forEach(type => {
                    if (!db.objectStoreNames.contains(type)) {
                        const store = db.createObjectStore(type, { keyPath: 'key' });
                        store.createIndex('type', 'type', { unique: false });
                        store.createIndex('lastAccessTime', 'lastAccessTime', { unique: false });
                    }
                });
            };
        });
    }

    private initializeWebStorage(): void {
        // Web storage initialization (simplified for demo)
        console.log(`Initialized ${this.configuration.storageLocation} storage`);
    }

    private async initializeCacheAPI(): Promise<void> {
        if ('caches' in window) {
            // Cache API initialization
            console.log('Initialized Cache API storage');
        } else {
            throw new Error('Cache API not supported');
        }
    }

    /**
     * Store data in cache with TypeScript type safety
     */
    public async store(type: CacheType, key: string, data: ArrayBuffer): Promise<boolean> {
        try {
            const dataSize = data.byteLength;
            const typeLimit = this.statistics.typeLimits.get(type) || 0;

            if (dataSize > typeLimit) {
                console.warn(`Data too large for cache type ${type}: ${CacheManager.formatBytes(dataSize)}`);
                return false;
            }

            // Ensure space available
            await this.ensureSpaceAvailable(type, dataSize);

            // Create cache entry
            const entry: CacheEntry = {
                key,
                type,
                data,
                size: dataSize,
                creationTime: Date.now(),
                lastAccessTime: Date.now(),
                accessCount: 0
            };

            // Store in database
            await this.storeInDatabase(entry);

            // Update cache tracking
            this.cacheIndex.set(key, entry);
            this.statistics.typeSizes.set(type, (this.statistics.typeSizes.get(type) || 0) + dataSize);
            this.statistics.totalSize += dataSize;
            this.statistics.totalWrites++;

            // Notify listeners
            this.notifyListeners();

            console.debug(`Cached ${type} item: ${key} (${CacheManager.formatBytes(dataSize)})`);
            return true;

        } catch (error) {
            console.error(`Failed to store cache item: ${key}`, error);
            return false;
        }
    }

    /**
     * Retrieve data from cache with promise-based async
     */
    public async retrieve(type: CacheType, key: string): Promise<ArrayBuffer | null> {
        try {
            // Check cache index first
            const entry = this.cacheIndex.get(key);
            if (!entry || entry.type !== type) {
                this.statistics.totalMisses++;
                this.notifyListeners();
                return null;
            }

            // Update access time
            entry.lastAccessTime = Date.now();
            entry.accessCount++;

            // Retrieve from database
            const data = await this.retrieveFromDatabase(type, key);
            if (data) {
                this.statistics.totalHits++;
                console.debug(`Retrieved ${type} item: ${key} (${CacheManager.formatBytes(data.byteLength)})`);
            } else {
                this.statistics.totalMisses++;
            }

            this.notifyListeners();
            return data;

        } catch (error) {
            console.error(`Failed to retrieve cache item: ${key}`, error);
            this.statistics.totalMisses++;
            this.notifyListeners();
            return null;
        }
    }

    /**
     * Check if item exists in cache
     */
    public exists(type: CacheType, key: string): boolean {
        const entry = this.cacheIndex.get(key);
        return entry !== undefined && entry.type === type;
    }

    /**
     * Remove item from cache
     */
    public async remove(type: CacheType, key: string): Promise<boolean> {
        try {
            const entry = this.cacheIndex.get(key);
            if (!entry || entry.type !== type) {
                return false;
            }

            // Remove from database
            await this.removeFromDatabase(type, key);

            // Update tracking
            this.cacheIndex.delete(key);
            this.statistics.typeSizes.set(type, (this.statistics.typeSizes.get(type) || 0) - entry.size);
            this.statistics.totalSize -= entry.size;

            this.notifyListeners();
            console.debug(`Removed ${type} item: ${key} (${CacheManager.formatBytes(entry.size)})`);
            return true;

        } catch (error) {
            console.error(`Failed to remove cache item: ${key}`, error);
            return false;
        }
    }

    /**
     * Clear all cache for a specific type
     */
    public async clearCache(type: CacheType): Promise<void> {
        try {
            // Remove all entries of this type
            const keysToRemove: string[] = [];
            let clearedSize = 0;

            this.cacheIndex.forEach((entry, key) => {
                if (entry.type === type) {
                    keysToRemove.push(key);
                    clearedSize += entry.size;
                }
            });

            // Clear from database
            await this.clearTypeFromDatabase(type);

            // Update tracking
            keysToRemove.forEach(key => this.cacheIndex.delete(key));
            this.statistics.typeSizes.set(type, 0);
            this.statistics.totalSize -= clearedSize;

            this.notifyListeners();
            console.log(`Cleared ${type} cache (${CacheManager.formatBytes(clearedSize)})`);

        } catch (error) {
            console.error(`Failed to clear cache for type: ${type}`, error);
        }
    }

    /**
     * Clear all cache data
     */
    public async clearAllCache(): Promise<void> {
        try {
            // Clear all types
            await Promise.all(
                Object.values(CacheType).map(type => this.clearCache(type))
            );

            console.log('Cleared all cache data');
        } catch (error) {
            console.error('Failed to clear all cache', error);
        }
    }

    private async storeInDatabase(entry: CacheEntry): Promise<void> {
        if (!this.cacheDatabase) {
            throw new Error('Database not initialized');
        }

        return new Promise((resolve, reject) => {
            const transaction = this.cacheDatabase!.transaction([entry.type], 'readwrite');
            const store = transaction.objectStore(entry.type);
            const request = store.put(entry);

            request.onsuccess = () => resolve();
            request.onerror = () => reject(new Error('Failed to store in database'));
        });
    }

    private async retrieveFromDatabase(type: CacheType, key: string): Promise<ArrayBuffer | null> {
        if (!this.cacheDatabase) {
            return null;
        }

        return new Promise((resolve, reject) => {
            const transaction = this.cacheDatabase!.transaction([type], 'readonly');
            const store = transaction.objectStore(type);
            const request = store.get(key);

            request.onsuccess = () => {
                const result = request.result;
                resolve(result ? result.data : null);
            };
            request.onerror = () => reject(new Error('Failed to retrieve from database'));
        });
    }

    private async removeFromDatabase(type: CacheType, key: string): Promise<void> {
        if (!this.cacheDatabase) {
            throw new Error('Database not initialized');
        }

        return new Promise((resolve, reject) => {
            const transaction = this.cacheDatabase!.transaction([type], 'readwrite');
            const store = transaction.objectStore(type);
            const request = store.delete(key);

            request.onsuccess = () => resolve();
            request.onerror = () => reject(new Error('Failed to remove from database'));
        });
    }

    private async clearTypeFromDatabase(type: CacheType): Promise<void> {
        if (!this.cacheDatabase) {
            throw new Error('Database not initialized');
        }

        return new Promise((resolve, reject) => {
            const transaction = this.cacheDatabase!.transaction([type], 'readwrite');
            const store = transaction.objectStore(type);
            const request = store.clear();

            request.onsuccess = () => resolve();
            request.onerror = () => reject(new Error('Failed to clear from database'));
        });
    }

    private async loadCacheIndex(): Promise<void> {
        if (!this.cacheDatabase) {
            return;
        }

        this.cacheIndex.clear();

        for (const type of Object.values(CacheType)) {
            try {
                await new Promise<void>((resolve, reject) => {
                    const transaction = this.cacheDatabase!.transaction([type], 'readonly');
                    const store = transaction.objectStore(type);
                    const request = store.getAll();

                    request.onsuccess = () => {
                        const entries = request.result as CacheEntry[];
                        let typeSize = 0;

                        entries.forEach(entry => {
                            this.cacheIndex.set(entry.key, entry);
                            typeSize += entry.size;
                        });

                        this.statistics.typeSizes.set(type, typeSize);
                        this.statistics.totalSize += typeSize;
                        resolve();
                    };

                    request.onerror = () => reject(new Error(`Failed to load cache index for ${type}`));
                });
            } catch (error) {
                console.error(`Failed to load cache index for ${type}`, error);
            }
        }
    }

    private async ensureSpaceAvailable(type: CacheType, requiredSpace: number): Promise<void> {
        const currentTypeSize = this.statistics.typeSizes.get(type) || 0;
        const typeLimit = this.statistics.typeLimits.get(type) || 0;

        if (currentTypeSize + requiredSpace > typeLimit) {
            await this.cleanupOldestEntries(type, requiredSpace);
        }

        if (this.statistics.totalSize + requiredSpace > this.configuration.maxCacheSize) {
            await this.performGlobalCleanup(requiredSpace);
        }
    }

    private async cleanupOldestEntries(type: CacheType, spaceNeeded: number): Promise<void> {
        const typeEntries: CacheEntry[] = [];
        
        this.cacheIndex.forEach(entry => {
            if (entry.type === type) {
                typeEntries.push(entry);
            }
        });

        // Sort by last access time (oldest first)
        typeEntries.sort((a, b) => a.lastAccessTime - b.lastAccessTime);

        let freedSpace = 0;
        for (const entry of typeEntries) {
            if (freedSpace >= spaceNeeded) break;

            await this.remove(entry.type, entry.key);
            freedSpace += entry.size;
        }

        if (freedSpace > 0) {
            console.log(`Cleaned up ${CacheManager.formatBytes(freedSpace)} from ${type} cache`);
            this.statistics.totalCleanups++;
        }
    }

    private async performGlobalCleanup(spaceNeeded: number): Promise<void> {
        console.log(`Performing global cache cleanup, need ${CacheManager.formatBytes(spaceNeeded)}`);

        const allEntries: CacheEntry[] = Array.from(this.cacheIndex.values());
        allEntries.sort((a, b) => a.lastAccessTime - b.lastAccessTime);

        let freedSpace = 0;
        for (const entry of allEntries) {
            if (freedSpace >= spaceNeeded) break;
            
            await this.remove(entry.type, entry.key);
            freedSpace += entry.size;
        }
    }

    private startPeriodicCleanup(): void {
        if (this.configuration.enableAutoCleanup) {
            setInterval(() => {
                this.performMaintenanceCleanup();
            }, this.configuration.cleanupInterval);
        }
    }

    private async performMaintenanceCleanup(): Promise<void> {
        console.debug('Performing maintenance cleanup');

        // Remove expired temporary cache entries
        const now = Date.now();
        const tempCacheExpiry = 24 * 60 * 60 * 1000; // 24 hours

        const expiredKeys: string[] = [];
        this.cacheIndex.forEach((entry, key) => {
            if (entry.type === CacheType.TEMPORARY && 
                now - entry.creationTime > tempCacheExpiry) {
                expiredKeys.push(key);
            }
        });

        // Remove expired entries
        await Promise.all(
            expiredKeys.map(key => {
                const entry = this.cacheIndex.get(key);
                return entry ? this.remove(entry.type, key) : Promise.resolve(false);
            })
        );
    }

    // Configuration methods
    public setStorageLocation(location: StorageLocation): void {
        if (location !== this.configuration.storageLocation) {
            console.log(`Changing storage location from ${this.configuration.storageLocation} to ${location}`);
            this.configuration.storageLocation = location;
            this.initializeCacheStorage();
        }
    }

    public setMaxCacheSize(maxSize: number): void {
        const newSize = Math.min(maxSize, CacheManager.MAX_CACHE_SIZE);
        if (newSize !== this.configuration.maxCacheSize) {
            console.log(`Changing max cache size from ${CacheManager.formatBytes(this.configuration.maxCacheSize)} to ${CacheManager.formatBytes(newSize)}`);
            this.configuration.maxCacheSize = newSize;
            this.initializeDefaultLimits();

            // Cleanup if current size exceeds new limit
            if (this.statistics.totalSize > newSize) {
                this.performGlobalCleanup(this.statistics.totalSize - newSize);
            }
        }
    }

    // Statistics and React integration
    public getStatistics(): CacheStatistics {
        const hitRatio = this.statistics.totalHits + this.statistics.totalMisses === 0 ? 0 :
            this.statistics.totalHits / (this.statistics.totalHits + this.statistics.totalMisses);

        return {
            totalSize: this.statistics.totalSize,
            maxSize: this.configuration.maxCacheSize,
            totalHits: this.statistics.totalHits,
            totalMisses: this.statistics.totalMisses,
            totalWrites: this.statistics.totalWrites,
            totalCleanups: this.statistics.totalCleanups,
            typeSizes: new Map(this.statistics.typeSizes),
            typeLimits: new Map(this.statistics.typeLimits),
            storageLocation: this.configuration.storageLocation,
            basePath: 'browser',
            usagePercent: this.configuration.maxCacheSize === 0 ? 0 : 
                (this.statistics.totalSize / this.configuration.maxCacheSize) * 100,
            hitRatio,
            availableSpace: this.configuration.maxCacheSize - this.statistics.totalSize,
            totalRequests: this.statistics.totalHits + this.statistics.totalMisses
        };
    }

    /**
     * Subscribe to cache statistics updates (React-style)
     */
    public subscribe(listener: (stats: CacheStatistics) => void): () => void {
        this.listeners.push(listener);
        
        // Return unsubscribe function
        return () => {
            const index = this.listeners.indexOf(listener);
            if (index > -1) {
                this.listeners.splice(index, 1);
            }
        };
    }

    private notifyListeners(): void {
        const stats = this.getStatistics();
        this.listeners.forEach(listener => {
            try {
                listener(stats);
            } catch (error) {
                console.error('Error in cache statistics listener', error);
            }
        });
    }

    // Utility methods
    public static formatBytes(bytes: number): string {
        if (bytes < 1024) return `${bytes} B`;
        if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`;
        if (bytes < 1024 * 1024 * 1024) return `${(bytes / (1024 * 1024)).toFixed(1)} MB`;
        return `${(bytes / (1024 * 1024 * 1024)).toFixed(1)} GB`;
    }

    public shutdown(): void {
        console.log('Shutting down TypeScript cache manager');

        // Close database connection
        if (this.cacheDatabase) {
            this.cacheDatabase.close();
            this.cacheDatabase = null;
        }

        // Clear listeners
        this.listeners.length = 0;

        console.log('TypeScript cache manager shutdown complete');
    }
}