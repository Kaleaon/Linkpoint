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

## Lumiya Threading Analysis (CRITICAL INSIGHT)

After reviewing Lumiya's decompiled source, the **threading architecture is fundamentally different** and this may be why Lumiya works flawlessly on mobile while Linkpoint struggles:

### Lumiya's Threading Model (Proven Stable):
```java
// SLConnection.java - Single dedicated thread with NIO Selector
public void run() {
    while (!this.selector.keys().isEmpty()) {
        // 1. Process wakeup for ALL circuits FIRST
        for (SelectionKey key : this.selector.keys()) {
            SLCircuit circuit = (SLCircuit) key.attachment();
            circuit.ProcessWakeup();  // Handles resends, ACKs, pings
        }
        
        // 2. Process selected keys (read/write)
        for (SelectionKey key : this.selector.selectedKeys()) {
            if (key.isReadable()) circuit.ProcessReceive();
            if (key.isWritable()) circuit.ProcessTransmit();
            circuit.UpdateSelectorOps();
            circuit.TryProcessIdle();  // Ping checks
        }
        
        // 3. Block with timeout
        this.selector.select(idleInterval);  // 1000ms default
    }
}

// SLCircuit.java - Key constants
MESSAGE_TIMEOUT_MILLIS = 5000   // 5 seconds before message timeout
PING_INTERVAL = 5000           // Ping every 5 seconds
NEED_PING_TIMEOUT = 10000      // Start pinging after 10s no packets
UNANSWERED_PINGS = 3           // Disconnect after 3 unanswered pings
MESSAGE_MAX_RETRIES = 3        // Resend up to 3 times

// SLThreadingCircuit.java - Message processing on dedicated thread
BlockingQueue<Runnable> queue = new LinkedBlockingQueue();
while (workEnabled) {
    Runnable task = queue.poll(1000, TimeUnit.MILLISECONDS);
    if (task != null) task.run();
    else InvokeProcessIdle();  // Process idle handlers when no work
}
```

### Linkpoint's Current Threading Model (Problematic):
```kotlin
// UDPConnectionFixed.kt - Separate coroutines, non-deterministic scheduling
scope.launch { receiveLoop() }      // Coroutine 1
scope.launch { ackSenderLoop() }    // Coroutine 2  
scope.launch { agentUpdateLoop() }  // Coroutine 3

// Receive loop doesn't call ProcessWakeup or handle resends
private suspend fun receiveLoop() {
    while (_isConnected.value) {
        selector.select(SELECTOR_TIMEOUT_MS)  // 1000ms
        // Only processes receives, no wakeup/idle processing
    }
}
```

### Critical Differences:

| Aspect | Lumiya | Linkpoint |
|--------|--------|-----------|
| **Thread model** | Single dedicated thread | Coroutine thread pool |
| **Processing order** | Deterministic: wakeup → receive → transmit → idle | Non-deterministic: coroutines scheduled independently |
| **Resend logic** | Integrated in `ProcessWakeup()` before each select | Separate/missing |
| **Ping checks** | Called after every packet in `TryProcessIdle()` | Not integrated with receive loop |
| **ACK handling** | Synchronous with receive/transmit | Separate coroutine (race conditions possible) |
| **Work queue** | `BlockingQueue` with 1s timeout → idle processing | No equivalent |

### Why This Matters for Mobile:
1. **Mobile networks are unreliable** - packets get lost, NAT times out
2. **Lumiya's tight loop** ensures pings/resends happen EVERY second
3. **Linkpoint's coroutines** may have scheduling delays, missing critical windows
4. **Mobile NAT requires frequent UDP traffic** - Lumiya's 1s loop guarantees this

---

## Comprehensive Lumiya Source Code Analysis

After exhaustive review of ALL Lumiya network-related source files, here are ALL techniques, patterns, and implementation details that can be used:

### 1. Core Network Constants (SLCircuit.java)
```java
// CRITICAL TIMING CONSTANTS - PROVEN TO WORK
private static final int DEFAULT_IDLE_INTERVAL = 1000;      // 1 second idle check
private static final int FAST_IDLE_INTERVAL = 100;          // 100ms when active
private static final int MESSAGE_MAX_RETRIES = 3;           // Retry 3 times
private static final int MESSAGE_TIMEOUT_MILLIS = 5000;     // 5 second timeout
private static final long NEED_PING_TIMEOUT = 10000;        // Ping after 10s no packets
private static final long PING_INTERVAL = 5000;             // Ping every 5 seconds
private static final int TRACK_HANDLED_PACKETS = 1024;      // Track last 1024 packets
private static final int UNANSWERED_PINGS = 3;              // Disconnect after 3 missed pings
```

