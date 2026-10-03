import { execSync } from 'node:child_process';
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const rootDir = path.resolve(__dirname, '..');

function installGitHooks() {
  const githooksDir = path.join(rootDir, '.githooks');
  if (!fs.existsSync(githooksDir)) {
    fs.mkdirSync(githooksDir, { recursive: true });
  }

  const preCommitHook = path.join(githooksDir, 'pre-commit');
  if (fs.existsSync(preCommitHook)) {
    try {
      fs.chmodSync(preCommitHook, 0o755);
    } catch {
      // Ignore permission errors on systems that do not support POSIX permissions
    }
  }

  try {
    execSync('git rev-parse --is-inside-work-tree', {
      cwd: rootDir,
      stdio: 'ignore',
    });
  } catch {
    console.log('[git-hooks] Not inside a git repository; skipping core.hooksPath setup.');
    return;
  }

  try {
    execSync('git config core.hooksPath .githooks', {
      cwd: rootDir,
      stdio: 'inherit',
    });
    console.log('[git-hooks] Configured git core.hooksPath to .githooks');
  } catch (err) {
    console.warn('[git-hooks] Failed to configure git core.hooksPath:', err.message);
  }
}

installGitHooks();
