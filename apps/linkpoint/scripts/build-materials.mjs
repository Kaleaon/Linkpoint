// Compiles materials/*.mat into src/linkpoint/renderer/materials/*.filamat with Filament's matc.
//
// The .filamat output is committed, so `npm run build` never needs this script or the network.
// Run `npm run build:materials` after editing a .mat file or upgrading the `filament` package.
//
// matc is not on npm (the `filament` package ships only the wasm runtime), so it is taken from
// the official release archive on GitHub, checked against a pinned SHA-256 and cached under
// node_modules/.cache/filament-matc. Set MATC=/path/to/matc to use a binary you already have.
// The matc version must match the filament runtime (a package built by another version is
// rejected at load time), so MATC_VERSION is pinned next to the `filament` dependency.
//
// Material sources may pull in shared GLSL with a line `//!include name.glsl`, resolved against
// materials/include/. Pass --check to compile into a temp directory and fail if the committed
// blobs differ (matc output is deterministic for a given version).
import { execFileSync } from 'node:child_process';
import { createHash } from 'node:crypto';
import fs from 'node:fs';
import os from 'node:os';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const MATC_VERSION = '1.53.4';
const RELEASE_BASE = `https://github.com/google/filament/releases/download/v${MATC_VERSION}`;
/** SHA-256 of each release archive, so a tampered download is never executed. */
const ARCHIVES = {
  linux: { file: `filament-v${MATC_VERSION}-linux.tgz`, sha256: '7ce0242434927ef4916c58788b49ba06642fae3ec350be0704bf819eb450f8da', binary: 'filament/bin/matc' },
  darwin: { file: `filament-v${MATC_VERSION}-mac.tgz`, sha256: 'b042bb2219b8dd8cdb6a0610dfff6bd2f861d49f88db54befe29040f1e3b0087', binary: 'filament/bin/matc' },
  win32: { file: `filament-v${MATC_VERSION}-windows.tgz`, sha256: '83d061cd63c11abde47b22842e09b7c4df9db4fdb34fd1d15780bc84f72251a1', binary: 'bin/matc.exe' },
};
/** WebGL2 runs ESSL 3.00, which is the mobile shader family at feature level 1. */
const MATC_ARGS = ['--platform', 'mobile', '--api', 'opengl', '--feature-level', '1'];

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..');
const sourceDir = path.join(root, 'materials');
const outputDir = path.join(root, 'src', 'linkpoint', 'renderer', 'materials');
const check = process.argv.includes('--check');

function sha256(file) {
  return createHash('sha256').update(fs.readFileSync(file)).digest('hex');
}

async function findMatc() {
  if (process.env.MATC) return process.env.MATC;
  const archive = ARCHIVES[process.platform];
  if (!archive) throw new Error(`No Filament release archive is known for ${process.platform}; set MATC=/path/to/matc`);
  const cache = path.join(root, 'node_modules', '.cache', 'filament-matc', MATC_VERSION);
  const binary = path.join(cache, archive.binary);
  if (fs.existsSync(binary)) return binary;

  fs.mkdirSync(cache, { recursive: true });
  const tarball = path.join(cache, archive.file);
  const url = `${RELEASE_BASE}/${archive.file}`;
  console.log(`Downloading ${url}`);
  const response = await fetch(url, { redirect: 'follow' });
  if (!response.ok) throw new Error(`Download failed: ${response.status} ${response.statusText}. Set MATC=/path/to/matc to skip it.`);
  fs.writeFileSync(tarball, Buffer.from(await response.arrayBuffer()));
  const actual = sha256(tarball);
  if (actual !== archive.sha256) {
    fs.rmSync(tarball);
    throw new Error(`Checksum mismatch for ${archive.file}: expected ${archive.sha256}, got ${actual}`);
  }
  execFileSync('tar', ['-xzf', tarball, '-C', cache, archive.binary], { stdio: 'inherit' });
  fs.rmSync(tarball);
  if (process.platform !== 'win32') fs.chmodSync(binary, 0o755);
  return binary;
}

function warnOnRuntimeMismatch() {
  try {
    const runtime = JSON.parse(fs.readFileSync(path.join(root, 'node_modules', 'filament', 'package.json'), 'utf8')).version;
    if (runtime !== MATC_VERSION) {
      console.warn(`Warning: installed filament runtime is ${runtime} but matc is ${MATC_VERSION}; keep them on the same release.`);
    }
  } catch { /* the runtime is optional while the renderer is being added */ }
}

/** Replace `//!include name` lines with the named file from materials/include. */
function expandIncludes(source, name) {
  return source.replace(/^[ \t]*\/\/!include[ \t]+(\S+)[ \t]*$/gm, (_, include) => {
    const file = path.join(sourceDir, 'include', include);
    if (!fs.existsSync(file)) throw new Error(`${name}: include "${include}" not found in materials/include`);
    return fs.readFileSync(file, 'utf8');
  });
}

const matc = await findMatc();
warnOnRuntimeMismatch();

const sources = fs.readdirSync(sourceDir).filter((file) => file.endsWith('.mat')).sort();
if (!sources.length) throw new Error('No materials/*.mat files found');

const target = fs.mkdtempSync(path.join(os.tmpdir(), 'linkpoint-materials-'));
fs.mkdirSync(target, { recursive: true });
const work = fs.mkdtempSync(path.join(os.tmpdir(), 'linkpoint-mat-src-'));
let failed = false;
try {
  for (const file of sources) {
    const name = file.replace(/\.mat$/, '');
    const expanded = path.join(work, file);
    fs.writeFileSync(expanded, expandIncludes(fs.readFileSync(path.join(sourceDir, file), 'utf8'), file));
    const out = path.join(target, `${name}.filamat`);
    execFileSync(matc, [...MATC_ARGS, '-o', out, expanded], { stdio: 'inherit' });
    const committed = path.join(outputDir, `${name}.filamat.base64`);
    const raw = fs.readFileSync(out).toString('base64');
    const encoded = raw.match(/.{1,76}/g).join('\n') + '\n';
    console.log(`${file} -> ${path.relative(root, committed)} (${fs.statSync(out).size} decoded bytes)`);
    if (check) {
      if (!fs.existsSync(committed) || fs.readFileSync(committed, 'utf8') !== encoded) {
        console.error(`${name}.filamat.base64 is out of date; run npm run build:materials`);
        failed = true;
      }
    } else {
      fs.writeFileSync(committed, encoded);
    }

  }
} finally {
  fs.rmSync(work, { recursive: true, force: true });
  fs.rmSync(target, { recursive: true, force: true });
}
if (failed) process.exit(1);
