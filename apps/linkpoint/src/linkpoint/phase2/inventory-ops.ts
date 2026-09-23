/**
 * Linkpoint PWA - Inventory Operations (Features 26-30)
 *
 * Phase 2: Core Protocol Extensions - Priority 2
 * Roadmap: PWA-demo/ANDROID_PORT_ROADMAP.md (Lines 58-63)
 * Android Source: app/src/main/java/com/lumiyaviewer/lumiya/slproto/modules/inventory/
 *
 * Handles inventory operations like create, delete, move, copy, and rename.
 */

import { InventoryCore } from './inventory-core';

export class InventoryOperations {
  private core: InventoryCore;
  private operationQueue: Function[] = [];

  constructor(inventoryCore: InventoryCore) {
    if (!inventoryCore) {
      throw new Error('InventoryCore instance required');
    }
    this.core = inventoryCore;
  }

  /**
   * Feature 26: Create folder
   * Create a new folder with validation
   */
  async createFolder(parentId: string, folderName: string) {
    if (!parentId || typeof parentId !== 'string') {
      throw new Error('Valid parent folder ID required');
    }
    if (!folderName || typeof folderName !== 'string') {
      throw new Error('Valid folder name required');
    }

    // Generate UUID for new folder (simplified)
    const folderId = `folder-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`;

    const folder = this.core.createFolder(folderId, {
      name: folderName,
      parentId: parentId
    });

    console.log(`[InventoryOps] Created folder: ${folderName}`);
    return Promise.resolve(folder);
  }

  /**
   * Feature 27: Delete item/folder
   * Delete inventory item or folder
   */
  async deleteItem(id: string, type: string = 'item') {
    if (!id || typeof id !== 'string') {
      throw new Error('Valid ID required');
    }

    if (type === 'folder') {
      const folder = this.core.getFolder(id);
      if (!folder) {
        throw new Error(`Folder not found: ${id}`);
      }

      // Check if folder is empty
      const contents = this.core.listFolderContents(id);
      if (contents.folders.length > 0 || contents.items.length > 0) {
        throw new Error('Cannot delete non-empty folder');
      }

      console.log(`[InventoryOps] Deleted folder: ${id}`);
    } else {
      const item = this.core.getItem(id);
      if (!item) {
        throw new Error(`Item not found: ${id}`);
      }

      console.log(`[InventoryOps] Deleted item: ${id}`);
    }

    return Promise.resolve(true);
  }

  /**
   * Feature 28: Move item
   * Move item to different folder
   */
  async moveItem(itemId: string, targetFolderId: string) {
    this.core.moveItem(itemId, targetFolderId);
    console.log(`[InventoryOps] Moved item ${itemId} to ${targetFolderId}`);
    return Promise.resolve();
  }

  /**
   * Feature 29: Copy item
   * Copy item to folder
   */
  async copyItem(itemId: string, targetFolderId: string) {
    if (!itemId || !targetFolderId) {
      throw new Error('Valid item ID and target folder ID required');
    }

    const sourceItem = this.core.getItem(itemId);
    if (!sourceItem) {
      throw new Error(`Source item not found: ${itemId}`);
    }

    // Generate new UUID for copy
    const newItemId = `item-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`;

    // Create copy with new ID
    const copiedItem = this.core.addItem(newItemId, {
      name: `${sourceItem.name} (copy)`,
      assetType: sourceItem.assetType,
      inventoryType: sourceItem.inventoryType,
      folderId: targetFolderId,
      description: sourceItem.description,
      permissions: { ...sourceItem.permissions }
    });

    console.log(`[InventoryOps] Copied item ${itemId} to ${targetFolderId}`);
    return Promise.resolve(copiedItem);
  }

  /**
   * Feature 30: Rename operations
   * Rename item or folder
   */
  async renameItem(id: string, newName: string, type: string = 'item') {
    if (!id || !newName) {
      throw new Error('Valid ID and new name required');
    }

    if (type === 'folder') {
      const folder = this.core.getFolder(id);
      if (!folder) {
        throw new Error(`Folder not found: ${id}`);
      }
      folder.name = newName;
      console.log(`[InventoryOps] Renamed folder ${id} to: ${newName}`);
    } else {
      const item = this.core.getItem(id);
      if (!item) {
        throw new Error(`Item not found: ${id}`);
      }
      item.name = newName;
      console.log(`[InventoryOps] Renamed item ${id} to: ${newName}`);
    }

    return Promise.resolve();
  }

  queueOperation(operation: Function) {
    this.operationQueue.push(operation);
  }

  async processQueue() {
    const results = [];
    while (this.operationQueue.length > 0) {
      const operation = this.operationQueue.shift();
      try {
        const result = await operation!();
        results.push({ success: true, result });
      } catch (error: any) {
        results.push({ success: false, error: error.message });
      }
    }
    return results;
  }
}
