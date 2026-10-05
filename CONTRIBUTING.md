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

Running `npm ci` automatically configures local Git hooks via `git config core.hooksPath .githooks` using `scripts/install_git_hooks.mjs`.

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
- Do not copy recovered or analyzed source indiscriminately. Small adaptations
  require file-level provenance, license/notice review, maintainer approval,
  attribution, and tests; otherwise implement independently from behavior.

See [docs/REPOSITORY_LAYOUT.md](docs/REPOSITORY_LAYOUT.md) for ownership details
and [docs/OVERHAUL_ARCHITECTURE.md](docs/OVERHAUL_ARCHITECTURE.md) for dependency
rules. For protocol or parity work informed by Lumiya, follow the
[Lumiya-Redux working guide](docs/LUMIYA_REDUX_WORKING_GUIDE.md) to establish an
executable oracle, sanitize fixtures, and apply the source-review gate.

## Validation & Itemized CI/CD Tasks

When you run `git commit`, local Git hooks in `.githooks/pre-commit` validate your staged changes.

### Local Verification Tasks

Before submitting a pull request, run the itemized validation tasks locally:

1. **Task 1 - Pre-Commit & Linting**: `pre-commit run --all-files` (or `npm run check:contract`)
2. **Task 2 - Android Verification**: `./gradlew :Linkpoint:testStableDebugUnitTest :Linkpoint:lintStableDebug`
3. **Task 3 - Web Workspace Validation**: `npm run check:web`
4. **Task 4 - Rust Core Validation**: `cargo fmt --all --check && cargo clippy --workspace --all-targets --locked`
5. **Task 5 - Protocol Conformance**: `Linkpoint/tools/protocol/run_conformance.sh --skip-gradle`
6. **Task 6 - Complete Active Validation**: `npm run check`

### Automated CI/CD Pipeline

The repository CI workflow (`.github/workflows/ci.yml`) automatically enforces all 6 tasks on pull requests and pushes to `main` and `develop`. Release builds and web deployments are handled by `.github/workflows/release.yml`.

To run code coverage reports locally across components:

```bash
# Android JaCoCo coverage
./gradlew testDebugUnitTest jacocoTestReport

# Web Vitest V8 coverage
npm run test:coverage

# Rust LLVM coverage
cargo llvm-cov --workspace --lcov --output-path lcov.info

# Evaluate quality gate locally
python3 scripts/evaluate_coverage.py
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
