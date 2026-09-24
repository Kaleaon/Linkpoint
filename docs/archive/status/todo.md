# Linkpoint - Task Tracking

## Current Priority (April 2026)

### 🟡 High Priority - Stability

- [ ] **ACK timing** - Improve reliability on high-latency networks (7+ second latency seen)
- [ ] **Error handling** - Add graceful recovery for packet parsing failures
- [ ] **Reconnection** - Auto-reconnect on connection drop
- [ ] **Swap chain cold-start lifecycle** - PR #454 fixed the eager-create race; verify cold start on first run

### 🟢 Medium Priority - Features

- [ ] **Texture pipeline (Lumiya port)** - Wire `LinkpointTexture` / `MmappedTextureCache` through `TextureManager.decodeTexture` (see docs/lumiya-port/README.md "Wiring follow-ups")
- [ ] **etcpak ETC2/EAC** - Vendor native entry point so `Etc2Compressor` stops falling back to ETC1 opaque-only
- [ ] **Mesh loading** - Load mesh assets via GetMesh capability
- [ ] **Inventory** - Fetch full inventory tree (warm-fetch at FULLY_CONNECTED already lands the skeleton)

---

## ✅ Recently Completed

- [x] World-data wiring: `OBJECT_UPDATE` / `OBJECT_UPDATE_COMPRESSED` / `IMPROVED_TERSE_OBJECT_UPDATE` route into `ObjectManager` and `AvatarManager` (LinkpointApp.kt:933,950,1112)
- [x] RegionHandshake → `sessionManager.updateRegionName` flow (LinkpointApp.kt:731)
- [x] Friends list display-name resolution: don't poison `ProfileManager` cache with UUID-prefix fallbacks; retry on Friends screen open
- [x] Friend tap → ProfileActivity (was a no-op fetch); IM tap already wired
- [x] TextureMemoryTracker surfaced in DebugReportService
- [x] JPEG2000 sub-resolution decode bug + SwapChain attach race + LOCAL chat tab population (PR #454)
- [x] `UseCircuitCode` seq fix preventing world bootstrap (PR #453)
- [x] UDP receive loop hardening (PRs #450 / #451)
- [x] CI: NDK r26 pin + supported `packages` input (PRs #458 / #459)
- [x] Honest debug report (HTTP counters, SwapChain, JPEG2000 backend reasons) (PR #460)

---

## Build Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Run tests
./gradlew test

# Clean build
./gradlew clean assembleDebug
```

---

## Debug Checklist

When testing connection issues:

1. [ ] Check login response in logcat (`Auth: Login Success`)
2. [ ] Verify UDP connection (`UDP Connected: true`)
3. [ ] Confirm capabilities loaded (`Total Capabilities: 12`)
4. [ ] Check for packet ACKs (`Pending ACKs` should decrease)
5. [ ] Look for RegionHandshake (`REGION_HANDSHAKE` in handlers)
6. [ ] Verify object updates (`OBJECT_UPDATE` messages received)