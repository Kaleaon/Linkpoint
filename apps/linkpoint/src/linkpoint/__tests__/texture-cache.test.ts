import { describe, it, expect, vi, beforeEach } from 'vitest';
import { TextureCache } from '../texture-cache';
import { Graphics3D } from '../graphics-3d';
import { Scene3D } from '../scene-3d';
import { WorldViewer } from '../world';

describe('TextureCache', () => {
  it('enforces capacity limit and evicts LRU item', () => {
    const evicted: string[] = [];
    const cache = new TextureCache<any>({
      capacity: 3,
      onEvict: (_item, key) => evicted.push(key),
    });

    cache.set('tex1', { assetId: 'tex1', name: 'Texture 1' });
    cache.set('tex2', { assetId: 'tex2', name: 'Texture 2' });
    cache.set('tex3', { assetId: 'tex3', name: 'Texture 3' });

    expect(cache.size).toBe(3);
    expect(evicted).toEqual([]);

    // Adding 4th item should evict 'tex1' (LRU)
    cache.set('tex4', { assetId: 'tex4', name: 'Texture 4' });

    expect(cache.size).toBe(3);
    expect(cache.has('tex1')).toBe(false);
    expect(cache.has('tex2')).toBe(true);
    expect(cache.has('tex3')).toBe(true);
    expect(cache.has('tex4')).toBe(true);
    expect(evicted).toEqual(['tex1']);
  });

  it('updates LRU order on access via get or has', () => {
    const evicted: string[] = [];
    const cache = new TextureCache<any>({
      capacity: 3,
      onEvict: (_item, key) => evicted.push(key),
    });

    cache.set('tex1', { assetId: 'tex1' });
    cache.set('tex2', { assetId: 'tex2' });
    cache.set('tex3', { assetId: 'tex3' });

    // Access 'tex1' to mark it as MRU
    expect(cache.get('tex1')).toEqual({ assetId: 'tex1' });

    // Adding 4th item should now evict 'tex2' (the new LRU)
    cache.set('tex4', { assetId: 'tex4' });

    expect(cache.has('tex1')).toBe(true);
    expect(cache.has('tex2')).toBe(false);
    expect(cache.has('tex3')).toBe(true);
    expect(cache.has('tex4')).toBe(true);
    expect(evicted).toEqual(['tex2']);
  });

  it('handles lowercase alias keys and canonical mapping seamlessly', () => {
    const evicted: string[] = [];
    const cache = new TextureCache<any>({
      capacity: 2,
      onEvict: (_item, key) => evicted.push(key),
    });

    const asset = { assetId: 'UUID-ABC123', name: 'Test' };
    cache.set('UUID-ABC123', asset);
    cache.set('uuid-abc123', asset);

    // Setting alias should not increase size beyond 1
    expect(cache.size).toBe(1);
    expect(cache.has('UUID-ABC123')).toBe(true);
    expect(cache.has('uuid-abc123')).toBe(true);
    expect(cache.get('uuid-abc123')).toBe(asset);

    cache.set('UUID-DEF456', { assetId: 'UUID-DEF456' });
    expect(cache.size).toBe(2);

    // Adding 3rd texture should evict 'UUID-ABC123'
    cache.set('UUID-GHI789', { assetId: 'UUID-GHI789' });

    expect(cache.size).toBe(2);
    expect(cache.has('UUID-ABC123')).toBe(false);
    expect(cache.has('uuid-abc123')).toBe(false);
    expect(evicted).toEqual(['UUID-ABC123']);
  });

  it('defaults capacity to 100 textures', () => {
    const cache = new TextureCache<any>();
    expect(cache.maxCapacity).toBe(100);

    for (let i = 0; i < 105; i++) {
      cache.set(`tex-${i}`, { assetId: `tex-${i}` });
    }

    expect(cache.size).toBe(100);
    expect(cache.has('tex-0')).toBe(false);
    expect(cache.has('tex-4')).toBe(false);
    expect(cache.has('tex-5')).toBe(true);
    expect(cache.has('tex-104')).toBe(true);
  });
});

