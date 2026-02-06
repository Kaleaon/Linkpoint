# Linkpoint - Task Tracking

## Current Priority (January 2026)

### 🔴 Critical - Fix World Loading

- [ ] **RegionHandshake parsing** - Extract and store region name from SimName field
- [ ] **Object scene population** - Wire OBJECT_UPDATE handler to ObjectManager
- [ ] **Avatar scene population** - Wire avatar updates to AvatarManager
- [ ] **Swap chain init** - Create SwapChain when SurfaceView is available

### 🟡 High Priority - Stability

- [ ] **ACK timing** - Improve reliability on high-latency networks (7+ second latency seen)
- [ ] **Error handling** - Add graceful recovery for packet parsing failures
- [ ] **Reconnection** - Auto-reconnect on connection drop

### 🟢 Medium Priority - Features

- [ ] **Texture loading** - Implement actual texture fetch from SL asset system
- [ ] **Mesh loading** - Load mesh assets via GetMesh capability
- [ ] **Inventory** - Fetch inventory tree from server
- [ ] **Chat** - Send/receive local and IM chat

---

## ✅ Recently Completed (PRs #218-227)

- [x] ACK byte order fix (big-endian for header)
- [x] Connection sequence timing (wait for UseCircuitCode ACK)
- [x] Missing message handlers (PING_CHECK, TERSE_UPDATE, etc.)
- [x] UUID byte order standardization
- [x] Build infrastructure (AGP 8.6.1, Kotlin 2.1.0)
- [x] Theme crash fix (MD3 color attributes)
- [x] PacketAck format (count byte)
- [x] RegionHandshake terrain textures
- [x] ChatFromViewer AgentData block

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