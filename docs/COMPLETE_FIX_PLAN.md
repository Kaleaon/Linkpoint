# Complete Plan for Fixing Linkpoint App - Second Life Viewer

> **Created:** January 25, 2026  
> **Updated:** January 25, 2026 (with log analysis from actual device)
> **Purpose:** Comprehensive implementation plan to achieve Lumiya-level functionality  
> **Status:** Ready for Implementation

---

## Executive Summary

This document provides a complete, actionable plan for fixing the Linkpoint Second Life viewer to achieve the functionality that Lumiya had 10 years ago: **world rendering, friends display, working chat, and working controls**.

Based on comprehensive analysis of the codebase AND **actual debug logs from device testing**, the **good news** is that most of the infrastructure is already in place:
- ✅ All 426 protocol message handlers are registered
- ✅ Message handlers are properly wired to managers
- ✅ UI components exist for chat, friends, world view, etc.
- ✅ Build system is working (AGP 8.6.1, Kotlin 2.1.0)
- ✅ **Protocol works on fast networks** (First log shows full login sequence succeeding)

The **critical issues** identified from actual device logs:
- ❌ **SwapChain not created** → No rendering visible (confirmed in debug report)
- ❌ **High latency kills connection** → 5.8 second average latency on LTE causes UseCircuitCode ACK timeout
- ❌ **RegionHandshake not received on slow networks** → World data never loads
- ❌ **Objects in scene: 0** → Even when data is received, not rendered

---

## Log Analysis Results (Critical Findings)

### Successful Connection (network_log_2026-01-24, WiFi/fast network):
```
✅ UseCircuitCode sent → ACKed in 180ms
✅ CompleteAgentMovement sent → RegionHandshake received
✅ Region: Athanasia (parsed correctly)
✅ AgentMovementComplete received  
✅ ObjectUpdate messages flowing (with avatar data)
✅ Friends online notifications working (45 friends online)
⚠️ Texture errors: "GetTexture capability not available" (timing issue)
```

### Failed Connection (linkpoint_log_2026-01-25, 5G Mobile):
```
✅ UDP connected to 44.244.118.250:13006
✅ 17 capabilities loaded (including GetTexture)
✅ Packets sent: 10,434 (mostly AgentUpdate)
❌ Packets received: ONLY 5 (StartPingCheck x3, PacketAck x2)
❌ RegionHandshake: NEVER RECEIVED
❌ AgentMovementComplete: NEVER RECEIVED  
❌ Objects in Scene: 0
❌ Avatars in Scene: 0
❌ SwapChain: ✗ (No rendering)
❌ App reports "5812ms latency" but user is on 5G (should be ~20-50ms)
```

### Root Cause Analysis (REVISED)

**The "5812ms latency" is NOT actual network latency** - user confirmed they're on 5G which should be fast. The app is measuring response time, but **the server is not responding**.

Looking at the packet statistics:
- **Sent**: 10,434 packets
- **Received**: Only 5 packets (StartPingCheck, PacketAck)
- **Expected**: Should receive RegionHandshake, AgentMovementComplete, ObjectUpdate, etc.

**Possible causes**:

1. **UseCircuitCode never acknowledged** - Server didn't receive or accept the circuit
2. **NAT/Firewall issue** - Mobile carrier NAT blocking incoming UDP
3. **Wrong session credentials** - Circuit code or session ID invalid
4. **Server-side issue** - Different sim host than WiFi connection

**Key difference between logs**:
- WiFi (Jan 24): Connected to `44.244.118.250:13006` ✅ Full response
- 5G (Jan 25): Connected to `44.244.118.250:13006` ❌ Almost no response

Same server IP, but completely different behavior. This points to:
- **Mobile carrier UDP issues** (symmetric NAT, port blocking)
- **Session was already expired/invalid** when 5G attempt was made
- **Capabilities worked** (HTTP) but **UDP is blocked/filtered**

