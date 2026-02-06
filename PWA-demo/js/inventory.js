/**
 * Linkpoint PWA - Inventory Management
 */

class InventoryManager extends Utils.EventEmitter {
  constructor(protocolManager, authManager) {
    super();
    this.protocol = protocolManager;
    this.auth = authManager;
    this.rootFolder = null;
    this.items = new Map();
    this.folders = new Map();
    this.selectedItem = null;
  }

  /**
   * Initialize inventory
   */
  async init() {
    // Setup protocol listeners
    this.protocol.on('inventory_update', (data) => this.handleInventoryUpdate(data));
    this.protocol.on('inventory_item_received', (data) => this.handleItemReceived(data));
    
    // Setup UI
    this.setupUI();
  }

  /**
   * Setup UI
   */
  setupUI() {
    const refreshBtn = document.getElementById('refresh-inventory');
    refreshBtn?.addEventListener('click', () => this.refresh());

    const inventoryTree = document.getElementById('inventory-tree');
    inventoryTree?.addEventListener('click', (e) => {
      const item = e.target.closest('.inventory-item');
      if (item) {
        this.selectItem(item.dataset.itemId);
      }
    });
  }

  /**
   * Load inventory from server
   */
  async load() {
    if (!this.auth.isLoggedIn()) {
      Utils.showToast('Please login first', 'warning');
      return;
    }

    const inventoryRoot = this.protocol.inventoryRoot;
    if (!inventoryRoot) {
      console.warn('No inventory root found in protocol');
      return;
    }

    try {
      Utils.showToast('Loading inventory...', 'info');
      this.emit('inventory_loading');

      // Set root folder
      this.rootFolder = {
        id: inventoryRoot,
        name: 'My Inventory',
        type: 'folder',
        parent: null,
        children: []
      };
      this.folders.set(inventoryRoot, this.rootFolder);

      // Fetch root contents
      await this.fetchFolderContents(inventoryRoot);

      this.renderInventory();
      this.emit('inventory_loaded');
      Utils.showToast('Inventory loaded', 'success');
      
    } catch (error) {
      console.error('Failed to load inventory:', error);
      Utils.showToast('Failed to load inventory', 'error');
    }
  }

  /**
   * Get capability URL by name
   */
  getCapability(name) {
    if (this.protocol.getCapability) {
      return this.protocol.getCapability(name);
    }
    return this.protocol.capabilities ? this.protocol.capabilities[name] : null;
  }

