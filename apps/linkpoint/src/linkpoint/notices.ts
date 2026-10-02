/**
 * Group notices received from the grid, kept on this device so they can be
 * turned into calendar events later. Second Life delivers a notice once; without
 * this store it is gone when the session ends.
 *
 * Only notices the grid sent are stored. The record of what has been added to a
 * calendar lives with the notice so the same notice is not added twice.
 */

import { Utils } from './utils';
import type { SLConnectionFull } from './sl-connection-full';

export const NOTICES_STORAGE_KEY = 'linkpoint.notices.v1';
export const MAX_NOTICES = 200;

export interface NoticeCalendarEntry {
  /** Google Calendar event id, or null when it was saved as a file only. */
  eventId: string | null;
  link: string | null;
  addedAt: number;
}

export interface SavedNotice {
  id: string;
  groupId: string | null;
  subject: string;
  message: string;
  from: string;
  /** When the notice arrived (ms). */
  timestamp: number;
  calendar: NoticeCalendarEntry | null;
}

type StorageLike = Pick<Storage, 'getItem' | 'setItem'>;

const text = (value: unknown, max: number, fallback = '') => (typeof value === 'string' ? value.slice(0, max) : fallback);

function sanitize(raw: any): SavedNotice | null {
  if (!raw || typeof raw.id !== 'string' || !raw.id.trim()) return null;
  const calendar = raw.calendar && Number.isFinite(raw.calendar.addedAt)
    ? {
        eventId: typeof raw.calendar.eventId === 'string' && /^[A-Za-z0-9_-]{1,1024}$/.test(raw.calendar.eventId) ? raw.calendar.eventId : null,
        link: typeof raw.calendar.link === 'string' && /^https:\/\/(?:www\.)?google\.com\/calendar\//i.test(raw.calendar.link) ? raw.calendar.link : null,
        addedAt: raw.calendar.addedAt,
      }
    : null;
  return {
    id: raw.id.trim().slice(0, 100),
    groupId: typeof raw.groupId === 'string' && raw.groupId ? raw.groupId.slice(0, 100) : null,
    subject: text(raw.subject, 300, 'Group Notice'),
    message: text(raw.message, 5000),
    from: text(raw.from, 120, 'Resident'),
    timestamp: Number.isFinite(raw.timestamp) ? raw.timestamp : Date.now(),
    calendar,
  };
}

export class NoticeStore extends Utils.EventEmitter {
  private notices = new Map<string, SavedNotice>();
  private counter = 0;
  /** A notice another screen asked the calendar to open; read once with `takeFocus`. */
  private focusId: string | null = null;

  constructor(private protocol: SLConnectionFull | null = null, private storage: StorageLike | null = typeof localStorage !== 'undefined' ? localStorage : null) {
    super();
    this.load();
  }

  /** Start listening for notices from the grid. */
  init() {
    this.protocol?.on('group_notice', (data: any) => this.receive(data));
  }

  private load() {
    let parsed: any = null;
    try {
      const raw = this.storage?.getItem(NOTICES_STORAGE_KEY);
      parsed = raw ? JSON.parse(raw) : null;
    } catch {
      parsed = null;
    }
    for (const raw of (Array.isArray(parsed?.notices) ? parsed.notices : []).slice(-MAX_NOTICES)) {
      const notice = sanitize(raw);
      if (notice) this.notices.set(notice.id, notice);
    }
  }

  /** Persist. Storage can be full or blocked; the notice stays available this session either way. */
  private save() {
    try {
      this.storage?.setItem(NOTICES_STORAGE_KEY, JSON.stringify({ version: 1, notices: [...this.notices.values()] }));
    } catch {
      this.emit('storage_error', 'Notices could not be saved on this device.');
    }
    this.emit('notices_changed', this.list());
  }

  /** Ask the calendar screen to open this notice next. */
  focus(id: string) {
    this.focusId = this.notices.has(id) ? id : null;
    this.emit('focus_changed', this.focusId);
  }

  /** The notice the calendar was asked to open, once. */
  takeFocus(): string | null {
    const id = this.focusId;
    this.focusId = null;
    return id && this.notices.has(id) ? id : null;
  }

  /** Newest first. */
  list(): SavedNotice[] {
    return [...this.notices.values()].sort((a, b) => b.timestamp - a.timestamp);
  }

  get(id: string): SavedNotice | null {
    return this.notices.get(id) || null;
  }

  /** Store a notice the grid sent. A repeat of the same id is ignored. */
  receive(data: any): SavedNotice | null {
    if (!data || typeof data !== 'object') return null;
    const id = typeof data.id === 'string' && data.id ? data.id : `notice-${Date.now()}-${++this.counter}`;
    if (this.notices.has(id)) return null;
    const notice = sanitize({
      id,
      groupId: data.groupId,
      subject: data.subject,
      message: data.message,
      from: data.fromName || data.from,
      timestamp: data.timestamp,
      calendar: null,
    });
    if (!notice) return null;
    this.notices.set(notice.id, notice);
    while (this.notices.size > MAX_NOTICES) this.notices.delete(this.notices.keys().next().value as string);
    this.save();
    this.emit('notice_received', notice);
    return notice;
  }

  /** Record that a notice was added to a calendar (`eventId` null for a downloaded file). */
  markAdded(id: string, entry: { eventId: string | null; link: string | null }) {
    const notice = this.notices.get(id);
    if (!notice) throw new Error('That notice is not saved.');
    const next = sanitize({ ...notice, calendar: { ...entry, addedAt: Date.now() } });
    if (next) this.notices.set(id, next);
    this.save();
  }

  clearAdded(id: string) {
    const notice = this.notices.get(id);
    if (!notice) return;
    notice.calendar = null;
    this.save();
  }

  remove(id: string): boolean {
    if (!this.notices.delete(id)) return false;
    this.save();
    return true;
  }

  clear() {
    this.notices.clear();
    this.save();
  }
}