---

## Assumptions (REVISED)

1. **Protocol works correctly** - Confirmed by successful WiFi connection logs
2. **Message parsing works** - RegionHandshake parsed "Athanasia" correctly
3. **5G should be fast** - User confirmed 5G, so "5812ms latency" is NOT real network latency
4. **UDP may be blocked/filtered** - Mobile carriers often have strict NAT for UDP
5. **SwapChain issue is separate** - Need to ensure Surface lifecycle is handled

---

## The Plan

### Phase 0: Fix Mobile UDP Connectivity (CRITICAL - Day 1)

**Problem**: On 5G mobile, the server barely responds despite the network being fast. Only 5 packets received vs 10,434 sent. This suggests UDP connectivity issues, NOT latency.

**Evidence from Logs**:
```
# WiFi (working) - Full bidirectional UDP:
18:29:00.264 → UseCircuitCode sent (seq: 0)
18:29:00.445 ← PacketAck received (ACKing seq 0) [181ms later]
18:29:00.455 → CompleteAgentMovement sent ✓
18:29:00.620 ← RegionHandshake received (Athanasia) ✓
... hundreds of packets received ...

# 5G (broken) - One-way UDP (we send, server barely responds):
→ UseCircuitCode sent
← PacketAck received (x2 only)
← StartPingCheck received (x3 only)
→ 10,434 AgentUpdate packets sent
❌ No RegionHandshake, No ObjectUpdate, No world data
```

**Likely Causes**:
1. **Mobile Carrier NAT** - Symmetric NAT blocks incoming UDP after timeout
2. **UDP Port Mapping Timeout** - Carrier drops the UDP "connection" quickly
3. **UseCircuitCode ACK triggers CompleteAgentMovement** but server response gets lost

**Fix 0.1: Add UDP Keep-Alive Packets**

File: `Linkpoint/src/main/java/com/linkpoint/protocol/messages/UDPConnectionFixed.kt`

```kotlin
// Send keep-alive packets more frequently to maintain NAT mapping
// Mobile NAT can timeout in as little as 30 seconds
private const val KEEP_ALIVE_INTERVAL_MS = 5000L  // Every 5 seconds

private fun startKeepAliveLoop() {
    scope.launch {
        while (isActive && isConnected) {
            // Send a minimal packet to keep NAT mapping alive
            sendAgentHeartbeat()
            delay(KEEP_ALIVE_INTERVAL_MS)
        }
    }
}
```

**Fix 0.2: Verify UseCircuitCode Response Handling**

The WiFi log shows PacketAck is received and triggers CompleteAgentMovement. Check if the same happens on mobile:

```kotlin
// In PacketAck handler, add detailed logging:
udpConnection.registerHandler(MessageIds.PACKET_ACK) { _, rawPacket ->
    Log.i(TAG, "╔═══ PACKET_ACK RECEIVED ═══╗")
    // ... parse ACKs ...
    ackedSequences.forEach { seq ->
        Log.i(TAG, "║ ACK for sequence: $seq")
        if (seq == 0) {
            Log.i(TAG, "║ ⭐ UseCircuitCode ACKed - triggering CompleteAgentMovement")
        }
    }
    Log.i(TAG, "╚═══════════════════════════╝")
}
```

**Fix 0.3: Add Fallback for Lost ACKs**

Even if ACK is lost, try sending CompleteAgentMovement after a timeout:

```kotlin
// Start a fallback timer when UseCircuitCode is sent
private fun sendUseCircuitCodeWithFallback() {
    sendUseCircuitCode()
    
    // Fallback: Send CompleteAgentMovement after 5 seconds even without ACK
    scope.launch {
        delay(5000)
        if (!completeAgentMovementSent.get()) {
            Log.w(TAG, "⚠️ No ACK received - sending CompleteAgentMovement anyway")
            sendCompleteAgentMovement()
            completeAgentMovementSent.set(true)
        }
    }
}
```

