import { describe, it, expect, vi, beforeEach } from 'vitest';
import { AisClient } from '../ais';
import { InventoryManager } from '../inventory';
import { corsHandler } from '../cors-handler';

vi.mock('../cors-handler', () => ({
  corsHandler: {
    makeRequest: vi.fn(),
  },
}));

describe('Agent Inventory Service (AIS v3) REST Integration', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('AisClient Capability Detection & REST Methods', () => {
    it('detects AgentInventoryService capability URL correctly', () => {
      const getCaps = () => ({
        AgentInventoryService: 'https://sim.example.com/cap/ais3_uuid',
      });
      const client = new AisClient(getCaps);

      expect(client.hasAisCapability()).toBe(true);
      expect(client.getAisCapabilityUrl()).toBe('https://sim.example.com/cap/ais3_uuid');
    });

    it('falls back to AgentInventoryService3 if present', () => {
      const getCaps = () => ({
        AgentInventoryService3: 'https://sim.example.com/cap/ais3_v2_uuid',
      });
      const client = new AisClient(getCaps);

      expect(client.hasAisCapability()).toBe(true);
      expect(client.getAisCapabilityUrl()).toBe('https://sim.example.com/cap/ais3_v2_uuid');
    });

    it('returns false for hasAisCapability when grid lacks AIS endpoints (OpenSim)', () => {
      const getCaps = () => ({
        EventQueueGet: 'https://sim.example.com/cap/eqg',
      });
      const client = new AisClient(getCaps);

      expect(client.hasAisCapability()).toBe(false);
      expect(client.getAisCapabilityUrl()).toBeNull();
    });

    it('sends AIS replace outfit REST POST request with LLSD payload', async () => {
      const getCaps = () => ({
        AgentInventoryService: 'https://sim.example.com/cap/ais3',
      });
      const client = new AisClient(getCaps);

      (corsHandler.makeRequest as any).mockResolvedValueOnce({
        ok: true,
        status: 200,
        text: async () => '<llsd><map><key>status</key><string>ok</string></map></llsd>',
      });

      const res = await client.replaceOutfit('cof-123', 'outfit-folder-456', ['item-1', 'item-2']);

      expect(res.success).toBe(true);
      expect(res.usedAis).toBe(true);
      expect(corsHandler.makeRequest).toHaveBeenCalledWith(
        'https://sim.example.com/cap/ais3/category/cof-123?op=replace',
        expect.objectContaining({
          method: 'POST',
          headers: expect.objectContaining({
            'Content-Type': 'application/llsd+xml',
          }),
        })
      );
    });

    it('sends AIS add to outfit REST POST request without removing existing items', async () => {
      const getCaps = () => ({
        AgentInventoryService: 'https://sim.example.com/cap/ais3',
      });
      const client = new AisClient(getCaps);

      (corsHandler.makeRequest as any).mockResolvedValueOnce({
        ok: true,
        status: 200,
        text: async () => '<llsd><map><key>status</key><string>ok</string></map></llsd>',
      });

      const res = await client.addToOutfit('cof-123', 'outfit-folder-789', ['item-3']);

      expect(res.success).toBe(true);
      expect(res.usedAis).toBe(true);
      expect(corsHandler.makeRequest).toHaveBeenCalledWith(
        'https://sim.example.com/cap/ais3/category/cof-123/array_links',
        expect.objectContaining({
          method: 'POST',
        })
      );
    });

    it('handles HTTP status retry logic and token refresh on session expiration (401)', async () => {
      let capUrl = 'https://sim.example.com/cap/old_ais3';
      const refreshCaps = vi.fn().mockImplementation(async () => {
        capUrl = 'https://sim.example.com/cap/new_ais3';
        return { AgentInventoryService: capUrl };
      });

      const client = new AisClient(() => ({ AgentInventoryService: capUrl }), refreshCaps);

      // First request fails with 401 Unauthorized
      (corsHandler.makeRequest as any)
        .mockResolvedValueOnce({
          ok: false,
          status: 401,
          text: async () => 'Unauthorized token',
        })
        .mockResolvedValueOnce({
          ok: true,
          status: 200,
          text: async () => '<llsd><map><key>status</key><string>ok</string></map></llsd>',
        });

      const res = await client.replaceOutfit('cof-123', 'outfit-folder-1', ['item-1']);

      expect(refreshCaps).toHaveBeenCalled();
      expect(res.success).toBe(true);
      expect(corsHandler.makeRequest).toHaveBeenCalledTimes(2);
    });
  });

  describe('InventoryManager Outfit Operations & OpenSim Fallback', () => {
    let mockProtocol: any;
    let mockAuth: any;
    let inventory: InventoryManager;

    beforeEach(() => {
      mockProtocol = {
        capabilities: {
          AgentInventoryService: 'https://sim.example.com/cap/ais3',
        },
        inventoryRoot: 'root-id',
        on: vi.fn(),
        emit: vi.fn(),
      };
      mockAuth = {
        isLoggedIn: () => true,
        user: { id: 'agent-123' },
      };

      inventory = new InventoryManager(mockProtocol, mockAuth);

      // Set up test folders and items
      inventory.folders.set('cof-id', {
        id: 'cof-id',
        name: 'Current Outfit',
        type: 24,
        folderType: 24,
        children: ['old-item-1'],
      });

      inventory.folders.set('outfit-folder-1', {
        id: 'outfit-folder-1',
        name: 'Casual Outfit',
        type: 'folder',
        children: ['item-101', 'item-102'],
      });

      inventory.items.set('old-item-1', { id: 'old-item-1', name: 'Old Shoes', parent: 'cof-id' });
      inventory.items.set('item-101', { id: 'item-101', name: 'Blue Jeans', parent: 'outfit-folder-1' });
      inventory.items.set('item-102', { id: 'item-102', name: 'Red Shirt', parent: 'outfit-folder-1' });
    });

    it('executes server-side replace outfit via AIS REST when capability is present', async () => {
      (corsHandler.makeRequest as any).mockResolvedValueOnce({
        ok: true,
        status: 200,
        text: async () => '<llsd><map><key>status</key><string>ok</string></map></llsd>',
      });

      const outfitChangedListener = vi.fn();
      inventory.on('outfit_changed', outfitChangedListener);

      const result = await inventory.replaceOutfit('outfit-folder-1');

      expect(result.success).toBe(true);
      expect(result.method).toBe('ais_v3');
      expect(outfitChangedListener).toHaveBeenCalledWith(
        expect.objectContaining({
          mode: 'replace',
          method: 'ais_v3',
          itemIds: ['item-101', 'item-102'],
        })
      );

      // Verify COF folder contents updated in local state
      const cofFolder = inventory.folders.get('cof-id');
      expect(cofFolder.children).toEqual(['item-101', 'item-102']);
    });

    it('executes server-side add to outfit via AIS REST appending items to COF', async () => {
      (corsHandler.makeRequest as any).mockResolvedValueOnce({
        ok: true,
        status: 200,
        text: async () => '<llsd><map><key>status</key><string>ok</string></map></llsd>',
      });

      const outfitChangedListener = vi.fn();
      inventory.on('outfit_changed', outfitChangedListener);

      const result = await inventory.addToOutfit('outfit-folder-1');

      expect(result.success).toBe(true);
      expect(result.method).toBe('ais_v3');

      // Verify COF folder kept old-item-1 and appended new items
      const cofFolder = inventory.folders.get('cof-id');
      expect(cofFolder.children).toContain('old-item-1');
      expect(cofFolder.children).toContain('item-101');
      expect(cofFolder.children).toContain('item-102');
    });

    it('falls back to batch packet dispatch transparently on OpenSim grids lacking AIS v3', async () => {
      // OpenSim grid without AIS capability
      mockProtocol.capabilities = {
        EventQueueGet: 'https://opensim.example.com/cap/eqg',
      };

      const fallbackListener = vi.fn();
      const outfitChangedListener = vi.fn();
      inventory.on('outfit_fallback_used', fallbackListener);
      inventory.on('outfit_changed', outfitChangedListener);

      const result = await inventory.replaceOutfit('outfit-folder-1');

      expect(result.success).toBe(true);
      expect(result.method).toBe('packet_fallback');
      expect(fallbackListener).toHaveBeenCalledWith(
        expect.objectContaining({
          mode: 'replace',
          outfitFolderId: 'outfit-folder-1',
          itemIds: ['item-101', 'item-102'],
        })
      );
      expect(outfitChangedListener).toHaveBeenCalledWith(
        expect.objectContaining({
          mode: 'replace',
          method: 'packet_fallback',
        })
      );

      // Local COF folder state still reflects the outfit replacement
      const cofFolder = inventory.folders.get('cof-id');
      expect(cofFolder.children).toEqual(['item-101', 'item-102']);
    });
  });
});
