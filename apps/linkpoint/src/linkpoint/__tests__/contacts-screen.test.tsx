// @vitest-environment jsdom
import { afterEach, beforeAll, beforeEach, describe, expect, it, vi } from 'vitest';
import { act } from 'react';
import ContactsScreen from '../../screens/ContactsScreen.jsx';
import { app } from '../app';
import { buttonByText, click, flush, mountScreen, typeInto, unmount, type Mounted } from './ui-helpers';

const google = vi.hoisted(() => ({
  signIn: vi.fn(), token: vi.fn(), fetchSl: vi.fn(), push: vi.fn(),
}));
vi.mock('../../services/google.ts', () => ({
  loadGoogle: async () => ({
    auth: { getGoogleToken: google.token, signInWithGoogle: google.signIn, signOutGoogle: vi.fn() },
    contacts: { fetchSlContacts: google.fetchSl, pushContact: google.push },
    calendar: {},
  }),
}));

let mounted: Mounted | null = null;
const friends = [
  { id: 'aaaaaaaa-0000-0000-0000-000000000001', name: 'Pat Resident', onlineStatus: 'online' },
  { id: 'aaaaaaaa-0000-0000-0000-000000000002', name: 'Sam Linden', onlineStatus: 'offline' },
];

beforeAll(() => { vi.spyOn(console, 'log').mockImplementation(() => undefined); });
beforeEach(() => {
  localStorage.clear();
  app.contacts.clear();
  app.friends.replaceFriends([]);
  app.preferences.set('integrations', 'google', false);
  Object.values(google).forEach((fn) => fn.mockReset());
});
afterEach(async () => { await unmount(mounted); mounted = null; vi.restoreAllMocks(); vi.spyOn(console, 'log').mockImplementation(() => undefined); });

const seedFriends = async () => { await act(async () => { app.friends.replaceFriends(friends); }); };
const row = (host: HTMLElement, name: string) => [...host.querySelectorAll('ul[aria-label="Saved contacts"] button')].find((b) => (b.textContent || '').includes(name)) as HTMLElement;
const saveAll = async (host: HTMLElement) => click(buttonByText(host, /SAVE FRIENDS LIST/));
const addButtons = (host: HTMLElement) => [...host.querySelectorAll('button')].filter((b) => (b.textContent || '').trim() === 'ADD');

