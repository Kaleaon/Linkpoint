/**
 * Linkpoint - Local & Removable Flashdrive Cache Manager
 *
 * Provides persistent caching of Second Life inventory skeletons, items,
 * textures, asset metadata, and object meshes to a removable flashdrive,
 * custom directory path, or browser storage.
 */

import { Utils } from './utils';

export type CacheLocationType = 'internal' | 'flashdrive_fs' | 'flashdrive_path' | 'indexeddb';

export interface CacheStats {
  locationType: CacheLocationType;
  locationName: string;
  inventoryFolders: number;
  inventoryItems: number;
  texturesCount: number;
  totalSizeMb: number;
  lastUpdated: string | null;
  flashdriveReady: boolean;
}

const DB_NAME = 'linkpoint_sl_cache_db';
const DB_VERSION = 1;
const STORE_INVENTORY = 'inventory';
const STORE_TEXTURES = 'textures';
const STORE_META = 'metadata';

class LocalCacheManager extends Utils.EventEmitter {
  public locationType: CacheLocationType = 'internal';
  public customFlashdrivePath: string = '/media/usb/sl-cache';
  private dirHandle: any = null; // FileSystemDirectoryHandle if using Web File System API
  private idb: IDBDatabase | null = null;
  private memoryCache: Map<string, any> = new Map();

  constructor() {
    super();
    this.loadSavedConfig();
    this.initIDB().catch(() => {});
  }

  private loadSavedConfig() {
    try {
      const savedType = Utils.storage.get('sl_cache_loc_type');
      if (savedType) this.locationType = savedType as CacheLocationType;
      const savedPath = Utils.storage.get('sl_cache_flashdrive_path');
      if (savedPath) this.customFlashdrivePath = savedPath;
    } catch {
      // Ignore storage errors
    }
  }

  private async initIDB(): Promise<IDBDatabase | null> {
    if (typeof window === 'undefined' || !window.indexedDB) return null;
    if (this.idb) return this.idb;

    return new Promise((resolve) => {
      try {
        const req = window.indexedDB.open(DB_NAME, DB_VERSION);
        req.onupgradeneeded = (e: any) => {
          const db = e.target.result;
          if (!db.objectStoreNames.contains(STORE_INVENTORY)) {
            db.createObjectStore(STORE_INVENTORY, { keyPath: 'id' });
          }
          if (!db.objectStoreNames.contains(STORE_TEXTURES)) {
            db.createObjectStore(STORE_TEXTURES, { keyPath: 'uuid' });
          }
          if (!db.objectStoreNames.contains(STORE_META)) {
            db.createObjectStore(STORE_META, { keyPath: 'key' });
          }
        };
        req.onsuccess = (e: any) => {
          this.idb = e.target.result;
          resolve(this.idb);
        };
        req.onerror = () => resolve(null);
      } catch {
        resolve(null);
      }
    });
  }

  /**
   * Configure the target cache location.
   */
  public setLocation(type: CacheLocationType, customPath?: string, dirHandle?: any) {
    this.locationType = type;
    Utils.storage.set('sl_cache_loc_type', type);
    if (customPath) {
      this.customFlashdrivePath = customPath;
      Utils.storage.set('sl_cache_flashdrive_path', customPath);
    }
    if (dirHandle) {
      this.dirHandle = dirHandle;
    }
    this.emit('cache_location_changed', { type, customPath });
  }

  /**
   * Prompts user via the Web File System Access API to select a folder on a Flashdrive / USB drive.
   */
  public async pickFlashdriveDirectory(): Promise<{ success: boolean; name?: string; error?: string }> {
    if (typeof window === 'undefined' || !(window as any).showDirectoryPicker) {
      return {
        success: false,
        error: 'Directory selection is supported on desktop browsers (Chrome, Edge, Opera). You can also specify a custom path below.',
      };
    }

    try {
      const handle = await (window as any).showDirectoryPicker({
        id: 'linkpoint-flashdrive-cache',
        mode: 'readwrite',
        startIn: 'removable',
      });

      this.dirHandle = handle;
      this.setLocation('flashdrive_fs', handle.name, handle);
      return { success: true, name: handle.name };
    } catch (err: any) {
      if (err.name === 'AbortError') return { success: false, error: 'Directory selection cancelled' };
      return { success: false, error: err.message || 'Failed to select flashdrive directory' };
    }
  }

