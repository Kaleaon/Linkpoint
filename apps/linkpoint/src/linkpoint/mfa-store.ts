/**
 * Remembered multi-factor "trusted device" hashes.
 *
 * After a successful MFA login the grid returns a hash; sending it with later
 * logins from the same device avoids asking for a code every time. It is a
 * long-lived credential, so it is only saved when the resident chose to remember
 * the account, is kept per grid and avatar, and can be forgotten.
 */
import { Utils } from './utils';

const KEY = 'linkpoint_mfa_hashes';

const slot = (grid: string, username: string) => `${String(grid).toLowerCase()}|${String(username).trim().toLowerCase()}`;

export function getMfaHash(grid: string, username: string): string {
  const all = Utils.storage.get(KEY, {}) as Record<string, string>;
  const value = all && typeof all === 'object' ? all[slot(grid, username)] : '';
  return typeof value === 'string' ? value : '';
}

export function saveMfaHash(grid: string, username: string, hash: string) {
  if (!hash) return;
  const all = { ...(Utils.storage.get(KEY, {}) as Record<string, string>) };
  all[slot(grid, username)] = hash;
  Utils.storage.set(KEY, all);
}

export function forgetMfaHash(grid: string, username: string) {
  const all = { ...(Utils.storage.get(KEY, {}) as Record<string, string>) };
  delete all[slot(grid, username)];
  Utils.storage.set(KEY, all);
}
