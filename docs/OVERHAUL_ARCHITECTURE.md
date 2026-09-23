# Linkpoint cross-platform overhaul

This document is the entry point for the incremental replacement of the legacy
Android application. The existing application remains buildable while the new
workspace reaches feature parity.

## Inputs and decisions

The overhaul combines three maintained sources rather than treating any one of
them as the new application:

* **React-Linkpoint** supplies the React screen behavior and is the source for
  future work under `apps/linkpoint`.
* **linkpoint-design** owns visual tokens, layouts, accessibility rules, and
  screen specifications. Visual code moves into `packages/design-system`.
* **Lumiya-Redux** and the preserved Lumiya analysis in this repository are
  behavioral references for protocol edge cases and viewer feature parity.
  Decompiled code is reference material, not code to copy into the product.
* The supplied architecture review selects a Tauri 2 shell, a Rust networking
  and session core, a runtime-agnostic React UI, and a renderer-neutral scene
  contract. Babylon.js is the first rendering target once the contract is
  stable.

## Dependency rule

Dependencies point inward and never skip a layer:

```text
React screens -> viewer-store -> viewer-client -> viewer-types
renderer ------------------------------^              |
Tauri adapter -> linkpoint-core -> protocol/assets/scene/cache
```

The UI must not access Electron globals, Tauri commands, simulator packets, or
native storage directly. `ViewerClient` is its only simulator boundary. Rust is
authoritative for session and world state; frontend stores are projections.

## Repository layout

| Path | Responsibility |
| --- | --- |
| `apps/linkpoint` | React application and Tauri shell (introduced next) |
| `packages/viewer-types` | Stable command, event, and snapshot contract |
| `packages/viewer-client` | Runtime-independent client interface and adapters |
| `packages/viewer-store` | Deterministic frontend state projection |
| `packages/design-system` | Synced Linkpoint themes and reusable UI primitives |
| `packages/renderer` | Renderer-neutral scene consumer (introduced later) |
| `crates/linkpoint-core` | Session lifecycle and command/event orchestration |
| `crates/linkpoint-protocol` | Login, capabilities, UDP, and message semantics |
| `crates/linkpoint-assets` | Asset scheduling and decoding boundary |
| `crates/linkpoint-scene` | Normalized scene model and deltas |
| `crates/linkpoint-cache` | Memory/disk cache policy boundary |
| `crates/linkpoint-tauri` | Narrow native command adapter |

## Migration gates

1. **Boundary first:** land typed commands/events, `ViewerClient`, and the state
   projection without changing the shipped Android application.
2. **React application:** import screens a feature at a time, with design tokens
   coming only from `design-system`. A mock client keeps every screen testable.
3. **Tauri desktop:** implement endpoint policy, login, logout, chat, and session
   events in Rust. Keep the old desktop bridge only until these pass parity tests.
4. **Protocol parity:** use sanitized packet fixtures to port reliable UDP,
   capabilities, event queue, object updates, inventory, friends, groups,
   parcels, and teleports. Never commit credentials or captured personal data.
5. **Scene and assets:** normalize simulator data before it reaches the renderer;
   then introduce Babylon.js behind `packages/renderer`.
6. **Mobile:** generate Tauri Android/iOS projects only after desktop session and
   scene contracts are stable. Add secure storage and lifecycle plugins through
   narrow interfaces.

Every gate requires unit tests, a documented parity result, and a rollback path.
The legacy app is removed only after production-equivalent login, messaging,
inventory, world rendering, reconnection, accessibility, and platform packaging
are verified.