### 2. HTTP Connection Settings (SLHTTPSConnection.java)
```java
// OkHttp Configuration - Mobile Optimized
private static final long CONNECT_TIMEOUT = 60;  // 60 second connect timeout
private static final long READ_TIMEOUT = 60;     // 60 second read timeout
ConnectionPool(8, 5, TimeUnit.MINUTES)           // 8 connections, 5 min keep-alive
Proxy.NO_PROXY                                   // Direct connection, no proxy
```

### 3. DNS-over-HTTPS Fallback (SLHTTPSConnection.java - CRITICAL!)
```java
// Lumiya has DNS fallback when system DNS fails!
class SLDNS implements Dns {
    // First try system DNS
    List<InetAddress> lookup = systemDns.lookup(hostname);
    
    // If system DNS fails, try DNS-over-HTTPS via Google
    if (lookup fails) {
        Response response = httpClient.newCall(
            "https://dns.google.com/resolve?name=" + hostname + "&type=A"
        ).execute();
        // Parse JSON response and extract IP addresses
    }
    
    // Hardcoded fallback for critical hosts!
    if (hostname == "login.agni.lindenlab.com") {
        return InetAddress.getByName("216.82.57.58");  // Static fallback IP
    }
}
```

### 4. Message Event Listener Pattern (SLMessageEventListener.java)
```java
// Every message can have acknowledgement and timeout callbacks!
public interface SLMessageEventListener {
    void onMessageAcknowledged(SLMessage message);  // Called when ACK received
    void onMessageTimeout(SLMessage message);       // Called after 3 retries fail
}

// Usage in SLAgentCircuit:
UseCircuitCode useCircuitCode = new UseCircuitCode();
useCircuitCode.isReliable = true;
useCircuitCode.setEventListener(new SLMessageEventListener() {
    @Override
    public void onMessageAcknowledged(SLMessage msg) {
        // Only send CompleteAgentMovement AFTER UseCircuitCode is ACKed
        SendCompleteAgentMovement();
    }
    
    @Override
    public void onMessageTimeout(SLMessage msg) {
        gridConn.notifyLoginError("Timed out while connecting to the simulator.");
    }
});
```

### 5. Global Options / Configuration (GlobalOptions.java)
```java
// User-configurable settings that affect networking
private boolean autoReconnect = true;
private int maxReconnectAttempts = 10;      // Try 10 reconnects before giving up
private int maxTextureDownloads = 2;        // Default parallel texture downloads
private boolean keepWifiOn = true;          // Keep WiFi alive during connection

// Device-adaptive settings based on RAM
long totalMemory = getTotalMemory();
if (availableProcessors >= 4 && totalMemory > 524288) {
    maxTextureDownloads = 8;  // More parallel downloads on powerful devices
} else if (availableProcessors >= 2) {
    maxTextureDownloads = 4;
}
```

### 6. SLMessage Packet Structure (SLMessage.java)
```java
// Packet flags
private static final byte LL_ACK_FLAG = 16;        // 0x10 - Has ACK appended
private static final byte LL_RELIABLE_FLAG = 64;   // 0x40 - Needs ACK
private static final byte LL_RESENT_FLAG = 32;     // 0x20 - This is a resend
private static final byte LL_ZERO_CODE_FLAG = -128; // 0x80 - Zero-coded

// Size limits
public static final int MAX_MESSAGE_SIZE = 65536;   // 64KB max message
public static final int MAX_PAYLOAD_SIZE = 1018;    // Leave room for header/ACKs
public static final int MAX_TRANSMIT_SIZE = 1024;   // Actual UDP packet size

// ACK piggybacking - append ACKs to outgoing packets
public int AppendPendingAcks(ByteBuffer buffer, List<Integer> pendingAcks) {
    // Append ACKs to any outgoing packet to reduce separate ACK packets
    while (iterator.hasNext() && buffer.position() <= 1019) {
        buffer.putInt(iterator.next());
        count++;
    }
    if (count != 0) {
        buffer.put(0, (byte) (buffer.get(0) | 16));  // Set ACK flag
        buffer.put((byte) count);
    }
    return count;
}
```

