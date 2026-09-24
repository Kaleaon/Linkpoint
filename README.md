# Linkpoint

Linkpoint is an independent viewer for Second Life and OpenSimulator. The
active application is **Linkpoint Next**: a React interface backed by a shared
Rust core. The former Kotlin/Android application remains in this repository as
a migration reference and compatibility build, not as the default development
path.

> Linkpoint is not provided or supported by Linden Lab. It is an independent
> third-party viewer intended to comply with the
> [Third-Party Viewer Policy](https://secondlife.com/corporate/third-party-viewers).

## Start here

You need Node.js 22+, npm, and a stable Rust toolchain.

```bash
npm ci
npm run dev
```

The development server runs the React app from `apps/linkpoint`. Before opening
a pull request, validate both halves of the active application:

```bash
npm run check
```

Use `npm run check:web` or `npm run check:rust` while iterating on only one
side of the boundary. See [CONTRIBUTING.md](CONTRIBUTING.md) for the complete
workflow.

## Active architecture

```text
apps/linkpoint              React + Vite application
        │
        ├── packages/design-system
        ├── packages/viewer-store
        └── packages/viewer-client ── packages/viewer-types
                                           │
crates/linkpoint-tauri ── crates/linkpoint-core
        ├── crates/linkpoint-protocol
        ├── crates/linkpoint-assets
        ├── crates/linkpoint-scene
        └── crates/linkpoint-cache
```

The React application talks through `ViewerClient`; it does not depend directly
on native APIs or simulator packets. Rust owns native session, protocol, asset,
scene, and cache behavior. This boundary keeps the UI runnable in a browser and
allows the same core to support desktop and mobile shells.

| Area | State |
| --- | --- |
| React application | Runnable with Vite; covered by type checks and unit tests |
| TypeScript viewer boundary | Typed commands, events, client, and state projection |
| Rust workspace | Initial crate boundaries and foundational tests |
| Tauri packaging | Least-privilege desktop shell scaffolded; production transport/signing remain |
| Protocol and renderer parity | In progress; legacy behavior is retained for reference |

For design constraints and migration gates, read
[the cross-platform architecture](docs/OVERHAUL_ARCHITECTURE.md) and
[the progress board](docs/OVERHAUL_PROGRESS.md).

## Repository map

| Path | Purpose | Status |
| --- | --- | --- |
| `apps/linkpoint/` | Primary React application | Active |
| `packages/` | Shared TypeScript libraries and design tokens | Active |
| `crates/` | Shared Rust viewer core and native adapter | Active |
| `design/` | UI source studies and prototypes | Reference |
| `Linkpoint/` | Previous Kotlin/Android client | Legacy migration source |
| `docs/` | Architecture, migration, protocol, and historical notes | Mixed; use the index |
| `platforms/` | Earlier platform experiments | Reference |
| analysis/source directories | Reverse-engineering and compatibility evidence | Reference only |

The detailed ownership and placement rules are in
[docs/REPOSITORY_LAYOUT.md](docs/REPOSITORY_LAYOUT.md). Reference material must
not be imported into production code.

## Common commands

| Command | Purpose |
| --- | --- |
| `npm run dev` | Start the primary React application |
| `npm run build` | Create its production web bundle |
| `npm run test:web` | Run TypeScript/React tests |
| `npm run check:web` | Type-check, test, and build the web workspace |
| `npm run check:rust` | Format-check, test, and lint the Rust workspace |
| `npm run check` | Run the complete active-workspace validation |
| `./gradlew :Linkpoint:assembleDebug` | Build the legacy Android client when needed |

## Project policies

- [Contributing](CONTRIBUTING.md)
- [Privacy](PRIVACY_POLICY.md)
- [Third-party viewer policy compliance](THIRD_PARTY_VIEWER_POLICY_COMPLIANCE.md)
- [License](LICENSE)