describe('Contacts screen', () => {
  it('starts empty and honest, with the friends button disabled until friends are loaded', async () => {
    mounted = await mountScreen(ContactsScreen);
    expect(mounted.host.textContent).toContain('No contacts saved yet');
    expect(buttonByText(mounted.host, /SAVE FRIENDS LIST/)!.disabled).toBe(true);
  });

  it('saves the friends list and shows the contacts', async () => {
    mounted = await mountScreen(ContactsScreen);
    await seedFriends();
    await click(buttonByText(mounted.host, /SAVE FRIENDS LIST \(2\)/));
    expect(mounted.host.textContent).toContain('Saved 2 new contacts');
    expect(row(mounted.host, 'Pat Resident')).toBeTruthy();
    expect(row(mounted.host, 'Sam Linden')).toBeTruthy();
    expect(app.contacts.size).toBe(2);
    await saveAll(mounted.host);
    expect(mounted.host.textContent).toContain('already saved');
  });

  it('filters by name and by note', async () => {
    mounted = await mountScreen(ContactsScreen);
    await seedFriends();
    await saveAll(mounted.host);
    await act(async () => { app.contacts.setNote(friends[1].id, 'dance partner'); });
    const search = mounted.host.querySelector('input[type="search"]') as HTMLInputElement;
    await typeInto(search, 'pat');
    expect(row(mounted.host, 'Pat Resident')).toBeTruthy();
    expect(row(mounted.host, 'Sam Linden')).toBeFalsy();
    await typeInto(search, 'dance');
    expect(row(mounted.host, 'Sam Linden')).toBeTruthy();
    await typeInto(search, 'zzz');
    expect(mounted.host.textContent).toContain('No contact matches');
  });

  it('adds a valid Telegram link as a safe external link, and rejects bad input with a reason', async () => {
    mounted = await mountScreen(ContactsScreen);
    await seedFriends();
    await saveAll(mounted.host);
    await click(row(mounted.host, 'Pat Resident'));

    const telegram = mounted.host.querySelector(`#link-telegram-${friends[0].id}`) as HTMLInputElement;
    await typeInto(telegram, 'bad input!');
    await click(addButtons(mounted.host)[0]);
    expect(mounted.host.querySelector('[role="alert"]')?.textContent).toMatch(/Telegram usernames/);
    expect(app.contacts.get(friends[0].id)!.links).toEqual([]);

    await typeInto(telegram, 't.me/pat_user');
    await click(addButtons(mounted.host)[0]);
    const anchor = mounted.host.querySelector('a[href="https://t.me/pat_user"]') as HTMLAnchorElement;
    expect(anchor).toBeTruthy();
    expect(anchor.target).toBe('_blank');
    expect(anchor.rel).toContain('noopener');
    expect(app.contacts.get(friends[0].id)!.links[0].url).toBe('https://t.me/pat_user');
  });

  it('refuses a javascript: website link', async () => {
    mounted = await mountScreen(ContactsScreen);
    await seedFriends();
    await saveAll(mounted.host);
    await click(row(mounted.host, 'Pat Resident'));
    await typeInto(mounted.host.querySelector(`#link-web-${friends[0].id}`) as HTMLInputElement, 'javascript:alert(1)');
    await click(addButtons(mounted.host)[2]);
    expect(mounted.host.querySelector('[role="alert"]')?.textContent).toMatch(/http and https/);
    expect(mounted.host.querySelector('a[href^="javascript"]')).toBeNull();
    expect(app.contacts.get(friends[0].id)!.links).toEqual([]);
  });

  it('saves a note and removes a contact only after confirmation', async () => {
    mounted = await mountScreen(ContactsScreen);
    await seedFriends();
    await saveAll(mounted.host);
    await click(row(mounted.host, 'Pat Resident'));
    await typeInto(mounted.host.querySelector(`#note-${friends[0].id}`) as HTMLTextAreaElement, 'met at the market');
    await click(buttonByText(mounted.host, 'SAVE NOTE'));
    expect(app.contacts.get(friends[0].id)!.note).toBe('met at the market');

    await click(buttonByText(mounted.host, 'REMOVE CONTACT'));
    expect(app.contacts.has(friends[0].id)).toBe(true); // asked first, not removed yet
    await click(buttonByText(mounted.host, 'KEEP'));
    await click(buttonByText(mounted.host, 'REMOVE CONTACT'));
    await click(buttonByText(mounted.host, 'REMOVE'));
    expect(app.contacts.has(friends[0].id)).toBe(false);
  });

  it('refuses an unsupported photo file with a message and changes nothing', async () => {
    mounted = await mountScreen(ContactsScreen);
    await seedFriends();
    await saveAll(mounted.host);
    await click(row(mounted.host, 'Pat Resident'));
    const input = mounted.host.querySelector('input[aria-label="Choose a photo"]') as HTMLInputElement;
    await act(async () => {
      Object.defineProperty(input, 'files', { value: [new File(['<svg/>'], 'x.svg', { type: 'image/svg+xml' })], configurable: true });
      input.dispatchEvent(new Event('change', { bubbles: true }));
    });
    await flush();
    expect(mounted.host.querySelector('[role="alert"]')?.textContent).toMatch(/JPEG, PNG/);
    expect(app.contacts.get(friends[0].id)!.photo).toBeNull();
  });

  it('says so when a resident has no public profile picture', async () => {
    vi.spyOn(app.auth, 'isLoggedIn').mockReturnValue(true);
    const fetchPhoto = vi.spyOn(app.protocol, 'fetchProfilePhoto').mockResolvedValue({ photoBytes: null } as any);
    mounted = await mountScreen(ContactsScreen);
    await seedFriends();
    await saveAll(mounted.host);
    await click(row(mounted.host, 'Pat Resident'));
    await click(buttonByText(mounted.host, 'USE PROFILE PICTURE'));
    expect(fetchPhoto).toHaveBeenCalledWith('Pat Resident');
    expect(mounted.host.textContent).toContain('has no public profile picture');
  });

  it('lists friends who are not saved yet on the ADD FROM SL tab and saves one', async () => {
    mounted = await mountScreen(ContactsScreen);
    await seedFriends();
    await act(async () => { app.contacts.saveFriends([friends[0]]); });
    await act(async () => { mounted!.ctx.current.actions.setTab('Contacts', 'ADD FROM SL'); });
    const list = mounted.host.querySelector('ul[aria-label="Friends not yet saved"]')!;
    expect(list.textContent).toContain('Sam Linden');
    expect(list.textContent).not.toContain('Pat Resident');
    await click(buttonByText(mounted.host, 'SAVE'));
    expect(app.contacts.has(friends[1].id)).toBe(true);
    expect(mounted.host.textContent).toContain('All 2 of your friends are saved');
  });

  it('exports a backup of what is saved', async () => {
    const createObjectURL = vi.fn(() => 'blob:test');
    Object.assign(URL, { createObjectURL, revokeObjectURL: vi.fn() });
    mounted = await mountScreen(ContactsScreen);
    await seedFriends();
    await saveAll(mounted.host);
    await click(buttonByText(mounted.host, 'EXPORT'));
    expect(createObjectURL).toHaveBeenCalledTimes(1);
    const blob = (createObjectURL.mock.calls[0] as unknown as [Blob])[0];
    expect(JSON.parse(await blob.text()).contacts).toHaveLength(2);
    expect(mounted.host.textContent).toContain('keep it private');
  });

  describe('Google (optional)', () => {
    it('is off by default: no Google buttons, and a pointer to Settings', async () => {
      mounted = await mountScreen(ContactsScreen);
      await seedFriends();
      await saveAll(mounted.host);
      expect(buttonByText(mounted.host, 'COPY ALL TO GOOGLE')).toBeUndefined();
      expect(mounted.host.textContent).toContain('Google Contacts is off');
      await click(row(mounted.host, 'Pat Resident'));
      expect(mounted.host.textContent).not.toContain('COPY TO GOOGLE CONTACTS');
      await click(buttonByText(mounted.host, 'Turn it on in Settings'));
      expect(mounted.ctx.current.state.screen).toBe('Settings');
      expect(google.signIn).not.toHaveBeenCalled();
    });

    it('when switched on, signs in once, copies without duplicating, and marks the contact', async () => {
      google.token.mockReturnValue(null);
      google.signIn.mockResolvedValue({ email: 'me@example.com', name: 'Me' });
      google.fetchSl.mockResolvedValue(new Map());
      google.push.mockImplementation(async (contact: any) => ({ resourceName: `people/c${contact.id.slice(-1)}`, created: true, photoUploaded: null }));
      mounted = await mountScreen(ContactsScreen);
      await seedFriends();
      await saveAll(mounted.host);
      await act(async () => { app.preferences.set('integrations', 'google', true); });

      await click(buttonByText(mounted.host, 'COPY ALL TO GOOGLE'));
      await flush();
      expect(google.signIn).toHaveBeenCalledTimes(1);
      expect(google.signIn).toHaveBeenCalledWith('contacts');
      expect(google.push).toHaveBeenCalledTimes(2);
      expect(app.contacts.get(friends[0].id)!.googleResourceName).toBe('people/c1');
      expect(mounted.host.textContent).toContain('2 added, 0 already there');
    });

    it('reports a failed photo upload and a sign-in failure honestly', async () => {
      google.token.mockReturnValue('tok');
      google.fetchSl.mockResolvedValue(new Map());
      google.push.mockResolvedValueOnce({ resourceName: 'people/c1', created: true, photoUploaded: false, photoError: 'too big' });
      mounted = await mountScreen(ContactsScreen);
      await seedFriends();
      await act(async () => { app.contacts.saveFriends([friends[0]]); app.preferences.set('integrations', 'google', true); });
      await click(buttonByText(mounted.host, 'COPY ALL TO GOOGLE'));
      await flush();
      expect(mounted.host.querySelector('[role="alert"]')?.textContent).toMatch(/1 photo could not be uploaded/);

      google.token.mockReturnValue(null);
      google.signIn.mockRejectedValueOnce(new Error('Popup closed'));
      await click(buttonByText(mounted.host, 'COPY ALL TO GOOGLE'));
      await flush();
      expect(mounted.host.querySelector('[role="alert"]')?.textContent).toContain('Popup closed');
    });
  });
});
