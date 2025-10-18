/**
 * Linkpoint PWA - Inventory Operations (Features 26-30)
 * Ported from Android slproto/inventory/
 * 
 * Android Source: app/src/main/java/com/lumiyaviewer/lumiya/slproto/inventory/
 * See: PWA-demo/ANDROID_PORT_ROADMAP.md Phase 2, Priority 2 (Features 26-30)
 * 
 * Handles inventory operations including creating folders, deleting items/folders,
 * moving items, copying items, and rename operations.
 */

/**
 * InventoryOps class
 * Manages inventory modification operations
 */
class InventoryOps {
  constructor(inventoryCore = null, protocol = null) {
    this.inventoryCore = inventoryCore;
    this.protocol = protocol;
    
    // Operation queue for batch processing
    this.operationQueue = [];
    this.isProcessing = false;
    this.processingPromise = null;
    
    // Operation callbacks
    this.callbacks = new Map();
    
    // Operation history for undo/redo
    this.undoStack = [];
    this.redoStack = [];
    this.maxUndoSize = 50;
    
    // Operation states
    this.OPERATION_STATES = {
      PENDING: 'pending',
      PROCESSING: 'processing',
      COMPLETED: 'completed',
      FAILED: 'failed',
      CANCELLED: 'cancelled'
    };
    
    // Trash folder UUID (set via setTrashFolder)
    this.trashFolderUUID = null;
  }
  
  /**
   * Feature 26: Create folder
   * @param {string} parentUUID - UUID of parent folder
   * @param {string} name - Name of new folder
   * @param {number} type - Folder type (from InventoryCore.folderTypes)
   * @returns {Promise<string>} UUID of created folder
   */
  async createFolder(parentUUID, name, type = 8) {
    console.log(`Creating folder "${name}" in ${parentUUID}`);
    
    // Generate new UUID for folder
    const folderUUID = this.generateUUID();
    
    // TODO: Send CreateInventoryFolder message to server
    // TODO: Wait for server confirmation
    // TODO: Update inventory core with new folder
    
    // Placeholder - would normally wait for server response
    if (this.inventoryCore) {
      this.inventoryCore.setFolder({
        uuid: folderUUID,
        parentUUID: parentUUID,
        name: name,
        type: type,
        version: 0,
        children: [],
        descendentCount: 0
      });
    }
    
    return folderUUID;
  }
  
  /**
   * Feature 27: Delete item/folder
   * @param {string} itemUUID - UUID of item or folder to delete
   * @param {boolean} isFolder - True if deleting a folder
   * @returns {Promise<boolean>} True if deletion succeeded
   */
  async deleteItem(itemUUID, isFolder = false) {
    console.log(`Deleting ${isFolder ? 'folder' : 'item'} ${itemUUID}`);
    
    // TODO: Send RemoveInventoryItem or RemoveInventoryFolder message
    // TODO: Wait for server confirmation
    // TODO: Update inventory core to remove item/folder
    // TODO: Handle moving to trash vs permanent delete
    
    if (this.inventoryCore) {
      if (isFolder) {
        // Check if folder has children
        const childFolders = this.inventoryCore.getChildFolders(itemUUID);
        const items = this.inventoryCore.getItemsInFolder(itemUUID);
        
        if (childFolders.length > 0 || items.length > 0) {
          console.warn('Cannot delete non-empty folder');
          return false;
        }
      }
      
      // Placeholder deletion
      return true;
    }
    
    return false;
  }
  
