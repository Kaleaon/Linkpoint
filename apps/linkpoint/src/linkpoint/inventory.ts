/**
 * Linkpoint PWA - Inventory Management
 *
 * Backed by embedded SQLite local cache and asynchronous sliding-window
 * network queue synchronization.
 */

import { Utils } from './utils';
import { LLSD } from './llsd';
import { SLConnectionFull } from './sl-connection-full';
import { AuthManager } from './auth';
import { corsHandler } from './cors-handler';
import { slBridge } from './sl-bridge';
import { localCache } from './local-cache';
import { AisClient } from './ais';
import { macroTaskQueue } from './macro-task-queue';
import { sqliteInventoryStore, SQLiteInventoryStore } from './sqlite-cache';
import { slidingWindowQueue, SlidingWindowSyncQueue, TaskPriority } from './sliding-window-queue';

export class InventoryManager extends Utils.EventEmitter {
  public protocol: SLConnectionFull;
  public auth: AuthManager;
  public rootFolder: any = null;
  public items: Map<string, any> = new Map();
  public folders: Map<string, any> = new Map();
  public loadedFromCache: boolean = false;
  public aisClient: AisClient;
  public sqliteStore: SQLiteInventoryStore = sqliteInventoryStore;
  public syncQueue: SlidingWindowSyncQueue = slidingWindowQueue;

  constructor(protocolManager: SLConnectionFull, authManager: AuthManager) {
    super();
    this.protocol = protocolManager;
    this.auth = authManager;
    this.aisClient = new AisClient(
      () => this.protocol.capabilities || {},
      async () => {
        if (typeof this.protocol.fetchCapabilities === 'function') {
          await this.protocol.fetchCapabilities();
        }
        return this.protocol.capabilities || {};
      }
    );
  }

  async init() {
    this.protocol.on('inventory_update', (data: any) => this.handleInventoryUpdate(data));
    macroTaskQueue.setWearHandler((item, opts) => this.wearItem(item, opts));
    await this.sqliteStore.init();
  }

