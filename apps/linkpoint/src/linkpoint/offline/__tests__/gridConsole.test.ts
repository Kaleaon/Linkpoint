import { describe, it, expect, beforeEach, vi } from 'vitest';
import {
  GridConsole,
  LOG_COMPONENTS,
  formatLogEntry,
  formatLogTime,
  LogEntry
} from '../GridConsole';
import { LocalGridManager } from '../LocalGridManager';
import { LocalAssetManager } from '../LocalAssetManager';
import { CacheManager } from '../CacheManager';
import { OARParser } from '../OARParser';

describe('GridConsole', () => {
  let log: GridConsole;

  beforeEach(() => {
    localStorage.clear();
    log = new GridConsole();
  });

  it('records entries with level, component and message', () => {
    log.info(LOG_COMPONENTS.GRID, 'Local grid started');
    const entries = log.getEntries();
    expect(entries).toHaveLength(1);
    expect(entries[0].level).toBe('info');
    expect(entries[0].component).toBe('LOCAL GRID');
    expect(entries[0].message).toBe('Local grid started');
    expect(entries[0].timestamp).toBeGreaterThan(0);
  });

  it('emits an event per entry and on clear', () => {
    const onEntry = vi.fn();
    const onCleared = vi.fn();
    log.on('logEntry', onEntry);
    log.on('logCleared', onCleared);

    log.warn(LOG_COMPONENTS.CACHE, 'Cache nearly full');
    expect(onEntry).toHaveBeenCalledTimes(1);
    expect((onEntry.mock.calls[0][0] as LogEntry).message).toBe('Cache nearly full');

    log.clear();
    expect(onCleared).toHaveBeenCalledTimes(1);
    expect(log.getEntries()).toHaveLength(0);
  });

  it('drops the oldest entries once the ring buffer is full', () => {
    const bounded = new GridConsole(3);
    for (let i = 1; i <= 5; i++) {
      bounded.info(LOG_COMPONENTS.REGION, `entry ${i}`);
    }
    const messages = bounded.getEntries().map(e => e.message);
    expect(messages).toEqual(['entry 3', 'entry 4', 'entry 5']);
    expect(bounded.getEntryCount()).toBe(3);
  });

  it('trims existing entries when the limit is lowered', () => {
    for (let i = 1; i <= 6; i++) log.info(LOG_COMPONENTS.REGION, `entry ${i}`);
    log.setMaxEntries(2);
    expect(log.getEntries().map(e => e.message)).toEqual(['entry 5', 'entry 6']);
  });

  it('suppresses entries below the minimum level', () => {
    log.setMinLevel('warn');
    expect(log.debug(LOG_COMPONENTS.GRID, 'noisy')).toBeNull();
    expect(log.info(LOG_COMPONENTS.GRID, 'chatty')).toBeNull();
    expect(log.error(LOG_COMPONENTS.GRID, 'broken')).not.toBeNull();
    expect(log.getEntries()).toHaveLength(1);
  });

  it('filters by level threshold, component and search text', () => {
    log.debug(LOG_COMPONENTS.REGION, 'region debug');
    log.info(LOG_COMPONENTS.ASSET, 'asset stored');
    log.error(LOG_COMPONENTS.ASSET, 'asset write failed');

    expect(log.getEntries({ level: 'info' })).toHaveLength(2);
    expect(log.getEntries({ level: 'error' })).toHaveLength(1);
    expect(log.getEntries({ component: 'ASSET SERVICE' })).toHaveLength(2);
    expect(log.getEntries({ search: 'FAILED' })).toHaveLength(1);
    expect(log.getEntries({ level: 'info', component: 'ASSET SERVICE', search: 'stored' })).toHaveLength(1);
  });

  it('counts entries by level and lists components', () => {
    log.info(LOG_COMPONENTS.GRID, 'a');
    log.warn(LOG_COMPONENTS.CACHE, 'b');
    log.error(LOG_COMPONENTS.ASSET, 'c');
    log.fatal(LOG_COMPONENTS.ASSET, 'd');

    expect(log.getCountsByLevel()).toEqual({ debug: 0, info: 1, warn: 1, error: 1, fatal: 1 });
    expect(log.getComponents()).toEqual(['ASSET SERVICE', 'CACHE', 'LOCAL GRID']);
  });

  it('captures Error objects with their stack as detail', () => {
    const entry = log.captureError(LOG_COMPONENTS.ARCHIVER, 'Import failed', new Error('bad xml'));
    expect(entry?.level).toBe('error');
    expect(entry?.message).toBe('Import failed');
    expect(entry?.detail).toContain('bad xml');
  });

  it('captures non-Error throws without losing information', () => {
    expect(log.captureError(LOG_COMPONENTS.GRID, 'string throw', 'boom')?.detail).toBe('boom');
    expect(log.captureError(LOG_COMPONENTS.GRID, 'object throw', { code: 42 })?.detail).toContain('42');
  });

  it('formats entries in OpenSim console style', () => {
    const entry = log.info(LOG_COMPONENTS.LOGIN, 'Login request for "Ada Lovelace".')!;
    const line = formatLogEntry(entry);
    expect(line).toContain('INFO ');
    expect(line).toContain('[LOGIN SERVICE]: Login request for "Ada Lovelace".');
    expect(line.startsWith(formatLogTime(entry.timestamp))).toBe(true);
  });

  it('exports filtered text with dates for download', () => {
    log.debug(LOG_COMPONENTS.REGION, 'quiet');
    log.error(LOG_COMPONENTS.REGION, 'loud');
    const text = log.toText({ level: 'error' });
    expect(text).toContain('loud');
    expect(text).not.toContain('quiet');
    expect(text).toMatch(/^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2},\d{3} ERROR/);
  });

  it('routes uncaught window errors into the log', () => {
    const detach = log.attachGlobalErrorHandlers();
    window.dispatchEvent(new ErrorEvent('error', { message: 'kaboom', error: new Error('kaboom') }));
    expect(log.getEntries({ level: 'error' }).some(e => e.message.includes('kaboom'))).toBe(true);

    detach();
    window.dispatchEvent(new ErrorEvent('error', { message: 'after detach' }));
    expect(log.getEntries().some(e => e.message.includes('after detach'))).toBe(false);
  });
});

