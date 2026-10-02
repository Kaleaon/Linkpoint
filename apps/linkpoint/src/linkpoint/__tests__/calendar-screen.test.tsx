// @vitest-environment jsdom
import { afterEach, beforeAll, beforeEach, describe, expect, it, vi } from 'vitest';
import { act } from 'react';
import CalendarScreen from '../../screens/CalendarScreen.jsx';
import { app } from '../app';
import { buttonByText, click, flush, mountScreen, typeInto, unmount, type Mounted } from './ui-helpers';

const google = vi.hoisted(() => ({ signIn: vi.fn(), token: vi.fn(), add: vi.fn(), list: vi.fn(), del: vi.fn() }));
vi.mock('../../services/google.ts', () => ({
  loadGoogle: async () => ({
    auth: { getGoogleToken: google.token, signInWithGoogle: google.signIn, signOutGoogle: vi.fn() },
    contacts: {},
    calendar: { addNoticeToCalendar: google.add, fetchLinkpointEvents: google.list, deleteCalendarEvent: google.del },
  }),
}));

let mounted: Mounted | null = null;
// Wednesday 4 March 2026, 12:00 UTC (4 am in Los Angeles).
const RECEIVED = Date.UTC(2026, 2, 4, 12, 0);
const notice = (over: any = {}) => ({ id: 'n1', groupId: 'g1', fromName: 'Officer', subject: 'Dance night', message: 'Join us Saturday 7pm SLT at the club.', timestamp: RECEIVED, ...over });

beforeAll(() => { app.notices.init(); });
beforeEach(() => {
  localStorage.clear();
  app.notices.clear();
  app.preferences.set('integrations', 'google', false);
  Object.values(google).forEach((fn) => fn.mockReset());
  Object.assign(URL, { createObjectURL: vi.fn(() => 'blob:test'), revokeObjectURL: vi.fn() });
});
afterEach(async () => { await unmount(mounted); mounted = null; });

const receive = async (data: any = notice()) => { await act(async () => { (app.protocol as any).emit('group_notice', data); }); };
const open = async (host: HTMLElement, subject = 'Dance night') => click([...host.querySelectorAll('ul[aria-label="Group notices"] button')].find((b) => (b.textContent || '').includes(subject)));
const input = (host: HTMLElement, id: string) => host.querySelector(`#${id}`) as HTMLInputElement;