  /**
   * Feature 28: Move item
   * @param {string} itemUUID - UUID of item to move
   * @param {string} targetFolderUUID - UUID of destination folder
   * @returns {Promise<boolean>} True if move succeeded
   */
  async moveItem(itemUUID, targetFolderUUID) {
    console.log(`Moving item ${itemUUID} to folder ${targetFolderUUID}`);
    
    // TODO: Send MoveInventoryItem message to server
    // TODO: Wait for server confirmation
    // TODO: Update inventory core with new parent
    
    if (this.inventoryCore) {
      const item = this.inventoryCore.getItem(itemUUID);
      if (!item) {
        console.error('Item not found:', itemUUID);
        return false;
      }
      
      const targetFolder = this.inventoryCore.getFolder(targetFolderUUID);
      if (!targetFolder) {
        console.error('Target folder not found:', targetFolderUUID);
        return false;
      }
      
      // Update item's parent
      item.parentUUID = targetFolderUUID;
      this.inventoryCore.setItem(item);
      
      return true;
    }
    
    return false;
  }
  
  /**
   * Feature 29: Copy item
   * @param {string} itemUUID - UUID of item to copy
   * @param {string} targetFolderUUID - UUID of destination folder
   * @param {string} newName - Optional new name for copied item
   * @returns {Promise<string>} UUID of copied item
   */
  async copyItem(itemUUID, targetFolderUUID, newName = null) {
    console.log(`Copying item ${itemUUID} to folder ${targetFolderUUID}`);
    
    if (!this.inventoryCore) {
      throw new Error('InventoryCore not available');
    }
    
    const item = this.inventoryCore.getItem(itemUUID);
    if (!item) {
      throw new Error('Item not found: ' + itemUUID);
    }
    
    // Check copy permissions
    if (item.permissions && item.permissions.canCopy === false) {
      throw new Error('Item cannot be copied');
    }
    
    // Generate new UUID for copied item
    const newItemUUID = this.generateUUID();
    
    // TODO: Send CopyInventoryItem message to server
    // TODO: Wait for server confirmation with new item UUID
    // TODO: Handle asset copying if needed
    
    // Create copy in inventory
    const copiedItem = {
      ...item,
      uuid: newItemUUID,
      parentUUID: targetFolderUUID,
      name: newName || (item.name + ' (copy)'),
      creationDate: Date.now()
    };
    
    this.inventoryCore.setItem(copiedItem);
    
    return newItemUUID;
  }
  
  /**
   * Feature 30: Rename item or folder
   * @param {string} itemUUID - UUID of item or folder to rename
   * @param {string} newName - New name
   * @param {boolean} isFolder - True if renaming a folder
   * @returns {Promise<boolean>} True if rename succeeded
   */
  async renameItem(itemUUID, newName, isFolder = false) {
    console.log(`Renaming ${isFolder ? 'folder' : 'item'} ${itemUUID} to "${newName}"`);
    
    if (!newName || newName.trim().length === 0) {
      console.error('Invalid name');
      return false;
    }
    
    // TODO: Send UpdateInventoryItem or UpdateInventoryFolder message
    // TODO: Wait for server confirmation
    
    if (this.inventoryCore) {
      if (isFolder) {
        const folder = this.inventoryCore.getFolder(itemUUID);
        if (!folder) {
          console.error('Folder not found:', itemUUID);
          return false;
        }
        
        folder.name = newName;
        this.inventoryCore.setFolder(folder);
      } else {
        const item = this.inventoryCore.getItem(itemUUID);
        if (!item) {
          console.error('Item not found:', itemUUID);
          return false;
        }
        
        item.name = newName;
        this.inventoryCore.setItem(item);
      }
      
      return true;
    }
    
    return false;
  }
  
  /**
   * Move folder (similar to move item but for folders)
   * @param {string} folderUUID - UUID of folder to move
   * @param {string} targetParentUUID - UUID of destination parent folder
   * @returns {Promise<boolean>} True if move succeeded
   */
  async moveFolder(folderUUID, targetParentUUID) {
    console.log(`Moving folder ${folderUUID} to ${targetParentUUID}`);
    
    if (!this.inventoryCore) {
      return false;
    }
    
    const folder = this.inventoryCore.getFolder(folderUUID);
    if (!folder) {
      console.error('Folder not found:', folderUUID);
      return false;
    }
    
    // Prevent moving folder into its own descendant
    if (this.isDescendant(targetParentUUID, folderUUID)) {
      console.error('Cannot move folder into its own descendant');
      return false;
    }
    
    // TODO: Send MoveInventoryFolder message
    // TODO: Wait for confirmation
    
    folder.parentUUID = targetParentUUID;
    this.inventoryCore.setFolder(folder);
    
    return true;
  }
  
