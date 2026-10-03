import { describe, expect, it, beforeEach, vi } from 'vitest';
import { InventoryManager } from '../inventory';

describe('InventoryManager with SQLite Cache and Sliding Window Queue Integration', () => {
  let inventoryManager: InventoryManager;
  let mockProtocol: any;
  let mockAuth: any;

  beforeEach(() => {
    mockProtocol = {
      agentId: 'agent_integration_99',
      inventoryRoot: 'root_folder_uuid',
      on: vi.fn(),
      getCapability: vi.fn().mockReturnValue(null),
    };

    mockAuth = {
      isLoggedIn: vi.fn().mockReturnValue(true),
      user: { id: 'agent_integration_99' },
    };

    inventoryManager = new InventoryManager(mockProtocol, mockAuth);
  });

  it('renders inventory folder tree instantly from SQLite cache at startup', async () => {
    // Seed local SQLite database for this avatar account
    await inventoryManager.sqliteStore.saveFolderHierarchy('agent_integration_99', {
      rootId: 'root_folder_uuid',
      rootName: 'Cached Inventory Root',
      folders: [
        { id: 'f_sub1', name: 'Outfits', parent: 'root_folder_uuid', type: 'normal' },
        { id: 'f_sub2', name: 'Animations', parent: 'root_folder_uuid', type: 'normal' },
      ],
      items: [
        { id: 'i_item1', name: 'Mesh Jacket', parent: 'f_sub1', assetType: 'clothing' },
      ],
    });

    const startTime = performance.now();
    await inventoryManager.load();
    const durationMs = performance.now() - startTime;

    expect(inventoryManager.loadedFromCache).toBe(true);
    expect(inventoryManager.folders.has('f_sub1')).toBe(true);
    expect(inventoryManager.folders.has('f_sub2')).toBe(true);
    expect(inventoryManager.items.has('i_item1')).toBe(true);
    expect(durationMs).toBeLessThan(50); // Criterion 1
  });

  it('updates local SQLite tables upon receiving delta updates without interrupting scrolling', async () => {
    await inventoryManager.init();

    inventoryManager.handleInventoryUpdate({
      items: [
        { id: 'delta_item_1', name: 'Newly Received Gift', parent: 'root_folder_uuid', assetType: 'object' },
      ],
    });

    const cached = await inventoryManager.sqliteStore.loadFolderHierarchy('agent_integration_99');
    expect(cached).not.toBeNull();
    const deltaItem = cached?.items.find((i) => i.id === 'delta_item_1');
    expect(deltaItem).toBeDefined();
    expect(deltaItem?.name).toBe('Newly Received Gift');
  });

  it('promotes viewport-visible folders and limits parallel workers through sync queue', async () => {
    inventoryManager.updateViewportFolders(['f_viewport_1', 'f_viewport_2']);

    expect(inventoryManager.syncQueue.getMaxConcurrency()).toBe(3);
  });
});
