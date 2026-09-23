/**
 * Linkpoint PWA - Inventory Management
 */

import { Utils } from './utils';
import { LLSD } from './llsd';
import { SLConnectionFull } from './sl-connection-full';
import { AuthManager } from './auth';

export class InventoryManager extends Utils.EventEmitter {
  public protocol: SLConnectionFull;
  public auth: AuthManager;
  public rootFolder: any = null;
  public items: Map<string, any> = new Map();
  public folders: Map<string, any> = new Map();

  constructor(protocolManager: SLConnectionFull, authManager: AuthManager) {
    super();
    this.protocol = protocolManager;
    this.auth = authManager;
  }

  async init() {
    this.protocol.on('inventory_update', (data: any) => this.handleInventoryUpdate(data));
  }

  async load() {
    if (!this.auth.isLoggedIn()) return;

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

      const response = await fetch(url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/llsd+xml' },
        body: LLSD.buildXML(requestData)
      });

      if (response.ok) {
        const text = await response.text();
        const data = LLSD.parseXML(text);
        this.handleInventoryResponse(data);
      }
    } catch (error) {
      console.error(`Error fetching folder ${folderId}:`, error);
    }
  }

  handleInventoryResponse(data: any) {
    if (!data || !data.folders) return;

    data.folders.forEach((folderData: any) => {
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
  }

  handleInventoryUpdate(data: any) {
    this.emit('inventory_updated', data);
  }
}
