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
export const GROUP_NOTICES_CACHE_KEY = 'linkpoint.group_notices_cache.v1';
export const MAX_NOTICES = 200;
export const NOTICE_TTL_MS = 5 * 60 * 1000; // 5 minutes TTL

export interface NoticeCalendarEntry {
  /** Google Calendar event id, or null when it was saved as a file only. */
  eventId: string | null;
  link: string | null;
  addedAt: number;
}

export interface NoticeAttachmentEntry {
  hasAttachment: boolean;
  attachmentName: string | null;
  attachmentItemId: string | null;
  attachmentType: number | null;
  attachmentOwnerId: string | null;
  savedToInventoryAt: number | null;
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
  attachment?: NoticeAttachmentEntry | null;
  hasAttachment?: boolean;
}

export interface GroupNoticeCacheEntry {
  groupId: string;
  notices: SavedNotice[];
  timestamp: number;
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
  const attachment = raw.attachment && typeof raw.attachment === 'object'
    ? {
        hasAttachment: Boolean(raw.attachment.hasAttachment),
        attachmentName: typeof raw.attachment.attachmentName === 'string' ? raw.attachment.attachmentName : null,
        attachmentItemId: typeof raw.attachment.attachmentItemId === 'string' ? raw.attachment.attachmentItemId : null,
        attachmentType: typeof raw.attachment.attachmentType === 'number' ? raw.attachment.attachmentType : null,
        attachmentOwnerId: typeof raw.attachment.attachmentOwnerId === 'string' ? raw.attachment.attachmentOwnerId : null,
        savedToInventoryAt: typeof raw.attachment.savedToInventoryAt === 'number' ? raw.attachment.savedToInventoryAt : null,
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
    attachment,
    hasAttachment: Boolean(raw.hasAttachment || attachment?.hasAttachment),
  };
}

export class NoticeStore extends Utils.EventEmitter {
  private notices = new Map<string, SavedNotice>();
  private groupCaches = new Map<string, GroupNoticeCacheEntry>();
  private historyUnavailableGroups = new Set<string>();
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

    try {
      const rawGroupCache = this.storage?.getItem(GROUP_NOTICES_CACHE_KEY);
      if (rawGroupCache) {
        const parsedGroupCache = JSON.parse(rawGroupCache);
        if (parsedGroupCache && typeof parsedGroupCache === 'object') {
          for (const [gid, entry] of Object.entries(parsedGroupCache)) {
            if (entry && typeof entry === 'object' && Array.isArray((entry as any).notices)) {
              const cleanNotices = (entry as any).notices.map(sanitize).filter(Boolean) as SavedNotice[];
              this.groupCaches.set(gid, {
                groupId: gid,
                notices: cleanNotices,
                timestamp: Number((entry as any).timestamp) || Date.now(),
              });
            }
          }
        }
      }
    } catch {
      // ignore storage parsing error
    }
  }

  /** Persist. Storage can be full or blocked; the notice stays available this session either way. */
  private save() {
    try {
      this.storage?.setItem(NOTICES_STORAGE_KEY, JSON.stringify({ version: 1, notices: [...this.notices.values()] }));
    } catch {
      this.emit('storage_error', 'Notices could not be saved on this device.');
    }
    this.saveGroupCaches();
    this.emit('notices_changed', this.list());
  }

  private saveGroupCaches() {
    try {
      const obj: Record<string, any> = {};
      for (const [gid, entry] of this.groupCaches.entries()) {
        obj[gid] = entry;
      }
      this.storage?.setItem(GROUP_NOTICES_CACHE_KEY, JSON.stringify(obj));
    } catch {
      // ignore storage error
    }
  }

  /** Retrieve cached notices for a group along with TTL validity (default 5 min). */
  getGroupCache(groupId: string, ttlMs: number = NOTICE_TTL_MS): { valid: boolean; notices: SavedNotice[]; timestamp: number } | null {
    const entry = this.groupCaches.get(groupId);
    if (!entry) return null;
    const now = Date.now();
    const valid = (now - entry.timestamp) < ttlMs;
    return {
      valid,
      notices: entry.notices,
      timestamp: entry.timestamp,
    };
  }

  /** Store group notices in the 5-minute TTL cache and persist to localStorage. */
  setGroupCache(groupId: string, notices: SavedNotice[], timestamp: number = Date.now()): void {
    const cleanNotices = notices.map(sanitize).filter(Boolean) as SavedNotice[];
    const entry: GroupNoticeCacheEntry = {
      groupId,
      notices: cleanNotices,
      timestamp,
    };
    this.groupCaches.set(groupId, entry);
    for (const notice of cleanNotices) {
      if (notice && notice.id && !this.notices.has(notice.id)) {
        this.notices.set(notice.id, notice);
      }
    }
    this.save();
    this.emit('group_cache_updated', { groupId, entry });
  }

  isGroupCacheValid(groupId: string, ttlMs: number = NOTICE_TTL_MS): boolean {
    const cache = this.getGroupCache(groupId, ttlMs);
    return Boolean(cache && cache.valid);
  }

  /** Set whether notice history is unavailable for a group (e.g. after dual-transport failure). */
  setHistoryUnavailable(groupId: string, unavailable: boolean): void {
    if (unavailable) {
      this.historyUnavailableGroups.add(groupId);
    } else {
      this.historyUnavailableGroups.delete(groupId);
    }
    this.emit('notice_history_status', { groupId, unavailable, isAnyUnavailable: this.isHistoryUnavailable() });
  }

  isHistoryUnavailable(groupId?: string): boolean {
    if (groupId) {
      return this.historyUnavailableGroups.has(groupId);
    }
    return this.historyUnavailableGroups.size > 0;
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
