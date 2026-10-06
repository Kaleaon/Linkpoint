/**
 * Linkpoint PWA - Inventory Core (Features 21-25)
 *
 * Phase 2: Core Protocol Extensions - Priority 2
 * Roadmap: PWA-demo/ANDROID_PORT_ROADMAP.md (Lines 51-56)
 * Android Source: app/src/main/java/com/lumiyaviewer/lumiya/slproto/modules/inventory/
 *
 * Manages inventory folder structure and item properties.
 */

export class InventoryCore {
  private folders: Map<string, any> = new Map();
  private items: Map<string, any> = new Map();
  public rootFolderId: string | null = null;

  /**
   * Feature 21: Inventory folder structure
   * Create a new folder in inventory
   */
  createFolder(folderId: string, folderData: any) {
    if (!folderId || typeof folderId !== 'string') {
      throw new Error('Valid folder ID required');
    }
    if (!folderData || typeof folderData !== 'object') {
      throw new Error('Valid folder data required');
    }

    const existingFolder = this.folders.get(folderId);
    if (existingFolder && existingFolder.parentId) {
      const oldParent = this.folders.get(existingFolder.parentId);
      if (oldParent && Array.isArray(oldParent.children)) {
        oldParent.children = oldParent.children.filter((id: string) => id !== folderId);
      }
    }

    const parentId = folderData.parentId || this.rootFolderId;
    const folder = {
      id: folderId,
      name: folderData.name || 'New Folder',
      parentId: parentId,
      type: folderData.type || 'normal',
      children: existingFolder && Array.isArray(existingFolder.children) ? Array.from(new Set(existingFolder.children)) : [],
      items: existingFolder && Array.isArray(existingFolder.items) ? Array.from(new Set(existingFolder.items)) : [],
      version: folderData.version || 1,
      created: existingFolder?.created || Date.now()
    };

    this.folders.set(folderId, folder);

    // Add to parent's children
    if (folder.parentId) {
      const parent = this.folders.get(folder.parentId);
      if (parent) {
        if (!Array.isArray(parent.children)) {
          parent.children = [];
        }
        if (!parent.children.includes(folderId)) {
          parent.children.push(folderId);
        }
      }
    }

    console.log(`[Inventory] Created folder: ${folder.name}`);
    return folder;
  }

  getFolder(folderId: string) {
    return this.folders.get(folderId) || null;
  }

  /**
   * List folder contents
   */
  listFolderContents(folderId: string) {
    const folder = this.folders.get(folderId);
    if (!folder) {
      return { folders: [], items: [] };
    }

    return {
      folders: Array.from(new Set(folder.children || [])).map((id: string) => this.folders.get(id)).filter(Boolean),
      items: Array.from(new Set(folder.items || [])).map((id: string) => this.items.get(id)).filter(Boolean)
    };
  }

  /**
   * Feature 22: Item properties
   * Add item to inventory
   */
  addItem(itemId: string, itemData: any) {
    if (!itemId || typeof itemId !== 'string') {
      throw new Error('Valid item ID required');
    }
    if (!itemData || typeof itemData !== 'object') {
      throw new Error('Valid item data required');
    }

    const existingItem = this.items.get(itemId);
    const targetFolderId = itemData.folderId;

    // Detach from prior folder if present
    this.folders.forEach((f) => {
      if (Array.isArray(f.items) && f.items.includes(itemId)) {
        f.items = f.items.filter((id: string) => id !== itemId);
      }
    });

    const item = {
      id: itemId,
      name: itemData.name || 'New Item',
      assetType: itemData.assetType || 'unknown',
      inventoryType: itemData.inventoryType || 'object',
      folderId: targetFolderId,
      description: itemData.description || '',
      permissions: itemData.permissions || {},
      created: existingItem?.created || Date.now()
    };

    this.items.set(itemId, item);

    // Add to folder
    if (item.folderId) {
      const folder = this.folders.get(item.folderId);
      if (folder) {
        if (!Array.isArray(folder.items)) {
          folder.items = [];
        }
        if (!folder.items.includes(itemId)) {
          folder.items.push(itemId);
        }
      }
    }

    console.log(`[Inventory] Added item: ${item.name}`);
    return item;
  }

  getItem(itemId: string) {
    return this.items.get(itemId) || null;
  }

  /**
   * Feature 23: Folder sorting
   * Sort folder contents by criteria
   */
  sortFolder(folderId: string, sortBy: string = 'name') {
    const contents = this.listFolderContents(folderId);

    const sorter = (a: any, b: any) => {
      if (sortBy === 'name') {
        return (a.name || '').localeCompare(b.name || '');
      } else if (sortBy === 'date') {
        return (a.created || 0) - (b.created || 0);
      } else if (sortBy === 'type') {
        return (a.type || a.assetType || '').localeCompare(b.type || b.assetType || '');
      }
      return 0;
    };

    contents.folders.sort(sorter);
    contents.items.sort(sorter);

    return contents;
  }

  /**
   * Feature 24: Item movement
   * Move item to different folder
   */
  moveItem(itemId: string, targetFolderId: string) {
    if (!itemId || !targetFolderId) {
      throw new Error('Valid item ID and target folder ID required');
    }

    const item = this.items.get(itemId);
    if (!item) {
      throw new Error(`Item not found: ${itemId}`);
    }

    const newFolder = this.folders.get(targetFolderId);
    if (!newFolder) {
      throw new Error(`Target folder not found: ${targetFolderId}`);
    }

    // Remove from all folders to ensure atomic detachment
    this.folders.forEach((folder) => {
      if (Array.isArray(folder.items)) {
        folder.items = folder.items.filter((id: string) => id !== itemId);
      }
    });

    if (!Array.isArray(newFolder.items)) {
      newFolder.items = [];
    }
    if (!newFolder.items.includes(itemId)) {
      newFolder.items.push(itemId);
    }
    item.folderId = targetFolderId;

    console.log(`[Inventory] Moved item ${itemId} to folder ${targetFolderId}`);
  }

  setRootFolder(folderId: string) {
    this.rootFolderId = folderId;
    console.log(`[Inventory] Set root folder: ${folderId}`);
  }

  getStats() {
    return {
      totalFolders: this.folders.size,
      totalItems: this.items.size,
      rootFolder: this.rootFolderId
    };
  }
}
