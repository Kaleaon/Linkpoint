import { describe, it, expect, vi, beforeEach } from 'vitest';
import {
  scaleToPowerOfTwo,
  closestPowerOfTwo,
  prepareTextureCanvas,
  encodeJpeg2000,
} from '../jpeg2000-encoder';
import { uploadTexture } from '../texture-uploader';
import { app } from '../app';
import { economyManager } from '../economy-manager';
import { corsHandler } from '../cors-handler';

vi.mock('../cors-handler', () => ({
  corsHandler: {
    makeRequest: vi.fn(),
  },
}));

describe('JPEG2000 Texture Upload Pipeline', () => {
  beforeEach(() => {
    vi.clearAllMocks();

    // Reset app protocol & capabilities
    app.protocol.capabilities = {
      NewFileAgentInventory: 'https://sim.example.com/cap/new_file_agent_inventory',
    };
    app.protocol.inventoryRoot = 'folder-root-uuid';

    // Reset inventory folders & items
    app.inventory.folders.clear();
    app.inventory.items.clear();
    app.inventory.rootFolder = { id: 'folder-root-uuid', name: 'My Inventory', children: [] };
    app.inventory.folders.set('folder-root-uuid', app.inventory.rootFolder);
    app.inventory.folders.set('folder-textures-uuid', {
      id: 'folder-textures-uuid',
      name: 'Textures',
      folderType: 0,
      children: [],
    });

    // Reset economy manager
    economyManager.activeAgentId = 'test-agent-123';
    economyManager.balance = 500;
    economyManager.transactions = [];
  });

  describe('1. Power-of-Two Image Scaling', () => {
    it('snaps arbitrary image dimensions to power-of-two values between 32 and 1024', () => {
      expect(closestPowerOfTwo(1080)).toBe(1024);
      expect(closestPowerOfTwo(1920)).toBe(1024);
      expect(closestPowerOfTwo(300)).toBe(256);
      expect(closestPowerOfTwo(700)).toBe(512);
      expect(closestPowerOfTwo(15)).toBe(32);
      expect(closestPowerOfTwo(40)).toBe(32);
      expect(closestPowerOfTwo(5000)).toBe(1024);

      const scaled = scaleToPowerOfTwo(1080, 1920);
      expect(scaled.width).toBe(1024);
      expect(scaled.height).toBe(1024);
    });

    it('prepares canvas and extracts RGBA pixel array with explicit layout and color space metadata', async () => {
      const blob = new Blob(['fake image bytes'], { type: 'image/png' });
      const result = await prepareTextureCanvas(blob);

      expect(result.width).toBeGreaterThanOrEqual(32);
      expect(result.width).toBeLessThanOrEqual(1024);
      expect(result.height).toBeGreaterThanOrEqual(32);
      expect(result.height).toBeLessThanOrEqual(1024);
      expect(result.rgba).toBeInstanceOf(Uint8Array);
      expect(result.rgba.length).toBe(result.width * result.height * 4);
      expect(result.channelLayout).toBe('RGBA');
      expect(result.colorSpace).toBe('sRGB');

      const linearResult = await prepareTextureCanvas(blob, { channelLayout: 'BGRA', colorSpace: 'linear' });
      expect(linearResult.channelLayout).toBe('BGRA');
      expect(linearResult.colorSpace).toBe('linear');
    });
  });

  describe('2. JPEG2000 Codestream Encoding', () => {
    it('encodes RGBA buffer into a valid J2K codestream starting with SOC and SIZ markers', async () => {
      const width = 256;
      const height = 256;
      const rgba = new Uint8Array(width * height * 4);

      const j2k = await encodeJpeg2000(rgba, width, height, false);

      expect(j2k).toBeInstanceOf(Uint8Array);
      expect(j2k.length).toBeGreaterThan(50);

      // Verify SOC marker (0xFF, 0x4F)
      expect(j2k[0]).toBe(0xFF);
      expect(j2k[1]).toBe(0x4F);

      // Verify SIZ marker (0xFF, 0x51)
      expect(j2k[2]).toBe(0xFF);
      expect(j2k[3]).toBe(0x51);

      // Verify Xsiz (width = 256 => 0x00, 0x00, 0x01, 0x00 at bytes 8-11)
      const xsiz = (j2k[8] << 24) | (j2k[9] << 16) | (j2k[10] << 8) | j2k[11];
      const ysiz = (j2k[12] << 24) | (j2k[13] << 16) | (j2k[14] << 8) | j2k[15];
      expect(xsiz).toBe(256);
      expect(ysiz).toBe(256);

      // Verify EOC marker at the end (0xFF, 0xD9)
      expect(j2k[j2k.length - 2]).toBe(0xFF);
      expect(j2k[j2k.length - 1]).toBe(0xD9);
    });
  });

  describe('3. Account Balance Guard & Fee Handling', () => {
    it('blocks upload request when account balance is below L$10', async () => {
      economyManager.balance = 5;

      const blob = new Blob(['test image'], { type: 'image/png' });

      await expect(
        uploadTexture({ file: blob, name: 'Test Texture' })
      ).rejects.toThrow(/Insufficient funds: L\$10 upload fee required/);

      expect(corsHandler.makeRequest).not.toHaveBeenCalled();
    });
  });

  describe('4. Primary Texture Upload Workflow', () => {
    it('executes end-to-end texture upload, registers item in InventoryManager, and deducts L$10 fee', async () => {
      economyManager.balance = 25;

      // Mock NewFileAgentInventory LLSD response
      const mockCapResponseText = `<?xml version="1.0" encoding="UTF-8"?>
<llsd>
  <map>
    <key>uploader</key><string>https://sim.example.com/upload/texture_token_123</string>
    <key>state</key><string>upload</string>
    <key>new_asset</key><uuid>asset-uuid-1111</uuid>
    <key>new_inventory_item</key><uuid>item-uuid-2222</uuid>
  </map>
</llsd>`;

      // Mock uploader POST response
      const mockUploadResponseText = `<?xml version="1.0" encoding="UTF-8"?>
<llsd>
  <map>
    <key>state</key><string>complete</string>
    <key>new_asset</key><uuid>asset-uuid-1111</uuid>
    <key>new_inventory_item</key><uuid>item-uuid-2222</uuid>
  </map>
</llsd>`;

      (corsHandler.makeRequest as any)
        .mockResolvedValueOnce({
          ok: true,
          status: 200,
          text: async () => mockCapResponseText,
        })
        .mockResolvedValueOnce({
          ok: true,
          status: 200,
          text: async () => mockUploadResponseText,
        });

      const progressEvents: any[] = [];
      const blob = new Blob(['test image payload'], { type: 'image/png' });

      const result = await uploadTexture(
        {
          file: blob,
          name: 'Sunset Beach Texture',
          description: 'Custom mobile upload',
          folderId: 'folder-textures-uuid',
        },
        (prog) => progressEvents.push(prog)
      );

      // 1. Verify progress tracking stages
      expect(progressEvents.length).toBeGreaterThanOrEqual(4);
      expect(progressEvents[progressEvents.length - 1].stage).toBe('completing');
      expect(progressEvents[progressEvents.length - 1].percent).toBe(100);

      // 2. Verify result output
      expect(result.success).toBe(true);
      expect(result.item.name).toBe('Sunset Beach Texture');
      expect(result.item.parent).toBe('folder-textures-uuid');

      // 3. Verify item inserted into InventoryManager
      const insertedItem = app.inventory.items.get(result.inventoryItemId);
      expect(insertedItem).toBeDefined();
      expect(insertedItem.name).toBe('Sunset Beach Texture');
      expect(insertedItem.assetType).toBe(0);

      const texturesFolder = app.inventory.folders.get('folder-textures-uuid');
      expect(texturesFolder.children).toContain(result.inventoryItemId);

      // 4. Verify EconomyManager balance updated and fee recorded
      expect(economyManager.balance).toBe(15); // 25 - 10 = 15
      const uploadTx = economyManager.transactions.find((t) => t.description.includes('Sunset Beach Texture'));
      expect(uploadTx).toBeDefined();
      expect(uploadTx?.amount).toBe(10);
      expect(uploadTx?.status).toBe('success');
    });

    it('reports clear error when grid NewFileAgentInventory capability is missing', async () => {
      app.protocol.capabilities = {};

      const blob = new Blob(['test image'], { type: 'image/png' });

      await expect(
        uploadTexture({ file: blob, name: 'Test Texture' })
      ).rejects.toThrow(/Grid capability "NewFileAgentInventory" is not available/);
    });

    it('recovers cleanly when the uploader URL fails', async () => {
      (corsHandler.makeRequest as any).mockResolvedValueOnce({
        ok: false,
        status: 503,
      });

      const blob = new Blob(['test image'], { type: 'image/png' });

      await expect(
        uploadTexture({ file: blob, name: 'Failed Texture' })
      ).rejects.toThrow(/Capability NewFileAgentInventory request failed/);

      // Ensure balance was NOT deducted on failure
      expect(economyManager.balance).toBe(500);
    });
  });
});