**Fix 0.4: Try TCP Fallback for Capabilities (Already Working)**

The logs show capabilities loaded successfully (17 caps via HTTPS). This confirms HTTP works fine on mobile - only UDP has issues.

**Validation**:
- Test on 5G with verbose logging enabled
- Check if PacketAck for seq=0 is received
- Check if CompleteAgentMovement is sent
- Check if RegionHandshake arrives

---

### Phase 1: Fix World Rendering (CRITICAL - Week 1)

**Problem**: Objects and avatars show "0" in scene even when ObjectUpdate messages ARE received (confirmed in WiFi log).

**Root Cause Analysis** (from debug report):
1. **SwapChain: ✗** - Not created despite Filament engine being initialized
2. ObjectUpdate handlers update ObjectManager but scene shows 0 objects
3. The Surface lifecycle isn't being handled properly

**Fix 1.1: Ensure SwapChain Creation on Surface Ready**

File: `Linkpoint/src/main/java/com/linkpoint/render/RenderManager.kt`

```kotlin
// Verify this flow is working:
// 1. UiHelper.RendererCallback.onNativeWindowChanged() is called
// 2. SwapChain is created from the surface
// 3. Log confirms "SwapChain created: true"

// Add diagnostic logging to initialize():
fun initialize(surfaceView: SurfaceView): Boolean {
    Log.i(TAG, "╔═══ RenderManager.initialize called ═══╗")
    // ... existing code ...
}

// Add explicit fallback in render frame:
fun renderFrame() {
    val chain = synchronized(swapChainLock) {
        swapChain ?: ensureSwapChain(engine!!)
    }
    if (chain == null) {
        Log.w(TAG, "No SwapChain available - skipping frame")
        return
    }
    // ... render ...
}
```

**Fix 1.2: Wire Object Updates to Scene Graph**

File: `Linkpoint/src/main/java/com/linkpoint/LinkpointApp.kt`

The handlers ARE registered (lines 809-839), but verify the SceneManager connection:

```kotlin
// In processObjectUpdate() function, verify SceneManager is accessible:
fun processObjectUpdate(update: ObjectUpdateData) {
    val sceneManager = renderManager.getSceneManager()
    if (sceneManager == null) {
        Log.e(TAG, "SceneManager not available - cannot add object to scene")
        return
    }
    // Add to scene for rendering
    sceneManager.updateObject(
        objectId = update.fullId,
        localId = update.localId,
        position = update.position,
        rotation = update.rotation,
        scale = update.scale
    )
}
```

**Fix 1.3: Initialize Terrain Rendering**

File: `Linkpoint/src/main/java/com/linkpoint/protocol/terrain/TerrainManager.kt`

Ensure terrain patches from LayerData are being rendered:

```kotlin
fun processLayerData(result: LayerDataResult) {
    result.patches.forEach { patch ->
        // Create terrain mesh from heightmap data
        val mesh = createTerrainMesh(patch)
        // Add to scene
        renderManager.addTerrainPatch(patch.patchX, patch.patchY, mesh)
    }
}
```

**Validation**:
- Build and run app
- Login to Second Life
- Check logcat for "OBJECT_UPDATE received" and "SwapChain created"
- Should see terrain and objects appear in 3D view

---

### Phase 2: Fix Friends List Display (Week 2)

**Problem**: Friends list may not be populating in UI despite protocol working.

**Current Flow**:
1. `OnlineNotification` → `FriendsManager.handleUdpOnlineNotification()`
2. `OfflineNotification` → `FriendsManager.handleUdpOfflineNotification()`
3. FriendsManager updates `_onlineFriends` StateFlow
4. UI should observe this flow

**Fix 2.1: Verify Event Handler Registration**

File: `Linkpoint/src/main/java/com/linkpoint/world/FriendsManager.kt`

