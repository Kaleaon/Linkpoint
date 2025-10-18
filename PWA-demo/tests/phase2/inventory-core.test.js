/**
 * Unit tests for Inventory Core module
 * Tests inventory folder and item management
 */

// Import the module
const { InventoryCore } = require('../../js/phase2/inventory-core.js');

describe('InventoryCore', () => {
  let inventory;

  beforeEach(() => {
    inventory = new InventoryCore();
  });

  describe('Constructor', () => {
    test('should initialize with empty collections', () => {
      expect(inventory.folders.size).toBe(0);
      expect(inventory.items.size).toBe(0);
      expect(inventory.rootFolderId).toBeNull();
    });
  });

  describe('createFolder', () => {
    test('should throw error if folder ID is invalid', () => {
      expect(() => inventory.createFolder()).toThrow('Valid folder ID required');
      expect(() => inventory.createFolder(123, {})).toThrow('Valid folder ID required');
    });

    test('should throw error if folder data is invalid', () => {
      expect(() => inventory.createFolder('folder-1')).toThrow('Valid folder data required');
      expect(() => inventory.createFolder('folder-1', 'invalid')).toThrow('Valid folder data required');
    });

    test('should create folder with default values', () => {
      const folderId = 'folder-123';
      const folder = inventory.createFolder(folderId, {});
      
      expect(folder.id).toBe(folderId);
      expect(folder.name).toBe('New Folder');
      expect(folder.type).toBe('normal');
      expect(folder.children).toEqual([]);
      expect(folder.items).toEqual([]);
      expect(folder.version).toBe(1);
      expect(folder.created).toBeDefined();
    });

    test('should create folder with custom values', () => {
      const folderId = 'folder-123';
      const folderData = {
        name: 'My Objects',
        type: 'objects',
        version: 5
      };
      
      const folder = inventory.createFolder(folderId, folderData);
      
      expect(folder.name).toBe('My Objects');
      expect(folder.type).toBe('objects');
      expect(folder.version).toBe(5);
    });

    test('should add folder to parent children', () => {
      const parentId = 'parent-123';
      const childId = 'child-456';
      
      inventory.createFolder(parentId, { name: 'Parent' });
      inventory.createFolder(childId, { name: 'Child', parentId });
      
      const parent = inventory.getFolder(parentId);
      expect(parent.children).toContain(childId);
    });

    test('should use root folder as default parent', () => {
      inventory.setRootFolder('root-123');
      inventory.createFolder('root-123', { name: 'Root' });
      
      const folder = inventory.createFolder('child-456', { name: 'Child' });
      
      expect(folder.parentId).toBe('root-123');
    });
  });

  describe('getFolder', () => {
    test('should return null for non-existent folder', () => {
      expect(inventory.getFolder('non-existent')).toBeNull();
    });

    test('should return folder if exists', () => {
      const folderId = 'folder-123';
      inventory.createFolder(folderId, { name: 'Test Folder' });
      
      const folder = inventory.getFolder(folderId);
      
      expect(folder).toBeDefined();
      expect(folder.id).toBe(folderId);
      expect(folder.name).toBe('Test Folder');
    });
  });

  describe('listFolderContents', () => {
    test('should return empty lists for non-existent folder', () => {
      const contents = inventory.listFolderContents('non-existent');
      
      expect(contents.folders).toEqual([]);
      expect(contents.items).toEqual([]);
    });

    test('should return folder contents', () => {
      const parentId = 'parent-123';
      const child1Id = 'child-1';
      const child2Id = 'child-2';
      const item1Id = 'item-1';
      
      inventory.createFolder(parentId, { name: 'Parent' });
      inventory.createFolder(child1Id, { name: 'Child 1', parentId });
      inventory.createFolder(child2Id, { name: 'Child 2', parentId });
      inventory.addItem(item1Id, { name: 'Item 1', folderId: parentId });
      
      const contents = inventory.listFolderContents(parentId);
      
      expect(contents.folders.length).toBe(2);
      expect(contents.items.length).toBe(1);
      expect(contents.folders[0].name).toBeTruthy();
      expect(contents.items[0].name).toBe('Item 1');
    });
  });

  describe('addItem', () => {
    test('should throw error if item ID is invalid', () => {
      expect(() => inventory.addItem()).toThrow('Valid item ID required');
      expect(() => inventory.addItem(123, {})).toThrow('Valid item ID required');
    });

    test('should throw error if item data is invalid', () => {
      expect(() => inventory.addItem('item-1')).toThrow('Valid item data required');
      expect(() => inventory.addItem('item-1', 'invalid')).toThrow('Valid item data required');
    });

    test('should add item with default values', () => {
      const itemId = 'item-123';
      const item = inventory.addItem(itemId, {});
      
      expect(item.id).toBe(itemId);
      expect(item.name).toBe('New Item');
      expect(item.assetType).toBe('unknown');
      expect(item.inventoryType).toBe('object');
      expect(item.description).toBe('');
      expect(item.permissions).toEqual({});
      expect(item.created).toBeDefined();
    });

    test('should add item with custom values', () => {
      const itemId = 'item-123';
      const itemData = {
        name: 'My Object',
        assetType: 'object',
        inventoryType: 'object',
        description: 'A test object'
      };
      
      const item = inventory.addItem(itemId, itemData);
      
      expect(item.name).toBe('My Object');
      expect(item.assetType).toBe('object');
      expect(item.description).toBe('A test object');
    });

    test('should add item to folder', () => {
      const folderId = 'folder-123';
      const itemId = 'item-456';
      
      inventory.createFolder(folderId, { name: 'Folder' });
      inventory.addItem(itemId, { name: 'Item', folderId });
      
      const folder = inventory.getFolder(folderId);
      expect(folder.items).toContain(itemId);
    });
  });

  describe('getItem', () => {
    test('should return null for non-existent item', () => {
      expect(inventory.getItem('non-existent')).toBeNull();
    });

    test('should return item if exists', () => {
      const itemId = 'item-123';
      inventory.addItem(itemId, { name: 'Test Item' });
      
      const item = inventory.getItem(itemId);
      
      expect(item).toBeDefined();
      expect(item.id).toBe(itemId);
      expect(item.name).toBe('Test Item');
    });
  });

  describe('sortFolder', () => {
    beforeEach(() => {
      const folderId = 'folder-123';
      inventory.createFolder(folderId, { name: 'Folder' });
      
      // Add items with different properties
      inventory.addItem('item-1', { name: 'Zebra', folderId, assetType: 'texture' });
      inventory.addItem('item-2', { name: 'Apple', folderId, assetType: 'object' });
      inventory.addItem('item-3', { name: 'Mango', folderId, assetType: 'animation' });
    });

    test('should sort by name', () => {
      const contents = inventory.sortFolder('folder-123', 'name');
      
      expect(contents.items[0].name).toBe('Apple');
      expect(contents.items[1].name).toBe('Mango');
      expect(contents.items[2].name).toBe('Zebra');
    });

    test('should sort by date', () => {
      const contents = inventory.sortFolder('folder-123', 'date');
      
      expect(contents.items[0].created).toBeLessThanOrEqual(contents.items[1].created);
      expect(contents.items[1].created).toBeLessThanOrEqual(contents.items[2].created);
    });

    test('should sort by type', () => {
      const contents = inventory.sortFolder('folder-123', 'type');
      
      const types = contents.items.map(i => i.assetType).join(',');
      expect(types).toBe('animation,object,texture');
    });
  });

  describe('moveItem', () => {
    test('should throw error if item ID or target folder ID is invalid', () => {
      expect(() => inventory.moveItem()).toThrow('Valid item ID and target folder ID required');
      expect(() => inventory.moveItem('item-1')).toThrow('Valid item ID and target folder ID required');
    });

    test('should throw error if item not found', () => {
      inventory.createFolder('folder-1', { name: 'Folder' });
      
      expect(() => inventory.moveItem('non-existent', 'folder-1')).toThrow('Item not found');
    });

    test('should throw error if target folder not found', () => {
      inventory.addItem('item-1', { name: 'Item' });
      
      expect(() => inventory.moveItem('item-1', 'non-existent')).toThrow('Target folder not found');
    });

    test('should move item to new folder', () => {
      const folder1Id = 'folder-1';
      const folder2Id = 'folder-2';
      const itemId = 'item-1';
      
      inventory.createFolder(folder1Id, { name: 'Folder 1' });
      inventory.createFolder(folder2Id, { name: 'Folder 2' });
      inventory.addItem(itemId, { name: 'Item', folderId: folder1Id });
      
      inventory.moveItem(itemId, folder2Id);
      
      const folder1 = inventory.getFolder(folder1Id);
      const folder2 = inventory.getFolder(folder2Id);
      const item = inventory.getItem(itemId);
      
      expect(folder1.items).not.toContain(itemId);
      expect(folder2.items).toContain(itemId);
      expect(item.folderId).toBe(folder2Id);
    });
  });

  describe('setRootFolder', () => {
    test('should set root folder ID', () => {
      const rootId = 'root-123';
      inventory.setRootFolder(rootId);
      
      expect(inventory.rootFolderId).toBe(rootId);
    });
  });

  describe('getStats', () => {
    test('should return correct statistics', () => {
      inventory.setRootFolder('root-123');
      inventory.createFolder('folder-1', { name: 'Folder 1' });
      inventory.createFolder('folder-2', { name: 'Folder 2' });
      inventory.addItem('item-1', { name: 'Item 1' });
      inventory.addItem('item-2', { name: 'Item 2' });
      inventory.addItem('item-3', { name: 'Item 3' });
      
      const stats = inventory.getStats();
      
      expect(stats.totalFolders).toBe(2);
      expect(stats.totalItems).toBe(3);
      expect(stats.rootFolder).toBe('root-123');
    });
  });
});
