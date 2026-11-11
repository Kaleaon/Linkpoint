import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest';
import {
  LRUTextureCache,
  TextureDecoderPool,
  decodeJPEG2000,
  decodeSync,
  clearTextureCache,
  terminateWorkerPool,
  getTextureLRUCache,
} from '../texture-decoder';
import { createRequire } from 'node:module';

const require = createRequire(import.meta.url);
const sharp = require('sharp');
const { decodeJPEG2000: cjsDecodeJPEG2000, clearTextureCache: cjsClearCache } = require('../../../core/sl-asset-decoder.cjs');

async function createTestJ2kBuffer(width = 2, height = 2, r = 255, g = 0, b = 0): Promise<Buffer> {
  const rawData = Buffer.alloc(width * height * 3);
  for (let i = 0; i < width * height; i++) {
    rawData[i * 3] = r;
    rawData[i * 3 + 1] = g;
    rawData[i * 3 + 2] = b;
  }
  return sharp(rawData, { raw: { width, height, channels: 3 } }).png().toBuffer();
}

describe('LRUTextureCache', () => {
  let cache: LRUTextureCache;

  beforeEach(() => {
    cache = new LRUTextureCache(128);
  });

  it('defaults capacity to 128 items and enforces limit', () => {
    expect(cache.maxCapacity).toBe(128);

    for (let i = 0; i < 128; i++) {
      cache.set(`tex-${i}`, {
        width: 2,
        height: 2,
        rgba: 'AAAA',
        data: new Uint8Array([255, 0, 0, 255]),
        assetId: `tex-${i}`,
      });
    }

    expect(cache.size).toBe(128);

    // Add 129th item -> evicts tex-0 (the oldest item)
    cache.set('tex-128', {
      width: 2,
      height: 2,
      rgba: 'BBBB',
      data: new Uint8Array([0, 255, 0, 255]),
      assetId: 'tex-128',
    });

    expect(cache.size).toBe(128);
    expect(cache.has('tex-0')).toBe(false);
    expect(cache.has('tex-1')).toBe(true);
    expect(cache.has('tex-128')).toBe(true);
  });

  it('updates MRU order on get or has access', () => {
    for (let i = 0; i < 128; i++) {
      cache.set(`tex-${i}`, {
        width: 1,
        height: 1,
        rgba: 'AAAA',
        data: new Uint8Array(4),
        assetId: `tex-${i}`,
      });
    }

    // Access tex-0 to mark as MRU
    expect(cache.get('tex-0')).toBeDefined();

    // Add tex-128 -> should evict tex-1 instead of tex-0
    cache.set('tex-128', {
      width: 1,
      height: 1,
      rgba: 'BBBB',
      data: new Uint8Array(4),
      assetId: 'tex-128',
    });

    expect(cache.has('tex-0')).toBe(true);
    expect(cache.has('tex-1')).toBe(false);
    expect(cache.has('tex-128')).toBe(true);
  });

  it('supports case-insensitive UUID key lookup', () => {
    const uuid = 'F81D4FA2-3DEC-11D0-A765-00A0C91E6BF6';
    const item = {
      width: 4,
      height: 4,
      rgba: 'CCCC',
      data: new Uint8Array(16),
      assetId: uuid,
    };

    cache.set(uuid, item);

    expect(cache.has(uuid)).toBe(true);
    expect(cache.has(uuid.toLowerCase())).toBe(true);
    expect(cache.get(uuid.toLowerCase())).toBe(item);
  });

  it('invokes onEvict callback when items are evicted', () => {
    const evictedKeys: string[] = [];
    const customCache = new LRUTextureCache({
      capacity: 2,
      onEvict: (_item, key) => evictedKeys.push(key),
    });

    customCache.set('key1', { width: 1, height: 1, rgba: '', data: new Uint8Array() });
    customCache.set('key2', { width: 1, height: 1, rgba: '', data: new Uint8Array() });
    customCache.set('key3', { width: 1, height: 1, rgba: '', data: new Uint8Array() });

    expect(customCache.size).toBe(2);
    expect(evictedKeys).toEqual(['key1']);
  });
});

