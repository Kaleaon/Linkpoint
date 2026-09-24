# Contributing to Linkpoint

New product work belongs in the React/Rust workspace. Changes to the legacy
Android client should be limited to migration parity, security, and explicitly
scoped maintenance work.

## Prerequisites

- Node.js 22 or newer and npm
- The current stable Rust toolchain with `rustfmt` and `clippy`
- Git
- JDK 17 and Android SDK 35 only when working in `Linkpoint/`

## Setup

```bash
git clone https://github.com/Kaleaon/Linkpoint.git
cd Linkpoint
npm ci
npm run check
```

Start the primary application with `npm run dev`. The Vite app uses the npm
workspaces in `packages/`; do not install dependencies independently inside a
workspace.

## Where changes belong

- Put screens and browser composition in `apps/linkpoint`.
- Put reusable UI and tokens in `packages/design-system`.
- Put runtime-neutral commands and events in `packages/viewer-types`.
- Put frontend transport and projections in `viewer-client` and `viewer-store`.
- Put protocol, session, asset, scene, and cache behavior in the matching Rust
  crate under `crates/`.
- Keep Tauri-specific code inside `crates/linkpoint-tauri` (and the future
  native shell), behind the client contract.
- Do not copy decompiled or analyzed source into active application code.

See [docs/REPOSITORY_LAYOUT.md](docs/REPOSITORY_LAYOUT.md) for ownership details
and [docs/OVERHAUL_ARCHITECTURE.md](docs/OVERHAUL_ARCHITECTURE.md) for dependency
rules.

## Validation

Run the smallest useful check while iterating, then the complete check before
submitting:

```bash
npm run check:web
npm run check:rust
npm run check
```

For a legacy Android change, also run the relevant Gradle task, normally:

```bash
./gradlew :Linkpoint:testStableDebugUnitTest
./gradlew :Linkpoint:lintStableDebug
```

Never commit credentials, local SDK paths, proxy settings, packet captures with
personal data, generated build output, or dependency directories.

## Code and pull requests

- Keep TypeScript strict and prefer explicit contracts at runtime boundaries.
- Format Rust with `cargo fmt`; keep `cargo clippy` warning-free.
- Add tests for behavior and regression fixes.
- Keep commits focused and use imperative, descriptive subjects.
- Explain migration or compatibility impact in the pull request.
- Include a screenshot for visible UI changes.
- Update documentation when a command, boundary, or project status changes.

Contributions are accepted under the repository's [license](LICENSE).