### 6b. EXACT Message IDs (from PackPayload methods)
```java
// HIGH FREQUENCY MESSAGES (single byte after header)
StartPingCheck:     0x01     // Sent by server to check if client alive
CompletePingCheck:  0x02     // Client response to StartPingCheck

// LOW FREQUENCY MESSAGES (0xFFFF prefix + 2-byte ID)
UseCircuitCode:          0xFFFF0003  // First message to establish circuit
CompleteAgentMovement:   0xFFFF00F9  // Request full world state after UseCircuitCode ACK
PacketAck:               0xFFFFFFFB  // Standalone ACK packet (negative = 0xFFFFFFFB)
RegionHandshake:         0xFFFF0094  // Server sends region info (148 decimal)
RegionHandshakeReply:    0xFFFF0095  // Client must reply to receive world data

// Message encoding in PackPayload:
// UseCircuitCode.PackPayload:
byteBuffer.putShort((short) -1);     // 0xFFFF - low frequency marker
byteBuffer.put((byte) 0);            // 0x00
byteBuffer.put((byte) 3);            // 0x03 = message ID 3

// PacketAck.PackPayload:
byteBuffer.putShort((short) -1);     // 0xFFFF
byteBuffer.put((byte) -1);           // 0xFF
byteBuffer.put((byte) -5);           // 0xFB = -5 signed = message ID -5
```

### 7. Password Hash Algorithm (SLAuth.java)
```java
public static String getPasswordHash(String password) {
    String trimmed = password.trim();
    if (trimmed.length() > 16) {
        trimmed = trimmed.substring(0, 16);  // TRUNCATE TO 16 CHARS!
    }
    return "$1$" + HashUtils.MD5_Hash(trimmed);  // MD5 with $1$ prefix
}
```

### 8. Capability URL Repair (SLCaps.java)
```java
// Lumiya repairs broken capability URLs from SL servers
private static String repairCapabilityURL(boolean isMainGrid, String url) {
    if (isMainGrid) {
        String host = new URL(url).getHost();
        // If host is short name like "sim10234", add domain
        if (!host.contains(".") && host.startsWith("sim")) {
            url = url.replace(host, host + ".agni.lindenlab.com");
        }
    }
    return url;
}
```

### 9. Event Queue Long Polling (SLCapEventQueue.java)
```java
// Event queue polling with acknowledgement
while (!threadMustExit) {
    LLSDMap request = new LLSDMap(
        new LLSDMapEntry("ack", lastEventID != 0 ? new LLSDInt(lastEventID) : new LLSDUndefined()),
        new LLSDMapEntry("done", new LLSDBoolean(done))
    );
    
    LLSDNode response = xmlReq.PerformRequest(capURL, request);
    
    lastEventID = response.byKey("id").asInt();  // Track for next ack
    
    // Process events
    for (event in response.byKey("events")) {
        String eventName = event.byKey("message").asString();
        if (eventName == "TeleportFinish") {
            done = true;  // Signal graceful exit
        }
        eventHandler.OnCapsEvent(new CapsEvent(eventName, event.byKey("body")));
    }
    
    // Small delay between polls
    Thread.sleep(2500);
}
```

### 10. Agent Update Timing (SLAvatarControl.java)
```java
// Agent update intervals
private static final int IDLE_AGENT_UPDATE_INTERVAL = 2000;  // 2s when idle
private static final int MIN_AGENT_UPDATE_INTERVAL = 200;    // 200ms minimum

// Send initial fast updates, then slow down
private volatile int needFastUpdates = 10;  // First 10 updates are fast

// Timer-based agent updates
class AgentUpdateTimerTask extends TimerTask {
    @Override
    public void run() {
        if (enableAgentUpdates) {
            SendAgentUpdate(modules.drawDistance);
        }
    }
}
```

