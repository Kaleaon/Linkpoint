/**
 * Linkpoint PWA - Inventory Core (Features 21-25)
 * Ported from Android slproto/inventory/
 * 
 * Android Source: app/src/main/java/com/lumiyaviewer/lumiya/slproto/inventory/
 * See: PWA-demo/ANDROID_PORT_ROADMAP.md Phase 2, Priority 2 (Features 21-25)
 * 
 * Handles core inventory functionality including folder structure, item properties,
 * folder sorting, and item movement within the inventory system.
 */

/**
 * InventoryCore class
 * Manages inventory folder structure and item organization
 */
class InventoryCore {
  constructor() {
    // Feature 21: Inventory folder structure
    this.folders = new Map();
    this.rootFolderUUID = null;
    this.systemFolders = new Map(); // Special system folders
    
    // Feature 22: Item properties
    this.items = new Map();
    
    // Feature 23: Folder sorting
    this.sortPreferences = new Map();
    this.defaultSortPreference = { sortBy: 'name', ascending: true };
    
    // Feature 24: Item movement tracking
    this.moveQueue = [];
    
    // Feature 25: Search and filtering
    this.searchIndex = new Map(); // name -> Set of UUIDs
    this.filterCallbacks = [];
    
    // Change notifications
    this.changeCallbacks = [];
    
    // Standard folder types
    this.folderTypes = {
      TEXTURE: 0,
      SOUND: 1,
      CALLING_CARD: 2,
      LANDMARK: 3,
      CLOTHING: 5,
      OBJECT: 6,
      NOTECARD: 7,
      CATEGORY: 8,
      ROOT_INVENTORY: 8,
      LSL_TEXT: 10,
      BODYPART: 13,
      TRASH: 14,
      SNAPSHOT: 15,
      LOST_AND_FOUND: 16,
      ANIMATION: 20,
      GESTURE: 21,
      FAVORITES: 23,
      CURRENT_OUTFIT: 46,
      OUTFIT: 47,
      MY_OUTFITS: 48,
      MESH: 49,
      INBOX: 50,
      OUTBOX: 51,
      BASIC_ROOT: 52,
      MARKETPLACE_LISTINGS: 53,
      SETTINGS: 56
    };
    
    // Item types
    this.itemTypes = {
      TEXTURE: 0,
      SOUND: 1,
      CALLING_CARD: 2,
      LANDMARK: 3,
      OBJECT: 6,
      NOTECARD: 7,
      LSL_TEXT: 10,
      BODYPART: 13,
      WEARABLE: 18,
      ANIMATION: 19,
      GESTURE: 20,
      MESH: 22,
      SETTINGS: 25
    };
  }
  
  /**
   * Add or update a folder
   * @param {Object} folder - Folder data
   */
  setFolder(folder) {
    const isUpdate = this.folders.has(folder.uuid);
    
    const folderData = {
      uuid: folder.uuid,
      parentUUID: folder.parentUUID || null,
      name: folder.name,
      type: folder.type || this.folderTypes.CATEGORY,
      version: folder.version || 0,
      children: folder.children || [],
      descendentCount: folder.descendentCount || 0
    };
    
    this.folders.set(folder.uuid, folderData);
    
    // Set root if this is root folder
    if (folder.type === this.folderTypes.ROOT_INVENTORY || folder.type === this.folderTypes.BASIC_ROOT) {
      this.rootFolderUUID = folder.uuid;
    }
    
    const action = isUpdate ? 'update' : 'add';
    this.notifyChange(action, 'folder', folder.uuid);
  }
  
  /**
   * Get folder by UUID
   * @param {string} folderUUID - UUID of the folder
   * @returns {Object|null} Folder data or null if not found
   */
  getFolder(folderUUID) {
    return this.folders.get(folderUUID) || null;
  }
  
  /**
   * Get root folder UUID
   * @returns {string|null} Root folder UUID or null if not set
   */
  getRootFolder() {
    return this.rootFolderUUID;
  }
  
  /**
   * Get child folders of a folder
   * @param {string} parentUUID - UUID of parent folder
   * @returns {Array} Array of child folder UUIDs
   */
  getChildFolders(parentUUID) {
    const children = [];
    
    this.folders.forEach((folder, uuid) => {
      if (folder.parentUUID === parentUUID) {
        children.push(uuid);
      }
    });
    
    return children;
  }
  
