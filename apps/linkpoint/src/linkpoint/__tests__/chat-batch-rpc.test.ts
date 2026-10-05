import { createRequire } from 'node:module';
import { describe, expect, it, vi, beforeEach, afterEach } from 'vitest';
import { slBridge } from '../sl-bridge';
import { InventoryManager } from '../inventory';

const require = createRequire(import.meta.url);
const { ViewerSession } = require('../../../core/viewer-session.cjs');
const { callViewer } = require('../../../core/viewer-api.cjs');

describe('Bulk Chat Transport RPC (sendChatBatch)', () => {
  let session: any;
  let mockComms: any;

  beforeEach(() => {
    mockComms = {
      whisper: vi.fn().mockResolvedValue(undefined),
      say: vi.fn().mockResolvedValue(undefined),
      shout: vi.fn().mockResolvedValue(undefined),
    };
    session = new ViewerSession(() => undefined);
    session.bot = { clientCommands: { comms: mockComms } };
  });

  it('dispatches bulk chat commands via callViewer and ViewerSession', async () => {
    const items = [
      { message: '/wear Shirt', channel: 0, type: 1 },
      { message: '/wear Pants', channel: 0, type: 0 },
      { message: '/wear Shoes', channel: 0, type: 2 },
    ];

    const result = await callViewer(session, 'sendChatBatch', { items });

    expect(result.successful).toBe(3);
    expect(result.failed).toBe(0);
    expect(result.results.length).toBe(3);
    expect(mockComms.say).toHaveBeenCalledWith('/wear Shirt', 0);
    expect(mockComms.whisper).toHaveBeenCalledWith('/wear Pants', 0);
    expect(mockComms.shout).toHaveBeenCalledWith('/wear Shoes', 0);
  });

  it('enforces maximum 50 commands per batch in ViewerSession', async () => {
    const items = Array.from({ length: 51 }, (_, i) => ({
      message: `/wear Item ${i}`,
      channel: 0,
      type: 1,
    }));

    await expect(callViewer(session, 'sendChatBatch', { items })).rejects.toThrow(
      /Batch size exceeds maximum limit of 50 commands/
    );
  });

  it('tracks partial failures and identifies failed commands', async () => {
    mockComms.say.mockImplementation(async (msg: string) => {
      if (msg.includes('FailItem')) {
        throw new Error('Failed to deliver message');
      }
    });

    const items = [
      { message: '/wear GoodItem1', channel: 0, type: 1 },
      { message: '/wear FailItem', channel: 0, type: 1 },
      { message: '/wear GoodItem2', channel: 0, type: 1 },
    ];

    const result = await callViewer(session, 'sendChatBatch', { items });

    expect(result.successful).toBe(2);
    expect(result.failed).toBe(1);
    expect(result.results[0].success).toBe(true);
    expect(result.results[1].success).toBe(false);
    expect(result.results[1].error).toBe('Failed to deliver message');
    expect(result.results[2].success).toBe(true);
    expect(result.errors).toEqual([
      { index: 1, message: '/wear FailItem', error: 'Failed to deliver message' },
    ]);
  });

  it('chunks batch requests on slBridge if count exceeds 50 items', async () => {
    vi.spyOn(slBridge, 'connected', 'get').mockReturnValue(true);
    const callSpy = vi.spyOn(slBridge, 'call').mockImplementation(async (method: string, params: any) => {
      if (method === 'sendChatBatch') {
        return {
          successful: params.items.length,
          failed: 0,
          results: params.items.map((item: any, idx: number) => ({
            index: idx,
            message: item.message,
            channel: item.channel,
            type: item.type,
            success: true,
          })),
        };
      }
      return {};
    });

    const largeBatch = Array.from({ length: 75 }, (_, i) => ({
      message: `/wear Item ${i}`,
      channel: 0,
      type: 1,
    }));

    const result = await slBridge.sendChatBatch(largeBatch);

    expect(callSpy).toHaveBeenCalledTimes(2);
    expect(callSpy).toHaveBeenNthCalledWith(1, 'sendChatBatch', { items: largeBatch.slice(0, 50) });
    expect(callSpy).toHaveBeenNthCalledWith(2, 'sendChatBatch', { items: largeBatch.slice(50, 75) });
    expect(result.successful).toBe(75);
    expect(result.results.length).toBe(75);
    expect(result.results[74].index).toBe(74);

    callSpy.mockRestore();
  });
});

describe('Outfit Fallback Bulk Chat Transport & Backward Compatibility', () => {
  let inv: InventoryManager;

  beforeEach(() => {
    inv = new InventoryManager({ capabilities: {} } as any, {} as any);
    // Populate dummy folder and items
    (inv as any).folders.set('cof-id', { id: 'cof-id', name: 'Current Outfit', children: [] });
    (inv as any).folders.set('outfit-folder-id', { id: 'outfit-folder-id', name: 'Test Outfit', children: ['item-1', 'item-2'] });
    (inv as any).items.set('item-1', { id: 'item-1', name: 'Red Jacket' });
    (inv as any).items.set('item-2', { id: 'item-2', name: 'Blue Jeans' });
    vi.spyOn(inv, 'getCurrentOutfitFolderId').mockReturnValue('cof-id');
    vi.spyOn(inv, 'getFolderItemIds').mockReturnValue(['item-1', 'item-2']);
  });

  afterEach(() => {
    vi.restoreAllMocks();
  });

  it('uses sendChatBatch for executeOutfitFallback when slBridge is connected', async () => {
    vi.spyOn(slBridge, 'connected', 'get').mockReturnValue(true);
    const sendBatchSpy = vi.spyOn(slBridge, 'sendChatBatch').mockResolvedValue({
      successful: 2,
      failed: 0,
      results: [],
    });
    const sendChatSpy = vi.spyOn(slBridge, 'sendChat').mockResolvedValue(undefined);

    const res = await inv.addToOutfit('outfit-folder-id');

    expect(res.success).toBe(true);
    expect(sendBatchSpy).toHaveBeenCalledTimes(1);
    expect(sendBatchSpy).toHaveBeenCalledWith([
      { message: '/wear Red Jacket', channel: 0, type: 1 },
      { message: '/wear Blue Jeans', channel: 0, type: 1 },
    ]);
    expect(sendChatSpy).not.toHaveBeenCalled();
  });

  it('falls back to individual sendChat calls if sendChatBatch fails or is unsupported', async () => {
    vi.spyOn(slBridge, 'connected', 'get').mockReturnValue(true);
    const sendBatchSpy = vi.spyOn(slBridge, 'sendChatBatch').mockRejectedValue(
      new Error('Unknown viewer call: sendChatBatch')
    );
    const sendChatSpy = vi.spyOn(slBridge, 'sendChat').mockResolvedValue(undefined);

    const res = await inv.addToOutfit('outfit-folder-id');

    expect(res.success).toBe(true);
    expect(sendBatchSpy).toHaveBeenCalledTimes(1);
    expect(sendChatSpy).toHaveBeenCalledTimes(2);
    expect(sendChatSpy).toHaveBeenNthCalledWith(1, '/wear Red Jacket', 0, 1);
    expect(sendChatSpy).toHaveBeenNthCalledWith(2, '/wear Blue Jeans', 0, 1);
  });
});
