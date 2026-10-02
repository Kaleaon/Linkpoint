import { describe, expect, it } from 'vitest';
import { formatLatency, liveRegionName, formatSlt } from '../../components/menuStatus.js';

describe('menu bar status', () => {
  it('shows latency only for a real measurement', () => {
    expect(formatLatency({ latencyMs: 87.4 })).toBe('87 ms');
    for (const value of [null, undefined, 0, -5, Number.NaN, '54', Infinity]) expect(formatLatency({ latencyMs: value })).toBeNull();
    expect(formatLatency(null)).toBeNull();
    expect(formatLatency({})).toBeNull();
  });

  it('never invents a region name', () => {
    expect(liveRegionName({ protocol: { authReply: { sim_name: ' Ahern ' } }, world: {} })).toBe('Ahern');
    expect(liveRegionName({ protocol: {}, world: { region: { name: 'Da Boom' } } })).toBe('Da Boom');
    expect(liveRegionName({ protocol: { authReply: { sim_name: '' } }, world: { region: { name: '  ' } } })).toBeNull();
    expect(liveRegionName({ protocol: {}, world: {} })).toBeNull();
    expect(liveRegionName(undefined)).toBeNull();
  });

  it('formats Second Life Time as US Pacific, across daylight saving', () => {
    expect(formatSlt(new Date('2026-01-15T20:30:00Z'))).toBe('12:30 PM SLT'); // PST, UTC-8
    expect(formatSlt(new Date('2026-07-15T19:30:00Z'))).toBe('12:30 PM SLT'); // PDT, UTC-7
  });
});
