import { execSync } from 'node:child_process';

// Known unfixable upstream security advisories (with no available patched release)
const IGNORED_ADVISORIES = new Set([
  'GHSA-vfj7-8cjw-p6xm', // braces <= 3.0.3
  'GHSA-ch52-4w7c-c8xp', // http-cache-semantics <= 4.2.0
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

const vulnerabilities = auditResult.vulnerabilities || {};
const unhandledAdvisories = [];

for (const [pkgName, vuln] of Object.entries(vulnerabilities)) {
  const viaList = Array.isArray(vuln.via) ? vuln.via : [];
  for (const item of viaList) {
    if (typeof item === 'object' && item.url) {
      const ghsaId = item.url.split('/').pop();
      const severity = item.severity || vuln.severity;
      if ((severity === 'high' || severity === 'critical') && !IGNORED_ADVISORIES.has(ghsaId)) {
        unhandledAdvisories.push({
          package: pkgName,
          ghsaId,
          title: item.title,
          severity,
          url: item.url,
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