describe('Graphics3D & Scene3D Texture Disposal', () => {
  it('calls gl.deleteTexture when Graphics3D.deleteTexture is invoked', () => {
    const graphics = new Graphics3D({} as HTMLCanvasElement);
    const mockGl = {
      createTexture: vi.fn(() => ({ id: 'mock-webgl-texture-1' })),
      deleteTexture: vi.fn(),
      bindTexture: vi.fn(),
      pixelStorei: vi.fn(),
      texImage2D: vi.fn(),
      texParameteri: vi.fn(),
      generateMipmap: vi.fn(),
      getExtension: vi.fn(),
    };
    graphics.gl = mockGl as any;

    const rgba = new Uint8Array([255, 0, 0, 255]);
    graphics.createTexture('texture:abc-123', 1, 1, rgba);

    expect(graphics.hasTexture('texture:abc-123')).toBe(true);
    expect(graphics.hasTexture('abc-123')).toBe(true);

    const deleted = graphics.deleteTexture('abc-123');

    expect(deleted).toBe(true);
    expect(mockGl.deleteTexture).toHaveBeenCalledTimes(1);
    expect(mockGl.deleteTexture).toHaveBeenCalledWith({ id: 'mock-webgl-texture-1' });
    expect(graphics.hasTexture('texture:abc-123')).toBe(false);
    expect(graphics.hasTexture('abc-123')).toBe(false);
  });

  it('exposes removeTexture in Scene3D and deletes GPU texture', () => {
    const mockGraphics = {
      deleteTexture: vi.fn().mockReturnValue(true),
    };
    const scene = new Scene3D(mockGraphics as any, {} as any);

    const result = scene.removeTexture('asset-uuid-999');

    expect(result).toBe(true);
    expect(mockGraphics.deleteTexture).toHaveBeenCalledWith('asset-uuid-999');
  });
});

describe('WorldViewer LRU Eviction & Scene Reference Fallback', () => {
  it('evicts LRU textures, deletes WebGL textures, and updates scene objects', () => {
    const mockGl = {
      createTexture: vi.fn((name?: string) => ({ id: `tex-handle-${Math.random()}` })),
      deleteTexture: vi.fn(),
      bindTexture: vi.fn(),
      pixelStorei: vi.fn(),
      texImage2D: vi.fn(),
      texParameteri: vi.fn(),
      generateMipmap: vi.fn(),
      getExtension: vi.fn(),
    };

    const mockProtocol = {
      connected: true,
      on: vi.fn(),
    };

    const world = new WorldViewer(mockProtocol);
    const graphics = new Graphics3D({} as HTMLCanvasElement);
    graphics.gl = mockGl as any;
    world.scene3d = new Scene3D(graphics, {} as any);

    // Set capacity to 2 for test
    const decodedTextures = (world as any).decodedTextures as TextureCache;
    decodedTextures.setCapacity(2);

    // Create Base64 1x1 RGBA pixel
    const rgbaB64 = btoa(String.fromCharCode(255, 255, 255, 255));

    // Register 2 textures
    (world as any).applyTexture({ assetId: 'TEX-1', rgba: rgbaB64, width: 1, height: 1 });
    (world as any).applyTexture({ assetId: 'TEX-2', rgba: rgbaB64, width: 1, height: 1 });

    expect(decodedTextures.size).toBe(2);
    expect(mockGl.deleteTexture).not.toHaveBeenCalled();

    // Register a 3rd texture to trigger eviction of TEX-1
    (world as any).applyTexture({ assetId: 'TEX-3', rgba: rgbaB64, width: 1, height: 1 });

    expect(decodedTextures.size).toBe(2);
    expect(decodedTextures.has('TEX-1')).toBe(false);
    expect(decodedTextures.has('TEX-2')).toBe(true);
    expect(decodedTextures.has('TEX-3')).toBe(true);

    // Verify gl.deleteTexture was called for evicted TEX-1
    expect(mockGl.deleteTexture).toHaveBeenCalled();
  });
});
