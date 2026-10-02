// @vitest-environment jsdom
import { afterEach, beforeAll, beforeEach, describe, expect, it, vi } from 'vitest';
import { act } from 'react';
import Settings from '../../screens/Settings.jsx';
import { FriendsScreen, NoticesScreen } from '../../screens/LiveScreens.jsx';
import { app } from '../app';
import { buttonByText, click, flush, mountScreen, unmount, type Mounted } from './ui-helpers';

const google = vi.hoisted(() => ({ signOut: vi.fn() }));
vi.mock('../../services/google.ts', () => ({
  loadGoogle: async () => ({ auth: { signOutGoogle: google.signOut, getGoogleToken: () => null, signInWithGoogle: vi.fn() }, contacts: {}, calendar: {} }),
}));

let mounted: Mounted | null = null;
beforeAll(() => { vi.spyOn(console, 'log').mockImplementation(() => undefined); });
beforeEach(() => {
  localStorage.clear();
  app.notices.clear();
  app.contacts.clear();
  app.friends.replaceFriends([]);
  app.preferences.set('integrations', 'google', false);
  google.signOut.mockReset();
});
afterEach(async () => { await unmount(mounted); mounted = null; });

describe('Settings: optional Google integration', () => {
  const toggle = (host: HTMLElement) => host.querySelector('[role="switch"]') as HTMLElement;

  it('is off by default and says what it does', async () => {
    mounted = await mountScreen(Settings);
    expect(toggle(mounted.host).getAttribute('aria-checked')).toBe('false');
    expect(mounted.host.textContent).toContain('Off by default');
    expect(mounted.host.textContent).toContain('without it');
    expect(app.preferences.isGoogleEnabled()).toBe(false);
  });

  it('turns on without signing in, and turning off signs out', async () => {
    mounted = await mountScreen(Settings);
    await click(toggle(mounted.host));
    expect(app.preferences.isGoogleEnabled()).toBe(true);
    expect(toggle(mounted.host).getAttribute('aria-checked')).toBe('true');
    expect(google.signOut).not.toHaveBeenCalled(); // enabling never starts a sign-in

    await click(toggle(mounted.host));
    await flush();
    expect(app.preferences.isGoogleEnabled()).toBe(false);
    expect(google.signOut).toHaveBeenCalledTimes(1);
    expect(mounted.host.textContent).toContain('signed out');
  });

  it('is operable from the keyboard', async () => {
    mounted = await mountScreen(Settings);
    await act(async () => { toggle(mounted!.host).dispatchEvent(new KeyboardEvent('keydown', { key: 'Enter', bubbles: true })); });
    expect(app.preferences.isGoogleEnabled()).toBe(true);
  });
});

describe('screens that switch sub-tabs do not break React\'s hook order', () => {
  it('Notices can move to Calendar and back without crashing', async () => {
    const errors = vi.spyOn(console, 'error').mockImplementation(() => undefined);
    mounted = await mountScreen(NoticesScreen);
    expect(mounted.host.textContent).toContain('No group notices have been received');
    await act(async () => { mounted!.ctx.current.actions.setTab('Notices', 'CALENDAR'); });
    expect(mounted.host.textContent).toContain('No group notices yet');
    await act(async () => { mounted!.ctx.current.actions.setTab('Notices', 'NOTICES'); });
    expect(mounted.host.textContent).toContain('No group notices have been received');
    expect(errors.mock.calls.filter((call) => /hook|Rendered (fewer|more)/i.test(String(call[0])))).toEqual([]);
    errors.mockRestore();
  });

  it('Friends can move to Contacts and back', async () => {
    const errors = vi.spyOn(console, 'error').mockImplementation(() => undefined);
    mounted = await mountScreen(FriendsScreen);
    await act(async () => { mounted!.ctx.current.actions.setTab('Friends', 'CONTACTS'); });
    expect(mounted.host.textContent).toContain('SAVE FRIENDS LIST');
    await act(async () => { mounted!.ctx.current.actions.setTab('Friends', 'ALL'); });
    expect(mounted.host.textContent).toContain('SYNC FRIENDS');
    expect(errors.mock.calls.filter((call) => /hook|Rendered (fewer|more)/i.test(String(call[0])))).toEqual([]);
    errors.mockRestore();
  });
});

describe('Notices list', () => {
  const sent = (over: any = {}) => ({ id: 'n1', groupId: 'g1', fromName: 'Officer', subject: 'Dance night', message: 'Saturday 7pm SLT', timestamp: 1000, ...over });

  it('shows notices the grid sent, expands one, and sends it to the calendar', async () => {
    mounted = await mountScreen(NoticesScreen);
    await act(async () => { app.notices.receive(sent()); });
    expect(mounted.host.textContent).toContain('Dance night');
    expect(mounted.host.textContent).not.toContain('Saturday 7pm SLT'); // collapsed
    await click(mounted.host.querySelector('article button'));
    expect(mounted.host.textContent).toContain('Saturday 7pm SLT');
    await click(buttonByText(mounted.host, 'ADD TO CALENDAR'));
    expect(mounted.ctx.current.state.screen).toBe('Calendar');
    expect(app.notices.takeFocus()).toBe('n1');
  });

  it('deletes a notice', async () => {
    mounted = await mountScreen(NoticesScreen);
    await act(async () => { app.notices.receive(sent()); });
    await click(mounted.host.querySelector('article button'));
    await click(buttonByText(mounted.host, 'DELETE'));
    expect(app.notices.list()).toHaveLength(0);
  });
});

describe('Friends: saving to Contacts', () => {
  it('saves one friend or everyone, and marks saved friends', async () => {
    mounted = await mountScreen(FriendsScreen);
    await act(async () => { app.friends.replaceFriends([{ id: 'f-1', name: 'Pat Resident' }, { id: 'f-2', name: 'Sam Linden' }]); });
    await click([...mounted.host.querySelectorAll('button')].find((b) => (b.textContent || '').trim() === 'SAVE'));
    expect(app.contacts.size).toBe(1);
    expect([...mounted.host.querySelectorAll('button')].some((b) => (b.textContent || '').trim() === 'SAVED')).toBe(true);
    await click(buttonByText(mounted.host, /SAVE TO CONTACTS/));
    expect(app.contacts.size).toBe(2);
  });
});
