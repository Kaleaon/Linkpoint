#!/usr/bin/env node
// Fails when shipped source contains fabricated data (see fake-data-rules.mjs).
//
//   node scripts/check-fake-data.mjs            scan the repository
//   node scripts/check-fake-data.mjs --json     machine-readable output
//   node scripts/check-fake-data.mjs path ...   scan specific files or folders
//
// Allowed exceptions live in scripts/fake-data-allowlist.json and must each give
// a reason. Persisted fake data from older builds is removed at startup by
// src/linkpoint/fabricated-data.ts.
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import { RULES, DEFAULT_ROOTS, EXCLUDED, scanText, applyAllowlist } from './fake-data-rules.mjs';

const here = path.dirname(fileURLToPath(import.meta.url));
export const repoRoot = path.resolve(here, '..');

function* walk(target) {
  const full = path.resolve(repoRoot, target);
  if (!fs.existsSync(full)) return;
  const stat = fs.statSync(full);
  if (stat.isDirectory()) {
    for (const name of fs.readdirSync(full)) yield* walk(path.join(target, name));
  } else {
    yield target.split(path.sep).join('/');
  }
}

export function scanRepository(roots = DEFAULT_ROOTS) {
  const findings = [];
  for (const root of roots) {
    for (const file of walk(root)) {
      if (EXCLUDED.some((pattern) => pattern.test(file))) continue;
      if (!/\.(m?[jt]sx?|cjs|html|css)$/.test(file)) continue;
      findings.push(...scanText(fs.readFileSync(path.join(repoRoot, file), 'utf8'), file, RULES));
    }
  }
  const allowlist = JSON.parse(fs.readFileSync(path.join(here, 'fake-data-allowlist.json'), 'utf8'));
  return applyAllowlist(findings, allowlist);
}

if (process.argv[1] && path.resolve(process.argv[1]) === fileURLToPath(import.meta.url)) {
  const args = process.argv.slice(2);
  const json = args.includes('--json');
  const roots = args.filter((arg) => !arg.startsWith('--'));
  const { findings, problems } = scanRepository(roots.length ? roots : DEFAULT_ROOTS);
  if (json) {
    console.log(JSON.stringify({ findings, problems }, null, 2));
  } else {
    for (const f of findings) console.error(`${f.file}:${f.line}  [${f.rule}]  ${f.message}\n    ${f.text}`);
    for (const p of problems) console.error(`allowlist: ${p}`);
    console.error(findings.length || problems.length ? `\n${findings.length} fabricated-data finding(s), ${problems.length} allowlist problem(s).` : 'No fabricated data found.');
  }
  process.exit(findings.length || problems.length ? 1 : 0);
}
