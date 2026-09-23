/**
 * Linkpoint Offline Grid - Password Hashing
 *
 * Local grid accounts are stored on the device, so the account password must
 * never be persisted in a recoverable form. Passwords are stretched with
 * PBKDF2-SHA256 and stored as a salted digest.
 *
 * WebCrypto is used when available (native, so a high iteration count is cheap).
 * Browsers only expose `crypto.subtle` in a secure context, so a pure-JS
 * crypto-js fallback covers plain-http local hosting. Both implementations
 * compute standard PBKDF2-SHA256, so a record produced by one verifies against
 * the other. The parameters used are stored in the record and verification
 * always replays those, never the current defaults.
 */

import CryptoJS from 'crypto-js';

export interface PasswordRecord {
  version: 1;
  algo: 'pbkdf2-sha256';
  iterations: number;
  /** Hex-encoded random salt. */
  salt: string;
  /** Hex-encoded derived key. */
  hash: string;
}

/** OWASP-recommended PBKDF2-SHA256 count; ~100ms via native WebCrypto. */
export const PBKDF2_ITERATIONS = 210000;
/** Pure-JS PBKDF2 is ~100x slower, so the fallback uses a lower count. */
export const PBKDF2_FALLBACK_ITERATIONS = 100000;
export const SALT_BYTES = 16;
export const KEY_BITS = 256;

function bytesToHex(bytes: Uint8Array): string {
  let out = '';
  for (let i = 0; i < bytes.length; i++) {
    out += bytes[i].toString(16).padStart(2, '0');
  }
  return out;
}

function hexToBytes(hex: string): Uint8Array {
  const clean = hex.length % 2 === 0 ? hex : `0${hex}`;
  const out = new Uint8Array(clean.length / 2);
  for (let i = 0; i < out.length; i++) {
    out[i] = parseInt(clean.slice(i * 2, i * 2 + 2), 16);
  }
  return out;
}

function hasSubtleCrypto(): boolean {
  try {
    return (
      typeof globalThis.crypto !== 'undefined' &&
      typeof globalThis.crypto.subtle !== 'undefined' &&
      typeof globalThis.crypto.subtle.deriveBits === 'function'
    );
  } catch (e) {
    return false;
  }
}

function randomSalt(size: number = SALT_BYTES): Uint8Array {
  const out = new Uint8Array(size);
  try {
    if (typeof globalThis.crypto !== 'undefined' && typeof globalThis.crypto.getRandomValues === 'function') {
      globalThis.crypto.getRandomValues(out);
      return out;
    }
  } catch (e) {
    // Fall through to the crypto-js PRNG below.
  }
  return hexToBytes(CryptoJS.lib.WordArray.random(size).toString(CryptoJS.enc.Hex));
}

async function deriveWithSubtle(password: string, salt: Uint8Array, iterations: number): Promise<string> {
  const keyMaterial = await globalThis.crypto.subtle.importKey(
    'raw',
    new TextEncoder().encode(password),
    'PBKDF2',
    false,
    ['deriveBits']
  );
  const bits = await globalThis.crypto.subtle.deriveBits(
    { name: 'PBKDF2', salt: salt as unknown as BufferSource, iterations, hash: 'SHA-256' },
    keyMaterial,
    KEY_BITS
  );
  return bytesToHex(new Uint8Array(bits));
}

function deriveWithCryptoJS(password: string, salt: Uint8Array, iterations: number): string {
  return CryptoJS.PBKDF2(password, CryptoJS.enc.Hex.parse(bytesToHex(salt)), {
    keySize: KEY_BITS / 32,
    iterations,
    hasher: CryptoJS.algo.SHA256
  }).toString(CryptoJS.enc.Hex);
}

async function derive(password: string, salt: Uint8Array, iterations: number): Promise<string> {
  if (hasSubtleCrypto()) {
    return deriveWithSubtle(password, salt, iterations);
  }
  return deriveWithCryptoJS(password, salt, iterations);
}

/** Length-independent comparison so verification does not leak the digest byte by byte. */
function timingSafeEqual(a: string, b: string): boolean {
  if (a.length !== b.length) return false;
  let diff = 0;
  for (let i = 0; i < a.length; i++) {
    diff |= a.charCodeAt(i) ^ b.charCodeAt(i);
  }
  return diff === 0;
}

/** Derive a salted PBKDF2 record for a new or changed password. */
export async function hashPassword(password: string): Promise<PasswordRecord> {
  if (typeof password !== 'string' || password.length === 0) {
    throw new Error('A password is required to create a local grid account.');
  }
  const salt = randomSalt();
  const iterations = hasSubtleCrypto() ? PBKDF2_ITERATIONS : PBKDF2_FALLBACK_ITERATIONS;
  const hash = await derive(password, salt, iterations);
  return { version: 1, algo: 'pbkdf2-sha256', iterations, salt: bytesToHex(salt), hash };
}

/** Check a candidate password against a stored record, replaying its parameters. */
export async function verifyPassword(password: string, record: PasswordRecord | null | undefined): Promise<boolean> {
  if (!isPasswordRecord(record) || typeof password !== 'string' || password.length === 0) {
    return false;
  }
  const candidate = await derive(password, hexToBytes(record.salt), record.iterations);
  return timingSafeEqual(candidate, record.hash);
}

/**
 * Type guard for persisted credentials. Anything that is not a well-formed
 * record - including a bare string left by an older build - is rejected, so a
 * plaintext value can never be treated as a valid credential.
 */
export function isPasswordRecord(value: any): value is PasswordRecord {
  return (
    !!value &&
    typeof value === 'object' &&
    value.version === 1 &&
    value.algo === 'pbkdf2-sha256' &&
    typeof value.iterations === 'number' &&
    value.iterations > 0 &&
    typeof value.salt === 'string' &&
    value.salt.length > 0 &&
    typeof value.hash === 'string' &&
    value.hash.length > 0
  );
}
