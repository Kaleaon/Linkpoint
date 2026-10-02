// @vitest-environment jsdom
import { describe, expect, it, vi } from 'vitest';
import { Graphics3D } from '../graphics-3d';

// A context whose every member is a harmless no-op, enough to get through init().
const fakeGl = () => new Proxy({}, { get: () => () => 0 });

describe('Graphics3D.init when destroyed mid-initialization', () => {
  it('fails with a clear error instead of dereferencing the released context', async () => {
    vi.spyOn(console, 'log').mockImplementation(() => undefined);
    const canvas = document.createElement('canvas');
    vi.spyOn(canvas, 'getContext').mockImplementation(() => fakeGl() as any);
    const graphics = new Graphics3D(canvas);
    // Tear the graphics down while shader creation is pending, as an unmounting view does.
    vi.spyOn(graphics as any, 'createDefaultShaders').mockImplementation(async () => { graphics.destroy(); });

    await expect(graphics.init()).rejects.toThrow('destroyed while initializing');
    vi.restoreAllMocks();
  });
});