describe('Calendar screen: group notices', () => {
  it('is honest when no notice has arrived', async () => {
    mounted = await mountScreen(CalendarScreen);
    expect(mounted.host.textContent).toContain('No group notices yet');
  });

  it('shows a notice the grid sent and reads its time in Second Life Time', async () => {
    mounted = await mountScreen(CalendarScreen);
    await receive();
    await open(mounted.host);
    expect(mounted.host.textContent).toContain('Found a time in the notice');
    expect(mounted.host.textContent).not.toContain('assumed'); // it named SLT
    expect(input(mounted.host, 'cal-start').value).toBe('2026-03-07T19:00'); // Saturday 7pm
    expect(input(mounted.host, 'cal-end').value).toBe('2026-03-07T20:00');
    expect((input(mounted.host, 'cal-zone') as any).value).toBe('America/Los_Angeles');
    expect(input(mounted.host, 'cal-title').value).toBe('Dance night');
  });

  it('says when no time was found and when the zone was assumed', async () => {
    mounted = await mountScreen(CalendarScreen);
    await receive(notice({ id: 'n2', subject: 'Rules update', message: 'Please read the new rules for room 7.' }));
    await receive(notice({ id: 'n3', subject: 'Market day', message: 'Saturday 2pm', timestamp: RECEIVED + 1 }));
    await open(mounted.host, 'Rules update');
    expect(mounted.host.textContent).toContain('No time was found in this notice');
    await open(mounted.host, 'Rules update'); // collapse
    await open(mounted.host, 'Market day');
    expect(mounted.host.textContent).toContain('Second Life Time is assumed');
  });

  it('keeps the same moment when the time zone is changed', async () => {
    mounted = await mountScreen(CalendarScreen);
    await receive();
    await open(mounted.host);
    await typeInto(mounted.host.querySelector('#cal-zone') as any, 'UTC');
    expect(input(mounted.host, 'cal-start').value).toBe('2026-03-08T03:00'); // 19:00 PST is 03:00 UTC next day
  });

  it('blocks saving when the end is not after the start', async () => {
    mounted = await mountScreen(CalendarScreen);
    await receive();
    await open(mounted.host);
    await typeInto(input(mounted.host, 'cal-end'), '2026-03-07T18:00');
    expect(mounted.host.querySelector('[role="alert"]')?.textContent).toContain('must end after it starts');
    expect(buttonByText(mounted.host, 'SAVE CALENDAR FILE (.ICS)')!.disabled).toBe(true);
    await typeInto(input(mounted.host, 'cal-start'), '');
    expect(mounted.host.querySelector('[role="alert"]')?.textContent).toContain('valid start');
  });

  it('saves a calendar file with the right event and records it, with Google off', async () => {
    mounted = await mountScreen(CalendarScreen);
    await receive();
    await open(mounted.host);
    expect(buttonByText(mounted.host, 'ADD TO GOOGLE CALENDAR')).toBeUndefined();
    expect(mounted.host.textContent).toContain('Google Calendar is off');

    await click(buttonByText(mounted.host, 'SAVE CALENDAR FILE (.ICS)'));
    const blob = ((URL.createObjectURL as any).mock.calls[0] as [Blob])[0];
    const text = await blob.text();
    expect(text).toContain('SUMMARY:Dance night');
    expect(text).toContain('DTSTART:20260308T030000Z');
    expect(text).toContain('DTEND:20260308T040000Z');
    expect(text).toContain('Sent by: Officer');
    expect(app.notices.get('n1')!.calendar).toMatchObject({ eventId: null });
    expect(mounted.host.textContent).toContain('SAVED AS FILE');
  });

  it('opens the notice another screen asked for', async () => {
    await act(async () => { app.notices.receive(notice()); });
    app.notices.focus('n1');
    mounted = await mountScreen(CalendarScreen);
    expect(mounted.host.querySelector('section[aria-label="Add notice to calendar"]')).not.toBeNull();
  });

  describe('Google (optional)', () => {
    it('adds to Google Calendar with the chosen zone, remembers the event, and links to it', async () => {
      google.token.mockReturnValue(null);
      google.signIn.mockResolvedValue({ email: 'me@example.com', name: 'Me' });
      google.add.mockResolvedValue({ existing: false, event: { id: 'evt1', summary: 'Dance night', htmlLink: 'https://www.google.com/calendar/event?eid=abc', start: 'S', end: 'E' } });
      mounted = await mountScreen(CalendarScreen);
      await receive();
      await act(async () => { app.preferences.set('integrations', 'google', true); });
      await open(mounted.host);

      await click(buttonByText(mounted.host, 'ADD TO GOOGLE CALENDAR'));
      await flush();
      expect(google.signIn).toHaveBeenCalledWith('calendar');
      const [forNotice, options] = google.add.mock.calls[0];
      expect(forNotice).toMatchObject({ id: 'n1', subject: 'Dance night', from: 'Officer' });
      expect(options.timeZone).toBe('America/Los_Angeles');
      expect(options.start.toISOString()).toBe('2026-03-08T03:00:00.000Z');
      expect(options.end.getTime() - options.start.getTime()).toBe(3_600_000);
      expect(app.notices.get('n1')!.calendar).toMatchObject({ eventId: 'evt1', link: 'https://www.google.com/calendar/event?eid=abc' });
      expect(mounted.host.textContent).toContain('Added to your Google Calendar');
      expect((mounted.host.querySelector('a[href^="https://www.google.com/calendar"]') as HTMLAnchorElement).rel).toContain('noopener');
    });

    it('says when the notice was already in the calendar, and shows Google\'s error otherwise', async () => {
      google.token.mockReturnValue('tok');
      google.add.mockResolvedValueOnce({ existing: true, event: { id: 'evt1', summary: 'x', htmlLink: null, start: null, end: null } }).mockRejectedValueOnce(new Error('Google refused: quota'));
      mounted = await mountScreen(CalendarScreen);
      await receive();
      await act(async () => { app.preferences.set('integrations', 'google', true); });
      await open(mounted.host);
      await click(buttonByText(mounted.host, 'ADD TO GOOGLE CALENDAR'));
      await flush();
      expect(mounted.host.textContent).toContain('already in your Google Calendar');
      await click(buttonByText(mounted.host, 'ADD TO GOOGLE CALENDAR'));
      await flush();
      expect(mounted.host.querySelector('[role="alert"]')?.textContent).toContain('Google refused: quota');
    });
  });
});

describe('Calendar screen: Google events tab', () => {
  it('explains that Google is off, without touching Google', async () => {
    mounted = await mountScreen(CalendarScreen);
    await act(async () => { mounted!.ctx.current.actions.setTab('Calendar', 'GOOGLE CALENDAR'); });
    expect(mounted.host.textContent).toContain('Google Calendar is off');
    expect(google.signIn).not.toHaveBeenCalled();
  });

  it('when on, loads upcoming events on request and deletes one, clearing the notice record', async () => {
    google.token.mockReturnValue('tok');
    google.list.mockResolvedValue([{ id: 'evt1', summary: 'Dance night', htmlLink: 'https://www.google.com/calendar/event?eid=abc', start: '2026-03-08T03:00:00Z', end: null }]);
    google.del.mockResolvedValue(undefined);
    await act(async () => { app.notices.receive(notice()); app.notices.markAdded('n1', { eventId: 'evt1', link: null }); app.preferences.set('integrations', 'google', true); });
    mounted = await mountScreen(CalendarScreen);
    await act(async () => { mounted!.ctx.current.actions.setTab('Calendar', 'GOOGLE CALENDAR'); });
    expect(google.list).not.toHaveBeenCalled(); // nothing is fetched until the user asks
    await click(buttonByText(mounted.host, 'SIGN IN AND LOAD'));
    await flush();
    expect(mounted.host.textContent).toContain('Dance night');
    await click(buttonByText(mounted.host, 'DELETE'));
    await flush();
    expect(google.del).toHaveBeenCalledWith('evt1');
    expect(app.notices.get('n1')!.calendar).toBeNull();
    expect(mounted.host.textContent).toContain('No upcoming events were added by Linkpoint');
  });
});
