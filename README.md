# Linkpoint

A modern Android viewer for Second Life and OpenSimulator virtual worlds.

> ⚠️ **Disclaimer:** Linkpoint is not provided or supported by Linden Lab. This is an independent, community-developed third-party viewer that complies with [Linden Lab's Third-Party Viewer Policy](https://secondlife.com/corporate/third-party-viewers).

---

## Current Status (January 2026)

| Component | Status | Notes |
|-----------|--------|-------|
| Build | ✅ Working | APK builds successfully |
| Login | ✅ Working | Connects to SL grid |
| UDP Connection | ✅ Working | Socket connected |
| Capabilities | ✅ Working | 12 caps loaded |
| Event Queue | ✅ Working | 18 handlers active |
| World Loading | ⚠️ In Progress | Objects/avatars not populating |
| 3D Rendering | ⚠️ In Progress | Swap chain issues |

**See [docs/FIXES_AND_STATUS.md](docs/FIXES_AND_STATUS.md) for detailed fix history and remaining issues.**

---

## Quick Start

### Prerequisites
- Android Studio Arctic Fox or later
- JDK 17+
- Android SDK 35

### Build
```bash
./gradlew assembleDebug
```

### Install
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