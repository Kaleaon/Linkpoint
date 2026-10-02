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

function findWorkspaceRoot(startDir) {
  let dir = path.resolve(startDir);
  for (;;) {
    const pkgPath = path.join(dir, 'package.json');
    if (fs.existsSync(pkgPath)) {
      try {
        const pkg = JSON.parse(fs.readFileSync(pkgPath, 'utf8'));
        if (pkg.workspaces) return dir;
      } catch (_) {}
    }
    const parent = path.dirname(dir);
    if (parent === dir) return startDir;
    dir = parent;
  }
}

function isSubpathOrEqual(dir, root) {
  if (!dir || !root) return false;
  const rel = path.relative(root, dir);
  return !rel.startsWith('..') && !path.isAbsolute(rel);
}

// Resolve a package the way Node does: walk up looking for node_modules.
function resolvePackageDir(name, fromDir, workspaceRoot) {
  let dir = path.resolve(fromDir);
  const resolvedWorkspaceRoot = path.resolve(workspaceRoot);
  for (;;) {
    const candidate = path.join(dir, 'node_modules', name);
    if (fs.existsSync(path.join(candidate, 'package.json'))) return candidate;
    const parent = path.dirname(dir);
    if (parent === dir || !isSubpathOrEqual(parent, resolvedWorkspaceRoot)) return null;
    dir = parent;
  }
}

function dependencyClosure(rootDir, workspaceRoot) {
  const keptPackages = new Set();
  const keptNames = new Set();
  const stack = mainProcessDeps(rootDir).map((name) => [name, rootDir]);

  while (stack.length) {
    const [name, fromDir] = stack.pop();
    if (NOT_ACTUALLY_RUNTIME.has(name)) continue;

    const dir = resolvePackageDir(name, fromDir, workspaceRoot);
    if (!dir) continue; // optional/unmet dependency; nothing to package

    if (keptPackages.has(dir)) continue;
    keptPackages.add(dir);

    try {
      const manifest = JSON.parse(fs.readFileSync(path.join(dir, 'package.json'), 'utf8'));
      if (manifest.name) keptNames.add(manifest.name);

      // Optional dependencies matter: sharp ships its native binary as a platform-specific optional package.
      for (const dep of Object.keys({ ...manifest.dependencies, ...manifest.optionalDependencies })) stack.push([dep, dir]);
    } catch (_) {}
  }

  return keptNames;
}

// Every package directly under node_modules (both local and monorepo workspace root), with scopes expanded.
function topLevelPackages(rootDir, workspaceRoot) {
  const names = new Set();
  const searchDirs = [
    path.join(rootDir, 'node_modules'),
    path.join(workspaceRoot, 'node_modules'),
  ];
  for (const modulesDir of searchDirs) {
    if (!fs.existsSync(modulesDir)) continue;
    for (const entry of fs.readdirSync(modulesDir, { withFileTypes: true })) {
      if (!entry.isDirectory() || entry.name.startsWith('.')) continue;
      if (entry.name.startsWith('@')) {
        const scopedDir = path.join(modulesDir, entry.name);
        for (const scoped of fs.readdirSync(scopedDir, { withFileTypes: true })) {
          if (scoped.isDirectory()) names.add(`${entry.name}/${scoped.name}`);
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
  const workspaceRoot = findWorkspaceRoot(rootDir);
  const keep = dependencyClosure(rootDir, workspaceRoot);
  return topLevelPackages(rootDir, workspaceRoot)
    .filter((name) => !keep.has(name))
    .sort();
}

const rootDir = __dirname;

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
