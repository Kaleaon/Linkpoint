/**
 * Work out when a group notice says an event happens.
 *
 * Group notices are free text ("Dance night Saturday 7pm SLT"). This reads an
 * explicit time and, if present, a date and time zone, and says plainly what it
 * found: when nothing is found the result is flagged so the UI asks the user to
 * pick a time instead of silently guessing one.
 *
 * Second Life Time (SLT) is US Pacific time, with daylight saving.
 */

export const SLT_ZONE = 'America/Los_Angeles';

const ZONES: Record<string, string> = {
  slt: SLT_ZONE, pst: SLT_ZONE, pdt: SLT_ZONE, pt: SLT_ZONE,
  utc: 'UTC', gmt: 'UTC',
  est: 'America/New_York', edt: 'America/New_York', et: 'America/New_York',
  cst: 'America/Chicago', cdt: 'America/Chicago', ct: 'America/Chicago',
};

const WEEKDAYS = ['sunday', 'monday', 'tuesday', 'wednesday', 'thursday', 'friday', 'saturday'];
const MONTHS = ['jan', 'feb', 'mar', 'apr', 'may', 'jun', 'jul', 'aug', 'sep', 'oct', 'nov', 'dec'];

interface Wall { year: number; month: number; day: number; hour: number; minute: number; weekday: number }

/** The calendar fields of an instant as seen in `zone`. */
function wallClock(instant: number, zone: string): Wall {
  const parts = new Intl.DateTimeFormat('en-US', { timeZone: zone, hourCycle: 'h23', year: 'numeric', month: 'numeric', day: 'numeric', hour: 'numeric', minute: 'numeric', weekday: 'short' }).formatToParts(new Date(instant));
  const get = (type: string) => parts.find((part) => part.type === type)?.value || '';
  const weekday = ['sun', 'mon', 'tue', 'wed', 'thu', 'fri', 'sat'].indexOf(get('weekday').toLowerCase());
  return { year: Number(get('year')), month: Number(get('month')), day: Number(get('day')), hour: Number(get('hour')) % 24, minute: Number(get('minute')), weekday };
}

/** `YYYY-MM-DDTHH:mm`, the clock reading of an instant in `zone`, in the form a datetime-local input uses. */
export function toLocalInputValue(instant: Date, zone: string): string {
  const w = wallClock(instant.getTime(), zone);
  const pad = (n: number, width = 2) => String(n).padStart(width, '0');
  return `${pad(w.year, 4)}-${pad(w.month)}-${pad(w.day)}T${pad(w.hour)}:${pad(w.minute)}`;
}

/** The instant a datetime-local value means when read in `zone`, or null if the value is not a valid date and time. */
export function fromLocalInputValue(value: string, zone: string): Date | null {
  const match = /^(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2})$/.exec(String(value || ''));
  if (!match) return null;
  const [year, month, day, hour, minute] = match.slice(1).map(Number);
  if (month < 1 || month > 12 || day < 1 || day > 31 || hour > 23 || minute > 59) return null;
  const check = new Date(Date.UTC(year, month - 1, day));
  if (check.getUTCMonth() !== month - 1) return null; // e.g. Feb 31
  return zonedTimeToInstant(year, month, day, hour, minute, zone);
}

/** Milliseconds `zone` is ahead of UTC at `instant`. */
function offsetAt(instant: number, zone: string): number {
  const wall = wallClock(instant, zone);
  return Date.UTC(wall.year, wall.month - 1, wall.day, wall.hour, wall.minute) - Math.floor(instant / 60000) * 60000;
}

/** The instant at which the clocks in `zone` read this date and time. */
export function zonedTimeToInstant(year: number, month: number, day: number, hour: number, minute: number, zone: string): Date {
  const naive = Date.UTC(year, month - 1, day, hour, minute);
  let instant = naive - offsetAt(naive, zone);
  // Across a daylight-saving change the first offset can be off by an hour; recompute once with the better guess.
  instant = naive - offsetAt(instant, zone);
  return new Date(instant);
}

export interface TimeSuggestion {
  start: Date;
  end: Date;
  timeZone: string;
  /** True when the notice named a time. When false, `start` is only a placeholder for the user to change. */
  found: boolean;
  /** True when the notice named no time zone and Second Life Time was assumed. */
  assumedZone: boolean;
}

const TIME_AMPM = /\b(1[0-2]|0?[1-9])(?::([0-5]\d))?\s*(a\.?m\.?|p\.?m\.?)(?![a-z])/i;
const TIME_24H = /\b([01]?\d|2[0-3]):([0-5]\d)\b/;
const ZONE_WORD = /\b(slt|pdt|pst|pt|utc|gmt|edt|est|et|cdt|cst|ct)\b/i;

