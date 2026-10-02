/**
 * Contacts: a device-local address book of Second Life residents, saved from the
 * friends list. Each contact can carry a note, a photo the user chose, and links
 * to the same person on other apps (Telegram, Discord, any web page).
 *
 * Works entirely offline and without a Google account. Syncing to Google
 * Contacts is a separate, optional step (see `services/googleContacts.ts`).
 *
 * Nothing is invented: a contact exists because the user saved it, and a photo
 * exists only if the user (or the resident's own public profile) supplied one.
 */

import { Utils } from './utils';

export const CONTACTS_STORAGE_KEY = 'linkpoint.contacts.v1';
export const MAX_CONTACTS = 2000;
export const MAX_NOTE_LENGTH = 1000;
/** A 256px JPEG is ~20-40 KB; this leaves room for a PNG while keeping localStorage healthy. */
export const MAX_PHOTO_CHARS = 200_000;

export type LinkService = 'telegram' | 'discord' | 'web';

export interface ContactLink {
  service: LinkService;
  /** What the user sees: `@name`, a Discord username or id, or the page's host. */
  label: string;
  /** Where it opens, or null when the service cannot deep-link (a Discord username). */
  url: string | null;
}

export interface Contact {
  /** The resident's Second Life UUID. */
  id: string;
  name: string;
  note: string;
  /** A small image as a data URL, or null. */
  photo: string | null;
  links: ContactLink[];
  /** Set once the contact has been copied to Google Contacts. */
  googleResourceName: string | null;
  savedAt: number;
  updatedAt: number;
}

export type LinkResult = { ok: true; link: ContactLink } | { ok: false; error: string };

const TELEGRAM_USERNAME = /^[A-Za-z][A-Za-z0-9_]{4,31}$/;
const DISCORD_SNOWFLAKE = /^\d{17,20}$/;
const DISCORD_USERNAME = /^[a-z0-9_.]{2,32}$/;
const PHOTO_URL = /^data:image\/(?:jpeg|png|webp);base64,[A-Za-z0-9+/=]+$/;

/**
 * Validate and normalise a link the user typed or pasted. Only well-formed
 * values become links, and only http(s) URLs are ever opened: a `javascript:` or
 * `data:` URL is rejected, not escaped.
 */
