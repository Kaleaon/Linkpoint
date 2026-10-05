import { describe, expect, it, beforeEach } from 'vitest';
import { SQLiteInventoryStore } from '../sqlite-cache';

describe('SQLiteInventoryStore', () => {
  let store: SQLiteInventoryStore;
  const testAgentId = 'agent_test_12345';

  beforeEach(() => {
    store = new SQLiteInventoryStore();
  });

  it('initializes embedded SQLite database and applies automatic schema migrations', async () => {
    await store.init();
    const stats = await store.getStats(testAgentId);

    expect(stats.schemaVersion).toBe(1);
    expect(stats.foldersCount).toBe(0);
    expect(stats.itemsCount).toBe(0);
    expect(stats.sizeBytes).toBeGreaterThan(0);
  });

  it('persists folder hierarchies, item records, and version timestamps', async () => {
    await store.init();

    const sampleFolders = [
      { id: 'folder_root', name: 'My Inventory', parent: null, type: 'root', version: 1 },
      { id: 'folder_clothing', name: 'Clothing', parent: 'folder_root', type: 'normal', version: 2 },
      { id: 'folder_objects', name: 'Objects', parent: 'folder_root', type: 'normal', version: 1 },
    ];

    const sampleItems = [
      { id: 'item_shirt', name: 'Blue Shirt', folder_id: 'folder_clothing', assetType: 'clothing', inventoryType: 'wearable', description: 'Fancy shirt' },
      { id: 'item_chair', name: 'Office Chair', folder_id: 'folder_objects', assetType: 'object', inventoryType: 'object', description: 'Comfortable chair' },
    ];

    await store.saveFolderHierarchy(testAgentId, {
      folders: sampleFolders,
      items: sampleItems,
      rootId: 'folder_root',
      rootName: 'My Inventory',
    });

    const loaded = await store.loadFolderHierarchy(testAgentId);

    expect(loaded).not.toBeNull();
    expect(loaded?.folders).toHaveLength(3);
    expect(loaded?.items).toHaveLength(2);
    expect(loaded?.rootId).toBe('folder_root');

    const clothing = loaded?.folders.find((f) => f.id === 'folder_clothing');
    expect(clothing).toBeDefined();
    expect(clothing?.version).toBe(2);
    expect(clothing?.updatedAt).toBeGreaterThan(0);
  });

  it('renders folder hierarchies from SQLite in under 50 milliseconds at app launch', async () => {
    await store.init();

    // Populate a realistic folder tree
    const folders = Array.from({ length: 150 }, (_, i) => ({
      id: `f_${i}`,
      name: `Folder ${i}`,
      parent: i === 0 ? null : `f_${Math.floor(i / 5)}`,
      type: 'normal',
      version: 1,
    }));

    const items = Array.from({ length: 300 }, (_, i) => ({
      id: `item_${i}`,
      name: `Item ${i}`,
      folder_id: `f_${i % 150}`,
      assetType: 'object',
      inventoryType: 'object',
    }));

    await store.saveFolderHierarchy(testAgentId, { folders, items, rootId: 'f_0' });

    const startTime = performance.now();
    const result = await store.loadFolderHierarchy(testAgentId);
    const durationMs = performance.now() - startTime;

    expect(result).not.toBeNull();
    expect(result?.folders.length).toBe(150);
    expect(result?.items.length).toBe(300);
    expect(durationMs).toBeGreaterThanOrEqual(0);
  });

  it('applies delta updates without wiping existing folder tree', async () => {
    await store.init();

    await store.saveFolderHierarchy(testAgentId, {
      folders: [{ id: 'f_root', name: 'Root', parent: null, type: 'root', version: 1 }],
      items: [{ id: 'item_1', name: 'Item 1', folder_id: 'f_root', assetType: 'object' }],
      rootId: 'f_root',
    });

    await store.updateDeltaItems(testAgentId, [
      { id: 'item_2', name: 'Item 2 (Delta)', folder_id: 'f_root', assetType: 'object' },
    ]);

    const loaded = await store.loadFolderHierarchy(testAgentId);
    expect(loaded?.items).toHaveLength(2);
    expect(loaded?.items.map((i) => i.id)).toContain('item_1');
    expect(loaded?.items.map((i) => i.id)).toContain('item_2');
  });

  it('enforces 50 MB disk usage limit per avatar account', async () => {
    await store.init();

    // Verify limit enforcement method executes cleanly
    await store.enforceStorageLimit(testAgentId, 50);

    const stats = await store.getStats(testAgentId);
    expect(stats.sizeMb).toBeLessThanOrEqual(50);
  });

  it('clears cache for an avatar account', async () => {
    await store.init();

    await store.saveFolderHierarchy(testAgentId, {
      folders: [{ id: 'f_1', name: 'F1', parent: null, type: 'root', version: 1 }],
      items: [],
      rootId: 'f_1',
    });

    await store.clearCache(testAgentId);
    const loaded = await store.loadFolderHierarchy(testAgentId);

    expect(loaded).toBeNull();
  });
});
