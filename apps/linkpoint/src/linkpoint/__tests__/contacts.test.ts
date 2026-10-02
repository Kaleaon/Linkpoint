// @vitest-environment jsdom
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { CONTACTS_STORAGE_KEY, ContactsStore, MAX_CONTACTS, MAX_NOTE_LENGTH, MAX_PHOTO_CHARS, normalizeLink } from '../contacts';

const PHOTO = 'data:image/jpeg;base64,/9j/4AAQSkZJRg==';
const friends = [{ id: 'a-1', name: 'Pat Resident' }, { id: 'b-2', name: 'Sam Linden' }];

beforeEach(() => localStorage.clear());

describe('normalizeLink', () => {
  it('accepts Telegram usernames in every common form', () => {
    for (const input of ['@pat_user', 'pat_user', 't.me/pat_user', 'https://t.me/pat_user', 'https://telegram.me/pat_user/', 'https://t.me/pat_user?start=1']) {
      expect(normalizeLink('telegram', input)).toEqual({ ok: true, link: { service: 'telegram', label: '@pat_user', url: 'https://t.me/pat_user' } });
    }
  });

  it('rejects malformed Telegram input', () => {
    for (const input of ['', '   ', 'ab', '9starts', 'has space', 'a'.repeat(33), 'https://evil.example/pat_user', 't.me/', '@@pat_user']) {
      expect(normalizeLink('telegram', input).ok).toBe(false);
    }
  });

  it('links a Discord id or profile URL, and keeps a username as a plain label', () => {
    expect(normalizeLink('discord', '123456789012345678')).toEqual({ ok: true, link: { service: 'discord', label: '123456789012345678', url: 'https://discord.com/users/123456789012345678' } });
    expect(normalizeLink('discord', 'https://discord.com/users/123456789012345678/')).toMatchObject({ ok: true, link: { url: 'https://discord.com/users/123456789012345678' } });
    expect(normalizeLink('discord', '@pat.user')).toEqual({ ok: true, link: { service: 'discord', label: 'pat.user', url: null } });
  });

  it('rejects malformed Discord input, including short numbers and other hosts', () => {
    for (const input of ['', 'x', '12345', 'Has Space', 'https://discord.com.evil.example/users/123456789012345678', 'a'.repeat(40)]) {
      expect(normalizeLink('discord', input).ok).toBe(false);
    }
  });

  it('allows only plain http(s) web links and refuses script, data and credential URLs', () => {
    expect(normalizeLink('web', 'example.com/pat')).toMatchObject({ ok: true, link: { url: 'https://example.com/pat', label: 'example.com/pat' } });
    expect(normalizeLink('web', 'http://example.com')).toMatchObject({ ok: true, link: { url: 'http://example.com/' } });
    for (const input of ['javascript:alert(1)', 'data:text/html,<script>', 'ftp://example.com', 'file:///etc/passwd', 'https://user:pw@example.com', 'localhost', 'not a url', '']) {
      expect(normalizeLink('web', input).ok).toBe(false);
    }
  });

  it('rejects an unknown service', () => {
    expect(normalizeLink('myspace' as any, 'x').ok).toBe(false);
  });
});

describe('saving the friends list', () => {
  it('adds friends as contacts and reports what happened', () => {
    const store = new ContactsStore(localStorage, () => 1000);
    expect(store.saveFriends(friends)).toEqual({ added: 2, updated: 0, skipped: 0, total: 2 });
    expect(store.list().map((c) => c.name)).toEqual(['Pat Resident', 'Sam Linden']);
    expect(store.get('a-1')).toMatchObject({ note: '', photo: null, links: [], googleResourceName: null, savedAt: 1000 });
  });

  it('keeps a contact\'s note, photo and links when the friends list is saved again', () => {
    const store = new ContactsStore();
    store.saveFriends(friends);
    store.setNote('a-1', 'Met at the market');
    store.setPhoto('a-1', PHOTO);
    store.setLink('a-1', 'telegram', '@pat_user');
    const result = store.saveFriends([{ id: 'a-1', name: 'Pat Renamed' }, ...friends.slice(1)]);
    expect(result).toMatchObject({ added: 0, updated: 1 });
    expect(store.get('a-1')).toMatchObject({ name: 'Pat Renamed', note: 'Met at the market', photo: PHOTO });
    expect(store.get('a-1')!.links).toHaveLength(1);
  });

  it('keeps contacts whose friendship ended, and skips entries without an id or name', () => {
    const store = new ContactsStore();
    store.saveFriends(friends);
    expect(store.saveFriends([{ id: '', name: 'No Id' }, { id: 'x', name: '  ' }, {}, null as any])).toMatchObject({ added: 0, skipped: 4, total: 2 });
  });

  it('stops at the contact limit', () => {
    const store = new ContactsStore();
    const many = Array.from({ length: MAX_CONTACTS + 5 }, (_, i) => ({ id: `id-${i}`, name: `Friend ${i}` }));
    const result = store.saveFriends(many);
    expect(result).toMatchObject({ added: MAX_CONTACTS, skipped: 5, total: MAX_CONTACTS });
  });
});

