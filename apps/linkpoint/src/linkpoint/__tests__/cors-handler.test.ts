import { afterEach, describe, expect, it, vi } from 'vitest';
import { CORSHandler } from '../cors-handler';

describe('CORSHandler trusted proxy configuration', () => {
  afterEach(() => vi.unstubAllGlobals());

  it('does not include public proxy providers', () => {
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue({ ok: true, json: async () => ({ status: 'ok' }) }));
    const handler = new CORSHandler() as any;
    expect(handler.corsProxies.map((proxy: { name: string }) => proxy.name)).toEqual(['Local Server Proxy']);
  });
});
