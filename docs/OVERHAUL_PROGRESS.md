# Linkpoint overhaul progress

Last refreshed: 2026-09-23

This is the authoritative progress page for the cross-platform migration. A
check means code and an automated check are present in this repository. It does
not imply feature parity with a production viewer.

## Delivered in the current baseline

- [x] npm workspace and runnable Vite/React application at `apps/linkpoint`
- [x] React-Linkpoint screens, application state, LLSD utilities, protocol
  experiments, PWA manifest, and automated unit tests
- [x] refreshed Linkpoint title/login experience and responsive layouts
- [x] typed TypeScript command/event contract and mockable `ViewerClient`
- [x] injected `ViewerClient` runtime with web, Tauri, and mock adapters; screens
  consume projected client state instead of the retired desktop global
- [x] Rust-owned generated TypeScript contract with drift and serialization tests
- [x] deterministic UI state projection with disconnect cleanup
- [x] Rust workspace split into core, protocol, asset, cache, scene, and native
  adapter boundaries
- [x] portable protocol foundation for LLSD XML/binary/notation, XML-RPC login
  request construction, endpoint policy, seed capabilities, event queues, and
  reliable UDP sequencing/retry behavior
- [x] renderer-safe simulator object normalization moved out of the retired
  Electron bridge
- [x] 24 named themes synchronized from linkpoint-design
- [x] deterministic core session state machine for login/logout, lifecycle,
  network loss, reconnect, chat/object command gating, and redacted errors
- [x] bounded HTTP/UDP transport interfaces, appended ACK codec, ACK batching,
  scene normalization, asset scheduling, and lazy Babylon renderer boundary
- [x] least-privilege Tauri 2 shell scaffold and GitHub Pages preview pipeline
- [x] threat model, secret/dependency gates, CSP, and private-cache exclusions
- [x] legacy Android tree retained as a parity and rollback implementation

## Active next milestones

The ordered implementation and platform validation plan is maintained in
[`CROSS_PLATFORM_ROADMAP.md`](CROSS_PLATFORM_ROADMAP.md). The cross-platform CI
workflow currently validates the web application and portable Rust crates on
Linux, Windows, and macOS. The Tauri shell is generated, but packaging remains
disabled until its production transport and platform security plugins land.

- [x] Inject `ViewerClient` into the imported React runtime; remove remaining
  `window.linkpointDesktop` checks from screens and protocol experiments
- [x] Generate TypeScript contracts from the Rust-owned schema rather than
  maintaining both by hand
- [ ] Connect the generated Tauri shell to production HTTP/UDP transports and
  platform secure credential storage (the shell currently fails closed)
- [x] Port XML-RPC login request construction and endpoint validation to Rust
- [x] Port LLSD, seed capabilities/event-queue shapes, and transport-independent
  reliable UDP sequencing/retries to Rust
- [x] Parse XML-RPC login redirects and add UDP header/zero-code framing
- [ ] Implement the concrete native HTTP adapter, simulator handshake,
  message-template codecs, throttling, circuit recovery, and golden fixtures
- [x] Add renderer-neutral transforms/material/entity kinds and normalization
- [x] Introduce lazy-loaded Babylon.js behind `packages/renderer`
- [ ] Feed decoded native object/terrain/avatar updates into that scene boundary
- [ ] Generate Android and iOS shells only after the desktop boundary is stable

## Reference policy

Reference implementations are used to discover behavior and interoperability
requirements, never as a source of blindly copied code:

| Reference | Use | Rules |
| --- | --- | --- |
| [Second Life Viewer](https://github.com/secondlife/viewer) | Canonical open-source protocol and viewer behavior | Record the upstream file/commit in parity tests; preserve license notices for adapted code |
| [Firestorm Viewer](https://github.com/FirestormViewer/phoenix-firestorm) | Widely deployed viewer behavior and OpenSim compatibility | Compare behavior where official code is silent; do not import UI wholesale |
| Lumiya / Lumiya-Redux | Executable compatibility oracle, mobile behavior, protocol edge cases, conformance tooling | Pin verified revisions; use synthetic differential fixtures; review provenance, licensing, and notices before any direct source adaptation |
| [Second Life protocol documentation](https://wiki.secondlife.com/wiki/Category:Protocols) | Message and capability documentation | Prefer documented wire behavior and validate against sanitized fixtures |

No credentials, session identifiers, private chat, inventory names, or other
resident data may be committed in fixtures. The project remains an independent
third-party viewer and must continue to follow the Second Life Third-Party
Viewer Policy.

## Definition of “working”

The web application is considered working when install, typecheck, unit tests,
and a production build pass. Native viewer functionality is considered working
only after login, circuit handshake, event queue, reconnection, world state,
chat, inventory, asset decoding, rendering, and lifecycle behavior have fixture
tests plus a device-level validation record. Until then, progress is explicitly
reported as foundation, prototype, or in progress.