describe('offline services write to the console', () => {
  beforeEach(() => {
    localStorage.clear();
  });

  it('logs the grid lifecycle and login outcomes', async () => {
    const log = new GridConsole();
    const manager = new LocalGridManager(log);
    await manager.setupOfflineAccount('Ada', 'Lovelace', 'analytical');

    manager.toggleGridState(true);
    await manager.getServer().processLogin('Ada', 'Lovelace', 'analytical');
    await manager.getServer().processLogin('Ada', 'Lovelace', 'wrong');
    manager.toggleGridState(false);

    const text = log.toText();
    expect(text).toContain('[LOCAL GRID]: Local grid started');
    expect(text).toContain('[LOCAL GRID]: Local grid shut down.');
    expect(text).toContain('Login succeeded');
    expect(text).toContain('Login failed');
    // The password must not leak into the log.
    expect(text).not.toContain('analytical');
  });

  it('logs a login attempt refused because the grid is stopped', async () => {
    const log = new GridConsole();
    const manager = new LocalGridManager(log);
    await manager.setupOfflineAccount('Ada', 'Lovelace', 'analytical');

    const res = await manager.getServer().processLogin('Ada', 'Lovelace', 'analytical');
    expect(res.login).toBe('false');
    expect(log.getEntries({ level: 'error' }).some(e => e.message.includes('local grid is offline'))).toBe(true);
  });

  it('logs asset, cache and archiver activity', async () => {
    const log = new GridConsole();

    new LocalAssetManager(log).uploadAsset('Tree', 'mesh', 'data');
    new CacheManager(log).setMaxSizeMB(512);
    await new OARParser(log).parseOAR('<SceneObjectGroup><Name>Hut</Name></SceneObjectGroup>');

    const text = log.toText();
    expect(text).toContain('[ASSET SERVICE]: Stored mesh asset "Tree"');
    expect(text).toContain('[CACHE]: Cache limit set to');
    expect(text).toContain('[ARCHIVER]: Loaded 1 object(s)');
  });

  it('warns rather than failing when OAR input has no scene objects', async () => {
    const log = new GridConsole();
    const parsed = await new OARParser(log).parseOAR('not xml at all');
    expect(parsed.region.prims).toHaveLength(0);
    expect(log.getEntries({ level: 'warn' }).some(e => e.message.includes('No <SceneObjectGroup>'))).toBe(true);
  });
});
