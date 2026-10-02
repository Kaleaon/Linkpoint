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

export class InventoryManager extends Utils.EventEmitter {
  public protocol: SLConnectionFull;
  public auth: AuthManager;
  public rootFolder: any = null;
  public items: Map<string, any> = new Map();
  public folders: Map<string, any> = new Map();
  public loadedFromCache: boolean = false;

  constructor(protocolManager: SLConnectionFull, authManager: AuthManager) {
    super();
    this.protocol = protocolManager;
    this.auth = authManager;
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
          if (Array.isArray(inv.folders)) {
            for (const f of inv.folders) {
              const fid = f.id || Utils.generateUUID();
              this.folders.set(fid, { id: fid, name: f.name, type: 'folder', parent: f.parent || folderId, children: [] });
              const parent = this.folders.get(f.parent || folderId);
              if (parent && !parent.children.includes(fid)) parent.children.push(fid);
            }
          }
          if (Array.isArray(inv.items)) {
            for (const item of inv.items) {
              const iid = item.id || Utils.generateUUID();
              this.items.set(iid, { id: iid, name: item.name, type: 'item', assetType: item.assetType, parent: item.parent || folderId, description: item.description });
              const parent = this.folders.get(item.parent || folderId);
              if (parent && !parent.children.includes(iid)) parent.children.push(iid);
            }
          }
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
        this.handleInventoryResponse(data);
      }
    } catch (error) {
      console.error(`Error fetching folder ${folderId}:`, error);
    }
  }

  handleInventoryResponse(data: any) {
    if (!data) return;

    const foldersList = Array.isArray(data.folders) ? data.folders : (data.categories ? [data] : []);

    foldersList.forEach((folderData: any) => {
      if (folderData.categories) {
        folderData.categories.forEach((cat: any) => {
          const folder = { id: cat.category_id || cat.folder_id, name: cat.name, type: 'folder', parent: cat.parent_id, children: [] };
          this.folders.set(folder.id, folder);
          const parent = this.folders.get(folder.parent);
          if (parent && !parent.children.includes(folder.id)) parent.children.push(folder.id);
        });
      }

      if (folderData.items) {
        folderData.items.forEach((itemData: any) => {
           const item = {
             ...itemData,
             id: itemData.item_id,
             name: itemData.name,
             type: 'item',
             assetType: itemData.asset_type ?? itemData.type_default,
             parent: itemData.parent_id,
           };
           this.items.set(item.id, item);
           const parent = this.folders.get(item.parent);
           if (parent && !parent.children.includes(item.id)) parent.children.push(item.id);
        });
      }
    });

    this.emit('inventory_updated');
    this.emit('inventory_loaded');
  }

  handleInventoryUpdate(data: any) {
    this.emit('inventory_updated', data);
  }
}