### 10b. CRITICAL: RegionHandshake → RegionHandshakeReply (SLAgentCircuit.java)
```java
// Server won't send world data until client replies to RegionHandshake!
public void HandleRegionHandshake(RegionHandshake regionHandshake) {
    if (authReply.isTemporary) return;  // Skip for teleport temp circuits
    
    // IMMEDIATELY send reply - server is waiting!
    RegionHandshakeReply reply = new RegionHandshakeReply();
    reply.AgentData_Field.AgentID = circuitInfo.agentID;
    reply.AgentData_Field.SessionID = circuitInfo.sessionID;
    reply.RegionInfo_Field.Flags = 0;
    SendMessage(reply);  // MUST send this to receive ObjectUpdates!
    
    // Now parse region info
    regionName = SLMessage.stringFromVariableOEM(regionHandshake.RegionInfo_Field.SimName);
    regionID = regionHandshake.RegionInfo2_Field.RegionID;
    isEstateManager = regionHandshake.RegionInfo_Field.IsEstateManager;
    
    // Apply terrain data
    gridConn.parcelInfo.terrainData.ApplyRegionInfo(regionHandshake.RegionInfo_Field);
    
    eventBus.publish(new SLRegionInfoChangedEvent());
}
```

### 11. Reconnection Logic (SLGridConnection.java)
```java
private synchronized boolean Reconnect() {
    if (!userWantsConnected || !hadConnected || 
        !GlobalOptions.getInstance().getAutoReconnect() || 
        reconnectAttempts >= GlobalOptions.getInstance().getMaxReconnectAttempts()) {
        isReconnecting = false;
        return false;
    }
    
    if (connectionState == ConnectionState.Idle && authParams != null) {
        reconnectAttempts++;
        isReconnecting = true;
        eventBus.publish(new SLReconnectingEvent(reconnectAttempts));
        
        // Wait 3 seconds before reconnect attempt
        Thread.sleep(3000);
        DoConnect(authParams, "last");
    }
    return true;
}
```

### 12. TempCircuit for Teleports (SLTempCircuit.java)
```java
// Pre-establish circuit to destination region during teleport
public class SLTempCircuit extends SLCircuit {
    private List<SLMessage> pendingMessages = new LinkedList();
    
    // Buffer messages until circuit is transferred to AgentCircuit
    @Override
    public void DefaultMessageHandler(SLMessage message) {
        pendingMessages.add(message);
    }
}
```

### 13. GridConnectionService - Background Connection (GridConnectionService.java)
```java
// Android Service keeps connection alive in background
public class GridConnectionService extends Service {
    private WifiManager.WifiLock wifiLock;  // Prevent WiFi sleep
    
    // Update notification based on connection state
    void updateOnlineNotification() {
        // Keep foreground service running
        startForeground(NOTIFICATION_ID, notification);
    }
}
```

### 14. IPv4 Preference (SLConnection.java)
```java
// Force IPv4 - SL doesn't support IPv6 well
public SLConnection() {
    System.setProperty("java.net.preferIPv4Stack", "true");
    System.setProperty("java.net.preferIPv6Addresses", "false");
}
```

### 15. Zero-Code Encoding/Decoding (SLMessage.java)
```java
// Zero-coding compresses runs of zeros
private static void ZeroEncode(ByteBuffer in, ByteBuffer out) {
    int zeroCount = 0;
    while (in.hasRemaining()) {
        byte b = in.get();
        if (b != 0) {
            if (zeroCount != 0) {
                out.put((byte) zeroCount);
                zeroCount = 0;
            }
            out.put(b);
        } else {
            if (!wasZero) {
                out.put((byte) 0);  // Zero marker
            }
            zeroCount++;
        }
    }
}

// Native code for performance!
DirectByteBuffer.zeroDecode(...)  // C/C++ implementation
```

### 16. Module System Architecture (SLModules.java)
```java
// All modules initialized together, share AgentCircuit
public SLModules(SLAgentCircuit circuit, SLCaps caps, SLGridConnection conn) {
    modules.add(userNameFetcher = new SLUserNameFetcher(circuit, caps));
    modules.add(avatarControl = new SLAvatarControl(circuit));
    modules.add(inventory = new SLInventory(circuit, caps));
    modules.add(textureFetcher = new SLTextureFetcher(circuit, caps, ...));
    modules.add(voice = new SLVoice(circuit, caps));
    // ... 20+ modules
}

// All modules notified when circuit ready
public void HandleCircuitReady() {
    for (SLModule module : modules) {
        module.HandleCircuitReady();
    }
}
```