  /**
   * Check if a folder is a descendant of another
   * @param {string} folderUUID - UUID to check
   * @param {string} ancestorUUID - Potential ancestor UUID
   * @returns {boolean} True if folderUUID is descendant of ancestorUUID
   */
  isDescendant(folderUUID, ancestorUUID) {
    let currentUUID = folderUUID;
    
    while (currentUUID) {
      if (currentUUID === ancestorUUID) {
        return true;
      }
      
      const folder = this.inventoryCore.getFolder(currentUUID);
      if (!folder) break;
      
      currentUUID = folder.parentUUID;
    }
    
    return false;
  }
  
  /**
   * Generate a UUID (placeholder - should use proper UUID generation)
   * @returns {string} Generated UUID
   */
  generateUUID() {
    // TODO: Replace with proper UUID v4 generation
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
      const r = Math.random() * 16 | 0;
      const v = c === 'x' ? r : (r & 0x3 | 0x8);
      return v.toString(16);
    });
  }
  
  /**
   * Batch operation - process multiple operations efficiently
   * @param {Array} operations - Array of operation objects
   * @returns {Promise<Array>} Results of operations
   */
  async batchOperations(operations) {
    console.log(`Processing ${operations.length} batch operations`);
    
    // TODO: Implement batch operation processing
    // TODO: Send batched messages to server
    // TODO: Handle partial failures
    
    const results = [];
    for (const op of operations) {
      try {
        switch (op.type) {
          case 'create':
            results.push(await this.createFolder(op.parentUUID, op.name, op.folderType));
            break;
          case 'delete':
            results.push(await this.deleteItem(op.uuid, op.isFolder));
            break;
          case 'move':
            results.push(await this.moveItem(op.uuid, op.targetUUID));
            break;
          case 'copy':
            results.push(await this.copyItem(op.uuid, op.targetUUID, op.newName));
            break;
          case 'rename':
            results.push(await this.renameItem(op.uuid, op.newName, op.isFolder));
            break;
          default:
            results.push({ error: 'Unknown operation type' });
        }
      } catch (error) {
        results.push({ error: error.message });
      }
    }
    
    return results;
  }
  
  /**
   * Set trash folder UUID
   * @param {string} folderUUID - UUID of trash folder
   */
  setTrashFolder(folderUUID) {
    this.trashFolderUUID = folderUUID;
  }
  
  /**
   * Move item to trash
   * @param {string} itemUUID - UUID of item to trash
   * @param {boolean} isFolder - True if item is a folder
   * @returns {Promise<boolean>} True if succeeded
   */
  async moveToTrash(itemUUID, isFolder = false) {
    if (!this.trashFolderUUID) {
      console.error('Trash folder not set');
      return false;
    }
    
    const result = await this.moveItem(itemUUID, this.trashFolderUUID);
    
    if (result && isFolder) {
      return await this.moveFolder(itemUUID, this.trashFolderUUID);
    }
    
    return result;
  }
  
  /**
   * Restore item from trash
   * @param {string} itemUUID - UUID of item to restore
   * @param {string} targetFolderUUID - UUID of destination folder
   * @returns {Promise<boolean>} True if succeeded
   */
  async restoreFromTrash(itemUUID, targetFolderUUID) {
    return await this.moveItem(itemUUID, targetFolderUUID);
  }
  
  /**
   * Empty trash (delete all items in trash permanently)
   * @returns {Promise<number>} Number of items deleted
   */
  async emptyTrash() {
    if (!this.trashFolderUUID || !this.inventoryCore) {
      return 0;
    }
    
    const items = this.inventoryCore.getItemsInFolder(this.trashFolderUUID);
    const folders = this.inventoryCore.getChildFolders(this.trashFolderUUID);
    
    let deleted = 0;
    
    // Delete all items
    for (const itemUUID of items) {
      if (await this.deleteItem(itemUUID, false)) {
        deleted++;
      }
    }
    
    // Delete all folders recursively
    for (const folderUUID of folders) {
      if (await this.deleteFolderRecursive(folderUUID)) {
        deleted++;
      }
    }
    
    return deleted;
  }
  
  /**
   * Delete folder and all contents recursively
   * @param {string} folderUUID - UUID of folder to delete
   * @returns {Promise<boolean>} True if succeeded
   */
  async deleteFolderRecursive(folderUUID) {
    if (!this.inventoryCore) return false;
    
    // Get all children
    const childFolders = this.inventoryCore.getChildFolders(folderUUID);
    const items = this.inventoryCore.getItemsInFolder(folderUUID);
    
    // Delete all items
    for (const itemUUID of items) {
      await this.deleteItem(itemUUID, false);
    }
    
    // Recursively delete child folders
    for (const childUUID of childFolders) {
      await this.deleteFolderRecursive(childUUID);
    }
    
    // Delete the folder itself
    return await this.deleteItem(folderUUID, true);
  }
  
  /**
   * Validate operation before execution
   * @param {string} operation - Operation type
   * @param {Object} params - Operation parameters
   * @returns {Object} Validation result {valid, error}
   */
  validateOperation(operation, params) {
    if (!this.inventoryCore) {
      return { valid: false, error: 'Inventory core not available' };
    }
    
    switch (operation) {
      case 'move':
        // Check if source exists
        const item = this.inventoryCore.getItem(params.itemUUID);
        if (!item) {
          return { valid: false, error: 'Source item not found' };
        }
        
        // Check if target folder exists
        const targetFolder = this.inventoryCore.getFolder(params.targetUUID);
        if (!targetFolder) {
          return { valid: false, error: 'Target folder not found' };
        }
        
        // Check if not moving to self
        if (params.itemUUID === params.targetUUID) {
          return { valid: false, error: 'Cannot move to self' };
        }
        
        break;
        
      case 'copy':
        // Similar validation as move
        if (!this.inventoryCore.getItem(params.itemUUID)) {
          return { valid: false, error: 'Source item not found' };
        }
        
        if (!this.inventoryCore.getFolder(params.targetUUID)) {
          return { valid: false, error: 'Target folder not found' };
        }
        
        break;
        
      case 'rename':
        if (!params.newName || params.newName.trim().length === 0) {
          return { valid: false, error: 'Invalid name' };
        }
        
        if (params.newName.length > 63) {
          return { valid: false, error: 'Name too long (max 63 characters)' };
        }
        
        break;
        
      case 'create':
        if (!params.name || params.name.trim().length === 0) {
          return { valid: false, error: 'Invalid folder name' };
        }
        
        if (!this.inventoryCore.getFolder(params.parentUUID)) {
          return { valid: false, error: 'Parent folder not found' };
        }
        
        break;
    }
    
    return { valid: true };
  }
  
  /**
   * Add operation to undo stack
   * @param {Object} operation - Operation data
   */
  addToUndoStack(operation) {
    this.undoStack.push(operation);
    
    if (this.undoStack.length > this.maxUndoSize) {
      this.undoStack.shift();
    }
    
    // Clear redo stack when new operation is performed
    this.redoStack = [];
  }
  
  /**
   * Undo last operation
   * @returns {Promise<boolean>} True if undo succeeded
   */
  async undo() {
    if (this.undoStack.length === 0) {
      return false;
    }
    
    const operation = this.undoStack.pop();
    
    try {
      // Reverse the operation
      switch (operation.type) {
        case 'create':
          await this.deleteItem(operation.uuid, operation.isFolder);
          break;
          
        case 'delete':
          // Would need to restore from backup
          console.warn('Cannot undo delete without backup data');
          break;
          
        case 'move':
          await this.moveItem(operation.uuid, operation.oldParent);
          break;
          
        case 'rename':
          await this.renameItem(operation.uuid, operation.oldName, operation.isFolder);
          break;
          
        case 'copy':
          await this.deleteItem(operation.newUUID, false);
          break;
      }
      
      this.redoStack.push(operation);
      return true;
    } catch (error) {
      console.error('Undo failed:', error);
      this.undoStack.push(operation); // Put it back
      return false;
    }
  }
  
  /**
   * Redo last undone operation
   * @returns {Promise<boolean>} True if redo succeeded
   */
  async redo() {
    if (this.redoStack.length === 0) {
      return false;
    }
    
    const operation = this.redoStack.pop();
    
    try {
      // Re-execute the operation
      switch (operation.type) {
        case 'create':
          await this.createFolder(operation.parentUUID, operation.name, operation.folderType);
          break;
          
        case 'delete':
          await this.deleteItem(operation.uuid, operation.isFolder);
          break;
          
        case 'move':
          await this.moveItem(operation.uuid, operation.newParent);
          break;
          
        case 'rename':
          await this.renameItem(operation.uuid, operation.newName, operation.isFolder);
          break;
          
        case 'copy':
          await this.copyItem(operation.uuid, operation.targetUUID, operation.newName);
          break;
      }
      
      this.undoStack.push(operation);
      return true;
    } catch (error) {
      console.error('Redo failed:', error);
      this.redoStack.push(operation); // Put it back
      return false;
    }
  }
  
  /**
   * Get undo stack size
   * @returns {number} Number of undoable operations
   */
  canUndo() {
    return this.undoStack.length > 0;
  }
  
  /**
   * Get redo stack size
   * @returns {number} Number of redoable operations
   */
  canRedo() {
    return this.redoStack.length > 0;
  }
  
  /**
   * Clear undo/redo history
   */
  clearHistory() {
    this.undoStack = [];
    this.redoStack = [];
  }
  
  /**
   * Queue operation for processing
   * @param {Object} operation - Operation to queue
   * @returns {Promise} Promise that resolves when operation completes
   */
  queueOperation(operation) {
    return new Promise((resolve, reject) => {
      operation.resolve = resolve;
      operation.reject = reject;
      operation.state = this.OPERATION_STATES.PENDING;
      
      this.operationQueue.push(operation);
      
      // Start processing if not already
      if (!this.isProcessing) {
        this.processQueue();
      }
    });
  }
  
  /**
   * Process operation queue
   */
  async processQueue() {
    if (this.isProcessing || this.operationQueue.length === 0) {
      return;
    }
    
    this.isProcessing = true;
    
    while (this.operationQueue.length > 0) {
      const operation = this.operationQueue.shift();
      operation.state = this.OPERATION_STATES.PROCESSING;
      
      try {
        let result;
        
        switch (operation.type) {
          case 'create':
            result = await this.createFolder(operation.parentUUID, operation.name, operation.folderType);
            break;
          case 'delete':
            result = await this.deleteItem(operation.uuid, operation.isFolder);
            break;
          case 'move':
            result = await this.moveItem(operation.uuid, operation.targetUUID);
            break;
          case 'copy':
            result = await this.copyItem(operation.uuid, operation.targetUUID, operation.newName);
            break;
          case 'rename':
            result = await this.renameItem(operation.uuid, operation.newName, operation.isFolder);
            break;
        }
        
        operation.state = this.OPERATION_STATES.COMPLETED;
        operation.resolve(result);
      } catch (error) {
        operation.state = this.OPERATION_STATES.FAILED;
        operation.reject(error);
      }
    }
    
    this.isProcessing = false;
  }
  
  /**
   * Get operation queue status
   * @returns {Object} Queue status
   */
  getQueueStatus() {
    return {
      pending: this.operationQueue.length,
      isProcessing: this.isProcessing
    };
  }
}

// Export class (no singleton for this one as it depends on inventoryCore)
export default InventoryOps;
export { InventoryOps };
