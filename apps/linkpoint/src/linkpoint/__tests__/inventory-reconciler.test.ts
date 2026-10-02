import { describe, it, expect, vi, beforeEach } from 'vitest';
import { InventoryManager } from '../inventory';
import { localCache } from '../local-cache';
import { slBridge } from '../sl-bridge';
import { corsHandler } from '../cors-handler';

vi.mock('../cors-handler', () => ({
  corsHandler: {
    makeRequest: vi.fn(),
  },
}));

describe('InventoryManager Folder Reconciler and LocalCache Persistence', () => {
  let inventory: InventoryManager;
  let mockProtocol: any;
  let mockAuth: any;

  beforeEach(async () => {
    vi.clearAllMocks();
    await localCache.clearCache('test-agent-123');

    mockProtocol = {
      on: vi.fn(),
      agentId: 'test-agent-123',
      inventoryRoot: 'root-folder-id',
      getCapability: vi.fn(),
    };

    mockAuth = {
      isLoggedIn: () => true,
      user: { id: 'test-agent-123' },
    };

    inventory = new InventoryManager(mockProtocol as any, mockAuth as any);
  });

  it('reconciles folder contents by adding new items and removing stale items', async () => {
    const folderId = 'folder-1';
    inventory.folders.set(folderId, {
      id: folderId,
      name: 'Test Folder',
      type: 'folder',
      parent: 'root-folder-id',
      children: ['item-stale-1', 'item-keep-1'],
    });

    inventory.items.set('item-stale-1', {
      id: 'item-stale-1',
      name: 'Old Item',
      type: 'item',
      parent: folderId,
    });

    inventory.items.set('item-keep-1', {
      id: 'item-keep-1',
      name: 'Existing Item',
      type: 'item',
      parent: folderId,
    });

    const incomingFolders: any[] = [];
    const incomingItems = [
      { id: 'item-keep-1', name: 'Existing Item Updated', parent: folderId },
      { id: 'item-new-2', name: 'Brand New Item', parent: folderId },
    ];

    await inventory.reconcileFolder(folderId, incomingFolders, incomingItems);

    const folder = inventory.folders.get(folderId);
    expect(folder.children).toEqual(['item-keep-1', 'item-new-2']);

    expect(inventory.items.has('item-stale-1')).toBe(false);
    expect(inventory.items.has('item-keep-1')).toBe(true);
    expect(inventory.items.has('item-new-2')).toBe(true);

    expect(inventory.items.get('item-keep-1').name).toBe('Existing Item Updated');
  });

  it('recursively purges orphaned child subfolders and their nested items', async () => {
    const parentFolderId = 'parent-folder';
    const subFolderId = 'sub-folder-stale';
    const nestedItemId = 'nested-item-stale';

    inventory.folders.set(parentFolderId, {
      id: parentFolderId,
      name: 'Parent Folder',
      type: 'folder',
      children: [subFolderId],
    });

    inventory.folders.set(subFolderId, {
      id: subFolderId,
      name: 'Stale SubFolder',
      type: 'folder',
      parent: parentFolderId,
      children: [nestedItemId],
    });

    inventory.items.set(nestedItemId, {
      id: nestedItemId,
      name: 'Nested Stale Item',
      type: 'item',
      parent: subFolderId,
    });

    // Reconcile parent folder with NO subfolders or items
    await inventory.reconcileFolder(parentFolderId, [], []);

    expect(inventory.folders.get(parentFolderId).children).toEqual([]);
    expect(inventory.folders.has(subFolderId)).toBe(false);
    expect(inventory.items.has(nestedItemId)).toBe(false);
  });

  it('syncs reconciled folder state immediately to localCache', async () => {
    const saveSpy = vi.spyOn(localCache, 'saveInventory');

    const folderId = 'folder-cache-test';
    inventory.folders.set(folderId, {
      id: folderId,
      name: 'Cache Sync Folder',
      type: 'folder',
      children: ['item-obsolete'],
    });
    inventory.items.set('item-obsolete', {
      id: 'item-obsolete',
      name: 'Obsolete Item',
      type: 'item',
      parent: folderId,
    });

    await inventory.reconcileFolder(folderId, [], [
      { id: 'item-fresh', name: 'Fresh Item', parent: folderId },
    ]);

    expect(saveSpy).toHaveBeenCalledWith('test-agent-123', expect.objectContaining({
      items: expect.arrayContaining([
        expect.objectContaining({ id: 'item-fresh' }),
      ]),
    }));

    const cached = await localCache.loadInventory('test-agent-123');
    expect(cached).not.toBeNull();
    const cachedItemIds = cached!.items.map((i: any) => i.id);
    expect(cachedItemIds).toContain('item-fresh');
    expect(cachedItemIds).not.toContain('item-obsolete');
  });

  it('preserves unvisited root folders when updating localCache with partial folder updates', async () => {
    await localCache.saveInventory('test-agent-123', {
      rootId: 'root-folder-id',
      rootName: 'My Inventory',
      folders: [
        { id: 'root-folder-id', name: 'My Inventory', type: 'folder', parent: 'root' },
        { id: 'unvisited-folder-A', name: 'Unvisited A', type: 'folder', parent: 'root-folder-id' },
        { id: 'visited-folder-B', name: 'Visited B', type: 'folder', parent: 'root-folder-id' },
      ],
      items: [
        { id: 'unvisited-item-1', name: 'Unvisited Item', parent: 'unvisited-folder-A' },
        { id: 'visited-item-old', name: 'Old Visited Item', parent: 'visited-folder-B' },
      ],
    });

    inventory.folders.set('visited-folder-B', {
      id: 'visited-folder-B',
      name: 'Visited B',
      type: 'folder',
      parent: 'root-folder-id',
      children: ['visited-item-old'],
    });
    inventory.items.set('visited-item-old', {
      id: 'visited-item-old',
      name: 'Old Visited Item',
      type: 'item',
      parent: 'visited-folder-B',
    });

    await inventory.reconcileFolder('visited-folder-B', [], [
      { id: 'visited-item-new', name: 'New Visited Item', parent: 'visited-folder-B' },
    ]);

    const cached = await localCache.loadInventory('test-agent-123');
    expect(cached).not.toBeNull();

    const folderIds = cached!.folders.map((f: any) => f.id);
    const itemIds = cached!.items.map((i: any) => i.id);

    expect(folderIds).toContain('unvisited-folder-A');
    expect(folderIds).toContain('visited-folder-B');

    expect(itemIds).toContain('unvisited-item-1');
    expect(itemIds).toContain('visited-item-new');
    expect(itemIds).not.toContain('visited-item-old');
  });

  it('integrates reconciler with handleInventoryResponse', async () => {
    const parentId = 'folder-llsd-1';
    inventory.folders.set(parentId, {
      id: parentId,
      name: 'LLSD Parent',
      type: 'folder',
      children: ['stale-llsd-item'],
    });
    inventory.items.set('stale-llsd-item', {
      id: 'stale-llsd-item',
      name: 'Stale LLSD Item',
      parent: parentId,
    });

    const llsdResponse = {
      folders: [
        {
          folder_id: parentId,
          categories: [
            { category_id: 'sub-cat-1', name: 'Sub Category', parent_id: parentId },
          ],
          items: [
            { item_id: 'fresh-llsd-item', name: 'Fresh LLSD Item', parent_id: parentId, asset_type: 1 },
          ],
        },
      ],
    };

    await inventory.handleInventoryResponse(llsdResponse);

    expect(inventory.items.has('stale-llsd-item')).toBe(false);
    expect(inventory.items.has('fresh-llsd-item')).toBe(true);
    expect(inventory.folders.has('sub-cat-1')).toBe(true);
    expect(inventory.folders.get(parentId).children).toEqual(['sub-cat-1', 'fresh-llsd-item']);
  });

  it('integrates reconciler with fetchFolderContents when slBridge is connected', async () => {
    const targetFolderId = 'bridge-folder-1';
    inventory.folders.set(targetFolderId, {
      id: targetFolderId,
      name: 'Bridge Folder',
      type: 'folder',
      children: ['bridge-stale-item'],
    });
    inventory.items.set('bridge-stale-item', {
      id: 'bridge-stale-item',
      name: 'Bridge Stale Item',
      parent: targetFolderId,
    });

    vi.spyOn(slBridge, 'connected', 'get').mockReturnValue(true);
    vi.spyOn(slBridge, 'fetchInventory').mockResolvedValueOnce({
      folders: [],
      items: [
        { id: 'bridge-fresh-item', name: 'Bridge Fresh Item', parent: targetFolderId, assetType: 0 },
      ],
    } as any);

    await inventory.fetchFolderContents(targetFolderId);

    expect(inventory.items.has('bridge-stale-item')).toBe(false);
    expect(inventory.items.has('bridge-fresh-item')).toBe(true);
    expect(inventory.folders.get(targetFolderId).children).toEqual(['bridge-fresh-item']);
  });
});