### 17. UUID Interning Pool (UUIDPool.java)
```java
// Memory optimization: reuse UUID instances
public class UUIDPool extends InternPool<UUID> {
    private static final UUIDPool instance = new UUIDPool();
    public static final UUID ZeroUUID = new UUID(0, 0);  // Constant for null UUID
    
    // WeakHashMap-based interning - deduplicate UUIDs
    public static UUID getUUID(long mostSig, long leastSig) {
        return instance.intern(new UUID(mostSig, leastSig));
    }
    
    // Parsing with interning
    public static UUID getUUID(String str) {
        if (Strings.isNullOrEmpty(str)) return null;
        return instance.intern(UUID.fromString(str));
    }
}
```

### 18. Texture Fetcher Limits (SLTextureFetcher.java)
```java
// CRITICAL: Only 2 concurrent UDP texture transfers!
private static final int MAX_UDP_TRANSFERS = 2;

// Implements SLIdleHandler for stall detection
public void ProcessIdle() {
    // Check every 1 second for stalled transfers
    if (currentTimeMillis >= lastCheckForStalls + 1000) {
        for (TextureUDPTransfer transfer : udpTransfers) {
            if (transfer.hasStalled()) {
                if (!transfer.RetryTransfer(agentCircuit, circuitInfo)) {
                    // Cancel after retry fails
                    udpTransfers.remove(transfer);
                }
            }
        }
    }
}
```

### 19. Draw Distance Management (SLDrawDistance.java)
```java
// Adaptive draw distance based on activity
public static final float CHAT_RANGE = 20.0f;       // Minimum for chat
public static final float MIN_DRAW_RANGE = 10.5f;   // Absolute minimum
private static final long DRAW_RANGE_TIMEOUT = 10000;  // 10s before reducing

// Draw distance increases for 3D view, object selection, etc.
// Decreases after 10 seconds of inactivity
```

### 20. Native Code Library (DirectByteBuffer.java)
```java
// CRITICAL: Native library for performance!
static {
    System.loadLibrary("rawbuf");  // Native C/C++ library
}

// Native zero-decode - much faster than Java
public static int zeroDecode(byte[] dest, int destStart, int destMaxLen, 
                              byte[] src, int srcStart, int srcLen) {
    return zeroDecodeArray(dest, destStart, destMaxLen, src, srcStart, srcLen);
}

private static native int zeroDecodeArray(...);  // Native implementation

// Native memory allocation/copy for buffers
private native ByteBuffer allocate(int size);
private native void copyPart(ByteBuffer dest, ByteBuffer src, ...);
private native void release(ByteBuffer buf);
```

### 21. EventBus Pattern (EventBus.java)
```java
// Annotation-based event handling with weak references
public synchronized void publish(Object event) {
    for (HandlerInfo handler : handlers) {
        if (handler.matchesEvent(event)) {
            Object subscriber = handler.getSubscriber();  // WeakReference
            if (subscriber == null) {
                // Auto-cleanup dead references
                toRemove.add(handler);
            } else {
                // Post to UI thread if Activity provided
                new EventInvocation(event, activity, subscriber, method, handler)
                    .runOnUIThread();
            }
        }
    }
}

// Usage with @EventHandler annotation
@EventHandler
public void onLoginResult(SLLoginResultEvent event) {
    // Handle login result
}
```

### 22. LLSD Binary Format Support (LLSDContentTypeDetector.java)
```java
// Support both XML and Binary LLSD formats
// Request header: "Accept: application/llsd+binary;q=0.5,application/llsd+xml;q=0.1"
// Binary is preferred (q=0.5) over XML (q=0.1) for efficiency
```

### 23. OkHttp Client Configuration (SLHTTPSConnection.java)
```java
// Production-ready HTTP client settings
private static final OkHttpClient okHttpClient = new OkHttpClient.Builder()
    .proxy(Proxy.NO_PROXY)                    // Direct connection
    .dns(new SLDNS())                         // Custom DNS with fallback
    .connectionPool(new ConnectionPool(       // Connection reuse
        8,                                    // Max 8 idle connections
        5, TimeUnit.MINUTES))                 // 5 minute keep-alive
    .connectTimeout(60, TimeUnit.SECONDS)     // 60s connect timeout
    .readTimeout(60, TimeUnit.SECONDS)        // 60s read timeout
    .hostnameVerifier((hostname, session) -> true)  // Accept any hostname
    .sslSocketFactory(getSocketFactory(), trustEverythingManager)  // Trust all certs
    .addNetworkInterceptor(new CharsetStripInterceptor())  // Strip charset from Content-Type
    .build();
```

