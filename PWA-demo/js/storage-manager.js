/**
 * Linkpoint Storage Manager
 * Manages multi-gigabyte persistent storage for Second Life assets
 * 
 * Features:
 * - Persistent storage request (prevents browser eviction)
 * - IndexedDB for structured SL asset metadata
 * - Cache API for binary asset storage (textures, meshes, sounds)
 * - Storage quota monitoring and management
 * - LRU eviction when quota is reached
 */

class StorageManager {
  constructor() {
    this.dbName = 'LinkpointDB';
    this.dbVersion = 1;
    this.db = null;
    this.storageInfo = {
      persisted: false,
      usage: 0,
      quota: 0,
      usagePercent: 0
    };
    
    // Asset stores
    this.stores = {
      textures: 'textures',
      meshes: 'meshes',
      sounds: 'sounds',
      animations: 'animations',
      landmarks: 'landmarks',
      notecards: 'notecards',
      scripts: 'scripts',
      wearables: 'wearables'
    };
  }
  
  /**
   * Initialize storage system
   */
  async init() {
    console.log('[Storage] Initializing storage manager...');
    
    try {
      // Request persistent storage first
      await this.requestPersistentStorage();
      
      // Initialize IndexedDB
      await this.initDB();
      
      // Update storage info
      await this.updateStorageInfo();
      
      console.log('[Storage] ✅ Storage manager initialized');
      console.log(`[Storage] Quota: ${this.formatBytes(this.storageInfo.quota)}`);
      console.log(`[Storage] Usage: ${this.formatBytes(this.storageInfo.usage)} (${this.storageInfo.usagePercent.toFixed(1)}%)`);
      console.log(`[Storage] Persisted: ${this.storageInfo.persisted}`);
      
      return true;
    } catch (error) {
      console.error('[Storage] Initialization failed:', error);
      return false;
    }
  }
  
  /**
   * Request persistent storage to prevent browser eviction
   */
  async requestPersistentStorage() {
    if (!navigator.storage || !navigator.storage.persist) {
      console.warn('[Storage] Persistent storage API not available');
      return false;
    }
    
    try {
      // Check if already persisted
      const isPersisted = await navigator.storage.persisted();
      
      if (isPersisted) {
        console.log('[Storage] ✅ Storage already persisted');
        this.storageInfo.persisted = true;
        return true;
      }
      
      // Request persistence
      const granted = await navigator.storage.persist();
      this.storageInfo.persisted = granted;
      
      if (granted) {
        console.log('[Storage] ✅ Persistent storage granted');
      } else {
        console.warn('[Storage] ⚠️ Persistent storage denied - data may be evicted');
      }
      
      return granted;
    } catch (error) {
      console.error('[Storage] Failed to request persistent storage:', error);
      return false;
    }
  }
  
  /**
   * Initialize IndexedDB
   */
  async initDB() {
    return new Promise((resolve, reject) => {
      const request = indexedDB.open(this.dbName, this.dbVersion);
      
      request.onerror = () => {
        reject(new Error('Failed to open IndexedDB'));
      };
      
      request.onsuccess = (event) => {
        this.db = event.target.result;
        resolve(this.db);
      };
      
      request.onupgradeneeded = (event) => {
        const db = event.target.result;
        
        // Create object stores for each asset type
        Object.values(this.stores).forEach(storeName => {
          if (!db.objectStoreNames.contains(storeName)) {
            const store = db.createObjectStore(storeName, { keyPath: 'id' });
            store.createIndex('lastAccessed', 'lastAccessed', { unique: false });
            store.createIndex('size', 'size', { unique: false });
            console.log(`[Storage] Created object store: ${storeName}`);
          }
        });
      };
    });
  }
  
  /**
   * Update storage quota information
   */
  async updateStorageInfo() {
    if (!navigator.storage || !navigator.storage.estimate) {
      console.warn('[Storage] Storage estimation API not available');
      return this.storageInfo;
    }
    
    try {
      const estimate = await navigator.storage.estimate();
      this.storageInfo.usage = estimate.usage || 0;
      this.storageInfo.quota = estimate.quota || 0;
      this.storageInfo.usagePercent = this.storageInfo.quota > 0 
        ? (this.storageInfo.usage / this.storageInfo.quota) * 100 
        : 0;
      
      // Check persistence
      if (navigator.storage.persisted) {
        this.storageInfo.persisted = await navigator.storage.persisted();
      }
      
      return this.storageInfo;
    } catch (error) {
      console.error('[Storage] Failed to estimate storage:', error);
      return this.storageInfo;
    }
  }
  
  /**
   * Store an asset in IndexedDB
   */
  async storeAsset(storeName, id, data, metadata = {}) {
    if (!this.db) {
      throw new Error('Database not initialized');
    }
    
    return new Promise((resolve, reject) => {
      const transaction = this.db.transaction([storeName], 'readwrite');
      const store = transaction.objectStore(storeName);
      
      const asset = {
        id,
        data,
        ...metadata,
        lastAccessed: Date.now(),
        size: this.estimateSize(data)
      };
      
      const request = store.put(asset);
      
      request.onsuccess = () => {
        resolve(asset);
      };
      
      request.onerror = () => {
        reject(new Error(`Failed to store asset: ${id}`));
      };
    });
  }
  
