import { describe, it, expect, beforeEach } from 'vitest';
import { InventoryManager } from '../inventory';
import { InventoryCore } from '../phase2/inventory-core';

describe('InventoryManager Tree Pruning Filter API', () => {
  let inventory: InventoryManager;
  let mockProtocol: any;
  let mockAuth: any;

  beforeEach(() => {
    mockProtocol = {
      on: () => {},
      agentId: 'test-agent-123',
      inventoryRoot: 'root-id',
    };

    mockAuth = {
      isLoggedIn: () => true,
      user: { id: 'test-agent-123' },
    };

    inventory = new InventoryManager(mockProtocol as any, mockAuth as any);
    inventory.rootFolder = { id: 'root-id', name: 'My Inventory', type: 'folder', children: [] };
    inventory.folders.set('root-id', inventory.rootFolder);
  });

  describe('matchesCategory', () => {
    it('matches clothing items by numeric assetType and string name', () => {
      expect(inventory.matchesCategory({ assetType: 5 }, 'clothing')).toBe(true);
      expect(inventory.matchesCategory({ assetType: 'clothing' }, 'clothing')).toBe(true);
      expect(inventory.matchesCategory({ assetType: 0 }, 'clothing')).toBe(false);
    });

    it('matches textures, sounds, objects, body parts, and scripts', () => {
      expect(inventory.matchesCategory({ assetType: 0 }, 'texture')).toBe(true);
      expect(inventory.matchesCategory({ assetType: 1 }, 'sound')).toBe(true);
      expect(inventory.matchesCategory({ assetType: 6 }, 'object')).toBe(true);
      expect(inventory.matchesCategory({ assetType: 13 }, 'bodypart')).toBe(true);
      expect(inventory.matchesCategory({ assetType: 10 }, 'script')).toBe(true);
    });

    it('returns true for "all" or empty category filter', () => {
      expect(inventory.matchesCategory({ assetType: 5 }, 'all')).toBe(true);
      expect(inventory.matchesCategory({ assetType: 5 }, '')).toBe(true);
    });
  });

  describe('getFilteredFolderTree', () => {
    beforeEach(() => {
      // Setup test inventory hierarchy
      // Root (root-id)
      //  ├── folder-clothing (Clothing Folder)
      //  │     └── item-shirt (Blue Shirt, assetType: 5)
      //  ├── folder-textures (Textures Folder)
      //  │     └── item-grass (Grass Texture, assetType: 0)
      //  ├── folder-empty (Empty Folder - 0 items)
      //  └── folder-nested-parent (Outer Folder)
      //        └── folder-nested-child (Inner Empty Folder - 0 items)

      inventory.folders.set('folder-clothing', {
        id: 'folder-clothing',
        name: 'Clothing Folder',
        type: 'folder',
        parent: 'root-id',
        children: ['item-shirt'],
      });

      inventory.items.set('item-shirt', {
        id: 'item-shirt',
        name: 'Blue Denim Shirt',
        type: 'item',
        assetType: 5,
        parent: 'folder-clothing',
        description: 'Casual shirt',
      });

      inventory.folders.set('folder-textures', {
        id: 'folder-textures',
        name: 'Textures Folder',
        type: 'folder',
        parent: 'root-id',
        children: ['item-grass'],
      });

      inventory.items.set('item-grass', {
        id: 'item-grass',
        name: 'Seamless Grass Texture',
        type: 'item',
        assetType: 0,
        parent: 'folder-textures',
        description: 'Terrain texture',
      });

      inventory.folders.set('folder-empty', {
        id: 'folder-empty',
        name: 'Empty Folder',
        type: 'folder',
        parent: 'root-id',
        children: [],
      });

      inventory.folders.set('folder-nested-parent', {
        id: 'folder-nested-parent',
        name: 'Outer Folder',
        type: 'folder',
        parent: 'root-id',
        children: ['folder-nested-child'],
      });

      inventory.folders.set('folder-nested-child', {
        id: 'folder-nested-child',
        name: 'Inner Empty Folder',
        type: 'folder',
        parent: 'folder-nested-parent',
        children: [],
      });
    });

    it('prunes empty subfolders when filtering by clothing category', () => {
      const tree = inventory.getFilteredFolderTree({ category: 'clothing' });
      expect(tree).toBeDefined();
      expect(tree.id).toBe('root-id');

      const childrenIds = tree.children.map((c: any) => c.id);
      expect(childrenIds).toContain('folder-clothing');
      expect(childrenIds).not.toContain('folder-textures');
      expect(childrenIds).not.toContain('folder-empty');
      expect(childrenIds).not.toContain('folder-nested-parent');

      const clothingFolderNode = tree.children.find((c: any) => c.id === 'folder-clothing');
      expect(clothingFolderNode.children).toHaveLength(1);
      expect(clothingFolderNode.children[0].id).toBe('item-shirt');
    });

    it('prunes clothing and empty folders when filtering by texture category', () => {
      const tree = inventory.getFilteredFolderTree('texture');
      const childrenIds = tree.children.map((c: any) => c.id);

      expect(childrenIds).toContain('folder-textures');
      expect(childrenIds).not.toContain('folder-clothing');
      expect(childrenIds).not.toContain('folder-empty');

      const textureFolderNode = tree.children.find((c: any) => c.id === 'folder-textures');
      expect(textureFolderNode.children[0].id).toBe('item-grass');
    });

    it('retains nested subfolders when a deep descendant item matches', () => {
      // Add a matching item inside folder-nested-child
      inventory.items.set('item-nested-jeans', {
        id: 'item-nested-jeans',
        name: 'Nested Jeans',
        type: 'item',
        assetType: 5,
        parent: 'folder-nested-child',
      });
      inventory.folders.get('folder-nested-child').children.push('item-nested-jeans');

      const tree = inventory.getFilteredFolderTree({ category: 'clothing' });
      const childrenIds = tree.children.map((c: any) => c.id);

      expect(childrenIds).toContain('folder-clothing');
      expect(childrenIds).toContain('folder-nested-parent');

      const parentNode = tree.children.find((c: any) => c.id === 'folder-nested-parent');
      expect(parentNode.children).toHaveLength(1);

      const childNode = parentNode.children[0];
      expect(childNode.id).toBe('folder-nested-child');
      expect(childNode.children[0].id).toBe('item-nested-jeans');
    });

    it('filters items by search text and prunes subfolders without matching search text', () => {
      const tree = inventory.getFilteredFolderTree({ search: 'Denim' });
      const childrenIds = tree.children.map((c: any) => c.id);

      expect(childrenIds).toContain('folder-clothing');
      expect(childrenIds).not.toContain('folder-textures');
    });
  });

  describe('Atomic Reconciled Inventory Tree Mutation', () => {
    it('InventoryCore addItem and moveItem deduplicate parent child ID lists automatically', () => {
      const core = new InventoryCore();
      core.createFolder('f1', { name: 'Folder 1' });
      core.createFolder('f2', { name: 'Folder 2' });

      // Repeated add
      core.addItem('item1', { name: 'Item 1', folderId: 'f1' });
      core.addItem('item1', { name: 'Item 1 Updated', folderId: 'f1' });

      const f1Contents = core.listFolderContents('f1');
      expect(f1Contents.items).toHaveLength(1);
      expect(f1Contents.items[0].id).toBe('item1');

      // Move item
      core.moveItem('item1', 'f2');
      const f1AfterMove = core.listFolderContents('f1');
      const f2AfterMove = core.listFolderContents('f2');

      expect(f1AfterMove.items).toHaveLength(0);
      expect(f2AfterMove.items).toHaveLength(1);
      expect(f2AfterMove.items[0].id).toBe('item1');

      // Repeated move call
      core.moveItem('item1', 'f2');
      expect(core.listFolderContents('f2').items).toHaveLength(1);
    });

    it('re-fetching folder contents multiple times produces identical, deduplicated folder child lists', async () => {
      await inventory.reconcileFolder('folder-clothing', [], [
        { id: 'item-shirt', name: 'Shirt', parent: 'folder-clothing' },
        { id: 'item-pants', name: 'Pants', parent: 'folder-clothing' },
      ]);

      const folder = inventory.folders.get('folder-clothing');
      expect(folder.children).toEqual(['item-shirt', 'item-pants']);

      // Re-fetch 1
      await inventory.reconcileFolder('folder-clothing', [], [
        { id: 'item-shirt', name: 'Shirt', parent: 'folder-clothing' },
        { id: 'item-pants', name: 'Pants', parent: 'folder-clothing' },
      ]);
      expect(folder.children).toEqual(['item-shirt', 'item-pants']);

      // Re-fetch 2 with moved item away
      await inventory.reconcileFolder('folder-clothing', [], [
        { id: 'item-shirt', name: 'Shirt', parent: 'folder-clothing' },
      ]);
      expect(folder.children).toEqual(['item-shirt']);
      expect(inventory.items.has('item-pants')).toBe(false);
    });

    it('moveItemToFolder detaches item from source folder and appends once to target folder', () => {
      inventory.folders.set('f_src', { id: 'f_sub_src', name: 'Source', children: ['item_x'] });
      inventory.folders.set('f_target', { id: 'f_target', name: 'Target', children: [] });
      inventory.items.set('item_x', { id: 'item_x', name: 'Moving Item', parent: 'f_src' });

      const moved = inventory.moveItemToFolder('item_x', 'f_target');
      expect(moved).toBe(true);

      expect(inventory.folders.get('f_src').children).not.toContain('item_x');
      expect(inventory.folders.get('f_target').children).toEqual(['item_x']);

      // Duplicate move call
      inventory.moveItemToFolder('item_x', 'f_target');
      expect(inventory.folders.get('f_target').children).toEqual(['item_x']);
    });
  });
});
