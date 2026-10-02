import { readdirSync, readFileSync } from 'node:fs';
import { createRequire } from 'node:module';
import path from 'node:path';
import { describe, expect, it } from 'vitest';

const require = createRequire(import.meta.url);
const root = path.resolve(__dirname, '../../..');
const config = require('../../../electron-builder.config.js') as { files: string[]; extraMetadata: { main: string } };
const excluded = new Set(config.files.filter((f) => f.startsWith('!node_modules/')).map((f) => f.slice('!node_modules/'.length).replace(/\/\*\*\/\*$/, '')));

/** Bare package names the main process and shared core load at runtime. */
function runtimeRequires() {
  const names = new Set<string>();
  for (const dir of ['electron', 'core']) {
    for (const file of readdirSync(path.join(root, dir)).filter((f) => f.endsWith('.cjs'))) {
      for (const m of readFileSync(path.join(root, dir, file), 'utf8').matchAll(/require\(\s*['"]([^'"]+)['"]\s*\)/g)) {
        const request = m[1];
        if (request.startsWith('.') || request.startsWith('node:') || request === 'electron') continue;
        const segments = request.split('/');
        names.add(request.startsWith('@') ? segments.slice(0, 2).join('/') : segments[0]);
      }
    }
  }
  return [...names];
}

describe('desktop packaging', () => {
  it('ships the shared core and the Electron entry', () => {
    expect(config.files).toEqual(expect.arrayContaining(['dist/**/*', 'electron/**/*', 'core/**/*']));
    expect(config.extraMetadata.main).toBe('electron/main.cjs');
  });

  it('keeps every package the main process and shared core require (sharp and jpeg2000 are loaded at startup)', () => {
    const packages = runtimeRequires().filter((name) => !require('node:module').builtinModules.includes(name));
    expect(packages).toEqual(expect.arrayContaining(['sharp', 'jpeg2000', '@caspertech/node-metaverse']));
    for (const name of packages) expect(excluded.has(name), `${name} would be left out of the package`).toBe(false);
  });

  it('keeps sharp\'s platform binaries (optional dependencies) and still drops renderer-only packages', () => {
    expect(excluded.has('@img/sharp-linux-x64')).toBe(false);
    expect(excluded.has('@img/sharp-win32-x64')).toBe(false);
    for (const name of ['react', 'vite', 'vitest']) expect(excluded.has(name), name).toBe(true);
  });
});
