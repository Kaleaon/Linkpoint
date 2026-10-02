/**
 * Linkpoint PWA - Inventory Management
 */

import { Utils } from './utils';
import { LLSD } from './llsd';
import { SLConnectionFull } from './sl-connection-full';
import { AuthManager } from './auth';
import { corsHandler } from './cors-handler';
import { slBridge } from './sl-bridge';
import { localCache } from './local-cache';
import { AisClient } from './ais';

export class InventoryManager extends Utils.EventEmitter {
  public protocol: SLConnectionFull;
  public auth: AuthManager;
  public rootFolder: any = null;
  public items: Map<string, any> = new Map();
  public folders: Map<string, any> = new Map();
  public loadedFromCache: boolean = false;
  public aisClient: AisClient;

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
  }

  async load(forceRebuild = false) {
    if (!this.auth.isLoggedIn()) return;
    const agentId = this.auth.user?.id || this.protocol.agentId || 'current';

    // 1. Check local/flashdrive cache first to avoid slow rebuild
    if (!forceRebuild) {
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
          // If we had a rich cached inventory, we don't need to block on network reload
          if (cached.folders.length > 50) {
            return;
          }
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
          // Persist to selected cache (flashdrive or local) for instant subsequent startups
          await localCache.saveInventory(agentId, {
            folders: inv.folders || [],
            items: inv.items || [],
            rootId,
            rootName: inv.folderName || 'My Inventory',
          });

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
      await this.fetchFolderContents(inventoryRoot);
      this.emit('inventory_loaded');
    } catch (error) {
      console.error('Failed to load inventory:', error);
    }
  }

  async fetchFolderContents(folderId: string) {
    if (slBridge.connected) {
      try {
        const inv = await slBridge.fetchInventory(folderId);
        if (inv) {
          const incomingFolders = Array.isArray(inv.folders) ? inv.folders : [];
          const incomingItems = Array.isArray(inv.items) ? inv.items : [];
          await this.reconcileFolder(folderId, incomingFolders, incomingItems);
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
      }
    } catch (error) {
      console.error(`Error fetching folder ${folderId}:`, error);
    }
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

  handleInventoryUpdate(data: any) {
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

    for (const itemId of itemIds) {
      const item = this.items.get(itemId);
      if (item && slBridge.connected) {
        try {
          await slBridge.sendChat(`/wear ${item.name || itemId}`, 0, 1);
        } catch {
          // Ignore individual packet errors during batch fallback
        }
      }
    }

    this.emit('outfit_changed', { mode, method: 'packet_fallback', cofId, outfitFolderId, itemIds });
    this.emit('outfit_fallback_used', { mode, cofId, outfitFolderId, itemIds });
    this.emit('inventory_updated');

    return { success: true, method: 'packet_fallback' };
  }
}
