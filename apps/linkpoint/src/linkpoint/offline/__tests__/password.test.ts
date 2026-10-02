import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest';
import { LocalGridManager, LOCAL_GRID_STORAGE_KEYS } from '../LocalGridManager';
import { GridConsole } from '../GridConsole';
import {
  hashPassword,
  verifyPassword,
  isPasswordRecord,
  PBKDF2_ITERATIONS,
  PBKDF2_FALLBACK_ITERATIONS,
  KEY_BITS
} from '../password';

describe('offline account password hashing', () => {
  beforeEach(() => {
    localStorage.clear();
  });

  it('produces a salted PBKDF2 record that never contains the password', async () => {
    const record = await hashPassword('hunter2');
    expect(record.algo).toBe('pbkdf2-sha256');
    expect(record.iterations).toBeGreaterThanOrEqual(100000);
    expect(record.salt.length).toBeGreaterThan(0);
    expect(JSON.stringify(record)).not.toContain('hunter2');
  });

  it('uses a fresh salt per call, so equal passwords hash differently', async () => {
    const a = await hashPassword('same password');
    const b = await hashPassword('same password');
    expect(a.salt).not.toBe(b.salt);
    expect(a.hash).not.toBe(b.hash);
    await expect(verifyPassword('same password', a)).resolves.toBe(true);
    await expect(verifyPassword('same password', b)).resolves.toBe(true);
  });

  it('accepts the correct password and rejects near misses', async () => {
    const record = await hashPassword('Tr0ub4dor&3');
    await expect(verifyPassword('Tr0ub4dor&3', record)).resolves.toBe(true);
    await expect(verifyPassword('Tr0ub4dor&4', record)).resolves.toBe(false);
    await expect(verifyPassword('tr0ub4dor&3', record)).resolves.toBe(false);
    await expect(verifyPassword('', record)).resolves.toBe(false);
  });

  it('rejects a malformed or legacy plaintext credential', async () => {
    expect(isPasswordRecord('plaintext')).toBe(false);
    expect(isPasswordRecord(null)).toBe(false);
    expect(isPasswordRecord({ version: 1, algo: 'pbkdf2-sha256' })).toBe(false);
    await expect(verifyPassword('plaintext', 'plaintext' as any)).resolves.toBe(false);
    await expect(verifyPassword('anything', null)).resolves.toBe(false);
  });

  it('refuses to hash an empty password', async () => {
    await expect(hashPassword('')).rejects.toThrow();
  });

  // crypto-js computes PBKDF2 in pure JavaScript, so replaying the full
  // PBKDF2_ITERATIONS count here takes ~4s locally and tips over vitest's 5s
  // default on a CI runner. The assertion is the point of the test, so the
  // clock is what gets relaxed rather than the iteration count.
  it('verifies against an independently computed PBKDF2-SHA256 digest', async () => {
    const record = await hashPassword('cross-impl');
    const keyMaterial = await globalThis.crypto.subtle.importKey(
      'raw',
      new TextEncoder().encode('cross-impl'),
      'PBKDF2',
      false,
      ['deriveBits']
    );
    const saltBytes = new Uint8Array(record.salt.match(/.{1,2}/g).map(byte => parseInt(byte, 16)));
    const bits = await globalThis.crypto.subtle.deriveBits(
      { name: 'PBKDF2', salt: saltBytes, iterations: record.iterations, hash: 'SHA-256' },
      keyMaterial,
      KEY_BITS
    );
    const expected = Array.from(new Uint8Array(bits)).map(b => b.toString(16).padStart(2, '0')).join('');
    expect(record.hash).toBe(expected);
  }, 30_000);

  it('replays the stored iteration count rather than the current default', async () => {
    const record = await hashPassword('legacy params');
    const downgraded = { ...record, iterations: 1000 };
    const keyMaterial = await globalThis.crypto.subtle.importKey(
      'raw',
      new TextEncoder().encode('legacy params'),
      'PBKDF2',
      false,
      ['deriveBits']
    );
    const saltBytes = new Uint8Array(record.salt.match(/.{1,2}/g).map(byte => parseInt(byte, 16)));
    const bits = await globalThis.crypto.subtle.deriveBits(
      { name: 'PBKDF2', salt: saltBytes, iterations: 1000, hash: 'SHA-256' },
      keyMaterial,
      KEY_BITS
    );
    const rehashed = Array.from(new Uint8Array(bits)).map(b => b.toString(16).padStart(2, '0')).join('');

    await expect(verifyPassword('legacy params', { ...downgraded, hash: rehashed })).resolves.toBe(true);
    expect(record.iterations).toBe(PBKDF2_ITERATIONS);
  });

  it('never writes the password to local storage during account setup', async () => {
    const manager = new LocalGridManager(new GridConsole());
    await manager.setupOfflineAccount('Grace', 'Hopper', 'nanosecond');

    const raw = localStorage.getItem(LOCAL_GRID_STORAGE_KEYS.USER_ACCOUNT) || '';
    expect(raw).not.toContain('nanosecond');
    expect(raw.length).toBeGreaterThan(0);

    const stored = JSON.parse(raw);
    expect(isPasswordRecord(stored.passwordRecord)).toBe(true);
    expect(stored.passwordHash).toBeUndefined();

    // The whole storage surface, not just the account key.
    const everything = Object.keys(localStorage)
      .map(k => `${k}=${localStorage.getItem(k)}`)
      .join('\n');
    expect(everything).not.toContain('nanosecond');
  });

  it('keeps the password out of the in-memory user record and login response', async () => {
    const manager = new LocalGridManager(new GridConsole());
    const user = await manager.setupOfflineAccount('Alan', 'Turing', 'enigma123');
    expect(JSON.stringify(user)).not.toContain('enigma123');

    manager.toggleGridState(true);
    const response = await manager.getServer().processLogin('Alan', 'Turing', 'enigma123');
    expect(response.login).toBe('true');
    const serialized = JSON.stringify(response);
    expect(serialized).not.toContain('enigma123');
    expect(serialized).not.toContain('passwordRecord');
  });

  it('supports changing the password', async () => {
    const manager = new LocalGridManager(new GridConsole());
    await manager.setupOfflineAccount('Edsger', 'Dijkstra', 'shortestpath');
    manager.toggleGridState(true);

    await manager.changePassword('newshortestpath');

    const server = manager.getServer();
    await expect(server.processLogin('Edsger', 'Dijkstra', 'shortestpath')).resolves.toMatchObject({ login: 'false' });
    await expect(server.processLogin('Edsger', 'Dijkstra', 'newshortestpath')).resolves.toMatchObject({ login: 'true' });
  });

  it('refuses authentication while the grid is stopped', async () => {
    const manager = new LocalGridManager(new GridConsole());
    await manager.setupOfflineAccount('Barbara', 'Liskov', 'substitution');
    await expect(manager.getServer().authenticate('Barbara', 'Liskov', 'substitution')).resolves.toBe(false);

    manager.toggleGridState(true);
    await expect(manager.getServer().authenticate('Barbara', 'Liskov', 'substitution')).resolves.toBe(true);
  });

  describe('without WebCrypto (plain-http hosting)', () => {
    afterEach(() => {
      vi.unstubAllGlobals();
    });

    it('hashes with the pure-JS fallback at the lower iteration count and the record verifies natively', async () => {
      const real = globalThis.crypto;
      vi.stubGlobal('crypto', { getRandomValues: real.getRandomValues.bind(real) });
      const record = await hashPassword('plain http pw');
      expect(record.iterations).toBe(PBKDF2_FALLBACK_ITERATIONS);
      await expect(verifyPassword('plain http pw', record)).resolves.toBe(true);
      await expect(verifyPassword('wrong', record)).resolves.toBe(false);
      vi.unstubAllGlobals();
      // The same record must verify through native WebCrypto: both compute standard PBKDF2-SHA256.
      await expect(verifyPassword('plain http pw', record)).resolves.toBe(true);
    }, 60_000);
  });
});