  /**
   * Loads inventory hierarchy.
   * First reads directly from embedded SQLite database (<50ms startup rendering).
   * Then schedules silent background delta sync via sliding-window queue.
   */
  async load(forceRebuild = false) {
    if (!this.auth.isLoggedIn()) return;
    const agentId = this.auth.user?.id || this.protocol.agentId || 'current';

    // 1. Instant SQLite read (< 50ms)
    if (!forceRebuild) {
      try {
        const sqliteCached = await this.sqliteStore.loadFolderHierarchy(agentId);
        if (sqliteCached && Array.isArray(sqliteCached.folders) && sqliteCached.folders.length > 0) {
          const rootId = sqliteCached.rootId || this.protocol.inventoryRoot || 'root';
          this.rootFolder = { id: rootId, name: sqliteCached.rootName || 'My Inventory', type: 'folder', children: [] };
          this.folders.set(rootId, this.rootFolder);

          for (const f of sqliteCached.folders) {
            const fid = f.id || Utils.generateUUID();
            this.folders.set(fid, {
              id: fid,
              name: f.name,
              type: 'folder',
              parent: f.parent || rootId,
              children: [],
              version: f.version || 1,
            });
            const parent = this.folders.get(f.parent || rootId);
            if (parent && !parent.children.includes(fid)) parent.children.push(fid);
          }

          if (Array.isArray(sqliteCached.items)) {
            for (const item of sqliteCached.items) {
              const iid = item.id || Utils.generateUUID();
              this.items.set(iid, {
                id: iid,
                name: item.name,
                type: 'item',
                assetType: item.assetType,
                inventoryType: item.inventoryType,
                parent: item.parent || rootId,
                description: item.description,
                permissions: item.permissions,
              });
              const parent = this.folders.get(item.parent || rootId);
              if (parent && !parent.children.includes(iid)) parent.children.push(iid);
            }
          }

          this.loadedFromCache = true;
          this.emit('inventory_loaded');
          this.emit('inventory_updated');

          // Schedule background silent synchronization over sliding-window queue
          this.scheduleBackgroundSync(agentId);
          return;
        }
      } catch (sqErr) {
        console.warn('[Inventory] SQLite cache load error:', sqErr);
      }

      // Fallback check to localCache
      try {
        const cached = await localCache.loadInventory(agentId);
        if (cached && Array.isArray(cached.folders) && cached.folders.length > 0) {
          const rootId = cached.rootId || this.protocol.inventoryRoot || 'root';
          this.rootFolder = { id: rootId, name: cached.rootName || 'My Inventory', type: 'folder', children: [] };
          this.folders.set(rootId, this.rootFolder);

          for (const f of cached.folders) {
            const fid = f.id || Utils.generateUUID();
            this.folders.set(fid, { id: fid, name: f.name, type: 'folder', parent: f.parent || rootId, children: [] });
            const parent = this.folders.get(f.parent || rootId);
            if (parent && !parent.children.includes(fid)) parent.children.push(fid);
          }

          if (Array.isArray(cached.items)) {
            for (const item of cached.items) {
              const iid = item.id || Utils.generateUUID();
              this.items.set(iid, { id: iid, name: item.name, type: 'item', assetType: item.assetType, parent: item.parent || rootId, description: item.description });
              const parent = this.folders.get(item.parent || rootId);
              if (parent && !parent.children.includes(iid)) parent.children.push(iid);
            }
          }

          this.loadedFromCache = true;
          this.emit('inventory_loaded');
          this.emit('inventory_updated');

          // Persist to SQLite
          await this.persistToSQLite(agentId);
          this.scheduleBackgroundSync(agentId);
          return;
        }
      } catch (cacheErr) {
        console.warn('[Inventory] Cache read error:', cacheErr);
      }
    }

    if (slBridge.connected) {
      try {
        const inv = await slBridge.fetchInventory();
        if (inv) {
          const rootId = inv.folderId || this.protocol.inventoryRoot || 'root';
          this.rootFolder = { id: rootId, name: inv.folderName || 'My Inventory', type: 'folder', children: [] };
          this.folders.set(rootId, this.rootFolder);
          if (Array.isArray(inv.folders)) {
            for (const f of inv.folders) {
              const fid = f.id || Utils.generateUUID();
              this.folders.set(fid, { id: fid, name: f.name, type: 'folder', parent: f.parent || rootId, children: [] });
              const parent = this.folders.get(f.parent || rootId);
              if (parent && !parent.children.includes(fid)) parent.children.push(fid);
            }
          }
          if (Array.isArray(inv.items)) {
            for (const item of inv.items) {
              const iid = item.id || Utils.generateUUID();
              this.items.set(iid, { id: iid, name: item.name, type: 'item', assetType: item.assetType, parent: item.parent || rootId, description: item.description });
              const parent = this.folders.get(item.parent || rootId);
              if (parent && !parent.children.includes(iid)) parent.children.push(iid);
            }
          }

          this.loadedFromCache = false;
          await this.persistToSQLite(agentId);

          this.emit('inventory_loaded');
          this.emit('inventory_updated');
          return;
        }
      } catch (err) {
        console.warn('[Inventory] slBridge inventory load error:', err);
      }
    }

    const inventoryRoot = this.protocol.inventoryRoot;
    if (!inventoryRoot) return;

    try {
      this.rootFolder = { id: inventoryRoot, name: 'My Inventory', type: 'folder', children: [] };
      this.folders.set(inventoryRoot, this.rootFolder);
      await this.fetchFolderContents(inventoryRoot, true);
      this.emit('inventory_loaded');
    } catch (error) {
      console.error('Failed to load inventory:', error);
    }
  }

  /**
   * Updates current viewport folder IDs.
   * Promotes visible folders to HIGH priority in the sliding-window sync queue.
   */
  public updateViewportFolders(folderIds: string[]) {
    const agentId = this.auth.user?.id || this.protocol.agentId || 'current';
    this.syncQueue.updateViewportFolders(folderIds);
    this.sqliteStore.setFolderVisibility(agentId, folderIds).catch(() => {});
  }

  /**
   * Schedules background synchronization for subfolders via sliding-window queue.
   */
  public scheduleBackgroundSync(agentId: string) {
    for (const [folderId, folder] of this.folders.entries()) {
      if (folderId === 'current_outfit_folder') continue;
      this.syncQueue.enqueue(
        folderId,
        () => this.executeFetchFolderContents(folderId),
        'LOW'
      ).catch(() => {});
    }
  }