  /**
   * Add or update an item
   * @param {Object} item - Item data
   */
  setItem(item) {
    // Remove old item from search index if updating
    const oldItem = this.items.get(item.uuid);
    if (oldItem) {
      this.removeFromSearchIndex(oldItem);
    }
    
    const itemData = {
      uuid: item.uuid,
      parentUUID: item.parentUUID,
      name: item.name,
      type: item.type,
      assetType: item.assetType,
      assetUUID: item.assetUUID,
      permissions: item.permissions || {},
      flags: item.flags || 0,
      saleType: item.saleType || 0,
      salePrice: item.salePrice || 0,
      creationDate: item.creationDate || Date.now(),
      description: item.description || ''
    };
    
    this.items.set(item.uuid, itemData);
    this.updateSearchIndex(itemData);
    
    const action = oldItem ? 'update' : 'add';
    this.notifyChange(action, 'item', item.uuid);
  }
  
  /**
   * Get item by UUID
   * @param {string} itemUUID - UUID of the item
   * @returns {Object|null} Item data or null if not found
   */
  getItem(itemUUID) {
    return this.items.get(itemUUID) || null;
  }
  
  /**
   * Get items in a folder
   * @param {string} folderUUID - UUID of the folder
   * @returns {Array} Array of item UUIDs in the folder
   */
  getItemsInFolder(folderUUID) {
    const items = [];
    
    this.items.forEach((item, uuid) => {
      if (item.parentUUID === folderUUID) {
        items.push(uuid);
      }
    });
    
    return items;
  }
  
  /**
   * Set folder sort preference
   * @param {string} folderUUID - UUID of the folder
   * @param {string} sortBy - Sort field (name, type, date)
   * @param {boolean} ascending - Sort direction
   */
  setSortPreference(folderUUID, sortBy = 'name', ascending = true) {
    this.sortPreferences.set(folderUUID, { sortBy, ascending });
  }
  
  /**
   * Get sorted items in folder
   * @param {string} folderUUID - UUID of the folder
   * @returns {Array} Array of sorted item objects
   */
  getSortedItems(folderUUID) {
    const itemUUIDs = this.getItemsInFolder(folderUUID);
    const items = itemUUIDs.map(uuid => this.getItem(uuid)).filter(i => i !== null);
    
    const pref = this.sortPreferences.get(folderUUID) || { sortBy: 'name', ascending: true };
    
    items.sort((a, b) => {
      let aVal, bVal;
      
      switch (pref.sortBy) {
        case 'type':
          aVal = a.type;
          bVal = b.type;
          break;
        case 'date':
          aVal = a.creationDate;
          bVal = b.creationDate;
          break;
        case 'name':
        default:
          aVal = a.name.toLowerCase();
          bVal = b.name.toLowerCase();
          break;
      }
      
      if (aVal < bVal) return pref.ascending ? -1 : 1;
      if (aVal > bVal) return pref.ascending ? 1 : -1;
      return 0;
    });
    
    return items;
  }
  
  /**
   * Get sorted child folders
   * @param {string} parentUUID - UUID of parent folder
   * @returns {Array} Array of sorted folder objects
   */
  getSortedFolders(parentUUID) {
    const folderUUIDs = this.getChildFolders(parentUUID);
    const folders = folderUUIDs.map(uuid => this.getFolder(uuid)).filter(f => f !== null);
    
    // Sort folders by name
    folders.sort((a, b) => a.name.toLowerCase().localeCompare(b.name.toLowerCase()));
    
    return folders;
  }
  
  /**
   * Find items by name
   * @param {string} searchTerm - Search term
   * @param {string} folderUUID - Optional folder to search in (searches all if not specified)
   * @returns {Array} Array of matching items
   */
  findItems(searchTerm, folderUUID = null) {
    const results = [];
    const term = searchTerm.toLowerCase();
    
    this.items.forEach((item, uuid) => {
      if (folderUUID && item.parentUUID !== folderUUID) {
        return;
      }
      
      if (item.name.toLowerCase().includes(term)) {
        results.push(item);
      }
    });
    
    return results;
  }
  
