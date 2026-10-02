// Rules for detecting fabricated data in shipped source.
//
// "Fabricated" means a value the viewer made up and presents as if a grid or the
// resident supplied it: invented residents and chats, placeholder telemetry,
// canned acknowledgements, AI-generated "simulated" sessions, and fallbacks that
// turn "unknown" into a plausible number. Tests may use fixtures; shipped code
// may not.
//
// Each rule has an id, a regex applied per line (comment lines are skipped), a
// message that says what to do instead, and an optional `scope` of path prefixes.

export const RULES = [
  {
    id: 'invented-resident',
    pattern: /\b(Jane Doe|Ruth Resident|Nyx Vaher|Kit Sandalwood|Sable Ashgrove|Test User|Example Avatar)\b/,
    message: 'Invented resident name. Show the real resident, or nothing.',
  },
  {
    id: 'invented-place',
    pattern: /\b(Welcome Island|Arapaima|Gemini Simulated Grid)\b/,
    message: 'Invented region or grid. Use the live region name, or show it as unknown.',
  },
  {
    id: 'placeholder-id',
    pattern: /['"`](nyx|kit|sable|ruth)-uuid['"`]|f496d6bf-8235-4ebf-bd56-4f7f0464a27a|216\.82\.52\.24/,
    message: 'Placeholder agent id or address. Use the session value, or show "—".',
  },
  {
    id: 'seeded-record',
    pattern: /['"`]seed-[a-z]|timestamp:\s*now\s*-\s*\d+/,
    message: 'Seeded record (made-up message or history). Never write generated data into real state.',
  },
  {
    id: 'simulated-session',
    pattern: /simulating a resident|synthesi[sz]es? an authentic|generateGeminiLoginResponse|generateSimulatedChat|Virtual Resident|Synthetic grid/i,
    message: 'Simulated grid/resident. A failed connection must be reported as a failure.',
    // The client message that says synthetic sessions were removed is the opposite of the problem.
    ignoreLine: /have been removed/i,
  },
  {
    id: 'tick-driven-value',
    pattern: /\(\s*(state\.)?tick\s*%\s*\d+\s*\)/,
    message: 'Value computed from a tick counter. Animated numbers must be real measurements.',
  },
  {
    id: 'unknown-to-number',
    pattern: /(latency|ping|fps|packet\w*|circuit\w*|simPort|capabilit\w+)\w*\)?\s*(\|\||\?\?)\s*(\d+|["'][^"']+["'])/i,
    message: 'Fallback turns an unknown measurement into a plausible one. Use null and render "—".',
  },
  {
    id: 'count-fallback',
    pattern: /\.length\s*(\|\||\?\?)\s*[1-9]\d*/,
    message: 'A missing count replaced with a non-zero number. Zero or "—" is the honest value.',
  },
  {
    id: 'fake-acknowledgement',
    pattern: /ACKNOWLEDGED|DENIED\s*[—-]/,
    message: 'Pretends an action happened or reports a permission the viewer cannot know.',
  },
  {
    id: 'invented-coordinates',
    pattern: /x:\s*128,\s*y:\s*128/,
    message: 'Default region-centre coordinates presented as the avatar position. Use null when unknown.',
  },
  {
    id: 'fake-data-phrase',
    pattern: /\b(sample|fake|dummy|demo|mock)\s+(data|residents?|chats?|messages?|avatars?|objects?|inventory|friends?)\b|lorem ipsum/i,
    message: 'Sample-data wording in shipped source.',
  },
  {
    id: 'random-in-ui',
    pattern: /Math\.random\(\)/,
    scope: ['src/screens/', 'src/components/', 'src/hooks/'],
    message: 'Math.random() in UI code. Displayed values must not be random.',
  },
];

export const DEFAULT_ROOTS = ['src', 'server.ts', 'electron'];

// Paths that are not shipped application code.
export const EXCLUDED = [
  /(^|\/)node_modules\//,
  /(^|\/)dist\//,
  /(^|\/)__tests__\//,
  /\.test\.[cm]?[jt]sx?$/,
  /^src\/design\//, // synced design prototype, not the app
  /\.(png|jpg|jpeg|gif|webp|svg|ico|woff2?|ttf|lock)$/,
];

const isCommentLine = (line) => /^\s*(\/\/|\*|\/\*)/.test(line);

/** Scan one file's text. Returns [{ file, line, rule, message, text }]. */
export function scanText(text, file, rules = RULES) {
  const findings = [];
  const lines = text.split('\n');
  lines.forEach((line, index) => {
    if (isCommentLine(line)) return;
    for (const rule of rules) {
      if (rule.scope && !rule.scope.some((prefix) => file.startsWith(prefix))) continue;
      if (!rule.pattern.test(line)) continue;
      if (rule.ignoreLine && rule.ignoreLine.test(line)) continue;
      findings.push({ file, line: index + 1, rule: rule.id, message: rule.message, text: line.trim().slice(0, 160) });
    }
  });
  return findings;
}

/** Apply allowlist entries: { rule, file, reason } where file may be a prefix. */
export function applyAllowlist(findings, allowlist) {
  const problems = [];
  for (const entry of allowlist) {
    if (!entry.reason || !String(entry.reason).trim()) problems.push(`allowlist entry for ${entry.file} (${entry.rule}) has no reason`);
  }
  const hasReason = (entry) => Boolean(entry.reason && String(entry.reason).trim());
  const allowed = (finding) => allowlist.some((entry) => entry.rule === finding.rule && finding.file.startsWith(entry.file) && hasReason(entry));
  return { findings: findings.filter((finding) => !allowed(finding)), problems };
}