```kotlin
// In init block, verify event handlers are registered:
init {
    Log.i(TAG, "╔═══ FriendsManager initializing ═══╗")
    capabilityManager.registerEventHandler("FriendshipOffered", this)
    capabilityManager.registerEventHandler("OnlineNotification", this)
    capabilityManager.registerEventHandler("OfflineNotification", this)
    Log.i(TAG, "╚═══ FriendsManager event handlers registered ═══╝")
}
```

**Fix 2.2: Wire Friends Flow to UI**

File: `Linkpoint/src/main/java/com/linkpoint/ui/friends/FriendsActivity.kt` (or FriendsScreen.kt)

```kotlin
// Ensure the Activity/Screen observes the FriendsManager flow:
lifecycleScope.launch {
    app.friendsManager.onlineFriends.collectLatest { onlineFriends ->
        Log.i(TAG, "Online friends updated: ${onlineFriends.size} friends online")
        updateUI(onlineFriends)
    }
}
```

**Fix 2.3: Fetch Initial Friends List**

The initial friends list comes via FetchInventory capability. Verify:

```kotlin
// In FriendsManager, add method to fetch initial list:
suspend fun fetchFriendsList() {
    val friendsFolder = inventoryManager.getFriendsFolderUUID()
    val friends = capabilityManager.fetchInventory(friendsFolder)
    // Parse friend calling cards...
}
```

**Validation**:
- Login with account that has friends
- Navigate to Friends screen
- Should see online friends with green indicator

---

### Phase 3: Fix Chat System (Week 2)

**Problem**: Chat may not be sending/receiving properly.

**Current Flow**:
1. Receive: `ChatFromSimulator` → `ChatManager.handleChatFromSimulator()`
2. Send: `ChatManager.sendChat()` → `udpConnection.sendPacket(CHAT_FROM_VIEWER)`

**Fix 3.1: Verify Chat Handler**

File: `Linkpoint/src/main/java/com/linkpoint/LinkpointApp.kt` (lines 731-745)

The handler IS registered. Add diagnostic:

```kotlin
udpConnection.registerHandler(MessageIds.CHAT_FROM_SIMULATOR) { _, rawPacket ->
    Log.i(TAG, "╔═══ CHAT_FROM_SIMULATOR received ═══╗")
    // ... existing code ...
}
```

**Fix 3.2: Verify Chat UI Observes Flow**

File: `Linkpoint/src/main/java/com/linkpoint/ui/chat/ChatActivity.kt`

```kotlin
lifecycleScope.launch {
    app.chatManager.chatFlow.collect { message ->
        Log.i(TAG, "Chat message received in UI: ${message.fromName}: ${message.message}")
        addMessageToChat(message)
    }
}
```

**Fix 3.3: Verify Chat Sending**

File: `Linkpoint/src/main/java/com/linkpoint/chat/ChatManager.kt`

```kotlin
fun sendChat(message: String, type: ChatType = ChatType.NORMAL, channel: Int = 0) {
    Log.i(TAG, "Sending chat: '$message' type=$type channel=$channel")
    scope.launch {
        val data = buildChatPacket(message, type, channel)
        udpConnection.sendPacket(MessageIds.CHAT_FROM_VIEWER, data)
        Log.i(TAG, "Chat packet sent")
    }
}
```

**Validation**:
- Login to Second Life
- Go to a populated area
- Type in chat - should see your message locally
- Should see others' chat appear in the chat window

---

### Phase 4: Fix Controls/Movement (Week 3)

**Problem**: Joystick/movement controls may not be sending AgentUpdate packets.

**Current Flow**:
1. `JoystickView.onJoystickMoved()` → `MovementController.setJoystickInput()`
2. `MovementController` generates control flags
3. `AgentUpdate` packets sent at regular intervals

**Fix 4.1: Verify AgentUpdate Loop**

File: `Linkpoint/src/main/java/com/linkpoint/avatar/MovementController.kt`