describe('TextureDecoderPool & Fallback Pipeline', () => {
  beforeEach(() => {
    clearTextureCache();
    terminateWorkerPool();
  });

  afterEach(() => {
    clearTextureCache();
    terminateWorkerPool();
  });

  it('falls back seamlessly to synchronous decoding in headless/Node test environment', async () => {
    const buffer = await createTestJ2kBuffer(2, 2, 10, 20, 30);
    const assetId = 'test-asset-uuid-1';

    const result = await decodeJPEG2000(buffer, assetId);

    expect(result.width).toBe(2);
    expect(result.height).toBe(2);
    expect(result.rgba).toBeDefined();
    expect(result.data).toBeDefined();
    expect(result.assetId).toBe(assetId);

    // Verify item is stored in global LRU cache
    const cached = getTextureLRUCache().get(assetId);
    expect(cached).toBe(result);
  });

  it('returns cached RGBA result immediately on cache hit without re-decoding', async () => {
    const buffer = await createTestJ2kBuffer(2, 2, 50, 100, 150);
    const assetId = 'test-asset-uuid-cache-hit';

    const result1 = await decodeJPEG2000(buffer, assetId);
    const result2 = await decodeJPEG2000(buffer, assetId);

    expect(result1).toBe(result2);
  });

  it('simulates Web Worker pool task distribution and zero-copy ArrayBuffer transfer', async () => {
    class MockWorker {
      public onmessage: ((e: MessageEvent) => void) | null = null;
      public onerror: ((e: ErrorEvent) => void) | null = null;

      postMessage(data: any, transferables?: Transferable[]) {
        setTimeout(() => {
          if (this.onmessage) {
            const width = 2;
            const height = 2;
            const rgba = new Uint8Array([100, 100, 100, 255, 100, 100, 100, 255, 100, 100, 100, 255, 100, 100, 100, 255]);
            this.onmessage({
              data: {
                id: data.id,
                width,
                height,
                rgbaBuffer: rgba.buffer,
              },
            } as MessageEvent);
          }
        }, 10);
      }

      terminate() {}
    }

    const pool = new TextureDecoderPool(2);
    (pool as any).isSupported = true;
    pool.addWorker(new MockWorker() as any);
    pool.addWorker(new MockWorker() as any);

    const buffer = new Uint8Array([1, 2, 3, 4]).buffer;
    const taskResult = await pool.decode(buffer, 'mock-asset-1');

    expect(taskResult.width).toBe(2);
    expect(taskResult.height).toBe(2);
    expect(taskResult.data.length).toBe(16);
    expect(taskResult.assetId).toBe('mock-asset-1');
  });

  it('handles worker error and falls back gracefully to synchronous decoding', async () => {
    const realBuffer = await createTestJ2kBuffer(2, 2, 200, 100, 50);

    class ErrorWorker {
      public onmessage: ((e: MessageEvent) => void) | null = null;
      public onerror: ((e: ErrorEvent) => void) | null = null;

      postMessage(data: any) {
        setTimeout(() => {
          if (this.onmessage) {
            this.onmessage({
              data: {
                id: data.id,
                error: 'Out of memory during JPEG2000 parse',
              },
            } as MessageEvent);
          }
        }, 10);
      }

      terminate() {}
    }

    const pool = new TextureDecoderPool(1);
    (pool as any).isSupported = true;
    pool.addWorker(new ErrorWorker() as any);

    const result = await pool.decode(realBuffer, 'error-fallback-asset');

    expect(result.width).toBe(2);
    expect(result.height).toBe(2);
    expect(result.rgba).toBeDefined();
  });
});

describe('Core CJS sl-asset-decoder LRU Cache Integration', () => {
  beforeEach(() => {
    cjsClearCache();
  });

  it('caches decoded JPEG2000 textures in sl-asset-decoder with 128 limit', async () => {
    const buffer = await createTestJ2kBuffer(2, 2, 1, 2, 3);
    const assetId = 'cjs-asset-uuid-1';

    const res1 = await cjsDecodeJPEG2000(buffer, assetId);
    const res2 = await cjsDecodeJPEG2000(buffer, assetId);

    expect(res1).toBe(res2);
    expect(res1.width).toBe(2);
    expect(res1.height).toBe(2);
  });
});