function readTime(text: string): { hour: number; minute: number } | null {
  const ampm = text.match(TIME_AMPM);
  if (ampm) {
    let hour = Number(ampm[1]) % 12;
    if (/^p/i.test(ampm[3])) hour += 12;
    return { hour, minute: ampm[2] ? Number(ampm[2]) : 0 };
  }
  if (/\bnoon\b/i.test(text)) return { hour: 12, minute: 0 };
  if (/\bmidnight\b/i.test(text)) return { hour: 0, minute: 0 };
  const h24 = text.match(TIME_24H);
  return h24 ? { hour: Number(h24[1]), minute: Number(h24[2]) } : null;
}

/** A date named in the text, as a calendar day relative to `today` (the notice's day in the target zone). */
function readDate(text: string, today: Wall): { year: number; month: number; day: number } | null {
  const lower = text.toLowerCase();
  const iso = lower.match(/\b(20\d{2})-(0?[1-9]|1[0-2])-(0?[1-9]|[12]\d|3[01])\b/);
  if (iso) return { year: Number(iso[1]), month: Number(iso[2]), day: Number(iso[3]) };

  const named = lower.match(new RegExp(`\\b(${MONTHS.join('|')})[a-z]*\\.?\\s+(0?[1-9]|[12]\\d|3[01])(?:st|nd|rd|th)?\\b`));
  if (named) return rollForward(today, MONTHS.indexOf(named[1]) + 1, Number(named[2]));

  const slash = lower.match(/\b(0?[1-9]|1[0-2])\/(0?[1-9]|[12]\d|3[01])(?:\/(\d{2}|\d{4}))?\b/);
  if (slash) {
    if (slash[3]) return { year: slash[3].length === 2 ? 2000 + Number(slash[3]) : Number(slash[3]), month: Number(slash[1]), day: Number(slash[2]) };
    return rollForward(today, Number(slash[1]), Number(slash[2]));
  }

  if (/\btomorrow\b/.test(lower)) return addDays(today, 1);
  if (/\btoday\b|\btonight\b/.test(lower)) return { year: today.year, month: today.month, day: today.day };
  for (let i = 0; i < 7; i++) {
    if (new RegExp(`\\b${WEEKDAYS[i]}\\b|\\b${WEEKDAYS[i].slice(0, 3)}\\b(?!\\w)`).test(lower)) {
      const ahead = (i - today.weekday + 7) % 7;
      return addDays(today, ahead);
    }
  }
  return null;
}

function addDays(from: { year: number; month: number; day: number }, days: number) {
  const date = new Date(Date.UTC(from.year, from.month - 1, from.day + days));
  return { year: date.getUTCFullYear(), month: date.getUTCMonth() + 1, day: date.getUTCDate() };
}

/** A month/day with no year means the next time it occurs. */
function rollForward(today: Wall, month: number, day: number) {
  const thisYear = { year: today.year, month, day };
  const passed = month < today.month || (month === today.month && day < today.day);
  return passed ? { year: today.year + 1, month, day } : thisYear;
}

/**
 * Suggest an event time for a notice. `receivedAt` is when the notice arrived (ms).
 * The default length is one hour; the user can change it.
 */
export function suggestEventTime(text: string, receivedAt: number = Date.now()): TimeSuggestion {
  const zoneWord = text.match(ZONE_WORD);
  const timeZone = zoneWord ? ZONES[zoneWord[1].toLowerCase()] : SLT_ZONE;
  const assumedZone = !zoneWord;
  const time = readTime(text);

  if (!time) {
    // Nothing to go on: the next whole hour after the notice, flagged as a placeholder.
    const start = new Date(Math.floor(receivedAt / 3_600_000) * 3_600_000 + 3_600_000);
    return { start, end: new Date(start.getTime() + 3_600_000), timeZone, found: false, assumedZone };
  }

  const today = wallClock(receivedAt, timeZone);
  const named = readDate(text, today);
  let day = named || { year: today.year, month: today.month, day: today.day };
  let start = zonedTimeToInstant(day.year, day.month, day.day, time.hour, time.minute, timeZone);
  // A bare time that has already passed today means tomorrow; an explicit date is respected as written.
  if (!named && start.getTime() < receivedAt) {
    day = addDays(day, 1);
    start = zonedTimeToInstant(day.year, day.month, day.day, time.hour, time.minute, timeZone);
  }
  return { start, end: new Date(start.getTime() + 3_600_000), timeZone, found: true, assumedZone };
}
