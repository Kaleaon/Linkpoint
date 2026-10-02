import { createRequire } from 'node:module';
import { describe, expect, it, vi } from 'vitest';

const { METHODS, callViewer } = createRequire(import.meta.url)('../../../core/viewer-api.cjs');
const { ViewerSession } = createRequire(import.meta.url)('../../../core/viewer-session.cjs');

describe('the shared viewer call table', () => {
  it('runs a listed method with its parameter object', async () => {
    const session = { sendChat: vi.fn(async () => undefined) };
    await callViewer(session, 'sendChat', { message: 'hi', channel: 0, type: 1 });
    expect(session.sendChat).toHaveBeenCalledWith({ message: 'hi', channel: 0, type: 1 });
  });

  it('refuses anything not on the list, including inherited and internal members', async () => {
    const session = new ViewerSession(() => undefined);
    for (const name of ['connect', 'close', 'constructor', '__proto__', 'bot', 'requireBot', 'toString', '']) {
      await expect(callViewer(session, name, {})).rejects.toThrow(/Unknown viewer call/);
    }
  });

  it('lists only methods the session actually has', () => {
    const session = new ViewerSession(() => undefined);
    for (const name of METHODS) expect(typeof session[name], name).toBe('function');
  });

  it('reports "not connected" for a session with no login', async () => {
    await expect(callViewer(new ViewerSession(() => undefined), 'sendChat', { message: 'x' })).rejects.toThrow(/Not connected/);
  });
});

describe('map blocks', () => {
  it('returns the grid\'s regions, drops nameless ones, and bounds the request', async () => {
    const session = new ViewerSession(() => undefined);
    const getRegionMapInfoRange = vi.fn(async () => ({ regions: [
      { name: 'Ahern', x: 1000, y: 1000, accessFlags: 13, waterHeight: 20, regionFlags: 0, mapImage: { toString: () => 'img' } },
      { name: '', x: 1001, y: 1000 },
      { name: 'Ahern', x: 1000, y: 1000, accessFlags: 13 },
    ] }));
    session.bot = { clientCommands: { grid: { getRegionMapInfoRange } } };
    const blocks = await callViewer(session, 'getMapBlocks', { minX: 999, minY: 999, maxX: 1001, maxY: 1001 });
    expect(blocks).toEqual([{ x: 1000, y: 1000, name: 'Ahern', access: 13, waterHeight: 20, regionFlags: 0, mapImage: 'img' }]);
    expect(getRegionMapInfoRange).toHaveBeenCalledWith(999, 999, 1001, 1001);
    await expect(callViewer(session, 'getMapBlocks', { minX: 0, minY: 0, maxX: 100, maxY: 100 })).rejects.toThrow(/too large/);
    await expect(callViewer(session, 'getMapBlocks', {})).rejects.toThrow(/Invalid map range/);
  });
});
