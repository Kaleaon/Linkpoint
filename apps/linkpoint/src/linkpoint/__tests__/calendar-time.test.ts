import { describe, expect, it } from 'vitest';
import { SLT_ZONE, fromLocalInputValue, suggestEventTime, toLocalInputValue, zonedTimeToInstant } from '../calendar-time';

const iso = (date: Date) => date.toISOString();
// Wednesday 2026-03-04 12:00 UTC (04:00 in Los Angeles, before US daylight saving starts on 2026-03-08).
const RECEIVED = Date.UTC(2026, 2, 4, 12, 0);

describe('zonedTimeToInstant', () => {
  it('uses standard time in winter and daylight time in summer', () => {
    expect(iso(zonedTimeToInstant(2026, 1, 15, 19, 0, SLT_ZONE))).toBe('2026-01-16T03:00:00.000Z'); // UTC-8
    expect(iso(zonedTimeToInstant(2026, 7, 15, 19, 0, SLT_ZONE))).toBe('2026-07-16T02:00:00.000Z'); // UTC-7
  });

  it('is right either side of the daylight saving change', () => {
    expect(iso(zonedTimeToInstant(2026, 3, 7, 12, 0, SLT_ZONE))).toBe('2026-03-07T20:00:00.000Z'); // before: UTC-8
    expect(iso(zonedTimeToInstant(2026, 3, 8, 12, 0, SLT_ZONE))).toBe('2026-03-08T19:00:00.000Z'); // after: UTC-7
    expect(iso(zonedTimeToInstant(2026, 11, 1, 12, 0, SLT_ZONE))).toBe('2026-11-01T20:00:00.000Z'); // after fall back: UTC-8
  });

  it('handles other zones', () => {
    expect(iso(zonedTimeToInstant(2026, 1, 15, 12, 0, 'UTC'))).toBe('2026-01-15T12:00:00.000Z');
    expect(iso(zonedTimeToInstant(2026, 1, 15, 12, 0, 'America/New_York'))).toBe('2026-01-15T17:00:00.000Z');
  });
});

describe('suggestEventTime', () => {
  it('reads a time with am/pm and assumes Second Life Time when no zone is named', () => {
    const result = suggestEventTime('Dance night tomorrow 7pm', RECEIVED);
    expect(result).toMatchObject({ found: true, assumedZone: true, timeZone: SLT_ZONE });
    expect(iso(result.start)).toBe('2026-03-06T03:00:00.000Z'); // Thursday 7pm PST = 03:00 UTC Friday
    expect(result.end.getTime() - result.start.getTime()).toBe(3_600_000);
  });

  it('does not mistake other numbers for a time', () => {
    for (const text of ['Room 7 is open', 'Meet at sim 12', 'We have 20 members now', 'Price is 5 L$ per hour']) {
      expect(suggestEventTime(text, RECEIVED).found).toBe(false);
    }
  });

  it('flags a notice with no time and gives the next whole hour as a placeholder', () => {
    const result = suggestEventTime('General update, no schedule', RECEIVED);
    expect(result.found).toBe(false);
    expect(iso(result.start)).toBe('2026-03-04T13:00:00.000Z');
  });

  it('reads 12-hour times with minutes and 24-hour times', () => {
    expect(iso(suggestEventTime('Starts 7:30 pm SLT on 3/20', RECEIVED).start)).toBe('2026-03-21T02:30:00.000Z');
    expect(iso(suggestEventTime('Starts 19:30 SLT on 3/20', RECEIVED).start)).toBe('2026-03-21T02:30:00.000Z');
    expect(iso(suggestEventTime('12am on 3/20', RECEIVED).start)).toBe('2026-03-20T07:00:00.000Z'); // midnight, PDT after the change
    expect(iso(suggestEventTime('12 pm on 3/20', RECEIVED).start)).toBe('2026-03-20T19:00:00.000Z');
    expect(iso(suggestEventTime('Noon on 3/20', RECEIVED).start)).toBe('2026-03-20T19:00:00.000Z');
  });

  it('respects a named zone', () => {
    const result = suggestEventTime('Party 8pm UTC on 2026-03-20', RECEIVED);
    expect(result).toMatchObject({ assumedZone: false, timeZone: 'UTC' });
    expect(iso(result.start)).toBe('2026-03-20T20:00:00.000Z');
    expect(iso(suggestEventTime('Party 8pm EST on 1/20/2027', RECEIVED).start)).toBe('2027-01-21T01:00:00.000Z');
  });

  it('understands weekdays, tomorrow and month names', () => {
    expect(iso(suggestEventTime('Saturday 2pm SLT', RECEIVED).start)).toBe('2026-03-07T22:00:00.000Z'); // Sat 2pm PST
    expect(iso(suggestEventTime('Join us March 14th at 6 pm', RECEIVED).start)).toBe('2026-03-15T01:00:00.000Z'); // PDT
    expect(iso(suggestEventTime('See you Jan 5 at 6pm', RECEIVED).start)).toBe('2027-01-06T02:00:00.000Z'); // already passed: next year
  });

  it('moves a bare time that has already passed to tomorrow, but not an explicit date', () => {
    const lateEvening = Date.UTC(2026, 2, 5, 6, 0); // Wed 22:00 in Los Angeles
    expect(iso(suggestEventTime('Start 7pm', lateEvening).start)).toBe('2026-03-06T03:00:00.000Z'); // tomorrow
    expect(iso(suggestEventTime('Today 7pm', lateEvening).start)).toBe('2026-03-05T03:00:00.000Z'); // explicit "today" stays
  });

  it('uses the notice\'s own day in the target zone, not the viewer\'s', () => {
    // 02:00 UTC on Mar 5 is still Wednesday Mar 4 evening in Los Angeles, so "Thursday" means Mar 5 there.
    const result = suggestEventTime('Thursday 7pm SLT', Date.UTC(2026, 2, 5, 2, 0));
    expect(iso(result.start)).toBe('2026-03-06T03:00:00.000Z');
  });
});

describe('datetime-local helpers', () => {
  it('round-trips a time through a zone', () => {
    const instant = new Date(Date.UTC(2026, 2, 6, 3, 0));
    expect(toLocalInputValue(instant, SLT_ZONE)).toBe('2026-03-05T19:00');
    expect(toLocalInputValue(instant, 'UTC')).toBe('2026-03-06T03:00');
    expect(fromLocalInputValue('2026-03-05T19:00', SLT_ZONE)!.toISOString()).toBe(instant.toISOString());
  });

  it('rejects values that are not real dates', () => {
    for (const bad of ['', 'nonsense', '2026-02-31T10:00', '2026-13-01T10:00', '2026-01-01T25:00', '2026-01-01T10:60', '2026-01-01', null as any]) {
      expect(fromLocalInputValue(bad, SLT_ZONE)).toBeNull();
    }
  });
});
