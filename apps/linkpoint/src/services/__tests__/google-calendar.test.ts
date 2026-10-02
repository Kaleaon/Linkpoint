import { beforeEach, describe, expect, it, vi } from 'vitest';

const googleFetch = vi.fn();
vi.mock('../googleApi', () => ({ googleFetch: (...args: any[]) => googleFetch(...args) }));

import { addNoticeToCalendar, buildEventBody, deleteCalendarEvent, fetchLinkpointEvents } from '../googleCalendar';

const json = (body: any) => ({ json: async () => body });
const notice = { id: 'notice-1', subject: 'Dance night', message: 'Bring friends', from: 'Officer', groupName: 'Dancers' };
const options = { start: new Date(Date.UTC(2026, 2, 6, 3, 0)), end: new Date(Date.UTC(2026, 2, 6, 4, 0)), timeZone: 'America/Los_Angeles' };

beforeEach(() => googleFetch.mockReset());

describe('buildEventBody', () => {
  it('describes the notice, sets the zone and tags the event with the notice id', () => {
    const body: any = buildEventBody(notice, options);
    expect(body.summary).toBe('Dance night');
    expect(body.description).toBe('Group: Dancers\nSent by: Officer\n\nBring friends');
    expect(body.location).toBe('Second Life (Dancers)');
    expect(body.start).toEqual({ dateTime: '2026-03-06T03:00:00.000Z', timeZone: 'America/Los_Angeles' });
    expect(body.extendedProperties.private).toEqual({ linkpointNotice: '1', linkpointNoticeId: 'notice-1' });
  });

  it('uses the user\'s title and location when given and refuses an impossible time range', () => {
    expect((buildEventBody(notice, { ...options, summary: 'Mine', location: 'Club' }) as any)).toMatchObject({ summary: 'Mine', location: 'Club' });
    expect(() => buildEventBody(notice, { ...options, end: options.start })).toThrow(/after it starts/);
    expect(() => buildEventBody(notice, { ...options, start: new Date(NaN) })).toThrow(/valid start/);
  });
});

describe('addNoticeToCalendar', () => {
  it('looks for an existing event first, then creates one', async () => {
    googleFetch.mockResolvedValueOnce(json({ items: [] })).mockResolvedValueOnce(json({ id: 'evt1', summary: 'Dance night', htmlLink: 'https://www.google.com/calendar/event?eid=abc', start: { dateTime: 'S' }, end: { dateTime: 'E' } }));
    const result = await addNoticeToCalendar(notice, options);
    expect(result).toEqual({ existing: false, event: { id: 'evt1', summary: 'Dance night', htmlLink: 'https://www.google.com/calendar/event?eid=abc', start: 'S', end: 'E' } });
    expect(decodeURIComponent(googleFetch.mock.calls[0][1])).toContain('privateExtendedProperty=linkpointNoticeId=notice-1');
    expect(googleFetch.mock.calls[1][2].method).toBe('POST');
  });

  it('returns the event already made for this notice instead of adding a duplicate', async () => {
    googleFetch.mockResolvedValueOnce(json({ items: [{ id: 'evt0', summary: 'Dance night' }] }));
    const result = await addNoticeToCalendar(notice, options);
    expect(result.existing).toBe(true);
    expect(result.event.id).toBe('evt0');
    expect(googleFetch).toHaveBeenCalledTimes(1);
  });

  it('validates the time before using the network, and distrusts odd ids and links from Google', async () => {
    await expect(addNoticeToCalendar(notice, { ...options, end: options.start })).rejects.toThrow(/after it starts/);
    expect(googleFetch).not.toHaveBeenCalled();
    googleFetch.mockResolvedValueOnce(json({ items: [] })).mockResolvedValueOnce(json({ id: '../evil' }));
    await expect(addNoticeToCalendar(notice, options)).rejects.toThrow(/unexpected event id/);
    googleFetch.mockResolvedValueOnce(json({ items: [{ id: 'ok1', htmlLink: 'javascript:alert(1)' }] }));
    expect((await addNoticeToCalendar(notice, options)).event.htmlLink).toBeNull();
  });
});

describe('fetchLinkpointEvents and deleteCalendarEvent', () => {
  it('lists only events this app made, soonest first', async () => {
    googleFetch.mockResolvedValueOnce(json({ items: [{ id: 'a1', summary: 'One', start: { date: '2026-03-06' } }, { id: '../x' }] }));
    const events = await fetchLinkpointEvents();
    expect(events.map((e) => e.id)).toEqual(['a1']);
    expect(events[0].start).toBe('2026-03-06');
    const url = decodeURIComponent(googleFetch.mock.calls[0][1]);
    expect(url).toContain('privateExtendedProperty=linkpointNotice=1');
    expect(url).toContain('orderBy=startTime');
  });

  it('deletes by a valid id only', async () => {
    googleFetch.mockResolvedValueOnce(json({}));
    await deleteCalendarEvent('evt1');
    expect(googleFetch.mock.calls[0][1]).toBe('https://www.googleapis.com/calendar/v3/calendars/primary/events/evt1');
    await expect(deleteCalendarEvent('a/b')).rejects.toThrow(/not a calendar event id/);
  });
});
