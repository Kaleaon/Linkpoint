import { describe, expect, it } from 'vitest';
import { buildIcs, foldLine, icsDate, icsEscape, icsFileName } from '../ics';

const base = { uid: 'notice-1@linkpoint', summary: 'Dance night', start: new Date(Date.UTC(2026, 2, 6, 3, 0)), end: new Date(Date.UTC(2026, 2, 6, 4, 0)), stamp: new Date(Date.UTC(2026, 2, 4, 12, 0)) };

describe('icsDate and icsEscape', () => {
  it('formats UTC timestamps', () => {
    expect(icsDate(new Date(Date.UTC(2026, 2, 6, 3, 5, 9)))).toBe('20260306T030509Z');
    expect(() => icsDate(new Date(NaN))).toThrow(/valid date/);
  });

  it('escapes the characters that would break a value', () => {
    expect(icsEscape('a;b,c\\d\ne\r\nf')).toBe('a\;b\\,c\\\\d\\ne\\nf');
  });
});

describe('foldLine', () => {
  it('leaves short lines alone and folds long ones at 75 octets', () => {
    expect(foldLine('SUMMARY:short')).toBe('SUMMARY:short');
    const folded = foldLine(`DESCRIPTION:${'x'.repeat(200)}`).split('\r\n');
    expect(folded.length).toBeGreaterThan(2);
    for (const [index, line] of folded.entries()) {
      expect(new TextEncoder().encode(line).length).toBeLessThanOrEqual(75);
      if (index > 0) expect(line.startsWith(' ')).toBe(true);
    }
    expect(folded.map((line, i) => (i ? line.slice(1) : line)).join('')).toBe(`DESCRIPTION:${'x'.repeat(200)}`);
  });

  it('never splits a multi-byte character', () => {
    const text = `SUMMARY:${'€'.repeat(60)}`;
    const folded = foldLine(text).split('\r\n');
    expect(folded.map((line, i) => (i ? line.slice(1) : line)).join('')).toBe(text);
    for (const line of folded) expect(new TextEncoder().encode(line).length).toBeLessThanOrEqual(75);
  });
});

describe('buildIcs', () => {
  it('produces a valid single-event calendar with CRLF line endings', () => {
    const text = buildIcs({ ...base, description: 'Group: Dancers\nBring friends', location: 'Second Life (Dancers)', reminders: [30, 10] });
    expect(text.startsWith('BEGIN:VCALENDAR\r\n')).toBe(true);
    expect(text.endsWith('END:VEVENT\r\nEND:VCALENDAR\r\n')).toBe(true);
    expect(text).not.toMatch(/[^\r]\n/);
    expect(text).toContain('UID:notice-1@linkpoint\r\n');
    expect(text).toContain('DTSTART:20260306T030000Z\r\n');
    expect(text).toContain('DTEND:20260306T040000Z\r\n');
    expect(text).toContain('DESCRIPTION:Group: Dancers\\nBring friends\r\n');
    expect(text).toContain('TRIGGER:-PT30M');
    expect(text).toContain('TRIGGER:-PT10M');
  });

  it('keeps hostile text from injecting extra calendar properties', () => {
    const text = buildIcs({ ...base, summary: 'Party\r\nATTENDEE:mailto:evil@example.com', description: 'x\nEND:VEVENT' });
    expect(text).not.toMatch(/^ATTENDEE:/m);
    expect(text.match(/^END:VEVENT/gm)).toHaveLength(1);
  });

  it('refuses a bad id or an event that ends before it starts, and ignores nonsense reminders', () => {
    expect(() => buildIcs({ ...base, uid: '' })).toThrow(/id/);
    expect(() => buildIcs({ ...base, uid: 'a\r\nb' })).toThrow(/id/);
    expect(() => buildIcs({ ...base, end: base.start })).toThrow(/after it starts/);
    expect(buildIcs({ ...base, reminders: [-5, 1.5, 999999] })).not.toContain('VALARM');
  });
});

describe('icsFileName', () => {
  it('makes a safe name', () => {
    expect(icsFileName('Dance night / Sat!')).toBe('Dance-night-Sat.ics');
    expect(icsFileName('../../etc/passwd')).toBe('etc-passwd.ics');
    expect(icsFileName('')).toBe('notice.ics');
    expect(icsFileName('€€€')).toBe('notice.ics');
  });
});
