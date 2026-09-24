# Repository layout and ownership

This repository contains the active cross-platform viewer and historical inputs
used to migrate it. The distinction is intentional: proximity in the tree does
not make reference code a production dependency.

## Active product

| Path | Owner and responsibility |
| --- | --- |
| `apps/linkpoint/` | React application composition, routes, screens, and browser entry point |
| `packages/design-system/` | Shared visual tokens and reusable UI foundations |
| `packages/viewer-types/` | Runtime-neutral TypeScript command, event, and snapshot contracts |
| `packages/viewer-client/` | The only UI-facing viewer transport boundary |
| `packages/viewer-store/` | Deterministic projection of viewer events into UI state |
| `crates/linkpoint-core/` | Session orchestration and shared native behavior |
| `crates/linkpoint-protocol/` | Login, capabilities, UDP, and message semantics |
| `crates/linkpoint-assets/` | Asset scheduling and decoding boundary |
| `crates/linkpoint-scene/` | Renderer-neutral world model and scene deltas |
| `crates/linkpoint-cache/` | Memory and persistent cache policies |
| `crates/linkpoint-tauri/` | Thin native adapter; it must not become a second core |

The root `package.json`, `Cargo.toml`, and lockfiles are the canonical workspace
entry points. CI for these paths lives in `.github/workflows/cross-platform.yml`.

## Migration and reference areas

| Path | Use |
| --- | --- |
| `Linkpoint/` | Buildable Kotlin/Android predecessor and behavioral baseline |
| `design/` | Design explorations and screen prototypes |
| `platforms/` | Prior platform experiments |
| `Gauss/`, `LLSD-KOTLIN/` | Vendored or studied supporting projects |
| `apk_analysis/`, `disassembled-apps/`, `lumiya_*`, `secondlife_*` | Compatibility research and source evidence |
| `file_bundle/`, `kotlin-translations/` | Historical migration inputs |

Reference areas may inform tests and documented behavior. Active TypeScript and
Rust code must not import them or copy implementation from decompiled sources.
When behavior is migrated, express it as a clean contract plus independently
written implementation and fixture-backed tests.

## Documentation

Current cross-platform decisions use the `OVERHAUL_*` documents and this file.
Many other documents describe the legacy Android implementation or completed
investigations. Preserve those as historical evidence, but do not treat old
"complete" reports as the status of Linkpoint Next.

When adding documentation:

1. Update `docs/README.md` if it is a durable entry point.
2. Name active architecture documents by subject, not by session or completion.
3. Put generated reports under ignored build/output directories rather than the
   repository root.
4. State whether instructions target Linkpoint Next or the legacy client.

## Dependency direction

Frontend screens depend on stores, clients, and shared types. Native adapters
depend on the Rust core, which delegates to focused domain crates. Packets,
native APIs, and persistence details must not leak into React components.

```text
React -> store -> client -> shared TypeScript contracts
Tauri adapter -> Rust core -> protocol/assets/scene/cache
```

Any bridge between those halves should translate contracts rather than expose a
framework-specific global.