```kotlin
// Ensure the AgentUpdate loop is running:
fun startAgentUpdateLoop() {
    agentUpdateJob = scope.launch {
        while (isActive) {
            sendAgentUpdate()
            delay(100) // 10 updates per second
        }
    }
}

private suspend fun sendAgentUpdate() {
    val packet = buildAgentUpdatePacket(
        position = currentPosition,
        velocity = calculateVelocity(),
        rotation = currentRotation,
        controlFlags = buildControlFlags()
    )
    udpConnection.sendPacket(MessageIds.AGENT_UPDATE, packet)
}
```

**Fix 4.2: Verify Control Flag Generation**

```kotlin
private fun buildControlFlags(): Int {
    var flags = 0
    if (joystickY > 0.5f) flags = flags or AGENT_CONTROL_AT_POS      // Forward
    if (joystickY < -0.5f) flags = flags or AGENT_CONTROL_AT_NEG     // Backward
    if (joystickX > 0.5f) flags = flags or AGENT_CONTROL_LEFT_POS    // Strafe right
    if (joystickX < -0.5f) flags = flags or AGENT_CONTROL_LEFT_NEG   // Strafe left
    if (isFlying.value) flags = flags or AGENT_CONTROL_FLY
    if (isRunning.value) flags = flags or AGENT_CONTROL_FAST_AT
    // ... etc
    return flags
}
```

**Fix 4.3: Confirm AgentUpdate is Started After Login**

File: `Linkpoint/src/main/java/com/linkpoint/LinkpointApp.kt`

In the `AGENT_MOVEMENT_COMPLETE` handler (lines 674-728):

```kotlin
// This already calls startAgentUpdates(), verify it works:
sessionManager.setConnectionState(ConnectionState.CONNECTED)
udpConnection.startAgentUpdates()
Log.i(TAG, "✓ AgentUpdate loop started")
```

**Validation**:
- Login to Second Life
- Use joystick controls
- Avatar should move in-world
- Other users should see your avatar moving

---

### Phase 5: Connection Stability (Week 4)

**Problem**: High latency causing ACK timeouts and packet retries.

**Fix 5.1: Increase ACK Timeout**

File: `Linkpoint/src/main/java/com/linkpoint/protocol/messages/UDPConnectionFixed.kt`

```kotlin
// Increase from 3 seconds to 10 seconds for mobile networks:
private const val ACK_TIMEOUT_MS = 10000L
private const val MAX_RETRIES = 5
```

**Fix 5.2: Add Adaptive Timing**

```kotlin
// Track RTT and adjust timeout dynamically:
private var averageRTT = 1000L

fun updateRTT(newRTT: Long) {
    averageRTT = (averageRTT * 7 + newRTT) / 8  // Exponential moving average
}

fun getAdaptiveTimeout(): Long {
    return maxOf(averageRTT * 3, 5000L)  // At least 5 seconds
}
```

**Fix 5.3: Add Auto-Reconnection**

```kotlin
private fun monitorConnection() {
    scope.launch {
        while (isActive) {
            delay(30000) // Check every 30 seconds
            if (System.currentTimeMillis() - lastReceivedPacketTime > 60000) {
                Log.w(TAG, "No packets received in 60s - attempting reconnection")
                reconnect()
            }
        }
    }
}
```

---

## Implementation Priority

| Priority | Component | Effort | Impact |
|----------|-----------|--------|--------|
| 🔴 P0 | SwapChain/Rendering | 2-4 hours | Critical - nothing visible without this |
| 🔴 P0 | Object→Scene wiring | 2-4 hours | Critical - world won't populate |
| 🟡 P1 | Chat verification | 1-2 hours | High - core social feature |
| 🟡 P1 | Friends list wiring | 1-2 hours | High - core social feature |
| 🟢 P2 | Movement controls | 2-4 hours | Medium - basic navigation |
| 🟢 P2 | Connection stability | 4-8 hours | Medium - quality of life |