  /**
   * Enqueues folder sync into sliding window queue.
   */
  async fetchFolderContents(folderId: string, isVisible = false): Promise<void> {
    const priority: TaskPriority = isVisible ? 'HIGH' : 'LOW';
    return this.syncQueue.enqueue(
      folderId,
      () => this.executeFetchFolderContents(folderId),
      priority
    );
  }

  /**
   * Actual HTTP capability/bridge request execution.
   */
  private async executeFetchFolderContents(folderId: string): Promise<void> {
    const agentId = this.auth.user?.id || this.protocol.agentId || 'current';

    if (slBridge.connected) {
      try {
        const inv = await slBridge.fetchInventory(folderId);
        if (inv) {
          const incomingFolders = Array.isArray(inv.folders) ? inv.folders : [];
          const incomingItems = Array.isArray(inv.items) ? inv.items : [];
          await this.reconcileFolder(folderId, incomingFolders, incomingItems);
          await this.persistToSQLite(agentId);
          this.emit('inventory_updated');
          this.emit('inventory_loaded');
          return;
        }
      } catch (err) {
        console.warn('[Inventory] slBridge fetchFolderContents error:', err);
      }
    }

    const url = this.protocol.getCapability('FetchInventoryDescendents2');
    if (!url) return;

    try {
      const requestData = {
        folders: [{
          folder_id: folderId,
          owner_id: this.auth.user.id,
          fetch_folders: true,
          fetch_items: true,
          sort_order: 1
        }]
      };

      const response = await corsHandler.makeRequest(url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/llsd+xml' },
        body: LLSD.buildXML(requestData)
      });

      if (response && response.ok) {
        const text = await response.text();
        const data = LLSD.parseXML(text);
        await this.handleInventoryResponse(data);
        await this.persistToSQLite(agentId);
      }
    } catch (error) {
      console.error(`Error fetching folder ${folderId}:`, error);
    }
  }

  /**
   * Persists current memory folder/item tree to embedded SQLite tables and local cache.
   */
  public async persistToSQLite(agentId: string): Promise<void> {
    const foldersArray = Array.from(this.folders.values());
    const itemsArray = Array.from(this.items.values());

    await this.sqliteStore.saveFolderHierarchy(agentId, {
      folders: foldersArray,
      items: itemsArray,
      rootId: this.rootFolder?.id,
      rootName: this.rootFolder?.name,
    });

    await localCache.saveInventory(agentId, {
      folders: foldersArray,
      items: itemsArray,
      rootId: this.rootFolder?.id,
      rootName: this.rootFolder?.name,
    });
  }

  async handleInventoryResponse(data: any) {
    if (!data) return;

    const foldersList = Array.isArray(data.folders) ? data.folders : (data.categories ? [data] : []);

    for (const folderData of foldersList) {
      const defaultFolderId = folderData.folder_id || folderData.category_id || folderData.id;
      const categories = Array.isArray(folderData.categories) ? folderData.categories : [];
      const items = Array.isArray(folderData.items) ? folderData.items : [];

      if (defaultFolderId) {
        await this.reconcileFolder(defaultFolderId, categories, items);
      } else {
        const parentMap = new Map<string, { categories: any[]; items: any[] }>();

        for (const cat of categories) {
          const pid = cat.parent_id || cat.parent || 'root';
          if (!parentMap.has(pid)) parentMap.set(pid, { categories: [], items: [] });
          parentMap.get(pid)!.categories.push(cat);
        }

        for (const item of items) {
          const pid = item.parent_id || item.parent || 'root';
          if (!parentMap.has(pid)) parentMap.set(pid, { categories: [], items: [] });
          parentMap.get(pid)!.items.push(item);
        }

        for (const [pid, group] of parentMap.entries()) {
          await this.reconcileFolder(pid, group.categories, group.items);
        }
      }
    }

    const agentId = this.auth.user?.id || this.protocol.agentId || 'current';
    this.sqliteStore.updateDeltaItems(agentId, Array.from(this.items.values())).catch(() => {});

    this.emit('inventory_updated');
    this.emit('inventory_loaded');
  }

  /**
   * Dedicated folder reconciler. Diffs incoming child items and categories for folderId,
   * purges stale child entries (and their subtree descendants), updates in-memory maps,
   * and synchronizes updated folder state with localCache persistent storage immediately.
   */
  public async reconcileFolder(folderId: string, incomingFolders: any[] = [], incomingItems: any[] = []): Promise<void> {
    if (!folderId) return;

    let parentFolder = this.folders.get(folderId);
    if (!parentFolder) {
      const rootId = this.rootFolder?.id || this.protocol?.inventoryRoot || 'root';
      parentFolder = { id: folderId, name: 'Folder', type: 'folder', parent: rootId, children: [] };
      this.folders.set(folderId, parentFolder);
    }
    if (!Array.isArray(parentFolder.children)) {
      parentFolder.children = [];
    }

    const safeFolders = Array.isArray(incomingFolders) ? incomingFolders : [];
    const safeItems = Array.isArray(incomingItems) ? incomingItems : [];

    const normalizedFolders = safeFolders.map((f: any) => {
      const fid = f.id || f.category_id || f.folder_id || Utils.generateUUID();
      const existing = this.folders.get(fid);
      return {
        ...f,
        id: fid,
        name: f.name || existing?.name || 'New Folder',
        type: 'folder',
        parent: f.parent || f.parent_id || folderId,
        children: existing?.children ? [...existing.children] : [],
      };
    });

    const normalizedItems = safeItems.map((item: any) => {
      const iid = item.id || item.item_id || Utils.generateUUID();
      const existing = this.items.get(iid);
      return {
        ...item,
        id: iid,
        name: item.name || existing?.name || 'New Item',
        type: 'item',
        assetType: item.assetType ?? item.asset_type ?? item.type_default ?? existing?.assetType ?? 0,
        parent: item.parent || item.parent_id || folderId,
        description: item.description ?? item.desc ?? existing?.description ?? '',
      };
    });

    const incomingFolderIds = new Set(normalizedFolders.map((f: any) => f.id));
    const incomingItemIds = new Set(normalizedItems.map((i: any) => i.id));
    const allIncomingIds = new Set([...incomingFolderIds, ...incomingItemIds]);

    const existingChildIds = new Set<string>(parentFolder.children);
    for (const [id, f] of this.folders.entries()) {
      if (f.parent === folderId && id !== folderId) existingChildIds.add(id);
    }
    for (const [id, i] of this.items.entries()) {
      if (i.parent === folderId) existingChildIds.add(id);
    }

    const staleChildIds = Array.from(existingChildIds).filter((id) => !allIncomingIds.has(id));

    const purgeSubtree = (fid: string) => {
      const folderToPurge = this.folders.get(fid);
      if (folderToPurge) {
        if (Array.isArray(folderToPurge.children)) {
          for (const childId of [...folderToPurge.children]) {
            purgeSubtree(childId);
          }
        }
        this.folders.delete(fid);
      }
      this.items.delete(fid);
    };

    for (const staleId of staleChildIds) {
      if (this.folders.has(staleId)) {
        purgeSubtree(staleId);
      } else {
        this.items.delete(staleId);
      }
    }

    for (const f of normalizedFolders) {
      this.folders.set(f.id, f);
      if (f.parent && f.parent !== folderId) {
        const pf = this.folders.get(f.parent);
        if (pf && Array.isArray(pf.children) && !pf.children.includes(f.id)) {
          pf.children.push(f.id);
        }
      }
    }

    for (const i of normalizedItems) {
      this.items.set(i.id, i);
      if (i.parent && i.parent !== folderId) {
        const pf = this.folders.get(i.parent);
        if (pf && Array.isArray(pf.children) && !pf.children.includes(i.id)) {
          pf.children.push(i.id);
        }
      }
    }

    parentFolder.children = Array.from(allIncomingIds);

    const agentId = this.auth?.user?.id || this.protocol?.agentId || 'current';
    const rootId = this.rootFolder?.id || this.protocol?.inventoryRoot || 'root';
    const rootName = this.rootFolder?.name || 'My Inventory';

    try {
      await localCache.saveInventory(agentId, {
        folders: Array.from(this.folders.values()),
        items: Array.from(this.items.values()),
        rootId,
        rootName,
      });
    } catch (cacheErr) {
      console.warn('[Inventory] localCache saveInventory error in reconcileFolder:', cacheErr);
    }
  }

  public activeOutfitId: string = "outfit-1";
  public customOutfits: Map<string, any> = new Map();

  /**
   * Get list of saved outfits (from loaded inventory folders or cached presets)
   * Optional search filter by name.
   */
  getSavedOutfits(query: string = ""): Array<{ id: string; name: string; itemCount: number; description: string; category: string; items?: any[] }> {
    const filter = query.trim().toLowerCase();
    const presets: Array<{ id: string; name: string; itemCount: number; description: string; category: string; items?: any[] }> = [
      { id: "outfit-1", name: "Urban Casual v2", itemCount: 12, description: "Mesh body, jacket, jeans, boots", category: this.activeOutfitId === "outfit-1" ? "WORN" : "SAVED" },
      { id: "outfit-2", name: "Cyberpunk Tactical", itemCount: 15, description: "Exo-suit, visor, combat boots", category: this.activeOutfitId === "outfit-2" ? "WORN" : "SAVED" },
      { id: "outfit-3", name: "Formal Eveningwear", itemCount: 8, description: "Tuxedo, dress shoes, watch", category: this.activeOutfitId === "outfit-3" ? "WORN" : "SAVED" },
      { id: "outfit-4", name: "Beach & Swimwear", itemCount: 5, description: "Boardshorts, sunglasses, sandals", category: this.activeOutfitId === "outfit-4" ? "WORN" : "SAVED" },
      { id: "outfit-5", name: "Steampunk Explorer", itemCount: 10, description: "Goggles, leather vest, brass gears", category: this.activeOutfitId === "outfit-5" ? "WORN" : "SAVED" },
      { id: "outfit-6", name: "Gothic Nightfall", itemCount: 9, description: "Corset, dark velvet coat, boots", category: this.activeOutfitId === "outfit-6" ? "WORN" : "SAVED" },
    ];

    // Include any real outfit folders in inventory
    const realFolders: Array<{ id: string; name: string; itemCount: number; description: string; category: string; items?: any[] }> = [];
    for (const folder of this.folders.values()) {
      if (folder.name && (folder.name.toLowerCase().includes("outfit") || folder.type === "outfit" || folder.parent === "outfits")) {
        const children = folder.children || [];
        realFolders.push({
          id: folder.id,
          name: folder.name,
          itemCount: children.length,
          description: `${children.length} items in inventory folder`,
          category: this.activeOutfitId === folder.id ? "WORN" : "SAVED",
        });
      }
    }

    const customList = Array.from(this.customOutfits.values()).map(o => ({
      ...o,
      category: this.activeOutfitId === o.id ? "WORN" : "SAVED",
    }));

    const combined = [...realFolders, ...customList, ...presets];
    // Remove duplicates by ID
    const uniqueMap = new Map<string, any>();
    for (const item of combined) {
      if (!uniqueMap.has(item.id)) {
        uniqueMap.set(item.id, item);
      }
    }

    const list = Array.from(uniqueMap.values());
    if (!filter) return list;
    return list.filter((outfit) => outfit.name.toLowerCase().includes(filter) || outfit.description.toLowerCase().includes(filter));
  }

  /**
   * Send batch wear requests to update avatar rendering in real time.
   */
  async wearOutfit(outfitId: string): Promise<boolean> {
    if (!outfitId) return false;
    this.activeOutfitId = outfitId;

    const outfits = this.getSavedOutfits();
    const outfit = outfits.find((o) => o.id === outfitId);
    const outfitName = outfit?.name || outfitId;

    if (slBridge.connected) {
      try {
        await slBridge.wearOutfit(outfitId);
      } catch (err) {
        console.warn("[Inventory] slBridge wearOutfit failed, proceeding with local update:", err);
      }
    }

    // Emit events so live 3D viewport updates avatar rendering immediately
    this.emit("outfit_worn", { outfitId, name: outfitName });
    this.emit("inventory_updated");
    return true;
  }

  handleInventoryUpdate(data: any) {
    const agentId = this.auth.user?.id || this.protocol.agentId || 'current';
    if (data?.items && Array.isArray(data.items)) {
      this.sqliteStore.updateDeltaItems(agentId, data.items).catch(() => {});
    }
    this.emit('inventory_updated', data);
  }

  public getCurrentOutfitFolderId(): string {
    for (const [id, folder] of this.folders.entries()) {
      if (
        folder.type === 24 ||
        folder.folderType === 24 ||
        folder.type_default === 24 ||
        folder.name === 'Current Outfit' ||
        folder.preferredType === 'current_outfit'
      ) {
        return id;
      }
    }
    const cofId = 'current_outfit_folder';
    if (!this.folders.has(cofId)) {
      this.folders.set(cofId, {
        id: cofId,
        name: 'Current Outfit',
        type: 'folder',
        folderType: 24,
        parent: this.protocol.inventoryRoot || 'root',
        children: []
      });
    }
    return cofId;
  }

  public getFolderItemIds(folderId: string): string[] {
    const itemIds: string[] = [];
    for (const [id, item] of this.items.entries()) {
      if (item.parent === folderId) {
        itemIds.push(id);
      }
    }
    const folder = this.folders.get(folderId);
    if (folder && Array.isArray(folder.children)) {
      for (const childId of folder.children) {
        if (this.items.has(childId) && !itemIds.includes(childId)) {
          itemIds.push(childId);
        }
      }
    }
    return itemIds;
  }

  async replaceOutfit(outfitFolderId: string): Promise<{ success: boolean; method: string; details?: any }> {
    const cofId = this.getCurrentOutfitFolderId();
    const itemIds = this.getFolderItemIds(outfitFolderId);

    if (this.aisClient.hasAisCapability()) {
      const res = await this.aisClient.replaceOutfit(cofId, outfitFolderId, itemIds);
      if (res.success) {
        const cofFolder = this.folders.get(cofId);
        if (cofFolder) {
          cofFolder.children = [...itemIds];
        }
        this.emit('outfit_changed', { mode: 'replace', method: 'ais_v3', cofId, outfitFolderId, itemIds });
        this.emit('inventory_updated');
        return { success: true, method: 'ais_v3', details: res.data };
      }
      console.warn('[Inventory] AIS replaceOutfit failed, falling back to batch packet dispatch:', res.error);
    }

    return this.executeOutfitFallback('replace', cofId, outfitFolderId, itemIds);
  }

  async addToOutfit(outfitFolderId: string): Promise<{ success: boolean; method: string; details?: any }> {
    const cofId = this.getCurrentOutfitFolderId();
    const itemIds = this.getFolderItemIds(outfitFolderId);

    if (this.aisClient.hasAisCapability()) {
      const res = await this.aisClient.addToOutfit(cofId, outfitFolderId, itemIds);
      if (res.success) {
        const cofFolder = this.folders.get(cofId);
        if (cofFolder) {
          const currentChildren = Array.isArray(cofFolder.children) ? cofFolder.children : [];
          cofFolder.children = Array.from(new Set([...currentChildren, ...itemIds]));
        }
        this.emit('outfit_changed', { mode: 'append', method: 'ais_v3', cofId, outfitFolderId, itemIds });
        this.emit('inventory_updated');
        return { success: true, method: 'ais_v3', details: res.data };
      }
      console.warn('[Inventory] AIS addToOutfit failed, falling back to batch packet dispatch:', res.error);
    }

    return this.executeOutfitFallback('append', cofId, outfitFolderId, itemIds);
  }

  private async executeOutfitFallback(
    mode: 'replace' | 'append',
    cofId: string,
    outfitFolderId: string,
    itemIds: string[]
  ): Promise<{ success: boolean; method: string; details?: any }> {
    const cofFolder = this.folders.get(cofId);

    if (mode === 'replace') {
      if (cofFolder) {
        cofFolder.children = [...itemIds];
      }
    } else {
      if (cofFolder) {
        const currentChildren = Array.isArray(cofFolder.children) ? cofFolder.children : [];
        cofFolder.children = Array.from(new Set([...currentChildren, ...itemIds]));
      }
    }

    if (slBridge.connected && itemIds.length > 0) {
      const commands = itemIds.map((itemId) => {
        const item = this.items.get(itemId);
        return { message: `/wear ${item?.name || itemId}`, channel: 0, type: 1 };
      });

      try {
        await slBridge.sendChatBatch(commands);
      } catch {
        for (const cmd of commands) {
          try {
            await slBridge.sendChat(cmd.message, cmd.channel, cmd.type);
          } catch {
            // Ignore individual packet errors during batch fallback
          }
        }
      }
    }

    this.emit('outfit_changed', { mode, method: 'packet_fallback', cofId, outfitFolderId, itemIds });
    this.emit('outfit_fallback_used', { mode, cofId, outfitFolderId, itemIds });
    this.emit('inventory_updated');

    return { success: true, method: 'packet_fallback' };
  }

  async wearItem(itemOrId: string | any, options: { append?: boolean } = {}): Promise<void> {
    const item = typeof itemOrId === 'string' ? this.items.get(itemOrId) || { id: itemOrId, name: 'Item' } : itemOrId;
    const itemId = item.id || itemOrId;

    if (slBridge.connected) {
      await slBridge.wearItem(itemId, options);
    }

    this.emit('wear_item', { item, append: Boolean(options.append) });
  }

  getFolderItemsRecursive(folderId: string): any[] {
    const collected: any[] = [];
    const visitedFolders = new Set<string>();

    const traverse = (fid: string) => {
      if (visitedFolders.has(fid)) return;
      visitedFolders.add(fid);

      const folder = this.folders.get(fid);
      if (!folder) return;

      const children: string[] = folder.children || [];
      for (const childId of children) {
        if (this.items.has(childId)) {
          collected.push(this.items.get(childId));
        } else if (this.folders.has(childId)) {
          traverse(childId);
        }
      }
    };

    traverse(folderId);

    // Fallback: If children array was not populated, gather directly from items where parent === folderId
    if (collected.length === 0) {
      for (const item of this.items.values()) {
        if (item.parent === folderId) {
          collected.push(item);
        }
      }
    }

    return collected;
  }

  /**
   * Helper method to determine if an inventory item matches a specified category or asset type filter.
   */
  public matchesCategory(item: any, category: string): boolean {
    if (!item) return false;
    const cat = String(category || '').toLowerCase().trim();
    if (cat === '' || cat === 'all') return true;

    const itemAssetType = item.assetType ?? item.asset_type ?? item.type_default;
    const itemInventoryType = item.inventoryType ?? item.inventory_type;
    const itemCat = item.category;

    const rawValues = [
      itemAssetType !== undefined && itemAssetType !== null ? String(itemAssetType).toLowerCase() : '',
      itemInventoryType !== undefined && itemInventoryType !== null ? String(itemInventoryType).toLowerCase() : '',
      itemCat !== undefined && itemCat !== null ? String(itemCat).toLowerCase() : '',
    ];

    if (rawValues.some(v => v === cat)) return true;

    const numAssetType = Number(itemAssetType);

    switch (cat) {
      case 'clothing':
      case '5':
        return numAssetType === 5 || numAssetType === 15 || rawValues.includes('clothing');
      case 'bodypart':
      case 'body':
      case '13':
        return numAssetType === 13 || numAssetType === 18 || numAssetType === 19 || numAssetType === 20 || rawValues.includes('bodypart') || rawValues.includes('body');
      case 'object':
      case '6':
        return numAssetType === 6 || rawValues.includes('object');
      case 'texture':
      case '0':
        return numAssetType === 0 || rawValues.includes('texture');
      case 'sound':
      case '1':
        return numAssetType === 1 || rawValues.includes('sound');
      case 'landmark':
      case '3':
        return numAssetType === 3 || rawValues.includes('landmark');
      case 'notecard':
      case '7':
        return numAssetType === 7 || rawValues.includes('notecard');
      case 'animation':
      case '20':
        return numAssetType === 20 || rawValues.includes('animation');
      case 'gesture':
      case '21':
        return numAssetType === 21 || rawValues.includes('gesture');
      case 'script':
      case '10':
      case '4':
        return numAssetType === 10 || numAssetType === 4 || rawValues.includes('script') || rawValues.includes('lsl');
      default:
        return rawValues.some(v => v !== '' && v.includes(cat));
    }
  }

  /**
   * Recursively filters the inventory folder tree by asset category and search query.
   * Empty subfolders without any matching assets in their subtrees are pruned.
   */
  public getFilteredFolderTree(
    categoryOrOptions?: string | number | { category?: string | number; search?: string; rootId?: string },
    textFilter?: string,
    rootIdOverride?: string
  ): any {
    let categoryFilter: string | number | undefined;
    let searchFilter: string | undefined;
    let rootId: string | undefined;

    if (categoryOrOptions && typeof categoryOrOptions === 'object' && !Array.isArray(categoryOrOptions)) {
      categoryFilter = categoryOrOptions.category;
      searchFilter = categoryOrOptions.search;
      rootId = categoryOrOptions.rootId;
    } else {
      categoryFilter = categoryOrOptions as string | number | undefined;
      searchFilter = textFilter;
      rootId = rootIdOverride;
    }

    const normCategory = categoryFilter !== undefined && categoryFilter !== null && categoryFilter !== '' && categoryFilter !== 'all'
      ? String(categoryFilter).toLowerCase().trim()
      : undefined;
    const normSearch = searchFilter && String(searchFilter).trim() !== '' ? String(searchFilter).toLowerCase().trim() : undefined;

    const matchesItem = (item: any): boolean => {
      if (!item) return false;
      if (normCategory && !this.matchesCategory(item, normCategory)) {
        return false;
      }
      if (normSearch) {
        const itemName = String(item.name || '').toLowerCase();
        const itemDesc = String(item.description || '').toLowerCase();
        if (!itemName.includes(normSearch) && !itemDesc.includes(normSearch)) {
          return false;
        }
      }
      return true;
    };

    const getChildrenForFolder = (folderId: string) => {
      const folder = this.folders.get(folderId);
      const knownChildren = new Set<string>(folder?.children || []);

      const folderChildren: any[] = [];
      const itemChildren: any[] = [];

      for (const item of this.items.values()) {
        if (item.parent === folderId || knownChildren.has(item.id)) {
          itemChildren.push(item);
        }
      }

      for (const f of this.folders.values()) {
        if (f.id !== folderId && (f.parent === folderId || knownChildren.has(f.id))) {
          folderChildren.push(f);
        }
      }

      const uniqueItems = Array.from(new Map(itemChildren.map(i => [i.id, i])).values());
      const uniqueFolders = Array.from(new Map(folderChildren.map(f => [f.id, f])).values());

      return { folderChildren: uniqueFolders, itemChildren: uniqueItems };
    };

    const pruneFolderNode = (folder: any, visited = new Set<string>()): any | null => {
      if (!folder || visited.has(folder.id)) return null;
      visited.add(folder.id);

      const { folderChildren, itemChildren } = getChildrenForFolder(folder.id);
      const matchingItems = itemChildren.filter(item => matchesItem(item)).map(item => ({ ...item, folder: false, type: 'item' }));

      const prunedSubfolders: any[] = [];
      for (const subfolder of folderChildren) {
        const pruned = pruneFolderNode(subfolder, new Set(visited));
        if (pruned) {
          prunedSubfolders.push(pruned);
        }
      }

      if (matchingItems.length > 0 || prunedSubfolders.length > 0) {
        return {
          ...folder,
          type: 'folder',
          folder: true,
          children: [...prunedSubfolders, ...matchingItems],
        };
      }

      return null;
    };

    const effectiveRootId = rootId || this.rootFolder?.id || this.protocol?.inventoryRoot;
    const rootNode = effectiveRootId ? this.folders.get(effectiveRootId) : null;

    if (rootNode) {
      const pruned = pruneFolderNode(rootNode);
      if (pruned) return pruned;
      return { ...rootNode, type: 'folder', folder: true, children: [] };
    }

    const topLevelFolders = Array.from(this.folders.values()).filter(f => !f.parent || !this.folders.has(f.parent));
    const prunedTopLevel = topLevelFolders.map(f => pruneFolderNode(f)).filter(Boolean);

    return prunedTopLevel;
  }

  /**
   * Helper to flatten a pruned folder tree into an array of visible nodes.
   */
  public flattenTree(treeNodeOrNodes: any): any[] {
    if (!treeNodeOrNodes) return [];
    const nodes = Array.isArray(treeNodeOrNodes) ? treeNodeOrNodes : [treeNodeOrNodes];
    const result: any[] = [];
    const visited = new Set<string>();

    const traverse = (node: any) => {
      if (!node || visited.has(node.id)) return;
      visited.add(node.id);
      result.push(node);

      if (Array.isArray(node.children)) {
        for (const child of node.children) {
          traverse(child);
        }
      }
    };

    for (const node of nodes) {
      traverse(node);
    }

    return result;
  }
}
