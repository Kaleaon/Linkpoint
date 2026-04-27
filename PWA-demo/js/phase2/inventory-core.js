/**
 * Linkpoint PWA - Inventory Core (Features 21-25)
 * 
 * Phase 2: Core Protocol Extensions - Priority 2
 * Roadmap: PWA-demo/ANDROID_PORT_ROADMAP.md (Lines 51-56)
 * Android Source: app/src/main/java/com/lumiyaviewer/lumiya/slproto/modules/inventory/
 * 
 * Manages inventory folder structure and item properties.
 * 
 * Features:
 * - Feature 21: Inventory folder structure
 * - Feature 22: Item properties
 * - Feature 23: Folder sorting
 * - Feature 24: Item movement
 * 
 * @module phase2/inventory-core
 */

/**
 * Inventory Core Manager
 * Manages inventory folders and items
 */
class InventoryCore {
  constructor() {
    this.folders = new Map();
    this.items = new Map();
    this.rootFolderId = null;
  }

  /**
   * Feature 21: Inventory folder structure
   * Create a new folder in inventory
   * 
   * @param {string} folderId - Folder UUID
   * @param {Object} folderData - Folder properties
   * @returns {Object} Created folder
   * 
   * TODO: Implement folder type categorization (see Android InventoryFolder)
   * TODO: Add support for system folders (Trash, Lost & Found, etc.)
   */
  createFolder(folderId, folderData) {
    if (!folderId || typeof folderId !== 'string') {
      throw new Error('Valid folder ID required');
    }
    if (!folderData || typeof folderData !== 'object') {
      throw new Error('Valid folder data required');
    }
    
    const folder = {
      id: folderId,
      name: folderData.name || 'New Folder',
      parentId: folderData.parentId || this.rootFolderId,
      type: folderData.type || 'normal',
      children: [],
      items: [],
      version: folderData.version || 1,
      created: Date.now()
    };
    
    this.folders.set(folderId, folder);
    
    // Add to parent's children
    if (folder.parentId) {
      const parent = this.folders.get(folder.parentId);
      if (parent) {
        parent.children.push(folderId);
      }
    }
    
    console.log(`[Inventory] Created folder: ${folder.name}`);
    return folder;
  }

  /**
   * Get folder by ID
   * @param {string} folderId - Folder UUID
   * @returns {Object|null}
   */
  getFolder(folderId) {
    return this.folders.get(folderId) || null;
  }

  /**
   * List folder contents
   * @param {string} folderId - Folder UUID
   * @returns {Object} Folder contents (subfolders and items)
   */
  listFolderContents(folderId) {
    const folder = this.folders.get(folderId);
    if (!folder) {
      return { folders: [], items: [] };
    }
    
    return {
      folders: folder.children.map(id => this.folders.get(id)).filter(Boolean),
      items: folder.items.map(id => this.items.get(id)).filter(Boolean)
    };
  }

  /**
   * Feature 22: Item properties
   * Add item to inventory
   * 
   * @param {string} itemId - Item UUID
   * @param {Object} itemData - Item properties
   * @returns {Object} Created item
   * 
   * TODO: Implement asset type validation
   * TODO: Add permission flags support
   */
  addItem(itemId, itemData) {
    if (!itemId || typeof itemId !== 'string') {
      throw new Error('Valid item ID required');
    }
    if (!itemData || typeof itemData !== 'object') {
      throw new Error('Valid item data required');
    }
    
    const item = {
      id: itemId,
      name: itemData.name || 'New Item',
      assetType: itemData.assetType || 'unknown',
      inventoryType: itemData.inventoryType || 'object',
      folderId: itemData.folderId,
      description: itemData.description || '',
      permissions: itemData.permissions || {},
      created: Date.now()
    };
    
    this.items.set(itemId, item);
    
    // Add to folder
    if (item.folderId) {
      const folder = this.folders.get(item.folderId);
      if (folder) {
        folder.items.push(itemId);
      }
    }
    
    console.log(`[Inventory] Added item: ${item.name}`);
    return item;
  }

  /**
   * Get item by ID
   * @param {string} itemId - Item UUID
   * @returns {Object|null}
   */
  getItem(itemId) {
    return this.items.get(itemId) || null;
  }

  /**
   * Feature 23: Folder sorting
   * Sort folder contents by criteria
   * 
   * @param {string} folderId - Folder UUID
   * @param {string} sortBy - Sort criteria ('name', 'date', 'type')
   * @returns {Object} Sorted folder contents
   * 
   * TODO: Implement custom sort order persistence
   */
  sortFolder(folderId, sortBy = 'name') {
    const contents = this.listFolderContents(folderId);
    
    const sorter = (a, b) => {
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
   * 
   * @param {string} itemId - Item UUID
   * @param {string} targetFolderId - Target folder UUID
   * 
   * TODO: Implement move validation (folder types, permissions)
   */
  moveItem(itemId, targetFolderId) {
    if (!itemId || !targetFolderId) {
      throw new Error('Valid item ID and target folder ID required');
    }
    
    const item = this.items.get(itemId);
    if (!item) {
      throw new Error(`Item not found: ${itemId}`);
    }
    
    // Remove from old folder
    if (item.folderId) {
      const oldFolder = this.folders.get(item.folderId);
      if (oldFolder) {
        oldFolder.items = oldFolder.items.filter(id => id !== itemId);
      }
    }
    
    // Add to new folder
    const newFolder = this.folders.get(targetFolderId);
    if (!newFolder) {
      throw new Error(`Target folder not found: ${targetFolderId}`);
    }
    
    newFolder.items.push(itemId);
    item.folderId = targetFolderId;
    
    console.log(`[Inventory] Moved item ${itemId} to folder ${targetFolderId}`);
  }

  /**
   * Set root folder
   * @param {string} folderId - Root folder UUID
   */
  setRootFolder(folderId) {
    this.rootFolderId = folderId;
    console.log(`[Inventory] Set root folder: ${folderId}`);
  }

  /**
   * Get inventory statistics
   * @returns {Object}
   */
  getStats() {
    return {
      totalFolders: this.folders.size,
      totalItems: this.items.size,
      rootFolder: this.rootFolderId
    };
  }
}

// Export for use in other modules
if (typeof module !== 'undefined' && module.exports) {
  module.exports = { InventoryCore };
}

/**
 * Example Usage:
 * 
 * const inventory = new InventoryCore();
 * 
 * // Create root folder
 * const root = inventory.createFolder('root-123', {
 *   name: 'My Inventory'
 * });
 * inventory.setRootFolder('root-123');
 * 
 * // Create subfolder
 * inventory.createFolder('objects-456', {
 *   name: 'Objects',
 *   parentId: 'root-123'
 * });
 * 
 * // Add item
 * inventory.addItem('item-789', {
 *   name: 'My Object',
 *   assetType: 'object',
 *   folderId: 'objects-456'
 * });
 * 
 * // List and sort
 * const contents = inventory.sortFolder('objects-456', 'name');
 */