---

## Testing Strategy

### Manual Testing Checklist

- [ ] **Login succeeds** - No exceptions, session established
- [ ] **Region handshake completes** - Region name shows in UI (not "Unknown")
- [ ] **Terrain loads** - Ground visible, not black void
- [ ] **Objects appear** - Prims, trees, grass visible in world
- [ ] **Avatars appear** - Other users' avatars render
- [ ] **Chat receives** - See others' chat messages
- [ ] **Chat sends** - Your messages appear for others
- [ ] **Friends show online** - Green indicators for online friends
- [ ] **Movement works** - Joystick moves avatar
- [ ] **Camera works** - Can rotate view
- [ ] **Connection stable** - No disconnects in 5 minutes

### Debug Report Analysis

After each test session, use the Debug Floater button to capture a report. Check:

1. **Packet statistics** - Are we receiving ObjectUpdate packets?
2. **Handler counts** - How many objects/avatars processed?
3. **SwapChain status** - Is it created and valid?
4. **Error messages** - Any exceptions in log?

---

## Pitfalls to Avoid

### 1. Don't Ignore ACK Timing
Mobile networks have unpredictable latency. The default 3-second ACK timeout from desktop viewers is too short. Use at least 10 seconds.

### 2. Don't Block the Main Thread
All network operations MUST be on background threads. Blocking main thread causes ANR (Application Not Responding) on Android.

### 3. Don't Assume Packet Order
UDP packets can arrive out of order. The protocol handles this with sequence numbers, but code must be defensive.

### 4. Don't Skip RegionHandshakeReply
The server won't send world data until it receives RegionHandshakeReply. This is already implemented but verify it's working.

### 5. Don't Forget Surface Lifecycle
Android destroys/recreates Surfaces on rotation and app backgrounding. SwapChain must be recreated when surface changes.

### 6. Don't Ignore Zero-Coding
Many SL messages use "zero-coding" compression where repeated zeros are run-length encoded. MessageParser must handle this.

### 7. Don't Assume Capabilities Always Available
Some capabilities may not be available on all grids/regions. Always check for null before using capability URLs.

---

## Success Criteria

The app is "fixed" when:

1. ✅ Can login to Second Life Main Grid and Beta Grid
2. ✅ Terrain renders (ground visible, not black)
3. ✅ Objects render (prims, trees, grass visible)
4. ✅ Avatars render (other users visible)
5. ✅ Chat works bidirectionally
6. ✅ Friends list shows online status
7. ✅ Movement controls work (walk, fly, turn)
8. ✅ Connection stable for 5+ minutes of activity
9. ✅ No crashes during normal use

---

## Next Steps

1. **Run the app with logging enabled** to see exactly where data stops flowing
2. **Start with Phase 1** (rendering) as nothing else is useful without visible world
3. **Use Beta Grid** for testing to avoid affecting real accounts
4. **Document any new issues** found during implementation
5. **Create focused PRs** for each fix to enable rollback if needed

---

## Appendix: Key Files Reference

| File | Purpose |
|------|---------|
| `LinkpointApp.kt` | Application singleton, manager initialization, message handler registration |
| `RenderManager.kt` | Filament rendering, SwapChain management |
| `SceneManager.kt` | Scene graph, object/avatar entities |
| `ObjectManager.kt` | Object tracking and state |
| `AvatarManager.kt` | Avatar tracking and state |
| `ChatManager.kt` | Local chat handling |
| `FriendsManager.kt` | Friends list and online status |
| `UDPConnectionFixed.kt` | UDP protocol, packet sending/receiving |
| `MessageParser.kt` | Packet parsing |
| `WorldViewActivity.kt` | Main 3D world view UI |

---

*This plan was created based on comprehensive analysis of the Linkpoint codebase. Implementation should proceed phase by phase with validation at each step.*