export function normalizeLink(service: LinkService, input: string): LinkResult {
  const text = String(input ?? '').trim();
  if (!text) return { ok: false, error: 'Enter a value for this link.' };

  if (service === 'telegram') {
    const match = text.match(/^(?:https?:\/\/)?(?:t\.me|telegram\.me)\/([^/?#\s]+)\/?(?:[?#].*)?$/i);
    const username = (match ? match[1] : text).replace(/^@/, '');
    if (!TELEGRAM_USERNAME.test(username)) return { ok: false, error: 'Telegram usernames are 5-32 letters, digits or underscores, starting with a letter.' };
    return { ok: true, link: { service, label: `@${username}`, url: `https://t.me/${username}` } };
  }

  if (service === 'discord') {
    const match = text.match(/^(?:https?:\/\/)?(?:www\.)?discord(?:app)?\.com\/users\/(\d{17,20})\/?$/i);
    const value = match ? match[1] : text.replace(/^@/, '');
    if (DISCORD_SNOWFLAKE.test(value)) return { ok: true, link: { service, label: value, url: `https://discord.com/users/${value}` } };
    // Discord cannot link to a username, only to a numeric id, so a username is kept as a label.
    if (DISCORD_USERNAME.test(value.toLowerCase()) && !/^\d+$/.test(value)) return { ok: true, link: { service, label: value, url: null } };
    return { ok: false, error: 'Enter a Discord user id (17-20 digits), a profile link, or a username.' };
  }

  if (service === 'web') {
    let url: URL;
    try {
      url = new URL(/^[a-z][a-z0-9+.-]*:/i.test(text) ? text : `https://${text}`);
    } catch {
      return { ok: false, error: 'That is not a valid web address.' };
    }
    if (url.protocol !== 'https:' && url.protocol !== 'http:') return { ok: false, error: 'Only http and https links are allowed.' };
    if (url.username || url.password) return { ok: false, error: 'Links with a username or password are not allowed.' };
    if (!url.hostname.includes('.')) return { ok: false, error: 'That is not a valid web address.' };
    const path = url.pathname === '/' ? '' : url.pathname;
    return { ok: true, link: { service, label: `${url.hostname}${path}`.slice(0, 80), url: url.href } };
  }

  return { ok: false, error: 'Unknown link type.' };
}

function validPhoto(value: unknown): value is string {
  return typeof value === 'string' && value.length <= MAX_PHOTO_CHARS && PHOTO_URL.test(value);
}

function isLink(value: any): value is ContactLink {
  if (!value || !['telegram', 'discord', 'web'].includes(value.service)) return false;
  if (typeof value.label !== 'string') return false;
  if (value.url === null) return true;
  return typeof value.url === 'string' && /^https?:\/\//i.test(value.url);
}

/** Clean one stored record, or null if it is unusable. Stored data is not trusted. */
function sanitize(raw: any): Contact | null {
  if (!raw || typeof raw.id !== 'string' || !raw.id.trim() || typeof raw.name !== 'string' || !raw.name.trim()) return null;
  const seen = new Set<string>();
  const links = (Array.isArray(raw.links) ? raw.links : []).filter(isLink).filter((link: ContactLink) => !seen.has(link.service) && seen.add(link.service));
  const now = Date.now();
  return {
    id: raw.id.trim(),
    name: raw.name.trim().slice(0, 120),
    note: typeof raw.note === 'string' ? raw.note.slice(0, MAX_NOTE_LENGTH) : '',
    photo: validPhoto(raw.photo) ? raw.photo : null,
    links: links.map((link: ContactLink) => ({ service: link.service, label: link.label.slice(0, 80), url: link.url })),
    googleResourceName: typeof raw.googleResourceName === 'string' && /^people\/[A-Za-z0-9_-]+$/.test(raw.googleResourceName) ? raw.googleResourceName : null,
    savedAt: Number.isFinite(raw.savedAt) ? raw.savedAt : now,
    updatedAt: Number.isFinite(raw.updatedAt) ? raw.updatedAt : now,
  };
}

type StorageLike = Pick<Storage, 'getItem' | 'setItem'>;

export class ContactsStore extends Utils.EventEmitter {
  private contacts = new Map<string, Contact>();

  constructor(private storage: StorageLike | null = typeof localStorage !== 'undefined' ? localStorage : null, private now: () => number = () => Date.now()) {
    super();
    this.load();
  }

  private load() {
    this.contacts.clear();
    let parsed: any = null;
    try {
      const text = this.storage?.getItem(CONTACTS_STORAGE_KEY);
      parsed = text ? JSON.parse(text) : null;
    } catch {
      parsed = null; // unreadable storage or corrupt JSON: start empty rather than crash
    }
    const list = Array.isArray(parsed?.contacts) ? parsed.contacts : [];
    for (const raw of list.slice(0, MAX_CONTACTS)) {
      const contact = sanitize(raw);
      if (contact) this.contacts.set(contact.id, contact);
    }
  }

  /** Write to storage. On failure (typically the storage quota) the previous state is restored and the error thrown. */
  private commit(previous: Map<string, Contact>) {
    try {
      this.storage?.setItem(CONTACTS_STORAGE_KEY, JSON.stringify({ version: 1, contacts: [...this.contacts.values()] }));
    } catch {
      this.contacts = previous;
      throw new Error('This device has no room left to save contacts. Remove a photo or a contact and try again.');
    }
    this.emit('contacts_changed', this.list());
  }

  private mutate(change: () => void) {
    const previous = new Map([...this.contacts].map(([id, contact]) => [id, { ...contact, links: [...contact.links] }]));
    change();
    this.commit(previous);
  }

  /** All saved contacts, sorted by name. */
  list(): Contact[] {
    return [...this.contacts.values()].sort((a, b) => a.name.localeCompare(b.name, undefined, { sensitivity: 'base' }));
  }

  get(id: string): Contact | null {
    return this.contacts.get(id) || null;
  }

  has(id: string) {
    return this.contacts.has(id);
  }

  get size() {
    return this.contacts.size;
  }

  /**
   * Save the resident friends list. New friends are added; a friend already saved keeps its
   * note, photo and links, and only has its name refreshed. Contacts whose friendship ended
   * are kept: this is an address book, not a mirror.
   */
  saveFriends(friends: Array<{ id?: string; name?: string }>): { added: number; updated: number; skipped: number; total: number } {
    let added = 0, updated = 0, skipped = 0;
    this.mutate(() => {
      for (const friend of friends || []) {
        const id = typeof friend?.id === 'string' ? friend.id.trim() : '';
        const name = typeof friend?.name === 'string' ? friend.name.trim().slice(0, 120) : '';
        if (!id || !name) { skipped++; continue; }
        const existing = this.contacts.get(id);
        if (existing) {
          if (existing.name !== name) { existing.name = name; existing.updatedAt = this.now(); updated++; }
        } else if (this.contacts.size >= MAX_CONTACTS) {
          skipped++;
        } else {
          const at = this.now();
          this.contacts.set(id, { id, name, note: '', photo: null, links: [], googleResourceName: null, savedAt: at, updatedAt: at });
          added++;
        }
      }
    });
    return { added, updated, skipped, total: this.contacts.size };
  }

  private require(id: string): Contact {
    const contact = this.contacts.get(id);
    if (!contact) throw new Error('That contact is not saved.');
    return contact;
  }

  setNote(id: string, note: string) {
    if (typeof note !== 'string') throw new Error('A note must be text.');
    if (note.length > MAX_NOTE_LENGTH) throw new Error(`Notes can be at most ${MAX_NOTE_LENGTH} characters.`);
    this.mutate(() => { const contact = this.require(id); contact.note = note; contact.updatedAt = this.now(); });
  }

  /** Set (or with null, clear) the contact's photo. Must be a small image data URL. */
  setPhoto(id: string, photo: string | null) {
    if (photo !== null && !validPhoto(photo)) throw new Error('That photo is not a supported image, or it is too large.');
    this.mutate(() => { const contact = this.require(id); contact.photo = photo; contact.updatedAt = this.now(); });
  }

  /** Add or replace the link for `service`. Returns the validation result; nothing is saved if it failed. */
  setLink(id: string, service: LinkService, input: string): LinkResult {
    const result = normalizeLink(service, input);
    if (!result.ok) return result;
    this.mutate(() => {
      const contact = this.require(id);
      contact.links = [...contact.links.filter((link) => link.service !== service), result.link];
      contact.updatedAt = this.now();
    });
    return result;
  }

  removeLink(id: string, service: LinkService) {
    this.mutate(() => { const contact = this.require(id); contact.links = contact.links.filter((link) => link.service !== service); contact.updatedAt = this.now(); });
  }

  setGoogleResource(id: string, resourceName: string | null) {
    if (resourceName !== null && !/^people\/[A-Za-z0-9_-]+$/.test(resourceName)) throw new Error('That is not a Google contact id.');
    this.mutate(() => { this.require(id).googleResourceName = resourceName; });
  }

  remove(id: string): boolean {
    if (!this.contacts.has(id)) return false;
    this.mutate(() => { this.contacts.delete(id); });
    return true;
  }

  /** A JSON backup of every contact, for the user to keep or move to another device. */
  exportJson(): string {
    return JSON.stringify({ version: 1, exportedAt: new Date(this.now()).toISOString(), contacts: this.list() }, null, 2);
  }

  /**
   * Restore a backup. Records are validated one by one; an existing contact is overwritten by
   * the imported one. Returns how many were imported and how many were unusable.
   */
  importJson(text: string): { imported: number; rejected: number } {
    let parsed: any;
    try {
      parsed = JSON.parse(text);
    } catch {
      throw new Error('That file is not a Linkpoint contacts backup.');
    }
    const list = Array.isArray(parsed?.contacts) ? parsed.contacts : null;
    if (!list) throw new Error('That file is not a Linkpoint contacts backup.');
    let imported = 0, rejected = 0;
    this.mutate(() => {
      for (const raw of list) {
        const contact = sanitize(raw);
        if (!contact || (!this.contacts.has(contact.id) && this.contacts.size >= MAX_CONTACTS)) { rejected++; continue; }
        this.contacts.set(contact.id, contact);
        imported++;
      }
    });
    return { imported, rejected };
  }

  /** Forget everything (for example when the user asks to clear local data). */
  clear() {
    this.mutate(() => { this.contacts.clear(); });
  }
}
