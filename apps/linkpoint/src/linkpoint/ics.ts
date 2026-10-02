/**
 * Build an iCalendar (.ics) file for one event, so a notice can be saved to any
 * calendar app without a Google account (RFC 5545).
 */

export interface IcsEvent {
  uid: string;
  summary: string;
  description?: string;
  location?: string;
  start: Date;
  end: Date;
  /** When the file is made (DTSTAMP). Defaults to now. */
  stamp?: Date;
  /** Minutes before the start for popup reminders. */
  reminders?: number[];
}

const pad = (n: number) => String(n).padStart(2, '0');

/** `YYYYMMDDTHHMMSSZ` in UTC. */
export function icsDate(date: Date): string {
  if (!Number.isFinite(date.getTime())) throw new Error('The event time is not a valid date.');
  return `${date.getUTCFullYear()}${pad(date.getUTCMonth() + 1)}${pad(date.getUTCDate())}T${pad(date.getUTCHours())}${pad(date.getUTCMinutes())}${pad(date.getUTCSeconds())}Z`;
}

/** Escape text for an iCalendar value: backslash, semicolon, comma and line breaks. */
export function icsEscape(text: string): string {
  return String(text ?? '').replace(/\\/g, '\\\\').replace(/;/g, '\;').replace(/,/g, '\\,').replace(/\r\n|\r|\n/g, '\\n');
}

/** Fold a content line to 75 octets (not characters), continuing on lines that start with a space. */
export function foldLine(line: string): string {
  const encoder = new TextEncoder();
  if (encoder.encode(line).length <= 75) return line;
  const out: string[] = [];
  let current = '';
  let size = 0;
  let limit = 75;
  for (const char of line) {
    const bytes = encoder.encode(char).length;
    if (size + bytes > limit) { out.push(current); current = ' '; size = 1; limit = 75; }
    current += char;
    size += bytes;
  }
  out.push(current);
  return out.join('\r\n');
}

export function buildIcs(event: IcsEvent): string {
  if (!event.uid || /[\r\n]/.test(event.uid)) throw new Error('The event needs a plain-text id.');
  if (event.end.getTime() <= event.start.getTime()) throw new Error('The event must end after it starts.');
  const lines = [
    'BEGIN:VCALENDAR',
    'VERSION:2.0',
    'PRODID:-//Linkpoint Viewer//Group notices//EN',
    'CALSCALE:GREGORIAN',
    'BEGIN:VEVENT',
    `UID:${event.uid}`,
    `DTSTAMP:${icsDate(event.stamp || new Date())}`,
    `DTSTART:${icsDate(event.start)}`,
    `DTEND:${icsDate(event.end)}`,
    `SUMMARY:${icsEscape(event.summary || 'Second Life notice')}`,
  ];
  if (event.description) lines.push(`DESCRIPTION:${icsEscape(event.description)}`);
  if (event.location) lines.push(`LOCATION:${icsEscape(event.location)}`);
  for (const minutes of event.reminders || []) {
    if (!Number.isInteger(minutes) || minutes < 0 || minutes > 40320) continue;
    lines.push('BEGIN:VALARM', 'ACTION:DISPLAY', 'DESCRIPTION:Reminder', `TRIGGER:-PT${minutes}M`, 'END:VALARM');
  }
  lines.push('END:VEVENT', 'END:VCALENDAR');
  return lines.map(foldLine).join('\r\n') + '\r\n';
}

/** A safe file name for the download. */
export function icsFileName(summary: string): string {
  const base = String(summary || 'notice').replace(/[^A-Za-z0-9]+/g, '-').replace(/^-+|-+$/g, '').slice(0, 40);
  return `${base || 'notice'}.ics`;
}
