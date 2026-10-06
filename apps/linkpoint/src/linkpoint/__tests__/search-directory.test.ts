import { createRequire } from 'node:module';
import { afterEach, describe, expect, it, vi } from 'vitest';
import { slBridge } from '../sl-bridge';

const { METHODS, callViewer } = createRequire(import.meta.url)('../../../core/viewer-api.cjs');
const { ViewerSession } = createRequire(import.meta.url)('../../../core/viewer-session.cjs');

describe('searchDir RPC and ViewerSession directory search', () => {
  afterEach(() => {
    vi.restoreAllMocks();
    (slBridge as any).connected = false;
    (slBridge as any).sessionId = null;
  });

  it('registers searchDir in viewer-api allowed METHODS', () => {
    expect(METHODS.has('searchDir')).toBe(true);
  });

  it('dispatches searchDir via callViewer to ViewerSession', async () => {
    const session = new ViewerSession(() => undefined);
    const result = await callViewer(session, 'searchDir', { category: 'places', query: 'Beach', maturity: 3 });
    expect(result).toHaveProperty('results');
    expect(Array.isArray(result.results)).toBe(true);
    expect(result.results.length).toBeGreaterThan(0);
    expect(result.results[0].category).toBe('places');
  });

  it('filters results by maturity bitmask (General = 1, Moderate = 2, Adult = 4)', async () => {
    const session = new ViewerSession(() => undefined);

    // General only (maturity = 1)
    const generalRes = await session.searchDir({ category: 'places', query: 'Club', maturity: 1 });
    expect(generalRes.results.every((r: any) => r.maturity === 'General')).toBe(true);

    // General + Moderate (maturity = 3)
    const modRes = await session.searchDir({ category: 'places', query: 'Club', maturity: 3 });
    expect(modRes.results.some((r: any) => r.maturity === 'General')).toBe(true);
    expect(modRes.results.some((r: any) => r.maturity === 'Moderate')).toBe(true);
    expect(modRes.results.every((r: any) => r.maturity !== 'Adult')).toBe(true);

    // All maturities (maturity = 7)
    const allRes = await session.searchDir({ category: 'places', query: 'Club', maturity: 7 });
    expect(allRes.results.some((r: any) => r.maturity === 'Adult')).toBe(true);
  });

  it('enforces agent access flags for maturity restrictions', async () => {
    const session = new ViewerSession(() => undefined);
    session.bot = {
      agent: { accessFlags: 'PG' },
    };

    // Even if requested maturity includes Adult (7), PG access caps it at General (1)
    const res = await session.searchDir({ category: 'places', query: 'Lounge', maturity: 7 });
    expect(res.results.every((r: any) => r.maturity === 'General')).toBe(true);
  });

  it('returns appropriate structured search results for all directory categories', async () => {
    const session = new ViewerSession(() => undefined);

    const categories = ['places', 'events', 'land', 'groups', 'people'];
    for (const cat of categories) {
      const res = await session.searchDir({ category: cat, query: 'Test', maturity: 3 });
      expect(res).toHaveProperty('results');
      expect(res.results.length).toBeGreaterThan(0);
      expect(res.results[0].category).toBe(cat);
    }
  });

  it('calls searchDir on slBridge when connected', async () => {
    const spy = vi.spyOn(slBridge, 'call').mockResolvedValueOnce({ results: [{ id: '1', name: 'Place 1', type: 'place' }], hasMore: false });
    vi.spyOn(slBridge, 'connected', 'get').mockReturnValue(true);
    slBridge.sessionId = 'test-session';

    const params = { category: 'places', query: 'Island', maturity: 3 };
    const res = await slBridge.searchDir(params);

    expect(spy).toHaveBeenCalledWith('searchDir', params);
    expect(res.results[0].name).toBe('Place 1');
  });
});
