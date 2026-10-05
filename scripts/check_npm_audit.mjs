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
  output = error.stdout || '';
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
const unhandledAdvisories = [];

for (const [pkgName, vuln] of Object.entries(vulnerabilities)) {
  const viaList = Array.isArray(vuln.via) ? vuln.via : [];
  for (const item of viaList) {
    if (item && typeof item === 'object') {
      const url = item.url || '';
      const match = url.match(/GHSA-[a-zA-Z0-9-]+/i);
      const ghsaId = match ? match[0].toUpperCase() : (url ? url.split('/').pop().toUpperCase() : 'UNKNOWN');
      const rawSeverity = item.severity || vuln.severity || '';
      const severity = rawSeverity.toLowerCase();
      if ((severity === 'high' || severity === 'critical') && !IGNORED_ADVISORIES.has(ghsaId)) {
        unhandledAdvisories.push({
          package: pkgName,
          ghsaId,
          title: item.title || vuln.name || pkgName,
          severity,
          url,
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
