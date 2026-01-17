# Start Here - Linkpoint Development Guide

## Quick Start

1. **Build the app:**
   ```bash
   ./gradlew assembleDebug
   ```

2. **Install on device:**
   ```bash
   adb install -r Linkpoint/build/outputs/apk/debug/Linkpoint-debug.apk
   ```

3. **Check status:**
   - See [docs/FIXES_AND_STATUS.md](docs/FIXES_AND_STATUS.md) for current issues
   - See [todo.md](todo.md) for task tracking

---

## Project Structure

```
Linkpoint/
├── README.md                 # Project overview
├── todo.md                   # Task tracking
├── docs/
│   ├── FIXES_AND_STATUS.md   # Fix history & issues
│   └── [technical docs]      # Protocol guides
└── Linkpoint/src/main/kotlin/
    └── com/linkpoint/
        ├── slproto/          # SL protocol
        ├── modern/           # Modern components
        ├── ui/               # UI layer
        └── render/           # 3D rendering
```

---

## Current Status (January 2026)

### ✅ Working
- HTTP login
- UDP connection
- Capabilities fetching
- Event queue

### ⚠️ In Progress
- World object loading
- Avatar loading
- 3D rendering swap chain
- Region name parsing

---

## Key Files

| Purpose | File |
|---------|------|
| UDP protocol | `slproto/udp/SLUDPConnection.kt` |
| Message handlers | `slproto/udp/handlers/` |
| Connection state | `slproto/SLConnection.kt` |
| Rendering | `render/FilamentRenderer.kt` |

---

## Debug Tips

1. **Logcat filter:** `tag:SL OR tag:UDP OR tag:Linkpoint`
2. **Enable debug report:** Settings → Generate Debug Report
3. **Check handlers:** Look for registered message handlers in log