import { execSync } from 'node:child_process';

// Known unfixable upstream security advisories (with no available patched release)
const IGNORED_ADVISORIES = new Set([
  'GHSA-VFJ7-8CJW-P6XM', // braces <= 3.0.3
  'GHSA-CH52-4W7C-C8XP', // http-cache-semantics <= 4.2.0
]);

let output = '';
try {
  output = execSync('npm audit --json', { encoding: 'utf8', maxBuffer: 10 * 1024 * 1024 });
} catch (error) {
  output = String(error.stdout || '');
}

if (!output) {
  console.error('Failed to obtain npm audit output');
  process.exit(1);
}

let auditResult;
try {
  auditResult = JSON.parse(output);
} catch (e) {
  console.error('Failed to parse npm audit JSON output:', e);
  process.exit(1);
}

if (auditResult.error) {
  console.error('npm audit reported error:', auditResult.error);
  process.exit(1);
}

const vulnerabilities = auditResult.vulnerabilities || {};

function getAdvisoriesForPackage(pkgName, vulnerabilitiesMap, visited = new Set()) {
  if (visited.has(pkgName)) return [];
  visited.add(pkgName);

  const vuln = vulnerabilitiesMap[pkgName];
  if (!vuln) return [];

  const advisories = [];
  const viaList = Array.isArray(vuln.via) ? vuln.via : [];

  for (const item of viaList) {
    if (item && typeof item === 'object') {
      advisories.push({ ...item, pkgName, vulnSeverity: vuln.severity });
    } else if (typeof item === 'string') {
      if (item.match(/^GHSA-[a-zA-Z0-9-]+$/i)) {
        advisories.push({
          url: `https://github.com/advisories/${item}`,
          title: item,
          severity: vuln.severity,
          pkgName,
          vulnSeverity: vuln.severity,
        });
      } else {
        advisories.push(...getAdvisoriesForPackage(item, vulnerabilitiesMap, visited));
      }
    }
  }
  return advisories;
}

const unhandledAdvisories = [];
const seenKeys = new Set();

for (const pkgName of Object.keys(vulnerabilities)) {
  const advisories = getAdvisoriesForPackage(pkgName, vulnerabilities);
  for (const item of advisories) {
    const rawUrl = item.url || (typeof item.source === 'string' ? item.source : '') || item.github_advisory_id || '';
    let match = typeof rawUrl === 'string' ? rawUrl.match(/GHSA-[a-zA-Z0-9-]+/i) : null;
    if (!match && typeof item.title === 'string') {
      match = item.title.match(/GHSA-[a-zA-Z0-9-]+/i);
    }
    const ghsaId = match ? match[0].toUpperCase() : (rawUrl ? String(rawUrl).split('/').pop().toUpperCase() : 'UNKNOWN');
    const severity = String(item.severity || item.vulnSeverity || '').toLowerCase();
    if ((severity === 'high' || severity === 'critical') && !IGNORED_ADVISORIES.has(ghsaId)) {
      const key = `${pkgName}:${ghsaId}`;
      if (!seenKeys.has(key)) {
        seenKeys.add(key);
        unhandledAdvisories.push({
          package: pkgName,
          ghsaId,
          title: item.title || item.name || pkgName,
          severity,
          url: rawUrl,
        });
      }
    }
  }
}

if (unhandledAdvisories.length > 0) {
  console.error('npm audit found unhandled high/critical vulnerabilities:');
  console.error(JSON.stringify(unhandledAdvisories, null, 2));
  process.exit(1);
}

console.log('npm audit check passed (all high/critical advisories are handled or known unfixable upstream issues).');
process.exit(0);
