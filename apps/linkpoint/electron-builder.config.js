/**
 * electron-builder configuration for the Linkpoint desktop packages.
 *
 * The desktop app is the Vite web build (dist/) loaded from disk by
 * electron/main.cjs. `main` is injected through extraMetadata rather than a
 * top-level package.json "main" field, because Expo resolves the NATIVE app
 * entry from that same field and would otherwise make the Android and iOS
 * builds bundle the Electron main process.
 *
 * This is a .js config rather than a static .yml so that `files` can be
 * computed: the main process needs exactly one dependency tree from
 * node_modules (@caspertech/node-metaverse, for the native simulator session)
 * and nothing else. Shipping every production dependency instead puts the web
 * and React Native stacks in the package and takes the asar from ~27 MB to
 * ~280 MB; hand-listing the tree would go stale the next time a dependency
 * changes, so it is resolved from the installed tree at build time.
 */
const fs = require('node:fs');
const path = require('node:path');

// Packages the main process requires at runtime, found by scanning the Electron entry and the shared
// core it loads (every bare `require('pkg')`), so adding a dependency to either cannot be forgotten here.
function mainProcessDeps(rootDir) {
  const deps = new Set();
  for (const dir of ['electron', 'core']) {
    const directory = path.join(rootDir, dir);
    if (!fs.existsSync(directory)) continue;
    for (const file of fs.readdirSync(directory)) {
      if (!file.endsWith('.cjs')) continue;
      const source = fs.readFileSync(path.join(directory, file), 'utf8');
      for (const match of source.matchAll(/require\(\s*['"]([^'"]+)['"]\s*\)/g)) {
        const request = match[1];
        if (request.startsWith('.') || request.startsWith('node:') || request === 'electron') continue;
        const segments = request.split('/');
        deps.add(request.startsWith('@') ? segments.slice(0, 2).join('/') : segments[0]);
      }
    }
  }
  // Node built-ins required without the node: prefix
  for (const builtin of require('node:module').builtinModules) deps.delete(builtin);
  return [...deps].sort();
}

// node-metaverse declares vitest as a runtime dependency upstream rather than a
// dev one. The main process never loads it, and pulling it in drags vite,
// rollup and esbuild along with it.
const NOT_ACTUALLY_RUNTIME = new Set(['vitest']);

const rootDir = __dirname;
const workspaceRoot = path.resolve(rootDir, '../..');

function isSubdirectoryOrSame(child, parent) {
  const relative = path.relative(parent, child);
  return relative === '' || (!relative.startsWith('..') && !path.isAbsolute(relative));
}

// Resolve a package the way Node does: walk up looking for node_modules.
function resolvePackageDir(name, fromDir, stopDir = workspaceRoot) {
  let dir = fromDir;
  for (;;) {
    const candidate = path.join(dir, 'node_modules', name);
    if (fs.existsSync(path.join(candidate, 'package.json'))) return candidate;
    const parent = path.dirname(dir);
    if (parent === dir || !isSubdirectoryOrSame(dir, stopDir)) return null;
    dir = parent;
  }
}

function dependencyClosure(rootDir) {
  const found = new Set();
  const stack = mainProcessDeps(rootDir).map((name) => [name, rootDir]);

  while (stack.length) {
    const [name, fromDir] = stack.pop();
    if (NOT_ACTUALLY_RUNTIME.has(name)) continue;

    const dir = resolvePackageDir(name, fromDir, workspaceRoot);
    if (!dir) continue; // optional/unmet dependency; nothing to package

    const relative = path.relative(workspaceRoot, dir).split(path.sep).join('/');
    if (found.has(relative)) continue;
    found.add(relative);

    const manifest = JSON.parse(fs.readFileSync(path.join(dir, 'package.json'), 'utf8'));
    // Optional dependencies matter: sharp ships its native binary as a platform-specific optional package.
    for (const dep of Object.keys({ ...manifest.dependencies, ...manifest.optionalDependencies })) stack.push([dep, dir]);
  }

  return found;
}

