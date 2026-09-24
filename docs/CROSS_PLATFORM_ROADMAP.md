# Linkpoint cross-platform delivery roadmap

Last updated: 2026-09-23

## What can be built today

| Target | Configuration | Current verification | Status |
| --- | --- | --- | --- |
| Web/PWA | `apps/linkpoint`, npm workspace, Vite | Typecheck, 174 unit tests, production bundle, browser smoke test | Runnable baseline |
| Android (legacy Kotlin) | root/`Linkpoint` Gradle projects and Android workflows | Gradle unit tests, lint, debug/release APK jobs | Existing application; not the new Tauri shell |
| Linux/Windows/macOS web runtime | `.github/workflows/cross-platform.yml` OS matrix | npm check/build and Rust tests/clippy | Automated portability checks |
| Android (Tauri) | Desktop shell exists; mobile project not initialized | None | Not runnable yet |
| iOS (Tauri) | Desktop shell exists; requires macOS/Xcode/signing | None | Not runnable yet |
| Windows/macOS/Linux native (Tauri) | `apps/linkpoint/src-tauri` least-privilege shell | Core tests only; transport fails closed | Scaffolded, not release-ready |

The repository therefore does **not** yet support an honest “fully working on
all platforms” claim. Passing web and core tests proves that the imported UI and
portable libraries build; it does not prove simulator login, UDP, rendering,
mobile lifecycle behavior, signing, or store packaging on physical devices.

## How to test the current baseline

Run the same checks used by CI:

```bash
npm ci
npm run check
cargo test --workspace --locked
cargo clippy --workspace --all-targets --locked -- -D warnings
```

For an interactive web smoke test:

```bash
npm run dev
```

Open the printed local URL and verify login validation, responsive navigation,
theme changes, offline-grid setup, inventory empty states, chat error handling,
and 3D-view fallback behavior. Do not use a production account until the native
security boundary and end-to-end credential tests are complete.

## Definition of fully working

A platform is “working” only when CI produces an installable artifact and a
device-level test record covers:

1. launch, upgrade, suspend/resume, and clean shutdown;
2. secure credential storage and redaction from logs;
3. Second Life and OpenSim login, redirects, logout, and reconnect;
4. reliable UDP, capabilities, event queue, throttling, and circuit recovery;
5. local/IM/group chat, inventory, friends, groups, parcels, and teleport;
6. scene updates, terrain, avatars, attachments, textures, meshes, and picking;
7. accessibility, responsive layouts, notifications, and platform permissions;
8. offline/error behavior, cache limits, and destructive migration rollback;
9. signed packaging and installation on the oldest and newest supported OS.

## Delivery plan

### Phase 0 — make the baseline enforceable

- Keep `npm run check`, Rust tests, clippy, and the three-OS CI matrix green.
- Add dependency, secret, license, and generated-artifact checks.
- Record sanitized protocol fixtures and prohibit credentials or resident data.
- Exit gate: every pull request exercises the web app and portable Rust core.

### Phase 1 — enforce the runtime boundary (complete)

- Implement `TauriViewerClient`, `WebViewerClient`, and `MockViewerClient`.
- Inject one client during bootstrap and remove `window.linkpointDesktop` and
  direct protocol-manager access from React screens.
- Generate TypeScript command/event definitions from the Rust schema.
- Exit gate: UI integration tests run entirely against `MockViewerClient` and a
  contract test proves TypeScript/Rust serialization compatibility.

### Phase 2 — create the desktop Tauri application (scaffold complete; transport and packaging remain)

- Generate `apps/linkpoint/src-tauri` with least-privilege capabilities.
- Implement HTTPS endpoint policy, secure storage, login/logout, event channels,
  crash-safe lifecycle handling, and signed configuration.
- Add Linux, Windows, and macOS debug packaging jobs; retain artifacts in CI.
- Exit gate: fresh-machine install and login/chat/logout tests pass on all three.

### Phase 3 — protocol and world parity

- Port XML-RPC redirects, reliable UDP/ACKs, capabilities, and event queue to
  `linkpoint-protocol` using sanitized golden fixtures.
- Port inventory, friends, groups, parcels, teleports, appearance, and assets.
- Normalize all world state through `linkpoint-scene`; add Babylon.js only
  behind `packages/renderer`.
- Exit gate: scripted login-to-render and reconnect scenarios pass without the
  legacy JavaScript or Electron protocol paths.

### Phase 4 — Android and iOS shells

- Run `tauri android init` and `tauri ios init` only after desktop contracts are
  stable; keep Kotlin/Swift limited to narrowly scoped plugins.
- Add notification, secure-storage, file-access, connectivity, and lifecycle
  plugins with permission tests.
- Add emulator/simulator CI; validate cellular switching and background restore
  on physical devices. iOS release builds run on macOS with Apple signing.
- Exit gate: signed internal Android and iOS builds pass the full device matrix.

### Phase 5 — release readiness and legacy retirement

- Add reproducible release builds, SBOMs, provenance, signing, update channels,
  crash reporting, privacy review, and store metadata.
- Run accessibility, performance, battery, memory, bandwidth, and soak tests.
- Compare every parity item against the legacy Android app and Lumiya behavioral
  references, then document rollback and data migration.
- Exit gate: production-equivalent parity is signed off before legacy removal.

The `next-v*` preview workflow now creates a normalized web archive, npm SBOM,
Rust dependency inventory, checksums, and GitHub provenance attestation. Native
code signing, update channels, store metadata, and production parity sign-off
remain release-environment work and are not implied by that preview artifact.

## Required test matrix

| Layer | Pull request | Nightly | Release |
| --- | --- | --- | --- |
| TypeScript | Typecheck, unit, component, production build | browser E2E | browser compatibility |
| Rust core | fmt, clippy, unit, contract fixtures | fuzz/soak/reconnect | locked reproducible build |
| Desktop | smoke test after Phase 2 | login-to-render on 3 OSes | signed install/upgrade/uninstall |
| Android | legacy Gradle checks now; Tauri checks after Phase 4 | emulator + physical smoke | signed AAB/device matrix |
| iOS | readiness only until Phase 4 | simulator + physical smoke | signed archive/device matrix |
| Security | secret/dependency scan | endpoint-policy abuse suite | threat-model and privacy sign-off |

Each phase should be delivered in small pull requests with its exit-gate evidence
linked from `docs/OVERHAUL_PROGRESS.md`; scaffolding alone must never be marked as
feature complete. Protocol and parity slices should follow the
[Lumiya-Redux working guide](LUMIYA_REDUX_WORKING_GUIDE.md), which translates
the reference project's conformance practices into the Rust-owned protocol and
React `ViewerClient` boundaries used here.
