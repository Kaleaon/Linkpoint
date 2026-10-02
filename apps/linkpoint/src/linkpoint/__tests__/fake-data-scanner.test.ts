import { describe, expect, it } from 'vitest';
import { RULES, scanText, applyAllowlist } from '../../../scripts/fake-data-rules.mjs';
import { scanRepository } from '../../../scripts/check-fake-data.mjs';

const hits = (text: string, file = 'src/screens/X.jsx') => scanText(text, file).map((f: any) => f.rule);

describe('fabricated-data scanner rules', () => {
  const bad: Array<[string, string]> = [
    ['invented-resident', 'const name = "Jane Doe";'],
    ['invented-resident', "sender: 'Nyx Vaher',"],
    ['invented-place', 'const region = info.name || "Arapaima";'],
    ['invented-place', 'startLocation || "Welcome Island"'],
    ['placeholder-id', "senderId: 'nyx-uuid',"],
    ['placeholder-id', 'agent = "f496d6bf-8235-4ebf-bd56-4f7f0464a27a";'],
    ['placeholder-id', 'ip = "216.82.52.24"'],
    ['seeded-record', "id: 'seed-local-1',"],
    ['seeded-record', 'timestamp: now - 3600000,'],
    ['simulated-session', 'You are simulating a resident in Second Life'],
    ['simulated-session', 'await generateGeminiLoginResponse(params)'],
    ['simulated-session', '// ok'.replace('// ok', 'Gemini proxy synthesizes an authentic login')],
    ['tick-driven-value', '"SIM " + (21.4 + (tick % 13) * 0.1).toFixed(1)'],
    ['tick-driven-value', 'AGENTS {11 + (state.tick % 4)}'],
    ['unknown-to-number', 'const ms = diag.latencyMs || 48;'],
    ['unknown-to-number', 'circuitCode: circuit?.circuitCode || 1001,'],
    ['count-fallback', 'const n = Object.keys(caps).length || 14;'],
    ['unknown-to-number', 'seed = app.protocol.seedCapability || "https://sim.example/cap/seed"'],
    ['fake-acknowledgement', 'setCReason(b.label + " — ACKNOWLEDGED");'],
    ['fake-acknowledgement', 'setCReason("DENIED — no fly in this region");'],
    ['invented-coordinates', ': { x: 128, y: 128, z: 24 };'],
    ['fake-data-phrase', '<p>Showing sample residents</p>'],
    ['fake-data-phrase', 'const mock chats = []'],
    ['random-in-ui', 'const lag = Math.random() * 40;'],
  ];
  for (const [rule, text] of bad) {
    it(`flags ${rule}: ${text.slice(0, 50)}`, () => expect(hits(text)).toContain(rule));
  }

  it('does not flag honest code', () => {
    const good = [
      'const latency = realLatency(diag.latencyMs);',
      'return v === null ? "—" : String(v);',
      '{region ? region.name : "Region not supplied"}',
      'throw new Error("Synthetic grid sessions have been removed; select a live endpoint");',
      '// Jane Doe and Arapaima are mentioned in this comment only',
      ' * Welcome Island',
      'const capabilityCount = Object.keys(caps).length;',
      'const n = list.length || 0;',
    ];
    for (const line of good) expect(hits(line), line).toEqual([]);
  });

  it('limits Math.random to UI code', () => {
    expect(hits('Math.random()', 'src/linkpoint/utils.ts')).toEqual([]);
    expect(hits('Math.random()', 'src/hooks/useAppState.js')).toContain('random-in-ui');
  });

  it('has a unique id and message for every rule', () => {
    const ids = RULES.map((r: any) => r.id);
    expect(new Set(ids).size).toBe(ids.length);
    for (const rule of RULES) expect(rule.message.length).toBeGreaterThan(10);
  });
});

describe('allowlist', () => {
  const finding = { file: 'src/a.ts', line: 1, rule: 'seeded-record', message: '', text: '' };
  it('suppresses a finding only when rule and file match and a reason is given', () => {
    expect(applyAllowlist([finding], [{ rule: 'seeded-record', file: 'src/a.ts', reason: 'why' }]).findings).toEqual([]);
    expect(applyAllowlist([finding], [{ rule: 'other', file: 'src/a.ts', reason: 'why' }]).findings).toHaveLength(1);
    expect(applyAllowlist([finding], [{ rule: 'seeded-record', file: 'src/b.ts', reason: 'why' }]).findings).toHaveLength(1);
  });
  it('rejects entries without a reason and does not honour them', () => {
    const result = applyAllowlist([finding], [{ rule: 'seeded-record', file: 'src/a.ts', reason: ' ' }]);
    expect(result.problems).toHaveLength(1);
    expect(result.findings).toHaveLength(1);
  });
});

describe('repository', () => {
  it('contains no fabricated data in shipped source', () => {
    const { findings, problems } = scanRepository();
    expect(problems).toEqual([]);
    expect(findings.map((f: any) => `${f.file}:${f.line} [${f.rule}]`)).toEqual([]);
  });
});