  /**
   * Save complete inventory (skeleton folders & items) to cache.
   */
  public async saveInventory(agentId: string, inventoryData: { folders: any[]; items: any[]; rootId?: string; rootName?: string }): Promise<void> {
    const payload = {
      id: `inv_${agentId}`,
      agentId,
      timestamp: Date.now(),
      foldersCount: inventoryData.folders?.length || 0,
      itemsCount: inventoryData.items?.length || 0,
      data: inventoryData,
    };

    // 1. Keep in memory for fast lookup
    this.memoryCache.set(`inv_${agentId}`, payload);

    // 2. Save via Web File System API if user selected a flashdrive folder
    if (this.locationType === 'flashdrive_fs' && this.dirHandle) {
      try {
        const fileHandle = await this.dirHandle.getFileHandle(`inventory_${agentId}.json`, { create: true });
        const writable = await fileHandle.createWritable();
        await writable.write(JSON.stringify(payload, null, 2));
        await writable.close();
      } catch (fsErr) {
        console.warn('[LocalCache] FileSystem save error:', fsErr);
      }
    }

    // 3. Save to backend / disk flashdrive path
    try {
      await fetch('/api/sl/cache/inventory', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          agentId,
          locationType: this.locationType,
          customPath: this.customFlashdrivePath,
          inventoryData: payload,
        }),
      });
    } catch {
      // Backend may be offline or in desktop mode
    }

    // 4. Save to IndexedDB as browser backup
    try {
      const db = await this.initIDB();
      if (db) {
        const tx = db.transaction([STORE_INVENTORY], 'readwrite');
        tx.objectStore(STORE_INVENTORY).put(payload);
      }
    } catch {
      // IDB error fallback
    }

    this.emit('cache_updated', { type: 'inventory', agentId, count: inventoryData.folders?.length });
  }

  /**
   * Load inventory from selected cache. Returns null if not cached.
   */
  public async loadInventory(agentId: string): Promise<{ folders: any[]; items: any[]; rootId?: string; rootName?: string } | null> {
    // 1. Check memory cache first
    const mem = this.memoryCache.get(`inv_${agentId}`);
    if (mem?.data?.folders?.length) {
      return mem.data;
    }

    // 2. Try loading from selected flashdrive folder via Web File System API
    if (this.locationType === 'flashdrive_fs' && this.dirHandle) {
      try {
        const fileHandle = await this.dirHandle.getFileHandle(`inventory_${agentId}.json`);
        const file = await fileHandle.getFile();
        const text = await file.text();
        const parsed = JSON.parse(text);
        if (parsed?.data?.folders?.length) {
          this.memoryCache.set(`inv_${agentId}`, parsed);
          return parsed.data;
        }
      } catch {
        // Fall back to server/IDB
      }
    }

    // 3. Try loading from server disk / configured flashdrive directory
    try {
      const res = await fetch(`/api/sl/cache/inventory?agentId=${encodeURIComponent(agentId)}&path=${encodeURIComponent(this.customFlashdrivePath)}`);
      if (res.ok) {
        const serverCache = await res.json();
        if (serverCache?.data?.folders?.length) {
          this.memoryCache.set(`inv_${agentId}`, serverCache);
          return serverCache.data;
        }
      }
    } catch {
      // Ignore network errors
    }

    // 4. Try loading from IndexedDB
    try {
      const db = await this.initIDB();
      if (db) {
        const result: any = await new Promise((resolve) => {
          const tx = db.transaction([STORE_INVENTORY], 'readonly');
          const req = tx.objectStore(STORE_INVENTORY).get(`inv_${agentId}`);
          req.onsuccess = () => resolve(req.result);
          req.onerror = () => resolve(null);
        });

        if (result?.data?.folders?.length) {
          this.memoryCache.set(`inv_${agentId}`, result);
          return result.data;
        }
      }
    } catch {
      // IDB error fallback
    }

    return null;
  }

  /**
   * Save texture image data URL / blob to cache.
   */
  public async saveTexture(uuid: string, dataUrl: string): Promise<void> {
    const entry = { uuid, dataUrl, timestamp: Date.now() };
    this.memoryCache.set(`tex_${uuid}`, entry);

    // Save to flashdrive handle if configured
    if (this.locationType === 'flashdrive_fs' && this.dirHandle) {
      try {
        const dir = await this.dirHandle.getDirectoryHandle('textures', { create: true });
        const file = await dir.getFileHandle(`${uuid}.txt`, { create: true });
        const writable = await file.createWritable();
        await writable.write(dataUrl);
        await writable.close();
      } catch {
        // Ignore
      }
    }

    // Save to server flashdrive cache
    try {
      await fetch('/api/sl/cache/texture', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          uuid,
          dataUrl,
          customPath: this.customFlashdrivePath,
        }),
      });
    } catch {
      // Ignore
    }

    // Save to IndexedDB
    try {
      const db = await this.initIDB();
      if (db) {
        const tx = db.transaction([STORE_TEXTURES], 'readwrite');
        tx.objectStore(STORE_TEXTURES).put(entry);
      }
    } catch {
      // Ignore
    }
  }

  /**
   * Retrieve texture from cache.
   */
  public async getTexture(uuid: string): Promise<string | null> {
    const mem = this.memoryCache.get(`tex_${uuid}`);
    if (mem?.dataUrl) return mem.dataUrl;

    if (this.locationType === 'flashdrive_fs' && this.dirHandle) {
      try {
        const dir = await this.dirHandle.getDirectoryHandle('textures');
        const file = await dir.getFileHandle(`${uuid}.txt`);
        const f = await file.getFile();
        const dataUrl = await f.text();
        this.memoryCache.set(`tex_${uuid}`, { uuid, dataUrl });
        return dataUrl;
      } catch {
        // Fall back
      }
    }

    try {
      const db = await this.initIDB();
      if (db) {
        const item: any = await new Promise((resolve) => {
          const tx = db.transaction([STORE_TEXTURES], 'readonly');
          const req = tx.objectStore(STORE_TEXTURES).get(uuid);
          req.onsuccess = () => resolve(req.result);
          req.onerror = () => resolve(null);
        });
        if (item?.dataUrl) {
          this.memoryCache.set(`tex_${uuid}`, item);
          return item.dataUrl;
        }
      }
    } catch {
      // Ignore
    }

    return null;
  }

  /**
   * Export the entire cache as a JSON bundle that can be saved directly to a flashdrive.
   */
  public async exportCacheBundle(agentId: string): Promise<Blob> {
    const inv = await this.loadInventory(agentId);
    const bundle = {
      version: 1,
      app: 'Linkpoint',
      exportedAt: new Date().toISOString(),
      agentId,
      inventory: inv,
      stats: await this.getStats(agentId),
    };

    return new Blob([JSON.stringify(bundle, null, 2)], { type: 'application/json' });
  }

  /**
   * Import a cache bundle from a file selected on a flashdrive.
   */
  public async importCacheBundle(file: File): Promise<{ success: boolean; foldersCount: number; error?: string }> {
    try {
      const text = await file.text();
      const bundle = JSON.parse(text);
      if (!bundle?.inventory?.folders) {
        return { success: false, foldersCount: 0, error: 'Invalid Linkpoint cache bundle file' };
      }

      const agentId = bundle.agentId || 'current';
      await this.saveInventory(agentId, bundle.inventory);
      return { success: true, foldersCount: bundle.inventory.folders.length };
    } catch (err: any) {
      return { success: false, foldersCount: 0, error: err.message || 'Failed to read cache bundle' };
    }
  }

  /**
   * Clear all cache data from the active location.
   */
  public async clearCache(agentId?: string): Promise<void> {
    this.memoryCache.clear();

    // Clear server cache
    try {
      await fetch('/api/sl/cache/clear', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ customPath: this.customFlashdrivePath, agentId }),
      });
    } catch {
      // Ignore
    }

    // Clear IndexedDB
    try {
      const db = await this.initIDB();
      if (db) {
        const tx = db.transaction([STORE_INVENTORY, STORE_TEXTURES, STORE_META], 'readwrite');
        tx.objectStore(STORE_INVENTORY).clear();
        tx.objectStore(STORE_TEXTURES).clear();
        tx.objectStore(STORE_META).clear();
      }
    } catch {
      // Ignore
    }

    this.emit('cache_cleared');
  }

  /**
   * Get current statistics for the selected cache.
   */
  public async getStats(agentId?: string): Promise<CacheStats> {
    let foldersCount = 0;
    let itemsCount = 0;

    if (agentId) {
      const inv = await this.loadInventory(agentId);
      if (inv) {
        foldersCount = inv.folders?.length || 0;
        itemsCount = inv.items?.length || 0;
      }
    }

    let texturesCount = 0;
    try {
      const db = await this.initIDB();
      if (db) {
        texturesCount = await new Promise((resolve) => {
          const tx = db.transaction([STORE_TEXTURES], 'readonly');
          const countReq = tx.objectStore(STORE_TEXTURES).count();
          countReq.onsuccess = () => resolve(countReq.result || 0);
          countReq.onerror = () => resolve(0);
        });
      }
    } catch {
      // Ignore
    }

    const approxMb = Number(((foldersCount * 120 + itemsCount * 80 + texturesCount * 65000) / (1024 * 1024)).toFixed(2));

    let locationName = 'Internal Browser Storage';
    if (this.locationType === 'flashdrive_fs') {
      locationName = `Flash Drive (${this.dirHandle?.name || 'Selected Folder'})`;
    } else if (this.locationType === 'flashdrive_path') {
      locationName = `Flash Drive (${this.customFlashdrivePath})`;
    }

    return {
      locationType: this.locationType,
      locationName,
      inventoryFolders: foldersCount,
      inventoryItems: itemsCount,
      texturesCount,
      totalSizeMb: approxMb,
      lastUpdated: foldersCount > 0 ? new Date().toLocaleTimeString() : null,
      flashdriveReady: Boolean(this.locationType === 'flashdrive_path' || this.dirHandle),
    };
  }
}

export const localCache = new LocalCacheManager();