### 24. Object Flags Constants (SLObjectInfo.java)
```java
// Object permission and state flags
public static final int FLAGS_USE_PHYSICS = 1;
public static final int FLAGS_CREATE_SELECTED = 2;
public static final int FLAGS_OBJECT_MODIFY = 4;
public static final int FLAGS_OBJECT_COPY = 8;
public static final int FLAGS_OBJECT_ANY_OWNER = 16;
public static final int FLAGS_OBJECT_YOU_OWNER = 32;
public static final int FLAGS_SCRIPTED = 64;
public static final int FLAGS_HANDLE_TOUCH = 128;       // Touchable object
public static final int FLAGS_OBJECT_MOVE = 256;
public static final int FLAGS_TAKES_MONEY = 512;        // Pay-enabled
public static final int FLAGS_PHANTOM = 1024;
public static final int FLAGS_INVENTORY_EMPTY = 2048;
public static final int FLAGS_ALLOW_INVENTORY_DROP = 65536;
public static final int FLAGS_OBJECT_TRANSFER = 131072;
public static final int FLAGS_TEMPORARY = 1073741824;   // Temp-on-rez
public static final int FLAGS_ZLIB_COMPRESSED = Integer.MIN_VALUE;  // Compressed update
```

---

## Summary of ALL Lumiya Techniques

| # | Technique | File | Critical? |
|---|-----------|------|-----------|
| 1 | Network Constants (5s timeout, 3 retries, 1s idle) | SLCircuit.java | ✅ |
| 2 | HTTP Timeouts (60s connect/read) | SLHTTPSConnection.java | ✅ |
| 3 | DNS-over-HTTPS Fallback | SLHTTPSConnection.java | ✅ |
| 4 | Message ACK/Timeout Callbacks | SLMessageEventListener.java | ✅ |
| 5 | Device-adaptive Settings | GlobalOptions.java | ⚠️ |
| 6a | Packet Flags & Size Limits | SLMessage.java | ✅ |
| 6b | Exact Message IDs | Various messages | ✅ |
| 7 | Password Hash (truncate 16, MD5) | SLAuth.java | ✅ |
| 8 | Capability URL Repair | SLCaps.java | ⚠️ |
| 9 | Event Queue Long Polling | SLCapEventQueue.java | ✅ |
| 10a | Agent Update Timing | SLAvatarControl.java | ✅ |
| 10b | RegionHandshake→Reply | SLAgentCircuit.java | ✅ |
| 11 | Reconnection (3s delay, 10 max) | SLGridConnection.java | ✅ |
| 12 | TempCircuit for Teleports | SLTempCircuit.java | ⚠️ |
| 13 | WiFi Lock & Foreground Service | GridConnectionService.java | ✅ |
| 14 | IPv4 Preference | SLConnection.java | ✅ |
| 15 | Zero-Code Native Implementation | DirectByteBuffer.java | ⚠️ |
| 16 | Module System | SLModules.java | ⚠️ |
| 17 | UUID Interning Pool | UUIDPool.java | ⚠️ |
| 18 | Max 2 UDP Texture Transfers | SLTextureFetcher.java | ✅ |
| 19 | Draw Distance Management | SLDrawDistance.java | ⚠️ |
| 20 | Native Memory Management | DirectByteBuffer.java | ⚠️ |
| 21 | WeakRef EventBus Pattern | EventBus.java | ⚠️ |
| 22 | LLSD Binary Preference | HTTP headers | ⚠️ |
| 23 | OkHttp Connection Pool | SLHTTPSConnection.java | ✅ |
| 24 | Object Permission Flags | SLObjectInfo.java | ⚠️ |

---

## Assumptions (REVISED)

1. **Protocol works correctly** - Confirmed by successful WiFi connection logs
2. **Message parsing works** - RegionHandshake parsed "Athanasia" correctly
3. **5G should be fast** - User confirmed 5G, so "5812ms latency" is NOT real network latency
4. **UDP may be blocked/filtered** - Mobile carriers often have strict NAT for UDP
5. **SwapChain issue is separate** - Need to ensure Surface lifecycle is handled
6. **Threading model matters** - Lumiya's deterministic threading is key to mobile stability

