/**
 * Add Second Life group notices to Google Calendar (optional, user-initiated).
 *
 * Events are tagged with the notice id in a private extended property, so adding
 * the same notice twice finds the first event instead of creating a duplicate,
 * and the app can list just the events it made.
 */

import { googleFetch } from "./googleApi";

const EVENTS = "https://www.googleapis.com/calendar/v3/calendars/primary/events";
const EVENT_ID = /^[A-Za-z0-9_-]{1,1024}$/;

export interface NoticeForCalendar {
  id: string;
  subject: string;
  message: string;
  from: string;
  groupName: string;
}

export interface CalendarEventInfo {
  id: string;
  summary: string;
  htmlLink: string | null;
  start: string | null;
  end: string | null;
}

function toInfo(item: any): CalendarEventInfo {
  return {
    id: String(item.id),
    summary: String(item.summary || "Untitled event"),
    htmlLink: typeof item.htmlLink === "string" && /^https:\/\/(?:www\.)?google\.com\/calendar\//i.test(item.htmlLink) ? item.htmlLink : null,
    start: item.start?.dateTime || item.start?.date || null,
    end: item.end?.dateTime || item.end?.date || null,
  };
}

export interface AddOptions {
  start: Date;
  end: Date;
  /** IANA zone the notice's time was read in; Google uses it for display and daylight saving. */
  timeZone: string;
  summary?: string;
  location?: string;
}

export function buildEventBody(notice: NoticeForCalendar, options: AddOptions): Record<string, unknown> {
  if (!Number.isFinite(options.start.getTime()) || !Number.isFinite(options.end.getTime())) throw new Error("Choose a valid start and end time.");
  if (options.end.getTime() <= options.start.getTime()) throw new Error("The event must end after it starts.");
  const description = [
    `Group: ${notice.groupName || "Second Life group"}`,
    notice.from ? `Sent by: ${notice.from}` : null,
    "",
    notice.message,
  ].filter((line) => line !== null).join("\n");
  return {
    summary: (options.summary || notice.subject || "Group notice").slice(0, 300),
    description,
    location: options.location ?? `Second Life${notice.groupName ? ` (${notice.groupName})` : ""}`,
    start: { dateTime: options.start.toISOString(), timeZone: options.timeZone },
    end: { dateTime: options.end.toISOString(), timeZone: options.timeZone },
    reminders: { useDefault: false, overrides: [{ method: "popup", minutes: 30 }, { method: "popup", minutes: 10 }] },
    extendedProperties: { private: { linkpointNotice: "1", linkpointNoticeId: notice.id } },
  };
}

/** Add a notice to the primary calendar, or return the event already made for it. */
export async function addNoticeToCalendar(notice: NoticeForCalendar, options: AddOptions): Promise<{ event: CalendarEventInfo; existing: boolean }> {
  const body = buildEventBody(notice, options); // validate before touching the network

  const lookup = `${EVENTS}?privateExtendedProperty=${encodeURIComponent(`linkpointNoticeId=${notice.id}`)}&maxResults=1`;
  const found = (await (await googleFetch("calendar", lookup)).json()).items?.[0];
  if (found && EVENT_ID.test(String(found.id))) return { event: toInfo(found), existing: true };

  const created = await (await googleFetch("calendar", EVENTS, { method: "POST", body: JSON.stringify(body) })).json();
  if (!EVENT_ID.test(String(created?.id))) throw new Error("Google returned an unexpected event id.");
  return { event: toInfo(created), existing: false };
}

/** Upcoming events this app created, soonest first. */
export async function fetchLinkpointEvents(maxResults = 30): Promise<CalendarEventInfo[]> {
  const url = `${EVENTS}?singleEvents=true&orderBy=startTime&timeMin=${encodeURIComponent(new Date().toISOString())}&privateExtendedProperty=${encodeURIComponent("linkpointNotice=1")}&maxResults=${Math.max(1, Math.min(100, maxResults))}`;
  const data = await (await googleFetch("calendar", url)).json();
  return (data.items || []).filter((item: any) => EVENT_ID.test(String(item.id))).map(toInfo);
}

export async function deleteCalendarEvent(eventId: string): Promise<void> {
  if (!EVENT_ID.test(eventId)) throw new Error("That is not a calendar event id.");
  await googleFetch("calendar", `${EVENTS}/${encodeURIComponent(eventId)}`, { method: "DELETE" });
}