  /**
   * Find folders by name
   * @param {string} searchTerm - Search term
   * @returns {Array} Array of matching folders
   */
  findFolders(searchTerm) {
    const results = [];
    const term = searchTerm.toLowerCase();
    
    this.folders.forEach((folder, uuid) => {
      if (folder.name.toLowerCase().includes(term)) {
        results.push(folder);
      }
    });
    
    return results;
  }
  
  /**
   * Get folder path (breadcrumb trail)
   * @param {string} folderUUID - UUID of the folder
   * @returns {Array} Array of folder objects from root to specified folder
   */
  getFolderPath(folderUUID) {
    const path = [];
    let currentUUID = folderUUID;
    
    while (currentUUID) {
      const folder = this.getFolder(currentUUID);
      if (!folder) break;
      
      path.unshift(folder);
      currentUUID = folder.parentUUID;
    }
    
    return path;
  }
  
  /**
   * Get total item count in folder and subfolders (recursive)
   * @param {string} folderUUID - UUID of the folder
   * @returns {number} Total item count
   */
  getTotalItemCount(folderUUID) {
    let count = this.getItemsInFolder(folderUUID).length;
    
    const childFolders = this.getChildFolders(folderUUID);
    childFolders.forEach(childUUID => {
      count += this.getTotalItemCount(childUUID);
    });
    
    return count;
  }
  
  /**
   * Register system folder (like Trash, Lost and Found, etc.)
   * @param {string} type - Folder type name
   * @param {string} folderUUID - UUID of the folder
   */
  registerSystemFolder(type, folderUUID) {
    this.systemFolders.set(type, folderUUID);
  }
  
  /**
   * Get system folder UUID
   * @param {string} type - Folder type name (e.g., 'TRASH', 'LOST_AND_FOUND')
   * @returns {string|null} Folder UUID or null
   */
  getSystemFolder(type) {
    return this.systemFolders.get(type) || null;
  }
  
  /**
   * Check if folder is a system folder
   * @param {string} folderUUID - UUID of the folder
   * @returns {boolean} True if system folder
   */
  isSystemFolder(folderUUID) {
    for (const systemFolderUUID of this.systemFolders.values()) {
      if (systemFolderUUID === folderUUID) {
        return true;
      }
    }
    return false;
  }
  
  /**
   * Update search index for item
   * @param {Object} item - Item data
   */
  updateSearchIndex(item) {
    const terms = item.name.toLowerCase().split(/\s+/);
    terms.forEach(term => {
      if (!this.searchIndex.has(term)) {
        this.searchIndex.set(term, new Set());
      }
      this.searchIndex.get(term).add(item.uuid);
    });
  }
  
  /**
   * Remove item from search index
   * @param {Object} item - Item data
   */
  removeFromSearchIndex(item) {
    const terms = item.name.toLowerCase().split(/\s+/);
    terms.forEach(term => {
      const uuids = this.searchIndex.get(term);
      if (uuids) {
        uuids.delete(item.uuid);
        if (uuids.size === 0) {
          this.searchIndex.delete(term);
        }
      }
    });
  }
  
  /**
   * Fast search using index
   * @param {string} searchTerm - Search term
   * @returns {Array} Array of matching items
   */
  fastSearch(searchTerm) {
    const term = searchTerm.toLowerCase().trim();
    const matchingUUIDs = this.searchIndex.get(term);
    
    if (!matchingUUIDs) {
      return [];
    }
    
    return Array.from(matchingUUIDs)
      .map(uuid => this.getItem(uuid))
      .filter(item => item !== null);
  }
  
  /**
   * Get items by type
   * @param {number} itemType - Item type
   * @param {string} folderUUID - Optional folder to search in
   * @returns {Array} Array of items
   */
  getItemsByType(itemType, folderUUID = null) {
    const results = [];
    
    this.items.forEach((item, uuid) => {
      if (folderUUID && item.parentUUID !== folderUUID) {
        return;
      }
      
      if (item.type === itemType) {
        results.push(item);
      }
    });
    
    return results;
  }
  
  /**
   * Get folders by type
   * @param {number} folderType - Folder type
   * @returns {Array} Array of folders
   */
  getFoldersByType(folderType) {
    const results = [];
    
    this.folders.forEach((folder, uuid) => {
      if (folder.type === folderType) {
        results.push(folder);
      }
    });
    
    return results;
  }
  