---

## The Plan

### Phase -1: Refactor to Lumiya-Style Threading (HIGHEST PRIORITY)

**Problem**: Linkpoint uses coroutines which have non-deterministic scheduling. On mobile networks where timing is critical, this causes dropped connections. Lumiya uses a single dedicated thread with deterministic processing order.

**Key Insight**: Modern devices have 10x more RAM than Lumiya's target devices. We can afford a dedicated thread model without worrying about resource constraints.

**Fix -1.1: Create Dedicated Connection Thread**

```kotlin
// New: SLConnectionThread.kt - Lumiya-style single thread
class SLConnectionThread : Runnable {
    private val selector: Selector = Selector.open()
    private val circuits = ConcurrentHashMap<SelectionKey, SLCircuit>()
    private val workQueue = LinkedBlockingQueue<Runnable>()
    
    companion object {
        const val IDLE_INTERVAL_MS = 1000L
        const val PING_INTERVAL_MS = 5000L
        const val MESSAGE_TIMEOUT_MS = 5000L
        const val MAX_RETRIES = 3
    }
    
    override fun run() {
        while (selector.keys().isNotEmpty()) {
            // 1. Process wakeup for ALL circuits FIRST (resends, ACKs)
            processWakeups()
            
            // 2. Process selector events
            val readyKeys = selector.select(IDLE_INTERVAL_MS)
            if (readyKeys > 0) {
                processSelectedKeys()
            }
            
            // 3. Process work queue
            processWorkQueue()
            
            // 4. Process idle (pings)
            processIdle()
        }
    }
    
    private fun processWakeups() {
        for (key in selector.keys()) {
            val circuit = circuits[key] ?: continue
            if (key.isValid) {
                circuit.processResends()  // Resend timed-out reliable packets
            }
        }
    }
    
    private fun processSelectedKeys() {
        val iterator = selector.selectedKeys().iterator()
        while (iterator.hasNext()) {
            val key = iterator.next()
            iterator.remove()
            
            val circuit = circuits[key] ?: continue
            
            if (key.isValid && key.isReadable) {
                circuit.processReceive()
            }
            if (key.isValid && key.isWritable) {
                circuit.processTransmit()
            }
            if (key.isValid) {
                circuit.updateSelectorOps()
            }
        }
    }
    
    private fun processIdle() {
        for ((_, circuit) in circuits) {
            circuit.tryProcessIdle()  // Send pings if needed
        }
    }
}
```

**Fix -1.2: Integrate Resend Logic into Main Loop**

```kotlin
// In SLCircuit.kt
fun processResends() {
    val now = System.currentTimeMillis()
    val iterator = unackedQueue.iterator()
    
    while (iterator.hasNext()) {
        val message = iterator.next()
        if (now >= message.sentTime + MESSAGE_TIMEOUT_MS) {
            iterator.remove()
            message.retries++
            
            if (message.retries > MAX_RETRIES) {
                message.onTimeout()
            } else {
                message.isResent = true
                message.sentTime = now
                outgoingQueue.add(message)
            }
        }
    }
}
```

**Fix -1.3: Add Ping Check Logic Like Lumiya**

```kotlin
fun tryProcessIdle() {
    val now = SystemClock.elapsedRealtime()
    
    // Only ping if we haven't received packets recently
    if (now < lastReceivedTime + NEED_PING_TIMEOUT) return
    if (now < lastPingSent + PING_INTERVAL) return
    
    if (unansweredPings >= MAX_UNANSWERED_PINGS) {
        if (!timedOut) {
            timedOut = true
            processTimeout()
        }
        return
    }
    
    // Send StartPingCheck
    val ping = StartPingCheck().apply {
        pingId = nextPingId++
        oldestUnacked = unackedQueue.peek()?.seqNum ?: lastSeqNum
    }
    sendMessage(ping)
    unansweredPings++
    lastPingSent = now
}
```

**Validation**:
- Test on 5G mobile network
- Monitor packet flow: should see pings every 5 seconds
- Should see resends for reliable packets
- Connection should stay alive with NAT keep-alive traffic

---

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