  /**
   * Fetch folder contents
   */
  async fetchFolderContents(folderId) {
    const url = this.getCapability('FetchInventoryDescendents2');
    if (!url) {
      console.warn('FetchInventoryDescendents2 capability not available');
      return;
    }

    try {
      const requestData = {
        folders: [{
          folder_id: folderId,
          owner_id: this.auth.getUser().agentId || this.auth.getUser().id,
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

      if (!response.ok) throw new Error(`HTTP ${response.status}`);

      const xmlText = await response.text();
      const data = LLSD.parseXML(xmlText);

      this.handleInventoryResponse(data);

    } catch (error) {
      console.error(`Error fetching folder ${folderId}:`, error);
    }
  }

  /**
   * Handle inventory response data
   */
  handleInventoryResponse(data) {
    if (!data || !data.folders) return;

    data.folders.forEach(folderData => {
      // Process subfolders
      if (folderData.categories) {
        folderData.categories.forEach(cat => {
          const folder = {
            id: cat.category_id || cat.folder_id,
            name: cat.name,
            type: 'folder',
            parent: cat.parent_id,
            children: []
          };
          this.folders.set(folder.id, folder);

          // Add to parent
          const parent = this.folders.get(folder.parent);
          if (parent) {
             if (!parent.children.includes(folder.id)) {
               parent.children.push(folder.id);
             }
          }
        });
      }

      // Process items
      if (folderData.items) {
        folderData.items.forEach(itemData => {
           const item = {
             id: itemData.item_id,
             name: itemData.name,
             type: 'item',
             parent: itemData.parent_id,
             assetType: itemData.type,
             inventoryType: itemData.inv_type,
             description: itemData.desc,
             permissions: itemData.permissions,
             sale_info: itemData.sale_info,
             created_at: itemData.created_at
           };
           this.items.set(item.id, item);

           // Add to parent
           const parent = this.folders.get(item.parent);
           if (parent) {
             if (!parent.children.includes(item.id)) {
               parent.children.push(item.id);
             }
           }
        });
      }
    });
  }

  /**
   * Render inventory tree
   */
  renderInventory() {
    const tree = document.getElementById('inventory-tree');
    if (!tree) return;

    tree.innerHTML = '';

    // Render root folder
    const rootEl = this.createFolderElement(this.rootFolder);
    tree.appendChild(rootEl);

    // Render child folders
    this.rootFolder.children.forEach(childId => {
      const folder = this.folders.get(childId);
      if (folder) {
        const folderEl = this.createFolderElement(folder);
        rootEl.appendChild(folderEl);
      }
    });
  }

  /**
   * Create folder element
   */
  createFolderElement(folder) {
    const div = document.createElement('div');
    div.className = 'inventory-item folder';
    div.dataset.itemId = folder.id;
    
    const icon = folder.icon || '📁';
    div.innerHTML = `
      <span class="folder-icon">${icon}</span>
      <span class="folder-name">${folder.name}</span>
      <span class="item-count">(${folder.children?.length || 0})</span>
    `;

    // Add children container
    const childrenContainer = document.createElement('div');
    childrenContainer.className = 'folder-children';
    childrenContainer.style.display = 'none';
    childrenContainer.style.paddingLeft = '20px';
    div.appendChild(childrenContainer);

    // Toggle children on click
    div.querySelector('.folder-name')?.addEventListener('click', (e) => {
      e.stopPropagation();
      const expanded = childrenContainer.style.display === 'block';
      childrenContainer.style.display = expanded ? 'none' : 'block';
      div.classList.toggle('expanded', !expanded);
    });

    return div;
  }

  /**
   * Select item
   */
  selectItem(itemId) {
    this.selectedItem = this.items.get(itemId) || this.folders.get(itemId);
    
    // Update UI
    document.querySelectorAll('.inventory-item').forEach(el => {
      el.classList.remove('selected');
    });
    
    const itemEl = document.querySelector(`[data-item-id="${itemId}"]`);
    itemEl?.classList.add('selected');

    this.emit('item_selected', this.selectedItem);
  }

  /**
   * Add item to inventory
   */
  addItem(item) {
    this.items.set(item.id, item);
    
    // Add to folder
    const folder = this.folders.get(item.parent);
    if (folder) {
      folder.children.push(item.id);
    }

    this.emit('item_added', item);
  }

  /**
   * Remove item
   */
  removeItem(itemId) {
    const item = this.items.get(itemId);
    if (!item) return;

    // Remove from folder
    const folder = this.folders.get(item.parent);
    if (folder) {
      folder.children = folder.children.filter(id => id !== itemId);
    }

    this.items.delete(itemId);
    this.emit('item_removed', itemId);
  }

  /**
   * Move item to folder
   */
  moveItem(itemId, targetFolderId) {
    const item = this.items.get(itemId);
    if (!item) return;

    // Remove from old folder
    const oldFolder = this.folders.get(item.parent);
    if (oldFolder) {
      oldFolder.children = oldFolder.children.filter(id => id !== itemId);
    }

    // Add to new folder
    const newFolder = this.folders.get(targetFolderId);
    if (newFolder) {
      newFolder.children.push(itemId);
      item.parent = targetFolderId;
    }

    this.emit('item_moved', { itemId, targetFolderId });
  }

  /**
   * Search inventory
   */
  search(query) {
    if (!query) return [];

    const results = [];
    const lowerQuery = query.toLowerCase();

    this.items.forEach(item => {
      if (item.name.toLowerCase().includes(lowerQuery)) {
        results.push(item);
      }
    });

    return results;
  }

  /**
   * Handle inventory update from server
   */
  handleInventoryUpdate(data) {
    // Update local inventory
    if (data.items) {
      data.items.forEach(item => this.addItem(item));
    }

    this.renderInventory();
    this.emit('inventory_updated', data);
  }

  /**
   * Handle item received
   */
  handleItemReceived(data) {
    this.addItem(data.item);
    this.renderInventory();
    Utils.showToast(`Received: ${data.item.name}`, 'success');
  }

  /**
   * Refresh inventory
   */
  async refresh() {
    this.items.clear();
    this.folders.clear();
    this.rootFolder = null;
    await this.load();
  }

  /**
   * Get selected item
   */
  getSelectedItem() {
    return this.selectedItem;
  }
}

// Make available globally
window.InventoryManager = InventoryManager;