  /**
   * Get recent items
   * @param {number} count - Number of items to return
   * @returns {Array} Array of recent items
   */
  getRecentItems(count = 20) {
    const allItems = Array.from(this.items.values());
    allItems.sort((a, b) => b.creationDate - a.creationDate);
    return allItems.slice(0, count);
  }
  
  /**
   * Get folder depth (distance from root)
   * @param {string} folderUUID - UUID of the folder
   * @returns {number} Folder depth
   */
  getFolderDepth(folderUUID) {
    let depth = 0;
    let currentUUID = folderUUID;
    
    while (currentUUID && currentUUID !== this.rootFolderUUID) {
      const folder = this.getFolder(currentUUID);
      if (!folder) break;
      depth++;
      currentUUID = folder.parentUUID;
    }
    
    return depth;
  }
  
  /**
   * Check if folder is ancestor of another
   * @param {string} ancestorUUID - Potential ancestor UUID
   * @param {string} descendantUUID - Potential descendant UUID
   * @returns {boolean} True if ancestor
   */
  isAncestor(ancestorUUID, descendantUUID) {
    let currentUUID = descendantUUID;
    
    while (currentUUID) {
      if (currentUUID === ancestorUUID) {
        return true;
      }
      
      const folder = this.getFolder(currentUUID);
      if (!folder) break;
      currentUUID = folder.parentUUID;
    }
    
    return false;
  }
  
  /**
   * Get all folders (flat list)
   * @returns {Array} Array of all folders
   */
  getAllFolders() {
    return Array.from(this.folders.values());
  }
  
  /**
   * Get all items (flat list)
   * @returns {Array} Array of all items
   */
  getAllItems() {
    return Array.from(this.items.values());
  }
  
  /**
   * Get inventory statistics
   * @returns {Object} Statistics
   */
  getStatistics() {
    return {
      totalFolders: this.folders.size,
      totalItems: this.items.size,
      rootFolderUUID: this.rootFolderUUID,
      systemFolders: this.systemFolders.size,
      searchIndexTerms: this.searchIndex.size
    };
  }
  
  /**
   * Register change callback
   * @param {Function} callback - Callback function(action, type, uuid)
   */
  onChange(callback) {
    this.changeCallbacks.push(callback);
  }
  
  /**
   * Notify change
   * @param {string} action - 'add', 'update', 'delete'
   * @param {string} type - 'folder' or 'item'
   * @param {string} uuid - UUID
   */
  notifyChange(action, type, uuid) {
    this.changeCallbacks.forEach(cb => {
      try {
        cb(action, type, uuid);
      } catch (error) {
        console.error('Error in inventory change callback:', error);
      }
    });
  }
  
  /**
   * Delete item
   * @param {string} itemUUID - UUID of the item
   */
  deleteItem(itemUUID) {
    const item = this.items.get(itemUUID);
    if (item) {
      this.removeFromSearchIndex(item);
      this.items.delete(itemUUID);
      this.notifyChange('delete', 'item', itemUUID);
    }
  }
  
  /**
   * Delete folder (must be empty)
   * @param {string} folderUUID - UUID of the folder
   * @returns {boolean} True if deleted
   */
  deleteFolder(folderUUID) {
    const folder = this.folders.get(folderUUID);
    if (!folder) return false;
    
    // Check if empty
    const childFolders = this.getChildFolders(folderUUID);
    const items = this.getItemsInFolder(folderUUID);
    
    if (childFolders.length > 0 || items.length > 0) {
      console.warn('Cannot delete non-empty folder');
      return false;
    }
    
    this.folders.delete(folderUUID);
    this.notifyChange('delete', 'folder', folderUUID);
    return true;
  }
  
  /**
   * Clear all inventory data
   */
  clear() {
    this.folders.clear();
    this.items.clear();
    this.sortPreferences.clear();
    this.systemFolders.clear();
    this.searchIndex.clear();
    this.moveQueue = [];
    this.rootFolderUUID = null;
  }
}

// Export singleton instance
const inventoryCore = new InventoryCore();
export default inventoryCore;
export { InventoryCore };
