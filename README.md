# Linkpoint

A modern, independent viewer for Second Life and OpenSimulator virtual worlds.

> ⚠️ **Disclaimer:** Linkpoint is not provided or supported by Linden Lab. This is an independent, community-developed third-party viewer that complies with [Linden Lab's Third-Party Viewer Policy](https://secondlife.com/corporate/third-party-viewers).

---

## Current status (September 2026)

Linkpoint is migrating incrementally from the legacy Android client to a shared
React UI with a Rust viewer core and Tauri shell. Status labels describe what is
verified in this repository; they are not claims of production parity.

| Track | Status | Verified scope |
|-------|--------|----------------|
| Legacy Android client | Maintained during migration | Existing Gradle project, protocol stack, and Filament renderer remain available |
| Linkpoint Next web app | Runnable baseline | Vite production build and imported React-Linkpoint unit suite |
| Shared UI contract | Foundation complete | Typed commands/events, client boundary, deterministic state projection |
| Rust viewer core | Foundation only | Workspace boundaries, command serialization, reliable sequence rollover |
| Tauri native shell | Not started | Planned after the frontend/runtime boundary is stable |
| Rust login, capabilities, UDP | Not started | Port requires fixture-backed parity tests before replacing legacy behavior |
| World rendering | Prototype | Imported browser renderer; not yet wired to the Rust scene contract |

See [the overhaul architecture](docs/OVERHAUL_ARCHITECTURE.md) and
[the live overhaul progress board](docs/OVERHAUL_PROGRESS.md) for exact scope.

**See [docs/FIXES_AND_STATUS.md](docs/FIXES_AND_STATUS.md) for detailed fix history and remaining issues.**

---

## Quick Start

### Linkpoint Next (web)

```bash
npm ci --legacy-peer-deps
npm run dev -w @linkpoint/app
```

Build and validate it with `npm run check`.

### Legacy Android client

#### Prerequisites
- Android Studio Arctic Fox or later
- JDK 17+
- Android SDK 35

#### Build
```bash
./gradlew assembleDebug
```

#### Install
```bash
adb install -r Linkpoint/build/outputs/apk/debug/Linkpoint-debug.apk
```

---

## Features

- **Grid Support:** Second Life, Beta Grid, OpenSimulator
- **Modern UI:** Jetpack Compose with Material Design 3
- **3D Rendering:** Filament-based rendering engine
- **Voice Chat:** WebRTC integration
- **Full Protocol:** UDP messages + HTTP capabilities

---

## XR Support & Runtime Requirements

- **Stable builds (`stableDebug` / `stableRelease`)** expose only XR paths marked **Ready** at runtime.
- **Experimental builds (`xrExperimentalDebug` / `xrExperimentalRelease`)** enable OpenXR/Android XR paths marked **Experimental**.
- **Device/runtime checks:**
  - Cardboard mode requires `android.software.vr.mode` or `android.hardware.vr.high_performance`.
  - Android XR requires **Android 15+ (API 35)** and `android.hardware.xr.immersive`.
  - OpenXR requires an available OpenXR runtime class on-device.
- If XR init/rendering fails, the app exits XR and returns to the standard world view.

### Building XR variants

```bash
./gradlew assembleStableDebug
./gradlew assembleXrExperimentalDebug
```

## Documentation

| Document | Description |
|----------|-------------|
| [FIXES_AND_STATUS.md](docs/FIXES_AND_STATUS.md) | Fix history and current issues |
| [Broken_Code_Analysis_and_Fixes.md](docs/Broken_Code_Analysis_and_Fixes.md) | Technical analysis |
| [QUICK_START_GUIDE.md](QUICK_START_GUIDE.md) | Setup instructions |
| [CONTRIBUTING.md](CONTRIBUTING.md) | How to contribute |
| [PRIVACY_POLICY.md](PRIVACY_POLICY.md) | Privacy practices |
| [THIRD_PARTY_VIEWER_POLICY_COMPLIANCE.md](THIRD_PARTY_VIEWER_POLICY_COMPLIANCE.md) | TPV compliance |

---

## Recent Fixes (PRs #222-227)

### ✅ What Worked
- **ACK byte order fix** - Changed appended ACKs from little-endian to big-endian
- **Connection sequence** - Wait for UseCircuitCode ACK before CompleteAgentMovement
- **Missing message handlers** - Added PING_CHECK, TERSE_UPDATE, COARSE_LOCATION, KILL_OBJECT
- **UUID byte order** - Centralized big-endian UUID writes
- **Build infrastructure** - AGP 8.6.1, Kotlin 2.1.0, compileSdk 35
- **Theme crash** - Added missing MD3 color attributes

### ⚠️ Still In Progress
- RegionHandshake name parsing
- Object/avatar scene population
- Swap chain initialization
- ACK timing on high-latency networks

---

## Architecture

```
Linkpoint/src/main/kotlin/com/linkpoint/
├── slproto/          # Second Life protocol implementation
│   ├── udp/          # UDP packet handling
│   ├── caps/         # HTTP capabilities
│   ├── llsd/         # LLSD serialization
│   └── messages/     # Message handlers
├── modern/           # Modern architecture components
│   ├── connection/   # Connection management
│   ├── graphics/     # Texture/rendering
│   └── protocol/     # Protocol abstractions
├── ui/               # User interface
│   └── compose/      # Jetpack Compose screens
└── render/           # 3D rendering (Filament)
```

---

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Submit a pull request

See [CONTRIBUTING.md](CONTRIBUTING.md) for detailed guidelines.

---

## Support

- **Issues:** [GitHub Issues](https://github.com/Kaleaon/Linkpoint/issues)
- **Discussions:** [GitHub Discussions](https://github.com/Kaleaon/Linkpoint/discussions)

---

## License

See [LICENSE](LICENSE) file for details.