describe('editing a contact', () => {
  const make = () => { const store = new ContactsStore(); store.saveFriends(friends); return store; };

  it('sets and limits notes', () => {
    const store = make();
    store.setNote('a-1', 'hello');
    expect(store.get('a-1')!.note).toBe('hello');
    expect(() => store.setNote('a-1', 'x'.repeat(MAX_NOTE_LENGTH + 1))).toThrow(/at most/);
    expect(() => store.setNote('nope', 'x')).toThrow(/not saved/);
  });

  it('accepts a small image data URL and refuses anything else as a photo', () => {
    const store = make();
    store.setPhoto('a-1', PHOTO);
    expect(store.get('a-1')!.photo).toBe(PHOTO);
    for (const bad of ['http://example.com/x.jpg', 'data:text/html;base64,AAAA', 'data:image/svg+xml;base64,AAAA', 'javascript:alert(1)', '', `data:image/png;base64,${'A'.repeat(MAX_PHOTO_CHARS)}`]) {
      expect(() => store.setPhoto('a-1', bad)).toThrow(/photo/);
    }
    store.setPhoto('a-1', null);
    expect(store.get('a-1')!.photo).toBeNull();
  });

  it('keeps one link per service, replacing the old one, and saves nothing for an invalid link', () => {
    const store = make();
    store.setLink('a-1', 'telegram', '@first_name');
    const result = store.setLink('a-1', 'telegram', '@second_name');
    expect(result.ok).toBe(true);
    expect(store.get('a-1')!.links).toEqual([{ service: 'telegram', label: '@second_name', url: 'https://t.me/second_name' }]);
    expect(store.setLink('a-1', 'discord', 'bad input!').ok).toBe(false);
    expect(store.get('a-1')!.links).toHaveLength(1);
    store.removeLink('a-1', 'telegram');
    expect(store.get('a-1')!.links).toEqual([]);
  });

  it('removes contacts and records a Google resource only if it looks like one', () => {
    const store = make();
    store.setGoogleResource('a-1', 'people/c123456');
    expect(store.get('a-1')!.googleResourceName).toBe('people/c123456');
    expect(() => store.setGoogleResource('a-1', '../evil')).toThrow(/Google contact/);
    expect(store.remove('a-1')).toBe(true);
    expect(store.remove('a-1')).toBe(false);
    expect(store.size).toBe(1);
  });
});

describe('persistence', () => {
  it('survives a reload and tells listeners about every change', () => {
    const store = new ContactsStore();
    const changed = vi.fn();
    store.on('contacts_changed', changed);
    store.saveFriends(friends);
    store.setNote('a-1', 'remember');
    expect(changed).toHaveBeenCalledTimes(2);
    expect(new ContactsStore().get('a-1')!.note).toBe('remember');
  });

  it('starts empty on corrupt storage, and drops malformed or hostile records', () => {
    localStorage.setItem(CONTACTS_STORAGE_KEY, '{not json');
    expect(new ContactsStore().size).toBe(0);
    localStorage.setItem(CONTACTS_STORAGE_KEY, JSON.stringify({ version: 1, contacts: [
      { id: 'ok', name: 'Fine', links: [{ service: 'web', label: 'x', url: 'javascript:alert(1)' }, { service: 'telegram', label: '@fine_user', url: 'https://t.me/fine_user' }, { service: 'telegram', label: '@dupe_user', url: 'https://t.me/dupe_user' }], photo: 'http://evil.example/p.jpg', googleResourceName: 'people/../x' },
      { id: '', name: 'No id' }, { id: 'noname' }, null, 42,
    ] }));
    const store = new ContactsStore();
    expect(store.size).toBe(1);
    expect(store.get('ok')).toMatchObject({ photo: null, googleResourceName: null });
    expect(store.get('ok')!.links).toEqual([{ service: 'telegram', label: '@fine_user', url: 'https://t.me/fine_user' }]);
  });

  it('rolls back and explains when storage is full', () => {
    const real = localStorage;
    let full = false;
    const storage = { getItem: (key: string) => real.getItem(key), setItem: (key: string, value: string) => { if (full) throw new DOMException('quota', 'QuotaExceededError'); real.setItem(key, value); } };
    const store = new ContactsStore(storage);
    store.saveFriends(friends);
    full = true;
    expect(() => store.setPhoto('a-1', PHOTO)).toThrow(/no room left/);
    expect(store.get('a-1')!.photo).toBeNull();
    expect(() => store.saveFriends([{ id: 'c-3', name: 'New Person' }])).toThrow(/no room left/);
    expect(store.has('c-3')).toBe(false);
  });

  it('works with no storage at all', () => {
    const store = new ContactsStore(null);
    store.saveFriends(friends);
    expect(store.size).toBe(2);
  });
});

describe('backup and restore', () => {
  it('round-trips through JSON and tolerates bad records', () => {
    const source = new ContactsStore();
    source.saveFriends(friends);
    source.setNote('a-1', 'hi');
    source.setLink('a-1', 'discord', '123456789012345678');
    const backup = source.exportJson();

    localStorage.clear();
    const target = new ContactsStore();
    expect(target.importJson(backup)).toEqual({ imported: 2, rejected: 0 });
    expect(target.get('a-1')).toMatchObject({ note: 'hi' });
    expect(target.get('a-1')!.links[0].url).toBe('https://discord.com/users/123456789012345678');

    const mixed = JSON.stringify({ contacts: [{ id: 'z', name: 'Zed' }, { id: '', name: 'Bad' }] });
    expect(target.importJson(mixed)).toEqual({ imported: 1, rejected: 1 });
  });

  it('refuses a file that is not a backup, leaving contacts untouched', () => {
    const store = new ContactsStore();
    store.saveFriends(friends);
    for (const text of ['nonsense', '{}', '[]', '{"contacts":"x"}']) expect(() => store.importJson(text)).toThrow(/not a Linkpoint contacts backup/);
    expect(store.size).toBe(2);
  });
});