// Every package directly under node_modules (app or workspace level), with scopes expanded.
function topLevelPackages(rootDir) {
  const names = new Set();
  for (const modulesDir of [path.join(workspaceRoot, 'node_modules'), path.join(rootDir, 'node_modules')]) {
    if (!fs.existsSync(modulesDir)) continue;
    for (const entry of fs.readdirSync(modulesDir, { withFileTypes: true })) {
      if (!entry.isDirectory() || entry.name.startsWith('.')) continue;
      if (entry.name.startsWith('@')) {
        const scopeDir = path.join(modulesDir, entry.name);
        if (fs.existsSync(scopeDir)) {
          for (const scoped of fs.readdirSync(scopeDir, { withFileTypes: true })) {
            if (scoped.isDirectory()) names.add(`${entry.name}/${scoped.name}`);
          }
        }
      } else {
        names.add(entry.name);
      }
    }
  }
  return [...names];
}

// Exclude the top-level packages the main process does not need, rather than
// excluding all of node_modules and adding wanted packages back. electron-builder
// resolves nested node_modules through its own dependency walker, and a blanket
// `!node_modules/**/*` defeats it: @caspertech/node-metaverse/node_modules/long
// was dropped that way and the packaged app died on `Cannot find module 'long'`.
function unusedTopLevelPackages(rootDir) {
  const keep = new Set();
  for (const dir of dependencyClosure(rootDir)) {
    const segments = dir.split('/').filter((s) => s !== 'node_modules');
    // Keep the outermost package, which carries any nested node_modules with
    // it, AND the package's own name at top level. A dependency can be present
    // both hoisted and nested, and electron-builder's walker may package the
    // hoisted copy while this closure resolved the nested one -- dropping the
    // hoisted copy then leaves the app with neither (`Cannot find module
    // 'xml2js'` at startup).
    for (const depth of [0, segments.length - 1]) {
      const name = segments[depth];
      if (!name) continue;
      keep.add(name.startsWith('@') ? `${name}/${segments[depth + 1]}` : name);
    }
  }
  return topLevelPackages(rootDir)
    .filter((name) => !keep.has(name))
    .sort();
}


module.exports = {
  appId: 'io.linkpoint.viewer',
  productName: 'Linkpoint',
  copyright: 'Copyright © Linkpoint contributors',
  asar: true,

  directories: { output: 'build-desktop' },

  files: [
    'dist/**/*',
    'electron/**/*',
    'core/**/*',
    'package.json',
    '!node_modules/react/**/*',
    '!node_modules/vite/**/*',
    '!node_modules/vitest/**/*',
    ...unusedTopLevelPackages(rootDir).map((name) => `!node_modules/${name}/**/*`),
  ],

  extraMetadata: { main: 'electron/main.cjs' },

  artifactName: '${productName}-${version}-${os}-${arch}.${ext}',

  win: {
    target: [
      { target: 'nsis', arch: ['x64', 'arm64'] },
      // A single executable that can be copied to a USB drive and launched
      // without an installer or administrator access.
      { target: 'portable', arch: ['x64', 'arm64'] },
      { target: 'zip', arch: ['x64', 'arm64'] },
    ],
  },
  nsis: {
    oneClick: false,
    perMachine: false,
    allowToChangeInstallationDirectory: true,
    artifactName: '${productName}-${version}-windows-${arch}-setup.${ext}',
  },
  portable: {
    artifactName: '${productName}-${version}-windows-${arch}-portable.${ext}',
  },

  mac: {
    category: 'public.app-category.social-networking',
    target: [
      { target: 'dmg', arch: ['x64', 'arm64'] },
      { target: 'zip', arch: ['x64', 'arm64'] },
    ],
  },
  dmg: { writeUpdateInfo: false },

  linux: {
    category: 'Network',
    synopsis: 'Second Life communicator and viewer utility suite',
    maintainer: 'Linkpoint Contributors <noreply@users.noreply.github.com>',
    target: [
      { target: 'AppImage', arch: ['x64'] },
      { target: 'deb', arch: ['x64'] },
    ],
  },
};
