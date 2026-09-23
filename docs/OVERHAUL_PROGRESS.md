# Linkpoint overhaul progress

Last refreshed: 2026-09-23

This is the authoritative progress page for the cross-platform migration. A
check means code and an automated check are present in this repository. It does
not imply feature parity with a production viewer.

## Delivered in the current baseline

- [x] npm workspace and runnable Vite/React application at `apps/linkpoint`
- [x] React-Linkpoint screens, application state, LLSD utilities, protocol
  experiments, PWA manifest, and 172 passing unit tests
- [x] refreshed Linkpoint title/login experience and responsive layouts
- [x] typed TypeScript command/event contract and mockable `ViewerClient`
- [x] deterministic UI state projection with disconnect cleanup
- [x] Rust workspace split into core, protocol, asset, cache, scene, and native
  adapter boundaries
- [x] renderer-safe simulator object normalization moved out of the retired
  Electron bridge
- [x] 24 named themes synchronized from linkpoint-design
- [x] legacy Android tree retained as a parity and rollback implementation

## Active next milestones

- [ ] Inject `ViewerClient` into the imported React runtime; remove remaining
  `window.linkpointDesktop` checks from screens and protocol experiments
- [ ] Generate TypeScript contracts from Rust rather than maintaining both by
  hand
- [ ] Add a Tauri desktop shell with login-host allowlisting, HTTPS policy,
  secure credential storage, logout, chat, and lifecycle events
- [ ] Port XML-RPC login and redirect validation to Rust with sanitized fixtures
- [ ] Port capabilities/event queue and reliable UDP with packet parity fixtures
- [ ] Convert protocol objects into renderer-neutral scene snapshots/deltas
- [ ] Introduce Babylon.js behind `packages/renderer`
- [ ] Generate Android and iOS shells only after the desktop boundary is stable

## Reference policy

Reference implementations are used to discover behavior and interoperability
requirements, never as a source of blindly copied code:

| Reference | Use | Rules |
| --- | --- | --- |
| [Second Life Viewer](https://github.com/secondlife/viewer) | Canonical open-source protocol and viewer behavior | Record the upstream file/commit in parity tests; preserve license notices for adapted code |
| [Firestorm Viewer](https://github.com/FirestormViewer/phoenix-firestorm) | Widely deployed viewer behavior and OpenSim compatibility | Compare behavior where official code is silent; do not import UI wholesale |
| Lumiya / Lumiya-Redux | Mobile interaction, lifecycle, protocol edge cases | Treat decompiled material as behavioral evidence only; implement independently |
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
