// @vitest-environment jsdom
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { MAX_NOTICES, NOTICES_STORAGE_KEY, NoticeStore } from '../notices';
import { Utils } from '../utils';

beforeEach(() => localStorage.clear());
const sent = (over: any = {}) => ({ id: 'n1', groupId: 'g1', fromName: 'Officer', subject: 'Dance Saturday 7pm', message: 'Bring friends', timestamp: 1000, ...over });

describe('NoticeStore', () => {
  it('stores notices the grid sends, newest first, ignoring repeats', () => {
    const protocol = new Utils.EventEmitter();
    const store = new NoticeStore(protocol as any);
    store.init();
    const received = vi.fn();
    store.on('notice_received', received);
    protocol.emit('group_notice', sent());
    protocol.emit('group_notice', sent({ id: 'n2', timestamp: 2000, subject: 'Later' }));
    protocol.emit('group_notice', sent());
    expect(store.list().map((n) => n.id)).toEqual(['n2', 'n1']);
    expect(received).toHaveBeenCalledTimes(2);
    expect(store.get('n1')).toMatchObject({ subject: 'Dance Saturday 7pm', from: 'Officer', groupId: 'g1', calendar: null });
  });

  it('gives a notice with no id its own id, and rejects non-objects', () => {
    const store = new NoticeStore();
    expect(store.receive(null)).toBeNull();
    expect(store.receive('text')).toBeNull();
    const a = store.receive({ subject: 'A' })!;
    const b = store.receive({ subject: 'B' })!;
    expect(a.id).not.toBe(b.id);
    expect(a.from).toBe('Resident');
  });

  it('survives a reload', () => {
    new NoticeStore().receive(sent());
    expect(new NoticeStore().get('n1')!.message).toBe('Bring friends');
  });

  it('remembers what was added to a calendar so it is not added twice', () => {
    const store = new NoticeStore();
    store.receive(sent());
    store.markAdded('n1', { eventId: 'abc123', link: 'https://www.google.com/calendar/event?eid=xyz' });
    expect(store.get('n1')!.calendar).toMatchObject({ eventId: 'abc123', link: 'https://www.google.com/calendar/event?eid=xyz' });
    expect(new NoticeStore().get('n1')!.calendar!.eventId).toBe('abc123');
    store.clearAdded('n1');
    expect(store.get('n1')!.calendar).toBeNull();
    expect(() => store.markAdded('missing', { eventId: null, link: null })).toThrow(/not saved/);
  });

  it('does not trust stored links or ids', () => {
    localStorage.setItem(NOTICES_STORAGE_KEY, JSON.stringify({ notices: [
      { id: 'x', subject: 'S', calendar: { eventId: '../evil', link: 'javascript:alert(1)', addedAt: 5 } },
      { id: '', subject: 'no id' }, null, 7,
    ] }));
    const notice = new NoticeStore().get('x')!;
    expect(notice.calendar).toEqual({ eventId: null, link: null, addedAt: 5 });
  });

  it('keeps at most MAX_NOTICES, dropping the oldest, and can remove or clear', () => {
    const store = new NoticeStore();
    for (let i = 0; i < MAX_NOTICES + 3; i++) store.receive(sent({ id: `n${i}`, timestamp: i }));
    expect(store.list()).toHaveLength(MAX_NOTICES);
    expect(store.get('n0')).toBeNull();
    expect(store.remove('n5')).toBe(true);
    expect(store.remove('n5')).toBe(false);
    store.clear();
    expect(store.list()).toHaveLength(0);
  });

  it('keeps working in memory and says so when storage is full', () => {
    const storage = { getItem: () => null, setItem: () => { throw new DOMException('quota', 'QuotaExceededError'); } };
    const store = new NoticeStore(null, storage);
    const failed = vi.fn();
    store.on('storage_error', failed);
    store.receive(sent());
    expect(failed).toHaveBeenCalled();
    expect(store.get('n1')).not.toBeNull();
  });

  it('hands a requested notice to the calendar once, and ignores an unknown one', () => {
    const store = new NoticeStore();
    store.receive(sent());
    const changed = vi.fn();
    store.on('focus_changed', changed);
    store.focus('n1');
    expect(changed).toHaveBeenCalledWith('n1');
    expect(store.takeFocus()).toBe('n1');
    expect(store.takeFocus()).toBeNull();
    store.focus('missing');
    expect(store.takeFocus()).toBeNull();
  });
});
