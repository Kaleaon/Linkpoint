import { describe, it, expect, beforeEach } from 'vitest';
import { InventoryManager } from '../inventory';

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

  describe('flattenTree', () => {
    it('flattens a pruned tree hierarchy into a list of nodes', () => {
      const treeNode = {
        id: 'root-id',
        name: 'My Inventory',
        children: [
          {
            id: 'folder-clothing',
            name: 'Clothing Folder',
            folder: true,
            children: [
              { id: 'item-shirt', name: 'Shirt', folder: false }
            ]
          }
        ]
      };

      const flatList = inventory.flattenTree(treeNode);
      expect(flatList).toHaveLength(3);
      expect(flatList.map(n => n.id)).toEqual(['root-id', 'folder-clothing', 'item-shirt']);
    });
  });
});
