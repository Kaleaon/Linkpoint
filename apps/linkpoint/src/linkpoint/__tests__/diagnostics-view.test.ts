import { describe, expect, it } from 'vitest';
import { readFileSync } from 'node:fs';
import { join } from 'node:path';
import { UNKNOWN, show, positiveOrNull, realLatency, latencyBand, lossBand, pushLatency, latencyRange, packetAgeMs, describePing, eventQueueState } from '../../screens/diagnosticsView.js';

describe('diagnostics display rules', () => {
  it('shows unknown values as a dash, never a placeholder', () => {
    for (const v of [null, undefined, '', Number.NaN, Infinity]) expect(show(v)).toBe(UNKNOWN);
    expect(show(0)).toBe('0'); // a genuine zero is still a value
    expect(show('Ahern')).toBe('Ahern');
    expect(positiveOrNull(0)).toBeNull();
    expect(positiveOrNull(13000)).toBe(13000);
    expect(positiveOrNull(null)).toBeNull();
  });

  it('bands latency only when it is a real measurement', () => {
    expect(latencyBand(null)).toMatchObject({ label: 'NO DATA', tone: 'none', text: UNKNOWN });
    expect(latencyBand(0)).toMatchObject({ label: 'NO DATA' });
    expect(latencyBand(45)).toMatchObject({ label: 'EXCELLENT', tone: 'ok', text: '45' });
    expect(latencyBand(120.4)).toMatchObject({ label: 'NORMAL', tone: 'warn', text: '120' });
    expect(latencyBand(450)).toMatchObject({ label: 'DEGRADED', tone: 'err' });
    expect(realLatency('54')).toBeNull();
  });

  it('does not report zero packet loss when none was reported', () => {
    expect(lossBand(null)).toMatchObject({ text: UNKNOWN, tone: 'none' });
    expect(lossBand(undefined).text).toBe(UNKNOWN);
    expect(lossBand(0)).toMatchObject({ text: '0.0', tone: 'ok' });
    expect(lossBand(0.8)).toMatchObject({ tone: 'warn' });
    expect(lossBand(4)).toMatchObject({ tone: 'err' });
  });

  it('keeps only real samples in the latency history', () => {
    let history: number[] = [];
    history = pushLatency(history, null);
    history = pushLatency(history, 0);
    expect(history).toEqual([]);
    expect(latencyRange(history)).toBeNull();
    history = pushLatency(history, 40);
    history = pushLatency(history, 80);
    expect(latencyRange(history)).toEqual({ min: 40, max: 80 });
    let long: number[] = [];
    for (let i = 1; i <= 30; i++) long = pushLatency(long, i);
    expect(long).toHaveLength(24);
    expect(long[0]).toBe(7);
  });

  it('only reports packet age once a packet has been seen', () => {
    expect(packetAgeMs(null, 5000)).toBeNull();
    expect(packetAgeMs(0, 5000)).toBeNull();
    expect(packetAgeMs(4000, 5000)).toBe(1000);
    expect(packetAgeMs(9000, 5000)).toBe(0);
  });

  it('describes a ping probe honestly', () => {
    expect(describePing(null)).toMatch(/no latency/i);
    expect(describePing({ latencyMs: null })).toMatch(/no latency/i);
    expect(describePing({ latencyMs: 61.6 })).toBe('Ping complete: 62 ms latency');
  });

  it('reports the event queue from its real state', () => {
    expect(eventQueueState(false, true)).toMatchObject({ text: 'NOT CONNECTED' });
    expect(eventQueueState(true, true)).toMatchObject({ text: 'RUNNING', tone: 'ok' });
    expect(eventQueueState(true, false)).toMatchObject({ text: 'STOPPED', tone: 'err' });
  });
});

describe('no fabricated telemetry remains in the source', () => {
  const read = (p: string) => readFileSync(join(process.cwd(), 'src', p), 'utf8');
  const sources = ['screens/DiagnosticsPanel.jsx', 'linkpoint/sl-connection-full.ts', 'server/sl-session.ts', 'components/MenuBar.jsx'];
  const banned: Array<[string, RegExp]> = [
    ['hardcoded agent UUID', /f496d6bf-8235-4ebf-bd56-4f7f0464a27a/],
    ['fake sim IP', /216\.82\.52\.24/],
    ['fake region', /Arapaima/],
    ['fake grid coordinates', /1797|1197/],
    ['fake seed capability', /sim\.agni\.lindenlab\.com\/cap\/seed/],
    ['claimed HTTP 200', /HTTP 200 OK/],
    ['fake capability count', /\|\| 14\b/],
    ['fake latency fallback', /latencyMs\s*\|\|\s*\d+|latency[^\n]*:\s*(32|48|54)\b/],
    ['fake circuit code', /circuit(_c|C)ode[^\n]*\|\|\s*1001/],
  ];
  for (const [label, pattern] of banned) {
    it(`has no ${label}`, () => {
      for (const file of sources) expect(read(file), file).not.toMatch(pattern);
    });
  }
});