  /**
   * Retrieve an asset from IndexedDB
   */
  async getAsset(storeName, id) {
    if (!this.db) {
      throw new Error('Database not initialized');
    }
    
    return new Promise((resolve, reject) => {
      const transaction = this.db.transaction([storeName], 'readwrite');
      const store = transaction.objectStore(storeName);
      
      const request = store.get(id);
      
      request.onsuccess = () => {
        const asset = request.result;
        
        if (asset) {
          // Update last accessed time
          asset.lastAccessed = Date.now();
          store.put(asset);
        }
        
        resolve(asset);
      };
      
      request.onerror = () => {
        reject(new Error(`Failed to retrieve asset: ${id}`));
      };
    });
  }
  
  /**
   * Delete an asset from IndexedDB
   */
  async deleteAsset(storeName, id) {
    if (!this.db) {
      throw new Error('Database not initialized');
    }
    
    return new Promise((resolve, reject) => {
      const transaction = this.db.transaction([storeName], 'readwrite');
      const store = transaction.objectStore(storeName);
      
      const request = store.delete(id);
      
      request.onsuccess = () => {
        resolve(true);
      };
      
      request.onerror = () => {
        reject(new Error(`Failed to delete asset: ${id}`));
      };
    });
  }
  
  /**
   * Clear all assets from a store
   */
  async clearStore(storeName) {
    if (!this.db) {
      throw new Error('Database not initialized');
    }
    
    return new Promise((resolve, reject) => {
      const transaction = this.db.transaction([storeName], 'readwrite');
      const store = transaction.objectStore(storeName);
      
      const request = store.clear();
      
      request.onsuccess = () => {
        console.log(`[Storage] Cleared store: ${storeName}`);
        resolve(true);
      };
      
      request.onerror = () => {
        reject(new Error(`Failed to clear store: ${storeName}`));
      };
    });
  }
  
  /**
   * Get all assets from a store
   */
  async getAllAssets(storeName) {
    if (!this.db) {
      throw new Error('Database not initialized');
    }
    
    return new Promise((resolve, reject) => {
      const transaction = this.db.transaction([storeName], 'readonly');
      const store = transaction.objectStore(storeName);
      
      const request = store.getAll();
      
      request.onsuccess = () => {
        resolve(request.result || []);
      };
      
      request.onerror = () => {
        reject(new Error(`Failed to get all assets from: ${storeName}`));
      };
    });
  }
  
  /**
   * Get storage statistics for a specific store
   */
  async getStoreStats(storeName) {
    const assets = await this.getAllAssets(storeName);
    const totalSize = assets.reduce((sum, asset) => sum + (asset.size || 0), 0);
    
    return {
      count: assets.length,
      size: totalSize,
      sizeFormatted: this.formatBytes(totalSize)
    };
  }
  
  /**
   * Get storage statistics for all stores
   */
  async getAllStats() {
    const stats = {};
    
    for (const [key, storeName] of Object.entries(this.stores)) {
      stats[key] = await this.getStoreStats(storeName);
    }
    
    await this.updateStorageInfo();
    stats.total = {
      usage: this.storageInfo.usage,
      quota: this.storageInfo.quota,
      usageFormatted: this.formatBytes(this.storageInfo.usage),
      quotaFormatted: this.formatBytes(this.storageInfo.quota),
      usagePercent: this.storageInfo.usagePercent
    };
    
    return stats;
  }
  
  /**
   * Evict least recently used assets if quota is reached
   */
  async evictLRU(storeName, targetPercent = 70) {
    await this.updateStorageInfo();
    
    if (this.storageInfo.usagePercent < 90) {
      return; // No eviction needed
    }
    
    console.log(`[Storage] Quota at ${this.storageInfo.usagePercent.toFixed(1)}% - starting LRU eviction`);
    
    const assets = await this.getAllAssets(storeName);
    
    // Sort by last accessed (oldest first)
    assets.sort((a, b) => a.lastAccessed - b.lastAccessed);
    
    // Delete oldest assets until we reach target percent
    let evicted = 0;
    for (const asset of assets) {
      await this.deleteAsset(storeName, asset.id);
      evicted++;
      
      await this.updateStorageInfo();
      if (this.storageInfo.usagePercent < targetPercent) {
        break;
      }
    }
    
    console.log(`[Storage] Evicted ${evicted} assets from ${storeName}`);
    console.log(`[Storage] New usage: ${this.storageInfo.usagePercent.toFixed(1)}%`);
  }
  
  /**
   * Estimate size of data in bytes
   */
  estimateSize(data) {
    if (!data) return 0;
    
    if (typeof data === 'string') {
      return new Blob([data]).size;
    }
    
    if (data instanceof ArrayBuffer) {
      return data.byteLength;
    }
    
    if (data instanceof Blob) {
      return data.size;
    }
    
    // JSON.stringify for objects
    try {
      return new Blob([JSON.stringify(data)]).size;
    } catch {
      return 0;
    }
  }
  
  /**
   * Format bytes to human-readable string
   */
  formatBytes(bytes) {
    if (bytes === 0) return '0 Bytes';
    
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    
    return Math.round((bytes / Math.pow(k, i)) * 100) / 100 + ' ' + sizes[i];
  }
}

// Export for use in app
if (typeof module !== 'undefined' && module.exports) {
  module.exports = StorageManager;
}
